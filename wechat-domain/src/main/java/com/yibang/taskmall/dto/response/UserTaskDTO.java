package com.yibang.taskmall.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户任务返回DTO
 */
@Data
public class UserTaskDTO {
    
    /**
     * 用户任务ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 任务标题
     */
    private String taskTitle;
    
    /**
     * 任务描述
     */
    private String taskDescription;
    
    /**
     * 任务图片URL
     */
    private String taskImageUrl;
    
    /**
     * 任务类型
     */
    private String taskType;
    
    /**
     * 任务状态
     */
    private String status;
    
    /**
     * 任务状态显示名称
     */
    private String statusDisplayName;
    
    /**
     * 领取时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime claimedAt;
    
    /**
     * 完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;
    
    /**
     * 验证时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime verifiedAt;
    
    /**
     * 完成凭证数据
     */
    private String proofData;
    
    /**
     * 奖励金额（分）
     */
    private Integer rewardAmount;
    
    /**
     * 奖励金额（元，格式化后）
     */
    private String rewardAmountDisplay;
    
    /**
     * 失败原因
     */
    private String failureReason;
    
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
