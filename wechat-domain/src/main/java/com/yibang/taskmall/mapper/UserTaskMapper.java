package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户任务Mapper
 */
@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {
    
    /**
     * 查询用户的任务列表（关联任务信息）
     */
    @Select("SELECT ut.*, t.title, t.description, t.image_url, t.type, t.reward_amount " +
            "FROM user_tasks ut " +
            "LEFT JOIN tasks t ON ut.task_id = t.id " +
            "WHERE ut.user_id = #{userId} " +
            "AND (#{status} IS NULL OR ut.status = #{status}) " +
            "ORDER BY ut.claimed_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<UserTask> findUserTasksWithDetails(@Param("userId") Long userId,
                                          @Param("status") String status,
                                          @Param("offset") int offset,
                                          @Param("limit") int limit);
    
    /**
     * 统计用户任务数量
     */
    @Select("SELECT COUNT(*) FROM user_tasks " +
            "WHERE user_id = #{userId} " +
            "AND (#{status} IS NULL OR status = #{status})")
    Long countUserTasks(@Param("userId") Long userId, @Param("status") String status);
    
    /**
     * 检查用户是否已领取某个任务
     */
    @Select("SELECT COUNT(*) FROM user_tasks " +
            "WHERE user_id = #{userId} AND task_id = #{taskId} " +
            "AND status IN ('claimed', 'completed', 'verified')")
    Long checkTaskClaimed(@Param("userId") Long userId, @Param("taskId") Long taskId);
    
    /**
     * 统计用户已完成任务数量
     */
    @Select("SELECT COUNT(*) FROM user_tasks " +
            "WHERE user_id = #{userId} AND status = 'verified'")
    Long countCompletedTasks(@Param("userId") Long userId);
    
    /**
     * 计算用户任务总收益
     */
    @Select("SELECT COALESCE(SUM(reward_amount), 0) FROM user_tasks " +
            "WHERE user_id = #{userId} AND status = 'verified'")
    Long calculateTotalEarnings(@Param("userId") Long userId);
}