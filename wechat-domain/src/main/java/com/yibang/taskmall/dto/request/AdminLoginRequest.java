package com.yibang.taskmall.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 管理端登录请求
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Schema(description = "管理端登录请求")
public class AdminLoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "123456")
    private String password;
}
