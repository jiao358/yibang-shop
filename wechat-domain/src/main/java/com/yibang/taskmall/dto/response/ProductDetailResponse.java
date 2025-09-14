package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class ProductDetailResponse {
    
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
     * 商品图片列表
     */
    private List<String> images;
    
    /**
     * 商品规格
     */
    private List<ProductSpec> specs;
    
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
     * 评价数量
     */
    private Integer reviewCount;
    
    /**
     * 商品标签
     */
    private List<String> tags;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
    
    /**
     * 商品规格
     */
    @Data
    public static class ProductSpec {
        
        /**
         * 规格名称
         */
        private String name;
        
        /**
         * 规格选项
         */
        private List<SpecOption> options;
        
        /**
         * 规格选项
         */
        @Data
        public static class SpecOption {
            
            /**
             * 选项标签
             */
            private String label;
            
            /**
             * 选项值
             */
            private String value;
        }
    }
}
