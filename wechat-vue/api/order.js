import request from './request'

// 订单相关API
export const orderApi = {
  // 获取用户订单列表
  getUserOrders(params = {}) {
    return request({
      url: '/api/orders',
      method: 'GET',
      data: params
    })
  },

  // 获取订单详情
  getOrderDetail(orderId) {
    return request({
      url: `/api/orders/${orderId}`,
      method: 'GET'
    })
  },

  // 创建订单
  createOrder(orderData) {
    return request({
      url: '/api/orders',
      method: 'POST',
      data: orderData
    })
  },

  // 取消订单
  cancelOrder(orderId) {
    return request({
      url: `/api/orders/${orderId}/cancel`,
      method: 'PUT'
    })
  },

  // 支付订单
  payOrder(orderId, paymentData) {
    return request({
      url: `/api/orders/${orderId}/pay`,
      method: 'POST',
      data: paymentData
    })
  },

  // 确认收货
  confirmOrder(orderId) {
    return request({
      url: `/api/orders/${orderId}/confirm`,
      method: 'PUT'
    })
  },

  // 评价订单
  reviewOrder(orderId, reviewData) {
    return request({
      url: `/api/orders/${orderId}/review`,
      method: 'POST',
      data: reviewData
    })
  },

  // 获取订单统计
  getOrderStats() {
    return request({
      url: '/api/orders/stats',
      method: 'GET'
    })
  }
}
