package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class OrderResponse {
    
    /**
     * 订单ID
     */
    private Long id;
    
    /**
     * 订单号
     */
    private String orderNumber;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 商品总金额（分）
     */
    private Integer productAmount;
    
    /**
     * 运费（分）
     */
    private Integer shippingFee;
    
    /**
     * 优惠金额（分）
     */
    private Integer discountAmount;
    
    /**
     * 实付金额（分）
     */
    private Integer totalAmount;
    
    /**
     * 订单商品列表
     */
    private List<OrderItem> items;
    
    /**
     * 收货地址
     */
    private Address address;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 支付时间
     */
    private LocalDateTime paidAt;
    
    /**
     * 发货时间
     */
    private LocalDateTime shippedAt;
    
    /**
     * 完成时间
     */
    private LocalDateTime completedAt;
    
    /**
     * 订单商品项
     */
    @Data
    public static class OrderItem {
        
        /**
         * 商品ID
         */
        private Long productId;
        
        /**
         * 商品名称
         */
        private String productName;
        
        /**
         * 商品图片
         */
        private String productImage;
        
        /**
         * 商品规格
         */
        private String spec;
        
        /**
         * 商品价格（分）
         */
        private Integer price;
        
        /**
         * 商品数量
         */
        private Integer quantity;
    }
    
    /**
     * 收货地址
     */
    @Data
    public static class Address {
        
        /**
         * 收货人姓名
         */
        private String name;
        
        /**
         * 收货人电话
         */
        private String phone;
        
        /**
         * 收货地址
         */
        private String address;
    }
}
