package com.yibang.taskmall.service;

import com.yibang.taskmall.entity.User;

public interface WechatPayService {
    /**
     * 企业付款到零钱（同步触发，返回渠道交易号；真实场景建议走异步回调校验）
     * @param user           提现用户（读取 openid）
     * @param amountInCents  金额（分）
     * @param requestNo      业务请求单号
     * @return 渠道交易号（非空表示提交成功）
     */
    String transferToWallet(User user, long amountInCents, String requestNo) throws Exception;
}


