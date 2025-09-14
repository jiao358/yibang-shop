import { get, post, put } from './request'

export const mallApi = {
  // 获取分类列表
  getCategories() {
    return get('/api/mall/categories')
  },

  // 获取商品列表
  getProducts(params = {}) {
    return get('/api/mall/products', params)
  },

  // 获取商品详情
  getProductDetail(productId) {
    return get(`/api/mall/products/${productId}`)
  },

  // 搜索商品
  searchProducts(keyword, params = {}) {
    return get('/api/mall/products/search', { keyword, ...params })
  },

  // 获取热门商品
  getHotProducts() {
    return get('/api/mall/products/hot')
  },

  // 获取推荐商品
  getRecommendProducts() {
    return get('/api/mall/products/recommend')
  },

  // 获取商品评价
  getProductReviews(productId, params = {}) {
    return get(`/api/mall/products/${productId}/reviews`, params)
  },

  // 添加商品评价
  addProductReview(productId, data) {
    return post(`/api/mall/products/${productId}/reviews`, data)
  },

  // 获取商品库存
  getProductStock(productId) {
    return get(`/api/mall/products/${productId}/stock`)
  },

  // 获取商品规格
  getProductSpecs(productId) {
    return get(`/api/mall/products/${productId}/specs`)
  },

  // 获取商品图片
  getProductImages(productId) {
    return get(`/api/mall/products/${productId}/images`)
  },

  // 获取商品详情
  getProductDescription(productId) {
    return get(`/api/mall/products/${productId}/description`)
  },

  // 获取商品相关推荐
  getRelatedProducts(productId) {
    return get(`/api/mall/products/${productId}/related`)
  },

  // 获取商品收藏状态
  getProductFavoriteStatus(productId) {
    return get(`/api/mall/products/${productId}/favorite`)
  },

  // 收藏商品
  favoriteProduct(productId) {
    return post(`/api/mall/products/${productId}/favorite`)
  },

  // 取消收藏商品
  unfavoriteProduct(productId) {
    return post(`/api/mall/products/${productId}/unfavorite`)
  },

  // 获取收藏列表
  getFavoriteProducts(params = {}) {
    return get('/api/mall/favorites', params)
  },

  // 获取浏览历史
  getBrowsingHistory(params = {}) {
    return get('/api/mall/browsing-history', params)
  },

  // 添加浏览记录
  addBrowsingHistory(productId) {
    return post('/api/mall/browsing-history', { productId })
  },

  // 清除浏览历史
  clearBrowsingHistory() {
    return post('/api/mall/browsing-history/clear')
  },

  // 获取商品筛选条件
  getProductFilters() {
    return get('/api/mall/products/filters')
  },

  // 获取商品排序选项
  getProductSortOptions() {
    return get('/api/mall/products/sort-options')
  },

  // 获取商品标签
  getProductTags() {
    return get('/api/mall/products/tags')
  },

  // 获取商品品牌
  getProductBrands() {
    return get('/api/mall/products/brands')
  },

  // 获取商品价格区间
  getProductPriceRanges() {
    return get('/api/mall/products/price-ranges')
  }
}
