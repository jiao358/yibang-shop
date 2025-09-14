package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 刷新Token请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class RefreshTokenRequest {
    
    /**
     * 刷新令牌
     */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
