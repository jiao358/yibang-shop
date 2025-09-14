package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 完成任务请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class CompleteTaskRequest {
    
    /**
     * 完成凭证数据（JSON格式）
     */
    @NotBlank(message = "完成凭证不能为空")
    private String proofData;
    
    /**
     * 完成时间
     */
    private String completionTime;
    
    /**
     * 完成描述
     */
    private String description;
}
