import { defineStore } from 'pinia'
import { ref } from 'vue'
import systemApi from '@/api/system'

export const useSystemStore = defineStore('system', () => {
  // 状态
  const loading = ref(false)
  const error = ref(null)
  const customerServiceConfig = ref({
    qrCode: '',
    content: ''
  })

  // 获取客服配置
  const getCustomerServiceConfig = async () => {
    try {
      loading.value = true
      error.value = null
      
      const response = await systemApi.getCustomerServiceConfig()
      customerServiceConfig.value = response.data
      
      return response
    } catch (err) {
      error.value = err.message || '获取客服配置失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取系统配置
  const getSystemConfig = async (configKey) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await systemApi.getSystemConfig(configKey)
      
      return response
    } catch (err) {
      error.value = err.message || '获取系统配置失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    customerServiceConfig,
    getCustomerServiceConfig,
    getSystemConfig
  }
})
