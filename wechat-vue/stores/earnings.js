import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { earningsApi } from '@/api/earnings'

export const useEarningsStore = defineStore('earnings', () => {
  // 状态
  const userEarnings = ref({})
  const earningsList = ref([])
  const withdrawals = ref([])
  const total = ref(0)
  const loading = ref(false)

  // 计算属性
  const totalEarnings = computed(() => userEarnings.value.totalEarnings || 0)
  const availableBalance = computed(() => userEarnings.value.balance || 0)
  const frozenAmount = computed(() => userEarnings.value.frozenAmount || 0)
  const todayEarnings = computed(() => userEarnings.value.todayEarnings || 0)

  // 方法
  const getUserEarnings = async () => {
    try {
      const response = await earningsApi.getUserEarnings()
      userEarnings.value = response.earnings
      return response
    } catch (error) {
      console.error('获取用户收益失败:', error)
      throw error
    }
  }

  const getEarningsList = async (params = {}) => {
    try {
      loading.value = true
      const response = await earningsApi.getEarningsList(params)
      
      if (params.page === 1) {
        earningsList.value = response.earnings
      } else {
        earningsList.value = [...earningsList.value, ...response.earnings]
      }
      
      total.value = response.total
      return response
    } catch (error) {
      console.error('获取收益列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getWithdrawals = async (params = {}) => {
    try {
      const response = await earningsApi.getWithdrawals(params)
      withdrawals.value = response.withdrawals
      return response
    } catch (error) {
      console.error('获取提现记录失败:', error)
      throw error
    }
  }

  const applyWithdrawal = async (data) => {
    try {
      const response = await earningsApi.applyWithdrawal(data)
      
      // 更新用户收益
      await getUserEarnings()
      
      return response
    } catch (error) {
      console.error('申请提现失败:', error)
      throw error
    }
  }

  const getWithdrawalStats = async () => {
    try {
      const response = await earningsApi.getWithdrawalStats()
      return response
    } catch (error) {
      console.error('获取提现统计失败:', error)
      throw error
    }
  }

  const getEarningsStats = async (params = {}) => {
    try {
      const response = await earningsApi.getEarningsStats(params)
      return response
    } catch (error) {
      console.error('获取收益统计失败:', error)
      throw error
    }
  }

  const getEarningsByTask = async (taskId) => {
    try {
      const response = await earningsApi.getEarningsByTask(taskId)
      return response
    } catch (error) {
      console.error('获取任务收益失败:', error)
      throw error
    }
  }

  const getEarningsByDate = async (date) => {
    try {
      const response = await earningsApi.getEarningsByDate(date)
      return response
    } catch (error) {
      console.error('获取日期收益失败:', error)
      throw error
    }
  }

  const getEarningsSummary = async (params = {}) => {
    try {
      const response = await earningsApi.getEarningsSummary(params)
      return response
    } catch (error) {
      console.error('获取收益汇总失败:', error)
      throw error
    }
  }

  const clearEarnings = () => {
    userEarnings.value = {}
    earningsList.value = []
    withdrawals.value = []
    total.value = 0
  }

  return {
    // 状态
    userEarnings,
    earningsList,
    withdrawals,
    total,
    loading,
    
    // 计算属性
    totalEarnings,
    availableBalance,
    frozenAmount,
    todayEarnings,
    
    // 方法
    getUserEarnings,
    getEarningsList,
    getWithdrawals,
    applyWithdrawal,
    getWithdrawalStats,
    getEarningsStats,
    getEarningsByTask,
    getEarningsByDate,
    getEarningsSummary,
    clearEarnings
  }
})
