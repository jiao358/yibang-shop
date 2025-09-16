package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SystemConfigRequest {
    @Schema(description = "配置值")
    private String value;

    @Schema(description = "描述")
    private String description;
}


