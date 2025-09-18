package com.yibang.taskmall.bkservice.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yibang.taskmall.bkdto.request.UserBalanceUpdateRequest;
import com.yibang.taskmall.bkdto.request.UserLevelUpdateRequest;
import com.yibang.taskmall.bkdto.request.UserQueryRequest;
import com.yibang.taskmall.bkdto.response.UserDetailResponse;
import com.yibang.taskmall.bkdto.response.UserListResponse;
import com.yibang.taskmall.bkdto.response.UserStatsResponse;
import com.yibang.taskmall.bkservice.UserBkService;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.entity.UserTask;
import com.yibang.taskmall.mapper.UserMapper;
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
public class UserBkServiceImpl implements UserBkService {

    private final UserMapper userMapper;
    private final UserTaskMapper userTaskMapper;
    private final RedisCacheUtils redisCacheUtils;
    private final ObjectMapper objectMapper;

    private static final String USER_CACHE_PREFIX = "user";
    private static final String USER_LIST_CACHE_PREFIX = "user:list";
    private static final String USER_STATS_CACHE_PREFIX = "user:stats";
    private static final String USER_LEVELS_CACHE_KEY = "user:levels";

    @Override
    public Page<UserListResponse> getUserList(UserQueryRequest request, Integer page, Integer size) {
        // 构建缓存key
        String cacheKey = RedisCacheUtils.buildKey(USER_LIST_CACHE_PREFIX, 
            request.toString(), String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Page.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户列表缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(request.getKeyword()), User::getNickname, request.getKeyword())
                   .or(StringUtils.hasText(request.getKeyword()))
                   .like(StringUtils.hasText(request.getKeyword()), User::getPhone, request.getKeyword())
                   .eq(StringUtils.hasText(request.getLevel()), User::getUserLevel, request.getLevel())
                   .eq(StringUtils.hasText(request.getStatus()), User::getStatus, request.getStatus());
        
        // 日期范围查询
        if (StringUtils.hasText(request.getStartDate())) {
            LocalDateTime startTime = LocalDateTime.parse(request.getStartDate() + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(User::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(request.getEndDate())) {
            LocalDateTime endTime = LocalDateTime.parse(request.getEndDate() + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(User::getCreatedAt, endTime);
        }
        
        queryWrapper.orderByDesc(User::getCreatedAt);

        // 手动分页查询
        long startTime = System.currentTimeMillis();
        
        // 查询总数（使用count优化）
        long total = userMapper.selectCount(queryWrapper);
        
        // 分页查询数据
        int offset = PageUtils.getOffset(page, size);
        int limit = size == null || size < 1 ? PageUtils.DEFAULT_PAGE_SIZE : Math.min(size, PageUtils.MAX_PAGE_SIZE);
        
        List<User> users = userMapper.selectList(queryWrapper.last("LIMIT " + offset + ", " + limit));
        
        // 转换为响应DTO
        List<UserListResponse> records = users.stream()
            .map(this::convertToUserListResponse)
            .collect(Collectors.toList());
        
        Page<UserListResponse> responsePage = PageUtils.buildPage(records, total, page, size);
        
        // 记录分页信息
        PageUtils.logPageInfo("getUserList", page, size, total, System.currentTimeMillis() - startTime);

        // 缓存结果（15分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(responsePage), 15, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户列表缓存失败: {}", e.getMessage());
        }

        return responsePage;
    }

    @Override
    public UserDetailResponse getUserDetail(Long userId) {
        String cacheKey = RedisCacheUtils.buildKey(USER_CACHE_PREFIX, "detail", String.valueOf(userId));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, UserDetailResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户详情缓存失败: {}", e.getMessage());
            }
        }

        // 查询数据库
        User user = userMapper.selectById(userId);
        if (user == null) {
            return null;
        }

        UserDetailResponse response = convertToUserDetailResponse(user);
        
        // 缓存结果（30分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 30, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户详情缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public void updateUserBalance(Long userId, UserBalanceUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if ("add".equals(request.getType())) {
            user.setAvailableBalance(user.getAvailableBalance() + request.getAmount());
        } else if ("subtract".equals(request.getType())) {
            if (user.getAvailableBalance() < request.getAmount()) {
                throw new RuntimeException("余额不足");
            }
            user.setAvailableBalance(user.getAvailableBalance() - request.getAmount());
        }

