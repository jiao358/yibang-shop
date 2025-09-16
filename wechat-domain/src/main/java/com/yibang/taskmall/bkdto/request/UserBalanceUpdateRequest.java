package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户余额调整请求
 */
@Data
public class UserBalanceUpdateRequest {

    @NotNull
    @Schema(description = "调整金额(分)")
    private Long amount;

    @NotBlank
    @Schema(description = "调整类型: add-增加, subtract-减少")
    private String type;

    @Schema(description = "调整原因")
    private String reason;
}
