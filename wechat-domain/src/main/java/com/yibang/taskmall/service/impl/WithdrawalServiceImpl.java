package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.request.WithdrawalRequest;
import com.yibang.taskmall.dto.response.WithdrawalResponse;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.service.WithdrawalService;
import com.yibang.taskmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 提现服务实现类
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean applyWithdrawal(WithdrawalRequest request) {
        log.info("申请提现: {}", request);
        
        // 1. 验证用户信息
        User user = userService.findById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 2. 验证提现金额
        Integer requestAmount = request.getAmount(); // 分
        if (requestAmount == null || requestAmount <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        
        // 3. 检查最低提现金额（假设最低10元 = 1000分）
        if (requestAmount < 1000) {
            throw new RuntimeException("提现金额不能少于10元");
        }
        
        // 4. 验证用户余额（可用余额 = 总余额 - 冻结金额）
        Long availableBalance = user.getAvailableBalance() - user.getFrozenBalance();
        if (availableBalance < requestAmount) {
            throw new RuntimeException("可提现余额不足");
        }
        
        // 5. 检查用户当日提现次数限制（通过Redis缓存）
        String dailyLimitKey = String.format("wx.withdraw:daily:%d:%s", 
            request.getUserId(), 
            java.time.LocalDate.now().toString());
        
        Integer dailyCount = (Integer) redisTemplate.opsForValue().get(dailyLimitKey);
        if (dailyCount != null && dailyCount >= 3) { // 假设每日最多3次
            throw new RuntimeException("今日提现次数已达上限");
        }
        
        // 6. 冻结提现金额（从可用余额转到冻结余额）
        user.setAvailableBalance(user.getAvailableBalance() - requestAmount);
        user.setFrozenBalance(user.getFrozenBalance() + requestAmount);
        userService.updateLoginInfo(user, null, null, null); // 更新用户余额
        
        // 7. 创建提现记录
        // TODO: 这里应该创建提现记录到withdrawals表
        
        // 8. 更新Redis缓存（当日提现次数）
        redisTemplate.opsForValue().set(dailyLimitKey, (dailyCount == null ? 0 : dailyCount) + 1, 1, TimeUnit.DAYS);
        
        // 9. 清除用户余额相关缓存
        String userCacheKey = "wx.user:info:id:" + request.getUserId();
        redisTemplate.delete(userCacheKey);
        
        log.info("提现申请成功: userId={}, amount={}", request.getUserId(), requestAmount);
        return true;
    }

    @Override
    public Page<WithdrawalResponse> getWithdrawals(Long userId, String status, Integer page, Integer size) {
        log.info("获取提现记录: userId={}, status={}, page={}, size={}", userId, status, page, size);
        
        // TODO: 实现获取提现记录逻辑
        // 1. 根据状态筛选提现记录
        // 2. 分页返回结果
        
        return new Page<>(page, size);
    }

    @Override
    public WithdrawalResponse getWithdrawalDetail(Long withdrawalId) {
        log.info("获取提现详情: withdrawalId={}", withdrawalId);
        
        // TODO: 实现获取提现详情逻辑
        // 1. 验证用户权限
        // 2. 查询提现详情
        // 3. 返回提现信息
        
        return new WithdrawalResponse();
    }

    @Override
    public Boolean cancelWithdrawal(Long withdrawalId) {
        log.info("取消提现: withdrawalId={}", withdrawalId);
        
        // TODO: 实现取消提现逻辑
        // 1. 验证提现状态
        // 2. 更新提现状态为已取消
        // 3. 退还提现金额到用户余额
        
        return true;
    }

    @Override
    public Object getWithdrawalConfig(Long userId) {
        log.info("获取提现配置: userId={}", userId);
        
        // 先从Redis缓存获取
        String configKey = "wx.withdraw:config:" + userId;
        Object cached = redisTemplate.opsForValue().get(configKey);
        if (cached != null) {
            return cached;
        }
        
        // 根据用户等级返回提现配置
        User user = userService.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 构建提现配置
        java.util.Map<String, Object> config = new java.util.HashMap<>();
        config.put("minAmount", 10); // 最低提现金额（元）
        config.put("maxAmount", 10000); // 最高提现金额（元）
        config.put("feeRate", 0.6); // 手续费率（%）
        config.put("dailyLimit", 3); // 每日提现次数限制
        config.put("quickAmounts", java.util.Arrays.asList(10, 50, 100, 200, 500)); // 快捷金额
        config.put("workingDays", "1-3个工作日"); // 到账时间
        
        // 根据用户等级调整配置
        if ("vip".equals(user.getUserLevel())) {
            config.put("feeRate", 0.3); // VIP用户手续费更低
            config.put("dailyLimit", 5); // VIP用户每日限制更高
        }
        
        // 缓存配置30分钟
        redisTemplate.opsForValue().set(configKey, config, 30, TimeUnit.MINUTES);
        
        return config;
    }

    @Override
    public Object calculateWithdrawalFee(Long userId, Integer amount) {
        log.info("计算提现手续费: userId={}, amount={}", userId, amount);
        
        if (amount == null || amount <= 0) {
            throw new RuntimeException("提现金额必须大于0");
        }
        
        // 获取提现配置
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> config = (java.util.Map<String, Object>) getWithdrawalConfig(userId);
        Double feeRate = (Double) config.get("feeRate");
        
        // 计算手续费（分）
        Integer feeAmount = (int) Math.round(amount * feeRate / 100);
        
        // 返回计算结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("amount", amount); // 提现金额（分）
        result.put("feeRate", feeRate); // 手续费率（%）
        result.put("feeAmount", feeAmount); // 手续费（分）
        result.put("actualAmount", amount - feeAmount); // 实际到账金额（分）
        result.put("amountYuan", String.format("%.2f", amount / 100.0)); // 提现金额（元）
        result.put("feeAmountYuan", String.format("%.2f", feeAmount / 100.0)); // 手续费（元）
        result.put("actualAmountYuan", String.format("%.2f", (amount - feeAmount) / 100.0)); // 实际到账（元）
        
        return result;
    }
}
