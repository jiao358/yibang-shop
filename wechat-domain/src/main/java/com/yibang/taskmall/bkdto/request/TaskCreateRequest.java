package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台创建任务请求
 */
@Data
public class TaskCreateRequest {

    @NotBlank
    @Schema(description = "任务标题")
    private String title;

    @Schema(description = "任务描述")
    private String description;

    @Schema(description = "任务宣传图片URL")
    private String imageUrl;

    @NotBlank
    @Schema(description = "任务类型")
    private String type;

    @NotNull
    @Min(0)
    @Schema(description = "奖励金额(分)")
    private Integer rewardAmount;

    @Schema(description = "佣金等级")
    private String rewardLevel;

    @Schema(description = "任务要求(JSON)")
    private String requirements;

    @Schema(description = "适用用户等级")
    private String userLevel;
}


