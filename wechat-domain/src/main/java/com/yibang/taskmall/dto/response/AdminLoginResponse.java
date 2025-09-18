package com.yibang.taskmall.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端登录响应
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@Schema(description = "管理端登录响应")
public class AdminLoginResponse {

    @Schema(description = "JWT令牌")
    private String token;

    @Schema(description = "用户信息")
    private AdminUserInfo user;

    @Schema(description = "令牌过期时间(秒)")
    private Long expiresIn;
}
