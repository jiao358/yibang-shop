package com.yibang.taskmall.dto.response;

import lombok.Data;

@Data
public class WalletSummaryDTO {
    private Long totalIn;     // 本期收入（分）
    private Long totalOut;    // 本期提现（分）
    private Long netChange;   // 净增（分）
    private Long balance;     // 当前可用余额（分）
    private Long frozenBalance; // 冻结余额（分）
}


