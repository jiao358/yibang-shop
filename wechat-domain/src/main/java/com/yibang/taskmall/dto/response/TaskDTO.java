package com.yibang.taskmall.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务返回DTO
 */
@Data
public class TaskDTO {
    
    /**
     * 任务ID
     */
    private Long id;
    
    /**
     * 任务标题
     */
    private String title;
    
    /**
     * 任务描述
     */
    private String description;
    
    /**
     * 任务宣传图片URL
     */
    private String imageUrl;
    
    /**
     * 任务类型
     */
    private String type;
    
    /**
     * 任务类型显示名称
     */
    private String typeDisplayName;
    
    /**
     * 奖励金额（分）
     */
    private Integer rewardAmount;
    
    /**
     * 奖励金额（元，格式化后）
     */
    private String rewardAmountDisplay;
    
    /**
     * 佣金等级
     */
    private String rewardLevel;
    
    /**
     * 佣金等级显示名称
     */
    private String rewardLevelDisplayName;
    
    /**
     * 任务要求配置
     */
    private String requirements;
    
    /**
     * 适用用户等级
     */
    private String userLevel;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 过期时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
    
    /**
     * 最大领取次数
     */
    private Integer maxClaimCount;
    
    /**
     * 当前已领取次数
     */
    private Integer currentClaimCount;
    
    /**
     * 剩余可领取次数
     */
    private Integer remainingClaimCount;
    
    /**
     * 是否可领取
     */
    private Boolean canClaim;
    
    /**
     * 用户任务状态（如果已领取）
     */
    private String userTaskStatus;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
