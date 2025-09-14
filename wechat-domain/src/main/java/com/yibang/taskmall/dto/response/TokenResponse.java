package com.yibang.taskmall.dto.response;

import lombok.Data;

/**
 * Token响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class TokenResponse {
    
    /**
     * 访问令牌
     */
    private String accessToken;
    
    /**
     * 刷新令牌
     */
    private String refreshToken;
    
    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";
    
    /**
     * 过期时间（秒）
     */
    private Long expiresIn;
}
