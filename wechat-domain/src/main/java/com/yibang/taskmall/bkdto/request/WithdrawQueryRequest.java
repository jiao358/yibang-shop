package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WithdrawQueryRequest {
    @Schema(description = "提现状态")
    private String status;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "开始日期(yyyy-MM-dd)")
    private String startDate;

    @Schema(description = "结束日期(yyyy-MM-dd)")
    private String endDate;

    @Override
    public String toString() {
        return String.format("status=%s&uid=%s&sd=%s&ed=%s", status, userId, startDate, endDate);
    }
}


