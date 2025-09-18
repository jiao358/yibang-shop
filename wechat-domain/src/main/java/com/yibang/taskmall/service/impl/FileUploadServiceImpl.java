package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.config.FileUploadProperties;
import com.yibang.taskmall.dto.response.FileUploadDTO;
import com.yibang.taskmall.entity.FileUpload;
import com.yibang.taskmall.mapper.FileUploadMapper;
import com.yibang.taskmall.service.FileUploadService;
import com.yibang.taskmall.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 文件上传服务实现
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileUploadMapper fileUploadMapper;
    private final FileUploadProperties fileUploadProperties;

    @Override
    public FileUploadDTO uploadAvatar(MultipartFile file, Long userId, String uploadIp) {
        return uploadFile(file, "avatar", userId, userId, "user", uploadIp);
    }

    @Override
    public FileUploadDTO uploadFile(MultipartFile file, String uploadType, Long userId, 
                                  Long businessId, String businessType, String uploadIp) {
        
        // 验证文件
        validateFile(file, uploadType);
        
        try {
            // 生成文件存储路径
            String fileName = generateFileName(file.getOriginalFilename());
            String relativePath = generateFilePath(uploadType, fileName);
            String fullPath = fileUploadProperties.getImageStoragePath(fileName);
            
            // 确保目录存在
            Path directory = Paths.get(fullPath).getParent();
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // 保存文件
            file.transferTo(new File(fullPath));
            
            // 生成访问URL
            String fileUrl = fileUploadProperties.getImageAccessUrl(fileName);
            
            // 保存文件记录
            FileUpload fileUpload = new FileUpload();
            fileUpload.setUserId(userId);
            fileUpload.setFileName(file.getOriginalFilename());
            fileUpload.setFilePath(relativePath);
            fileUpload.setFileUrl(fileUrl);
            fileUpload.setFileSize(file.getSize());
            fileUpload.setFileType(file.getContentType());
            fileUpload.setFileExtension(getFileExtension(file.getOriginalFilename()));
            fileUpload.setUploadType(uploadType);
            fileUpload.setBusinessId(businessId);
            fileUpload.setBusinessType(businessType);
            fileUpload.setUploadIp(uploadIp);
            fileUpload.setStatus("active");
            
            fileUploadMapper.insert(fileUpload);
            
            log.info("文件上传成功: userId={}, fileName={}, uploadType={}", userId, fileName, uploadType);
            
            return convertToDTO(fileUpload);
            
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<FileUploadDTO> getUserFiles(Long userId, String uploadType) {
        LambdaQueryWrapper<FileUpload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileUpload::getUserId, userId)
               .eq(FileUpload::getStatus, "active");
        
        if (StringUtils.hasText(uploadType)) {
            wrapper.eq(FileUpload::getUploadType, uploadType);
        }
        
        wrapper.orderByDesc(FileUpload::getCreatedAt);
        
        List<FileUpload> files = fileUploadMapper.selectList(wrapper);
        return files.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void deleteFile(Long fileId, Long userId) {
        LambdaQueryWrapper<FileUpload> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileUpload::getId, fileId)
               .eq(FileUpload::getUserId, userId);
        
        FileUpload fileUpload = fileUploadMapper.selectOne(wrapper);
        if (fileUpload == null) {
            throw new RuntimeException("文件不存在或无权限删除");
        }
        
        // 软删除：更新状态为deleted
        fileUpload.setStatus("deleted");
        fileUploadMapper.updateById(fileUpload);
        
        // TODO: 异步删除物理文件
        log.info("文件删除成功: fileId={}, userId={}", fileId, userId);
    }

    @Override
    public FileUploadDTO getFileInfo(Long fileId) {
        FileUpload fileUpload = fileUploadMapper.selectById(fileId);
        if (fileUpload == null || "deleted".equals(fileUpload.getStatus())) {
            throw new RuntimeException("文件不存在");
        }
        
        return convertToDTO(fileUpload);
    }

    @Override
    public void cleanExpiredFiles() {
        // TODO: 实现清理过期文件的逻辑
        log.info("开始清理过期文件");
    }

    /**
     * 验证文件
     */
    private void validateFile(MultipartFile file, String uploadType) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }
        
        // 检查文件大小
        long maxSize = getMaxFileSize(uploadType);
        if (file.getSize() > maxSize) {
            throw new RuntimeException("文件大小超过限制: " + (maxSize / 1024 / 1024) + "MB");
        }
        
        // 检查文件类型
        String extension = getFileExtension(file.getOriginalFilename());
        if (!fileUploadProperties.isAllowedImageType(extension)) {
            throw new RuntimeException("不支持的文件类型: " + extension + "，允许的类型: " + fileUploadProperties.getImage().getAllowedTypes());
        }
    }

    /**
     * 获取最大文件大小
     */
    private long getMaxFileSize(String uploadType) {
        if ("avatar".equals(uploadType)) {
            // 头像文件限制更小
            return 2 * 1024 * 1024; // 2MB
        }
        return fileUploadProperties.getImageMaxSizeBytes();
    }


    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 生成文件路径
     */
    private String generateFilePath(String uploadType, String fileName) {
        LocalDate today = LocalDate.now();
        String datePath = today.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return "/" + uploadType + "/" + datePath + "/" + fileName;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    /**
     * 转换为DTO
     */
    private FileUploadDTO convertToDTO(FileUpload fileUpload) {
        FileUploadDTO dto = new FileUploadDTO();
        BeanUtils.copyProperties(fileUpload, dto);
        return dto;
    }
}
