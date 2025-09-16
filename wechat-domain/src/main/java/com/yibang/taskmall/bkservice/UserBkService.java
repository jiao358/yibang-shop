package com.yibang.taskmall.bkservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.UserBalanceUpdateRequest;
import com.yibang.taskmall.bkdto.request.UserLevelUpdateRequest;
import com.yibang.taskmall.bkdto.request.UserQueryRequest;
import com.yibang.taskmall.bkdto.response.UserDetailResponse;
import com.yibang.taskmall.bkdto.response.UserListResponse;
import com.yibang.taskmall.bkdto.response.UserStatsResponse;

import java.util.List;
import java.util.Map;

public interface UserBkService {

    Page<UserListResponse> getUserList(UserQueryRequest request, Integer page, Integer size);

    UserDetailResponse getUserDetail(Long userId);

    void updateUserBalance(Long userId, UserBalanceUpdateRequest request);

    void updateUserLevel(Long userId, UserLevelUpdateRequest request);

    void batchOperation(List<Long> userIds, String operation);

    UserStatsResponse getUserStats(String type, String startDate, String endDate);

    Map<String, Object> getUserLevels();

    Map<String, Object> getUserTasks(Long userId, Integer page, Integer size);

    Map<String, Object> getUserInvites(Long userId);
}
