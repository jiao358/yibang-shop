package com.yibang.taskmall.service;

/**
 * 缓存预热服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface CacheWarmupService {

    /**
     * 预热系统配置缓存
     */
    void warmupSystemConfigs();

    /**
     * 预热用户等级缓存
     */
    void warmupUserLevels();

    /**
     * 预热任务类型缓存
     */
    void warmupTaskTypes();

    /**
     * 预热热门任务列表
     */
    void warmupHotTasks();

    /**
     * 预热用户统计数据
     */
    void warmupUserStats();

    /**
     * 全量缓存预热
     */
    void warmupAll();

    /**
     * 清理所有缓存
     */
    void clearAllCache();
}
