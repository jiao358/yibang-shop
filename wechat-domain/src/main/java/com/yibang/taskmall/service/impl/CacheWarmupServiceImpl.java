package com.yibang.taskmall.service.impl;

import com.yibang.taskmall.bkservice.SystemBkService;
import com.yibang.taskmall.bkservice.TaskBkService;
import com.yibang.taskmall.bkservice.UserBkService;
import com.yibang.taskmall.service.CacheWarmupService;
import com.yibang.taskmall.utils.RedisCacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 缓存预热服务实现类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmupServiceImpl implements CacheWarmupService, CommandLineRunner {

    private final SystemBkService systemBkService;
    private final TaskBkService taskBkService;
    private final UserBkService userBkService;
    private final RedisCacheUtils redisCacheUtils;

    @Override
    public void run(String... args) throws Exception {
        // 应用启动时自动预热缓存
        log.info("开始缓存预热...");
        warmupAll();
        log.info("缓存预热完成");
    }

    @Override
    @Async
    public void warmupSystemConfigs() {
        try {
            log.info("预热系统配置缓存...");
            
            // 预热所有配置分组
            systemBkService.getConfigGroups();
            
            // 预热各分组配置
            systemBkService.getSystemConfigs("basic");
            systemBkService.getSystemConfigs("pay");
            systemBkService.getSystemConfigs("notify");
            systemBkService.getSystemConfigs("security");
            
            log.info("系统配置缓存预热完成");
        } catch (Exception e) {
            log.error("系统配置缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void warmupUserLevels() {
        try {
            log.info("预热用户等级缓存...");
            userBkService.getUserLevels();
            log.info("用户等级缓存预热完成");
        } catch (Exception e) {
            log.error("用户等级缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void warmupTaskTypes() {
        try {
            log.info("预热任务类型缓存...");
            taskBkService.getTaskTypes();
            log.info("任务类型缓存预热完成");
        } catch (Exception e) {
            log.error("任务类型缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void warmupHotTasks() {
        try {
            log.info("预热热门任务列表缓存...");
            
            // 预热第一页任务列表
            com.yibang.taskmall.bkdto.request.TaskQueryRequest request = new com.yibang.taskmall.bkdto.request.TaskQueryRequest();
            request.setStatus("active");
            taskBkService.getTaskList(request, 1, 20);
            
            log.info("热门任务列表缓存预热完成");
        } catch (Exception e) {
            log.error("热门任务列表缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void warmupUserStats() {
        try {
            log.info("预热用户统计缓存...");
            
            // 预热今日用户统计
            String today = java.time.LocalDate.now().toString();
            userBkService.getUserStats(null, today, today);
            
            log.info("用户统计缓存预热完成");
        } catch (Exception e) {
            log.error("用户统计缓存预热失败: {}", e.getMessage());
        }
    }

    @Override
    public void warmupAll() {
        warmupSystemConfigs();
        warmupUserLevels();
        warmupTaskTypes();
        warmupHotTasks();
        warmupUserStats();
    }

    @Override
    public void clearAllCache() {
        try {
            log.info("清理所有缓存...");
            redisCacheUtils.deletePattern("*");
            log.info("缓存清理完成");
        } catch (Exception e) {
            log.error("缓存清理失败: {}", e.getMessage());
        }
    }
}
