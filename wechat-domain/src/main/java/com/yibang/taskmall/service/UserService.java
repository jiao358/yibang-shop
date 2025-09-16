package com.yibang.taskmall.service;

import com.yibang.taskmall.entity.User;

/**
 * 用户服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface UserService {

    /**
     * 根据openid查找用户
     * 
     * @param openid 微信openid
     * @return 用户信息，如果不存在返回null
     */
    User findByOpenid(String openid);

    /**
     * 创建新用户
     * 
     * @param openid 微信openid
     * @param nickname 昵称
     * @param avatar 头像
     * @param phone 手机号
     * @return 创建的用户信息
     */
    User createUser(String openid, String nickname, String avatar, String phone);

    /**
     * 更新用户登录信息
     * 
     * @param user 用户对象
     * @param nickname 昵称
     * @param avatar 头像
     * @param phone 手机号
     * @return 更新后的用户信息
     */
    User updateLoginInfo(User user, String nickname, String avatar, String phone);

    /**
     * 生成唯一的邀请码
     * 
     * @return 邀请码
     */
    String generateInviteCode();

    /**
     * 根据用户ID查找用户
     * 
     * @param userId 用户ID
     * @return 用户信息，如果不存在返回null
     */
    User findById(Long userId);
}
