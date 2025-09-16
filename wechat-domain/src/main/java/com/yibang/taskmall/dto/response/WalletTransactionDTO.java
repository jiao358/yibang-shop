package com.yibang.taskmall.dto.response;

import lombok.Data;

@Data
public class WalletTransactionDTO {
    private Long id;
    private String occurTime;
    private String bizType; // commission | withdrawal
    private Long taskId;
    private String taskTitle;
    private Long amountInCents;
    private Long balanceAfterInCents;
    private String remark;
}


