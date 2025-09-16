import { get, post, put } from './request'

export const withdrawApi = {
  // 获取提现配置（手续费率、最低金额等）
  getWithdrawConfig() {
    return get('/api/withdrawals/config')
  },

  // 计算提现手续费
  calculateFee(amount) {
    return post('/api/withdrawals/calculate-fee', { amount })
  },

  // 申请提现
  applyWithdraw(data) {
    return post('/api/withdrawals/apply', data)
  },

  // 获取提现记录
  getWithdrawHistory(params = {}) {
    return get('/api/withdrawals', params)
  },

  // 获取提现详情
  getWithdrawDetail(withdrawalId) {
    return get(`/api/withdrawals/${withdrawalId}`)
  },

  // 取消提现
  cancelWithdraw(withdrawalId) {
    return post(`/api/withdrawals/${withdrawalId}/cancel`)
  },

  // 获取最近提现记录（用于首页显示）
  getRecentWithdraws(limit = 3) {
    return get('/api/withdrawals', { page: 1, size: limit, recent: true })
  }
}
