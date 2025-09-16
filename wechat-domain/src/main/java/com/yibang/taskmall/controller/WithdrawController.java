package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.request.WithdrawCreateRequest;
import com.yibang.taskmall.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/withdraw")
@RequiredArgsConstructor
@Tag(name = "提现", description = "提现创建与回调")
public class WithdrawController {

    private final WithdrawService withdrawService;

    private Long currentUserId() {
        try { return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()); }
        catch (Exception ignored) { return 1L; }
    }

    @PostMapping
    @Operation(summary = "发起提现")
    public Result<Long> create(@RequestBody WithdrawCreateRequest req) {
        Long id = withdrawService.createWithdraw(currentUserId(), req);
        return Result.success(id);
    }

    @PostMapping("/wxpay/callback")
    @Operation(summary = "微信支付回调(示例)")
    public Result<Void> wxpayCallback(@RequestParam String requestNo,
                                      @RequestParam boolean success,
                                      @RequestParam(required = false) String channelTxnId,
                                      @RequestParam(required = false) String remark) {
        withdrawService.handleWxpayCallback(requestNo, success, channelTxnId, remark);
        return Result.success();
    }
}


