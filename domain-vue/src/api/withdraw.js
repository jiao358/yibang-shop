import request from './request'

export const withdrawApi = {
  // 分页查询提现申请列表
  getWithdrawList(params) {
    return request({
      url: '/bk/withdraws',
      method: 'get',
      params
    })
  },

  // 获取提现申请详情
  getWithdrawDetail(withdrawId) {
    return request({
      url: `/bk/withdraws/${withdrawId}`,
      method: 'get'
    })
  },

  // 审核提现申请
  approveWithdraw(withdrawId, data) {
    return request({
      url: `/bk/withdraws/${withdrawId}/approve`,
      method: 'post',
      data
    })
  },

  // 批量审核提现申请
  batchApprove(data) {
    return request({
      url: '/bk/withdraws/batch-approve',
      method: 'post',
      data
    })
  },

  // 确认打款完成
  confirmPayment(withdrawId, data) {
    return request({
      url: `/bk/withdraws/${withdrawId}/confirm`,
      method: 'post',
      params: data
    })
  },

  // 标记打款失败
  markFailed(withdrawId, failureReason) {
    return request({
      url: `/bk/withdraws/${withdrawId}/fail`,
      method: 'post',
      params: { failureReason }
    })
  },

  // 获取提现统计
  getWithdrawStats(params) {
    return request({
      url: '/bk/withdraws/stats',
      method: 'get',
      params
    })
  },

  // 获取待审核数量
  getPendingCount() {
    return request({
      url: '/bk/withdraws/pending-count',
      method: 'get'
    })
  },

  // 导出提现记录
  exportWithdraws(params) {
    return request({
      url: '/bk/withdraws/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 获取提现状态选项
  getWithdrawStatuses() {
    return request({
      url: '/bk/withdraws/statuses',
      method: 'get'
    })
  }
}
