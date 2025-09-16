import { get } from '@/api/request'

/**
 * 系统配置相关API
 */

// 获取系统配置
export function getSystemConfig(configKey) {
  return get(`/api/system/config/${configKey}`)
}

// 获取客服配置
export function getCustomerServiceConfig() {
  return get('/api/system/config/customer-service')
}

export default {
  getSystemConfig,
  getCustomerServiceConfig
}
