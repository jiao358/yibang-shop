package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("system_config")
public class SystemConfig {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("config_key")
    private String configKey; // 配置键

    @TableField("config_value")
    private String configValue; // 配置值


    @TableField("config_type")
    private String configType; // 配置类型：string, number, boolean, json

    @TableField("description")
    private String description; // 配置描述

    @TableField("is_public")
    private Boolean isPublic; // 是否公开


    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt; // 创建时间

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt; // 更新时间

    @TableField("created_by")
    private String createdBy; // 创建人

    @TableField("updated_by")
    private String updatedBy; // 更新人
}