package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.TaskClaimRequest;
import com.yibang.taskmall.dto.request.TaskCompleteRequest;
import com.yibang.taskmall.dto.request.TaskQueryRequest;
import com.yibang.taskmall.dto.response.TaskDTO;
import com.yibang.taskmall.dto.response.UserTaskDTO;
import com.yibang.taskmall.dto.response.UserTaskStatsDTO;
import com.yibang.taskmall.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务控制器
 * 提供任务相关的API接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务相关接口")
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "分页获取任务列表，支持多种筛选条件")
    public Result<Map<String, Object>> getTasks(
            @Parameter(description = "任务类型") @RequestParam(required = false) String taskType,
            @Parameter(description = "佣金等级") @RequestParam(required = false) String rewardLevel,
            @Parameter(description = "状态筛选") @RequestParam(required = false, defaultValue = "all") String statusFilter,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取任务列表请求: taskType={}, rewardLevel={}, statusFilter={}, page={}, size={}", 
            taskType, rewardLevel, statusFilter, page, size);
        
        try {
            // 获取当前用户ID
            Long currentUserId = getCurrentUserId();
            
            // 构建查询请求
            TaskQueryRequest queryRequest = new TaskQueryRequest();
            queryRequest.setUserId(currentUserId);
            queryRequest.setTaskType(taskType);
            queryRequest.setRewardLevel(rewardLevel);
            queryRequest.setStatusFilter(statusFilter);
            queryRequest.setPage(page);
            queryRequest.setSize(size);
            
            // 查询任务列表
            List<TaskDTO> taskList = taskService.getAvailableTasks(queryRequest);
            Long totalCount = taskService.countAvailableTasks(queryRequest);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("list", taskList);
            result.put("total", totalCount);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (totalCount + size - 1) / size);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取任务列表失败", e);
            return Result.error("获取任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据任务ID获取任务详细信息")
    public Result<TaskDTO> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        
        log.info("获取任务详情请求: taskId={}", taskId);
        
        try {
            Long currentUserId = getCurrentUserId();
            TaskDTO taskDetail = taskService.getTaskDetail(taskId, currentUserId);
            return Result.success(taskDetail);
            
        } catch (Exception e) {
            log.error("获取任务详情失败", e);
            return Result.error("获取任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 领取任务
     */
    @PostMapping("/claim")
    @Operation(summary = "领取任务", description = "用户领取指定任务")
    public Result<UserTaskDTO> claimTask(
            @RequestBody TaskClaimRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("领取任务请求: {}", request);
        
        try {
            // 设置用户ID和客户端信息
            request.setUserId(getCurrentUserId());
            request.setClientIp(getClientIpAddress(httpRequest));
            
            UserTaskDTO userTask = taskService.claimTask(request);
            return Result.success(userTask);
            
        } catch (Exception e) {
            log.error("领取任务失败", e);
            return Result.error("领取任务失败: " + e.getMessage());
        }
    }

    /**
     * 完成任务
     */
    @PostMapping("/complete")
    @Operation(summary = "完成任务", description = "用户提交任务完成凭证")
    public Result<UserTaskDTO> completeTask(@RequestBody TaskCompleteRequest request) {
        
        log.info("完成任务请求: {}", request);
        
        try {
            // 设置用户ID
            request.setUserId(getCurrentUserId());
            
            UserTaskDTO userTask = taskService.submitTaskCompletion(request);
            return Result.success(userTask);
            
        } catch (Exception e) {
            log.error("完成任务失败", e);
            return Result.error("完成任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户任务列表
     */
    @GetMapping("/user-tasks")
    @Operation(summary = "获取用户任务列表", description = "获取当前用户的任务列表")
    public Result<Map<String, Object>> getUserTasks(
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取用户任务列表请求: status={}, page={}, size={}", status, page, size);
        
        try {
            Long currentUserId = getCurrentUserId();
            
            List<UserTaskDTO> userTasks = taskService.getUserTasks(currentUserId, status, page, size);
            Long totalCount = taskService.countUserTasks(currentUserId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", userTasks);
            result.put("total", totalCount);
            result.put("page", page);
            result.put("size", size);
            result.put("pages", (totalCount + size - 1) / size);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户任务列表失败", e);
            return Result.error("获取用户任务列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户任务统计
     */
    @GetMapping("/user-stats")
    @Operation(summary = "获取用户任务统计", description = "获取当前用户的任务统计信息")
    public Result<UserTaskStatsDTO> getUserTaskStats() {
        
        log.info("获取用户任务统计请求");
        
        try {
            Long currentUserId = getCurrentUserId();
            UserTaskStatsDTO stats = taskService.getUserTaskStats(currentUserId);
            return Result.success(stats);
            
        } catch (Exception e) {
            log.error("获取用户任务统计失败", e);
            return Result.error("获取用户任务统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取推荐任务
     */
    @GetMapping("/recommended")
    @Operation(summary = "获取推荐任务", description = "根据用户等级和历史行为推荐任务")
    public Result<List<TaskDTO>> getRecommendedTasks(
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") Integer limit) {
        
        log.info("获取推荐任务请求: limit={}", limit);
        
        try {
            Long currentUserId = getCurrentUserId();
            List<TaskDTO> recommendedTasks = taskService.getRecommendedTasks(currentUserId, limit);
            return Result.success(recommendedTasks);
            
        } catch (Exception e) {
            log.error("获取推荐任务失败", e);
            return Result.error("获取推荐任务失败: " + e.getMessage());
        }
    }

    /**
     * 验证任务完成（管理员接口）
     */
    @PostMapping("/{userTaskId}/verify")
    @Operation(summary = "验证任务完成", description = "管理员验证用户提交的任务完成凭证")
    public Result<UserTaskDTO> verifyTaskCompletion(
            @Parameter(description = "用户任务ID") @PathVariable Long userTaskId,
            @Parameter(description = "是否通过验证") @RequestParam Boolean isApproved,
            @Parameter(description = "失败原因") @RequestParam(required = false) String failureReason) {
        
        log.info("验证任务完成请求: userTaskId={}, isApproved={}, failureReason={}", 
            userTaskId, isApproved, failureReason);
        
        try {
            UserTaskDTO userTask = taskService.verifyTaskCompletion(userTaskId, isApproved, failureReason);
            return Result.success(userTask);
            
        } catch (Exception e) {
            log.error("验证任务完成失败", e);
            return Result.error("验证任务完成失败: " + e.getMessage());
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            String userIdStr = SecurityContextHolder.getContext().getAuthentication().getName();
            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            log.error("获取当前用户ID失败", e);
            throw new RuntimeException("用户未登录");
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }
        if (clientIp == null || clientIp.isEmpty() || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        if (clientIp != null && clientIp.contains(",")) {
            clientIp = clientIp.substring(0, clientIp.indexOf(",")).trim();
        }
        return clientIp;
    }
}