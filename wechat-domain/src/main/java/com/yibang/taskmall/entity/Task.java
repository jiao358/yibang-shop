package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 任务实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tasks")
public class Task {

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务标题
     */
    @TableField("title")
    private String title;

    /**
     * 任务描述
     */
    @TableField("description")
    private String description;

    /**
     * 任务类型：ad-广告任务, video-视频任务, app_install-应用安装, survey-问卷调查, share-分享任务
     */
    @TableField("type")
    private String type;

    /**
     * 奖励金额（分）
     */
    @TableField("reward_amount")
    private Integer rewardAmount;

    /**
     * 任务要求配置（JSON格式）
     */
    @TableField("requirements")
    private String requirements;

    /**
     * 适用用户等级：normal-普通用户, signed-签约用户, vip-VIP用户, all-所有用户
     */
    @TableField("user_level")
    private String userLevel;

    /**
     * 任务状态：active-活跃, inactive-非活跃, completed-已完成
     */
    @TableField("status")
    private String status;

    /**
     * 任务过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 最大领取次数，-1表示无限制
     */
    @TableField("max_claim_count")
    private Integer maxClaimCount;

    /**
     * 当前已领取次数
     */
    @TableField("current_claim_count")
    private Integer currentClaimCount;

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
