-- =============================================
-- 系统配置模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 系统配置表
CREATE TABLE `system_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(20) DEFAULT 'string' COMMENT '配置类型：string-字符串, number-数字, boolean-布尔, json-JSON, text-文本',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `is_public` tinyint(1) DEFAULT '0' COMMENT '是否公开：0-否, 1-是',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 插入初始配置数据
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('app_name', '任务商城', 'string', '应用名称', 1),
('app_version', '1.0.0', 'string', '应用版本', 1),
('min_withdrawal_amount', '100', 'number', '最小提现金额（分）', 0),
('max_daily_withdrawal', '10000', 'number', '每日最大提现金额（分）', 0),
('withdrawal_fee_rate', '0.01', 'number', '提现手续费率', 0),
('task_reward_rate', '0.8', 'number', '任务奖励分成比例', 0),
('invite_reward_amount', '500', 'number', '邀请奖励金额（分）', 1),
('sign_bonus_amount', '100', 'number', '签到奖励金额（分）', 1);