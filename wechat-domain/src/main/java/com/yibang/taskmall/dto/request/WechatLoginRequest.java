package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 微信登录请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class WechatLoginRequest {
    
    /**
     * 微信授权码
     */
    @NotBlank(message = "授权码不能为空")
    private String code;
    
    /**
     * 加密数据
     */
    private String encryptedData;
    
    /**
     * 初始向量
     */
    private String iv;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 手机号授权码
     */
    private String phoneCode;
}
