package com.yibang.taskmall.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统配置响应DTO
 *
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统配置响应")
public class SystemConfigDTO {

    @Schema(description = "配置ID")
    private Long id;

    @Schema(description = "配置键名")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "配置类型")
    private String configType;

    @Schema(description = "配置分组")
    private String configGroup;

    @Schema(description = "配置描述")
    private String configDesc;

    @Schema(description = "是否启用")
    private Boolean isEnabled;

    @Schema(description = "是否加密存储")
    private Boolean isEncrypted;

    @Schema(description = "排序权重")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
