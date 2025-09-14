package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 领取任务请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class ClaimTaskRequest {
    
    /**
     * 任务ID
     */
    @NotNull(message = "任务ID不能为空")
    private Long taskId;
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
