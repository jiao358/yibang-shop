package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class ProductResponse {
    
    /**
     * 商品ID
     */
    private Long id;
    
    /**
     * 商品名称
     */
    private String name;
    
    /**
     * 商品描述
     */
    private String description;
    
    /**
     * 商品价格（分）
     */
    private Integer price;
    
    /**
     * 原价（分）
     */
    private Integer originalPrice;
    
    /**
     * 商品图片
     */
    private String image;
    
    /**
     * 商品分类
     */
    private String category;
    
    /**
     * 商品状态
     */
    private String status;
    
    /**
     * 库存数量
     */
    private Integer stock;
    
    /**
     * 销量
     */
    private Integer sales;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
