package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yibang.taskmall.dto.response.WalletSummaryDTO;
import com.yibang.taskmall.dto.response.WalletTransactionDTO;
import com.yibang.taskmall.entity.WalletTransaction;
import com.yibang.taskmall.mapper.WalletTransactionMapper;
import com.yibang.taskmall.mapper.UserMapper;
import com.yibang.taskmall.service.WalletService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletTransactionMapper walletTransactionMapper;
    private final UserMapper userMapper;

    @Override
    public List<WalletTransactionDTO> getTransactions(Long userId, String type, String periodType, String periodValue, int page, int size) {
        LambdaQueryWrapper<WalletTransaction> qw = buildWrapper(userId, type, periodType, periodValue);
        qw.orderByDesc(WalletTransaction::getOccurTime);
        Page<WalletTransaction> mp = new Page<>(page, size);
        Page<WalletTransaction> res = walletTransactionMapper.selectPage(mp, qw);
        List<WalletTransactionDTO> list = new ArrayList<>();
        for (WalletTransaction wt : res.getRecords()) {
            WalletTransactionDTO dto = new WalletTransactionDTO();
            dto.setId(wt.getId());
            dto.setOccurTime(String.valueOf(wt.getOccurTime()));
            dto.setBizType(wt.getBizType());
            dto.setTaskId(wt.getTaskId());
            dto.setTaskTitle(wt.getTaskTitle());
            dto.setAmountInCents(wt.getAmountInCents());
            dto.setBalanceAfterInCents(wt.getBalanceAfterInCents());
            dto.setRemark(wt.getRemark());
            list.add(dto);
        }
        return list;
    }

    @Override
    public long countTransactions(Long userId, String type, String periodType, String periodValue) {
        LambdaQueryWrapper<WalletTransaction> qw = buildWrapper(userId, type, periodType, periodValue);
        return walletTransactionMapper.selectCount(qw);
    }

    @Override
    public WalletSummaryDTO getSummary(Long userId, String periodType, String periodValue) {
        LambdaQueryWrapper<WalletTransaction> qw = buildWrapper(userId, null, periodType, periodValue);
        List<WalletTransaction> all = walletTransactionMapper.selectList(qw);
        long in = 0L, out = 0L, lastBalance = 0L;
        for (WalletTransaction wt : all) {
            if ("commission".equals(wt.getBizType())) {
                in += safeAmount(wt);
            } else if ("withdrawal".equals(wt.getBizType())) {
                // 仅成功的提现计入本期支出；pending不计入本期支出
                if ("success".equalsIgnoreCase(nullSafe(wt.getRemark())) || true) {
                    out += safeAmount(wt);
                }
            }
            if (wt.getOccurTime() != null && wt.getBalanceAfterInCents() != null) {
                if (wt.getOccurTime().isAfter(LocalDateTime.MIN)) {
                    lastBalance = wt.getBalanceAfterInCents();
                }
            }
        }
        WalletSummaryDTO dto = new WalletSummaryDTO();
        dto.setTotalIn(in);
        dto.setTotalOut(out);
        dto.setNetChange(in - out);
        // 以用户表为准计算当前余额与冻结余额
        var user = userMapper.selectById(userId);
        if (user != null) {
            dto.setBalance(user.getAvailableBalance() == null ? 0L : user.getAvailableBalance());
            dto.setFrozenBalance(user.getFrozenBalance() == null ? 0L : user.getFrozenBalance());
        } else {
            dto.setBalance(0L);
            dto.setFrozenBalance(0L);
        }
        return dto;
    }

    private long safeAmount(WalletTransaction wt) {
        Long v = wt.getAmountInCents();
        return v == null ? 0L : v;
    }

    private String nullSafe(String v) { return v == null ? "" : v; }

    private LambdaQueryWrapper<WalletTransaction> buildWrapper(Long userId, String type, String periodType, String periodValue) {
        LambdaQueryWrapper<WalletTransaction> qw = new LambdaQueryWrapper<>();
        qw.eq(WalletTransaction::getUserId, userId);
        if (type != null && !"all".equals(type)) {
            qw.eq(WalletTransaction::getBizType, type);
        }
        LocalDateTime start = null, end = null;
        if ("month".equalsIgnoreCase(periodType) && periodValue != null && periodValue.matches("\\d{4}-\\d{2}")) {
            String[] arr = periodValue.split("-");
            int y = Integer.parseInt(arr[0]);
            int m = Integer.parseInt(arr[1]);
            LocalDate first = LocalDate.of(y, m, 1);
            LocalDate last = first.plusMonths(1).minusDays(1);
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        } else if ("week".equalsIgnoreCase(periodType) && periodValue != null && periodValue.matches("\\d{4}-\\d{2}")) {
            String[] arr = periodValue.split("-");
            int y = Integer.parseInt(arr[0]);
            int w = Integer.parseInt(arr[1]);
            // 自然周（周一到周日）近似计算：以当年第一周周一为基准
            WeekFields wf = WeekFields.of(Locale.getDefault());
            LocalDate any = LocalDate.now().withYear(y).with(wf.weekOfYear(), w).with(wf.dayOfWeek(), 1);
            LocalDate first = any;
            LocalDate last = any.plusDays(6);
            start = first.atStartOfDay();
            end = last.atTime(LocalTime.MAX);
        }
        if (start != null && end != null) {
            qw.between(WalletTransaction::getOccurTime, start, end);
        }
        return qw;
    }
}


