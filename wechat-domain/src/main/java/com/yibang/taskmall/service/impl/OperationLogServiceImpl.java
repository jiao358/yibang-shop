package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.entity.OperationLog;
import com.yibang.taskmall.mapper.OperationLogMapper;
import com.yibang.taskmall.service.OperationLogService;
import com.yibang.taskmall.utils.PageUtils;
import com.yibang.taskmall.utils.RedisCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 操作日志服务实现类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final RedisCacheUtils redisCacheUtils;
    private final ObjectMapper objectMapper;

    private static final String LOG_CACHE_PREFIX = "operation:log";

    @Override
    @Async
    public void recordOperation(String action, String module, String targetType, String targetId, 
                               String targetName, Object requestData, Object responseData, 
                               Long executionTime, String status, String errorMessage) {
        try {
            OperationLog operationLog = new OperationLog();
            
            // 获取当前操作人信息
            try {
                String userId = SecurityContextHolder.getContext().getAuthentication().getName();
                operationLog.setOperatorId(Long.valueOf(userId));
                operationLog.setOperatorName("admin"); // TODO: 从用户服务获取真实姓名
            } catch (Exception e) {
                operationLog.setOperatorId(0L);
                operationLog.setOperatorName("system");
            }
            
            // 获取请求信息
            try {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    operationLog.setIpAddress(getClientIpAddress(request));
                    operationLog.setUserAgent(request.getHeader("User-Agent"));
                }
            } catch (Exception e) {
                // 忽略获取请求信息失败
            }
            
            operationLog.setAction(action);
            operationLog.setModule(module);
            operationLog.setTargetType(targetType);
            operationLog.setTargetId(targetId);
            operationLog.setTargetName(targetName);
            operationLog.setExecutionTime(executionTime);
            operationLog.setStatus(status);
            operationLog.setErrorMessage(errorMessage);
            
            // 序列化请求和响应数据
            try {
                if (requestData != null) {
                    operationLog.setRequestData(objectMapper.writeValueAsString(requestData));
                }
                if (responseData != null) {
                    operationLog.setResponseData(objectMapper.writeValueAsString(responseData));
                }
            } catch (JsonProcessingException e) {
                log.warn("序列化操作日志数据失败: {}", e.getMessage());
            }
            
            operationLog.setCreatedAt(LocalDateTime.now());
            
            operationLogMapper.insert(operationLog);
            
        } catch (Exception e) {
            log.error("记录操作日志失败: action={}, module={}, error={}", action, module, e.getMessage());
        }
    }

    @Override
    public Page<Map<String, Object>> getOperationLogs(String action, String operator, String module,
                                                      String startTime, String endTime, Integer page, Integer size) {
        String cacheKey = RedisCacheUtils.buildKey(LOG_CACHE_PREFIX, "list", action, operator, module, 
            startTime, endTime, String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Page.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化操作日志缓存失败: {}", e.getMessage());
            }
        }

        // 解析时间参数
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        if (StringUtils.hasText(startTime)) {
            startDateTime = LocalDateTime.parse(startTime + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        if (StringUtils.hasText(endTime)) {
            endDateTime = LocalDateTime.parse(endTime + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        // 查询总数
        long total = operationLogMapper.countWithConditions(action, operator, module, startDateTime, endDateTime);
        
        // 分页查询
        int offset = PageUtils.getOffset(page, size);
        int limit = size == null || size < 1 ? PageUtils.DEFAULT_PAGE_SIZE : Math.min(size, PageUtils.MAX_PAGE_SIZE);
        
        List<OperationLog> logs = operationLogMapper.selectPageWithConditions(
            action, operator, module, startDateTime, endDateTime, offset, limit);
        
        // 转换为Map格式
        List<Map<String, Object>> records = logs.stream()
            .map(this::convertToLogMap)
            .collect(Collectors.toList());
        
        Page<Map<String, Object>> responsePage = PageUtils.buildPage(records, total, page, size);
        
        // 缓存结果（5分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(responsePage), 5, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化操作日志缓存失败: {}", e.getMessage());
        }
        
        return responsePage;
    }

    @Override
    public Map<String, Object> getOperationStats(String startTime, String endTime) {
        LocalDateTime startDateTime = LocalDateTime.parse(startTime + " 00:00:00", 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime endDateTime = LocalDateTime.parse(endTime + " 23:59:59", 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> stats = new HashMap<>();
        
        // 操作类型统计
        List<Map<String, Object>> actionStats = operationLogMapper.selectActionStats(startDateTime, endDateTime);
        stats.put("actionStats", actionStats);
        
        // 操作人统计
        List<Map<String, Object>> operatorStats = operationLogMapper.selectOperatorStats(startDateTime, endDateTime);
        stats.put("operatorStats", operatorStats);
        
        // 总操作数
        long totalOperations = operationLogMapper.countWithConditions(null, null, null, startDateTime, endDateTime);
        stats.put("totalOperations", totalOperations);
        
        return stats;
    }

    @Override
    public int cleanExpiredLogs(int days) {
        LocalDateTime expireTime = LocalDateTime.now().minusDays(days);
        return operationLogMapper.deleteExpiredLogs(expireTime);
    }

    @Override
    public void recordBalanceAdjustment(Long userId, String userNickname, Long amount, String type, String reason) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", userId);
        requestData.put("amount", amount);
        requestData.put("type", type);
        requestData.put("reason", reason);
        
        recordOperation("BALANCE_ADJUST", "USER", "user", String.valueOf(userId), 
                       userNickname, requestData, null, null, "success", null);
    }

    @Override
    public void recordLevelChange(Long userId, String userNickname, String oldLevel, String newLevel, String reason) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", userId);
        requestData.put("oldLevel", oldLevel);
        requestData.put("newLevel", newLevel);
        requestData.put("reason", reason);
        
        recordOperation("LEVEL_CHANGE", "USER", "user", String.valueOf(userId), 
                       userNickname, requestData, null, null, "success", null);
    }

    @Override
    public void recordWithdrawApproval(Long withdrawId, Long userId, String action, String remark) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("withdrawId", withdrawId);
        requestData.put("userId", userId);
        requestData.put("action", action);
        requestData.put("remark", remark);
        
        recordOperation("WITHDRAW_" + action.toUpperCase(), "WITHDRAW", "withdrawal", 
                       String.valueOf(withdrawId), "提现申请", requestData, null, null, "success", null);
    }

    @Override
    public void recordConfigChange(String configKey, String oldValue, String newValue, String description) {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("configKey", configKey);
        requestData.put("oldValue", oldValue);
        requestData.put("newValue", newValue);
        requestData.put("description", description);
        
        recordOperation("CONFIG_UPDATE", "SYSTEM", "config", configKey, 
                       description, requestData, null, null, "success", null);
    }

    private Map<String, Object> convertToLogMap(OperationLog log) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", log.getId());
        map.put("operatorId", log.getOperatorId());
        map.put("operatorName", log.getOperatorName());
        map.put("action", log.getAction());
        map.put("module", log.getModule());
        map.put("targetType", log.getTargetType());
        map.put("targetId", log.getTargetId());
        map.put("targetName", log.getTargetName());
        map.put("ipAddress", log.getIpAddress());
        map.put("executionTime", log.getExecutionTime());
        map.put("status", log.getStatus());
        map.put("errorMessage", log.getErrorMessage());
        map.put("createdAt", log.getCreatedAt());
        return map;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        
        for (String header : headers) {
            String ip = request.getHeader(header);
            if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
}