        user.setUpdatedAt(LocalDateTime.now());
        int result = userMapper.updateById(user);
        
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(USER_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(USER_CACHE_PREFIX, "detail", String.valueOf(userId)));
            redisCacheUtils.deletePattern(USER_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public void updateUserLevel(Long userId, UserLevelUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setUserLevel(request.getNewLevel());
        user.setUpdatedAt(LocalDateTime.now());
        
        int result = userMapper.updateById(user);
        if (result > 0) {
            // 清除相关缓存
            redisCacheUtils.deletePattern(USER_LIST_CACHE_PREFIX + "*");
            redisCacheUtils.delete(RedisCacheUtils.buildKey(USER_CACHE_PREFIX, "detail", String.valueOf(userId)));
            redisCacheUtils.deletePattern(USER_STATS_CACHE_PREFIX + "*");
        }
    }

    @Override
    public void batchOperation(List<Long> userIds, String operation) {
        if ("delete".equals(operation)) {
            userMapper.deleteBatchIds(userIds);
        } else if ("activate".equals(operation)) {
            userIds.forEach(id -> {
                User user = new User();
                user.setId(id);
                user.setStatus("active");
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            });
        } else if ("deactivate".equals(operation)) {
            userIds.forEach(id -> {
                User user = new User();
                user.setId(id);
                user.setStatus("inactive");
                user.setUpdatedAt(LocalDateTime.now());
                userMapper.updateById(user);
            });
        }
        
        // 清除相关缓存
        redisCacheUtils.deletePattern(USER_LIST_CACHE_PREFIX + "*");
        redisCacheUtils.deletePattern(USER_STATS_CACHE_PREFIX + "*");
    }

    @Override
    public UserStatsResponse getUserStats(String type, String startDate, String endDate) {
        String cacheKey = RedisCacheUtils.buildKey(USER_STATS_CACHE_PREFIX, type, startDate, endDate);
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, UserStatsResponse.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户统计缓存失败: {}", e.getMessage());
            }
        }

        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type)) {
            queryWrapper.eq(User::getUserLevel, type);
        }
        if (StringUtils.hasText(startDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.ge(User::getCreatedAt, startTime);
        }
        if (StringUtils.hasText(endDate)) {
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            queryWrapper.le(User::getCreatedAt, endTime);
        }

        // 统计查询
        long totalUsers = userMapper.selectCount(queryWrapper);
        long activeUsers = userMapper.selectCount(queryWrapper.clone().eq(User::getStatus, "active"));
        long inactiveUsers = userMapper.selectCount(queryWrapper.clone().eq(User::getStatus, "inactive"));

        UserStatsResponse response = new UserStatsResponse();
        response.setTotalUsers(totalUsers);
        response.setActiveUsers(activeUsers);
        response.setInactiveUsers(inactiveUsers);
        // 统计新用户（当日注册）
        LambdaQueryWrapper<User> newUserWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
            LocalDateTime startTime = LocalDateTime.parse(startDate + " 00:00:00", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(endDate + " 23:59:59", 
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            newUserWrapper.between(User::getCreatedAt, startTime, endTime);
        } else {
            // 默认统计今日新用户
            LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime todayEnd = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);
            newUserWrapper.between(User::getCreatedAt, todayStart, todayEnd);
        }
        long newUsers = userMapper.selectCount(newUserWrapper);
        response.setNewUsers(newUsers);

        // 缓存结果（10分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(response), 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户统计缓存失败: {}", e.getMessage());
        }

        return response;
    }

    @Override
    public Map<String, Object> getUserLevels() {
        String cacheKey = USER_LEVELS_CACHE_KEY;
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Map.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户等级缓存失败: {}", e.getMessage());
            }
        }

        Map<String, Object> userLevels = new HashMap<>();
        userLevels.put("normal", "普通用户");
        userLevels.put("signed", "签约用户");
        userLevels.put("vip", "VIP用户");
        userLevels.put("premium", "高级用户");

        // 缓存结果（1小时）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(userLevels), 1, java.util.concurrent.TimeUnit.HOURS);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户等级缓存失败: {}", e.getMessage());
        }

        return userLevels;
    }

    @Override
    public Map<String, Object> getUserTasks(Long userId, Integer page, Integer size) {
        String cacheKey = RedisCacheUtils.buildKey(USER_CACHE_PREFIX, "tasks", String.valueOf(userId), 
            String.valueOf(page), String.valueOf(size));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Map.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户任务缓存失败: {}", e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        
        // 统计任务总数
        long totalTasks = userTaskMapper.countUserTasks(userId, null);
        
        // 统计已完成任务数
        long completedTasks = userTaskMapper.countUserTasks(userId, "completed");
        
        // 统计进行中任务数
        long pendingTasks = userTaskMapper.countUserTasks(userId, "claimed");
        
        // 查询任务详情（分页）
        int offset = PageUtils.getOffset(page, size);
        int limit = size == null || size < 1 ? PageUtils.DEFAULT_PAGE_SIZE : Math.min(size, PageUtils.MAX_PAGE_SIZE);
        
        List<UserTask> userTasks = userTaskMapper.findUserTasksWithDetails(userId, null, offset, limit);
        
        result.put("total", totalTasks);
        result.put("completed", completedTasks);
        result.put("pending", pendingTasks);
        result.put("tasks", userTasks);
        result.put("totalEarnings", calculateUserTaskEarnings(userId));
        
        // 缓存结果（5分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(result), 5, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户任务缓存失败: {}", e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getUserInvites(Long userId) {
        String cacheKey = RedisCacheUtils.buildKey(USER_CACHE_PREFIX, "invites", String.valueOf(userId));
        
        // 尝试从缓存获取
        String cachedData = redisCacheUtils.get(cacheKey);
        if (StringUtils.hasText(cachedData)) {
            try {
                return objectMapper.readValue(cachedData, Map.class);
            } catch (JsonProcessingException e) {
                log.warn("反序列化用户邀请缓存失败: {}", e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        
        // 获取用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            result.put("inviteCode", "");
            result.put("inviteCount", 0);
            result.put("invitedUsers", new ArrayList<>());
            return result;
        }
        
        // 查询被邀请的用户列表
        LambdaQueryWrapper<User> invitedWrapper = new LambdaQueryWrapper<>();
        invitedWrapper.eq(User::getInvitedBy, userId)
                     .orderByDesc(User::getCreatedAt)
                     .last("LIMIT 20"); // 只显示最近20个
        
        List<User> invitedUsers = userMapper.selectList(invitedWrapper);
        
        // 转换为简化的用户信息
        List<Map<String, Object>> invitedUserInfos = invitedUsers.stream()
            .map(invitedUser -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", invitedUser.getId());
                userInfo.put("nickname", invitedUser.getNickname());
                userInfo.put("avatar", invitedUser.getAvatar());
                userInfo.put("userLevel", invitedUser.getUserLevel());
                userInfo.put("createdAt", invitedUser.getCreatedAt());
                userInfo.put("totalEarnings", invitedUser.getTotalEarnings());
                return userInfo;
            })
            .collect(Collectors.toList());
        
        result.put("inviteCode", user.getInviteCode());
        result.put("inviteCount", user.getInviteCount());
        result.put("invitedUsers", invitedUserInfos);
        result.put("totalInviteEarnings", calculateInviteEarnings(userId));
        
        // 缓存结果（10分钟）
        try {
            redisCacheUtils.set(cacheKey, objectMapper.writeValueAsString(result), 10, java.util.concurrent.TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            log.warn("序列化用户邀请缓存失败: {}", e.getMessage());
        }
        
        return result;
    }

    private UserListResponse convertToUserListResponse(User user) {
        UserListResponse response = new UserListResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    private UserDetailResponse convertToUserDetailResponse(User user) {
        UserDetailResponse response = new UserDetailResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 计算用户任务总收益
     */
    private Long calculateUserTaskEarnings(Long userId) {
        try {
            // 查询已验证的任务收益
            LambdaQueryWrapper<UserTask> earningsWrapper = new LambdaQueryWrapper<>();
            earningsWrapper.eq(UserTask::getUserId, userId)
                          .eq(UserTask::getStatus, "verified")
                          .isNotNull(UserTask::getRewardAmount);
            
            List<UserTask> verifiedTasks = userTaskMapper.selectList(earningsWrapper);
            
            return verifiedTasks.stream()
                .mapToLong(task -> task.getRewardAmount() != null ? task.getRewardAmount() : 0L)
                .sum();
        } catch (Exception e) {
            log.warn("计算用户任务收益失败: userId={}, error={}", userId, e.getMessage());
            return 0L;
        }
    }

    /**
     * 计算用户邀请收益
     */
    private Long calculateInviteEarnings(Long userId) {
        try {
            // 这里可以根据业务规则计算邀请收益
            // 比如：被邀请用户完成任务的一定比例作为邀请收益
            LambdaQueryWrapper<User> invitedWrapper = new LambdaQueryWrapper<>();
            invitedWrapper.eq(User::getInvitedBy, userId);
            
            List<User> invitedUsers = userMapper.selectList(invitedWrapper);
            
            // 简单计算：每个被邀请用户的总收益的5%作为邀请收益
            return invitedUsers.stream()
                .mapToLong(user -> (user.getTotalEarnings() != null ? user.getTotalEarnings() : 0L) * 5 / 100)
                .sum();
        } catch (Exception e) {
            log.warn("计算用户邀请收益失败: userId={}, error={}", userId, e.getMessage());
            return 0L;
        }
    }
}


