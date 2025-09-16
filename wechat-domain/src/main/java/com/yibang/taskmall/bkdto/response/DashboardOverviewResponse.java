package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DashboardOverviewResponse {
    @Schema(description = "新增用户数")
    private Long newUsers;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "完成任务数")
    private Long completedTasks;

    @Schema(description = "提现申请数")
    private Long withdrawRequests;

    @Schema(description = "GMV(分)")
    private Long gmv;
}


