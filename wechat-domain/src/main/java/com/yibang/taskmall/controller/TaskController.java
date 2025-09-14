package com.yibang.taskmall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.ClaimTaskRequest;
import com.yibang.taskmall.dto.request.CompleteTaskRequest;
import com.yibang.taskmall.dto.response.TaskResponse;
import com.yibang.taskmall.dto.response.TaskDetailResponse;
import com.yibang.taskmall.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 任务控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "任务管理", description = "任务相关接口")
public class TaskController {

    private final TaskService taskService;

    /**
     * 获取任务列表
     */
    @GetMapping
    @Operation(summary = "获取任务列表", description = "分页获取任务列表，支持按类型和用户等级筛选")
    public Result<Page<TaskResponse>> getTasks(
            @Parameter(description = "任务类型") @RequestParam(required = false) String type,
            @Parameter(description = "用户等级") @RequestParam(required = false) String userLevel,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取任务列表请求: type={}, userLevel={}, page={}, size={}", type, userLevel, page, size);
        
        // TODO: 实现获取任务列表逻辑
        // 1. 根据条件查询任务
        // 2. 分页处理
        // 3. 返回任务列表
        
        return Result.success("获取任务列表成功");
    }

    /**
     * 获取任务详情
     */
    @GetMapping("/{taskId}")
    @Operation(summary = "获取任务详情", description = "根据任务ID获取任务详细信息")
    public Result<TaskDetailResponse> getTaskDetail(
            @Parameter(description = "任务ID") @PathVariable Long taskId) {
        
        log.info("获取任务详情请求: taskId={}", taskId);
        
        // TODO: 实现获取任务详情逻辑
        // 1. 根据任务ID查询任务详情
        // 2. 检查任务状态和用户权限
        // 3. 返回任务详情
        
        return Result.success("获取任务详情成功");
    }

    /**
     * 领取任务
     */
    @PostMapping("/claim")
    @Operation(summary = "领取任务", description = "用户领取指定任务")
    public Result<Void> claimTask(@Validated @RequestBody ClaimTaskRequest request) {
        log.info("领取任务请求: {}", request);
        
        // TODO: 实现领取任务逻辑
        // 1. 验证任务是否可领取
        // 2. 检查用户是否已领取
        // 3. 创建用户任务记录
        // 4. 更新任务领取次数
        
        return Result.success("任务领取成功");
    }

    /**
     * 完成任务
     */
    @PostMapping("/{userTaskId}/complete")
    @Operation(summary = "完成任务", description = "用户提交任务完成凭证")
    public Result<Void> completeTask(
            @Parameter(description = "用户任务ID") @PathVariable Long userTaskId,
            @Validated @RequestBody CompleteTaskRequest request) {
        
        log.info("完成任务请求: userTaskId={}, request={}", userTaskId, request);
        
        // TODO: 实现完成任务逻辑
        // 1. 验证用户任务状态
        // 2. 验证完成凭证
        // 3. 更新任务状态
        // 4. 计算并发放奖励
        
        return Result.success("任务完成成功");
    }

    /**
     * 获取用户任务列表
     */
    @GetMapping("/user-tasks")
    @Operation(summary = "获取用户任务列表", description = "获取当前用户的任务列表")
    public Result<Page<Object>> getUserTasks(
            @Parameter(description = "任务状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取用户任务列表请求: status={}, page={}, size={}", status, page, size);
        
        // TODO: 实现获取用户任务列表逻辑
        // 1. 从JWT Token获取用户ID
        // 2. 根据状态筛选用户任务
        // 3. 分页返回结果
        
        return Result.success("获取用户任务列表成功");
    }

    /**
     * 放弃任务
     */
    @PostMapping("/{userTaskId}/abandon")
    @Operation(summary = "放弃任务", description = "用户放弃已领取的任务")
    public Result<Void> abandonTask(@Parameter(description = "用户任务ID") @PathVariable Long userTaskId) {
        log.info("放弃任务请求: userTaskId={}", userTaskId);
        
        // TODO: 实现放弃任务逻辑
        // 1. 验证用户任务状态
        // 2. 更新任务状态为已放弃
        // 3. 释放任务名额
        
        return Result.success("任务放弃成功");
    }
}
