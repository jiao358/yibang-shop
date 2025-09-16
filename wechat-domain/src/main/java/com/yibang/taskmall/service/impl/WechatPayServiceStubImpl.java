package com.yibang.taskmall.service.impl;

import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.service.WechatPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WechatPayServiceStubImpl implements WechatPayService {
    @Override
    public String transferToWallet(User user, long amountInCents, String requestNo) throws Exception {
        // 这里为占位实现：真实实现应调用 wxpay 企业付款 API，并校验签名、证书等
        log.info("[STUB] 微信企业付款，userId={}, openid={}, amount={}, requestNo={}",
                user.getId(), user.getOpenid(), amountInCents, requestNo);
        // 模拟返回渠道交易号
        return "WXTXN" + requestNo.substring(0, 12);
    }
}


