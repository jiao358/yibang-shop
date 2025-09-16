package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务实体类
 */
@Data
@TableName("tasks")
public class Task {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("title")
    private String title; // 任务标题
    
    @TableField("description")
    private String description; // 任务描述
    
    @TableField("image_url")
    private String imageUrl; // 任务宣传图片URL
    
    @TableField("type")
    private String type; // 任务类型：ad-广告任务, video-视频任务, app_install-应用安装, survey-问卷调查, share-分享任务
    
    @TableField("reward_amount")
    private Integer rewardAmount; // 奖励金额（分）
    
    @TableField("reward_level")
    private String rewardLevel; // 佣金等级：low-低, medium-中, high-高
    
    @TableField("requirements")
    private String requirements; // 任务要求配置（JSON格式）
    
    @TableField("user_level")
    private String userLevel; // 适用用户等级：normal-普通用户, signed-签约用户, vip-VIP用户, all-所有用户
    
    @TableField("status")
    private String status; // 任务状态：active-活跃, inactive-非活跃, completed-已完成
    
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime; // 任务过期时间
    
    @TableField("max_claim_count")
    private Integer maxClaimCount; // 最大领取次数，-1表示无限制
    
    @TableField("current_claim_count")
    private Integer currentClaimCount; // 当前已领取次数
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 创建时间
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt; // 更新时间
}