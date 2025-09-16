import { get, post, put, del } from './request'

// 地址相关API
export const addressApi = {
  // 获取地址列表
  getAddressList(params = {}) {
    return get('/api/addresses', params)
  },

  // 获取地址详情
  getAddressDetail(addressId) {
    return get(`/api/addresses/${addressId}`)
  },

  // 创建地址
  createAddress(addressData) {
    return post('/api/addresses', addressData)
  },

  // 更新地址
  updateAddress(addressId, addressData) {
    return put(`/api/addresses/${addressId}`, addressData)
  },

  // 删除地址
  deleteAddress(addressId) {
    return del(`/api/addresses/${addressId}`)
  },

  // 设置默认地址
  setDefaultAddress(addressId) {
    return put(`/api/addresses/${addressId}/default`)
  }
}
