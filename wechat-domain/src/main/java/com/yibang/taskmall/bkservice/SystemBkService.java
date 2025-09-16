package com.yibang.taskmall.bkservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.NotificationRequest;
import com.yibang.taskmall.bkdto.request.SystemConfigRequest;
import com.yibang.taskmall.bkdto.response.NotificationResponse;
import com.yibang.taskmall.bkdto.response.SystemConfigResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SystemBkService {
    List<SystemConfigResponse> getSystemConfigs(String group);

    void updateSystemConfig(String key, SystemConfigRequest request);

    void batchUpdateConfigs(Map<String, SystemConfigRequest> configMap);

    List<String> getConfigGroups();

    Page<NotificationResponse> getNotifications(String type, Integer page, Integer size);

    Long createNotification(NotificationRequest request);

    void updateNotification(Long notificationId, NotificationRequest request);

    void deleteNotification(Long notificationId);

    Map<String, Object> sendNotification(Long notificationId, List<Long> userIds);

    Map<String, String> uploadFile(MultipartFile file, String type);

    List<Map<String, String>> batchUploadFiles(MultipartFile[] files, String type);

    Page<Map<String, Object>> getOperationLogs(String action, String operator, String startTime, String endTime, Integer page, Integer size);

    void clearCache(String cacheType);

    Map<String, Object> getSystemInfo();
}


