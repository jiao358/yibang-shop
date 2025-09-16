package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WithdrawDetailResponse {
    @Schema(description = "提现ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "金额(分)")
    private Integer amount;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "微信交易号")
    private String wechatTransactionId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "完成时间")
    private LocalDateTime completedAt;
}


