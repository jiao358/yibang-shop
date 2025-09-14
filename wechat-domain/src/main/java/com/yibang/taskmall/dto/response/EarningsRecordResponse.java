package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收益记录响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class EarningsRecordResponse {
    
    /**
     * 收益ID
     */
    private Long id;
    
    /**
     * 收益金额（分）
     */
    private Integer amount;
    
    /**
     * 收益类型
     */
    private String type;
    
    /**
     * 收益状态
     */
    private String status;
    
    /**
     * 收益描述
     */
    private String description;
    
    /**
     * 来源任务标题
     */
    private String sourceTaskTitle;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 处理完成时间
     */
    private LocalDateTime processedAt;
}
