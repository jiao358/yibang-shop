-- =============================================
-- 任务模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 任务表
CREATE TABLE `tasks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `title` varchar(200) NOT NULL COMMENT '任务标题',
  `description` text COMMENT '任务描述',
  `image_url` varchar(500) DEFAULT NULL COMMENT '任务宣传图片URL',
  `type` varchar(20) NOT NULL COMMENT '任务类型：ad-广告任务, video-视频任务, app_install-应用安装, survey-问卷调查, share-分享任务',
  `reward_amount` int(11) NOT NULL DEFAULT '0' COMMENT '奖励金额（分）',
  `reward_level` varchar(10) DEFAULT 'low' COMMENT '佣金等级：low-低, medium-中, high-高',
  `requirements` text COMMENT '任务要求配置（JSON格式）',
  `user_level` varchar(20) DEFAULT 'all' COMMENT '适用用户等级：normal-普通用户, signed-签约用户, vip-VIP用户, all-所有用户',
  `status` varchar(20) DEFAULT 'active' COMMENT '任务状态：active-活跃, inactive-非活跃, completed-已完成',
  `expire_time` timestamp NULL DEFAULT NULL COMMENT '任务过期时间',
  `max_claim_count` int(11) DEFAULT '-1' COMMENT '最大领取次数，-1表示无限制',
  `current_claim_count` int(11) DEFAULT '0' COMMENT '当前已领取次数',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_type_status` (`type`,`status`),
  KEY `idx_user_level` (`user_level`),
  KEY `idx_expire_time` (`expire_time`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 2. 用户任务表
CREATE TABLE `user_tasks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户任务ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID（来自ERP系统）',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `status` varchar(20) DEFAULT 'claimed' COMMENT '任务状态：claimed-已领取, completed-已完成, verified-已验证, expired-已过期, failed-失败',
  `claimed_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
  `completed_at` timestamp NULL DEFAULT NULL COMMENT '完成时间',
  `verified_at` timestamp NULL DEFAULT NULL COMMENT '验证时间',
  `proof_data` text COMMENT '完成凭证数据（JSON格式）',
  `reward_amount` int(11) DEFAULT NULL COMMENT '实际奖励金额（分）',
  `failure_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_task_status` (`task_id`,`status`),
  KEY `idx_claimed_at` (`claimed_at`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_task_id` (`task_id`),
  CONSTRAINT `fk_user_tasks_task_id` FOREIGN KEY (`task_id`) REFERENCES `tasks` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户任务表';

-- 插入示例任务数据
INSERT INTO `tasks` (`title`, `description`, `image_url`, `type`, `reward_amount`, `reward_level`, `requirements`, `user_level`, `status`, `expire_time`, `max_claim_count`) VALUES
('观看30秒广告', '观看指定广告30秒即可获得奖励，完成后立即获得现金收益', 'https://example.com/images/ad-task.jpg', 'ad', 50, 'low', '{"duration": 30, "ad_type": "video"}', 'all', 'active', DATE_ADD(NOW(), INTERVAL 30 DAY), 1000),
('安装推荐应用', '下载并安装指定应用，保持7天即可获得高额奖励', 'https://example.com/images/app-task.jpg', 'app_install', 200, 'medium', '{"app_package": "com.example.app", "keep_days": 7}', 'all', 'active', DATE_ADD(NOW(), INTERVAL 15 DAY), 500),
('完成用户调研', '填写用户满意度调研问卷，助力产品优化改进', 'https://example.com/images/survey-task.jpg', 'survey', 100, 'low', '{"questions_count": 10, "min_score": 80}', 'all', 'active', DATE_ADD(NOW(), INTERVAL 7 DAY), 200),
('分享到朋友圈', '分享指定内容到微信朋友圈，获得点赞奖励', 'https://example.com/images/share-task.jpg', 'share', 30, 'low', '{"platform": "wechat", "min_likes": 5}', 'all', 'active', DATE_ADD(NOW(), INTERVAL 3 DAY), 1000),
('高价值视频任务', '观看完整广告视频并完成互动，获得高额收益', 'https://example.com/images/video-task.jpg', 'video', 500, 'high', '{"duration": 120, "interaction_required": true}', 'signed', 'active', DATE_ADD(NOW(), INTERVAL 7 DAY), 100);
