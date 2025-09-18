import request from './request'

export const withdrawApi = {
  // 分页查询提现申请列表
  getWithdrawList(params) {
    return request.get('/bk/withdraws', { params })
  },

  // 获取提现申请详情
  getWithdrawDetail(withdrawId) {
    return request.get(`/bk/withdraws/${withdrawId}`)
  },

  // 审核提现申请
  approveWithdraw(withdrawId, data) {
    return request.post(`/bk/withdraws/${withdrawId}/approve`, data)
  },

  // 批量审核提现申请
  batchApprove(data) {
    return request.post('/bk/withdraws/batch-approve', data)
  },

  // 确认打款完成
  confirmPayment(withdrawId, data) {
    return request.post(`/bk/withdraws/${withdrawId}/confirm`, null, {
      params: data
    })
  },

  // 标记打款失败
  markFailed(withdrawId, failureReason) {
    return request.post(`/bk/withdraws/${withdrawId}/fail`, null, {
      params: { failureReason }
    })
  },

  // 获取提现统计
  getWithdrawStats(params) {
    return request.get('/bk/withdraws/stats', { params })
  },

  // 获取待审核数量
  getPendingCount() {
    return request.get('/bk/withdraws/pending-count')
  },

  // 导出提现记录
  exportWithdraws(params) {
    return request.get('/bk/withdraws/export', { 
      params,
      responseType: 'blob'
    })
  },

  // 获取提现状态选项
  getWithdrawStatuses() {
    return request.get('/bk/withdraws/statuses')
  }
}
