package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class WithdrawBatchRequest {
    @NotEmpty
    @Schema(description = "提现申请ID列表")
    private List<Long> withdrawIds;

    @NotNull
    @Schema(description = "是否通过")
    private Boolean approved;

    @Schema(description = "备注/拒绝原因")
    private String remark;
}


