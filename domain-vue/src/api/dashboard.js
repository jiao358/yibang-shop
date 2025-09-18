import request from './request'

export const dashboardApi = {
  // 获取仪表盘概览数据
  getOverview() {
    return request.get('/bk/dashboard/overview')
  },

  // 获取用户增长趋势图
  getUserGrowthChart(days = 30) {
    return request.get('/bk/dashboard/charts/user-growth', { 
      params: { days } 
    })
  },

  // 获取任务完成趋势图
  getTaskCompletionChart(days = 30) {
    return request.get('/bk/dashboard/charts/task-completion', { 
      params: { days } 
    })
  },

  // 获取收入统计图表
  getRevenueChart(days = 30, type = 'daily') {
    return request.get('/bk/dashboard/charts/revenue', { 
      params: { days, type } 
    })
  },

  // 获取用户等级分布
  getUserLevelDistribution() {
    return request.get('/bk/dashboard/charts/user-level-distribution')
  },

  // 获取任务类型统计
  getTaskTypeStats() {
    return request.get('/bk/dashboard/charts/task-type-stats')
  },

  // 获取提现趋势图
  getWithdrawTrend(days = 30) {
    return request.get('/bk/dashboard/charts/withdraw-trend', { 
      params: { days } 
    })
  },

  // 获取热门任务排行
  getTopTasks(limit = 10) {
    return request.get('/bk/dashboard/top-tasks', { 
      params: { limit } 
    })
  },

  // 获取活跃用户排行
  getTopUsers(limit = 10, sortBy = 'earnings') {
    return request.get('/bk/dashboard/top-users', { 
      params: { limit, sortBy } 
    })
  },

  // 获取实时数据
  getRealtimeData() {
    return request.get('/bk/dashboard/realtime')
  },

  // 刷新仪表盘缓存
  refreshCache() {
    return request.post('/bk/dashboard/refresh-cache')
  }
}
