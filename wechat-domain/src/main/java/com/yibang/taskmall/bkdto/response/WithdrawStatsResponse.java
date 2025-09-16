package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

@Data
public class WithdrawStatsResponse {
    @Schema(description = "总申请数")
    private Long total;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按日期聚合")
    private Map<String, Long> byDate;
}


