package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.dto.request.SystemConfigRequest;
import com.yibang.taskmall.dto.response.SystemConfigDTO;
import com.yibang.taskmall.entity.SystemConfig;
import com.yibang.taskmall.mapper.SystemConfigMapper;
import com.yibang.taskmall.service.SystemConfigService;
import com.yibang.taskmall.utils.RedisCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统配置服务实现类
 *
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigMapper systemConfigMapper;
    private final RedisCacheUtils redisCacheUtils;

    @Override
    public Map<String, Object> getConfigByGroup(String configGroup) {
        log.info("获取配置组: {}", configGroup);
        
        // 先从Redis缓存获取
        String cacheKey = "system:config:group:" + configGroup;
        String cached = redisCacheUtils.get(cacheKey);
        if (cached != null) {
            // TODO: 反序列化JSON为Map
            return new HashMap<>();
        }
        
        // 从数据库查询 - 根据配置键前缀查询
        List<SystemConfig> configs = systemConfigMapper.selectByKeyPrefix(configGroup);
        
        // 转换为Map格式
        Map<String, Object> result = new HashMap<>();
        for (SystemConfig config : configs) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        
        // 缓存结果
        redisCacheUtils.set(cacheKey, "cached", 30, TimeUnit.MINUTES); // 30分钟
        
        return result;
    }

    @Override
    public String getConfigValue(String configKey) {
        return getConfigValue(configKey, null);
    }

    @Override
    public String getConfigValue(String configKey, String defaultValue) {
        log.info("获取配置值: key={}, defaultValue={}", configKey, defaultValue);
        
        // 先从Redis缓存获取
        String cacheKey = "system:config:key:" + configKey;
        String cached = redisCacheUtils.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 从数据库查询
        SystemConfig config = systemConfigMapper.selectByKey(configKey);
        String value = config != null ? config.getConfigValue() : defaultValue;
        
        // 缓存结果
        if (value != null) {
            redisCacheUtils.set(cacheKey, value, 30, TimeUnit.MINUTES); // 30分钟
        }
        
        return value;
    }

    @Override
    public List<SystemConfigDTO> getConfigList(String configGroup, Boolean isEnabled) {
        log.info("获取配置列表: group={}, enabled={}", configGroup, isEnabled);
        
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(configGroup)) {
            queryWrapper.like(SystemConfig::getConfigKey, configGroup + ".");
        }
        if (isEnabled != null) {
            queryWrapper.eq(SystemConfig::getIsPublic, isEnabled);
        }
        queryWrapper.orderByAsc(SystemConfig::getConfigKey);
        
        List<SystemConfig> configs = systemConfigMapper.selectList(queryWrapper);
        
        return configs.stream().map(config -> {
            SystemConfigDTO dto = new SystemConfigDTO();
            BeanUtils.copyProperties(config, dto);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SystemConfigDTO createConfig(SystemConfigRequest request, Long operatorId) {
        log.info("创建配置: key={}, operatorId={}", request.getConfigKey(), operatorId);
        
        // 检查配置键是否已存在
        LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConfig::getConfigKey, request.getConfigKey());
        SystemConfig existing = systemConfigMapper.selectOne(queryWrapper);
        if (existing != null) {
            throw new RuntimeException("配置键已存在: " + request.getConfigKey());
        }
        
        // 创建配置
        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(request, config);
        config.setCreatedBy(String.valueOf(operatorId));
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedBy(String.valueOf(operatorId));
        config.setUpdatedAt(LocalDateTime.now());
        
        systemConfigMapper.insert(config);
        
        // 清除相关缓存
        clearConfigCache(request.getConfigGroup(), request.getConfigKey());
        
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    public SystemConfigDTO updateConfig(Long id, SystemConfigRequest request, Long operatorId) {
        log.info("更新配置: id={}, operatorId={}", id, operatorId);
        
        SystemConfig config = systemConfigMapper.selectById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在: " + id);
        }
        
        // 更新配置
        BeanUtils.copyProperties(request, config);
        config.setUpdatedBy(String.valueOf(operatorId));
        config.setUpdatedAt(LocalDateTime.now());
        
        systemConfigMapper.updateById(config);
        
        // 清除相关缓存
        clearConfigCache(getGroupByKey(config.getConfigKey()), config.getConfigKey());
        
        SystemConfigDTO dto = new SystemConfigDTO();
        BeanUtils.copyProperties(config, dto);
        return dto;
    }

    @Override
    public void deleteConfig(Long id, Long operatorId) {
        log.info("删除配置: id={}, operatorId={}", id, operatorId);
        
        SystemConfig config = systemConfigMapper.selectById(id);
        if (config == null) {
            throw new RuntimeException("配置不存在: " + id);
        }
        
        // 软删除
        config.setIsPublic(false);
        config.setUpdatedBy(String.valueOf(operatorId));
        config.setUpdatedAt(LocalDateTime.now());
        
        systemConfigMapper.updateById(config);
        
        // 清除相关缓存
        clearConfigCache(getGroupByKey(config.getConfigKey()), config.getConfigKey());
    }

    @Override
    public void batchUpdateConfig(Map<String, String> configMap, Long operatorId) {
        log.info("批量更新配置: 数量={}, operatorId={}", configMap.size(), operatorId);
        
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            String configKey = entry.getKey();
            String configValue = entry.getValue();
            
            LambdaQueryWrapper<SystemConfig> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(SystemConfig::getConfigKey, configKey);
            
            SystemConfig config = systemConfigMapper.selectOne(queryWrapper);
            if (config != null) {
                config.setConfigValue(configValue);
                config.setUpdatedBy(String.valueOf(operatorId));
                config.setUpdatedAt(LocalDateTime.now());
                systemConfigMapper.updateById(config);
                
                    // 清除相关缓存
                    clearConfigCache(getGroupByKey(config.getConfigKey()), config.getConfigKey());
            }
        }
    }

    @Override
    public void refreshCache() {
        log.info("刷新所有配置缓存");
        
        // 清除所有配置相关缓存
        redisCacheUtils.deletePattern("system:config:*");
        
        // 重新加载常用配置
        getConfigByGroup("basic");
        getConfigByGroup("pay");
        getConfigByGroup("notify");
        getConfigByGroup("security");
    }

    @Override
    public void refreshCacheByGroup(String configGroup) {
        log.info("刷新配置组缓存: {}", configGroup);
        
        // 清除指定分组的缓存
        redisCacheUtils.deletePattern("system:config:group:" + configGroup);
        
        // 重新加载配置
        getConfigByGroup(configGroup);
    }

    /**
     * 根据配置键获取分组
     */
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

    /**
     * 清除配置缓存
     */
    private void clearConfigCache(String configGroup, String configKey) {
        if (StringUtils.hasText(configGroup)) {
            redisCacheUtils.delete("system:config:group:" + configGroup);
        }
        if (StringUtils.hasText(configKey)) {
            redisCacheUtils.delete("system:config:key:" + configKey);
        }
    }
}
