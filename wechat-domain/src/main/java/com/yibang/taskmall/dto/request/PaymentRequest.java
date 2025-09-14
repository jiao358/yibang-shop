package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 支付请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class PaymentRequest {
    
    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    /**
     * 支付方式
     */
    private String paymentMethod = "wechat_pay";
    
    /**
     * 支付金额（分）
     */
    @NotNull(message = "支付金额不能为空")
    private Integer amount;
    
    /**
     * 支付描述
     */
    private String description;
}
