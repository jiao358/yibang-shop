package com.yibang.taskmall.hsf.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAuthRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 客户端标识
     */
    private String clientId;

    /**
     * 请求来源
     */
    private String source;
}
