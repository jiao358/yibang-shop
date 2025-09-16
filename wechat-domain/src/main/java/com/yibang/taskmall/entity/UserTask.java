package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户任务实体类
 */
@Data
@TableName("user_tasks")
public class UserTask {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId; // 用户ID（来自ERP系统）
    
    @TableField("task_id")
    private Long taskId; // 任务ID
    
    @TableField("status")
    private String status; // 任务状态：claimed-已领取, completed-已完成, verified-已验证, expired-已过期, failed-失败
    
    @TableField("claimed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime claimedAt; // 领取时间
    
    @TableField("completed_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt; // 完成时间
    
    @TableField("verified_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt; // 验证时间
    
    @TableField("proof_data")
    private String proofData; // 完成凭证数据（JSON格式）
    
    @TableField("reward_amount")
    private Integer rewardAmount; // 实际奖励金额（分）
    
    @TableField("failure_reason")
    private String failureReason; // 失败原因
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 创建时间
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt; // 更新时间
}