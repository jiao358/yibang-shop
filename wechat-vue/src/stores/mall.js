import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { mallApi } from '@/api/mall'

export const useMallStore = defineStore('mall', () => {
  // 状态
  const categories = ref([])
  const products = ref([])
  const currentProduct = ref(null)
  const total = ref(0)
  const loading = ref(false)

  // 计算属性
  const hotProducts = computed(() => 
    products.value.filter(product => product.isHot)
  )
  
  const discountProducts = computed(() => 
    products.value.filter(product => (product.discount || 1) < 1)
  )

  // 方法
  const getCategories = async () => {
    try {
      const res = await mallApi.getCategories()
      categories.value = res.data?.records || res.data?.categories || []
      return res
    } catch (error) {
      console.error('获取分类失败:', error)
      throw error
    }
  }

  const getProducts = async (params = {}) => {
    try {
      loading.value = true
      const res = await mallApi.getProducts(params)
      const records = res.data?.records || []
      const totalCount = res.data?.total || 0

      if (params.page === 1) {
        products.value = records
      } else {
        products.value = [...products.value, ...records]
      }

      total.value = totalCount
      return res
    } catch (error) {
      console.error('获取商品列表失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const getProductDetail = async (productId) => {
    try {
      const res = await mallApi.getProductDetail(productId)
      currentProduct.value = res.data?.product || res.data || null
      return res
    } catch (error) {
      console.error('获取商品详情失败:', error)
      throw error
    }
  }

  const searchProducts = async (keyword, params = {}) => {
    try {
      const res = await mallApi.searchProducts(keyword, params)
      products.value = res.data?.records || []
      total.value = res.data?.total || 0
      return res
    } catch (error) {
      console.error('搜索商品失败:', error)
      throw error
    }
  }

  const getHotProducts = async () => {
    try {
      const res = await mallApi.getHotProducts()
      return res
    } catch (error) {
      console.error('获取热门商品失败:', error)
      throw error
    }
  }

  const getRecommendProducts = async () => {
    try {
      const res = await mallApi.getRecommendProducts()
      return res
    } catch (error) {
      console.error('获取推荐商品失败:', error)
      throw error
    }
  }

  const getProductReviews = async (productId, params = {}) => {
    try {
      const res = await mallApi.getProductReviews(productId, params)
      return res
    } catch (error) {
      console.error('获取商品评价失败:', error)
      throw error
    }
  }

  const addProductReview = async (productId, data) => {
    try {
      const res = await mallApi.addProductReview(productId, data)
      return res
    } catch (error) {
      console.error('添加商品评价失败:', error)
      throw error
    }
  }

  const getProductStock = async (productId) => {
    try {
      const res = await mallApi.getProductStock(productId)
      return res
    } catch (error) {
      console.error('获取商品库存失败:', error)
      throw error
    }
  }

  const clearProducts = () => {
    products.value = []
    currentProduct.value = null
    total.value = 0
  }

  return {
    // 状态
    categories,
    products,
    currentProduct,
    total,
    loading,
    
    // 计算属性
    hotProducts,
    discountProducts,
    
    // 方法
    getCategories,
    getProducts,
    getProductDetail,
    searchProducts,
    getHotProducts,
    getRecommendProducts,
    getProductReviews,
    addProductReview,
    getProductStock,
    clearProducts
  }
})
