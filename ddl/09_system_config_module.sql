-- =====================================================
-- 系统配置管理模块 DDL
-- 兼容 MySQL 5.7+
-- =====================================================

-- 系统配置表
CREATE TABLE `system_configs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键名',
  `config_value` text NOT NULL COMMENT '配置值（JSON格式）',
  `config_type` varchar(50) NOT NULL DEFAULT 'string' COMMENT '配置类型：string,json,number,boolean',
  `config_group` varchar(50) NOT NULL DEFAULT 'default' COMMENT '配置分组：customer_service,app_settings,upload,payment等',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `is_enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用：0-禁用,1-启用',
  `is_encrypted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否加密存储：0-否,1-是',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序权重',
  `created_by` bigint(20) DEFAULT NULL COMMENT '创建人ID',
  `updated_by` bigint(20) DEFAULT NULL COMMENT '更新人ID',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_group` (`config_group`),
  KEY `idx_is_enabled` (`is_enabled`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 文件上传记录表
CREATE TABLE `file_uploads` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '上传用户ID',
  `file_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件存储路径',
  `file_url` varchar(500) NOT NULL COMMENT '文件访问URL',
  `file_size` bigint(20) NOT NULL DEFAULT '0' COMMENT '文件大小（字节）',
  `file_type` varchar(100) NOT NULL COMMENT '文件MIME类型',
  `file_extension` varchar(20) NOT NULL COMMENT '文件扩展名',
  `upload_type` varchar(50) NOT NULL DEFAULT 'avatar' COMMENT '上传类型：avatar,document,image,video等',
  `business_id` bigint(20) DEFAULT NULL COMMENT '关联业务ID',
  `business_type` varchar(50) DEFAULT NULL COMMENT '关联业务类型',
  `upload_ip` varchar(45) DEFAULT NULL COMMENT '上传IP地址',
  `status` varchar(20) NOT NULL DEFAULT 'active' COMMENT '文件状态：active,deleted,expired',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_upload_type` (`upload_type`),
  KEY `idx_business` (`business_id`, `business_type`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_file_uploads_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件上传记录表';

-- 配置变更日志表
CREATE TABLE `config_change_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `config_id` bigint(20) NOT NULL COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键名',
  `old_value` text COMMENT '原配置值',
  `new_value` text COMMENT '新配置值',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型：CREATE,UPDATE,DELETE',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_ip` varchar(45) DEFAULT NULL COMMENT '操作IP',
  `change_reason` varchar(500) DEFAULT NULL COMMENT '变更原因',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_config_id` (`config_id`),
  KEY `idx_config_key` (`config_key`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_config_change_logs_config_id` FOREIGN KEY (`config_id`) REFERENCES `system_configs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配置变更日志表';

-- 插入默认配置数据
INSERT INTO `system_configs` (`config_key`, `config_value`, `config_type`, `config_group`, `config_desc`, `sort_order`) VALUES
('customer_service.qr_code', 'https://example.com/qr-code.jpg', 'string', 'customer_service', '客服二维码URL', 1),
('customer_service.title', '联系客服', 'string', 'customer_service', '客服页面标题', 2),
('customer_service.description', '如有任何问题，请扫码联系客服，我们将竭诚为您服务！', 'string', 'customer_service', '客服页面描述', 3),
('customer_service.work_time', '工作时间：周一至周日 9:00-21:00', 'string', 'customer_service', '客服工作时间', 4),
('customer_service.phone', '400-123-4567', 'string', 'customer_service', '客服电话', 5),
('customer_service.email', 'service@yibang-taskmall.com', 'string', 'customer_service', '客服邮箱', 6),

('app.name', '任务商城', 'string', 'app_settings', '应用名称', 1),
('app.version', '1.0.0', 'string', 'app_settings', '应用版本', 2),
('app.copyright', '© 2024 任务商城 版权所有', 'string', 'app_settings', '版权信息', 3),
('app.privacy_url', 'https://example.com/privacy', 'string', 'app_settings', '隐私政策URL', 4),
('app.terms_url', 'https://example.com/terms', 'string', 'app_settings', '用户协议URL', 5),
('app.about_us', '任务商城是一个通过完成任务获得收益的平台，致力于为用户提供更多收入来源。', 'string', 'app_settings', '关于我们', 6),

('upload.max_file_size', '10485760', 'number', 'upload', '最大文件上传大小（字节），默认10MB', 1),
('upload.allowed_types', '["jpg","jpeg","png","gif","webp"]', 'json', 'upload', '允许的文件类型', 2),
('upload.avatar_max_size', '2097152', 'number', 'upload', '头像最大文件大小（字节），默认2MB', 3),
('upload.storage_path', '/uploads', 'string', 'upload', '文件存储路径', 4),
('upload.base_url', 'https://cdn.yibang-taskmall.com', 'string', 'upload', '文件访问基础URL', 5);
