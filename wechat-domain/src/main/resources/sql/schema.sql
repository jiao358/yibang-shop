-- 懿邦任务商城数据库表结构
-- 创建时间: 2024-01-15

-- 任务表
CREATE TABLE tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    type VARCHAR(32) NOT NULL COMMENT '任务类型: ad/video/app_install/survey/share',
    reward_amount INT NOT NULL DEFAULT 0 COMMENT '奖励金额（分）',
    requirements TEXT COMMENT '任务要求配置(JSON文本)',
    user_level VARCHAR(32) DEFAULT 'all' COMMENT '适用用户等级: normal/signed/vip/all',
    status VARCHAR(32) DEFAULT 'active' COMMENT '任务状态: active/inactive/completed',
    expire_time TIMESTAMP NULL COMMENT '任务过期时间',
    max_claim_count INT DEFAULT -1 COMMENT '最大领取次数，-1表示无限制',
    current_claim_count INT DEFAULT 0 COMMENT '当前已领取次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_type_status (type, status),
    INDEX idx_user_level (user_level),
    INDEX idx_expire_time (expire_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 用户任务表
CREATE TABLE user_tasks (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户任务ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（来自ERP系统）',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    status VARCHAR(32) DEFAULT 'claimed' COMMENT '任务状态: claimed/completed/verified/expired/failed',
    claimed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    verified_at TIMESTAMP NULL COMMENT '验证时间',
    proof_data TEXT COMMENT '完成凭证数据(JSON文本)',
    reward_amount INT COMMENT '实际奖励金额（分）',
    failure_reason VARCHAR(500) COMMENT '失败原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_task_status (task_id, status),
    INDEX idx_claimed_at (claimed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户任务表';

-- 收益记录表
CREATE TABLE earnings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收益ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（来自ERP系统）',
    user_task_id BIGINT NULL COMMENT '关联的用户任务ID',
    amount INT NOT NULL COMMENT '收益金额（分）',
    type VARCHAR(32) NOT NULL COMMENT '收益类型: task_reward/withdrawal/refund/bonus',
    status VARCHAR(32) DEFAULT 'pending' COMMENT '收益状态: pending/completed/failed/cancelled',
    description VARCHAR(500) COMMENT '收益描述',
    source_task_title VARCHAR(200) COMMENT '来源任务标题',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    processed_at TIMESTAMP NULL COMMENT '处理完成时间',
    FOREIGN KEY (user_task_id) REFERENCES user_tasks(id),
    INDEX idx_user_type (user_id, type),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收益记录表';

-- 提现记录表
CREATE TABLE withdrawals (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '提现ID',
    user_id BIGINT NOT NULL COMMENT '用户ID（来自ERP系统）',
    amount INT NOT NULL COMMENT '提现金额（分）',
    status VARCHAR(32) DEFAULT 'pending' COMMENT '提现状态: pending/processing/completed/failed/cancelled',
    wechat_transaction_id VARCHAR(100) COMMENT '微信交易单号',
    wechat_payment_no VARCHAR(100) COMMENT '微信支付单号',
    failure_reason VARCHAR(500) COMMENT '失败原因',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    processed_at TIMESTAMP NULL COMMENT '处理完成时间',
    completed_at TIMESTAMP NULL COMMENT '完成时间',
    INDEX idx_user_status (user_id, status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提现记录表';

-- 用户等级配置表
CREATE TABLE user_level_config (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '配置ID',
    level_name VARCHAR(50) NOT NULL COMMENT '等级名称',
    level_code VARCHAR(32) NOT NULL COMMENT '等级代码: normal/signed/vip',
    min_earnings INT DEFAULT 0 COMMENT '最低收益要求（分）',
    max_daily_withdrawal INT DEFAULT 0 COMMENT '每日最大提现金额（分），0表示无限制',
    withdrawal_fee_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '提现手续费率',
    task_bonus_rate DECIMAL(5,4) DEFAULT 0.0000 COMMENT '任务奖励加成率',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_level_code (level_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户等级配置表';

-- 任务完成统计表
CREATE TABLE task_completion_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    completion_count INT DEFAULT 0 COMMENT '完成次数',
    total_reward_amount BIGINT DEFAULT 0 COMMENT '总奖励金额（分）',
    last_completion_at TIMESTAMP NULL COMMENT '最后完成时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (task_id) REFERENCES tasks(id),
    UNIQUE KEY uk_task_id (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务完成统计表';

-- 插入默认用户等级配置
INSERT INTO user_level_config (level_name, level_code, min_earnings, max_daily_withdrawal, withdrawal_fee_rate, task_bonus_rate) VALUES
('普通用户', 'normal', 0, 10000, 0.0060, 0.0000),
('签约用户', 'signed', 100000, 50000, 0.0030, 0.1000),
('VIP用户', 'vip', 500000, 100000, 0.0000, 0.2000);

-- 插入示例任务数据
INSERT INTO tasks (title, description, type, reward_amount, requirements, user_level, status, max_claim_count) VALUES
('观看广告视频', '观看完整的广告视频，了解产品信息', 'ad', 550, '{"duration": 30, "questions": 3}', 'all', 'active', 1000),
('安装推荐应用', '下载并安装指定的应用程序', 'app_install', 1200, '{"min_usage_time": 300}', 'signed', 'active', 500),
('填写问卷调查', '完成产品相关的问卷调查', 'survey', 800, '{"min_answers": 10}', 'all', 'active', 2000),
('分享商品链接', '分享指定商品到社交媒体', 'share', 300, '{"platforms": ["wechat", "weibo"]}', 'all', 'active', 5000);
