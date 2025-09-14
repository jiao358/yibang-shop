package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提现记录实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("withdrawals")
public class Withdrawal {

    /**
     * 提现ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（来自ERP系统）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 提现金额（分）
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 提现状态：pending-待处理, processing-处理中, completed-已完成, failed-失败, cancelled-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 微信交易单号
     */
    @TableField("wechat_transaction_id")
    private String wechatTransactionId;

    /**
     * 微信支付单号
     */
    @TableField("wechat_payment_no")
    private String wechatPaymentNo;

    /**
     * 失败原因
     */
    @TableField("failure_reason")
    private String failureReason;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 处理完成时间
     */
    @TableField("processed_at")
    private LocalDateTime processedAt;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;
}
