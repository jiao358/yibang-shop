# 字典与轮播图功能系统设计

创建于：2025-09-18 04:30  作者：estela  分支：main

## 目标
- 覆盖：任务类型、用户等级、佣金等级、提现状态、订单状态、通知类型、隐私协议/用户须知（多语言），轮播图。
- 约束：ENUM→VARCHAR，JSON→TEXT；`type/user_level` 使用字典值但无外键。
- 缓存：Redis `wx:dict:{group}`，TTL=1小时；提供按组清理。
- 接口：小程序公开（无需JWT）与后台管理（bk）。
- 兜底：后端Java内置默认数据。

## DDL 摘要
- dict_items(id, group_code, item_code, label_zh, label_en, enabled, sort, remark, created_at, updated_at)
- banners(id, title_zh, title_en, image_url, enable_jump, jump_target, enabled, sort, start_time, end_time, client)

## 缓存
- 读取：Redis → DB → 兜底。
- 键：`wx:dict:{group}`；轮播：`wx:dict:banners:{client}`；TTL=1小时。

## 接口
- 公开：
  - GET /api/public/dicts/{group}
  - GET /api/public/banners
- 后台：
  - /api/bk/dicts CRUD，POST /api/bk/dicts/{group}/cache/evict
  - /api/bk/banners CRUD

## 进度
- 2025-09-18: schema.sql ENUM/JSON→VARCHAR/TEXT 完成
- 待办：新增DDL；实现公开/后台接口；前端接入；清缓存接口
