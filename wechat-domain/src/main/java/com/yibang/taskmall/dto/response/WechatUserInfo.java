package com.yibang.taskmall.dto.response;

import lombok.Data;

/**
 * 微信用户信息
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class WechatUserInfo {
    
    /**
     * 用户唯一标识
     */
    private String openid;
    
    /**
     * 会话密钥
     */
    private String sessionKey;
    
    /**
     * 用户在开放平台的唯一标识
     */
    private String unionid;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
    
    /**
     * 用户性别
     */
    private Integer gender;
    
    /**
     * 用户所在国家
     */
    private String country;
    
    /**
     * 用户所在省份
     */
    private String province;
    
    /**
     * 用户所在城市
     */
    private String city;
}
