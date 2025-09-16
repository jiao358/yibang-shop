package com.yibang.taskmall.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.WithdrawalRequest;
import com.yibang.taskmall.dto.response.WithdrawalResponse;
import com.yibang.taskmall.service.WithdrawalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 提现管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/withdrawals")
@RequiredArgsConstructor
@Tag(name = "提现管理", description = "提现相关接口")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    /**
     * 申请提现
     */
    @PostMapping("/apply")
    @Operation(summary = "申请提现", description = "用户申请提现到微信")
    public Result<Long> applyWithdrawal(@Validated @RequestBody WithdrawalRequest request) {
        log.info("申请提现请求: {}", request);
        
        try {
            // 从JWT获取当前用户ID
            Long currentUserId = getCurrentUserId();
            request.setUserId(currentUserId);
            
            // 申请提现
            Boolean result = withdrawalService.applyWithdrawal(request);
            
            if (result) {
                return Result.success(request.getUserId());
            } else {
                return Result.error("提现申请失败");
            }
            
        } catch (Exception e) {
            log.error("申请提现失败", e);
            return Result.error("申请提现失败: " + e.getMessage());
        }
    }

    /**
     * 获取提现记录
     */
    @GetMapping
    @Operation(summary = "获取提现记录", description = "分页获取用户提现记录")
    public Result<Page<WithdrawalResponse>> getWithdrawals(
            @Parameter(description = "提现状态") @RequestParam(required = false) String status,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("获取提现记录请求: status={}, page={}, size={}", status, page, size);
        
        // TODO: 实现获取提现记录逻辑
        // 1. 从JWT Token获取用户ID
        // 2. 根据状态筛选提现记录
        // 3. 分页返回结果
        
        return Result.success();
    }

    /**
     * 获取提现详情
     */
    @GetMapping("/{withdrawalId}")
    @Operation(summary = "获取提现详情", description = "根据提现ID获取提现详情")
    public Result<WithdrawalResponse> getWithdrawalDetail(
            @Parameter(description = "提现ID") @PathVariable Long withdrawalId) {
        
        log.info("获取提现详情请求: withdrawalId={}", withdrawalId);
        
        // TODO: 实现获取提现详情逻辑
        // 1. 验证用户权限
        // 2. 查询提现详情
        // 3. 返回提现信息
        
        return Result.success();
    }

    /**
     * 取消提现
     */
    @PostMapping("/{withdrawalId}/cancel")
    @Operation(summary = "取消提现", description = "取消待处理的提现申请")
    public Result<Void> cancelWithdrawal(
            @Parameter(description = "提现ID") @PathVariable Long withdrawalId) {
        
        log.info("取消提现请求: withdrawalId={}", withdrawalId);
        
        // TODO: 实现取消提现逻辑
        // 1. 验证提现状态
        // 2. 更新提现状态为已取消
        // 3. 退还提现金额到用户余额
        
        return Result.success();
    }

    /**
     * 获取提现配置
     */
    @GetMapping("/config")
    @Operation(summary = "获取提现配置", description = "获取提现相关配置信息")
    public Result<Object> getWithdrawalConfig() {
        log.info("获取提现配置请求");
        
        try {
            Long currentUserId = getCurrentUserId();
            Object config = withdrawalService.getWithdrawalConfig(currentUserId);
            return Result.success(config);
        } catch (Exception e) {
            log.error("获取提现配置失败", e);
            return Result.error("获取提现配置失败: " + e.getMessage());
        }
    }

    /**
     * 计算提现手续费
     */
    @PostMapping("/calculate-fee")
    @Operation(summary = "计算提现手续费", description = "计算指定金额的提现手续费")
    public Result<Object> calculateWithdrawalFee(
            @Parameter(description = "提现金额（分）") @RequestParam Integer amount) {
        
        log.info("计算提现手续费请求: amount={}", amount);
        
        try {
            Long currentUserId = getCurrentUserId();
            Object feeResult = withdrawalService.calculateWithdrawalFee(currentUserId, amount);
            return Result.success(feeResult);
        } catch (Exception e) {
            log.error("计算提现手续费失败", e);
            return Result.error("计算提现手续费失败: " + e.getMessage());
        }
    }

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
}
