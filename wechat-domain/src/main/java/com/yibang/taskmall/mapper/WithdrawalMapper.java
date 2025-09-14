package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.Withdrawal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提现记录Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface WithdrawalMapper extends BaseMapper<Withdrawal> {

    /**
     * 根据用户ID获取提现记录列表
     * 
     * @param userId 用户ID
     * @param status 提现状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 提现记录列表
     */
    List<Withdrawal> selectByUserIdAndConditions(@Param("userId") Long userId,
                                                 @Param("status") String status,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户今日提现金额
     * 
     * @param userId 用户ID
     * @return 今日提现金额（分）
     */
    Integer selectTodayWithdrawalAmountByUserId(@Param("userId") Long userId);

    /**
     * 获取用户总提现金额
     * 
     * @param userId 用户ID
     * @return 总提现金额（分）
     */
    Integer selectTotalWithdrawalAmountByUserId(@Param("userId") Long userId);

    /**
     * 根据微信交易单号查询提现记录
     * 
     * @param wechatTransactionId 微信交易单号
     * @return 提现记录
     */
    Withdrawal selectByWechatTransactionId(@Param("wechatTransactionId") String wechatTransactionId);
}
