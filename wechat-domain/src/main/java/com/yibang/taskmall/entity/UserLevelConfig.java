package com.yibang.taskmall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户等级配置实体类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_level_config")
public class UserLevelConfig {

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 等级名称
     */
    @TableField("level_name")
    private String levelName;

    /**
     * 等级代码：normal-普通用户, signed-签约用户, vip-VIP用户
     */
    @TableField("level_code")
    private String levelCode;

    /**
     * 最低收益要求（分）
     */
    @TableField("min_earnings")
    private Integer minEarnings;

    /**
     * 每日最大提现金额（分），0表示无限制
     */
    @TableField("max_daily_withdrawal")
    private Integer maxDailyWithdrawal;

    /**
     * 提现手续费率
     */
    @TableField("withdrawal_fee_rate")
    private BigDecimal withdrawalFeeRate;

    /**
     * 任务奖励加成率
     */
    @TableField("task_bonus_rate")
    private BigDecimal taskBonusRate;

    /**
     * 创建时间
     */
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
