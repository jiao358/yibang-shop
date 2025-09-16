package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.request.TaskClaimRequest;
import com.yibang.taskmall.dto.request.TaskCompleteRequest;
import com.yibang.taskmall.dto.request.TaskQueryRequest;
import com.yibang.taskmall.dto.response.TaskDTO;
import com.yibang.taskmall.dto.response.UserTaskDTO;
import com.yibang.taskmall.dto.response.UserTaskStatsDTO;
import com.yibang.taskmall.entity.Task;
import com.yibang.taskmall.entity.UserTask;

import java.util.List;

/**
 * 任务服务接口
 * 提供原子化的业务方法，每个方法都有明确的业务含义
 */
public interface TaskService {
    
    /**
     * 查询可用任务列表
     * 根据用户等级、任务类型、佣金等级进行筛选
     * 
     * @param queryRequest 查询请求参数
     * @return 任务列表和分页信息
     */
    List<TaskDTO> getAvailableTasks(TaskQueryRequest queryRequest);
    
    /**
     * 统计可用任务总数
     * 用于分页计算
     * 
     * @param queryRequest 查询请求参数
     * @return 任务总数
     */
    Long countAvailableTasks(TaskQueryRequest queryRequest);
    
    /**
     * 获取任务详情
     * 包含任务的完整信息和当前用户的参与状态
     * 
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 任务详情
     */
    TaskDTO getTaskDetail(Long taskId, Long userId);
    
    /**
     * 检查任务可领取性
     * 验证用户等级、任务状态、已领取次数等条件
     * 
     * @param taskId 任务ID
     * @param userId 用户ID
     * @return 是否可领取
     */
    boolean canClaimTask(Long taskId, Long userId);
    
    /**
     * 领取任务
     * 原子操作：检查条件 -> 创建用户任务记录 -> 更新任务统计
     * 
     * @param claimRequest 领取请求
     * @return 用户任务记录
     */
    UserTaskDTO claimTask(TaskClaimRequest claimRequest);
    
    /**
     * 提交任务完成凭证
     * 用户上传完成截图或其他凭证数据
     * 
     * @param completeRequest 完成请求
     * @return 更新后的用户任务记录
     */
    UserTaskDTO submitTaskCompletion(TaskCompleteRequest completeRequest);
    
    /**
     * 验证任务完成
     * 系统或管理员验证用户提交的凭证
     * 
     * @param userTaskId 用户任务ID
     * @param isApproved 是否通过验证
     * @param failureReason 失败原因（如果不通过）
     * @return 更新后的用户任务记录
     */
    UserTaskDTO verifyTaskCompletion(Long userTaskId, boolean isApproved, String failureReason);
    
    /**
     * 发放任务奖励
     * 将奖励金额添加到用户账户余额
     * 
     * @param userTaskId 用户任务ID
     * @return 是否发放成功
     */
    boolean distributeTaskReward(Long userTaskId);
    
    /**
     * 查询用户任务列表
     * 根据状态筛选用户的任务记录
     * 
     * @param userId 用户ID
     * @param status 任务状态（可选）
     * @param page 页码
     * @param size 页大小
     * @return 用户任务列表
     */
    List<UserTaskDTO> getUserTasks(Long userId, String status, int page, int size);
    
    /**
     * 统计用户任务数量
     * 
     * @param userId 用户ID
     * @param status 任务状态（可选）
     * @return 任务数量
     */
    Long countUserTasks(Long userId, String status);
    
    /**
     * 获取用户任务统计
     * 包含完成任务数、总收益等信息
     * 
     * @param userId 用户ID
     * @return 任务统计信息
     */
    UserTaskStatsDTO getUserTaskStats(Long userId);
    
    /**
     * 处理任务过期
     * 定时任务调用，将过期的用户任务标记为expired
     * 
     * @return 处理的过期任务数量
     */
    int handleExpiredTasks();
    
    /**
     * 获取推荐任务
     * 根据用户等级和历史行为推荐合适的任务
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐任务列表
     */
    List<TaskDTO> getRecommendedTasks(Long userId, int limit);
}