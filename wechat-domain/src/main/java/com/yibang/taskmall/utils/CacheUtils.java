package com.yibang.taskmall.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis缓存工具类
 * 实现热点数据缓存策略，防止缓存穿透、击穿
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    // 缓存键前缀常量
    public static final String BK_PREFIX = "bk.";
    
    // 任务相关缓存键
    public static final String TASK_LIST_KEY = BK_PREFIX + "tasks:list:";
    public static final String TASK_DETAIL_KEY = BK_PREFIX + "task:detail:";
    public static final String TASK_STATS_KEY = BK_PREFIX + "tasks:stats:";
    public static final String TASK_TYPES_KEY = BK_PREFIX + "tasks:types";
    
    // 用户相关缓存键
    public static final String USER_LIST_KEY = BK_PREFIX + "users:list:";
    public static final String USER_DETAIL_KEY = BK_PREFIX + "user:detail:";
    public static final String USER_STATS_KEY = BK_PREFIX + "users:stats:";
    public static final String USER_LEVELS_KEY = BK_PREFIX + "users:levels";
    
    // 提现相关缓存键
    public static final String WITHDRAW_LIST_KEY = BK_PREFIX + "withdraws:list:";
    public static final String WITHDRAW_DETAIL_KEY = BK_PREFIX + "withdraw:detail:";
    public static final String WITHDRAW_STATS_KEY = BK_PREFIX + "withdraws:stats:";
    public static final String WITHDRAW_STATUSES_KEY = BK_PREFIX + "withdraws:statuses";
    
    // 仪表盘相关缓存键
    public static final String DASHBOARD_OVERVIEW_KEY = BK_PREFIX + "dashboard:overview";
    public static final String DASHBOARD_CHART_KEY = BK_PREFIX + "dashboard:chart:";
    public static final String DASHBOARD_TOP_KEY = BK_PREFIX + "dashboard:top:";
    
    // 系统相关缓存键
    public static final String SYSTEM_CONFIG_KEY = BK_PREFIX + "system:configs:";
    public static final String SYSTEM_GROUPS_KEY = BK_PREFIX + "system:config-groups";

    /**
     * 获取缓存，如果不存在则执行supplier并缓存结果
     * 
     * @param key 缓存键
     * @param supplier 数据提供者
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 缓存值
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrSet(String key, Supplier<T> supplier, long timeout, TimeUnit unit) {
        try {
            // 先尝试从缓存获取
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                log.debug("缓存命中: key={}", key);
                return (T) cached;
            }
            
            log.debug("缓存未命中，执行查询: key={}", key);
            
            // 缓存不存在，执行查询
            T result = supplier.get();
            
            if (result != null) {
                // 添加随机抖动，避免缓存雪崩
                long randomTimeout = timeout + (long) (Math.random() * timeout * 0.1);
                redisTemplate.opsForValue().set(key, result, randomTimeout, unit);
                log.debug("数据已缓存: key={}, timeout={}{}",  key, randomTimeout, unit.name().toLowerCase());
            } else {
                // 防止缓存穿透：空结果也缓存，但时间较短
                redisTemplate.opsForValue().set(key, new Object(), 1, TimeUnit.MINUTES);
                log.debug("空结果防穿透缓存: key={}", key);
            }
            
            return result;
            
        } catch (Exception e) {
            log.error("缓存操作失败: key={}", key, e);
            // 缓存失败时直接执行查询
            return supplier.get();
        }
    }

    /**
     * 设置缓存
     * 
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            if (value != null) {
                // 添加随机抖动
                long randomTimeout = timeout + (long) (Math.random() * timeout * 0.1);
                redisTemplate.opsForValue().set(key, value, randomTimeout, unit);
                log.debug("设置缓存: key={}, timeout={}{}", key, randomTimeout, unit.name().toLowerCase());
            }
        } catch (Exception e) {
            log.error("设置缓存失败: key={}", key, e);
        }
    }

    /**
     * 删除缓存
     * 
     * @param key 缓存键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存: key={}", key);
        } catch (Exception e) {
            log.error("删除缓存失败: key={}", key, e);
        }
    }

    /**
     * 批量删除缓存（支持通配符）
     * 
     * @param pattern 键名模式
     */
    public void deletePattern(String pattern) {
        try {
            Collection<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("批量删除缓存: pattern={}, count={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("批量删除缓存失败: pattern={}", pattern, e);
        }
    }

    /**
     * 检查缓存是否存在
     * 
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("检查缓存存在性失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 构建分页列表缓存键
     * 
     * @param prefix 前缀
     * @param paramsHash 参数哈希
     * @param page 页码
     * @param size 每页大小
     * @return 缓存键
     */
    public static String buildListCacheKey(String prefix, String paramsHash, Integer page, Integer size) {
        return prefix + paramsHash + ":" + page + ":" + size;
    }

    /**
     * 构建详情缓存键
     * 
     * @param prefix 前缀
     * @param id ID
     * @return 缓存键
     */
    public static String buildDetailCacheKey(String prefix, Long id) {
        return prefix + id;
    }

    /**
     * 构建统计缓存键
     * 
     * @param prefix 前缀
     * @param type 统计类型
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 缓存键
     */
    public static String buildStatsCacheKey(String prefix, String type, String startDate, String endDate) {
        return prefix + type + ":" + (startDate != null ? startDate : "") + ":" + (endDate != null ? endDate : "");
    }

    /**
     * 清除模块相关的所有缓存
     * 
     * @param module 模块名 (tasks, users, withdraws, dashboard, system)
     */
    public void clearModuleCache(String module) {
        String pattern = BK_PREFIX + module + ":*";
        deletePattern(pattern);
        log.info("清除模块缓存: module={}", module);
    }

    /**
     * 清除所有后台缓存
     */
    public void clearAllBackendCache() {
        deletePattern(BK_PREFIX + "*");
        log.info("清除所有后台缓存");
    }

    /**
     * 获取缓存统计信息
     * 
     * @return 缓存统计
     */
    public java.util.Map<String, Object> getCacheStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        
        try {
            // 统计各模块缓存键数量
            stats.put("tasks", redisTemplate.keys(BK_PREFIX + "task*").size());
            stats.put("users", redisTemplate.keys(BK_PREFIX + "user*").size());
            stats.put("withdraws", redisTemplate.keys(BK_PREFIX + "withdraw*").size());
            stats.put("dashboard", redisTemplate.keys(BK_PREFIX + "dashboard*").size());
            stats.put("system", redisTemplate.keys(BK_PREFIX + "system*").size());
            stats.put("total", redisTemplate.keys(BK_PREFIX + "*").size());
            
        } catch (Exception e) {
            log.error("获取缓存统计失败", e);
        }
        
        return stats;
    }
}
