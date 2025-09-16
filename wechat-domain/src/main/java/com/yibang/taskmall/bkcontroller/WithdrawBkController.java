package com.yibang.taskmall.bkcontroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.WithdrawQueryRequest;
import com.yibang.taskmall.bkdto.request.WithdrawApprovalRequest;
import com.yibang.taskmall.bkdto.request.WithdrawBatchRequest;
import com.yibang.taskmall.bkdto.response.WithdrawDetailResponse;
import com.yibang.taskmall.bkdto.response.WithdrawListResponse;
import com.yibang.taskmall.bkdto.response.WithdrawStatsResponse;
import com.yibang.taskmall.bkservice.WithdrawBkService;
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
 * 后台提现审核控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/bk/withdraws")
@RequiredArgsConstructor
@Tag(name = "后台提现审核", description = "提现申请审核、批量处理、统计分析")
public class WithdrawBkController {

    private final WithdrawBkService withdrawBkService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询提现申请列表
     */
    @GetMapping
    @Operation(summary = "分页查询提现申请列表", description = "支持按状态、用户、时间等条件筛选")
    public Result<Page<WithdrawListResponse>> getWithdrawList(
            @ModelAttribute WithdrawQueryRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询提现申请列表: request={}, page={}, size={}", request, page, size);
        
        try {
            // 对于待审核的提现，不使用缓存确保实时性
            if (!"pending".equals(request.getStatus())) {
                String cacheKey = String.format("bk.withdraws:list:%s:%d:%d", 
                    request.toString().hashCode(), page, size);
                Page<WithdrawListResponse> cached = (Page<WithdrawListResponse>) redisTemplate.opsForValue().get(cacheKey);
                if (cached != null) {
                    log.info("从缓存获取提现申请列表");
                    return Result.success(cached);
                }
            }
            
            Page<WithdrawListResponse> result = withdrawBkService.getWithdrawList(request, page, size);
            
            // 非待审核状态的记录可以缓存3分钟
            if (!"pending".equals(request.getStatus())) {
                String cacheKey = String.format("bk.withdraws:list:%s:%d:%d", 
                    request.toString().hashCode(), page, size);
                redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.MINUTES);
            }
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("查询提现申请列表失败", e);
            return Result.error("查询提现申请列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取提现申请详情
     */
    @GetMapping("/{withdrawId}")
    @Operation(summary = "获取提现申请详情", description = "获取提现申请的详细信息")
    public Result<WithdrawDetailResponse> getWithdrawDetail(
            @Parameter(description = "提现申请ID") @PathVariable Long withdrawId) {
        
        log.info("获取提现申请详情: withdrawId={}", withdrawId);
        
        try {
            WithdrawDetailResponse result = withdrawBkService.getWithdrawDetail(withdrawId);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取提现申请详情失败: withdrawId={}", withdrawId, e);
            return Result.error("获取提现申请详情失败: " + e.getMessage());
        }
    }

    /**
     * 审核提现申请
     */
    @PostMapping("/{withdrawId}/approve")
    @Operation(summary = "审核提现申请", description = "通过或拒绝提现申请")
    public Result<Void> approveWithdraw(
            @Parameter(description = "提现申请ID") @PathVariable Long withdrawId,
            @Validated @RequestBody WithdrawApprovalRequest request) {
        
        log.info("审核提现申请: withdrawId={}, request={}", withdrawId, request);
        
        try {
            // TODO: 权限检查 - 需要财务审核权限
            
            withdrawBkService.approveWithdraw(withdrawId, request);
            
            // 清除相关缓存
            clearWithdrawCache(withdrawId);
            clearWithdrawListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("审核提现申请失败: withdrawId={}", withdrawId, e);
            return Result.error("审核提现申请失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核提现申请
     */
    @PostMapping("/batch-approve")
    @Operation(summary = "批量审核提现申请", description = "批量通过或拒绝多个提现申请")
    public Result<Map<String, Object>> batchApprove(
            @Validated @RequestBody WithdrawBatchRequest request) {
        
        log.info("批量审核提现申请: request={}", request);
        
        try {
            // TODO: 权限检查 - 需要财务审核权限
            
            Map<String, Object> result = withdrawBkService.batchApprove(request);
            
            // 清除相关缓存
            request.getWithdrawIds().forEach(this::clearWithdrawCache);
            clearWithdrawListCache();
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量审核提现申请失败", e);
            return Result.error("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 确认打款完成
     */
    @PostMapping("/{withdrawId}/confirm")
    @Operation(summary = "确认打款完成", description = "确认微信支付打款完成")
    public Result<Void> confirmPayment(
            @Parameter(description = "提现申请ID") @PathVariable Long withdrawId,
            @Parameter(description = "微信交易单号") @RequestParam String wechatTransactionId,
            @Parameter(description = "微信支付单号") @RequestParam(required = false) String wechatPaymentNo) {
        
        log.info("确认打款完成: withdrawId={}, wechatTransactionId={}", withdrawId, wechatTransactionId);
        
        try {
            // TODO: 权限检查 - 需要财务操作权限
            
            withdrawBkService.confirmPayment(withdrawId, wechatTransactionId, wechatPaymentNo);
            
            // 清除相关缓存
            clearWithdrawCache(withdrawId);
            clearWithdrawListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("确认打款完成失败: withdrawId={}", withdrawId, e);
            return Result.error("确认打款完成失败: " + e.getMessage());
        }
    }

    /**
     * 标记打款失败
     */
    @PostMapping("/{withdrawId}/fail")
    @Operation(summary = "标记打款失败", description = "标记微信支付打款失败")
    public Result<Void> markFailed(
            @Parameter(description = "提现申请ID") @PathVariable Long withdrawId,
            @Parameter(description = "失败原因") @RequestParam String failureReason) {
        
        log.info("标记打款失败: withdrawId={}, failureReason={}", withdrawId, failureReason);
        
        try {
            // TODO: 权限检查 - 需要财务操作权限
            
            withdrawBkService.markFailed(withdrawId, failureReason);
            
            // 清除相关缓存
            clearWithdrawCache(withdrawId);
            clearWithdrawListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("标记打款失败失败: withdrawId={}", withdrawId, e);
            return Result.error("标记打款失败失败: " + e.getMessage());
        }
    }

    /**
     * 获取提现统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取提现统计数据", description = "获取提现相关统计信息")
    public Result<WithdrawStatsResponse> getWithdrawStats(
            @Parameter(description = "统计类型") @RequestParam(defaultValue = "overview") String type,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        
        log.info("获取提现统计: type={}, startDate={}, endDate={}", type, startDate, endDate);
        
        try {
            // 检查缓存
            String cacheKey = String.format("bk.withdraws:stats:%s:%s:%s", type, startDate, endDate);
            WithdrawStatsResponse cached = (WithdrawStatsResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            WithdrawStatsResponse result = withdrawBkService.getWithdrawStats(type, startDate, endDate);
            
            // 缓存结果10分钟
            redisTemplate.opsForValue().set(cacheKey, result, 10, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取提现统计失败", e);
            return Result.error("获取提现统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取待审核数量
     */
    @GetMapping("/pending-count")
    @Operation(summary = "获取待审核数量", description = "获取当前待审核的提现申请数量")
    public Result<Long> getPendingCount() {
        try {
            // 实时查询，不使用缓存
            Long count = withdrawBkService.getPendingCount();
            return Result.success(count);
            
        } catch (Exception e) {
            log.error("获取待审核数量失败", e);
            return Result.error("获取待审核数量失败: " + e.getMessage());
        }
    }

    /**
     * 导出提现记录
     */
    @GetMapping("/export")
    @Operation(summary = "导出提现记录", description = "根据条件导出提现记录到Excel")
    public Result<String> exportWithdraws(
            @ModelAttribute WithdrawQueryRequest request) {
        
        log.info("导出提现记录: request={}", request);
        
        try {
            // TODO: 权限检查 - 需要数据导出权限
            
            String filePath = withdrawBkService.exportWithdraws(request);
            return Result.success(filePath);
            
        } catch (Exception e) {
            log.error("导出提现记录失败", e);
            return Result.error("导出提现记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取提现状态选项
     */
    @GetMapping("/statuses")
    @Operation(summary = "获取提现状态选项", description = "获取所有可用的提现状态")
    public Result<Map<String, String>> getWithdrawStatuses() {
        try {
            // 检查缓存
            String cacheKey = "bk.withdraws:statuses";
            Map<String, String> cached = (Map<String, String>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            Map<String, String> result = withdrawBkService.getWithdrawStatuses();
            
            // 缓存结果1小时
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取提现状态失败", e);
            return Result.error("获取提现状态失败: " + e.getMessage());
        }
    }

    /**
     * 清除提现详情缓存
     */
    private void clearWithdrawCache(Long withdrawId) {
        String cacheKey = "bk.withdraw:detail:" + withdrawId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 清除提现列表缓存
     */
    private void clearWithdrawListCache() {
        // 使用模式匹配删除所有提现列表缓存
        redisTemplate.delete(redisTemplate.keys("bk.withdraws:list:*"));
        redisTemplate.delete("bk.withdraws:stats:*");
    }
}
