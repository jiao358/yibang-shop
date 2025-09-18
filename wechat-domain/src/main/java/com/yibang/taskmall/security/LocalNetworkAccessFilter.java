package com.yibang.taskmall.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * 局域网访问过滤器
 * 只允许局域网内的IP访问HSF服务
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
// @Component  // 暂时禁用，使用 API Key 认证
public class LocalNetworkAccessFilter implements Filter {

    @Value("${hsf.security.allowed-networks:192.168.0.0/16,10.0.0.0/8,172.16.0.0/12,127.0.0.1/32}")
    private String allowedNetworks;

    private List<String> allowedNetworkList;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 解析允许的网络段
        allowedNetworkList = Arrays.asList(allowedNetworks.split(","));
        log.info("HSF服务允许的网络段: {}", allowedNetworkList);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestPath = httpRequest.getRequestURI();
        
        // 只对HSF服务进行IP限制
        if (requestPath.startsWith("/api/hsf/")) {
            String clientIp = getClientIpAddress(httpRequest);
            
            if (!isAllowedIp(clientIp)) {
                log.warn("拒绝非局域网IP访问HSF服务: {} from {}", requestPath, clientIp);
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"code\":403,\"message\":\"只允许局域网内访问\",\"data\":null}");
                return;
            }
            
            log.debug("允许局域网IP访问HSF服务: {} from {}", requestPath, clientIp);
        }
        
        chain.doFilter(request, response);
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
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
     * 检查IP是否在允许的网络段内
     */
    private boolean isAllowedIp(String clientIp) {
        try {
            InetAddress clientAddress = InetAddress.getByName(clientIp);
            
            for (String network : allowedNetworkList) {
                if (isIpInNetwork(clientAddress, network.trim())) {
                    return true;
                }
            }
            
            return false;
        } catch (UnknownHostException e) {
            log.warn("无法解析客户端IP: {}", clientIp);
            return false;
        }
    }

    /**
     * 检查IP是否在指定网络段内
     */
    private boolean isIpInNetwork(InetAddress clientAddress, String network) {
        try {
            if (network.contains("/")) {
                // CIDR格式: 192.168.1.0/24
                String[] parts = network.split("/");
                InetAddress networkAddress = InetAddress.getByName(parts[0]);
                int prefixLength = Integer.parseInt(parts[1]);
                
                return isIpInCidr(clientAddress, networkAddress, prefixLength);
            } else {
                // 单个IP: 192.168.1.100
                InetAddress networkAddress = InetAddress.getByName(network);
                return clientAddress.equals(networkAddress);
            }
        } catch (Exception e) {
            log.warn("解析网络段失败: {}", network);
            return false;
        }
    }

    /**
     * 检查IP是否在CIDR网络段内
     */
    private boolean isIpInCidr(InetAddress clientAddress, InetAddress networkAddress, int prefixLength) {
        byte[] clientBytes = clientAddress.getAddress();
        byte[] networkBytes = networkAddress.getAddress();
        
        if (clientBytes.length != networkBytes.length) {
            return false;
        }
        
        int bytesToCheck = prefixLength / 8;
        int bitsToCheck = prefixLength % 8;
        
        // 检查完整字节
        for (int i = 0; i < bytesToCheck; i++) {
            if (clientBytes[i] != networkBytes[i]) {
                return false;
            }
        }
        
        // 检查部分字节
        if (bitsToCheck > 0) {
            int mask = 0xFF << (8 - bitsToCheck);
            return (clientBytes[bytesToCheck] & mask) == (networkBytes[bytesToCheck] & mask);
        }
        
        return true;
    }

    @Override
    public void destroy() {
        // 清理资源
    }
}
