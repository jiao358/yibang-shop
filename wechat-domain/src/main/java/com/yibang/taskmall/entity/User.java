package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息实体
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@TableName("users")
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("openid") // 微信OpenID
    private String openid;

    @TableField("unionid") // 微信UnionID
    private String unionid;

    @TableField("nickname") // 用户昵称
    private String nickname;

    @TableField("avatar") // 用户头像
    private String avatar;

    @TableField("phone") // 手机号
    private String phone;

    @TableField("gender") // 性别：0-未知, 1-男, 2-女
    private Integer gender;

    @TableField("city") // 城市
    private String city;

    @TableField("province") // 省份
    private String province;

    @TableField("country") // 国家
    private String country;

    @TableField("user_level") // 用户等级
    private String userLevel;


    @TableField("total_earnings") // 累计收益（分）
    private Long totalEarnings;

    @TableField("available_balance") // 可用余额（分）
    private Long availableBalance;

    @TableField("frozen_balance") // 冻结余额（分）
    private Long frozenBalance;

    @TableField("total_tasks") // 完成任务总数
    private Integer totalTasks;

    @TableField("invite_code") // 邀请码
    private String inviteCode;

    @TableField("invited_by") // 邀请人ID
    private Long invitedBy;

    @TableField("invite_count") // 邀请人数
    private Integer inviteCount;

    @TableField("last_login_time") // 最后登录时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastLoginTime;

    @TableField("status") // 用户状态
    private String status;

    @TableField(value = "created_at", fill = FieldFill.INSERT) // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE) // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatedAt;
}
