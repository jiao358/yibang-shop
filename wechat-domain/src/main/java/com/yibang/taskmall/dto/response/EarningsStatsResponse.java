package com.yibang.taskmall.dto.response;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 收益统计响应DTO
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Data
public class EarningsStatsResponse {
    
    /**
     * 总收益（分）
     */
    private Integer totalEarnings;
    
    /**
     * 今日收益（分）
     */
    private Integer todayEarnings;
    
    /**
     * 可提现余额（分）
     */
    private Integer availableBalance;
    
    /**
     * 已提现金额（分）
     */
    private Integer withdrawnAmount;
    
    /**
     * 冻结金额（分）
     */
    private Integer frozenAmount;
    
    /**
     * 本月收益（分）
     */
    private Integer monthlyEarnings;
    
    /**
     * 上月收益（分）
     */
    private Integer lastMonthEarnings;
    
    /**
     * 收益增长率
     */
    private BigDecimal growthRate;
}
