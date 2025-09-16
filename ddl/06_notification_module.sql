-- =============================================
-- 通知模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 通知表
CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `type` varchar(20) NOT NULL COMMENT '通知类型：system-系统通知, task-任务通知, earnings-收益通知, order-订单通知, marketing-营销通知, promotion-推广通知',
  `channel` varchar(20) NOT NULL COMMENT '通知渠道：push-推送, sms-短信, email-邮件, wechat-微信, in_app-站内信',
  `status` varchar(20) DEFAULT 'pending' COMMENT '通知状态：pending-待发送, sent-已发送, delivered-已送达, failed-发送失败, read-已读',
  `priority` varchar(20) DEFAULT 'normal' COMMENT '优先级：low-低, normal-普通, high-高, urgent-紧急',
  `read_at` timestamp NULL DEFAULT NULL COMMENT '阅读时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_notifications_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';