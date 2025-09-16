-- =============================================
-- 收益模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 收益记录表
CREATE TABLE `earnings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收益ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（来自ERP系统）',
  `user_task_id` bigint(20) DEFAULT NULL COMMENT '关联的用户任务ID',
  `amount` int(11) NOT NULL COMMENT '收益金额（分）',
  `type` varchar(20) NOT NULL COMMENT '收益类型：task_reward-任务奖励, withdrawal-提现, refund-退款, bonus-奖金, invite_reward-邀请奖励, sign_bonus-签到奖励',
  `status` varchar(20) DEFAULT 'pending' COMMENT '收益状态：pending-待处理, completed-已完成, failed-失败, cancelled-已取消',
  `description` varchar(500) DEFAULT NULL COMMENT '收益描述',
  `source_task_title` varchar(200) DEFAULT NULL COMMENT '来源任务标题',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `processed_at` timestamp NULL DEFAULT NULL COMMENT '处理完成时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_task_id` (`user_task_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_earnings_user_task_id` FOREIGN KEY (`user_task_id`) REFERENCES `user_tasks` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收益记录表';