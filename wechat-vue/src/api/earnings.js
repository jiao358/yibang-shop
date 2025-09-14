import { get, post, put } from './request'

export const earningsApi = {
  // 获取用户收益信息
  getUserEarnings() {
    return get('/api/earnings/user')
  },

  // 获取收益列表
  getEarningsList(params = {}) {
    return get('/api/earnings', params)
  },

  // 获取提现记录
  getWithdrawals(params = {}) {
    return get('/api/earnings/withdrawals', params)
  },

  // 申请提现
  applyWithdrawal(data) {
    return post('/api/earnings/withdraw', data)
  },

  // 获取提现统计
  getWithdrawalStats() {
    return get('/api/earnings/withdrawal-stats')
  },

  // 获取收益统计
  getEarningsStats(params = {}) {
    return get('/api/earnings/stats', params)
  },

  // 获取任务收益
  getEarningsByTask(taskId) {
    return get(`/api/earnings/task/${taskId}`)
  },

  // 获取日期收益
  getEarningsByDate(date) {
    return get(`/api/earnings/date/${date}`)
  },

  // 获取收益汇总
  getEarningsSummary(params = {}) {
    return get('/api/earnings/summary', params)
  },

  // 获取收益趋势
  getEarningsTrend(params = {}) {
    return get('/api/earnings/trend', params)
  },

  // 获取收益排行榜
  getEarningsRanking(params = {}) {
    return get('/api/earnings/ranking', params)
  },

  // 获取收益明细
  getEarningsDetail(earningsId) {
    return get(`/api/earnings/${earningsId}`)
  },

  // 获取收益来源统计
  getEarningsSourceStats(params = {}) {
    return get('/api/earnings/source-stats', params)
  },

  // 获取收益类型统计
  getEarningsTypeStats(params = {}) {
    return get('/api/earnings/type-stats', params)
  },

  // 获取收益时间分布
  getEarningsTimeDistribution(params = {}) {
    return get('/api/earnings/time-distribution', params)
  },

  // 获取收益目标设置
  getEarningsGoals() {
    return get('/api/earnings/goals')
  },

  // 设置收益目标
  setEarningsGoals(data) {
    return post('/api/earnings/goals', data)
  },

  // 获取收益提醒设置
  getEarningsReminders() {
    return get('/api/earnings/reminders')
  },

  // 设置收益提醒
  setEarningsReminders(data) {
    return post('/api/earnings/reminders', data)
  },

  // 获取收益分享信息
  getEarningsShareInfo() {
    return get('/api/earnings/share')
  },

  // 分享收益
  shareEarnings(data) {
    return post('/api/earnings/share', data)
  }
}
