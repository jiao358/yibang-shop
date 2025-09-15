import request from './request'

// 地址相关API
export const addressApi = {
  // 获取地址列表
  getAddressList(params = {}) {
    return request({
      url: '/api/addresses',
      method: 'GET',
      data: params
    })
  },

  // 获取地址详情
  getAddressDetail(addressId) {
    return request({
      url: `/api/addresses/${addressId}`,
      method: 'GET'
    })
  },

  // 创建地址
  createAddress(addressData) {
    return request({
      url: '/api/addresses',
      method: 'POST',
      data: addressData
    })
  },

  // 更新地址
  updateAddress(addressId, addressData) {
    return request({
      url: `/api/addresses/${addressId}`,
      method: 'PUT',
      data: addressData
    })
  },

  // 删除地址
  deleteAddress(addressId) {
    return request({
      url: `/api/addresses/${addressId}`,
      method: 'DELETE'
    })
  },

  // 设置默认地址
  setDefaultAddress(addressId) {
    return request({
      url: `/api/addresses/${addressId}/default`,
      method: 'PUT'
    })
  }
}
