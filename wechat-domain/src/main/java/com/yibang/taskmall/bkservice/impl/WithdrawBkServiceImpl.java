package com.yibang.taskmall.bkservice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.bkdto.request.WithdrawApprovalRequest;
import com.yibang.taskmall.bkdto.request.WithdrawBatchRequest;
import com.yibang.taskmall.bkdto.request.WithdrawQueryRequest;
import com.yibang.taskmall.bkdto.response.WithdrawDetailResponse;
import com.yibang.taskmall.bkdto.response.WithdrawListResponse;
import com.yibang.taskmall.bkdto.response.WithdrawStatsResponse;
import com.yibang.taskmall.bkservice.WithdrawBkService;
import com.yibang.taskmall.entity.Withdrawal;
import com.yibang.taskmall.mapper.WithdrawalMapper;
import com.yibang.taskmall.utils.RedisCacheUtils;
import com.yibang.taskmall.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawBkServiceImpl implements WithdrawBkService {

    private final WithdrawalMapper withdrawalMapper;
    private final RedisCacheUtils redisCacheUtils;
    private final ObjectMapper objectMapper;

    private static final String WITHDRAW_CACHE_PREFIX = "withdraw";
    private static final String WITHDRAW_LIST_CACHE_PREFIX = "withdraw:list";
    private static final String WITHDRAW_STATS_CACHE_PREFIX = "withdraw:stats";

    @Override
    public Page<WithdrawListResponse> getWithdrawList(WithdrawQueryRequest request, Integer page, Integer size) {
        // 构建缓存key
        String cacheKey = RedisCacheUtils.buildKey(WITHDRAW_LIST_CACHE_PREFIX, 
            request.toString(), String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Page.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化提现列表缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<Withdrawal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(request.getStatus()), Withdrawal::getStatus, request.getStatus())
                   .eq(request.getUserId() != null, Withdrawal::getUserId, request.getUserId());
        
        // 日期范围查询
        if (StringUtils.hasText(request.getStartDate())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartDate() + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Withdrawal::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(request.getEndDate())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndDate() + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Withdrawal::getCreatedAt, endTime);
        }
        
        queryWrapper.orderByDesc(Withdrawal::getCreatedAt);

        // 手动分页查询
        long startTime = System.currentTimeMillis();
        
        // 查询总数（使用count优化）
        long total = withdrawalMapper.selectCount(queryWrapper);
        
        // 分页查询数据
        int offset = PageUtils.getOffset(page, size);
        int limit = size == null || size < 1 ? PageUtils.DEFAULT_PAGE_SIZE : Math.min(size, PageUtils.MAX_PAGE_SIZE);
        
        List<Withdrawal> withdrawals = withdrawalMapper.selectList(queryWrapper.last("LIMIT " + offset + ", " + limit));
        
        // 转换为响应DTO
        List<WithdrawListResponse> records = withdrawals.stream()
            .map(this::convertToWithdrawListResponse)
            .collect(Collectors.toList());
        
        Page<WithdrawListResponse> responsePage = PageUtils.buildPage(records, total, page, size);
        
        // 记录分页信息
        PageUtils.logPageInfo("getWithdrawList", page, size, total, System.currentTimeMillis() - startTime);

        // 缓存结果（10分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(responsePage), 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化提现列表缓存失败: {}", e.getMessage());
        }

        return responsePage;
    }

    @Override
    public WithdrawDetailResponse getWithdrawDetail(Long id) {
        String cacheKey = RedisCacheUtils.buildKey(WITHDRAW_CACHE_PREFIX, "detail", String.valueOf(id));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, WithdrawDetailResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化提现详情缓存失败: {}", e.getMessage());
            }
        }

        // 查询数据库
        Withdrawal withdrawal = withdrawalMapper.selectById(id);
        if (withdrawal == null) {
            return null;
        }

        WithdrawDetailResponse response = convertToWithdrawDetailResponse(withdrawal);
        
        // 缓存结果（30分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 30, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化提现详情缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public void approveWithdraw(Long id, WithdrawApprovalRequest request) {
        Withdrawal withdrawal = withdrawalMapper.selectById(id);
        if (withdrawal == null) {
            throw new RuntimeException("提现记录不存在");
        }

        if (!"pending".equals(withdrawal.getStatus())) {
            throw new RuntimeException("只能审核待处理的提现申请");
        }

        withdrawal.setStatus("processing");
        withdrawal.setProcessedAt(LocalDateTime.now());
        
        int result = withdrawalMapper.updateById(withdrawal);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(WITHDRAW_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(WITHDRAW_CACHE_PREFIX, "detail", String.valueOf(id)));
            redisCacheUtils.deletePattern(WITHDRAW_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public Map<String, Object> batchApprove(WithdrawBatchRequest request) {
        List<Long> successIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();
        
        for (Long id : request.getWithdrawIds()) {
            try {
                WithdrawApprovalRequest approvalRequest = new WithdrawApprovalRequest();
                approvalRequest.setRemark("批量审核通过");
                approveWithdraw(id, approvalRequest);
                successIds.add(id);
            } catch (Exception e) {
                log.warn("批量审核提现失败: id={}, error={}", id, e.getMessage());
                failedIds.add(id);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successIds.size());
        result.put("failedCount", failedIds.size());
        result.put("successIds", successIds);
        result.put("failedIds", failedIds);
        
        return result;
    }

    @Override
    public void confirmPayment(Long withdrawId, String wechatTransactionId, String wechatPaymentNo) {
        Withdrawal withdrawal = withdrawalMapper.selectById(withdrawId);
        if (withdrawal == null) {
            throw new RuntimeException("提现记录不存在");
        }

        withdrawal.setStatus("completed");
        withdrawal.setWechatTransactionId(wechatTransactionId);
        withdrawal.setWechatPaymentNo(wechatPaymentNo);
        withdrawal.setCompletedAt(LocalDateTime.now());
        
        int result = withdrawalMapper.updateById(withdrawal);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(WITHDRAW_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(WITHDRAW_CACHE_PREFIX, "detail", String.valueOf(withdrawId)));
            redisCacheUtils.deletePattern(WITHDRAW_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public void markFailed(Long withdrawId, String failureReason) {
        Withdrawal withdrawal = withdrawalMapper.selectById(withdrawId);
        if (withdrawal == null) {
            throw new RuntimeException("提现记录不存在");
        }

        withdrawal.setStatus("failed");
        withdrawal.setFailureReason(failureReason);
        withdrawal.setProcessedAt(LocalDateTime.now());
        
        int result = withdrawalMapper.updateById(withdrawal);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(WITHDRAW_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(WITHDRAW_CACHE_PREFIX, "detail", String.valueOf(withdrawId)));
            redisCacheUtils.deletePattern(WITHDRAW_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public WithdrawStatsResponse getWithdrawStats(String type, String startDate, String endDate) {
        String cacheKey = RedisCacheUtils.buildKey(WITHDRAW_STATS_CACHE_PREFIX, type, startDate, endDate);
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, WithdrawStatsResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化提现统计缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<Withdrawal> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Withdrawal::getStatus, type);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Withdrawal::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Withdrawal::getCreatedAt, endTime);
        }

        // 统计查询
        long totalWithdraws = withdrawalMapper.selectCount(queryWrapper);
        long pendingWithdraws = withdrawalMapper.selectCount(queryWrapper.clone().eq(Withdrawal::getStatus, "pending"));
        long completedWithdraws = withdrawalMapper.selectCount(queryWrapper.clone().eq(Withdrawal::getStatus, "completed"));
        long failedWithdraws = withdrawalMapper.selectCount(queryWrapper.clone().eq(Withdrawal::getStatus, "failed"));

        WithdrawStatsResponse response = new WithdrawStatsResponse();
        response.setTotalWithdraws(totalWithdraws);
        response.setPendingWithdraws(pendingWithdraws);
        response.setCompletedWithdraws(completedWithdraws);
        response.setFailedWithdraws(failedWithdraws);
        // 统计总金额
        LambdaQueryWrapper<Withdrawal> amountWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            amountWrapper.eq(Withdrawal::getStatus, type);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            amountWrapper.ge(Withdrawal::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            amountWrapper.le(Withdrawal::getCreatedAt, endTime);
        }
        
        List<Withdrawal> withdrawals = withdrawalMapper.selectList(amountWrapper);
        long totalAmount = withdrawals.stream()
            .mapToLong(w -> w.getAmount() != null ? w.getAmount() : 0L)
            .sum();
        response.setTotalAmount(totalAmount);

        // 缓存结果（10分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化提现统计缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public Long getPendingCount() {
        String cacheKey = RedisCacheUtils.buildKey(WITHDRAW_CACHE_PREFIX, "pending:count");
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return Long.parseLong(cachedData);
            } catch (NumberFormatException e) {
                log.warn("反序列化待处理提现数量缓存失败: {}", e.getMessage());
            }
        }

        LambdaQueryWrapper<Withdrawal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Withdrawal::getStatus, "pending");
        long count = withdrawalMapper.selectCount(queryWrapper);

        // 缓存结果（5分钟）
        redisCacheUtils.set(cacheKey, String.valueOf(count), 5, java.util.concurrent.TimeUnit.MINUTES);

        return count;
    }

    @Override
    public String exportWithdraws(WithdrawQueryRequest request) {
        // TODO: 实现导出功能
        return "export_" + System.currentTimeMillis() + ".xlsx";
    }

    @Override
    public Map<String, String> getWithdrawStatuses() {
        Map<String, String> statuses = new HashMap<>();
        statuses.put("pending", "待处理");
        statuses.put("processing", "处理中");
        statuses.put("completed", "已完成");
        statuses.put("failed", "失败");
        statuses.put("cancelled", "已取消");
        return statuses;
    }

    private WithdrawListResponse convertToWithdrawListResponse(Withdrawal withdrawal) {
        WithdrawListResponse response = new WithdrawListResponse();
        BeanUtils.copyProperties(withdrawal, response);
        return response;
    }

    private WithdrawDetailResponse convertToWithdrawDetailResponse(Withdrawal withdrawal) {
        WithdrawDetailResponse response = new WithdrawDetailResponse();
        BeanUtils.copyProperties(withdrawal, response);
        return response;
    }
}


