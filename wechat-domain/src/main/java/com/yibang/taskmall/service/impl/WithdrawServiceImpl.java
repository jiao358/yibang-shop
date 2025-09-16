package com.yibang.taskmall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yibang.taskmall.dto.request.WithdrawCreateRequest;
import com.yibang.taskmall.entity.User;
import com.yibang.taskmall.entity.WalletTransaction;
import com.yibang.taskmall.entity.WithdrawRequest;
import com.yibang.taskmall.mapper.UserMapper;
import com.yibang.taskmall.mapper.WalletTransactionMapper;
import com.yibang.taskmall.mapper.WithdrawRequestMapper;
import com.yibang.taskmall.service.WithdrawService;
import com.yibang.taskmall.service.WechatPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawServiceImpl implements WithdrawService {

    private final UserMapper userMapper;
    private final WithdrawRequestMapper withdrawRequestMapper;
    private final WalletTransactionMapper walletTransactionMapper;
    private final WechatPayService wechatPayService;

    @Override
    @Transactional
    public Long createWithdraw(Long userId, WithdrawCreateRequest req) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new RuntimeException("用户不存在");
        long amount = req.getAmountInCents() == null ? 0L : req.getAmountInCents();
        long available = user.getAvailableBalance() == null ? 0L : user.getAvailableBalance();
        if (amount <= 0 || amount > available) throw new RuntimeException("余额不足");

        // 扣减可用，增加冻结
        user.setAvailableBalance(available - amount);
        long frozen = user.getFrozenBalance() == null ? 0L : user.getFrozenBalance();
        user.setFrozenBalance(frozen + amount);
        userMapper.updateById(user);

        // 生成提现单
        String requestNo = UUID.randomUUID().toString().replace("-", "");
        WithdrawRequest wr = new WithdrawRequest();
        wr.setUserId(userId);
        wr.setRequestNo(requestNo);
        wr.setAmountInCents(amount);
        wr.setStatus("pending");
        wr.setChannel("wxpay");
        withdrawRequestMapper.insert(wr);

        // 记录pending提现流水（不计入summary支出）
        WalletTransaction tx = new WalletTransaction();
        tx.setUserId(userId);
        tx.setBizType("withdrawal");
        tx.setRemark("pending");
        tx.setAmountInCents(amount);
        tx.setBalanceAfterInCents(user.getAvailableBalance());
        tx.setOccurTime(LocalDateTime.now());
        walletTransactionMapper.insert(tx);

        // 调起微信企业付款到零钱（占位实现，建议实际使用异步回调）
        try {
            String channelTxnId = wechatPayService.transferToWallet(user, amount, requestNo);
            // 这里可直接当作回调成功处理，也可以等待真实回调
            handleWxpayCallback(requestNo, true, channelTxnId, "auto-confirm by stub");
        } catch (Exception e) {
            handleWxpayCallback(requestNo, false, null, e.getMessage());
        }
        return wr.getId();
    }

    @Override
    @Transactional
    public void handleWxpayCallback(String requestNo, boolean success, String channelTxnId, String remark) {
        WithdrawRequest wr = withdrawRequestMapper.selectOne(new LambdaQueryWrapper<WithdrawRequest>()
                .eq(WithdrawRequest::getRequestNo, requestNo));
        if (wr == null) return;
        if ("success".equals(wr.getStatus()) || "failed".equals(wr.getStatus())) return;

        User user = userMapper.selectById(wr.getUserId());
        long amount = wr.getAmountInCents();
        long frozen = user.getFrozenBalance() == null ? 0L : user.getFrozenBalance();

        if (success) {
            // 冻结释放（已在创建时从可用转入冻结，此处仅减少冻结即可）
            user.setFrozenBalance(Math.max(0, frozen - amount));
            userMapper.updateById(user);
            wr.setStatus("success");
            wr.setChannelTxnId(channelTxnId);
            wr.setRemark(remark);
            withdrawRequestMapper.updateById(wr);

            // 将对应pending提现流水remark更新为success，并保持金额与余额快照
            WalletTransaction tx = new WalletTransaction();
            tx.setUserId(user.getId());
            tx.setBizType("withdrawal");
            tx.setRemark("success");
            tx.setAmountInCents(amount);
            tx.setBalanceAfterInCents(user.getAvailableBalance());
            tx.setOccurTime(LocalDateTime.now());
            walletTransactionMapper.insert(tx);
        } else {
            // 回滚：冻结转回可用
            long available = user.getAvailableBalance() == null ? 0L : user.getAvailableBalance();
            user.setFrozenBalance(Math.max(0, frozen - amount));
            user.setAvailableBalance(available + amount);
            userMapper.updateById(user);
            wr.setStatus("failed");
            wr.setChannelTxnId(channelTxnId);
            wr.setRemark(remark);
            withdrawRequestMapper.updateById(wr);

            // 记录失败回滚流水（可选）
            WalletTransaction tx = new WalletTransaction();
            tx.setUserId(user.getId());
            tx.setBizType("withdrawal");
            tx.setRemark("failed");
            tx.setAmountInCents(amount);
            tx.setBalanceAfterInCents(user.getAvailableBalance());
            tx.setOccurTime(LocalDateTime.now());
            walletTransactionMapper.insert(tx);
        }
    }
}


