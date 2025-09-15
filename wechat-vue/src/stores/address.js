import { defineStore } from 'pinia'
import { ref } from 'vue'
import { addressApi } from '@/api/address'

export const useAddressStore = defineStore('address', () => {
  // 状态
  const addressList = ref([])
  const currentAddress = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // 获取地址列表
  const getAddressList = async (params = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.getAddressList(params)
      addressList.value = response.data || []
      
      return response
    } catch (err) {
      error.value = err.message || '获取地址列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取地址详情
  const getAddressDetail = async (addressId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.getAddressDetail(addressId)
      currentAddress.value = response.data
      
      return response
    } catch (err) {
      error.value = err.message || '获取地址详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 创建地址
  const createAddress = async (addressData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.createAddress(addressData)
      
      // 添加到地址列表
      if (response.data) {
        addressList.value.unshift(response.data)
      }
      
      return response
    } catch (err) {
      error.value = err.message || '创建地址失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 更新地址
  const updateAddress = async (addressId, addressData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.updateAddress(addressId, addressData)
      
      // 更新地址列表
      const index = addressList.value.findIndex(item => item.id === addressId)
      if (index !== -1 && response.data) {
        addressList.value[index] = response.data
      }
      
      return response
    } catch (err) {
      error.value = err.message || '更新地址失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 删除地址
  const deleteAddress = async (addressId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.deleteAddress(addressId)
      
      // 从地址列表中移除
      addressList.value = addressList.value.filter(item => item.id !== addressId)
      
      return response
    } catch (err) {
      error.value = err.message || '删除地址失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 设置默认地址
  const setDefaultAddress = async (addressId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await addressApi.setDefaultAddress(addressId)
      
      // 更新地址列表中的默认状态
      addressList.value.forEach(item => {
        item.isDefault = item.id === addressId
      })
      
      return response
    } catch (err) {
      error.value = err.message || '设置默认地址失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 重置状态
  const resetState = () => {
    addressList.value = []
    currentAddress.value = null
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    addressList,
    currentAddress,
    loading,
    error,
    
    // 方法
    getAddressList,
    getAddressDetail,
    createAddress,
    updateAddress,
    deleteAddress,
    setDefaultAddress,
    resetState
  }
})
