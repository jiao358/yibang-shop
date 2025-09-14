package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

/**
 * 提现请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class WithdrawalRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 提现金额（分）
     */
    @NotNull(message = "提现金额不能为空")
    @Min(value = 1, message = "提现金额必须大于0")
    private Integer amount;
    
    /**
     * 提现类型
     */
    private String withdrawType = "wechat_pay";
    
    /**
     * 提现备注
     */
    private String remark;
}
