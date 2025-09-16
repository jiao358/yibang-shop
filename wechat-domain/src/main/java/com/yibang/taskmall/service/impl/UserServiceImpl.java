package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.mapper.UserMapper;
import com.yibang.taskmall.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_CACHE_KEY = "wx.user:info:openid:%s";
    private static final String USER_ID_CACHE_KEY = "wx.user:info:id:%d";
    private static final int USER_CACHE_HOURS = 2; // 用户信息缓存2小时

    @Override
    public User findByOpenid(String openid) {
        // 先从缓存获取
        String cacheKey = String.format(USER_CACHE_KEY, openid);
        User cached = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存获取用户信息: openid={}", openid);
            return cached;
        }

        // 从数据库查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getOpenid, openid);
        User user = userMapper.selectOne(wrapper);
        
        if (user != null) {
            // 缓存用户信息（同时使用openid和userId作为key）
            redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
            String idCacheKey = String.format(USER_ID_CACHE_KEY, user.getId());
            redisTemplate.opsForValue().set(idCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
            log.debug("缓存用户信息: openid={}, userId={}", openid, user.getId());
        }
        
        return user;
    }

    @Override
    public User createUser(String openid, String nickname, String avatar, String phone) {
        User user = new User();
        user.setOpenid(openid);
        user.setNickname(nickname);
        user.setAvatar(avatar);
        user.setPhone(phone);
        user.setUserLevel("normal");
        user.setTotalEarnings(0L);
        user.setAvailableBalance(0L);
        user.setFrozenBalance(0L);
        user.setTotalTasks(0);
        user.setInviteCode(generateInviteCode());
        user.setInviteCount(0);
        user.setLastLoginTime(LocalDateTime.now());
        user.setStatus("active");
        user.setGender(0);

        userMapper.insert(user);
        log.info("创建新用户成功: openid={}, userId={}", openid, user.getId());
        
        // 缓存新用户信息
        String openidCacheKey = String.format(USER_CACHE_KEY, openid);
        String idCacheKey = String.format(USER_ID_CACHE_KEY, user.getId());
        redisTemplate.opsForValue().set(openidCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
        redisTemplate.opsForValue().set(idCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
        
        return user;
    }

    @Override
    public User updateLoginInfo(User user, String nickname, String avatar, String phone) {
        boolean needUpdate = false;

        if (nickname != null && !nickname.equals(user.getNickname())) {
            user.setNickname(nickname);
            needUpdate = true;
        }

        if (avatar != null && !avatar.equals(user.getAvatar())) {
            user.setAvatar(avatar);
            needUpdate = true;
        }

        if (phone != null && !phone.equals(user.getPhone())) {
            user.setPhone(phone);
            needUpdate = true;
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        needUpdate = true;

        if (needUpdate) {
            userMapper.updateById(user);
            log.info("更新用户登录信息: userId={}", user.getId());
            
            // 更新缓存
            String openidCacheKey = String.format(USER_CACHE_KEY, user.getOpenid());
            String idCacheKey = String.format(USER_ID_CACHE_KEY, user.getId());
            redisTemplate.opsForValue().set(openidCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
            redisTemplate.opsForValue().set(idCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
        }

        return user;
    }

    @Override
    public String generateInviteCode() {
        // 生成6位数字邀请码
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        
        // 检查邀请码是否已存在，如果存在则重新生成
        String inviteCode = code.toString();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getInviteCode, inviteCode);
        if (userMapper.selectCount(wrapper) > 0) {
            return generateInviteCode(); // 递归重新生成
        }
        
        return inviteCode;
    }

    @Override
    public User findById(Long userId) {
        // 先从缓存获取
        String cacheKey = String.format(USER_ID_CACHE_KEY, userId);
        User cached = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("从缓存获取用户信息: userId={}", userId);
            return cached;
        }

        // 从数据库查询
        User user = userMapper.selectById(userId);
        
        if (user != null) {
            // 缓存用户信息（同时使用openid和userId作为key）
            redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
            String openidCacheKey = String.format(USER_CACHE_KEY, user.getOpenid());
            redisTemplate.opsForValue().set(openidCacheKey, user, USER_CACHE_HOURS, TimeUnit.HOURS);
            log.debug("缓存用户信息: userId={}, openid={}", userId, user.getOpenid());
        }
        
        return user;
    }
}
