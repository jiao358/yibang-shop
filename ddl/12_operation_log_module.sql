-- =============================================
-- 操作日志模块数据库初始化脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 操作日志表
CREATE TABLE `operation_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人姓名',
  `action` varchar(50) NOT NULL COMMENT '操作动作',
  `module` varchar(50) NOT NULL COMMENT '操作模块',
  `target_type` varchar(50) DEFAULT NULL COMMENT '目标类型',
  `target_id` varchar(100) DEFAULT NULL COMMENT '目标ID',
  `target_name` varchar(200) DEFAULT NULL COMMENT '目标名称',
  `request_data` text COMMENT '请求数据（JSON）',
  `response_data` text COMMENT '响应数据（JSON）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COMMENT '用户代理',
  `execution_time` bigint(20) DEFAULT NULL COMMENT '执行时间（毫秒）',
  `status` varchar(20) DEFAULT 'success' COMMENT '操作状态：success-成功, failed-失败',
  `error_message` text COMMENT '错误信息',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operator` (`operator_id`, `created_at`),
  KEY `idx_action` (`action`, `created_at`),
  KEY `idx_module` (`module`, `created_at`),
  KEY `idx_target` (`target_type`, `target_id`),
  KEY `idx_status` (`status`, `created_at`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 2. 操作日志分区（可选，用于大数据量场景）
-- 按月分区，自动清理历史数据
/*
ALTER TABLE operation_logs PARTITION BY RANGE (TO_DAYS(created_at)) (
    PARTITION p202401 VALUES LESS THAN (TO_DAYS('2024-02-01')),
    PARTITION p202402 VALUES LESS THAN (TO_DAYS('2024-03-01')),
    PARTITION p202403 VALUES LESS THAN (TO_DAYS('2024-04-01')),
    PARTITION p202404 VALUES LESS THAN (TO_DAYS('2024-05-01')),
    PARTITION p202405 VALUES LESS THAN (TO_DAYS('2024-06-01')),
    PARTITION p202406 VALUES LESS THAN (TO_DAYS('2024-07-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
*/

-- 3. 创建日志清理存储过程
DELIMITER $$

CREATE PROCEDURE `CleanExpiredOperationLogs`(IN days_to_keep INT)
BEGIN
    DECLARE exit handler for sqlexception
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    -- 删除指定天数之前的日志
    DELETE FROM operation_logs 
    WHERE created_at < DATE_SUB(NOW(), INTERVAL days_to_keep DAY);
    
    -- 获取删除的记录数
    SELECT ROW_COUNT() as deleted_count;
    
    COMMIT;
END$$

DELIMITER ;

-- 4. 创建日志统计视图
CREATE VIEW `v_operation_log_stats` AS
SELECT 
    DATE(created_at) as log_date,
    module,
    action,
    status,
    COUNT(*) as operation_count,
    AVG(execution_time) as avg_execution_time,
    MAX(execution_time) as max_execution_time
FROM operation_logs 
WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY DATE(created_at), module, action, status
ORDER BY log_date DESC, operation_count DESC;

-- 5. 插入初始化数据（可选）
INSERT INTO `operation_logs` (`operator_id`, `operator_name`, `action`, `module`, `target_type`, `target_name`, `status`, `created_at`) VALUES
(1, 'system', 'SYSTEM_INIT', 'SYSTEM', 'system', '系统初始化', 'success', NOW()),
(1, 'system', 'DATABASE_INIT', 'SYSTEM', 'database', '数据库初始化', 'success', NOW());

-- 6. 创建定时清理事件（可选）
-- 每天凌晨2点自动清理90天前的日志
/*
CREATE EVENT IF NOT EXISTS `clean_operation_logs`
ON SCHEDULE EVERY 1 DAY
STARTS '2024-01-16 02:00:00'
DO
  CALL CleanExpiredOperationLogs(90);
*/

-- =============================================
-- 使用说明
-- =============================================

/*
1. 日志记录：
   - 所有敏感操作都会自动记录
   - 包含操作人、操作内容、IP地址等信息
   - 支持异步记录，不影响业务性能

2. 日志查询：
   - 支持按操作人、操作类型、模块、时间范围查询
   - 提供统计视图，方便分析操作趋势
   - 支持分页查询，处理大数据量

3. 日志清理：
   - 提供存储过程自动清理过期日志
   - 可配置保留天数
   - 支持分区表，提高查询和清理性能

4. 性能优化：
   - 创建了多个复合索引，优化查询性能
   - 支持异步记录，避免影响主业务
   - 可选择分区表，处理大数据量场景
*/
