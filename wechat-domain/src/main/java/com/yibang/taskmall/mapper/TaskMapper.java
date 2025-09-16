package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 任务Mapper
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    
    /**
     * 根据用户等级和任务状态查询可用任务
     */
    @Select("SELECT * FROM tasks WHERE status = 'active' " +
            "AND (user_level = #{userLevel} OR user_level = 'all') " +
            "AND (expire_time IS NULL OR expire_time > NOW()) " +
            "AND (max_claim_count = -1 OR current_claim_count < max_claim_count) " +
            "ORDER BY reward_amount DESC, created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Task> findAvailableTasksForUser(@Param("userLevel") String userLevel, 
                                        @Param("offset") int offset, 
                                        @Param("limit") int limit);
    
    /**
     * 根据任务类型和佣金等级查询任务
     */
    @Select("SELECT * FROM tasks WHERE status = 'active' " +
            "AND (#{taskType} IS NULL OR type = #{taskType}) " +
            "AND (#{rewardLevel} IS NULL OR reward_level = #{rewardLevel}) " +
            "AND (expire_time IS NULL OR expire_time > NOW()) " +
            "ORDER BY reward_amount DESC, created_at DESC " +
            "LIMIT #{offset}, #{limit}")
    List<Task> findTasksByTypeAndRewardLevel(@Param("taskType") String taskType,
                                           @Param("rewardLevel") String rewardLevel,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);
    
    /**
     * 统计任务总数
     */
    @Select("SELECT COUNT(*) FROM tasks WHERE status = 'active' " +
            "AND (#{taskType} IS NULL OR type = #{taskType}) " +
            "AND (#{rewardLevel} IS NULL OR reward_level = #{rewardLevel}) " +
            "AND (expire_time IS NULL OR expire_time > NOW())")
    Long countTasksByTypeAndRewardLevel(@Param("taskType") String taskType,
                                       @Param("rewardLevel") String rewardLevel);
}