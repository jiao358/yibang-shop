package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务Mapper接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 根据用户等级获取可用任务列表
     * 
     * @param userLevel 用户等级
     * @param status 任务状态
     * @return 任务列表
     */
    List<Task> selectAvailableTasksByUserLevel(@Param("userLevel") String userLevel, @Param("status") String status);

    /**
     * 更新任务领取次数
     * 
     * @param taskId 任务ID
     * @return 影响行数
     */
    int updateClaimCount(@Param("taskId") Long taskId);

    /**
     * 检查任务是否可领取
     * 
     * @param taskId 任务ID
     * @return 任务信息
     */
    Task selectTaskForClaim(@Param("taskId") Long taskId);
}
