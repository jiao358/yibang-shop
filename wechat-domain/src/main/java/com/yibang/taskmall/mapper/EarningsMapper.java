package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.Earnings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收益记录Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface EarningsMapper extends BaseMapper<Earnings> {

    /**
     * 根据用户ID获取收益记录列表
     * 
     * @param userId 用户ID
     * @param type 收益类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 收益记录列表
     */
    List<Earnings> selectByUserIdAndConditions(@Param("userId") Long userId, 
                                               @Param("type") String type,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户总收益金额
     * 
     * @param userId 用户ID
     * @return 总收益金额（分）
     */
    Integer selectTotalEarningsByUserId(@Param("userId") Long userId);

    /**
     * 获取用户今日收益金额
     * 
     * @param userId 用户ID
     * @return 今日收益金额（分）
     */
    Integer selectTodayEarningsByUserId(@Param("userId") Long userId);

    /**
     * 获取用户可提现余额
     * 
     * @param userId 用户ID
     * @return 可提现余额（分）
     */
    Integer selectAvailableBalanceByUserId(@Param("userId") Long userId);

    /**
     * 获取用户收益统计信息
     * 
     * @param userId 用户ID
     * @return 收益统计信息
     */
    List<Earnings> selectEarningsStatsByUserId(@Param("userId") Long userId);
}
