package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.SystemConfigRequest;
import com.yibang.taskmall.dto.response.FileUploadDTO;
import com.yibang.taskmall.dto.response.SystemConfigDTO;
import com.yibang.taskmall.service.FileUploadService;
import com.yibang.taskmall.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 系统配置管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
@Tag(name = "系统配置管理", description = "系统配置相关接口")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;
    private final FileUploadService fileUploadService;

    @GetMapping("/customer-service")
    @Operation(summary = "获取客服配置", description = "获取客服二维码和文案配置")
    public Result<Map<String, Object>> getCustomerServiceConfig() {
        log.info("获取客服配置");
        Map<String, Object> config = systemConfigService.getConfigByGroup("customer_service");
        return Result.success(config);
    }

    @GetMapping("/app-settings")
    @Operation(summary = "获取应用设置", description = "获取应用相关配置")
    public Result<Map<String, Object>> getAppSettings() {
        log.info("获取应用设置");
        Map<String, Object> settings = systemConfigService.getConfigByGroup("app_settings");
        return Result.success(settings);
    }

    @GetMapping("/upload-settings")
    @Operation(summary = "获取上传配置", description = "获取文件上传相关配置")
    public Result<Map<String, Object>> getUploadSettings() {
        log.info("获取上传配置");
        Map<String, Object> settings = systemConfigService.getConfigByGroup("upload");
        return Result.success(settings);
    }

    @GetMapping("/list")
    @Operation(summary = "获取配置列表", description = "获取系统配置列表")
    public Result<List<SystemConfigDTO>> getConfigList(
            @RequestParam(required = false) String configGroup,
            @RequestParam(required = false) Boolean isEnabled) {
        log.info("获取配置列表: configGroup={}, isEnabled={}", configGroup, isEnabled);
        List<SystemConfigDTO> configs = systemConfigService.getConfigList(configGroup, isEnabled);
        return Result.success(configs);
    }

    @PostMapping
    @Operation(summary = "创建配置", description = "创建系统配置")
    public Result<SystemConfigDTO> createConfig(@RequestBody SystemConfigRequest request) {
        Long operatorId = getCurrentUserId();
        log.info("创建配置: {}, 操作人: {}", request.getConfigKey(), operatorId);
        SystemConfigDTO config = systemConfigService.createConfig(request, operatorId);
        return Result.success(config);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新配置", description = "更新系统配置")
    public Result<SystemConfigDTO> updateConfig(@PathVariable Long id, @RequestBody SystemConfigRequest request) {
        Long operatorId = getCurrentUserId();
        log.info("更新配置: id={}, 操作人: {}", id, operatorId);
        SystemConfigDTO config = systemConfigService.updateConfig(id, request, operatorId);
        return Result.success(config);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置", description = "删除系统配置")
    public Result<Void> deleteConfig(@PathVariable Long id) {
        Long operatorId = getCurrentUserId();
        log.info("删除配置: id={}, 操作人: {}", id, operatorId);
        systemConfigService.deleteConfig(id, operatorId);
        return Result.success();
    }

    @PostMapping("/batch-update")
    @Operation(summary = "批量更新配置", description = "批量更新系统配置")
    public Result<Void> batchUpdateConfig(@RequestBody Map<String, String> configMap) {
        Long operatorId = getCurrentUserId();
        log.info("批量更新配置: 数量={}, 操作人: {}", configMap.size(), operatorId);
        systemConfigService.batchUpdateConfig(configMap, operatorId);
        return Result.success();
    }

    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新缓存", description = "刷新配置缓存")
    public Result<Void> refreshCache(@RequestParam(required = false) String configGroup) {
        log.info("刷新配置缓存: configGroup={}", configGroup);
        if (configGroup != null) {
            systemConfigService.refreshCacheByGroup(configGroup);
        } else {
            systemConfigService.refreshCache();
        }
        return Result.success();
    }

    @PostMapping("/upload/avatar")
    @Operation(summary = "上传头像", description = "用户头像上传")
    public Result<FileUploadDTO> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = getCurrentUserId();
        String uploadIp = getClientIpAddress(request);
        log.info("用户上传头像: userId={}, fileName={}", userId, file.getOriginalFilename());
        
        FileUploadDTO result = fileUploadService.uploadAvatar(file, userId, uploadIp);
        return Result.success(result);
    }

    @PostMapping("/upload/file")
    @Operation(summary = "上传文件", description = "通用文件上传")
    public Result<FileUploadDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("uploadType") String uploadType,
            @RequestParam(value = "businessId", required = false) Long businessId,
            @RequestParam(value = "businessType", required = false) String businessType,
            HttpServletRequest request) {
        
        Long userId = getCurrentUserId();
        String uploadIp = getClientIpAddress(request);
        log.info("用户上传文件: userId={}, uploadType={}, fileName={}", userId, uploadType, file.getOriginalFilename());
        
        FileUploadDTO result = fileUploadService.uploadFile(file, uploadType, userId, businessId, businessType, uploadIp);
        return Result.success(result);
    }

    @GetMapping("/files")
    @Operation(summary = "获取用户文件列表", description = "获取当前用户的文件列表")
    public Result<List<FileUploadDTO>> getUserFiles(@RequestParam(required = false) String uploadType) {
        Long userId = getCurrentUserId();
        log.info("获取用户文件列表: userId={}, uploadType={}", userId, uploadType);
        
        List<FileUploadDTO> files = fileUploadService.getUserFiles(userId, uploadType);
        return Result.success(files);
    }

    @DeleteMapping("/files/{fileId}")
    @Operation(summary = "删除文件", description = "删除用户文件")
    public Result<Void> deleteFile(@PathVariable Long fileId) {
        Long userId = getCurrentUserId();
        log.info("删除文件: fileId={}, userId={}", fileId, userId);
        
        fileUploadService.deleteFile(fileId, userId);
        return Result.success();
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            String principal = SecurityContextHolder.getContext().getAuthentication().getName();
            return Long.parseLong(principal);
        } catch (Exception e) {
            throw new RuntimeException("用户未登录");
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}
