package com.yibang.taskmall.bkdto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台用户详情
 */
@Data
public class UserDetailResponse {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "微信OpenID")
    private String openid;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "用户等级")
    private String userLevel;

    @Schema(description = "累计收益(分)")
    private Long totalEarnings;

    @Schema(description = "可用余额(分)")
    private Long availableBalance;

    @Schema(description = "冻结余额(分)")
    private Long frozenBalance;

    @Schema(description = "完成任务总数")
    private Integer totalTasks;

    @Schema(description = "邀请码")
    private String inviteCode;

    @Schema(description = "邀请人数")
    private Integer inviteCount;

    @Schema(description = "用户状态")
    private String status;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
