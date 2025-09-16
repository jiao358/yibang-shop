package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.dto.request.SystemConfigRequest;
import com.yibang.taskmall.dto.response.SystemConfigDTO;
import com.yibang.taskmall.entity.SystemConfig;
import com.yibang.taskmall.mapper.SystemConfigMapper;
import com.yibang.taskmall.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "wx.sys:config:";
    private static final String CACHE_KEY_GROUP = "wx.sys:config:group:";
    private static final String CACHE_KEY_SINGLE = "wx.sys:config:key:";
    private static final String LOCK_KEY_PREFIX = "wx.sys:config:lock:";
    
    // 缓存过期时间
    private static final long CACHE_EXPIRE_SECONDS = 3600; // 1小时
    private static final long LOCK_EXPIRE_SECONDS = 30; // 锁30秒

    @Override
    public Map<String, Object> getConfigByGroup(String configGroup) {
        String cacheKey = CACHE_KEY_GROUP + configGroup;
        
        // 先从缓存获取
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存获取配置组: {}", configGroup);
            return cached;
        }

        // 缓存miss，使用分布式锁防止击穿
        String lockKey = LOCK_KEY_PREFIX + configGroup;
        Boolean lockAcquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
        
        if (Boolean.TRUE.equals(lockAcquired)) {
            try {
                // 双重检查缓存
                @SuppressWarnings("unchecked")
                Map<String, Object> doubleCheck = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
                if (doubleCheck != null) {
                    return doubleCheck;
                }

                // 从数据库查询
                List<SystemConfig> configs = systemConfigMapper.selectByConfigGroup(configGroup);
                Map<String, Object> result = new HashMap<>();
                
                for (SystemConfig config : configs) {
                    Object value = parseConfigValue(config.getConfigValue(), config.getConfigType());
                    // 简化键名，去掉配置组前缀
                    String key = config.getConfigKey();
                    if (key.startsWith(configGroup + ".")) {
                        key = key.substring(configGroup.length() + 1);
                    }
                    result.put(key, value);
                }

                // 设置缓存，即使结果为空也缓存以防止频繁查询
                redisTemplate.opsForValue().set(cacheKey, result, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
                log.info("配置组缓存更新: {}, 配置数量: {}", configGroup, result.size());
                
                return result;
                
            } finally {
                // 释放锁
                redisTemplate.delete(lockKey);
            }
        } else {
            // 没有获取到锁，等待一小段时间后重试
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getConfigByGroup(configGroup); // 递归重试
        }
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        String cacheKey = CACHE_KEY_SINGLE + configKey;
        
        // 先从缓存获取
        String cached = (String) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }

        // 缓存miss，查询数据库
        SystemConfig config = systemConfigMapper.selectByConfigKey(configKey);
        String value = config != null ? config.getConfigValue() : defaultValue;
        
        // 设置缓存
        if (value != null) {
            redisTemplate.opsForValue().set(cacheKey, value, CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
        
        return value;
    }

    @Override
    public List<SystemConfigDTO> getConfigList(String configGroup, Boolean isEnabled) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        
        if (configGroup != null) {
            wrapper.eq(SystemConfig::getConfigGroup, configGroup);
        }
        if (isEnabled != null) {
            wrapper.eq(SystemConfig::getIsEnabled, isEnabled);
        }
        wrapper.orderByAsc(SystemConfig::getConfigGroup, SystemConfig::getSortOrder);

        List<SystemConfig> configs = systemConfigMapper.selectList(wrapper);
        return configs.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SystemConfigDTO createConfig(SystemConfigRequest request, Long operatorId) {
        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(request, config);
        config.setCreatedBy(operatorId);
        config.setUpdatedBy(operatorId);

        systemConfigMapper.insert(config);
        
        // 清除相关缓存
        clearRelatedCache(config.getConfigGroup(), config.getConfigKey());
        
        return convertToDTO(config);
    }

    @Override
    @Transactional
    public SystemConfigDTO updateConfig(Long id, SystemConfigRequest request, Long operatorId) {
        SystemConfig existingConfig = systemConfigMapper.selectById(id);
        if (existingConfig == null) {
            throw new RuntimeException("配置不存在");
        }

        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(request, config);
        config.setId(id);
        config.setUpdatedBy(operatorId);

        systemConfigMapper.updateById(config);
        
        // 清除相关缓存
        clearRelatedCache(existingConfig.getConfigGroup(), existingConfig.getConfigKey());
        
        return convertToDTO(systemConfigMapper.selectById(id));
    }

    @Override
    @Transactional
    public void deleteConfig(Long id, Long operatorId) {
        SystemConfig config = systemConfigMapper.selectById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在");
        }

        systemConfigMapper.deleteById(id);
        
        // 清除相关缓存
        clearRelatedCache(config.getConfigGroup(), config.getConfigKey());
    }

    @Override
    @Transactional
    public void batchUpdateConfig(Map<String, String> configMap, Long operatorId) {
        Set<String> affectedGroups = new HashSet<>();
        
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();
            
            LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SystemConfig::getConfigKey, configKey);
            
            SystemConfig config = systemConfigMapper.selectOne(wrapper);
            if (config != null) {
                config.setConfigValue(configValue);
                config.setUpdatedBy(operatorId);
                systemConfigMapper.updateById(config);
                affectedGroups.add(config.getConfigGroup());
            }
        }
        
        // 批量清除缓存
        for (String group : affectedGroups) {
            refreshCacheByGroup(group);
        }
    }

    @Override
    public void refreshCache() {
        // 清除所有配置相关缓存
        Set<String> keys = redisTemplate.keys(CACHE_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("清除所有配置缓存, 数量: {}", keys.size());
        }
    }

    @Override
    public void refreshCacheByGroup(String configGroup) {
        // 清除指定分组的缓存
        String groupCacheKey = CACHE_KEY_GROUP + configGroup;
        redisTemplate.delete(groupCacheKey);
        
        // 清除该分组下所有单个配置的缓存
        Set<String> keys = redisTemplate.keys(CACHE_KEY_SINGLE + configGroup + ".*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        
        log.info("清除配置组缓存: {}", configGroup);
    }

    /**
     * 清除相关缓存
     */
    private void clearRelatedCache(String configGroup, String configKey) {
        // 清除分组缓存
        redisTemplate.delete(CACHE_KEY_GROUP + configGroup);
        // 清除单个配置缓存
        redisTemplate.delete(CACHE_KEY_SINGLE + configKey);
    }

    /**
     * 解析配置值
     */
    private Object parseConfigValue(String configValue, String configType) {
        if (configValue == null) {
            return null;
        }
        
        try {
            switch (configType.toLowerCase()) {
                case "json":
                    return objectMapper.readValue(configValue, new TypeReference<Object>() {});
                case "number":
                    return Long.parseLong(configValue);
                case "boolean":
                    return Boolean.parseBoolean(configValue);
                case "string":
                default:
                    return configValue;
            }
        } catch (Exception e) {
            log.warn("解析配置值失败: {}, 类型: {}, 错误: {}", configValue, configType, e.getMessage());
            return configValue; // 解析失败返回原始字符串
        }
    }

    /**
     * 转换为DTO
     */
    private SystemConfigDTO convertToDTO(SystemConfig config) {
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }
}
