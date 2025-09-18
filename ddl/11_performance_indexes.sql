-- =============================================
-- 性能优化索引脚本
-- 为手动分页查询创建覆盖索引
-- 兼容 MySQL 5.7
-- 创建时间: 2024-01-15
-- 作者: yibang
-- =============================================

-- 1. 用户表覆盖索引
-- 用于用户列表查询，包含常用筛选字段和显示字段
DROP INDEX IF EXISTS idx_user_list_cover ON users;
CREATE INDEX idx_user_list_cover ON users (
    user_level, 
    status, 
    created_at DESC, 
    id, 
    nickname, 
    phone, 
    available_balance, 
    total_tasks, 
    last_login_time
) COMMENT '用户列表查询覆盖索引';

-- 用户统计查询索引
DROP INDEX IF EXISTS idx_user_stats ON users;
CREATE INDEX idx_user_stats ON users (
    status, 
    user_level, 
    created_at
) COMMENT '用户统计查询索引';

-- 用户邀请关系索引
DROP INDEX IF EXISTS idx_user_invite ON users;
CREATE INDEX idx_user_invite ON users (
    invited_by, 
    created_at DESC
) COMMENT '用户邀请关系索引';

-- 2. 任务表覆盖索引
-- 用于任务列表查询
DROP INDEX IF EXISTS idx_task_list_cover ON tasks;
CREATE INDEX idx_task_list_cover ON tasks (
    type, 
    status, 
    created_at DESC, 
    id, 
    title, 
    reward_amount, 
    reward_level, 
    user_level
) COMMENT '任务列表查询覆盖索引';

-- 任务统计查询索引
DROP INDEX IF EXISTS idx_task_stats ON tasks;
CREATE INDEX idx_task_stats ON tasks (
    status, 
    type, 
    created_at
) COMMENT '任务统计查询索引';

-- 任务过期时间索引
DROP INDEX IF EXISTS idx_task_expire ON tasks;
CREATE INDEX idx_task_expire ON tasks (
    expire_time, 
    status
) COMMENT '任务过期时间索引';

-- 3. 提现表覆盖索引
-- 用于提现列表查询
DROP INDEX IF EXISTS idx_withdraw_list_cover ON withdrawals;
CREATE INDEX idx_withdraw_list_cover ON withdrawals (
    status, 
    created_at DESC, 
    id, 
    user_id, 
    amount, 
    wechat_transaction_id, 
    processed_at
) COMMENT '提现列表查询覆盖索引';

-- 提现用户查询索引
DROP INDEX IF EXISTS idx_withdraw_user ON withdrawals;
CREATE INDEX idx_withdraw_user ON withdrawals (
    user_id, 
    status, 
    created_at DESC
) COMMENT '提现用户查询索引';

-- 提现统计查询索引
DROP INDEX IF EXISTS idx_withdraw_stats ON withdrawals;
CREATE INDEX idx_withdraw_stats ON withdrawals (
    status, 
    created_at, 
    amount
) COMMENT '提现统计查询索引';

-- 4. 用户任务表索引
-- 用户任务完成情况查询
DROP INDEX IF EXISTS idx_user_task_user ON user_tasks;
CREATE INDEX idx_user_task_user ON user_tasks (
    user_id, 
    status, 
    completed_at DESC
) COMMENT '用户任务查询索引';

-- 任务完成统计索引
DROP INDEX IF EXISTS idx_user_task_stats ON user_tasks;
CREATE INDEX idx_user_task_stats ON user_tasks (
    task_id, 
    status, 
    completed_at
) COMMENT '任务完成统计索引';

-- 用户任务收益索引
DROP INDEX IF EXISTS idx_user_task_earnings ON user_tasks;
CREATE INDEX idx_user_task_earnings ON user_tasks (
    user_id, 
    status, 
    reward_amount, 
    verified_at
) COMMENT '用户任务收益索引';

-- 5. 收益记录表索引
-- 用户收益查询索引
DROP INDEX IF EXISTS idx_earnings_user ON earnings;
CREATE INDEX idx_earnings_user ON earnings (
    user_id, 
    type, 
    created_at DESC
) COMMENT '用户收益查询索引';

-- 收益统计索引
DROP INDEX IF EXISTS idx_earnings_stats ON earnings;
CREATE INDEX idx_earnings_stats ON earnings (
    type, 
    status, 
    created_at, 
    amount
) COMMENT '收益统计索引';

-- 6. 系统配置表索引
-- 配置分组查询索引
DROP INDEX IF EXISTS idx_config_group ON system_config;
CREATE INDEX idx_config_group ON system_config (
    config_group, 
    config_key
) COMMENT '系统配置分组索引';

-- 配置查询索引
DROP INDEX IF EXISTS idx_config_key ON system_config;
CREATE INDEX idx_config_key ON system_config (
    config_key, 
    is_active
) COMMENT '系统配置键索引';

-- 7. 通知表索引
-- 用户通知查询索引
DROP INDEX IF EXISTS idx_notification_user ON notifications;
CREATE INDEX idx_notification_user ON notifications (
    user_id, 
    status, 
    created_at DESC
) COMMENT '用户通知查询索引';

-- 通知类型索引
DROP INDEX IF EXISTS idx_notification_type ON notifications;
CREATE INDEX idx_notification_type ON notifications (
    type, 
    status, 
    created_at DESC
) COMMENT '通知类型索引';

-- 8. 订单表索引（如果需要）
-- 用户订单查询索引
DROP INDEX IF EXISTS idx_order_user ON orders;
CREATE INDEX idx_order_user ON orders (
    user_id, 
    order_status, 
    created_at DESC
) COMMENT '用户订单查询索引';

-- 订单状态统计索引
DROP INDEX IF EXISTS idx_order_stats ON orders;
CREATE INDEX idx_order_stats ON orders (
    order_status, 
    created_at, 
    total_amount
) COMMENT '订单统计索引';

-- =============================================
-- 索引使用建议
-- =============================================

/*
1. 覆盖索引使用场景：
   - 查询的所有字段都包含在索引中
   - 避免回表查询，提高查询性能
   - 适用于列表查询和统计查询

2. 索引维护：
   - 定期执行 ANALYZE TABLE 更新索引统计信息
   - 监控索引使用情况，删除不必要的索引
   - 根据查询模式调整索引结构

3. 性能监控：
   - 使用 EXPLAIN 分析查询执行计划
   - 监控慢查询日志
   - 定期检查索引碎片

4. 注意事项：
   - 索引会增加写入成本
   - 过多索引会影响插入和更新性能
   - 根据实际查询需求调整索引
*/

-- 查看索引创建结果
SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX,
    INDEX_TYPE,
    INDEX_COMMENT
FROM information_schema.STATISTICS 
WHERE TABLE_SCHEMA = DATABASE()
AND INDEX_NAME LIKE 'idx_%'
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- 分析表统计信息
ANALYZE TABLE users, tasks, user_tasks, withdrawals, earnings, system_config, notifications, orders;
