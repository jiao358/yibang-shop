import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { cartApi } from '@/api/cart'

export const useCartStore = defineStore('cart', () => {
  // 状态
  const cartItems = ref([])
  const loading = ref(false)

  // 计算属性
  const totalCount = computed(() => 
    cartItems.value.reduce((total, item) => total + item.quantity, 0)
  )
  
  const totalPrice = computed(() => 
    cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
  )
  
  const selectedItems = computed(() => 
    cartItems.value.filter(item => item.selected)
  )
  
  const selectedCount = computed(() => 
    selectedItems.value.reduce((total, item) => total + item.quantity, 0)
  )
  
  const selectedPrice = computed(() => 
    selectedItems.value.reduce((total, item) => total + (item.price * item.quantity), 0)
  )

  // 方法
  const getCartItems = async () => {
    try {
      loading.value = true
      const response = await cartApi.getCartItems()
      cartItems.value = response.items
      return response
    } catch (error) {
      console.error('获取购物车失败:', error)
      throw error
    } finally {
      loading.value = false
    }
  }

  const addToCart = async (product) => {
    try {
      const existingItem = cartItems.value.find(item => item.productId === product.id)
      
      if (existingItem) {
        // 更新数量
        await updateCartItem(existingItem.id, existingItem.quantity + 1)
      } else {
        // 添加新商品
        const response = await cartApi.addToCart({
          productId: product.id,
          quantity: 1,
          price: product.price
        })
        
        cartItems.value.push({
          id: response.item.id,
          productId: product.id,
          title: product.title,
          image: product.image,
          price: product.price,
          quantity: 1,
          selected: true,
          stock: product.stock
        })
      }
      
      return response
    } catch (error) {
      console.error('添加到购物车失败:', error)
      throw error
    }
  }

  const updateCartItem = async (itemId, quantity) => {
    try {
      const response = await cartApi.updateCartItem(itemId, { quantity })
      
      const itemIndex = cartItems.value.findIndex(item => item.id === itemId)
      if (itemIndex !== -1) {
        cartItems.value[itemIndex].quantity = quantity
      }
      
      return response
    } catch (error) {
      console.error('更新购物车商品失败:', error)
      throw error
    }
  }

  const removeCartItem = async (itemId) => {
    try {
      await cartApi.removeCartItem(itemId)
      
      const itemIndex = cartItems.value.findIndex(item => item.id === itemId)
      if (itemIndex !== -1) {
        cartItems.value.splice(itemIndex, 1)
      }
      
    } catch (error) {
      console.error('删除购物车商品失败:', error)
      throw error
    }
  }

  const toggleItemSelection = (itemId) => {
    const item = cartItems.value.find(item => item.id === itemId)
    if (item) {
      item.selected = !item.selected
    }
  }

  const selectAll = (selected) => {
    cartItems.value.forEach(item => {
      item.selected = selected
    })
  }

  const clearCart = async () => {
    try {
      await cartApi.clearCart()
      cartItems.value = []
    } catch (error) {
      console.error('清空购物车失败:', error)
      throw error
    }
  }

  const getCartSummary = async () => {
    try {
      const response = await cartApi.getCartSummary()
      return response
    } catch (error) {
      console.error('获取购物车汇总失败:', error)
      throw error
    }
  }

  const validateCart = async () => {
    try {
      const response = await cartApi.validateCart()
      return response
    } catch (error) {
      console.error('验证购物车失败:', error)
      throw error
    }
  }

  const clearCartItems = () => {
    cartItems.value = []
  }

  return {
    // 状态
    cartItems,
    loading,
    
    // 计算属性
    totalCount,
    totalPrice,
    selectedItems,
    selectedCount,
    selectedPrice,
    
    // 方法
    getCartItems,
    addToCart,
    updateCartItem,
    removeCartItem,
    toggleItemSelection,
    selectAll,
    clearCart,
    getCartSummary,
    validateCart,
    clearCartItems
  }
})
