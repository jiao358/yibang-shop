package com.yibang.taskmall.bkservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.TaskCreateRequest;
import com.yibang.taskmall.bkdto.request.TaskQueryRequest;
import com.yibang.taskmall.bkdto.request.TaskUpdateRequest;
import com.yibang.taskmall.bkdto.response.TaskDetailResponse;
import com.yibang.taskmall.bkdto.response.TaskListResponse;
import com.yibang.taskmall.bkdto.response.TaskStatsResponse;

import java.util.List;
import java.util.Map;

public interface TaskBkService {

    Page<TaskListResponse> getTaskList(TaskQueryRequest request, Integer page, Integer size);

    TaskDetailResponse getTaskDetail(Long taskId);

    Long createTask(TaskCreateRequest request);

    void updateTask(Long taskId, TaskUpdateRequest request);

    void deleteTask(Long taskId);

    void batchOperation(List<Long> taskIds, String operation);

    TaskStatsResponse getTaskStats(String type, String startDate, String endDate);

    Map<String, String> getTaskTypes();
}


