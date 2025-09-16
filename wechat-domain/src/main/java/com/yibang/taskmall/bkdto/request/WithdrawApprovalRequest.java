package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WithdrawApprovalRequest {

    @NotNull
    @Schema(description = "是否通过")
    private Boolean approved;

    @Schema(description = "备注/拒绝原因")
    private String remark;
}


