package com.yibang.taskmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.request.WithdrawalRequest;
import com.yibang.taskmall.dto.response.WithdrawalResponse;

/**
 * 提现服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface WithdrawalService {
    
    /**
     * 申请提现
     * 
     * @param request 提现请求
     * @return 申请结果
     */
    Boolean applyWithdrawal(WithdrawalRequest request);
    
    /**
     * 获取提现记录
     * 
     * @param userId 用户ID
     * @param status 提现状态
     * @param page 页码
     * @param size 每页大小
     * @return 提现记录
     */
    Page<WithdrawalResponse> getWithdrawals(Long userId, String status, Integer page, Integer size);
    
    /**
     * 获取提现详情
     * 
     * @param withdrawalId 提现ID
     * @return 提现详情
     */
    WithdrawalResponse getWithdrawalDetail(Long withdrawalId);
    
    /**
     * 取消提现
     * 
     * @param withdrawalId 提现ID
     * @return 取消结果
     */
    Boolean cancelWithdrawal(Long withdrawalId);
    
    /**
     * 获取提现配置
     * 
     * @param userId 用户ID
     * @return 提现配置
     */
    Object getWithdrawalConfig(Long userId);
    
    /**
     * 计算提现手续费
     * 
     * @param userId 用户ID
     * @param amount 提现金额
     * @return 手续费计算结果
     */
    Object calculateWithdrawalFee(Long userId, Integer amount);
}
