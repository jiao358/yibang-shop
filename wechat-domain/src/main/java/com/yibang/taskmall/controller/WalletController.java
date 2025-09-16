package com.yibang.taskmall.controller;

import com.yibang.taskmall.common.Result;
import com.yibang.taskmall.dto.response.WalletSummaryDTO;
import com.yibang.taskmall.dto.response.WalletTransactionDTO;
import com.yibang.taskmall.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
@Tag(name = "钱包", description = "钱包流水与统计")
public class WalletController {

    private final WalletService walletService;

    private Long currentUserId() {
        try {
            return Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
        } catch (Exception ignored) {
            return 1L;
        }
    }

    @GetMapping("/transactions")
    @Operation(summary = "获取交易流水")
    public Result<Map<String, Object>> getTransactions(
            @RequestParam(defaultValue = "all") String type,
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = currentUserId();
        List<WalletTransactionDTO> rows = walletService.getTransactions(userId, type, periodType, periodValue, page, size);
        long total = walletService.countTransactions(userId, type, periodType, periodValue);
        Map<String, Object> data = new HashMap<>();
        data.put("records", rows);
        data.put("total", total);
        return Result.success(data);
    }

    @GetMapping("/summary")
    @Operation(summary = "获取周期汇总")
    public Result<WalletSummaryDTO> getSummary(
            @RequestParam(defaultValue = "month") String periodType,
            @RequestParam(required = false) String periodValue) {
        Long userId = currentUserId();
        WalletSummaryDTO dto = walletService.getSummary(userId, periodType, periodValue);
        return Result.success(dto);
    }
}


