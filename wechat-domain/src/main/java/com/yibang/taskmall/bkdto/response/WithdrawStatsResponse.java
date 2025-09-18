package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class WithdrawStatsResponse {
    @Schema(description = "总申请数")
    private Long totalWithdraws;

    @Schema(description = "待处理数")
    private Long pendingWithdraws;

    @Schema(description = "已完成数")
    private Long completedWithdraws;

    @Schema(description = "失败数")
    private Long failedWithdraws;

    @Schema(description = "总金额")
    private Long totalAmount;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按日期聚合")
    private Map<String, Long> byDate;
}


