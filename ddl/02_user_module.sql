-- =============================================
-- 用户模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 用户等级配置表
CREATE TABLE `user_level_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `level_name` varchar(50) NOT NULL COMMENT '等级名称',
  `level_code` varchar(20) NOT NULL COMMENT '等级代码：normal-普通用户, signed-签约用户, vip-VIP用户',
  `min_earnings` int(11) DEFAULT '0' COMMENT '最低收益要求（分）',
  `max_daily_withdrawal` int(11) DEFAULT '0' COMMENT '每日最大提现金额（分），0表示无限制',
  `withdrawal_fee_rate` decimal(5,4) DEFAULT '0.0000' COMMENT '提现手续费率',
  `task_bonus_rate` decimal(5,4) DEFAULT '0.0000' COMMENT '任务奖励加成率',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_level_code` (`level_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户等级配置表';

-- 2. 用户信息表（与ERP系统同步）
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `openid` varchar(50) NOT NULL COMMENT '微信OpenID',
  `unionid` varchar(50) DEFAULT NULL COMMENT '微信UnionID',
  `nickname` varchar(100) DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '用户头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint(1) DEFAULT '0' COMMENT '性别：0-未知, 1-男, 2-女',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `country` varchar(50) DEFAULT NULL COMMENT '国家',
  `user_level` varchar(20) DEFAULT 'normal' COMMENT '用户等级',
  `total_earnings` bigint(20) DEFAULT '0' COMMENT '累计收益（分）',
  `available_balance` bigint(20) DEFAULT '0' COMMENT '可用余额（分）',
  `frozen_balance` bigint(20) DEFAULT '0' COMMENT '冻结余额（分）',
  `total_tasks` int(11) DEFAULT '0' COMMENT '完成任务总数',
  `invite_code` varchar(20) DEFAULT NULL COMMENT '邀请码',
  `invited_by` bigint(20) DEFAULT NULL COMMENT '邀请人ID',
  `invite_count` int(11) DEFAULT '0' COMMENT '邀请人数',
  `last_login_time` timestamp NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` varchar(20) DEFAULT 'active' COMMENT '用户状态',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_openid` (`openid`),
  UNIQUE KEY `uk_invite_code` (`invite_code`),
  KEY `idx_user_level` (`user_level`),
  KEY `idx_status` (`status`),
  KEY `idx_invited_by` (`invited_by`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- 插入初始数据
-- 用户等级配置数据
INSERT INTO `user_level_config` (`level_name`, `level_code`, `min_earnings`, `max_daily_withdrawal`, `withdrawal_fee_rate`, `task_bonus_rate`) VALUES
('普通用户', 'normal', 0, 10000, 0.0100, 0.0000),
('签约用户', 'signed', 10000, 50000, 0.0050, 0.1000),
('VIP用户', 'vip', 50000, 0, 0.0000, 0.2000);