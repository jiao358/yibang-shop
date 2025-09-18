package com.yibang.taskmall.bkcontroller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.UserQueryRequest;
import com.yibang.taskmall.bkdto.request.UserBalanceUpdateRequest;
import com.yibang.taskmall.bkdto.request.UserLevelUpdateRequest;
import com.yibang.taskmall.bkdto.response.UserDetailResponse;
import com.yibang.taskmall.bkdto.response.UserListResponse;
import com.yibang.taskmall.bkdto.response.UserStatsResponse;
import com.yibang.taskmall.bkservice.UserBkService;
import com.yibang.taskmall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 后台用户管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/bk/users")
@RequiredArgsConstructor
@Tag(name = "后台用户管理", description = "用户信息管理、余额调整、等级管理")
public class UserBkController {

    private final UserBkService userBkService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 分页查询用户列表
     */
    @GetMapping
    @Operation(summary = "分页查询用户列表", description = "支持按昵称、手机号、等级等条件筛选")
    public Result<Page<UserListResponse>> getUserList(
            @ModelAttribute UserQueryRequest request,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        
        log.info("查询用户列表: request={}, page={}, size={}", request, page, size);
        
        try {
            // TODO: 分页优化 - 使用覆盖索引 idx_user_level、idx_status、idx_created_at，只查询列表必要字段
            // TODO: Redis缓存 - 键名: bk.users:list:{参数哈希}:{page}:{size}, TTL: 3分钟，防穿透保护
            
            String cacheKey = String.format("bk.users:list:%s:%d:%d", 
                request.toString().hashCode(), page, size);
            Page<UserListResponse> cached = (Page<UserListResponse>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("从缓存获取用户列表");
                return Result.success(cached);
            }
            
            Page<UserListResponse> result = userBkService.getUserList(request, page, size);
            
            // 缓存结果3分钟（用户数据变化较频繁）
            redisTemplate.opsForValue().set(cacheKey, result, 3, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("查询用户列表失败", e);
            return Result.error("查询用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{userId}")
    @Operation(summary = "获取用户详情", description = "获取用户完整信息，包括余额、任务统计等")
    public Result<UserDetailResponse> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        log.info("获取用户详情: userId={}", userId);
        
        try {
            // TODO: Redis缓存 - 键名: bk.user:detail:{userId}, TTL: 5分钟
            String cacheKey = "bk.user:detail:" + userId;
            UserDetailResponse cached = (UserDetailResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            UserDetailResponse result = userBkService.getUserDetail(userId);
            
            // 缓存结果5分钟
            redisTemplate.opsForValue().set(cacheKey, result, 5, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户详情失败: userId={}", userId, e);
            return Result.error("获取用户详情失败: " + e.getMessage());
        }
    }

    /**
     * 调整用户余额
     */
    @PostMapping("/{userId}/balance")
    @Operation(summary = "调整用户余额", description = "管理员调整用户余额（增加/减少）")
    public Result<Void> updateUserBalance(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Validated @RequestBody UserBalanceUpdateRequest request) {
        
        log.info("调整用户余额: userId={}, request={}", userId, request);
        
        try {
            // TODO: 权限检查 - 需要财务管理权限
            // TODO: 操作审计 - 记录余额调整日志，包含操作人、原因、金额变化
            
            userBkService.updateUserBalance(userId, request);
            
            // 清除相关缓存
            clearUserCache(userId);
            clearUserListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("调整用户余额失败: userId={}", userId, e);
            return Result.error("调整用户余额失败: " + e.getMessage());
        }
    }

    /**
     * 更新用户等级
     */
    @PostMapping("/{userId}/level")
    @Operation(summary = "更新用户等级", description = "管理员调整用户等级")
    public Result<Void> updateUserLevel(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Validated @RequestBody UserLevelUpdateRequest request) {
        
        log.info("更新用户等级: userId={}, request={}", userId, request);
        
        try {
            // TODO: 权限检查 - 需要用户管理权限
            // TODO: 操作审计 - 记录等级变更日志
            
            userBkService.updateUserLevel(userId, request);
            
            // 清除相关缓存
            clearUserCache(userId);
            clearUserListCache();
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("更新用户等级失败: userId={}", userId, e);
            return Result.error("更新用户等级失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    @Operation(summary = "获取用户统计数据", description = "获取用户相关统计信息")
    public Result<UserStatsResponse> getUserStats(
            @Parameter(description = "统计类型") @RequestParam(defaultValue = "overview") String type,
            @Parameter(description = "开始日期") @RequestParam(required = false) String startDate,
            @Parameter(description = "结束日期") @RequestParam(required = false) String endDate) {
        
        log.info("获取用户统计: type={}, startDate={}, endDate={}", type, startDate, endDate);
        
        try {
            // TODO: Redis缓存 - 键名: bk.users:stats:{type}:{startDate}:{endDate}, TTL: 15分钟
            String cacheKey = String.format("bk.users:stats:%s:%s:%s", type, startDate, endDate);
            UserStatsResponse cached = (UserStatsResponse) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            UserStatsResponse result = userBkService.getUserStats(type, startDate, endDate);
            
            // 缓存结果15分钟
            redisTemplate.opsForValue().set(cacheKey, result, 15, TimeUnit.MINUTES);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            return Result.error("获取用户统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户等级选项
     */
    @GetMapping("/levels")
    @Operation(summary = "获取用户等级选项", description = "获取所有可用的用户等级")
    public Result<Map<String, Object>> getUserLevels() {
        log.info("获取用户等级选项");
        
        try {
            // TODO: Redis缓存 - 键名: bk.users:levels, TTL: 1小时
            String cacheKey = "bk.users:levels";
            Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return Result.success(cached);
            }
            
            Map<String, Object> result = userBkService.getUserLevels();
            
            // 缓存结果1小时
            redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
            
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("获取用户等级选项失败", e);
            return Result.error("获取用户等级选项失败: " + e.getMessage());
        }
    }

    /**
     * 清除用户详情缓存
     */
    private void clearUserCache(Long userId) {
        String cacheKey = "bk.user:detail:" + userId;
        redisTemplate.delete(cacheKey);
    }

    /**
     * 清除用户列表缓存
     */
    private void clearUserListCache() {
        // 使用模式匹配删除所有用户列表缓存
        redisTemplate.delete(redisTemplate.keys("bk.users:list:*"));
        redisTemplate.delete("bk.users:stats:*");
    }
}
