package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.request.WithdrawCreateRequest;

public interface WithdrawService {
    Long createWithdraw(Long userId, WithdrawCreateRequest req);
    void handleWxpayCallback(String requestNo, boolean success, String channelTxnId, String remark);
}


