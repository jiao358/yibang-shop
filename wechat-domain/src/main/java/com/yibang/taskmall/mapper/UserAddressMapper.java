package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收货地址数据访问层
 * 
 * @author yibang
 * @since 2024-01-15
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
