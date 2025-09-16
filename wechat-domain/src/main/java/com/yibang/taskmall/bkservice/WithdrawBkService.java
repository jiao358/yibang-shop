package com.yibang.taskmall.bkservice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.bkdto.request.WithdrawApprovalRequest;
import com.yibang.taskmall.bkdto.request.WithdrawBatchRequest;
import com.yibang.taskmall.bkdto.request.WithdrawQueryRequest;
import com.yibang.taskmall.bkdto.response.WithdrawDetailResponse;
import com.yibang.taskmall.bkdto.response.WithdrawListResponse;
import com.yibang.taskmall.bkdto.response.WithdrawStatsResponse;

import java.util.List;
import java.util.Map;

public interface WithdrawBkService {
    Page<WithdrawListResponse> getWithdrawList(WithdrawQueryRequest request, Integer page, Integer size);

    WithdrawDetailResponse getWithdrawDetail(Long withdrawId);

    void approveWithdraw(Long withdrawId, WithdrawApprovalRequest request);

    Map<String, Object> batchApprove(WithdrawBatchRequest request);

    void confirmPayment(Long withdrawId, String wechatTransactionId, String wechatPaymentNo);

    void markFailed(Long withdrawId, String failureReason);

    WithdrawStatsResponse getWithdrawStats(String type, String startDate, String endDate);

    Long getPendingCount();

    String exportWithdraws(WithdrawQueryRequest request);

    Map<String, String> getWithdrawStatuses();
}


