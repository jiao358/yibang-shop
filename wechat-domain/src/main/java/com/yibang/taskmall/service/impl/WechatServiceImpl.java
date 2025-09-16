package com.yibang.taskmall.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.yibang.taskmall.dto.response.WechatUserInfo;
import com.yibang.taskmall.service.WechatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.stereotype.Service;

/**
 * 微信服务实现类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WechatServiceImpl implements WechatService {

    private final WxMaService wxMaService;

    @Override
    public WechatUserInfo getUserInfoByCode(String code) {
        try {
            // 1. 通过code获取session信息
            WxMaJscode2SessionResult sessionResult = wxMaService.getUserService().getSessionInfo(code);
            
            if (sessionResult == null) {
                log.error("微信登录失败：无法获取session信息，code: {}", code);
                throw new RuntimeException("微信登录失败：无法获取session信息");
            }
            
            // 2. 构建微信用户信息
            WechatUserInfo userInfo = new WechatUserInfo();
            userInfo.setOpenid(sessionResult.getOpenid());
            userInfo.setSessionKey(sessionResult.getSessionKey());
            userInfo.setUnionid(sessionResult.getUnionid());
            
            log.info("微信登录成功，openid: {}", userInfo.getOpenid());
            return userInfo;
            
        } catch (WxErrorException e) {
            log.error("微信登录失败：{}", e.getMessage(), e);
            throw new RuntimeException("微信登录失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("微信登录异常：{}", e.getMessage(), e);
            throw new RuntimeException("微信登录异常：" + e.getMessage());
        }
    }
    
    @Override
    public String getPhoneNumberByCode(String phoneCode) {
        try {
            // 通过手机号授权码获取手机号信息
            WxMaPhoneNumberInfo phoneNumberInfo = wxMaService.getUserService().getPhoneNoInfo(phoneCode);
            
            if (phoneNumberInfo == null) {
                log.error("微信获取手机号失败：无法获取手机号信息，phoneCode: {}", phoneCode);
                throw new RuntimeException("微信获取手机号失败：无法获取手机号信息");
            }
            
            String phoneNumber = phoneNumberInfo.getPhoneNumber();
            log.info("微信获取手机号成功，手机号: {}", phoneNumber);
            return phoneNumber;
            
        } catch (WxErrorException e) {
            log.error("微信获取手机号失败：{}", e.getMessage(), e);
            throw new RuntimeException("微信获取手机号失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("微信获取手机号异常：{}", e.getMessage(), e);
            throw new RuntimeException("微信获取手机号异常：" + e.getMessage());
        }
    }
}
