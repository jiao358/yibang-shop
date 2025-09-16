package com.yibang.taskmall.dto.request;

import lombok.Data;

@Data
public class WithdrawCreateRequest {
    private Long amountInCents; // 提现金额（分）
}


