package com.yibang.taskmall.security;

import com.yibang.taskmall.service.AuthService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * API Key 认证过滤器
 * 用于内部系统调用 HSF 服务的认证
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Component
@Order(50)
public class ApiKeyAuthenticationFilter implements Filter, Ordered {

    @Value("${hsf.security.api-keys:yibang-erp:erp-2024-abc123def456,other-system:other-2024-xyz789}")
    private String apiKeysConfig;

    @Autowired
    private AuthService authService;

    private Map<String, String> validApiKeys;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 解析配置的 API Keys
        validApiKeys = parseApiKeys(apiKeysConfig);
        log.info("API Key 认证过滤器初始化完成，已配置 {} 个内部系统", validApiKeys.size());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        
        // 只对 HSF 服务进行 API Key 认证
        if (requestPath.startsWith("/api/hsf/")) {
            String apiKey = httpRequest.getHeader("X-API-Key");
            
            if (apiKey == null || apiKey.trim().isEmpty()) {
                log.warn("HSF 服务调用缺少 API Key: {}", requestPath);
                sendErrorResponse(httpResponse, 401, "缺少 API Key");
                return;
            }
            
            String systemName = validateApiKey(apiKey);
            if (systemName == null) {
                log.warn("无效的 API Key: {} from {}", apiKey, getClientIp(httpRequest));
                sendErrorResponse(httpResponse, 401, "无效的 API Key");
                return;
            }
            
            // 设置认证状态，跳过 JWT 认证
            setAuthentication(systemName, apiKey);
           /* String token = authService.generateToken(0L, "0");
            ((HttpServletResponse) response).setHeader("Authorization", "Bearer " + token);
*/
            log.info("内部系统 {} 成功调用 HSF 服务: {}", systemName, requestPath);
            chain.doFilter(request, response);
            return ;
        }
        
        chain.doFilter(request, response);
    }

    /**
     * 验证 API Key
     */
    private String validateApiKey(String apiKey) {
        for (Map.Entry<String, String> entry : validApiKeys.entrySet()) {
            if (apiKey.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 设置认证状态
     */
    private void setAuthentication(String systemName, String apiKey) {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_INTERNAL_SYSTEM"),
            new SimpleGrantedAuthority("ROLE_HSF_ACCESS")
        );
        
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(systemName, apiKey, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * 解析 API Keys 配置
     */
    private Map<String, String> parseApiKeys(String config) {
        Map<String, String> apiKeys = new java.util.HashMap<>();
        
        if (config == null || config.trim().isEmpty()) {
            return apiKeys;
        }
        
        // 解析格式: system1:key1,system2:key2
        String[] pairs = config.split(",");
        for (String pair : pairs) {
            String[] keyValue = pair.trim().split(":");
            if (keyValue.length == 2) {
                apiKeys.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        
        return apiKeys;
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        String errorResponse = String.format(
            "{\"code\":%d,\"message\":\"%s\",\"data\":null,\"timestamp\":%d}",
            status, message, System.currentTimeMillis()
        );
        
        response.getWriter().write(errorResponse);
    }

    @Override
    public void destroy() {
        // 清理资源
    }

    @Override
    public int getOrder() {
        return 50; // API Key过滤器在JWT过滤器之前执行
    }
}
