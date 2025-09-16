package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 后台任务统计
 */
@Data
public class TaskStatsResponse {

    @Schema(description = "总任务数")
    private Long totalTasks;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按类型统计")
    private Map<String, Long> byType;
}


