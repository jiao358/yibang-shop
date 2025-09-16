package com.yibang.taskmall.bkcontroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.SystemConfigRequest;
import com.yibang.taskmall.bkdto.request.NotificationRequest;
import com.yibang.taskmall.bkdto.response.SystemConfigResponse;
import com.yibang.taskmall.bkdto.response.NotificationResponse;
import com.yibang.taskmall.bkservice.SystemBkService;
import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后台系统管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/bk/system")
@RequiredArgsConstructor
@Tag(name = "后台系统管理", description = "系统配置、通知管理、文件上传")
public class SystemBkController {

    private final SystemBkService systemBkService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取系统配置列表
     */
    @GetMapping("/configs")
    @Operation(summary = "获取系统配置列表", description = "获取所有系统配置参数")
    public Result<List<SystemConfigResponse>> getSystemConfigs(
            @Parameter(description = "配置分组") @RequestParam(required = false) String group) {
        
        log.info("获取系统配置列表: group={}", group);
        
        try {
            // 检查缓存
            String cacheKey = "bk.system:configs:" + (group != null ? group : "all");
            List<SystemConfigResponse> cached = (List<SystemConfigResponse>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("从缓存获取系统配置列表");
                return Result.success(cached);
            }
            
            List<SystemConfigResponse> result = systemBkService.getSystemConfigs(group);
            
            // 缓存结果30分钟
            redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取系统配置列表失败", e);
            return Result.error("获取系统配置列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新系统配置
     */
    @PutMapping("/configs/{configKey}")
    @Operation(summary = "更新系统配置", description = "更新指定的系统配置参数")
    public Result<Void> updateSystemConfig(
            @Parameter(description = "配置键") @PathVariable String configKey,
            @Validated @RequestBody SystemConfigRequest request) {
        
        log.info("更新系统配置: configKey={}, request={}", configKey, request);
        
        try {
            // TODO: 权限检查 - 需要系统管理权限
            
            systemBkService.updateSystemConfig(configKey, request);
            
            // 清除相关缓存
            clearSystemConfigCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("更新系统配置失败: configKey={}", configKey, e);
            return Result.error("更新系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新系统配置
     */
    @PutMapping("/configs/batch")
    @Operation(summary = "批量更新系统配置", description = "批量更新多个系统配置参数")
    public Result<Void> batchUpdateConfigs(
            @RequestBody Map<String, SystemConfigRequest> configMap) {
        
        log.info("批量更新系统配置: configMap={}", configMap);
        
        try {
            // TODO: 权限检查 - 需要系统管理权限
            
            systemBkService.batchUpdateConfigs(configMap);
            
            // 清除相关缓存
            clearSystemConfigCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量更新系统配置失败", e);
            return Result.error("批量更新系统配置失败: " + e.getMessage());
        }
    }

    /**
     * 获取配置分组列表
     */
    @GetMapping("/config-groups")
    @Operation(summary = "获取配置分组列表", description = "获取所有配置分组")
    public Result<List<String>> getConfigGroups() {
        try {
            // 检查缓存
            String cacheKey = "bk.system:config-groups";
            List<String> cached = (List<String>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            List<String> result = systemBkService.getConfigGroups();
            
            // 缓存结果1小时
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取配置分组列表失败", e);
            return Result.error("获取配置分组列表失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询通知消息
     */
    @GetMapping("/notifications")
    @Operation(summary = "分页查询通知消息", description = "获取系统通知消息列表")
    public Result<Page<NotificationResponse>> getNotifications(
            @Parameter(description = "消息类型") @RequestParam(required = false) String type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询通知消息: type={}, page={}, size={}", type, page, size);
        
        try {
            Page<NotificationResponse> result = systemBkService.getNotifications(type, page, size);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("查询通知消息失败", e);
            return Result.error("查询通知消息失败: " + e.getMessage());
        }
    }

    /**
     * 创建通知消息
     */
    @PostMapping("/notifications")
    @Operation(summary = "创建通知消息", description = "创建新的系统通知消息")
    public Result<Long> createNotification(@Validated @RequestBody NotificationRequest request) {
        log.info("创建通知消息: request={}", request);
        
        try {
            // TODO: 权限检查 - 需要通知管理权限
            
            Long notificationId = systemBkService.createNotification(request);
            
            return Result.success(notificationId);
            
        } catch (Exception e) {
            log.error("创建通知消息失败", e);
            return Result.error("创建通知消息失败: " + e.getMessage());
        }
    }

    /**
     * 更新通知消息
     */
    @PutMapping("/notifications/{notificationId}")
    @Operation(summary = "更新通知消息", description = "更新指定的通知消息")
    public Result<Void> updateNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId,
            @Validated @RequestBody NotificationRequest request) {
        
        log.info("更新通知消息: notificationId={}, request={}", notificationId, request);
        
        try {
            // TODO: 权限检查 - 需要通知管理权限
            
            systemBkService.updateNotification(notificationId, request);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("更新通知消息失败: notificationId={}", notificationId, e);
            return Result.error("更新通知消息失败: " + e.getMessage());
        }
    }

    /**
     * 删除通知消息
     */
    @DeleteMapping("/notifications/{notificationId}")
    @Operation(summary = "删除通知消息", description = "删除指定的通知消息")
    public Result<Void> deleteNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId) {
        
        log.info("删除通知消息: notificationId={}", notificationId);
        
        try {
            // TODO: 权限检查 - 需要通知管理权限
            
            systemBkService.deleteNotification(notificationId);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("删除通知消息失败: notificationId={}", notificationId, e);
            return Result.error("删除通知消息失败: " + e.getMessage());
        }
    }

    /**
     * 发送通知消息
     */
    @PostMapping("/notifications/{notificationId}/send")
    @Operation(summary = "发送通知消息", description = "向指定用户或全体用户发送通知")
    public Result<Map<String, Object>> sendNotification(
            @Parameter(description = "通知ID") @PathVariable Long notificationId,
            @Parameter(description = "目标用户ID列表，为空则发送给所有用户") @RequestParam(required = false) List<Long> userIds) {
        
        log.info("发送通知消息: notificationId={}, userIds={}", notificationId, userIds);
        
        try {
            // TODO: 权限检查 - 需要通知发送权限
            
            Map<String, Object> result = systemBkService.sendNotification(notificationId, userIds);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("发送通知消息失败: notificationId={}", notificationId, e);
            return Result.error("发送通知消息失败: " + e.getMessage());
        }
    }

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @Operation(summary = "文件上传", description = "上传文件到服务器")
    public Result<Map<String, String>> uploadFile(
            @Parameter(description = "上传文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "文件类型") @RequestParam(defaultValue = "image") String type) {
        
        log.info("文件上传: fileName={}, fileSize={}, type={}", 
            file.getOriginalFilename(), file.getSize(), type);
        
        try {
            // TODO: 权限检查 - 需要文件上传权限
            
            Map<String, String> result = systemBkService.uploadFile(file, type);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 批量文件上传
     */
    @PostMapping("/upload/batch")
    @Operation(summary = "批量文件上传", description = "批量上传多个文件")
    public Result<List<Map<String, String>>> batchUploadFiles(
            @Parameter(description = "上传文件列表") @RequestParam("files") MultipartFile[] files,
            @Parameter(description = "文件类型") @RequestParam(defaultValue = "image") String type) {
        
        log.info("批量文件上传: fileCount={}, type={}", files.length, type);
        
        try {
            // TODO: 权限检查 - 需要文件上传权限
            
            List<Map<String, String>> result = systemBkService.batchUploadFiles(files, type);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量文件上传失败", e);
            return Result.error("批量文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 获取操作日志
     */
    @GetMapping("/logs")
    @Operation(summary = "获取操作日志", description = "获取管理员操作日志")
    public Result<Page<Map<String, Object>>> getOperationLogs(
            @Parameter(description = "操作类型") @RequestParam(required = false) String action,
            @Parameter(description = "操作人") @RequestParam(required = false) String operator,
            @Parameter(description = "开始时间") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取操作日志: action={}, operator={}, startTime={}, endTime={}, page={}, size={}", 
            action, operator, startTime, endTime, page, size);
        
        try {
            // TODO: 权限检查 - 需要日志查看权限
            
            Page<Map<String, Object>> result = systemBkService.getOperationLogs(
                action, operator, startTime, endTime, page, size);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取操作日志失败", e);
            return Result.error("获取操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 清除缓存
     */
    @PostMapping("/cache/clear")
    @Operation(summary = "清除缓存", description = "清除指定类型的缓存数据")
    public Result<Void> clearCache(
            @Parameter(description = "缓存类型") @RequestParam String cacheType) {
        
        log.info("清除缓存: cacheType={}", cacheType);
        
        try {
            // TODO: 权限检查 - 需要系统管理权限
            
            systemBkService.clearCache(cacheType);
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("清除缓存失败: cacheType={}", cacheType, e);
            return Result.error("清除缓存失败: " + e.getMessage());
        }
    }

    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取系统信息", description = "获取服务器和应用系统信息")
    public Result<Map<String, Object>> getSystemInfo() {
        try {
            Map<String, Object> result = systemBkService.getSystemInfo();
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取系统信息失败", e);
            return Result.error("获取系统信息失败: " + e.getMessage());
        }
    }

    /**
     * 清除系统配置缓存
     */
    private void clearSystemConfigCache() {
        redisTemplate.delete(redisTemplate.keys("bk.system:configs:*"));
        redisTemplate.delete("bk.system:config-groups");
    }
}
