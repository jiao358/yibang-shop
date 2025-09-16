package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.request.SystemConfigRequest;
import com.yibang.taskmall.dto.response.SystemConfigDTO;
import com.yibang.taskmall.entity.SystemConfig;

import java.util.List;
import java.util.Map;

/**
 * 系统配置服务接口
 *
 * @author yibang
 * @since 2024-01-15
 */
public interface SystemConfigService {

    /**
     * 根据配置组获取配置
     */
    Map<String, Object> getConfigByGroup(String configGroup);

    /**
     * 根据配置键名获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 根据配置键名获取配置值，带默认值
     */
    String getConfigValue(String configKey, String defaultValue);

    /**
     * 获取配置列表
     */
    List<SystemConfigDTO> getConfigList(String configGroup, Boolean isEnabled);

    /**
     * 创建配置
     */
    SystemConfigDTO createConfig(SystemConfigRequest request, Long operatorId);

    /**
     * 更新配置
     */
    SystemConfigDTO updateConfig(Long id, SystemConfigRequest request, Long operatorId);

    /**
     * 删除配置
     */
    void deleteConfig(Long id, Long operatorId);

    /**
     * 批量更新配置
     */
    void batchUpdateConfig(Map<String, String> configMap, Long operatorId);

    /**
     * 刷新缓存
     */
    void refreshCache();

    /**
     * 刷新指定分组的缓存
     */
    void refreshCacheByGroup(String configGroup);
}
