package com.yibang.taskmall.service;

import java.util.Map;

/**
 * 微信支付服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface WechatPayService {

    /**
     * 企业付款到零钱
     * 
     * @param withdrawalId 提现记录ID
     * @param openid 用户openid
     * @param amount 金额（分）
     * @param description 付款描述
     * @return 付款结果
     */
    Map<String, Object> transferToBalance(Long withdrawalId, String openid, Integer amount, String description);

    /**
     * 查询企业付款状态
     * 
     * @param partnerTradeNo 商户订单号
     * @return 付款状态
     */
    Map<String, Object> queryTransferStatus(String partnerTradeNo);

    /**
     * 处理支付回调
     * 
     * @param callbackData 回调数据
     * @return 处理结果
     */
    Map<String, Object> handlePaymentCallback(Map<String, Object> callbackData);

    /**
     * 验证支付签名
     * 
     * @param data 待验证数据
     * @param signature 签名
     * @return 验证结果
     */
    boolean verifySignature(Map<String, Object> data, String signature);

    /**
     * 生成支付签名
     * 
     * @param data 待签名数据
     * @return 签名
     */
    String generateSignature(Map<String, Object> data);

    /**
     * 检查微信支付配置
     * 
     * @return 配置状态
     */
    boolean checkPaymentConfig();

    /**
     * 获取支付配置信息
     * 
     * @return 配置信息（脱敏）
     */
    Map<String, Object> getPaymentConfigInfo();

    /**
     * 处理提现失败回滚
     * 
     * @param withdrawalId 提现记录ID
     * @param failureReason 失败原因
     * @return 回滚结果
     */
    boolean handleWithdrawFailure(Long withdrawalId, String failureReason);

    /**
     * 批量查询付款状态
     * 
     * @param tradeNos 商户订单号列表
     * @return 状态查询结果
     */
    Map<String, Object> batchQueryTransferStatus(java.util.List<String> tradeNos);
}