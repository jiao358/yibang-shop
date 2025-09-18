-- =============================================
-- 字典模块 DDL（MySQL 5.7）
-- =============================================

CREATE TABLE IF NOT EXISTS `dict_items` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `group_code` VARCHAR(64) NOT NULL COMMENT '字典分组编码，例如 task_type、user_level 等',
  `item_code` VARCHAR(64) NOT NULL COMMENT '字典项编码',
  `label_zh` VARCHAR(200) NOT NULL COMMENT '中文标签',
  `label_en` VARCHAR(200) DEFAULT NULL COMMENT '英文标签',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用：1-启用 0-禁用',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序（越大越靠前）',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_item` (`group_code`, `item_code`),
  KEY `idx_group_sort` (`group_code`, `enabled`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统字典表';

-- 兜底初始化（可选）
INSERT INTO `dict_items` (`group_code`, `item_code`, `label_zh`, `label_en`, `enabled`, `sort`) VALUES
('task_type','ad','广告任务','Ad',1,100),
('task_type','video','视频任务','Video',1,90),
('task_type','app_install','应用安装','App Install',1,80),
('task_type','survey','问卷调查','Survey',1,70),
('task_type','share','分享任务','Share',1,60),
('user_level','normal','普通用户','Normal',1,100),
('user_level','signed','签约用户','Signed',1,90),
('user_level','vip','VIP用户','VIP',1,80),
('commission_level','low','低','Low',1,100),
('commission_level','medium','中','Medium',1,90),
('commission_level','high','高','High',1,80),
('withdraw_status','pending','待处理','Pending',1,100),
('withdraw_status','processing','处理中','Processing',1,90),
('withdraw_status','completed','已完成','Completed',1,80),
('withdraw_status','failed','失败','Failed',1,70),
('withdraw_status','cancelled','已取消','Cancelled',1,60),
('order_status','pending','待支付','Pending',1,100),
('order_status','paid','已支付','Paid',1,90),
('order_status','shipped','已发货','Shipped',1,80),
('order_status','received','已收货','Received',1,70),
('order_status','completed','已完成','Completed',1,60),
('order_status','refunding','退款中','Refunding',1,50),
('order_status','refunded','已退款','Refunded',1,40),
('notify_type','system','系统通知','System',1,100),
('notify_type','task','任务通知','Task',1,90),
('agreements','privacy','隐私协议','Privacy Policy',1,100),
('agreements','user_guide','用户须知','User Guide',1,90);
