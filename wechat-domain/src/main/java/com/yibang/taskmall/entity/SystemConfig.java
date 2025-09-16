package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 *
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("system_configs")
public class SystemConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("config_key") // 配置键名
    private String configKey;

    @TableField("config_value") // 配置值（JSON格式）
    private String configValue;

    @TableField("config_type") // 配置类型：string,json,number,boolean
    private String configType;

    @TableField("config_group") // 配置分组
    private String configGroup;

    @TableField("config_desc") // 配置描述
    private String configDesc;

    @TableField("is_enabled") // 是否启用
    private Boolean isEnabled;

    @TableField("is_encrypted") // 是否加密存储
    private Boolean isEncrypted;

    @TableField("sort_order") // 排序权重
    private Integer sortOrder;

    @TableField("created_by") // 创建人ID
    private Long createdBy;

    @TableField("updated_by") // 更新人ID
    private Long updatedBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
