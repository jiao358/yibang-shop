<template>
  <view class="mall-page">
    <!-- 搜索栏 -->
    <view class="search-bar">
      <view class="search-input">
        <image src="/static/icons/search.png" mode="aspectFit"></image>
        <input 
          type="text" 
          placeholder="搜索商品" 
          v-model="searchKeyword"
          @confirm="onSearch"
        />
      </view>
    </view>

    <!-- 分类导航 -->
    <view class="category-nav">
      <scroll-view scroll-x="true" class="category-scroll">
        <view class="category-list">
          <view 
            class="category-item" 
            :class="{ active: currentCategory === 'all' }"
            @click="changeCategory('all')"
          >
            全部
          </view>
          <view 
            class="category-item" 
            v-for="category in categories" 
            :key="category.id"
            :class="{ active: currentCategory === category.id }"
            @click="changeCategory(category.id)"
          >
            {{ category.name }}
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 商品列表 -->
    <view class="product-list">
      <view 
        class="product-item" 
        v-for="product in filteredProducts" 
        :key="product.id"
        @click="goToProductDetail(product.id)"
      >
        <image 
          class="product-image" 
          :src="product.image" 
          mode="aspectFill"
          :lazy-load="true"
        ></image>
        
        <view class="product-info">
          <text class="product-title">{{ product.title }}</text>
          <text class="product-desc">{{ product.description }}</text>
          
          <view class="product-price">
            <text class="current-price">¥{{ product.price }}</text>
            <text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
            <text class="discount" v-if="product.discount">{{ product.discount }}折</text>
          </view>
          
          <view class="product-meta">
            <text class="sales">已售{{ product.sales }}件</text>
            <text class="rating">⭐ {{ product.rating }}</text>
          </view>
          
          <view class="product-tags" v-if="product.tags && product.tags.length > 0">
            <text 
              class="tag" 
              v-for="tag in product.tags" 
              :key="tag"
            >
              {{ tag }}
            </text>
          </view>
        </view>
        
        <view class="product-actions">
          <button 
            class="add-cart-btn" 
            @click.stop="addToCart(product)"
            :disabled="!canAddToCart(product)"
          >
            {{ getAddCartText(product) }}
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="filteredProducts.length === 0 && !loading">
      <image src="/static/images/empty-product.png" mode="aspectFit"></image>
      <text class="empty-text">暂无商品</text>
      <text class="empty-desc">请尝试其他分类或关键词</text>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore && filteredProducts.length > 0">
      <text v-if="loading">加载中...</text>
      <text v-else @click="loadMore">加载更多</text>
    </view>

    <!-- 购物车悬浮按钮 -->
    <view class="cart-float" @click="goToCart" v-if="cartCount > 0">
      <image src="/static/icons/cart.png" mode="aspectFit"></image>
      <view class="cart-badge">{{ cartCount }}</view>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useMallStore } from '@/stores/mall'
import { useCartStore } from '@/stores/cart'

