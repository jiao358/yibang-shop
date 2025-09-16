package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.AddressCreateRequest;
import com.yibang.taskmall.dto.request.AddressUpdateRequest;
import com.yibang.taskmall.dto.response.AddressDTO;
import com.yibang.taskmall.security.JwtTokenProvider;
import com.yibang.taskmall.service.UserAddressService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址管理控制器
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
@Tag(name = "地址管理", description = "用户收货地址相关接口")
public class AddressController {

    private final UserAddressService userAddressService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 获取用户地址列表
     */
    @GetMapping
    @Operation(summary = "获取地址列表", description = "获取当前用户的收货地址列表")
    public Result<List<AddressDTO>> getAddressList(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("获取用户地址列表: userId={}", userId);
        
        List<AddressDTO> addresses = userAddressService.getUserAddressList(userId);
        return Result.success(addresses);
    }

    /**
     * 获取地址详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取地址详情", description = "根据ID获取收货地址详情")
    public Result<AddressDTO> getAddressById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("获取地址详情: id={}, userId={}", id, userId);
        
        try {
            AddressDTO address = userAddressService.getAddressById(id, userId);
            return Result.success(address);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 创建新地址
     */
    @PostMapping
    @Operation(summary = "创建地址", description = "创建新的收货地址")
    public Result<AddressDTO> createAddress(@Valid @RequestBody AddressCreateRequest request, 
                                          HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        log.info("创建地址: userId={}, request={}", userId, request);
        
        try {
            AddressDTO address = userAddressService.createAddress(userId, request);
            return Result.success(address);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新地址
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新地址", description = "更新收货地址信息")
    public Result<AddressDTO> updateAddress(@PathVariable Long id, 
                                          @Valid @RequestBody AddressUpdateRequest request,
                                          HttpServletRequest httpRequest) {
        Long userId = getCurrentUserId(httpRequest);
        log.info("更新地址: id={}, userId={}, request={}", id, userId, request);
        
        try {
            AddressDTO address = userAddressService.updateAddress(id, userId, request);
            return Result.success(address);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除地址
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除地址", description = "删除收货地址")
    public Result<Void> deleteAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("删除地址: id={}, userId={}", id, userId);
        
        try {
            userAddressService.deleteAddress(id, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 设置默认地址
     */
    @PutMapping("/{id}/default")
    @Operation(summary = "设置默认地址", description = "将指定地址设为默认地址")
    public Result<Void> setDefaultAddress(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        log.info("设置默认地址: id={}, userId={}", id, userId);
        
        try {
            userAddressService.setDefaultAddress(id, userId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从JWT Token中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token == null) {
            throw new RuntimeException("未找到有效的认证信息");
        }
        
        try {
            Claims claims = jwtTokenProvider.parseToken(token);
            Object userIdObj = claims.get("userId");
            if (userIdObj instanceof Integer) {
                return ((Integer) userIdObj).longValue();
            } else if (userIdObj instanceof Long) {
                return (Long) userIdObj;
            } else {
                throw new RuntimeException("无效的用户ID格式");
            }
        } catch (Exception e) {
            log.error("解析JWT Token失败", e);
            throw new RuntimeException("认证信息无效");
        }
    }

    /**
     * 从请求中提取JWT Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}