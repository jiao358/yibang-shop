package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("operation_logs")
public class OperationLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("operator_id")
    private Long operatorId; // 操作人ID

    @TableField("operator_name")
    private String operatorName; // 操作人姓名

    @TableField("action")
    private String action; // 操作动作

    @TableField("module")
    private String module; // 操作模块

    @TableField("target_type")
    private String targetType; // 目标类型

    @TableField("target_id")
    private String targetId; // 目标ID

    @TableField("target_name")
    private String targetName; // 目标名称

    @TableField("request_data")
    private String requestData; // 请求数据（JSON）

    @TableField("response_data")
    private String responseData; // 响应数据（JSON）

    @TableField("ip_address")
    private String ipAddress; // IP地址

    @TableField("user_agent")
    private String userAgent; // 用户代理

    @TableField("execution_time")
    private Long executionTime; // 执行时间（毫秒）

    @TableField("status")
    private String status; // 操作状态：success-成功, failed-失败

    @TableField("error_message")
    private String errorMessage; // 错误信息

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt; // 创建时间
}
