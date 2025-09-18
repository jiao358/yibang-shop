-- =============================================
-- 轮播图模块 DDL（MySQL 5.7）
-- =============================================

CREATE TABLE IF NOT EXISTS `banners` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title_zh` VARCHAR(200) NOT NULL COMMENT '中文标题',
  `title_en` VARCHAR(200) DEFAULT NULL COMMENT '英文标题',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `enable_jump` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否开启跳转：1-开启 0-关闭',
  `jump_target` TEXT DEFAULT NULL COMMENT '跳转目标（不限定类型：小程序页、外链、任务ID等）',
  `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序（越大越靠前）',
  `start_time` TIMESTAMP NULL DEFAULT NULL COMMENT '生效开始时间',
  `end_time` TIMESTAMP NULL DEFAULT NULL COMMENT '生效结束时间',
  `client` VARCHAR(32) NOT NULL DEFAULT 'wx' COMMENT '终端：wx等',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_enabled_sort` (`client`, `enabled`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='轮播图表';

-- 兜底初始化（可选）
INSERT INTO `banners` (`title_zh`,`title_en`,`image_url`,`enable_jump`,`jump_target`,`enabled`,`sort`,`client`) VALUES
('欢迎来到任务商城','Welcome','/static/images/banner1.jpg',1,'/pages/task/task',1,100,'wx');
