package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台任务列表项
 */
@Data
public class TaskListResponse {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "奖励金额(分)")
    private Integer rewardAmount;

    @Schema(description = "佣金等级")
    private String rewardLevel;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}


