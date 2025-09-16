package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户列表项
 */
@Data
public class UserListResponse {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "用户等级")
    private String userLevel;

    @Schema(description = "可用余额(分)")
    private Long availableBalance;

    @Schema(description = "完成任务数")
    private Integer totalTasks;

    @Schema(description = "用户状态")
    private String status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
