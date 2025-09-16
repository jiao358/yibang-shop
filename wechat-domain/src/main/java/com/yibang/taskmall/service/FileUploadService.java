package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.response.FileUploadDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 *
 * @author yibang
 * @since 2024-01-15
 */
public interface FileUploadService {

    /**
     * 上传头像
     */
    FileUploadDTO uploadAvatar(MultipartFile file, Long userId, String uploadIp);

    /**
     * 上传文件
     */
    FileUploadDTO uploadFile(MultipartFile file, String uploadType, Long userId, 
                           Long businessId, String businessType, String uploadIp);

    /**
     * 获取用户文件列表
     */
    List<FileUploadDTO> getUserFiles(Long userId, String uploadType);

    /**
     * 删除文件
     */
    void deleteFile(Long fileId, Long userId);

    /**
     * 获取文件信息
     */
    FileUploadDTO getFileInfo(Long fileId);

    /**
     * 清理过期文件
     */
    void cleanExpiredFiles();
}
