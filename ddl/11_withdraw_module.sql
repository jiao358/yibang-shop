-- =============================================
-- 提现模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- =============================================

CREATE TABLE `withdraw_requests` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '提现请求ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `request_no` varchar(64) NOT NULL COMMENT '提现请求单号',
  `amount_in_cents` bigint(20) NOT NULL COMMENT '提现金额（分）',
  `status` varchar(20) NOT NULL DEFAULT 'pending' COMMENT '状态：pending-处理中，success-成功，failed-失败',
  `channel` varchar(50) DEFAULT 'wxpay' COMMENT '渠道：wxpay',
  `channel_txn_id` varchar(128) DEFAULT NULL COMMENT '渠道交易号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_request_no` (`request_no`),
  KEY `idx_user_status_time` (`user_id`,`status`,`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现请求表';


