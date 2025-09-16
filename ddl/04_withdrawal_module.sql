-- =============================================
-- 提现模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 提现记录表
CREATE TABLE `withdrawals` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '提现ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（来自ERP系统）',
  `amount` int(11) NOT NULL COMMENT '提现金额（分）',
  `status` varchar(20) DEFAULT 'pending' COMMENT '提现状态：pending-待处理, processing-处理中, completed-已完成, failed-失败, cancelled-已取消',
  `withdrawal_type` varchar(20) DEFAULT 'wechat_pay' COMMENT '提现方式：wechat_pay-微信支付, alipay-支付宝, bank_card-银行卡',
  `wechat_transaction_id` varchar(100) DEFAULT NULL COMMENT '微信交易单号',
  `wechat_payment_no` varchar(100) DEFAULT NULL COMMENT '微信支付单号',
  `failure_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '处理完成时间',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';