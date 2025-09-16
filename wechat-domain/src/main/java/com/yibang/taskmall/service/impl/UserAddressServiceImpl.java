package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yibang.taskmall.dto.request.AddressCreateRequest;
import com.yibang.taskmall.dto.request.AddressUpdateRequest;
import com.yibang.taskmall.dto.response.AddressDTO;
import com.yibang.taskmall.entity.UserAddress;
import com.yibang.taskmall.mapper.UserAddressMapper;
import com.yibang.taskmall.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户收货地址服务实现
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressMapper userAddressMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String USER_ADDRESS_LIST_KEY = "wx.user:address:list:%d";
    private static final String USER_ADDRESS_DETAIL_KEY = "wx.user:address:detail:%d";
    private static final int ADDRESS_LIST_CACHE_MINUTES = 60; // 地址列表缓存1小时
    private static final int ADDRESS_DETAIL_CACHE_MINUTES = 30; // 地址详情缓存30分钟

    @Override
    public List<AddressDTO> getUserAddressList(Long userId) {
        // 先从缓存获取
        String cacheKey = String.format(USER_ADDRESS_LIST_KEY, userId);
        
        @SuppressWarnings("unchecked")
        List<AddressDTO> cachedList = (List<AddressDTO>) redisTemplate.opsForValue().get(cacheKey);
        if (cachedList != null) {
            log.debug("从缓存获取用户地址列表: userId={}", userId);
            return cachedList;
        }

        // 从数据库查询
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .orderByDesc(UserAddress::getIsDefault)
               .orderByDesc(UserAddress::getUpdatedAt);
        
        List<UserAddress> addresses = userAddressMapper.selectList(wrapper);
        List<AddressDTO> result = addresses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, ADDRESS_LIST_CACHE_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存用户地址列表: userId={}, count={}", userId, result.size());

        return result;
    }

    @Override
    public AddressDTO getAddressById(Long addressId, Long userId) {
        // 先从缓存获取
        String cacheKey = String.format(USER_ADDRESS_DETAIL_KEY, addressId);
        
        AddressDTO cached = (AddressDTO) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null && cached.getUserId().equals(userId)) {
            log.debug("从缓存获取地址详情: addressId={}", addressId);
            return cached;
        }

        // 从数据库查询
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        AddressDTO result = convertToDTO(address);
        
        // 缓存结果
        redisTemplate.opsForValue().set(cacheKey, result, ADDRESS_DETAIL_CACHE_MINUTES, TimeUnit.MINUTES);
        log.debug("缓存地址详情: addressId={}", addressId);

        return result;
    }

    @Override
    @Transactional
    public AddressDTO createAddress(Long userId, AddressCreateRequest request) {
        UserAddress address = new UserAddress();
        BeanUtils.copyProperties(request, address);
        address.setUserId(userId);
        
        // 生成完整地址
        String fullAddress = request.getProvince() + request.getCity() + request.getDistrict() + request.getDetailAddress();
        address.setFullAddress(fullAddress);

        // 如果设为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearOtherDefaultAddress(userId);
        }

        userAddressMapper.insert(address);
        log.info("创建用户地址: userId={}, addressId={}", userId, address.getId());

        // 清除相关缓存
        clearUserAddressCache(userId);

        return convertToDTO(address);
    }

    @Override
    @Transactional
    public AddressDTO updateAddress(Long addressId, Long userId, AddressUpdateRequest request) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        BeanUtils.copyProperties(request, address);
        
        // 生成完整地址
        String fullAddress = request.getProvince() + request.getCity() + request.getDistrict() + request.getDetailAddress();
        address.setFullAddress(fullAddress);

        // 如果设为默认地址，先取消其他默认地址
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearOtherDefaultAddress(userId);
        }

        userAddressMapper.updateById(address);
        log.info("更新用户地址: userId={}, addressId={}", userId, addressId);

        // 清除相关缓存
        clearUserAddressCache(userId);
        clearAddressDetailCache(addressId);

        return convertToDTO(address);
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        userAddressMapper.deleteById(addressId);
        log.info("删除用户地址: userId={}, addressId={}", userId, addressId);

        // 清除相关缓存
        clearUserAddressCache(userId);
        clearAddressDetailCache(addressId);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        UserAddress address = userAddressMapper.selectById(addressId);
        if (address == null || !address.getUserId().equals(userId)) {
            throw new RuntimeException("地址不存在或无权限访问");
        }

        // 先取消其他默认地址
        clearOtherDefaultAddress(userId);

        // 设置当前地址为默认
        address.setIsDefault(true);
        userAddressMapper.updateById(address);
        log.info("设置默认地址: userId={}, addressId={}", userId, addressId);

        // 清除相关缓存
        clearUserAddressCache(userId);
        clearAddressDetailCache(addressId);
    }

    @Override
    public void clearUserAddressCache(Long userId) {
        String listCacheKey = String.format(USER_ADDRESS_LIST_KEY, userId);
        redisTemplate.delete(listCacheKey);
        log.debug("清除用户地址列表缓存: userId={}", userId);
    }

    private void clearAddressDetailCache(Long addressId) {
        String detailCacheKey = String.format(USER_ADDRESS_DETAIL_KEY, addressId);
        redisTemplate.delete(detailCacheKey);
        log.debug("清除地址详情缓存: addressId={}", addressId);
    }

    private void clearOtherDefaultAddress(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .eq(UserAddress::getIsDefault, true)
               .set(UserAddress::getIsDefault, false);
        userAddressMapper.update(null, wrapper);
    }

    private AddressDTO convertToDTO(UserAddress address) {
        AddressDTO dto = new AddressDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }
}
