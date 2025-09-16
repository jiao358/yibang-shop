-- =============================================
-- 钱包模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 钱包交易流水表
CREATE TABLE `wallet_transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '流水ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `biz_type` varchar(20) NOT NULL COMMENT '业务类型：commission-佣金入账，withdrawal-提现',
  `status` varchar(20) DEFAULT 'success' COMMENT '状态：success-成功，pending-处理中，failed-失败（提现用）',
  `task_id` bigint(20) DEFAULT NULL COMMENT '关联任务ID（佣金入账时可用）',
  `task_title` varchar(200) DEFAULT NULL COMMENT '任务标题（快照）',
  `amount_in_cents` bigint(20) NOT NULL DEFAULT '0' COMMENT '金额（分），正值表示入账，提现为正值存储，前端以biz_type区分展示',
  `balance_after_in_cents` bigint(20) NOT NULL DEFAULT '0' COMMENT '变动后余额（分）',
  `occur_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发生时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_time` (`user_id`,`occur_time`),
  KEY `idx_user_type_time` (`user_id`,`biz_type`,`occur_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='钱包交易流水表';


