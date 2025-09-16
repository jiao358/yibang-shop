package com.yibang.taskmall.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Configuration
@ConfigurationProperties(prefix = "wechat.miniapp")
@Data
public class WechatConfig {

    /**
     * 微信小程序AppId
     */
    private String appId;

    /**
     * 微信小程序AppSecret
     */
    private String appSecret;

    /**
     * 微信小程序服务
     */
    @Bean
    public WxMaService wxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(appId);
        config.setSecret(appSecret);
        
        WxMaService service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        return service;
    }
}
