package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.request.WechatLoginRequest;
import com.yibang.taskmall.dto.request.RefreshTokenRequest;
import com.yibang.taskmall.dto.response.LoginResponse;
import com.yibang.taskmall.dto.response.TokenResponse;

/**
 * 认证服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface AuthService {
    
    /**
     * 微信登录
     * 
     * @param request 微信登录请求
     * @return 登录响应
     */
    LoginResponse wechatLogin(WechatLoginRequest request);
    
    /**
     * 刷新Token
     * 
     * @param request 刷新Token请求
     * @return Token响应
     */
    TokenResponse refreshToken(RefreshTokenRequest request);
    
    /**
     * 获取用户信息
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    Object getUserInfo(Long userId);
    
    /**
     * 退出登录
     * 
     * @param userId 用户ID
     * @return 退出结果
     */
    Boolean logout(Long userId);
}