export default {
  name: 'MallPage',
  setup() {
    const mallStore = useMallStore()
    const cartStore = useCartStore()

    // 响应式数据
    const searchKeyword = ref('')
    const currentCategory = ref('all')
    const loading = ref(false)
    const hasMore = ref(true)
    const page = ref(1)

    // 计算属性
    const categories = computed(() => mallStore.categories)
    const products = computed(() => mallStore.products)
    const cartCount = computed(() => cartStore.totalCount)

    const filteredProducts = computed(() => {
      let filtered = products.value

      // 分类筛选
      if (currentCategory.value !== 'all') {
        filtered = filtered.filter(product => product.categoryId === currentCategory.value)
      }

      // 搜索筛选
      if (searchKeyword.value) {
        const keyword = searchKeyword.value.toLowerCase()
        filtered = filtered.filter(product => 
          product.title.toLowerCase().includes(keyword) ||
          product.description.toLowerCase().includes(keyword) ||
          (product.tags && product.tags.some(tag => tag.toLowerCase().includes(keyword)))
        )
      }

      return filtered
    })

    // 方法
    const loadCategories = async () => {
      try {
        await mallStore.getCategories()
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    const loadProducts = async (reset = false) => {
      if (loading.value) return
      
      loading.value = true
      
      try {
        if (reset) {
          page.value = 1
          hasMore.value = true
        }
        
        await mallStore.getProducts({
          page: page.value,
          categoryId: currentCategory.value === 'all' ? null : currentCategory.value,
          keyword: searchKeyword.value || null
        })
        
        if (reset) {
          mallStore.products = mallStore.products
        } else {
          mallStore.products = [...mallStore.products, ...mallStore.products]
        }
        
        hasMore.value = mallStore.products.length < mallStore.total
        page.value++
        
      } catch (error) {
        console.error('加载商品失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }

    const loadMore = () => {
      loadProducts(false)
    }

    const changeCategory = (categoryId) => {
      currentCategory.value = categoryId
      loadProducts(true)
    }

    const onSearch = () => {
      loadProducts(true)
    }

    const goToProductDetail = (productId) => {
      uni.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      })
    }

    const goToCart = () => {
      uni.navigateTo({
        url: '/pages/cart/cart'
      })
    }

    const addToCart = async (product) => {
      try {
        await cartStore.addToCart(product)
        
        uni.showToast({
          title: '已加入购物车',
          icon: 'success'
        })
        
      } catch (error) {
        uni.showToast({
          title: error.message || '添加失败',
          icon: 'none'
        })
      }
    }

    const canAddToCart = (product) => {
      // 检查用户等级限制
      const userLevel = uni.getStorageSync('userLevel') || 0
      if (product.levelRequired && userLevel < product.levelRequired) {
        return false
      }
      
      // 检查库存
      return product.stock > 0
    }

    const getAddCartText = (product) => {
      if (!canAddToCart(product)) {
        return '不可购买'
      }
      
      if (product.stock <= 0) {
        return '缺货'
      }
      
      return '加入购物车'
    }

    // 生命周期
    onMounted(() => {
      loadCategories()
      loadProducts(true)
    })

    return {
      searchKeyword,
      currentCategory,
      categories,
      filteredProducts,
      cartCount,
      loading,
      hasMore,
      changeCategory,
      onSearch,
      loadMore,
      goToProductDetail,
      goToCart,
      addToCart,
      canAddToCart,
      getAddCartText
    }
  }
}
</script>

<style scoped>
.mall-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.search-bar {
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.search-input {
  display: flex;
  align-items: center;
  background: #F8F9FA;
  border-radius: 24rpx;
  padding: 16rpx 24rpx;
}

.search-input image {
  width: 32rpx;
  height: 32rpx;
  margin-right: 16rpx;
}

.search-input input {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.category-nav {
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.category-scroll {
  white-space: nowrap;
}

.category-list {
  display: flex;
  padding: 20rpx 30rpx;
  gap: 40rpx;
}

.category-item {
  color: #666666;
  font-size: 28rpx;
  font-weight: 500;
  padding: 12rpx 0;
  white-space: nowrap;
  position: relative;
}

.category-item.active {
  color: #FF6B6B;
  font-weight: 600;
}

.category-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: #FF6B6B;
  border-radius: 2rpx;
}

.product-list {
  padding: 20rpx 30rpx;
}

.product-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 400rpx;
}

.product-info {
  padding: 24rpx;
}

.product-title {
  color: #333333;
  font-size: 30rpx;
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: 12rpx;
  display: block;
}

.product-desc {
  color: #666666;
  font-size: 24rpx;
  line-height: 1.4;
  margin-bottom: 16rpx;
  display: block;
}

.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.current-price {
  color: #FF6B6B;
  font-size: 36rpx;
  font-weight: 700;
  margin-right: 16rpx;
}

.original-price {
  color: #999999;
  font-size: 24rpx;
  text-decoration: line-through;
  margin-right: 16rpx;
}

.discount {
  background: #FF6B6B;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 6rpx;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.sales,
.rating {
  color: #999999;
  font-size: 22rpx;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 12rpx;
  margin-bottom: 20rpx;
}

.tag {
  background: #F8F9FA;
  color: #666666;
  font-size: 20rpx;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
}

.product-actions {
  padding: 0 24rpx 24rpx;
}

.add-cart-btn {
  width: 100%;
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 20rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.add-cart-btn:disabled {
  background: #E9ECEF;
  color: #6C757D;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 60rpx;
  text-align: center;
}

.empty-state image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 40rpx;
}

.empty-text {
  color: #666666;
  font-size: 32rpx;
  font-weight: 500;
  margin-bottom: 16rpx;
}

.empty-desc {
  color: #999999;
  font-size: 26rpx;
}

.load-more {
  text-align: center;
  padding: 40rpx;
  color: #666666;
  font-size: 26rpx;
}

.cart-float {
  position: fixed;
  right: 30rpx;
  bottom: 150rpx;
  width: 100rpx;
  height: 100rpx;
  background: #FF6B6B;
  border-radius: 50rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 24rpx rgba(255, 107, 107, 0.3);
  z-index: 999;
}

.cart-float image {
  width: 48rpx;
  height: 48rpx;
}

.cart-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  background: #FF4444;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 12rpx;
  min-width: 24rpx;
  text-align: center;
}
</style>
