package com.yibang.taskmall.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.entity.OperationLog;

import java.util.Map;

/**
 * 操作日志服务接口
 * 
 * @author yibang
 * @since 2024-01-15
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     */
    void recordOperation(String action, String module, String targetType, String targetId, 
                        String targetName, Object requestData, Object responseData, 
                        Long executionTime, String status, String errorMessage);

    /**
     * 分页查询操作日志
     */
    Page<Map<String, Object>> getOperationLogs(String action, String operator, String module,
                                              String startTime, String endTime, Integer page, Integer size);

    /**
     * 获取操作统计
     */
    Map<String, Object> getOperationStats(String startTime, String endTime);

    /**
     * 清理过期日志
     */
    int cleanExpiredLogs(int days);

    /**
     * 记录用户余额调整日志
     */
    void recordBalanceAdjustment(Long userId, String userNickname, Long amount, String type, String reason);

    /**
     * 记录用户等级变更日志
     */
    void recordLevelChange(Long userId, String userNickname, String oldLevel, String newLevel, String reason);

    /**
     * 记录提现审核日志
     */
    void recordWithdrawApproval(Long withdrawId, Long userId, String action, String remark);

    /**
     * 记录系统配置变更日志
     */
    void recordConfigChange(String configKey, String oldValue, String newValue, String description);
}
