package com.yibang.taskmall.bkdto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台用户查询请求
 */
@Data
public class UserQueryRequest {

    @Schema(description = "用户等级")
    private String level;

    @Schema(description = "用户状态")
    private String status;

    @Schema(description = "关键词(昵称/手机号)")
    private String keyword;

    @Schema(description = "开始日期(yyyy-MM-dd)")
    private String startDate;

    @Schema(description = "结束日期(yyyy-MM-dd)")
    private String endDate;

    @Override
    public String toString() {
        return String.format("level=%s&status=%s&kw=%s&sd=%s&ed=%s", level, status, keyword, startDate, endDate);
    }
}
