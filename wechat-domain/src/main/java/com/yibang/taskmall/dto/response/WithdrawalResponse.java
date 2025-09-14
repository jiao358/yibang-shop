package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 提现响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class WithdrawalResponse {
    
    /**
     * 提现ID
     */
    private Long id;
    
    /**
     * 提现金额（分）
     */
    private Integer amount;
    
    /**
     * 提现状态
     */
    private String status;
    
    /**
     * 微信交易单号
     */
    private String wechatTransactionId;
    
    /**
     * 微信支付单号
     */
    private String wechatPaymentNo;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 处理完成时间
     */
    private LocalDateTime processedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
}
