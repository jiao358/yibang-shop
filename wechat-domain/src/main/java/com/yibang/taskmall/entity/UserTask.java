package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户任务实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_tasks")
public class UserTask {

    /**
     * 用户任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（来自ERP系统）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 任务ID
     */
    @TableField("task_id")
    private Long taskId;

    /**
     * 任务状态：claimed-已领取, completed-已完成, verified-已验证, expired-已过期, failed-失败
     */
    @TableField("status")
    private String status;

    /**
     * 领取时间
     */
    @TableField("claimed_at")
    private LocalDateTime claimedAt;

    /**
     * 完成时间
     */
    @TableField("completed_at")
    private LocalDateTime completedAt;

    /**
     * 验证时间
     */
    @TableField("verified_at")
    private LocalDateTime verifiedAt;

    /**
     * 完成凭证数据（JSON格式）
     */
    @TableField("proof_data")
    private String proofData;

    /**
     * 实际奖励金额（分）
     */
    @TableField("reward_amount")
    private Integer rewardAmount;

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
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
