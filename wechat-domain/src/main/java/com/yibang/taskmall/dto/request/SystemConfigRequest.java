package com.yibang.taskmall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 系统配置请求DTO
 *
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Schema(description = "系统配置请求")
public class SystemConfigRequest {

    @Schema(description = "配置键名", example = "customer_service.qr_code")
    private String configKey;

    @Schema(description = "配置值", example = "https://example.com/qr-code.jpg")
    private String configValue;

    @Schema(description = "配置类型", example = "string")
    private String configType;

    @Schema(description = "配置分组", example = "customer_service")
    private String configGroup;

    @Schema(description = "配置描述", example = "客服二维码URL")
    private String configDesc;

    @Schema(description = "是否启用", example = "true")
    private Boolean isEnabled;

    @Schema(description = "是否加密存储", example = "false")
    private Boolean isEncrypted;

    @Schema(description = "排序权重", example = "1")
    private Integer sortOrder;
}
