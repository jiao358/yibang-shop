package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.request.AddressCreateRequest;
import com.yibang.taskmall.dto.request.AddressUpdateRequest;
import com.yibang.taskmall.dto.response.AddressDTO;
import com.yibang.taskmall.entity.UserAddress;

import java.util.List;

/**
 * 用户收货地址服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface UserAddressService {

    /**
     * 获取用户地址列表（带缓存）
     * 
     * @param userId 用户ID
     * @return 地址列表
     */
    List<AddressDTO> getUserAddressList(Long userId);

    /**
     * 根据ID获取地址详情（带缓存）
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     * @return 地址详情
     */
    AddressDTO getAddressById(Long addressId, Long userId);

    /**
     * 创建新地址
     * 
     * @param userId 用户ID
     * @param request 创建请求
     * @return 创建的地址
     */
    AddressDTO createAddress(Long userId, AddressCreateRequest request);

    /**
     * 更新地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     * @param request 更新请求
     * @return 更新后的地址
     */
    AddressDTO updateAddress(Long addressId, Long userId, AddressUpdateRequest request);

    /**
     * 删除地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     */
    void deleteAddress(Long addressId, Long userId);

    /**
     * 设置默认地址
     * 
     * @param addressId 地址ID
     * @param userId 用户ID（验证权限）
     */
    void setDefaultAddress(Long addressId, Long userId);

    /**
     * 清除用户地址缓存
     * 
     * @param userId 用户ID
     */
    void clearUserAddressCache(Long userId);
}
