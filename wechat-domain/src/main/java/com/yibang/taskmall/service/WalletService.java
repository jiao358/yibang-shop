package com.yibang.taskmall.service;

import com.yibang.taskmall.dto.response.WalletSummaryDTO;
import com.yibang.taskmall.dto.response.WalletTransactionDTO;

import java.util.List;

public interface WalletService {
    List<WalletTransactionDTO> getTransactions(Long userId, String type, String periodType, String periodValue, int page, int size);
    long countTransactions(Long userId, String type, String periodType, String periodValue);
    WalletSummaryDTO getSummary(Long userId, String periodType, String periodValue);
}


