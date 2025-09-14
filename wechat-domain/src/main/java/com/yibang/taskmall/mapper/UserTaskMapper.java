package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户任务Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {

    /**
     * 根据用户ID和状态获取用户任务列表
     * 
     * @param userId 用户ID
     * @param status 任务状态
     * @return 用户任务列表
     */
    List<UserTask> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    /**
     * 检查用户是否已领取指定任务
     * 
     * @param userId 用户ID
     * @param taskId 任务ID
     * @return 用户任务信息
     */
    UserTask selectByUserIdAndTaskId(@Param("userId") Long userId, @Param("taskId") Long taskId);

    /**
     * 获取用户今日任务完成数量
     * 
     * @param userId 用户ID
     * @return 完成数量
     */
    int selectTodayCompletedCount(@Param("userId") Long userId);

    /**
     * 获取用户总收益金额
     * 
     * @param userId 用户ID
     * @return 总收益金额（分）
     */
    Integer selectTotalEarningsByUserId(@Param("userId") Long userId);
}
