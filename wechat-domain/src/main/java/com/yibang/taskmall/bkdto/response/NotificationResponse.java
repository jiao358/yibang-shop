package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    @Schema(description = "通知ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}


