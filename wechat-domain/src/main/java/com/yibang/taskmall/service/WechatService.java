package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.response.WechatUserInfo;

/**
 * 微信服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface WechatService {
    
    /**
     * 通过code获取微信用户信息
     * 
     * @param code 微信授权码
     * @return 微信用户信息
     */
    WechatUserInfo getUserInfoByCode(String code);
    
    /**
     * 通过手机号授权码获取手机号
     * 
     * @param phoneCode 手机号授权码
     * @return 手机号信息
     */
    String getPhoneNumberByCode(String phoneCode);
}
