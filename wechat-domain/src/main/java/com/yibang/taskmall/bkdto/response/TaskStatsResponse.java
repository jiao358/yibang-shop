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

    @Schema(description = "活跃任务数")
    private Long activeTasks;

    @Schema(description = "非活跃任务数")
    private Long inactiveTasks;

    @Schema(description = "已完成任务数")
    private Long completedTasks;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按类型统计")
    private Map<String, Long> byType;
}


