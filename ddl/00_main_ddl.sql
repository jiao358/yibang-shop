-- =============================================
-- 任务商城系统数据库初始化主脚本
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- 版本: v1.0
-- =============================================

-- 设置字符集和排序规则
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库（如果不存在）
-- CREATE DATABASE IF NOT EXISTS `yibang_taskmall` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- USE `yibang_taskmall`;

-- =============================================
-- 执行顺序说明
-- =============================================
-- 1. 系统配置模块 (07_system_config.sql) - 基础配置
-- 2. 用户模块 (02_user_module.sql) - 用户相关表
-- 3. 任务模块 (01_task_module.sql) - 任务相关表
-- 4. 收益模块 (03_earnings_module.sql) - 收益相关表
-- 5. 提现模块 (04_withdrawal_module.sql) - 提现相关表
-- 6. 商城模块 (05_mall_module.sql) - 商城相关表
-- 7. 通知模块 (06_notification_module.sql) - 通知相关表

-- =============================================
-- 数据库初始化检查
-- =============================================

-- 检查MySQL版本
SELECT VERSION() as mysql_version;

-- 检查字符集设置
SELECT @@character_set_database as database_charset, 
       @@collation_database as database_collation;

-- =============================================
-- 创建数据库用户（可选）
-- =============================================
-- CREATE USER IF NOT EXISTS 'taskmall_user'@'%' IDENTIFIED BY 'your_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, INDEX, ALTER ON yibang_taskmall.* TO 'taskmall_user'@'%';
-- FLUSH PRIVILEGES;

-- =============================================
-- 执行各模块DDL脚本
-- =============================================

-- 注意：以下脚本需要按顺序执行，因为存在外键依赖关系

-- 1. 系统配置模块
SOURCE 07_system_config.sql;

-- 2. 用户模块
SOURCE 02_user_module.sql;

-- 3. 任务模块
SOURCE 01_task_module.sql;

-- 4. 收益模块
SOURCE 03_earnings_module.sql;

-- 5. 提现模块
SOURCE 04_withdrawal_module.sql;

-- 6. 商城模块
SOURCE 05_mall_module.sql;

-- 7. 通知模块
SOURCE 06_notification_module.sql;

-- 8. 收货地址模块
SOURCE 08_address_module.sql;
SOURCE 09_system_config_module.sql;

-- =============================================
-- 数据库初始化完成后的检查
-- =============================================

-- 检查所有表是否创建成功
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME,
    UPDATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;

-- 检查外键约束
SELECT 
    CONSTRAINT_NAME,
    TABLE_NAME,
    COLUMN_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM information_schema.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = DATABASE()
AND REFERENCED_TABLE_NAME IS NOT NULL
ORDER BY TABLE_NAME, CONSTRAINT_NAME;

-- 检查索引
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    NON_UNIQUE
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 检查存储过程和函数
SELECT 
    ROUTINE_NAME,
    ROUTINE_TYPE,
    CREATED,
    LAST_ALTERED
FROM information_schema.ROUTINES 
WHERE ROUTINE_SCHEMA = DATABASE()
ORDER BY ROUTINE_TYPE, ROUTINE_NAME;

-- 检查视图
SELECT 
    TABLE_NAME,
    VIEW_DEFINITION,
    CREATED,
    LAST_ALTERED
FROM information_schema.VIEWS 
WHERE TABLE_SCHEMA = DATABASE()
ORDER BY TABLE_NAME;

-- =============================================
-- 性能优化建议
-- =============================================

-- 1. 为经常查询的字段创建索引
-- 2. 定期执行 ANALYZE TABLE 更新表统计信息
-- 3. 根据业务需求调整 MySQL 配置参数
-- 4. 定期清理过期数据，保持数据库性能

-- 示例：更新表统计信息
-- ANALYZE TABLE users, tasks, orders, earnings, withdrawals;

-- =============================================
-- 数据备份建议
-- =============================================

-- 1. 定期执行全量备份
-- mysqldump -u root -p yibang_taskmall > backup_$(date +%Y%m%d_%H%M%S).sql

-- 2. 启用二进制日志进行增量备份
-- 在 my.cnf 中设置：
-- log-bin=mysql-bin
-- binlog-format=ROW

-- 3. 设置自动备份脚本
-- 建议每天凌晨执行备份任务

-- =============================================
-- 监控和维护
-- =============================================

-- 1. 监控数据库性能
-- SHOW PROCESSLIST;
-- SHOW STATUS LIKE 'Threads_connected';
-- SHOW STATUS LIKE 'Innodb_buffer_pool_hit_rate';

-- 2. 监控慢查询
-- SHOW VARIABLES LIKE 'slow_query_log';
-- SHOW VARIABLES LIKE 'long_query_time';

-- 3. 定期检查表碎片
-- SELECT 
--     TABLE_NAME,
--     ROUND(((DATA_LENGTH + INDEX_LENGTH) / 1024 / 1024), 2) AS 'Size (MB)',
--     ROUND((DATA_FREE / 1024 / 1024), 2) AS 'Free Space (MB)'
-- FROM information_schema.TABLES 
-- WHERE TABLE_SCHEMA = DATABASE()
-- ORDER BY (DATA_LENGTH + INDEX_LENGTH) DESC;

-- =============================================
-- 安全建议
-- =============================================

-- 1. 定期更改数据库密码
-- 2. 限制数据库用户权限
-- 3. 启用 SSL 连接
-- 4. 定期更新 MySQL 版本
-- 5. 监控异常登录和操作

-- =============================================
-- 初始化完成
-- =============================================

SELECT '数据库初始化完成！' as message;
SELECT NOW() as completion_time;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================
-- 使用说明
-- =============================================

/*
1. 执行顺序：
   - 先执行 00_main_ddl.sql（主脚本）
   - 或者按顺序单独执行各模块脚本

2. 环境要求：
   - MySQL 5.7 或更高版本
   - 支持 JSON 数据类型
   - 支持存储过程和函数

3. 字符集要求：
   - 数据库字符集：utf8mb4
   - 排序规则：utf8mb4_unicode_ci

4. 权限要求：
   - CREATE 权限（创建数据库和表）
   - ALTER 权限（修改表结构）
   - INDEX 权限（创建索引）
   - ROUTINE 权限（创建存储过程和函数）

5. 注意事项：
   - 执行前请备份现有数据
   - 生产环境请先在测试环境验证
   - 根据实际需求调整配置参数
*/
