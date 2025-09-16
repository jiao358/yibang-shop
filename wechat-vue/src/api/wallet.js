import { get } from '@/api/request'

// 钱包相关API（按已确认口径）
export const walletApi = {
  // 交易流水列表：type=commission|withdrawal|all，periodType=month|week，periodValue=YYYY-MM|YYYY-ww
  getTransactions(params = {}) {
    return get('/api/wallet/transactions', params)
  },

  // 汇总统计：按周期统计总入账/总提现/净增等
  getSummary(params = {}) {
    return get('/api/wallet/summary', params)
  }
}

export default walletApi


