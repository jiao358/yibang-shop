package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户等级更新请求
 */
@Data
public class UserLevelUpdateRequest {

    @NotBlank
    @Schema(description = "新等级")
    private String newLevel;

    @Schema(description = "调整原因")
    private String reason;
}
