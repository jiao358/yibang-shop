package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务详情响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class TaskDetailResponse {
    
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
     * 任务类型
     */
    private String type;
    
    /**
     * 奖励金额（分）
     */
    private Integer rewardAmount;
    
    /**
     * 任务要求
     */
    private List<String> requirements;
    
    /**
     * 完成步骤
     */
    private List<String> steps;
    
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
    private LocalDateTime expireTime;
    
    /**
     * 预计完成时间（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 剩余可领取次数
     */
    private Integer remainingCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
