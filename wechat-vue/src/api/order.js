import { get, post, put, del } from './request'

// 订单相关API
export const orderApi = {
  // 获取用户订单列表
  getUserOrders(params = {}) {
    return get('/api/orders', params)
  },

  // 获取订单详情
  getOrderDetail(orderId) {
    return get(`/api/orders/${orderId}`)
  },

  // 创建订单
  createOrder(orderData) {
    return post('/api/orders', orderData)
  },

  // 取消订单
  cancelOrder(orderId) {
    return put(`/api/orders/${orderId}/cancel`)
  },

  // 支付订单
  payOrder(orderId, paymentData) {
    return post(`/api/orders/${orderId}/pay`, paymentData)
  },

  // 确认收货
  confirmOrder(orderId) {
    return put(`/api/orders/${orderId}/confirm`)
  },

  // 评价订单
  reviewOrder(orderId, reviewData) {
    return post(`/api/orders/${orderId}/review`, reviewData)
  },

  // 获取订单统计
  getOrderStats() {
    return get('/api/orders/stats')
  }
}
