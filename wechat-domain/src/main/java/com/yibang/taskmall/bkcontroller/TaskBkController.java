package com.yibang.taskmall.bkcontroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.TaskQueryRequest;
import com.yibang.taskmall.bkdto.request.TaskCreateRequest;
import com.yibang.taskmall.bkdto.request.TaskUpdateRequest;
import com.yibang.taskmall.bkdto.response.TaskDetailResponse;
import com.yibang.taskmall.bkdto.response.TaskListResponse;
import com.yibang.taskmall.bkdto.response.TaskStatsResponse;
import com.yibang.taskmall.bkservice.TaskBkService;
import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后台任务管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/bk/tasks")
@RequiredArgsConstructor
@Tag(name = "后台任务管理", description = "任务增删改查、统计分析")
public class TaskBkController {

    private final TaskBkService taskBkService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询任务列表
     */
    @GetMapping
    @Operation(summary = "分页查询任务列表", description = "支持多条件筛选和搜索")
    public Result<Page<TaskListResponse>> getTaskList(
            @ModelAttribute TaskQueryRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询任务列表: request={}, page={}, size={}", request, page, size);
        
        try {
            // 检查缓存
            String cacheKey = String.format("bk.tasks:list:%s:%d:%d", 
                request.toString().hashCode(), page, size);
            Page<TaskListResponse> cached = (Page<TaskListResponse>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("从缓存获取任务列表");
                return Result.success(cached);
            }
            
            Page<TaskListResponse> result = taskBkService.getTaskList(request, page, size);
            
            // 缓存结果5分钟
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("查询任务列表失败", e);
            return Result.error("查询任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据任务ID获取详细信息")
    public Result<TaskDetailResponse> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        
        log.info("获取任务详情: taskId={}", taskId);
        
        try {
            // 检查缓存
            String cacheKey = "bk.task:detail:" + taskId;
            TaskDetailResponse cached = (TaskDetailResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            TaskDetailResponse result = taskBkService.getTaskDetail(taskId);
            
            // 缓存结果10分钟
            redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务详情失败: taskId={}", taskId, e);
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建新任务
     */
    @PostMapping
    @Operation(summary = "创建新任务", description = "创建新的任务")
    public Result<Long> createTask(@Validated @RequestBody TaskCreateRequest request) {
        log.info("创建任务: request={}", request);
        
        try {
            // TODO: 权限检查 - 需要管理员权限
            
            Long taskId = taskBkService.createTask(request);
            
            // 清除相关缓存
            clearTaskListCache();
            
            return Result.success(taskId);
            
        } catch (Exception e) {
            log.error("创建任务失败", e);
            return Result.error("创建任务失败: " + e.getMessage());
        }
    }

    /**
     * 更新任务
     */
    @PutMapping("/{taskId}")
    @Operation(summary = "更新任务", description = "更新任务信息")
    public Result<Void> updateTask(
            @Parameter(description = "任务ID") @PathVariable Long taskId,
            @Validated @RequestBody TaskUpdateRequest request) {
        
        log.info("更新任务: taskId={}, request={}", taskId, request);
        
        try {
            // TODO: 权限检查 - 需要管理员权限
            
            taskBkService.updateTask(taskId, request);
            
            // 清除相关缓存
            clearTaskCache(taskId);
            clearTaskListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("更新任务失败: taskId={}", taskId, e);
            return Result.error("更新任务失败: " + e.getMessage());
        }
    }

    /**
     * 删除任务
     */
    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务", description = "删除指定任务")
    public Result<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        log.info("删除任务: taskId={}", taskId);
        
        try {
            // TODO: 权限检查 - 需要管理员权限
            
            taskBkService.deleteTask(taskId);
            
            // 清除相关缓存
            clearTaskCache(taskId);
            clearTaskListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("删除任务失败: taskId={}", taskId, e);
            return Result.error("删除任务失败: " + e.getMessage());
        }
    }

    /**
     * 批量操作任务
     */
    @PostMapping("/batch")
    @Operation(summary = "批量操作任务", description = "批量启用/禁用/删除任务")
    public Result<Void> batchOperation(
            @Parameter(description = "任务ID列表") @RequestParam List<Long> taskIds,
            @Parameter(description = "操作类型") @RequestParam String operation) {
        
        log.info("批量操作任务: taskIds={}, operation={}", taskIds, operation);
        
        try {
            // TODO: 权限检查 - 需要管理员权限
            
            taskBkService.batchOperation(taskIds, operation);
            
            // 清除相关缓存
            taskIds.forEach(this::clearTaskCache);
            clearTaskListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量操作任务失败: taskIds={}, operation={}", taskIds, operation, e);
            return Result.error("批量操作失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取任务统计数据", description = "获取任务相关统计信息")
    public Result<TaskStatsResponse> getTaskStats(
            @Parameter(description = "统计类型") @RequestParam(defaultValue = "overview") String type,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        
        log.info("获取任务统计: type={}, startDate={}, endDate={}", type, startDate, endDate);
        
        try {
            // 检查缓存
            String cacheKey = String.format("bk.tasks:stats:%s:%s:%s", type, startDate, endDate);
            TaskStatsResponse cached = (TaskStatsResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            TaskStatsResponse result = taskBkService.getTaskStats(type, startDate, endDate);
            
            // 缓存结果30分钟
            redisTemplate.opsForValue().set(cacheKey, result, 30, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务统计失败", e);
            return Result.error("获取任务统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务类型选项
     */
    @GetMapping("/types")
    @Operation(summary = "获取任务类型选项", description = "获取所有可用的任务类型")
    public Result<Map<String, String>> getTaskTypes() {
        try {
            // 检查缓存
            String cacheKey = "bk.tasks:types";
            Map<String, String> cached = (Map<String, String>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            Map<String, String> result = taskBkService.getTaskTypes();
            
            // 缓存结果1小时
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务类型失败", e);
            return Result.error("获取任务类型失败: " + e.getMessage());
        }
    }

    /**
     * 清除任务详情缓存
     */
    private void clearTaskCache(Long taskId) {
        String cacheKey = "bk.task:detail:" + taskId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 清除任务列表缓存
     */
    private void clearTaskListCache() {
        // 使用模式匹配删除所有任务列表缓存
        redisTemplate.delete(redisTemplate.keys("bk.tasks:list:*"));
        redisTemplate.delete("bk.tasks:stats:*");
    }
}
