package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 后台用户统计
 */
@Data
public class UserStatsResponse {

    @Schema(description = "总用户数")
    private Long totalUsers;

    @Schema(description = "活跃用户数")
    private Long activeUsers;

    @Schema(description = "非活跃用户数")
    private Long inactiveUsers;

    @Schema(description = "新用户数")
    private Long newUsers;

    @Schema(description = "按等级统计")
    private Map<String, Long> byLevel;

    @Schema(description = "按状态统计")
    private Map<String, Long> byStatus;

    @Schema(description = "按日期聚合")
    private Map<String, Long> byDate;
}
