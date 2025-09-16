package com.yibang.taskmall.dto.response;

import lombok.Data;

/**
 * 用户任务统计DTO
 */
@Data
public class UserTaskStatsDTO {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 已完成任务数量
     */
    private Long completedTaskCount;
    
    /**
     * 进行中任务数量
     */
    private Long inProgressTaskCount;
    
    /**
     * 总任务收益（分）
     */
    private Long totalEarnings;
    
    /**
     * 总任务收益（元，格式化后）
     */
    private String totalEarningsDisplay;
    
    /**
     * 本月完成任务数量
     */
    private Long monthlyCompletedCount;
    
    /**
     * 本月任务收益（分）
     */
    private Long monthlyEarnings;
    
    /**
     * 本月任务收益（元，格式化后）
     */
    private String monthlyEarningsDisplay;
    
    /**
     * 任务完成率（百分比）
     */
    private Double completionRate;
    
    /**
     * 用户等级
     */
    private String userLevel;
    
    /**
     * 下一等级所需完成任务数
     */
    private Long tasksNeededForNextLevel;
}
