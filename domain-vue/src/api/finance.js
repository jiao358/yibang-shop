import request from './request'

export const financeApi = {
  // 获取收支明细列表
  getIncomeExpenseList(params) {
    return request.get('/bk/finance/income-expense', { params })
  },

  // 导出收支明细
  exportIncomeExpense(params) {
    return request.get('/bk/finance/income-expense/export', { 
      params,
      responseType: 'blob'
    })
  },

  // 获取收支汇总
  getSummaryByPeriod(params) {
    return request.get('/bk/finance/summary', { params })
  },

  // 获取收入趋势
  getIncomeTrend(params) {
    return request.get('/bk/finance/charts/income-trend', { params })
  },

  // 获取支出分析
  getExpenseAnalysis(params) {
    return request.get('/bk/finance/charts/expense-analysis', { params })
  },

  // 获取利润分析
  getProfitAnalysis(params) {
    return request.get('/bk/finance/charts/profit-analysis', { params })
  },

  // 获取财务概览
  getFinanceOverview(params) {
    return request.get('/bk/finance/overview', { params })
  }
}
