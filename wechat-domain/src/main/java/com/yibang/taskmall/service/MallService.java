package com.yibang.taskmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.request.CreateOrderRequest;
import com.yibang.taskmall.dto.request.PaymentRequest;
import com.yibang.taskmall.dto.response.ProductResponse;
import com.yibang.taskmall.dto.response.ProductDetailResponse;
import com.yibang.taskmall.dto.response.OrderResponse;

/**
 * 商城服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface MallService {
    
    /**
     * 获取商品列表
     * 
     * @param category 商品分类
     * @param keyword 搜索关键词
     * @param sort 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 商品列表
     */
    Page<ProductResponse> getProducts(String category, String keyword, String sort, Integer page, Integer size);
    
    /**
     * 获取商品详情
     * 
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductDetailResponse getProductDetail(Long productId);
    
    /**
     * 获取商品分类
     * 
     * @return 商品分类
     */
    Object getCategories();
    
    /**
     * 创建订单
     * 
     * @param request 创建订单请求
     * @return 订单信息
     */
    OrderResponse createOrder(CreateOrderRequest request);
    
    /**
     * 获取订单列表
     * 
     * @param userId 用户ID
     * @param status 订单状态
     * @param page 页码
     * @param size 每页大小
     * @return 订单列表
     */
    Page<OrderResponse> getOrders(Long userId, String status, Integer page, Integer size);
    
    /**
     * 获取订单详情
     * 
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderResponse getOrderDetail(Long orderId);
    
    /**
     * 处理支付
     * 
     * @param request 支付请求
     * @return 支付结果
     */
    Object processPayment(PaymentRequest request);
    
    /**
     * 取消订单
     * 
     * @param orderId 订单ID
     * @return 取消结果
     */
    Boolean cancelOrder(Long orderId);
    
    /**
     * 确认收货
     * 
     * @param orderId 订单ID
     * @return 确认结果
     */
    Boolean confirmReceipt(Long orderId);
}
