package com.yibang.taskmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.request.ClaimTaskRequest;
import com.yibang.taskmall.dto.request.CompleteTaskRequest;
import com.yibang.taskmall.dto.response.TaskResponse;
import com.yibang.taskmall.dto.response.TaskDetailResponse;

/**
 * 任务服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface TaskService {
    
    /**
     * 获取任务列表
     * 
     * @param type 任务类型
     * @param userLevel 用户等级
     * @param page 页码
     * @param size 每页大小
     * @return 任务列表
     */
    Page<TaskResponse> getTasks(String type, String userLevel, Integer page, Integer size);
    
    /**
     * 获取任务详情
     * 
     * @param taskId 任务ID
     * @return 任务详情
     */
    TaskDetailResponse getTaskDetail(Long taskId);
    
    /**
     * 领取任务
     * 
     * @param request 领取任务请求
     * @return 领取结果
     */
    Boolean claimTask(ClaimTaskRequest request);
    
    /**
     * 完成任务
     * 
     * @param userTaskId 用户任务ID
     * @param request 完成任务请求
     * @return 完成结果
     */
    Boolean completeTask(Long userTaskId, CompleteTaskRequest request);
    
    /**
     * 获取用户任务列表
     * 
     * @param userId 用户ID
     * @param status 任务状态
     * @param page 页码
     * @param size 每页大小
     * @return 用户任务列表
     */
    Page<Object> getUserTasks(Long userId, String status, Integer page, Integer size);
    
    /**
     * 放弃任务
     * 
     * @param userTaskId 用户任务ID
     * @return 放弃结果
     */
    Boolean abandonTask(Long userTaskId);
}
