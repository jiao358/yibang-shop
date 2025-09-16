import request from './request'

export const financeApi = {
  // 获取收支明细列表
  getIncomeExpenseList(params) {
    return request({
      url: '/bk/finance/income-expense',
      method: 'get',
      params
    })
  },

  // 导出收支明细
  exportIncomeExpense(params) {
    return request({
      url: '/bk/finance/income-expense/export',
      method: 'get',
      params,
      responseType: 'blob'
    })
  },

  // 获取收支汇总
  getSummaryByPeriod(params) {
    return request({
      url: '/bk/finance/summary',
      method: 'get',
      params
    })
  },

  // 获取收入趋势
  getIncomeTrend(params) {
    return request({
      url: '/bk/finance/charts/income-trend',
      method: 'get',
      params
    })
  },

  // 获取支出分析
  getExpenseAnalysis(params) {
    return request({
      url: '/bk/finance/charts/expense-analysis',
      method: 'get',
      params
    })
  },

  // 获取利润分析
  getProfitAnalysis(params) {
    return request({
      url: '/bk/finance/charts/profit-analysis',
      method: 'get',
      params
    })
  },

  // 获取财务概览
  getFinanceOverview(params) {
    return request({
      url: '/bk/finance/overview',
      method: 'get',
      params
    })
  }
}
