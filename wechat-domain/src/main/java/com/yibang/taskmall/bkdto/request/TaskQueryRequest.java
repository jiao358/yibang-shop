package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台任务查询请求
 */
@Data
public class TaskQueryRequest {

    @Schema(description = "任务类型")
    private String type;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "关键词(标题/描述)")
    private String keyword;

    @Schema(description = "开始日期(yyyy-MM-dd)")
    private String startDate;

    @Schema(description = "结束日期(yyyy-MM-dd)")
    private String endDate;

    @Override
    public String toString() {
        return String.format("type=%s&status=%s&kw=%s&sd=%s&ed=%s", type, status, keyword, startDate, endDate);
    }
}


