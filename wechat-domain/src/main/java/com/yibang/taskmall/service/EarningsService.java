package com.yibang.taskmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.response.EarningsStatsResponse;
import com.yibang.taskmall.dto.response.EarningsRecordResponse;

/**
 * 收益服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface EarningsService {
    
    /**
     * 获取收益统计
     * 
     * @param userId 用户ID
     * @param period 统计周期
     * @param year 年份
     * @param month 月份
     * @return 收益统计
     */
    EarningsStatsResponse getEarningsStats(Long userId, String period, Integer year, Integer month);
    
    /**
     * 获取收益记录
     * 
     * @param userId 用户ID
     * @param type 收益类型
     * @param page 页码
     * @param size 每页大小
     * @return 收益记录
     */
    Page<EarningsRecordResponse> getEarningsRecords(Long userId, String type, Integer page, Integer size);
    
    /**
     * 获取收益来源统计
     * 
     * @param userId 用户ID
     * @return 收益来源统计
     */
    Object getEarningsSources(Long userId);
    
    /**
     * 获取收益趋势
     * 
     * @param userId 用户ID
     * @param range 时间范围
     * @return 收益趋势
     */
    Object getEarningsTrend(Long userId, String range);
    
    /**
     * 获取用户余额
     * 
     * @param userId 用户ID
     * @return 用户余额
     */
    Object getUserBalance(Long userId);
}
