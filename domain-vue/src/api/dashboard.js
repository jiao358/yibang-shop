import request from './request'

export const dashboardApi = {
  // 获取仪表盘概览数据
  getOverview() {
    return request({
      url: '/bk/dashboard/overview',
      method: 'get'
    })
  },

  // 获取用户增长趋势图
  getUserGrowthChart(days = 30) {
    return request({
      url: '/bk/dashboard/charts/user-growth',
      method: 'get',
      params: { days }
    })
  },

  // 获取任务完成趋势图
  getTaskCompletionChart(days = 30) {
    return request({
      url: '/bk/dashboard/charts/task-completion',
      method: 'get',
      params: { days }
    })
  },

  // 获取收入统计图表
  getRevenueChart(days = 30, type = 'daily') {
    return request({
      url: '/bk/dashboard/charts/revenue',
      method: 'get',
      params: { days, type }
    })
  },

  // 获取用户等级分布
  getUserLevelDistribution() {
    return request({
      url: '/bk/dashboard/charts/user-level-distribution',
      method: 'get'
    })
  },

  // 获取任务类型统计
  getTaskTypeStats() {
    return request({
      url: '/bk/dashboard/charts/task-type-stats',
      method: 'get'
    })
  },

  // 获取提现趋势图
  getWithdrawTrend(days = 30) {
    return request({
      url: '/bk/dashboard/charts/withdraw-trend',
      method: 'get',
      params: { days }
    })
  },

  // 获取热门任务排行
  getTopTasks(limit = 10) {
    return request({
      url: '/bk/dashboard/top-tasks',
      method: 'get',
      params: { limit }
    })
  },

  // 获取活跃用户排行
  getTopUsers(limit = 10, sortBy = 'earnings') {
    return request({
      url: '/bk/dashboard/top-users',
      method: 'get',
      params: { limit, sortBy }
    })
  },

  // 获取实时数据
  getRealtimeData() {
    return request({
      url: '/bk/dashboard/realtime',
      method: 'get'
    })
  },

  // 刷新仪表盘缓存
  refreshCache() {
    return request({
      url: '/bk/dashboard/refresh-cache',
      method: 'post'
    })
  }
}
