package com.yibang.taskmall.service;

import java.util.Map;

/**
 * ERP系统集成服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface ErpIntegrationService {

    /**
     * ERP用户SSO验证
     * 
     * @param username 用户名
     * @param password 密码
     * @return 验证结果，包含用户信息
     */
    Map<String, Object> validateUser(String username, String password);

    /**
     * 同步ERP用户信息
     * 
     * @param erpUserId ERP用户ID
     * @return 用户信息
     */
    Map<String, Object> syncUserInfo(String erpUserId);

    /**
     * 获取ERP用户权限
     * 
     * @param erpUserId ERP用户ID
     * @return 权限列表
     */
    Map<String, Object> getUserPermissions(String erpUserId);

    /**
     * 同步用户数据到ERP
     * 
     * @param userId 本地用户ID
     * @param userData 用户数据
     * @return 同步结果
     */
    boolean syncToErp(Long userId, Map<String, Object> userData);

    /**
     * 检查ERP连接状态
     * 
     * @return 连接状态
     */
    boolean checkConnection();

    /**
     * 获取ERP系统信息
     * 
     * @return 系统信息
     */
    Map<String, Object> getErpSystemInfo();
}
