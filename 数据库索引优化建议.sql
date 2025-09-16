-- =============================================
-- 数据库索引优化建议
-- 针对后台管理系统分页查询性能优化
-- =============================================

-- 1. 任务表 (tasks) 索引优化
-- 原有索引：idx_type_status, idx_user_level, idx_expire_time, idx_created_at

-- 建议新增覆盖索引（包含查询和排序字段）
ALTER TABLE `tasks` ADD INDEX `idx_type_status_created_at_cover` (`type`, `status`, `created_at` DESC, `id`, `title`, `reward_amount`, `reward_level`);

-- 针对后台列表查询的复合索引
ALTER TABLE `tasks` ADD INDEX `idx_bk_list_query` (`status`, `type`, `created_at` DESC);

-- 针对统计查询的索引
ALTER TABLE `tasks` ADD INDEX `idx_stats_type_status` (`type`, `status`, `created_at`);

-- 2. 用户表 (users) 索引优化
-- 原有索引：uk_openid, uk_invite_code, idx_user_level, idx_status, idx_created_at

-- 建议新增覆盖索引
ALTER TABLE `users` ADD INDEX `idx_level_status_created_at_cover` (`user_level`, `status`, `created_at` DESC, `id`, `nickname`, `phone`, `available_balance`, `total_tasks`);

-- 针对后台用户搜索的索引
ALTER TABLE `users` ADD INDEX `idx_nickname_phone` (`nickname`, `phone`);

-- 针对余额排序的索引
ALTER TABLE `users` ADD INDEX `idx_balance_desc` (`available_balance` DESC, `created_at` DESC);

-- 3. 提现表 (withdrawals) 索引优化
-- 原有索引：idx_user_id, idx_status, idx_created_at

-- 建议新增覆盖索引
ALTER TABLE `withdrawals` ADD INDEX `idx_status_created_at_cover` (`status`, `created_at` DESC, `id`, `user_id`, `amount`, `completed_at`);

-- 针对用户提现历史查询
ALTER TABLE `withdrawals` ADD INDEX `idx_user_status_time` (`user_id`, `status`, `created_at` DESC);

-- 针对审核流程的索引
ALTER TABLE `withdrawals` ADD INDEX `idx_pending_first` (`status`, `created_at` ASC) WHERE `status` = 'pending';

-- 4. 用户任务表 (user_tasks) 索引优化
-- 原有索引：idx_user_status, idx_task_status, idx_claimed_at, idx_user_id

-- 建议新增统计查询索引
ALTER TABLE `user_tasks` ADD INDEX `idx_stats_status_time` (`status`, `completed_at`, `verified_at`);

-- 针对用户任务完成情况查询
ALTER TABLE `user_tasks` ADD INDEX `idx_user_completed_time` (`user_id`, `status`, `completed_at` DESC);

-- 5. 商品表 (products) 索引优化（如果有商城管理需求）
-- 建议新增复合索引
ALTER TABLE `products` ADD INDEX `idx_category_status_sort` (`category_id`, `status`, `sort_order`, `created_at` DESC);

-- 针对商品搜索的全文索引
ALTER TABLE `products` ADD FULLTEXT INDEX `idx_fulltext_search` (`name`, `description`);

-- 6. 订单表 (orders) 索引优化（如果存在）
-- 假设订单表结构，建议索引
-- ALTER TABLE `orders` ADD INDEX `idx_user_status_time` (`user_id`, `status`, `created_at` DESC);
-- ALTER TABLE `orders` ADD INDEX `idx_status_time_amount` (`status`, `created_at` DESC, `total_amount`);

-- 7. 系统配置表 (system_configs) 索引优化
ALTER TABLE `system_configs` ADD INDEX `idx_group_key` (`config_group`, `config_key`);

-- 8. 通知表 (notifications) 索引优化
ALTER TABLE `notifications` ADD INDEX `idx_type_status_time` (`type`, `status`, `created_at` DESC);

-- =============================================
-- 查询优化建议
-- =============================================

-- 1. 避免SELECT * 查询
-- 错误示例：SELECT * FROM tasks WHERE status = 'active'
-- 正确示例：SELECT id, title, type, reward_amount, status, created_at FROM tasks WHERE status = 'active'

-- 2. 利用覆盖索引
-- 示例：上述 idx_type_status_created_at_cover 索引包含了列表查询的所有字段
-- 查询时无需回表，直接从索引获取数据

-- 3. 合理使用LIMIT
-- 大分页查询优化：使用游标分页替代OFFSET
-- 错误示例：SELECT * FROM tasks ORDER BY id LIMIT 10000, 10
-- 正确示例：SELECT * FROM tasks WHERE id > 10000 ORDER BY id LIMIT 10

-- 4. 统计查询优化
-- 使用预聚合表或定时任务计算统计数据
-- 避免实时COUNT(*)大表查询

-- 5. 时间范围查询优化
-- 使用合适的时间字段类型和索引
-- 避免函数运算：WHERE DATE(created_at) = '2024-01-15'
-- 推荐范围查询：WHERE created_at >= '2024-01-15 00:00:00' AND created_at < '2024-01-16 00:00:00'

-- =============================================
-- 缓存策略建议
-- =============================================

-- 1. 缓存层级设计
-- L1: 热点数据 (1-5分钟) - 列表查询、实时统计
-- L2: 温数据 (10-30分钟) - 详情数据、配置信息  
-- L3: 冷数据 (1-2小时) - 选项数据、元数据

-- 2. 缓存键命名规范
-- 格式：{模块}.{资源}:{场景}:{参数标识}
-- 示例：bk.tasks:list:type=ad&status=active:1:10

-- 3. 缓存更新策略
-- 写操作后立即删除相关缓存
-- 批量操作后批量清除缓存
-- 定时刷新核心业务缓存

-- 4. 缓存穿透保护
-- 空结果缓存1分钟
-- 使用布隆过滤器预判存在性
-- 接口限流防止恶意查询

-- =============================================
-- 监控和维护
-- =============================================

-- 1. 慢查询监控
-- 开启 MySQL 慢查询日志
-- 监控执行时间超过 1 秒的查询
-- 定期分析和优化慢查询

-- 2. 索引使用率监控
-- 定期检查索引使用情况
-- 删除无用索引，减少写入开销
-- 监控索引碎片率

-- 3. 缓存命中率监控
-- 监控各模块缓存命中率
-- 目标：列表查询 > 80%，详情查询 > 90%
-- 调整缓存TTL和策略

-- 4. 数据库连接池优化
-- 合理设置连接池大小
-- 监控连接池使用率
-- 避免连接泄漏

-- =============================================
-- 部署注意事项
-- =============================================

-- 1. 索引创建顺序
-- 先在从库创建索引
-- 确认性能提升后在主库创建
-- 避免高峰期创建大索引

-- 2. 缓存预热
-- 系统启动后预热核心缓存
-- 避免冷启动时缓存雪崩
-- 分批预热，避免Redis压力过大

-- 3. 数据一致性
-- 缓存更新使用延迟双删策略
-- 重要数据变更后强制刷新缓存
-- 定期校验缓存数据准确性
