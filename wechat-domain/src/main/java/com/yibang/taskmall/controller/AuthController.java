package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.WechatLoginRequest;
import com.yibang.taskmall.dto.request.RefreshTokenRequest;
import com.yibang.taskmall.dto.response.LoginResponse;
import com.yibang.taskmall.dto.response.TokenResponse;
import com.yibang.taskmall.dto.response.WechatUserInfo;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.service.AuthService;
import com.yibang.taskmall.service.UserService;
import com.yibang.taskmall.service.WechatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {

    private final AuthService authService;
    private final WechatService wechatService;
    private final UserService userService;
    private final StringRedisTemplate stringRedisTemplate;

    private static final String USER_CACHE_KEY_FMT = "user:info:%d";
    private static final long USER_CACHE_TTL_MINUTES = 30; // 用户信息缓存30分钟
    private static final String JWT_BLACKLIST_KEY_FMT = "jwt:blacklist:%s";
    private static final long JWT_BLACKLIST_TTL_SECONDS = 7200; // 与发放token时长同步（秒）

    @PostMapping("/wechat-login")
    @Operation(summary = "微信登录", description = "通过微信授权码进行登录")
    public Result<LoginResponse> wechatLogin(@RequestBody WechatLoginRequest request) {
        log.info("微信登录请求: {}", request);
        try {
            WechatUserInfo wechatUserInfo = wechatService.getUserInfoByCode(request.getCode());
            String phoneNumber = null;
            if (request.getPhoneCode() != null && !request.getPhoneCode().isEmpty()) {
                try {
                    phoneNumber = wechatService.getPhoneNumberByCode(request.getPhoneCode());
                    log.info("获取到用户手机号: {}", phoneNumber);
                } catch (Exception e) {
                    log.warn("获取手机号失败，继续登录流程: {}", e.getMessage());
                }
            }
            User user = userService.findByOpenid(wechatUserInfo.getOpenid());
            if (user == null) {
                user = userService.createUser(
                    wechatUserInfo.getOpenid(),
                    request.getNickname(),
                    request.getAvatar(),
                    phoneNumber
                );
                log.info("创建新用户: openid={}, userId={}", wechatUserInfo.getOpenid(), user.getId());
            } else {
                user = userService.updateLoginInfo(user, request.getNickname(), request.getAvatar(), phoneNumber);
                log.info("更新用户信息: userId={}", user.getId());
            }
            // 缓存用户信息
            cacheUser(user);

            String token = authService.generateToken(user.getId(), wechatUserInfo.getOpenid());
            LoginResponse response = new LoginResponse();
            response.setAccessToken(token);
            response.setRefreshToken("");
            response.setTokenType("Bearer");
            response.setExpiresIn(7200L);
            response.setUserId(user.getId());
            response.setNickname(user.getNickname() != null ? user.getNickname() : "微信用户");
            response.setAvatar(user.getAvatar() != null ? user.getAvatar() : "");
            response.setUserLevel(user.getUserLevel());
            return Result.success(response);
        } catch (Exception e) {
            log.error("微信登录失败", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新Token", description = "使用刷新令牌获取新的访问令牌")
    public Result<TokenResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        log.info("刷新Token请求: {}", request);
        return Result.success(new TokenResponse());
    }

    @GetMapping("/user-info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的基本信息")
    public Result<Map<String, Object>> getUserInfo() {
        log.info("获取用户信息请求");
        try {
            Long userId;
            try {
                userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
            } catch (Exception e) {
                return Result.success(new HashMap<>());
            }
            // 先查缓存
            String cacheKey = userCacheKey(userId);
            Map<String, Object> data = null;
            try {
                String cached = stringRedisTemplate.opsForValue().get(cacheKey);
                if (StringUtils.hasText(cached)) {
                    // 简单返回占位（前端字段以实时接口为准）。如需严格反序列化，可改为JSON工具
                    data = new HashMap<>();
                    data.put("cached", true);
                }
            } catch (Exception ignored) {}
            if (data == null) {
                User user = userService.findById(userId);
                if (user == null) {
                    return Result.success(new HashMap<>());
                }
                data = buildUserMap(user);
                cacheUser(user);
            }
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录，加入JWT黑名单")
    public Result<Void> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
            String jwt = auth.substring(7);
            try {
                String key = jwtBlacklistKey(jwt);
                stringRedisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(JWT_BLACKLIST_TTL_SECONDS));
                log.info("JWT已加入黑名单: {}", key);
            } catch (Exception e) {
                log.warn("加入JWT黑名单失败: {}", e.getMessage());
            }
        }
        return Result.success();
    }

    private void cacheUser(User user) {
        try {
            String key = userCacheKey(user.getId());
            // 只缓存必要字段（简化示例）
            Map<String, String> m = new HashMap<>();
            m.put("id", String.valueOf(user.getId()));
            m.put("nickname", user.getNickname() == null ? "" : user.getNickname());
            m.put("avatar", user.getAvatar() == null ? "" : user.getAvatar());
            m.put("userLevel", String.valueOf(user.getUserLevel() == null ? 0 : user.getUserLevel()));
            stringRedisTemplate.opsForHash().putAll(key, m);
            stringRedisTemplate.expire(key, USER_CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("缓存用户信息失败: {}", e.getMessage());
        }
    }

    private Map<String, Object> buildUserMap(User user) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("userLevel", user.getUserLevel());
        data.put("balance", user.getAvailableBalance());
        data.put("frozenBalance", user.getFrozenBalance());
        return data;
    }

    private String userCacheKey(Long userId) { return String.format(USER_CACHE_KEY_FMT, userId); }
    private String jwtBlacklistKey(String jwt) { return String.format(JWT_BLACKLIST_KEY_FMT, jwt); }
}
