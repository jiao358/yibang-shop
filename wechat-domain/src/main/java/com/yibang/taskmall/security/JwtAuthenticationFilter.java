package com.yibang.taskmall.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        log.debug("JWT认证过滤器处理请求: {}", request.getRequestURI());

        try {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.debug("Authorization header: {}", authHeader != null ? "Bearer ***" : "null");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                log.debug("提取JWT token: {}...", token.length() > 10 ? token.substring(0, 10) : token);
                
                try {
                    Claims claims = jwtTokenProvider.parseToken(token);
                    if (claims != null) {
                        Object userIdObj = claims.get("userId");
                        String openid = claims.get("openid", String.class);
                        
                        Long userId;
                        if (userIdObj instanceof Integer) {
                            userId = ((Integer) userIdObj).longValue();
                        } else if (userIdObj instanceof Long) {
                            userId = (Long) userIdObj;
                        } else {
                            log.warn("无效的userId类型: {}", userIdObj.getClass());
                            filterChain.doFilter(request, response);
                            return;
                        }
                        
                        log.debug("JWT解析成功: userId={}, openid={}", userId, openid);
                        
                        User principal = new User(String.valueOf(userId), "", java.util.Collections.emptyList());
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        log.debug("JWT认证成功，设置SecurityContext: userId={}", userId);
                    }
                } catch (Exception e) {
                    log.warn("JWT token解析失败: {}", e.getMessage());
                }
            } else {
                log.debug("没有找到Authorization header或格式不正确");
            }
        } catch (Exception e) {
            log.error("JWT认证过滤器异常: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
