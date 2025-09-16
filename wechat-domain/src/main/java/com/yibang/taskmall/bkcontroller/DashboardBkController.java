package com.yibang.taskmall.bkcontroller;

import com.yibang.taskmall.bkdto.response.DashboardOverviewResponse;
import com.yibang.taskmall.bkdto.response.DashboardChartResponse;
import com.yibang.taskmall.bkservice.DashboardBkService;
import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后台仪表盘控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/bk/dashboard")
@RequiredArgsConstructor
@Tag(name = "后台仪表盘", description = "数据统计和可视化图表")
public class DashboardBkController {

    private final DashboardBkService dashboardBkService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取仪表盘概览数据
     */
    @GetMapping("/overview")
    @Operation(summary = "获取仪表盘概览数据", description = "获取核心业务指标概览")
    public Result<DashboardOverviewResponse> getOverview() {
        log.info("获取仪表盘概览数据");
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:overview";
            DashboardOverviewResponse cached = (DashboardOverviewResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("从缓存获取仪表盘概览数据");
                return Result.success(cached);
            }
            
            DashboardOverviewResponse result = dashboardBkService.getOverview();
            
            // 缓存结果5分钟
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取仪表盘概览数据失败", e);
            return Result.error("获取仪表盘概览数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户增长趋势图
     */
    @GetMapping("/charts/user-growth")
    @Operation(summary = "获取用户增长趋势图", description = "获取用户注册和活跃趋势数据")
    public Result<DashboardChartResponse> getUserGrowthChart(
            @Parameter(description = "时间范围") @RequestParam(defaultValue = "30") Integer days) {
        
        log.info("获取用户增长趋势图: days={}", days);
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:chart:user-growth:" + days;
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getUserGrowthChart(days);
            
            // 缓存结果10分钟
            redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户增长趋势图失败", e);
            return Result.error("获取用户增长趋势图失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务完成趋势图
     */
    @GetMapping("/charts/task-completion")
    @Operation(summary = "获取任务完成趋势图", description = "获取任务完成情况趋势数据")
    public Result<DashboardChartResponse> getTaskCompletionChart(
            @Parameter(description = "时间范围") @RequestParam(defaultValue = "30") Integer days) {
        
        log.info("获取任务完成趋势图: days={}", days);
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:chart:task-completion:" + days;
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getTaskCompletionChart(days);
            
            // 缓存结果10分钟
            redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务完成趋势图失败", e);
            return Result.error("获取任务完成趋势图失败: " + e.getMessage());
        }
    }

    /**
     * 获取收入统计图表
     */
    @GetMapping("/charts/revenue")
    @Operation(summary = "获取收入统计图表", description = "获取平台收入趋势数据")
    public Result<DashboardChartResponse> getRevenueChart(
            @Parameter(description = "时间范围") @RequestParam(defaultValue = "30") Integer days,
            @Parameter(description = "统计类型") @RequestParam(defaultValue = "daily") String type) {
        
        log.info("获取收入统计图表: days={}, type={}", days, type);
        
        try {
            // 检查缓存
            String cacheKey = String.format("bk.dashboard:chart:revenue:%d:%s", days, type);
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getRevenueChart(days, type);
            
            // 缓存结果15分钟
            redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取收入统计图表失败", e);
            return Result.error("获取收入统计图表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户等级分布
     */
    @GetMapping("/charts/user-level-distribution")
    @Operation(summary = "获取用户等级分布", description = "获取用户等级分布饼图数据")
    public Result<DashboardChartResponse> getUserLevelDistribution() {
        log.info("获取用户等级分布");
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:chart:user-level-distribution";
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getUserLevelDistribution();
            
            // 缓存结果30分钟
            redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户等级分布失败", e);
            return Result.error("获取用户等级分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务类型统计
     */
    @GetMapping("/charts/task-type-stats")
    @Operation(summary = "获取任务类型统计", description = "获取各类型任务的统计数据")
    public Result<DashboardChartResponse> getTaskTypeStats() {
        log.info("获取任务类型统计");
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:chart:task-type-stats";
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getTaskTypeStats();
            
            // 缓存结果20分钟
            redisTemplate.opsForValue().set(cacheKey, result, 20, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务类型统计失败", e);
            return Result.error("获取任务类型统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取提现趋势图
     */
    @GetMapping("/charts/withdraw-trend")
    @Operation(summary = "获取提现趋势图", description = "获取提现申请和处理趋势数据")
    public Result<DashboardChartResponse> getWithdrawTrend(
            @Parameter(description = "时间范围") @RequestParam(defaultValue = "30") Integer days) {
        
        log.info("获取提现趋势图: days={}", days);
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:chart:withdraw-trend:" + days;
            DashboardChartResponse cached = (DashboardChartResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            DashboardChartResponse result = dashboardBkService.getWithdrawTrend(days);
            
            // 缓存结果10分钟
            redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取提现趋势图失败", e);
            return Result.error("获取提现趋势图失败: " + e.getMessage());
        }
    }

    /**
     * 获取实时数据
     */
    @GetMapping("/realtime")
    @Operation(summary = "获取实时数据", description = "获取实时关键指标")
    public Result<Map<String, Object>> getRealtimeData() {
        log.info("获取实时数据");
        
        try {
            // 实时数据不使用缓存
            Map<String, Object> result = dashboardBkService.getRealtimeData();
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取实时数据失败", e);
            return Result.error("获取实时数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取热门任务排行
     */
    @GetMapping("/top-tasks")
    @Operation(summary = "获取热门任务排行", description = "获取最受欢迎的任务排行榜")
    public Result<Map<String, Object>> getTopTasks(
            @Parameter(description = "排行数量") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("获取热门任务排行: limit={}", limit);
        
        try {
            // 检查缓存
            String cacheKey = "bk.dashboard:top-tasks:" + limit;
            Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            Map<String, Object> result = dashboardBkService.getTopTasks(limit);
            
            // 缓存结果15分钟
            redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取热门任务排行失败", e);
            return Result.error("获取热门任务排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取活跃用户排行
     */
    @GetMapping("/top-users")
    @Operation(summary = "获取活跃用户排行", description = "获取最活跃用户排行榜")
    public Result<Map<String, Object>> getTopUsers(
            @Parameter(description = "排行数量") @RequestParam(defaultValue = "10") Integer limit,
            @Parameter(description = "排序类型") @RequestParam(defaultValue = "earnings") String sortBy) {
        
        log.info("获取活跃用户排行: limit={}, sortBy={}", limit, sortBy);
        
        try {
            // 检查缓存
            String cacheKey = String.format("bk.dashboard:top-users:%d:%s", limit, sortBy);
            Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            Map<String, Object> result = dashboardBkService.getTopUsers(limit, sortBy);
            
            // 缓存结果15分钟
            redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取活跃用户排行失败", e);
            return Result.error("获取活跃用户排行失败: " + e.getMessage());
        }
    }

    /**
     * 刷新仪表盘缓存
     */
    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新仪表盘缓存", description = "手动刷新仪表盘所有缓存数据")
    public Result<Void> refreshCache() {
        log.info("刷新仪表盘缓存");
        
        try {
            // TODO: 权限检查 - 需要管理员权限
            
            // 清除所有仪表盘缓存
            redisTemplate.delete(redisTemplate.keys("bk.dashboard:*"));
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("刷新仪表盘缓存失败", e);
            return Result.error("刷新仪表盘缓存失败: " + e.getMessage());
        }
    }
}
