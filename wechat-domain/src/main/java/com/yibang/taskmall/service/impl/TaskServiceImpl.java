package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yibang.taskmall.dto.request.TaskClaimRequest;
import com.yibang.taskmall.dto.request.TaskCompleteRequest;
import com.yibang.taskmall.dto.request.TaskQueryRequest;
import com.yibang.taskmall.dto.response.TaskDTO;
import com.yibang.taskmall.dto.response.UserTaskDTO;
import com.yibang.taskmall.dto.response.UserTaskStatsDTO;
import com.yibang.taskmall.entity.Task;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.entity.UserTask;
import com.yibang.taskmall.mapper.TaskMapper;
import com.yibang.taskmall.mapper.UserMapper;
import com.yibang.taskmall.mapper.UserTaskMapper;
import com.yibang.taskmall.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 任务服务实现类
 * 提供原子化的业务方法，确保每个方法都有明确的业务含义
 */
@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private UserTaskMapper userTaskMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 查询可用任务列表
     * 支持按任务类型、佣金等级、用户等级筛选
     */
    @Override
    public List<TaskDTO> getAvailableTasks(TaskQueryRequest queryRequest) {
        log.info("查询可用任务列表，参数：{}", queryRequest);
        
        // 构建缓存键
        String cacheKey = String.format("tasks:available:%s:%s:%s:%d:%d", 
            queryRequest.getTaskType(), 
            queryRequest.getRewardLevel(), 
            queryRequest.getUserLevel(),
            queryRequest.getPage(), 
            queryRequest.getSize());
        
        // 尝试从缓存获取
        List<TaskDTO> cachedTasks = (List<TaskDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTasks != null) {
            log.info("从缓存获取任务列表，数量：{}", cachedTasks.size());
            return cachedTasks;
        }
        
        // 从数据库查询
        List<Task> tasks = taskMapper.findTasksByTypeAndRewardLevel(
            queryRequest.getTaskType(),
            queryRequest.getRewardLevel(),
            queryRequest.getOffset(),
            queryRequest.getSize()
        );
        
        // 转换为DTO
        List<TaskDTO> taskDTOs = convertToTaskDTOs(tasks, queryRequest.getUserId());
        
        // 缓存结果（缓存5分钟）
        redisTemplate.opsForValue().set(cacheKey, taskDTOs, 5, TimeUnit.MINUTES);
        
        log.info("查询到可用任务数量：{}", taskDTOs.size());
        return taskDTOs;
    }
    
    /**
     * 统计可用任务总数
     */
    @Override
    public Long countAvailableTasks(TaskQueryRequest queryRequest) {
        return taskMapper.countTasksByTypeAndRewardLevel(
            queryRequest.getTaskType(),
            queryRequest.getRewardLevel()
        );
    }
    
    /**
     * 获取任务详情
     * 包含用户是否已领取等状态信息
     */
    @Override
    public TaskDTO getTaskDetail(Long taskId, Long userId) {
        log.info("获取任务详情，taskId：{}，userId：{}", taskId, userId);
        
        // 构建缓存键
        String cacheKey = String.format("task:detail:%d:%d", taskId, userId);
        
        // 尝试从缓存获取
        TaskDTO cachedTask = (TaskDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cachedTask != null) {
            return cachedTask;
        }
        
        // 查询任务信息
        Task task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }
        
        // 转换为DTO
        TaskDTO taskDTO = convertToTaskDTO(task);
        
        // 设置用户相关状态
        if (userId != null) {
            // 检查用户是否已领取
            Long claimedCount = userTaskMapper.checkTaskClaimed(userId, taskId);
            taskDTO.setCanClaim(claimedCount == 0 && canClaimTask(taskId, userId));
            
            // 如果已领取，查询用户任务状态
            if (claimedCount > 0) {
                QueryWrapper<UserTask> wrapper = new QueryWrapper<>();
                wrapper.eq("user_id", userId).eq("task_id", taskId);
                UserTask userTask = userTaskMapper.selectOne(wrapper);
                if (userTask != null) {
                    taskDTO.setUserTaskStatus(userTask.getStatus());
                }
            }
        }
        
        // 缓存结果（缓存3分钟）
        redisTemplate.opsForValue().set(cacheKey, taskDTO, 3, TimeUnit.MINUTES);
        
        return taskDTO;
    }
    
    /**
     * 检查任务可领取性
     * 验证用户等级、任务状态、领取次数等条件
     */
    @Override
    public boolean canClaimTask(Long taskId, Long userId) {
        log.info("检查任务可领取性，taskId：{}，userId：{}", taskId, userId);
        
        // 查询任务信息
        Task task = taskMapper.selectById(taskId);
        if (task == null || !"active".equals(task.getStatus())) {
            log.warn("任务不存在或状态不可用，taskId：{}", taskId);
            return false;
        }
        
        // 检查任务是否过期
        if (task.getExpireTime() != null && task.getExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("任务已过期，taskId：{}，expireTime：{}", taskId, task.getExpireTime());
            return false;
        }
        
        // 检查任务领取次数限制
        if (task.getMaxClaimCount() != -1 && 
            task.getCurrentClaimCount() >= task.getMaxClaimCount()) {
            log.warn("任务领取次数已达上限，taskId：{}，current：{}，max：{}", 
                taskId, task.getCurrentClaimCount(), task.getMaxClaimCount());
            return false;
        }
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            log.warn("用户不存在，userId：{}", userId);
            return false;
        }
        
        // 检查用户等级权限
        if (!"all".equals(task.getUserLevel()) && !task.getUserLevel().equals(user.getUserLevel())) {
            log.warn("用户等级不符合任务要求，taskId：{}，requireLevel：{}，userLevel：{}", 
                taskId, task.getUserLevel(), user.getUserLevel());
            return false;
        }
        
        // 检查用户是否已领取该任务
        Long claimedCount = userTaskMapper.checkTaskClaimed(userId, taskId);
        if (claimedCount > 0) {
            log.warn("用户已领取该任务，taskId：{}，userId：{}", taskId, userId);
            return false;
        }
        
        return true;
    }
    
    /**
     * 领取任务
     * 原子操作：创建用户任务记录并更新任务统计
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTaskDTO claimTask(TaskClaimRequest claimRequest) {
        log.info("用户领取任务，request：{}", claimRequest);
        
        Long taskId = claimRequest.getTaskId();
        Long userId = claimRequest.getUserId();
        
        // 再次检查任务可领取性（防止并发问题）
        if (!canClaimTask(taskId, userId)) {
            throw new RuntimeException("任务不可领取");
        }
        
        // 创建用户任务记录
        UserTask userTask = new UserTask();
        userTask.setUserId(userId);
        userTask.setTaskId(taskId);
        userTask.setStatus("claimed");
        userTask.setClaimedAt(LocalDateTime.now());
        
        // 设置奖励金额（从任务表获取）
        Task task = taskMapper.selectById(taskId);
        userTask.setRewardAmount(task.getRewardAmount());
        
        // 保存用户任务记录
        userTaskMapper.insert(userTask);
        
        // 更新任务领取计数
        task.setCurrentClaimCount(task.getCurrentClaimCount() + 1);
        taskMapper.updateById(task);
        
        // 清除相关缓存
        clearTaskRelatedCache(taskId, userId);
        
        log.info("任务领取成功，userTaskId：{}", userTask.getId());
        
        // 转换为DTO返回
        return convertToUserTaskDTO(userTask, task);
    }
    
    /**
     * 提交任务完成凭证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTaskDTO submitTaskCompletion(TaskCompleteRequest completeRequest) {
        log.info("提交任务完成凭证，request：{}", completeRequest);
        
        // 查询用户任务记录
        UserTask userTask = userTaskMapper.selectById(completeRequest.getUserTaskId());
        if (userTask == null) {
            throw new RuntimeException("用户任务记录不存在");
        }
        
        // 验证用户身份
        if (!userTask.getUserId().equals(completeRequest.getUserId())) {
            throw new RuntimeException("无权限操作该任务");
        }
        
        // 验证任务状态
        if (!"claimed".equals(userTask.getStatus())) {
            throw new RuntimeException("任务状态不正确，无法提交完成凭证");
        }
        
        // 构建凭证数据
        StringBuilder proofData = new StringBuilder();
        proofData.append("{");
        proofData.append("\"images\":");
        proofData.append(completeRequest.getProofImages() != null ? 
            convertListToJsonArray(completeRequest.getProofImages()) : "[]");
        proofData.append(",\"note\":\"").append(completeRequest.getCompletionNote() != null ? 
            completeRequest.getCompletionNote() : "").append("\"");
        if (completeRequest.getAdditionalProofData() != null) {
            proofData.append(",\"additional\":").append(completeRequest.getAdditionalProofData());
        }
        proofData.append(",\"submitTime\":\"").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\"");
        proofData.append("}");
        
        // 更新用户任务状态
        userTask.setStatus("completed");
        userTask.setCompletedAt(LocalDateTime.now());
        userTask.setProofData(proofData.toString());
        
        userTaskMapper.updateById(userTask);
        
        // 清除相关缓存
        clearTaskRelatedCache(userTask.getTaskId(), userTask.getUserId());
        
        log.info("任务完成凭证提交成功，userTaskId：{}", userTask.getId());
        
        // 查询任务信息并转换为DTO
        Task task = taskMapper.selectById(userTask.getTaskId());
        return convertToUserTaskDTO(userTask, task);
    }
    
    /**
     * 验证任务完成
     * 系统或管理员验证用户提交的凭证
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTaskDTO verifyTaskCompletion(Long userTaskId, boolean isApproved, String failureReason) {
        log.info("验证任务完成，userTaskId：{}，isApproved：{}，failureReason：{}", 
            userTaskId, isApproved, failureReason);
        
        // 查询用户任务记录
        UserTask userTask = userTaskMapper.selectById(userTaskId);
        if (userTask == null) {
            throw new RuntimeException("用户任务记录不存在");
        }
        
        // 验证任务状态
        if (!"completed".equals(userTask.getStatus())) {
            throw new RuntimeException("任务状态不正确，无法进行验证");
        }
        
        // 更新验证结果
        if (isApproved) {
            userTask.setStatus("verified");
            userTask.setVerifiedAt(LocalDateTime.now());
            userTask.setFailureReason(null);
            
            // 发放任务奖励
            distributeTaskReward(userTaskId);
        } else {
            userTask.setStatus("failed");
            userTask.setFailureReason(failureReason);
        }
        
        userTaskMapper.updateById(userTask);
        
        // 清除相关缓存
        clearTaskRelatedCache(userTask.getTaskId(), userTask.getUserId());
        
        log.info("任务验证完成，result：{}", isApproved ? "通过" : "失败");
        
        // 查询任务信息并转换为DTO
        Task task = taskMapper.selectById(userTask.getTaskId());
        return convertToUserTaskDTO(userTask, task);
    }
    
    /**
     * 发放任务奖励
     * 将奖励金额添加到用户账户余额
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean distributeTaskReward(Long userTaskId) {
        log.info("发放任务奖励，userTaskId：{}", userTaskId);
        
        // 查询用户任务记录
        UserTask userTask = userTaskMapper.selectById(userTaskId);
        if (userTask == null || !"verified".equals(userTask.getStatus())) {
            log.warn("用户任务状态不正确，无法发放奖励，userTaskId：{}", userTaskId);
            return false;
        }
        
        // 查询用户信息
        User user = userMapper.selectById(userTask.getUserId());
        if (user == null) {
            log.warn("用户不存在，无法发放奖励，userId：{}", userTask.getUserId());
            return false;
        }
        
        // 更新用户余额
        Integer rewardAmount = userTask.getRewardAmount();
        if (rewardAmount != null && rewardAmount > 0) {
            Long currentBalance = user.getAvailableBalance() != null ? user.getAvailableBalance() : 0L;
            user.setAvailableBalance(currentBalance + rewardAmount.longValue());
            userMapper.updateById(user);
            
            // TODO: 记录收益流水
            // 这里应该调用收益服务记录详细的收益流水
            
            log.info("任务奖励发放成功，userId：{}，amount：{}", user.getId(), rewardAmount);
        }
        
        return true;
    }
    
    /**
     * 查询用户任务列表
     */
    @Override
    public List<UserTaskDTO> getUserTasks(Long userId, String status, int page, int size) {
        log.info("查询用户任务列表，userId：{}，status：{}，page：{}，size：{}", 
            userId, status, page, size);
        
        int offset = (page - 1) * size;
        List<UserTask> userTasks = userTaskMapper.findUserTasksWithDetails(userId, status, offset, size);
        
        return convertToUserTaskDTOs(userTasks);
    }
    
    /**
     * 统计用户任务数量
     */
    @Override
    public Long countUserTasks(Long userId, String status) {
        return userTaskMapper.countUserTasks(userId, status);
    }
    
    /**
     * 获取用户任务统计
     */
    @Override
    public UserTaskStatsDTO getUserTaskStats(Long userId) {
        log.info("获取用户任务统计，userId：{}", userId);
        
        UserTaskStatsDTO stats = new UserTaskStatsDTO();
        stats.setUserId(userId);
        
        // 统计已完成任务数量
        stats.setCompletedTaskCount(userTaskMapper.countCompletedTasks(userId));
        
        // 统计进行中任务数量
        stats.setInProgressTaskCount(userTaskMapper.countUserTasks(userId, "claimed") + 
                                    userTaskMapper.countUserTasks(userId, "completed"));
        
        // 计算总收益
        Long totalEarnings = userTaskMapper.calculateTotalEarnings(userId);
        stats.setTotalEarnings(totalEarnings);
        stats.setTotalEarningsDisplay(formatAmount(totalEarnings));
        
        // TODO: 计算本月数据、完成率等
        // 这里可以根据业务需要添加更多统计逻辑
        
        return stats;
    }
    
    /**
     * 处理任务过期
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleExpiredTasks() {
        log.info("开始处理过期任务");
        
        // 查询已过期但状态还是claimed的用户任务
        QueryWrapper<UserTask> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "claimed");
        // 这里需要关联任务表查询过期时间，简化处理
        
        List<UserTask> expiredTasks = userTaskMapper.selectList(wrapper);
        int expiredCount = 0;
        
        for (UserTask userTask : expiredTasks) {
            Task task = taskMapper.selectById(userTask.getTaskId());
            if (task != null && task.getExpireTime() != null && 
                task.getExpireTime().isBefore(LocalDateTime.now())) {
                
                userTask.setStatus("expired");
                userTaskMapper.updateById(userTask);
                expiredCount++;
            }
        }
        
        log.info("处理过期任务完成，数量：{}", expiredCount);
        return expiredCount;
    }
    
    /**
     * 获取推荐任务
     */
    @Override
    public List<TaskDTO> getRecommendedTasks(Long userId, int limit) {
        log.info("获取推荐任务，userId：{}，limit：{}", userId, limit);
        
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            return new ArrayList<>();
        }
        
        // 根据用户等级推荐任务
        List<Task> tasks = taskMapper.findAvailableTasksForUser(user.getUserLevel(), 0, limit);
        
        return convertToTaskDTOs(tasks, userId);
    }
    
    // ==================== 私有辅助方法 ====================
    
    /**
     * 转换Task列表为TaskDTO列表
     */
    private List<TaskDTO> convertToTaskDTOs(List<Task> tasks, Long userId) {
        List<TaskDTO> taskDTOs = new ArrayList<>();
        for (Task task : tasks) {
            TaskDTO taskDTO = convertToTaskDTO(task);
            
            // 设置用户相关状态
            if (userId != null) {
                Long claimedCount = userTaskMapper.checkTaskClaimed(userId, task.getId());
                taskDTO.setCanClaim(claimedCount == 0 && canClaimTask(task.getId(), userId));
            }
            
            taskDTOs.add(taskDTO);
        }
        return taskDTOs;
    }
    
    /**
     * 转换Task为TaskDTO
     */
    private TaskDTO convertToTaskDTO(Task task) {
        TaskDTO taskDTO = new TaskDTO();
        BeanUtils.copyProperties(task, taskDTO);
        
        // 设置显示名称
        taskDTO.setTypeDisplayName(getTaskTypeDisplayName(task.getType()));
        taskDTO.setRewardLevelDisplayName(getRewardLevelDisplayName(task.getRewardLevel()));
        
        // 格式化奖励金额
        taskDTO.setRewardAmountDisplay(formatAmount(task.getRewardAmount()));
        
        // 计算剩余可领取次数
        if (task.getMaxClaimCount() == -1) {
            taskDTO.setRemainingClaimCount(-1); // 无限制
        } else {
            taskDTO.setRemainingClaimCount(task.getMaxClaimCount() - task.getCurrentClaimCount());
        }
        
        return taskDTO;
    }
    
    /**
     * 转换UserTask列表为UserTaskDTO列表
     */
    private List<UserTaskDTO> convertToUserTaskDTOs(List<UserTask> userTasks) {
        List<UserTaskDTO> userTaskDTOs = new ArrayList<>();
        for (UserTask userTask : userTasks) {
            Task task = taskMapper.selectById(userTask.getTaskId());
            userTaskDTOs.add(convertToUserTaskDTO(userTask, task));
        }
        return userTaskDTOs;
    }
    
    /**
     * 转换UserTask为UserTaskDTO
     */
    private UserTaskDTO convertToUserTaskDTO(UserTask userTask, Task task) {
        UserTaskDTO userTaskDTO = new UserTaskDTO();
        BeanUtils.copyProperties(userTask, userTaskDTO);
        
        if (task != null) {
            userTaskDTO.setTaskTitle(task.getTitle());
            userTaskDTO.setTaskDescription(task.getDescription());
            userTaskDTO.setTaskImageUrl(task.getImageUrl());
            userTaskDTO.setTaskType(task.getType());
        }
        
        // 设置状态显示名称
        userTaskDTO.setStatusDisplayName(getUserTaskStatusDisplayName(userTask.getStatus()));
        
        // 格式化奖励金额
        userTaskDTO.setRewardAmountDisplay(formatAmount(userTask.getRewardAmount()));
        
        return userTaskDTO;
    }
    
    /**
     * 获取任务类型显示名称
     */
    private String getTaskTypeDisplayName(String type) {
        switch (type) {
            case "ad": return "广告任务";
            case "video": return "视频任务";
            case "app_install": return "应用安装";
            case "survey": return "问卷调查";
            case "share": return "分享任务";
            default: return type;
        }
    }
    
    /**
     * 获取佣金等级显示名称
     */
    private String getRewardLevelDisplayName(String rewardLevel) {
        switch (rewardLevel) {
            case "low": return "低佣金";
            case "medium": return "中佣金";
            case "high": return "高佣金";
            default: return rewardLevel;
        }
    }
    
    /**
     * 获取用户任务状态显示名称
     */
    private String getUserTaskStatusDisplayName(String status) {
        switch (status) {
            case "claimed": return "已领取";
            case "completed": return "已完成";
            case "verified": return "已验证";
            case "expired": return "已过期";
            case "failed": return "审核失败";
            default: return status;
        }
    }
    
    /**
     * 格式化金额（分转元）
     */
    private String formatAmount(Long amountInCents) {
        if (amountInCents == null || amountInCents == 0) {
            return "0.00";
        }
        return String.format("%.2f", amountInCents / 100.0);
    }

    /**
     * 格式化金额（分转元）- Integer重载
     */
    private String formatAmount(Integer amountInCents) {
        if (amountInCents == null || amountInCents == 0) {
            return "0.00";
        }
        return String.format("%.2f", amountInCents / 100.0);
    }
    
    /**
     * 将List转换为JSON数组字符串
     */
    private String convertListToJsonArray(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(list.get(i)).append("\"");
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * 清除任务相关缓存
     */
    private void clearTaskRelatedCache(Long taskId, Long userId) {
        // 清除任务详情缓存
        String taskDetailKey = String.format("task:detail:%d:%d", taskId, userId);
        redisTemplate.delete(taskDetailKey);
        
        // 清除任务列表缓存（这里简化处理，实际可以更精确）
        redisTemplate.delete("tasks:available:*");
    }
}