package com.yibang.taskmall.bkservice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.bkdto.request.TaskCreateRequest;
import com.yibang.taskmall.bkdto.request.TaskQueryRequest;
import com.yibang.taskmall.bkdto.request.TaskUpdateRequest;
import com.yibang.taskmall.bkdto.response.TaskDetailResponse;
import com.yibang.taskmall.bkdto.response.TaskListResponse;
import com.yibang.taskmall.bkdto.response.TaskStatsResponse;
import com.yibang.taskmall.bkservice.TaskBkService;
import com.yibang.taskmall.entity.Task;
import com.yibang.taskmall.entity.UserTask;
import com.yibang.taskmall.mapper.TaskMapper;
import com.yibang.taskmall.mapper.UserTaskMapper;
import com.yibang.taskmall.utils.RedisCacheUtils;
import com.yibang.taskmall.utils.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskBkServiceImpl implements TaskBkService {

    private final TaskMapper taskMapper;
    private final UserTaskMapper userTaskMapper;
    private final RedisCacheUtils redisCacheUtils;
    private final ObjectMapper objectMapper;

    private static final String TASK_CACHE_PREFIX = "task";
    private static final String TASK_LIST_CACHE_PREFIX = "task:list";
    private static final String TASK_STATS_CACHE_PREFIX = "task:stats";
    private static final String TASK_TYPES_CACHE_KEY = "task:types";

    @Override
    public Page<TaskListResponse> getTaskList(TaskQueryRequest request, Integer page, Integer size) {
        // 构建缓存key
        String cacheKey = RedisCacheUtils.buildKey(TASK_LIST_CACHE_PREFIX, 
            request.toString(), String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Page.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化任务列表缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(request.getKeyword()), Task::getTitle, request.getKeyword())
                   .or(StringUtils.hasText(request.getKeyword()))
                   .like(StringUtils.hasText(request.getKeyword()), Task::getDescription, request.getKeyword())
                   .eq(StringUtils.hasText(request.getType()), Task::getType, request.getType())
                   .eq(StringUtils.hasText(request.getStatus()), Task::getStatus, request.getStatus());
        
        // 日期范围查询
        if (StringUtils.hasText(request.getStartDate())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartDate() + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Task::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(request.getEndDate())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndDate() + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Task::getCreatedAt, endTime);
        }
        
        queryWrapper.orderByDesc(Task::getCreatedAt);

        // 手动分页查询
        long startTime = System.currentTimeMillis();
        
        // 查询总数（使用count优化）
        long total = taskMapper.selectCount(queryWrapper);
        
        // 分页查询数据
        int offset = PageUtils.getOffset(page, size);
        int limit = size == null || size < 1 ? PageUtils.DEFAULT_PAGE_SIZE : Math.min(size, PageUtils.MAX_PAGE_SIZE);
        
        List<Task> tasks = taskMapper.selectList(queryWrapper.last("LIMIT " + offset + ", " + limit));
        
        // 转换为响应DTO
        List<TaskListResponse> records = tasks.stream()
            .map(this::convertToTaskListResponse)
            .collect(Collectors.toList());
        
        Page<TaskListResponse> responsePage = PageUtils.buildPage(records, total, page, size);
        
        // 记录分页信息
        PageUtils.logPageInfo("getTaskList", page, size, total, System.currentTimeMillis() - startTime);

        // 缓存结果（15分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(responsePage), 15, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化任务列表缓存失败: {}", e.getMessage());
        }

        return responsePage;
    }

    @Override
    public TaskDetailResponse getTaskDetail(Long taskId) {
        String cacheKey = RedisCacheUtils.buildKey(TASK_CACHE_PREFIX, "detail", String.valueOf(taskId));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, TaskDetailResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化任务详情缓存失败: {}", e.getMessage());
            }
        }

        // 查询数据库
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            return null;
        }

        TaskDetailResponse response = convertToTaskDetailResponse(task);
        
        // 缓存结果（30分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 30, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化任务详情缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public Long createTask(TaskCreateRequest request) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task);
        task.setStatus("active");
        task.setCurrentClaimCount(0);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        int result = taskMapper.insert(task);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(TASK_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.deletePattern(TASK_STATS_CACHE_PREFIX + "*");
            return task.getId();
        }
        return 0L;
    }

    @Override
    public void updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        BeanUtils.copyProperties(request, task);
        task.setUpdatedAt(LocalDateTime.now());

        int result = taskMapper.updateById(task);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(TASK_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(TASK_CACHE_PREFIX, "detail", String.valueOf(taskId)));
            redisCacheUtils.deletePattern(TASK_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public void deleteTask(Long taskId) {
        int result = taskMapper.deleteById(taskId);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(TASK_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(TASK_CACHE_PREFIX, "detail", String.valueOf(taskId)));
            redisCacheUtils.deletePattern(TASK_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public void batchOperation(List<Long> taskIds, String operation) {
        if ("delete".equals(operation)) {
            taskMapper.deleteBatchIds(taskIds);
        } else if ("activate".equals(operation)) {
            taskIds.forEach(id -> {
                Task task = new Task();
                task.setId(id);
                task.setStatus("active");
                task.setUpdatedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            });
        } else if ("deactivate".equals(operation)) {
            taskIds.forEach(id -> {
                Task task = new Task();
                task.setId(id);
                task.setStatus("inactive");
                task.setUpdatedAt(LocalDateTime.now());
                taskMapper.updateById(task);
            });
        }
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(TASK_LIST_CACHE_PREFIX + "*");
        redisCacheUtils.deletePattern(TASK_STATS_CACHE_PREFIX + "*");
    }

    @Override
    public TaskStatsResponse getTaskStats(String type, String startDate, String endDate) {
        String cacheKey = RedisCacheUtils.buildKey(TASK_STATS_CACHE_PREFIX, type, startDate, endDate);
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, TaskStatsResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化任务统计缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<Task> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(Task::getType, type);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(Task::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(Task::getCreatedAt, endTime);
        }

        // 统计查询
        long totalTasks = taskMapper.selectCount(queryWrapper);
        long activeTasks = taskMapper.selectCount(queryWrapper.clone().eq(Task::getStatus, "active"));
        long inactiveTasks = taskMapper.selectCount(queryWrapper.clone().eq(Task::getStatus, "inactive"));

        TaskStatsResponse response = new TaskStatsResponse();
        response.setTotalTasks(totalTasks);
        response.setActiveTasks(activeTasks);
        response.setInactiveTasks(inactiveTasks);
        // 统计已完成任务数（关联user_tasks表）
        LambdaQueryWrapper<UserTask> completedWrapper = new LambdaQueryWrapper<>();
        completedWrapper.eq(UserTask::getStatus, "completed");
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            completedWrapper.ge(UserTask::getCompletedAt, startTime);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            completedWrapper.le(UserTask::getCompletedAt, endTime);
        }
        long completedTasks = userTaskMapper.selectCount(completedWrapper);
        response.setCompletedTasks(completedTasks);

        // 缓存结果（10分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化任务统计缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public Map<String, String> getTaskTypes() {
        String cacheKey = TASK_TYPES_CACHE_KEY;
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Map.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化任务类型缓存失败: {}", e.getMessage());
            }
        }

        Map<String, String> taskTypes = new HashMap<>();
        taskTypes.put("ad", "广告任务");
        taskTypes.put("video", "视频任务");
        taskTypes.put("app_install", "应用安装");
        taskTypes.put("survey", "问卷调查");
        taskTypes.put("share", "分享任务");
        taskTypes.put("follow", "关注任务");
        taskTypes.put("comment", "评论任务");

        // 缓存结果（1小时）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(taskTypes), 1, java.util.concurrent.TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("序列化任务类型缓存失败: {}", e.getMessage());
        }

        return taskTypes;
    }

    private TaskListResponse convertToTaskListResponse(Task task) {
        TaskListResponse response = new TaskListResponse();
        BeanUtils.copyProperties(task, response);
        return response;
    }

    private TaskDetailResponse convertToTaskDetailResponse(Task task) {
        TaskDetailResponse response = new TaskDetailResponse();
        BeanUtils.copyProperties(task, response);
        return response;
    }
}


