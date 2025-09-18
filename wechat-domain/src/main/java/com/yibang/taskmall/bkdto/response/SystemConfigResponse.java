package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SystemConfigResponse {
    @Schema(description = "配置键")
    private String key;

    @Schema(description = "配置值")
    private String value;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "分组")
    private String group;

    @Schema(description = "配置类型")
    private String type;
}


