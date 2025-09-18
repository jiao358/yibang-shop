package com.yibang.taskmall.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheUtils {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 缓存前缀 - 微信小程序系统
     */
    private static final String CACHE_PREFIX = "wx.";

    /**
     * 设置缓存
     */
    public void set(String key, String value, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.opsForValue().set(CACHE_PREFIX + key, value, timeout, unit);
        } catch (Exception e) {
            log.warn("设置缓存失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 设置缓存（默认30分钟）
     */
    public void set(String key, String value) {
        set(key, value, 30, TimeUnit.MINUTES);
    }

    /**
     * 获取缓存
     */
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(CACHE_PREFIX + key);
        } catch (Exception e) {
            log.warn("获取缓存失败: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            stringRedisTemplate.delete(CACHE_PREFIX + key);
        } catch (Exception e) {
            log.warn("删除缓存失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 删除多个缓存
     */
    public void deletePattern(String pattern) {
        try {
            stringRedisTemplate.delete(stringRedisTemplate.keys(CACHE_PREFIX + pattern));
        } catch (Exception e) {
            log.warn("删除缓存模式失败: pattern={}, error={}", pattern, e.getMessage());
        }
    }

    /**
     * 检查缓存是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(stringRedisTemplate.hasKey(CACHE_PREFIX + key));
        } catch (Exception e) {
            log.warn("检查缓存存在性失败: key={}, error={}", key, e.getMessage());
            return false;
        }
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        try {
            stringRedisTemplate.expire(CACHE_PREFIX + key, Duration.ofMillis(unit.toMillis(timeout)));
        } catch (Exception e) {
            log.warn("设置缓存过期时间失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 生成缓存key
     */
    public static String buildKey(String module, String... parts) {
        StringBuilder key = new StringBuilder(module);
        for (String part : parts) {
            if (part != null) {
                key.append(":").append(part);
            }
        }
        return key.toString();
    }

    /**
     * 防缓存穿透的获取方法
     * 当缓存和数据库都没有数据时，缓存空值防止穿透
     */
    public String getWithAntiPenetration(String key, java.util.function.Supplier<String> dataLoader) {
        String value = get(key);
        
        // 如果缓存中有数据（包括空值标记）
        if (value != null) {
            return "NULL_VALUE".equals(value) ? null : value;
        }
        
        // 从数据源加载数据
        String data = dataLoader.get();
        
        if (data != null) {
            // 缓存实际数据
            set(key, data);
            return data;
        } else {
            // 缓存空值标记，防止穿透（短时间缓存）
            set(key, "NULL_VALUE", 5, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 分布式锁获取
     */
    public boolean tryLock(String lockKey, String lockValue, long expireTime, TimeUnit unit) {
        try {
            Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(CACHE_PREFIX + "lock:" + lockKey, lockValue, expireTime, unit);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            log.warn("获取分布式锁失败: key={}, error={}", lockKey, e.getMessage());
            return false;
        }
    }

    /**
     * 分布式锁释放
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = stringRedisTemplate.execute(
                new org.springframework.data.redis.core.script.DefaultRedisScript<>(script, Long.class),
                java.util.Collections.singletonList(CACHE_PREFIX + "lock:" + lockKey),
                lockValue
            );
            return Long.valueOf(1).equals(result);
        } catch (Exception e) {
            log.warn("释放分布式锁失败: key={}, error={}", lockKey, e.getMessage());
            return false;
        }
    }

    /**
     * 缓存预热
     */
    public void warmup(String key, java.util.function.Supplier<String> dataLoader, long timeout, TimeUnit unit) {
        try {
            if (!exists(key)) {
                String data = dataLoader.get();
                if (data != null) {
                    set(key, data, timeout, unit);
                    log.debug("缓存预热完成: key={}", key);
                }
            }
        } catch (Exception e) {
            log.warn("缓存预热失败: key={}, error={}", key, e.getMessage());
        }
    }

    /**
     * 批量删除缓存
     */
    public void batchDelete(java.util.Collection<String> keys) {
        try {
            if (keys != null && !keys.isEmpty()) {
                List<String> fullKeys = keys.stream()
                    .map(key -> CACHE_PREFIX + key)
                    .collect(java.util.stream.Collectors.toList());
                stringRedisTemplate.delete(fullKeys);
            }
        } catch (Exception e) {
            log.warn("批量删除缓存失败: keys={}, error={}", keys, e.getMessage());
        }
    }
}
