import { defineStore } from 'pinia'
import { ref } from 'vue'
import { orderApi } from '@/api/order'

export const useOrderStore = defineStore('order', () => {
  // 状态
  const orders = ref([])
  const currentOrder = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // 获取用户订单列表
  const getUserOrders = async (params = {}) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.getUserOrders(params)
      orders.value = response.data || []
      
      return response
    } catch (err) {
      error.value = err.message || '获取订单列表失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取订单详情
  const getOrderDetail = async (orderId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.getOrderDetail(orderId)
      currentOrder.value = response.data
      
      return response
    } catch (err) {
      error.value = err.message || '获取订单详情失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 创建订单
  const createOrder = async (orderData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.createOrder(orderData)
      
      // 添加到订单列表
      if (response.data) {
        orders.value.unshift(response.data)
      }
      
      return response
    } catch (err) {
      error.value = err.message || '创建订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 取消订单
  const cancelOrder = async (orderId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.cancelOrder(orderId)
      
      // 更新订单状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'cancelled'
      }
      
      return response
    } catch (err) {
      error.value = err.message || '取消订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 支付订单
  const payOrder = async (orderId, paymentData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.payOrder(orderId, paymentData)
      
      // 更新订单状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'paid'
      }
      
      return response
    } catch (err) {
      error.value = err.message || '支付订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 确认收货
  const confirmOrder = async (orderId) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.confirmOrder(orderId)
      
      // 更新订单状态
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'completed'
      }
      
      return response
    } catch (err) {
      error.value = err.message || '确认收货失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 评价订单
  const reviewOrder = async (orderId, reviewData) => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.reviewOrder(orderId, reviewData)
      
      return response
    } catch (err) {
      error.value = err.message || '评价订单失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 获取订单统计
  const getOrderStats = async () => {
    try {
      loading.value = true
      error.value = null
      
      const response = await orderApi.getOrderStats()
      
      return response
    } catch (err) {
      error.value = err.message || '获取订单统计失败'
      throw err
    } finally {
      loading.value = false
    }
  }

  // 重置状态
  const resetState = () => {
    orders.value = []
    currentOrder.value = null
    loading.value = false
    error.value = null
  }

  return {
    // 状态
    orders,
    currentOrder,
    loading,
    error,
    
    // 方法
    getUserOrders,
    getOrderDetail,
    createOrder,
    cancelOrder,
    payOrder,
    confirmOrder,
    reviewOrder,
    getOrderStats,
    resetState
  }
})
