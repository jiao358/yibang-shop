package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 收益记录实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("earnings")
public class Earnings {

    /**
     * 收益ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（来自ERP系统）
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联的用户任务ID
     */
    @TableField("user_task_id")
    private Long userTaskId;

    /**
     * 收益金额（分）
     */
    @TableField("amount")
    private Integer amount;

    /**
     * 收益类型：task_reward-任务奖励, withdrawal-提现, refund-退款, bonus-奖金
     */
    @TableField("type")
    private String type;

    /**
     * 收益状态：pending-待处理, completed-已完成, failed-失败, cancelled-已取消
     */
    @TableField("status")
    private String status;

    /**
     * 收益描述
     */
    @TableField("description")
    private String description;

    /**
     * 来源任务标题
     */
    @TableField("source_task_title")
    private String sourceTaskTitle;

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
}
