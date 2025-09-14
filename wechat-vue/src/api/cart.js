import { get, post, put, del } from './request'

export const cartApi = {
  // 获取购物车商品
  getCartItems() {
    return get('/api/cart/items')
  },

  // 添加到购物车
  addToCart(data) {
    return post('/api/cart/items', data)
  },

  // 更新购物车商品
  updateCartItem(itemId, data) {
    return put(`/api/cart/items/${itemId}`, data)
  },

  // 删除购物车商品
  removeCartItem(itemId) {
    return del(`/api/cart/items/${itemId}`)
  },

  // 清空购物车
  clearCart() {
    return del('/api/cart/items')
  },

  // 获取购物车汇总
  getCartSummary() {
    return get('/api/cart/summary')
  },

  // 验证购物车
  validateCart() {
    return post('/api/cart/validate')
  },

  // 批量更新购物车
  batchUpdateCart(data) {
    return put('/api/cart/batch-update', data)
  },

  // 批量删除购物车商品
  batchDeleteCart(data) {
    return del('/api/cart/batch-delete', data)
  },

  // 获取购物车商品数量
  getCartCount() {
    return get('/api/cart/count')
  },

  // 同步购物车
  syncCart(data) {
    return post('/api/cart/sync', data)
  },

  // 获取购物车推荐商品
  getCartRecommendations() {
    return get('/api/cart/recommendations')
  },

  // 获取购物车优惠信息
  getCartDiscounts() {
    return get('/api/cart/discounts')
  },

  // 应用优惠券
  applyCoupon(couponCode) {
    return post('/api/cart/coupon', { couponCode })
  },

  // 移除优惠券
  removeCoupon() {
    return del('/api/cart/coupon')
  },

  // 获取购物车运费
  getCartShipping() {
    return get('/api/cart/shipping')
  },

  // 计算购物车总价
  calculateCartTotal() {
    return post('/api/cart/calculate')
  }
}
