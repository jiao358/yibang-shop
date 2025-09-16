package com.yibang.taskmall.dto.request;

import lombok.Data;

/**
 * 任务领取请求DTO
 */
@Data
public class TaskClaimRequest {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 任务ID
     */
    private Long taskId;
    
    /**
     * 客户端IP地址
     * 用于风控和统计
     */
    private String clientIp;
    
    /**
     * 设备信息
     * 用于防刷和统计
     */
    private String deviceInfo;
}
