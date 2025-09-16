import { defineStore } from 'pinia'
import { ref } from 'vue'
import { walletApi } from '@/api/wallet'

export const useWalletStore = defineStore('wallet', () => {
  const loading = ref(false)
  const error = ref(null)
  const transactions = ref([])
  const total = ref(0)
  const summary = ref({
    totalIn: 0,
    totalOut: 0,
    netChange: 0,
    balance: 0,
    frozenBalance: 0
  })

  const getTransactions = async (params = {}) => {
    try {
      loading.value = true
      error.value = null
      const res = await walletApi.getTransactions(params)
      transactions.value = res.data?.records || []
      total.value = res.data?.total || 0
      return res
    } catch (err) {
      error.value = err.message || '获取交易流水失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  const getSummary = async (params = {}) => {
    try {
      loading.value = true
      error.value = null
      const res = await walletApi.getSummary(params)
      const data = res.data || {}
      summary.value = {
        totalIn: data.totalIn || 0,
        totalOut: data.totalOut || 0,
        netChange: data.netChange || 0,
        balance: data.balance || 0,
        frozenBalance: data.frozenBalance || 0
      }
      return res
    } catch (err) {
      error.value = err.message || '获取汇总统计失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    transactions,
    total,
    summary,
    getTransactions,
    getSummary
  }
})


