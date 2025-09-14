package com.yibang.taskmall.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 创建订单请求DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class CreateOrderRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 商品列表
     */
    @NotEmpty(message = "商品列表不能为空")
    private List<OrderItem> items;
    
    /**
     * 收货地址ID
     */
    private Long addressId;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 订单项
     */
    @Data
    public static class OrderItem {
        
        /**
         * 商品ID
         */
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        /**
         * 商品数量
         */
        @NotNull(message = "商品数量不能为空")
        private Integer quantity;
        
        /**
         * 商品规格
         */
        private String spec;
    }
}
