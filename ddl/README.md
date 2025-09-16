# 数据库DDL文件说明

## 概述

本目录包含任务商城系统的数据库初始化脚本，所有脚本兼容 MySQL 5.7 及以上版本。

## 文件结构

```
ddl/
├── 00_main_ddl.sql          # 主DDL文件，包含所有模块
├── 01_task_module.sql       # 任务模块
├── 02_user_module.sql       # 用户模块
├── 03_earnings_module.sql   # 收益模块
├── 04_withdrawal_module.sql # 提现模块
├── 05_mall_module.sql       # 商城模块
├── 06_notification_module.sql # 通知模块
├── 07_system_config.sql     # 系统配置模块
├── 08_address_module.sql    # 收货地址模块
└── README.md               # 本说明文件
```

## 核心模块

### 任务模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `tasks` | 任务表 | title, type, reward_amount, user_level, status |
| `user_tasks` | 用户任务表 | user_id, task_id, status, reward_amount |

### 用户模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user_level_config` | 用户等级配置表 | level_name, level_code, min_earnings |
| `users` | 用户信息表 | openid, nickname, user_level, total_earnings |

### 收益模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `earnings` | 收益记录表 | user_id, amount, type, status |

### 提现模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `withdrawals` | 提现记录表 | user_id, amount, status, withdrawal_type |

### 商城模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `products` | 商品表 | name, price, images, category_id, status |
| `product_categories` | 商品分类表 | name, parent_id, icon, sort_order |
| `carts` | 购物车表 | user_id, product_id, quantity |
| `orders` | 订单表 | order_no, user_id, total_amount, order_status |
| `order_items` | 订单商品表 | order_id, product_id, quantity, total_amount |

### 通知模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `notifications` | 通知表 | user_id, title, content, type, status |

### 系统配置模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `system_config` | 系统配置表 | config_key, config_value, config_type |

### 收货地址模块

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user_addresses` | 用户收货地址表 | user_id, name, phone, full_address, is_default |

## 重要特性

### 1. 数据完整性
- 所有表都包含必要的索引和外键约束
- 使用 `utf8mb4` 字符集支持完整的 Unicode 字符
- 所有时间字段都使用 `timestamp` 类型

### 2. 兼容性
- 兼容 MySQL 5.7 及以上版本
- 避免使用 `enum` 字段，改用 `varchar` 提高灵活性
- 使用 `json` 字段存储复杂数据结构

### 3. 性能优化
- 为常用查询字段创建索引
- 合理使用外键约束保证数据一致性
- 避免过度设计，保持表结构简洁

## 使用说明

### 1. 执行所有DDL
```sql
SOURCE 00_main_ddl.sql;
```

### 2. 单独执行某个模块
```sql
SOURCE 01_task_module.sql;
SOURCE 02_user_module.sql;
-- ... 其他模块
```

### 3. 创建数据库
```sql
CREATE DATABASE yibang_taskmall CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE yibang_taskmall;
SOURCE 00_main_ddl.sql;
```

## 注意事项

1. **字符集**: 所有表都使用 `utf8mb4` 字符集
2. **字段类型**: 避免使用 `enum` 字段，改用 `varchar` 提高灵活性
3. **索引**: 为常用查询字段创建了合适的索引
4. **外键**: 使用外键约束保证数据一致性
5. **简化设计**: 避免过度设计，只保留核心业务表

## 版本信息

- **版本**: v1.0
- **创建日期**: 2024-01-15
- **最后更新**: 2024-01-15
- **兼容性**: MySQL 5.7+