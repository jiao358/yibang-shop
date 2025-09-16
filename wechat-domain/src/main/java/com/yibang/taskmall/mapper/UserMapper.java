package com.yibang.taskmall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yibang.taskmall.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息数据访问层
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
