package com.yibang.taskmall.bkservice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.bkdto.request.NotificationRequest;
import com.yibang.taskmall.bkdto.request.SystemConfigRequest;
import com.yibang.taskmall.bkdto.response.NotificationResponse;
import com.yibang.taskmall.bkdto.response.SystemConfigResponse;
import com.yibang.taskmall.bkservice.SystemBkService;
import com.yibang.taskmall.entity.SystemConfig;
import com.yibang.taskmall.mapper.SystemConfigMapper;
import com.yibang.taskmall.service.OperationLogService;
import com.yibang.taskmall.utils.RedisCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SystemBkServiceImpl implements SystemBkService {

    private final SystemConfigMapper systemConfigMapper;
    private final OperationLogService operationLogService;
    private final RedisCacheUtils redisCacheUtils;
    private final ObjectMapper objectMapper;

    private static final String CONFIG_CACHE_PREFIX = "config";
    private static final String NOTIFICATION_CACHE_PREFIX = "notification";

    @Override
    public List<SystemConfigResponse> getSystemConfigs(String group) {
        String cacheKey = RedisCacheUtils.buildKey(CONFIG_CACHE_PREFIX, "list", group);
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, List.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化系统配置缓存失败: {}", e.getMessage());
            }
        }

        // 从数据库查询配置数据
        List<SystemConfig> systemConfigs;
        if (StringUtils.hasText(group)) {
            systemConfigs = systemConfigMapper.selectByKeyPrefix(group);
        } else {
            systemConfigs = systemConfigMapper.selectList(
                new LambdaQueryWrapper<SystemConfig>()
                    .eq(SystemConfig::getIsPublic, true)
                    .orderByAsc(SystemConfig::getConfigKey)
            );
        }
        
        // 转换为响应DTO
        List<SystemConfigResponse> configs = systemConfigs.stream()
            .map(this::convertToSystemConfigResponse)
            .collect(Collectors.toList());
        
        // 如果数据库中没有配置，则初始化默认配置
        if (configs.isEmpty()) {
            configs = initializeDefaultConfigs(group);
        }

        // 缓存结果（1小时）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(configs), 1, java.util.concurrent.TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("序列化系统配置缓存失败: {}", e.getMessage());
        }

        return configs;
    }

    @Override
    public void updateSystemConfig(String key, SystemConfigRequest request) {
        log.info("更新系统配置: key={}, value={}", key, request.getValue());
        
        SystemConfig existingConfig = systemConfigMapper.selectByKey(key);
        String oldValue = null;
        
        if (existingConfig != null) {
            // 更新现有配置
            oldValue = existingConfig.getConfigValue();
            existingConfig.setConfigValue(request.getValue());
            existingConfig.setDescription(request.getDescription());
            existingConfig.setUpdatedAt(LocalDateTime.now());
            systemConfigMapper.updateById(existingConfig);
        } else {
            // 创建新配置
            SystemConfig newConfig = new SystemConfig();
            newConfig.setConfigKey(key);
            newConfig.setConfigValue(request.getValue());
            newConfig.setDescription(request.getDescription());
            newConfig.setConfigType("string");
            newConfig.setIsPublic(true);
            newConfig.setCreatedAt(LocalDateTime.now());
            newConfig.setUpdatedAt(LocalDateTime.now());
            systemConfigMapper.insert(newConfig);
        }
        
        // 记录配置变更日志
        operationLogService.recordConfigChange(key, oldValue, request.getValue(), request.getDescription());
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(CONFIG_CACHE_PREFIX + "*");
    }

    @Override
    public void batchUpdateConfigs(Map<String, SystemConfigRequest> configMap) {
        log.info("批量更新系统配置: {}", configMap.keySet());
        
        for (Map.Entry<String, SystemConfigRequest> entry : configMap.entrySet()) {
            updateSystemConfig(entry.getKey(), entry.getValue());
        }
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(CONFIG_CACHE_PREFIX + "*");
    }

    @Override
    public List<String> getConfigGroups() {
        String cacheKey = RedisCacheUtils.buildKey(CONFIG_CACHE_PREFIX, "groups");
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, List.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化配置分组缓存失败: {}", e.getMessage());
            }
        }

        // 返回默认配置分组（基于配置键前缀）
        List<String> groups = Arrays.asList("app", "withdraw", "notify", "login", "task", "user");
        
        // 缓存结果（2小时）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(groups), 2, java.util.concurrent.TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("序列化配置分组缓存失败: {}", e.getMessage());
        }

        return groups;
    }

    @Override
    public Page<NotificationResponse> getNotifications(String type, Integer page, Integer size) {
        String cacheKey = RedisCacheUtils.buildKey(NOTIFICATION_CACHE_PREFIX, "list", type, 
            String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Page.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化通知列表缓存失败: {}", e.getMessage());
            }
        }

        // Mock通知数据
        Page<NotificationResponse> responsePage = new Page<>(page == null ? 1 : page, size == null ? 10 : size);
        List<NotificationResponse> notifications = new ArrayList<>();
        
        // 添加一些模拟通知
        for (int i = 1; i <= 5; i++) {
            NotificationResponse notification = new NotificationResponse();
            notification.setId((long) i);
            notification.setTitle("系统通知 " + i);
            notification.setContent("这是第 " + i + " 条系统通知内容");
            notification.setType("system");
            notification.setStatus("published");
            notification.setCreatedAt(LocalDateTime.now().minusDays(i));
            notifications.add(notification);
        }
        
        responsePage.setRecords(notifications);
        responsePage.setTotal(notifications.size());

        // 缓存结果（15分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(responsePage), 15, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化通知列表缓存失败: {}", e.getMessage());
        }

        return responsePage;
    }

    @Override
    public Long createNotification(NotificationRequest request) {
        // TODO: 实际创建通知记录
        log.info("创建通知: title={}", request.getTitle());
        
        Long notificationId = System.currentTimeMillis();
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(NOTIFICATION_CACHE_PREFIX + "*");
        
        return notificationId;
    }

    @Override
    public void updateNotification(Long notificationId, NotificationRequest request) {
        // TODO: 实际更新通知记录
        log.info("更新通知: id={}, title={}", notificationId, request.getTitle());
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(NOTIFICATION_CACHE_PREFIX + "*");
    }

    @Override
    public void deleteNotification(Long notificationId) {
        // TODO: 实际删除通知记录
        log.info("删除通知: id={}", notificationId);
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(NOTIFICATION_CACHE_PREFIX + "*");
    }

    @Override
    public Map<String, Object> sendNotification(Long notificationId, List<Long> userIds) {
        // TODO: 实际发送通知
        log.info("发送通知: id={}, userIds={}", notificationId, userIds);
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", userIds.size());
        result.put("failedCount", 0);
        result.put("message", "通知发送成功");
        
        return result;
    }

    @Override
    public Map<String, String> uploadFile(MultipartFile file, String type) {
        // TODO: 实际文件上传逻辑
        log.info("上传文件: name={}, type={}", file.getOriginalFilename(), type);
        
        Map<String, String> result = new HashMap<>();
        result.put("url", "/uploads/" + file.getOriginalFilename());
        result.put("filename", file.getOriginalFilename());
        result.put("size", String.valueOf(file.getSize()));
        
        return result;
    }

    @Override
    public List<Map<String, String>> batchUploadFiles(MultipartFile[] files, String type) {
        // TODO: 实际批量文件上传逻辑
        log.info("批量上传文件: count={}, type={}", files.length, type);
        
        List<Map<String, String>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            Map<String, String> result = uploadFile(file, type);
            results.add(result);
        }
        
        return results;
    }

    private SystemConfigResponse createConfig(String key, String value, String description) {
        SystemConfigResponse config = new SystemConfigResponse();
        config.setKey(key);
        config.setValue(value);
        config.setDescription(description);
        config.setGroup(getGroupByKey(key));
        return config;
    }

    private String getGroupByKey(String key) {
        if (key.startsWith("app_") || key.startsWith("maintenance_")) {
            return "basic";
        } else if (key.contains("withdraw") || key.contains("pay")) {
            return "pay";
        } else if (key.contains("notify") || key.contains("email") || key.contains("sms")) {
            return "notify";
        } else if (key.contains("login") || key.contains("session") || key.contains("password")) {
            return "security";
        }
        return "basic";
    }

    @Override
    public Map<String, Object> getSystemInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("version", "1.0.0");
        info.put("uptime", System.currentTimeMillis());
        info.put("memory", Runtime.getRuntime().totalMemory());
        info.put("processors", Runtime.getRuntime().availableProcessors());
        return info;
    }

    @Override
    public void clearCache(String cacheType) {
        if (StringUtils.isEmpty(cacheType) || "all".equals(cacheType)) {
            redisCacheUtils.deletePattern("*");
        } else {
            redisCacheUtils.deletePattern(cacheType + "*");
        }
    }

    @Override
    public Page<Map<String, Object>> getOperationLogs(String action, String operator, String startTime, String endTime, Integer page, Integer size) {
        return operationLogService.getOperationLogs(action, operator, null, startTime, endTime, page, size);
    }

    private SystemConfigResponse convertToSystemConfigResponse(SystemConfig config) {
        SystemConfigResponse response = new SystemConfigResponse();
        response.setKey(config.getConfigKey());
        response.setValue(config.getConfigValue());
        response.setDescription(config.getDescription());
        response.setGroup(getGroupByKey(config.getConfigKey()));
        response.setType(config.getConfigType());
        return response;
    }

    private List<SystemConfigResponse> initializeDefaultConfigs(String group) {
        List<SystemConfigResponse> configs = new ArrayList<>();
        
        if (StringUtils.isEmpty(group) || "basic".equals(group)) {
            configs.add(createConfig("app_name", "任务商城", "应用名称"));
            configs.add(createConfig("app_version", "1.0.0", "应用版本"));
            configs.add(createConfig("maintenance_mode", "false", "维护模式"));
        }
        
        if (StringUtils.isEmpty(group) || "pay".equals(group)) {
            configs.add(createConfig("min_withdraw_amount", "1000", "最小提现金额(分)"));
            configs.add(createConfig("withdraw_fee_rate", "0.01", "提现手续费率"));
            configs.add(createConfig("daily_withdraw_limit", "50000", "每日提现限额(分)"));
        }
        
        if (StringUtils.isEmpty(group) || "notify".equals(group)) {
            configs.add(createConfig("email_enabled", "true", "邮件通知开关"));
            configs.add(createConfig("sms_enabled", "false", "短信通知开关"));
            configs.add(createConfig("push_enabled", "true", "推送通知开关"));
        }
        
        if (StringUtils.isEmpty(group) || "security".equals(group)) {
            configs.add(createConfig("login_attempt_limit", "5", "登录尝试次数限制"));
            configs.add(createConfig("session_timeout", "7200", "会话超时时间(秒)"));
            configs.add(createConfig("password_min_length", "6", "密码最小长度"));
        }
        
        return configs;
    }
}


