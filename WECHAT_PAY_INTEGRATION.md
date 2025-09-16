# 微信支付（企业付款到零钱）接入说明

## 1. 业务目标与资金口径
- 目标：支持用户提现，使用“企业付款到零钱”。
- 资金口径：
  - 可用余额 available_balance：可提现金额
  - 冻结余额 frozen_balance：发起提现后，从可用转入冻结；回调成功释放（实扣），失败回滚。
- 流水：
  - 钱包流水 wallet_transactions：commission 收入；withdrawal 提现（pending/success/failed）。
  - 提现单 withdraw_requests：记录提现请求与渠道返回（request_no、channel_txn_id、status）。

## 2. 配置与密钥（application.yml 占位项）
```yaml
wxpay:
  merchantId: ${WX_MCH_ID}
  appId: ${WX_APP_ID}
  apiV3Key: ${WX_API_V3_KEY}       # 32位
  privateKeyPath: ${WX_PRIVATE_KEY_PATH} # apiclient_key.pem 路径
  certSerialNo: ${WX_CERT_SERIAL_NO}
  apiHost: https://api.mch.weixin.qq.com
```
- 私钥文件不入库；APIv3Key 建议通过环境变量/KMS注入。

## 3. 后端接口与路由
- 发起提现：`POST /api/withdraw`
  - 请求：`{ amountInCents: long }`
  - 业务：检查余额→可用-、冻结+→创建提现单（pending）→写 pending 流水→调用微信→按结果触发回调。
- 回调：`POST /api/withdraw/wxpay/callback`
  - 放行路由、做验签与幂等（`request_no`、`channel_txn_id` 唯一）。
- 钱包：
  - `GET /api/wallet/transactions?type=&periodType=&periodValue=&page=&size=` 返回 `{records,total}`。
  - `GET /api/wallet/summary?periodType=&periodValue=` 返回 `{totalIncome,totalWithdrawal,netChange,balance,frozenBalance}`。

## 4. 调用流程（企业付款到零钱）
1) 组装参数：openid（来自用户表）、amount（分）、desc（提现说明）、`request_no` 唯一。
2) 证书签名：使用商户私钥与证书序列号；HTTPS双向认证。
3) 提交微信接口：成功仅代表受理，结果以异步回调为准。
4) 回调验签：使用平台证书校验；检查时间戳、nonce，防重放。
5) 落账：
   - 成功：冻结余额减少；写 withdrawal success 流水。
   - 失败：冻结回滚至可用；写 withdrawal failed 流水。
6) 风控与限额：单笔/单日限额、黑名单、频次限制、最小提现额等。

## 5. 周/月统计与流水筛选
- `periodType=month`：`periodValue=YYYY-MM`，按自然月起止。
- `periodType=week`：`periodValue=YYYY-ww`，按自然周（周一至周日）。
- `type=commission|withdrawal|all`：仅收入/仅提现/全部。
- 统计口径：commission 全部计入收入；withdrawal 仅 success 计入支出，pending 不计入本期支出。

## 6. 状态机与幂等
- `withdraw_requests.status`：pending→success/failed。
- `wallet_transactions`：提现相关流水可标记 pending/success/failed（收入可直接 success）。
- 幂等：`request_no` 与 `channel_txn_id` 唯一；重复回调直接返回成功（无副作用）。

## 7. 错误处理与重试
- 可重试：系统繁忙、网络错误→退避重试。
- 不可重试：参数、签名、余额不足→直接失败并告警。
- 超时：受理后 T 分钟未回调→主动查单与告警。
- 日志：请求/响应签名摘要（脱敏）、回调原文加密存档。

## 8. 安全要求
- 证书/密钥严格权限；仅服务账号可读。
- 强制 HTTPS；校验证书链。
- 回调可加来源IP白名单（可选）。
- 防重放：header 时间戳+nonce 校验，单号幂等。

## 9. 测试与上线
- 沙箱/仿真商户；先联调后灰度。
- 用例：成功提现、失败回调、重复回调、金额边界、统计校验。
- 上线检查：配置从环境注入；回调放行；监控与告警（失败率、pending超时、验签失败）；资金对账。

## 10. 代码位置
- DDL：`ddl/10_wallet_module.sql`、`ddl/11_withdraw_module.sql`
- 实体/Mapper：`WalletTransaction`、`WithdrawRequest`、`UserMapper`
- Service：`WalletServiceImpl`、`WithdrawServiceImpl`、`WechatPayService(Stub)`
- Controller：`WalletController`、`WithdrawController`
- 前端：`src/api/wallet.js`、`src/stores/wallet.js`、`pages/wallet/transactions.vue`

> 注：当前仓库内 `WechatPayServiceStubImpl` 为占位实现，替换为真实SDK调用与验签后即可投入联调。
