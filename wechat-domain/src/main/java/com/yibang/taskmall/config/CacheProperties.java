package com.yibang.taskmall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 缓存配置属性类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Configuration
@EnableAsync
public class CacheProperties {
    
    // 默认缓存时间（分钟）
    public static final int DEFAULT_EXPIRE_TIME = 30;
    
    // 列表查询缓存时间（分钟）
    public static final int LIST_CACHE_TIME = 15;
    
    // 详情查询缓存时间（分钟）
    public static final int DETAIL_CACHE_TIME = 30;
    
    // 统计数据缓存时间（分钟）
    public static final int STATS_CACHE_TIME = 10;
    
    // 配置数据缓存时间（小时）
    public static final int CONFIG_CACHE_TIME = 60;
    
    // 空值缓存时间（分钟）
    public static final int NULL_CACHE_TIME = 5;
    
    // 热点数据缓存时间（小时）
    public static final int HOT_DATA_CACHE_TIME = 120;
    
    // 用户数据缓存时间（分钟）
    public static final int USER_CACHE_TIME = 20;
}
