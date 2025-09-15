<template>
  <view class="product-detail-page">
    <!-- 商品图片轮播 -->
    <view class="product-images">
      <swiper class="image-swiper" indicator-dots="true" autoplay="false" circular="true">
        <swiper-item v-for="(image, index) in productDetail.images" :key="index">
          <image :src="image" mode="aspectFill" class="product-image" @click="previewImage(index)"></image>
        </swiper-item>
      </swiper>
    </view>

    <!-- 商品基本信息 -->
    <view class="product-info">
      <view class="product-header">
        <text class="product-title">{{ productDetail.title }}</text>
        <view class="product-favorite" @click="toggleFavorite">
          <image :src="isFavorite ? '/static/icons/heart-filled.png' : '/static/icons/heart.png'" mode="aspectFit"></image>
        </view>
      </view>
      
      <text class="product-subtitle">{{ productDetail.subtitle }}</text>
      
      <view class="product-price">
        <text class="current-price">¥{{ productDetail.price }}</text>
        <text class="original-price" v-if="productDetail.originalPrice">¥{{ productDetail.originalPrice }}</text>
        <text class="discount" v-if="productDetail.discount">{{ productDetail.discount }}折</text>
      </view>
      
      <view class="product-meta">
        <view class="meta-item">
          <text class="meta-label">销量</text>
          <text class="meta-value">{{ productDetail.sales }}件</text>
        </view>
        <view class="meta-item">
          <text class="meta-label">评分</text>
          <text class="meta-value">⭐ {{ productDetail.rating }}</text>
        </view>
        <view class="meta-item">
          <text class="meta-label">库存</text>
          <text class="meta-value">{{ productDetail.stock }}件</text>
        </view>
      </view>
    </view>

    <!-- 商品规格选择 -->
    <view class="product-specs" v-if="productDetail.specs && productDetail.specs.length > 0">
      <view class="specs-header">
        <text class="specs-title">选择规格</text>
      </view>
      <view class="specs-list">
        <view class="spec-group" v-for="spec in productDetail.specs" :key="spec.name">
          <text class="spec-name">{{ spec.name }}</text>
          <view class="spec-options">
            <view 
              class="spec-option" 
              v-for="option in spec.options" 
              :key="option.value"
              :class="{ active: selectedSpecs[spec.name] === option.value }"
              @click="selectSpec(spec.name, option.value)"
            >
              <text>{{ option.label }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 商品标签 -->
    <view class="product-tags" v-if="productDetail.tags && productDetail.tags.length > 0">
      <text class="tag" v-for="tag in productDetail.tags" :key="tag">{{ tag }}</text>
    </view>

    <!-- 商品详情 -->
    <view class="product-description">
      <view class="description-header">
        <text class="description-title">商品详情</text>
      </view>
      <view class="description-content">
        <text class="description-text">{{ productDetail.description }}</text>
        <view class="description-images" v-if="productDetail.detailImages && productDetail.detailImages.length > 0">
          <image 
            v-for="(image, index) in productDetail.detailImages" 
            :key="index"
            :src="image" 
            mode="widthFix" 
            class="detail-image"
            @click="previewDetailImage(index)"
          ></image>
        </view>
      </view>
    </view>

    <!-- 商品评价 -->
    <view class="product-reviews" v-if="productReviews.length > 0">
      <view class="reviews-header">
        <text class="reviews-title">用户评价 ({{ productReviews.length }})</text>
        <text class="more-reviews" @click="goToAllReviews">更多 ></text>
      </view>
      <view class="reviews-summary">
        <view class="rating-overview">
          <text class="rating-score">{{ productDetail.rating }}</text>
          <text class="rating-stars">⭐⭐⭐⭐⭐</text>
          <text class="rating-count">{{ productDetail.reviewCount }}条评价</text>
        </view>
        <view class="rating-breakdown">
          <view class="rating-item" v-for="(item, index) in ratingBreakdown" :key="index">
            <text class="rating-label">{{ 5 - index }}星</text>
            <view class="rating-bar">
              <view class="rating-fill" :style="{ width: item.percentage + '%' }"></view>
            </view>
            <text class="rating-percentage">{{ item.percentage }}%</text>
          </view>
        </view>
      </view>
      <view class="reviews-list">
        <view class="review-item" v-for="review in productReviews.slice(0, 3)" :key="review.id">
          <view class="review-header">
            <image :src="review.userAvatar" mode="aspectFill" class="review-avatar"></image>
            <view class="review-info">
              <text class="review-username">{{ review.username }}</text>
              <text class="review-time">{{ review.time }}</text>
            </view>
            <view class="review-rating">
              <text>⭐⭐⭐⭐⭐</text>
            </view>
          </view>
          <text class="review-content">{{ review.content }}</text>
          <view class="review-images" v-if="review.images && review.images.length > 0">
            <image 
              v-for="(image, index) in review.images" 
              :key="index"
              :src="image" 
              mode="aspectFill" 
              class="review-image"
              @click="previewReviewImage(review.images, index)"
            ></image>
          </view>
        </view>
      </view>
    </view>

    <!-- 相关推荐 -->
    <view class="related-products" v-if="relatedProducts.length > 0">
      <view class="section-header">
        <text class="section-title">相关推荐</text>
        <text class="more-btn" @click="goToMall">更多 ></text>
      </view>
      <scroll-view scroll-x="true" class="products-scroll">
        <view class="products-list">
          <view 
            class="product-item" 
            v-for="product in relatedProducts" 
            :key="product.id"
            @click="goToProductDetail(product.id)"
          >
            <image :src="product.image" mode="aspectFill" class="product-image"></image>
            <text class="product-title">{{ product.title }}</text>
            <text class="product-price">¥{{ product.price }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 底部操作栏 -->
    <view class="product-actions">
      <view class="action-left">
        <view class="action-item" @click="goToHome">
          <image src="/static/icons/home.png" mode="aspectFit"></image>
          <text>首页</text>
        </view>
        <view class="action-item" @click="goToCart">
          <image src="/static/icons/cart.png" mode="aspectFit"></image>
          <text>购物车</text>
          <view class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</view>
        </view>
        <view class="action-item" @click="goToService">
          <image src="/static/icons/service.png" mode="aspectFit"></image>
          <text>客服</text>
        </view>
      </view>
      <view class="action-right">
        <button class="add-cart-btn" @click="addToCart" :disabled="!canAddToCart">
          {{ getAddCartText() }}
        </button>
        <button class="buy-now-btn" @click="buyNow" :disabled="!canAddToCart">
          立即购买
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useMallStore } from '@/stores/mall'
import { useCartStore } from '@/stores/cart'

export default {
  name: 'ProductDetailPage',
  setup() {
    const mallStore = useMallStore()
    const cartStore = useCartStore()

    // 响应式数据
    const productDetail = ref({})
    const selectedSpecs = ref({})
    const isFavorite = ref(false)
    const productReviews = ref([])
    const relatedProducts = ref([])
    const ratingBreakdown = ref([])

    // 计算属性
    const cartCount = computed(() => cartStore.totalCount)
    const canAddToCart = computed(() => {
      return productDetail.value.stock > 0
    })

    // 方法
    const loadProductDetail = async () => {
      try {
        // Mock数据
        productDetail.value = {
          id: 1,
          title: '高品质商品名称',
          subtitle: '优质材料，精工制作',
          price: 299.00,
          originalPrice: 399.00,
          discount: 0.75,
          sales: 1234,
          rating: 4.8,
          stock: 50,
          reviewCount: 256,
          images: [
            '/static/images/product1-1.jpg',
            '/static/images/product1-2.jpg',
            '/static/images/product1-3.jpg'
          ],
          specs: [
            {
              name: '颜色',
              options: [
                { label: '红色', value: 'red' },
                { label: '蓝色', value: 'blue' },
                { label: '绿色', value: 'green' }
              ]
            },
            {
              name: '尺寸',
              options: [
                { label: 'S', value: 's' },
                { label: 'M', value: 'm' },
                { label: 'L', value: 'l' }
              ]
            }
          ],
          tags: ['热销', '包邮', '正品保证'],
          description: '这是一款高品质的商品，采用优质材料制作，工艺精湛，品质可靠。适合各种场合使用，是您的不二选择。',
          detailImages: [
            '/static/images/product1-detail1.jpg',
            '/static/images/product1-detail2.jpg'
          ]
        }

        productReviews.value = [
          {
            id: 1,
            username: '用户1',
            userAvatar: '/static/images/avatar1.jpg',
            time: '2天前',
            content: '商品质量很好，包装精美，物流很快，非常满意！',
            rating: 5,
            images: ['/static/images/review1-1.jpg']
          },
          {
            id: 2,
            username: '用户2',
            userAvatar: '/static/images/avatar2.jpg',
            time: '5天前',
            content: '性价比很高，推荐购买！',
            rating: 5,
            images: []
          }
        ]

        relatedProducts.value = [
          {
            id: 2,
            title: '相关商品1',
            price: 199.00,
            image: '/static/images/product2.jpg'
          },
          {
            id: 3,
            title: '相关商品2',
            price: 399.00,
            image: '/static/images/product3.jpg'
          }
        ]

        ratingBreakdown.value = [
          { percentage: 80 },
          { percentage: 15 },
          { percentage: 3 },
          { percentage: 1 },
          { percentage: 1 }
        ]

      } catch (error) {
        console.error('加载商品详情失败:', error)
      }
    }

    const selectSpec = (specName, value) => {
      selectedSpecs.value[specName] = value
    }

    const toggleFavorite = () => {
      isFavorite.value = !isFavorite.value
      uni.showToast({
        title: isFavorite.value ? '已收藏' : '已取消收藏',
        icon: 'success'
      })
    }

    const previewImage = (index) => {
      uni.previewImage({
        current: index,
        urls: productDetail.value.images
      })
    }

    const previewDetailImage = (index) => {
      uni.previewImage({
        current: index,
        urls: productDetail.value.detailImages
      })
    }

    const previewReviewImage = (images, index) => {
      uni.previewImage({
        current: index,
        urls: images
      })
    }

    const addToCart = async () => {
      try {
        await cartStore.addToCart(productDetail.value)
        uni.showToast({
          title: '已加入购物车',
          icon: 'success'
        })
      } catch (error) {
        uni.showToast({
          title: '添加失败',
          icon: 'none'
        })
      }
    }

    const buyNow = () => {
      // 直接购买逻辑
      uni.navigateTo({
        url: `/pages/order/order?productId=${productDetail.value.id}&quantity=1&type=buy_now`
      })
    }

    const getAddCartText = () => {
      if (productDetail.value.stock <= 0) {
        return '缺货'
      }
      return '加入购物车'
    }

    const goToHome = () => {
      uni.switchTab({
        url: '/pages/index/index'
      })
    }

    const goToCart = () => {
      uni.navigateTo({
        url: '/pages/cart/cart'
      })
    }

    const goToService = () => {
      uni.navigateTo({
        url: '/pages/service/service'
      })
    }

    const goToAllReviews = () => {
      uni.navigateTo({
        url: `/pages/product-reviews/product-reviews?productId=${productDetail.value.id}`
      })
    }

    const goToProductDetail = (productId) => {
      uni.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      })
    }

    const goToMall = () => {
      uni.switchTab({
        url: '/pages/mall/mall'
      })
    }

    // 生命周期
    onMounted(() => {
      loadProductDetail()
    })

    return {
      productDetail,
      selectedSpecs,
      isFavorite,
      productReviews,
      relatedProducts,
      ratingBreakdown,
      cartCount,
      canAddToCart,
      selectSpec,
      toggleFavorite,
      previewImage,
      previewDetailImage,
      previewReviewImage,
      addToCart,
      buyNow,
      getAddCartText,
      goToHome,
      goToCart,
      goToService,
      goToAllReviews,
      goToProductDetail,
      goToMall
    }
  }
}
</script>

<style scoped>
.product-detail-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.product-images {
  background: #FFFFFF;
  margin-bottom: 20rpx;
}

.image-swiper {
  height: 600rpx;
}

.product-image {
  width: 100%;
  height: 100%;
}

.product-info {
  background: #FFFFFF;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.product-title {
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  color: #333333;
  line-height: 1.4;
  margin-right: 20rpx;
}

.product-favorite {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-favorite image {
  width: 40rpx;
  height: 40rpx;
}

.product-subtitle {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 20rpx;
  display: block;
}

.product-price {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.current-price {
  font-size: 48rpx;
  font-weight: 700;
  color: #FF6B6B;
  margin-right: 20rpx;
}

.original-price {
  font-size: 28rpx;
  color: #999999;
  text-decoration: line-through;
  margin-right: 20rpx;
}

.discount {
  background: #FF6B6B;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
}

.product-meta {
  display: flex;
  gap: 40rpx;
}

.meta-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.meta-label {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.meta-value {
  font-size: 24rpx;
  color: #333333;
  font-weight: 500;
}

.product-specs,
.product-tags,
.product-description,
.product-reviews,
.related-products {
  background: #FFFFFF;
  margin-bottom: 20rpx;
  padding: 30rpx;
}

.specs-header,
.description-header,
.reviews-header,
.section-header {
  margin-bottom: 20rpx;
}

.specs-title,
.description-title,
.reviews-title,
.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.spec-group {
  margin-bottom: 24rpx;
}

.spec-name {
  font-size: 26rpx;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.spec-option {
  padding: 12rpx 24rpx;
  border: 2rpx solid #E9ECEF;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #666666;
}

.spec-option.active {
  border-color: #FF6B6B;
  color: #FF6B6B;
  background: #FFF5F5;
}

.product-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.tag {
  background: #F8F9FA;
  color: #666666;
  font-size: 22rpx;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
}

.description-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  margin-bottom: 20rpx;
  display: block;
}

.description-images {
  display: flex;
  flex-direction: column;
}

.detail-image {
  width: 100%;
  margin-bottom: 16rpx;
  border-radius: 8rpx;
}

.reviews-summary {
  display: flex;
  margin-bottom: 30rpx;
  padding: 20rpx;
  background: #F8F9FA;
  border-radius: 12rpx;
}

.rating-overview {
  text-align: center;
  margin-right: 40rpx;
}

.rating-score {
  font-size: 48rpx;
  font-weight: 700;
  color: #FF6B6B;
  margin-bottom: 8rpx;
  display: block;
}

.rating-stars {
  font-size: 24rpx;
  margin-bottom: 8rpx;
  display: block;
}

.rating-count {
  font-size: 22rpx;
  color: #999999;
}

.rating-breakdown {
  flex: 1;
}

.rating-item {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.rating-label {
  font-size: 22rpx;
  color: #666666;
  width: 60rpx;
  margin-right: 16rpx;
}

.rating-bar {
  flex: 1;
  height: 8rpx;
  background: #F0F0F0;
  border-radius: 4rpx;
  margin-right: 16rpx;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  background: #FF6B6B;
  border-radius: 4rpx;
}

.rating-percentage {
  font-size: 22rpx;
  color: #666666;
  width: 60rpx;
  text-align: right;
}

.reviews-list {
  display: flex;
  flex-direction: column;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.review-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  margin-right: 16rpx;
}

.review-info {
  flex: 1;
}

.review-username {
  font-size: 26rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 4rpx;
  display: block;
}

.review-time {
  font-size: 22rpx;
  color: #999999;
}

.review-rating {
  font-size: 20rpx;
}

.review-content {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
  margin-bottom: 12rpx;
  display: block;
}

.review-images {
  display: flex;
  gap: 12rpx;
}

.review-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 8rpx;
}

.products-scroll {
  white-space: nowrap;
}

.products-list {
  display: flex;
  gap: 20rpx;
}

.product-item {
  width: 200rpx;
  flex-shrink: 0;
}

.product-item .product-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  margin-bottom: 12rpx;
}

.product-item .product-title {
  font-size: 24rpx;
  color: #333333;
  margin-bottom: 8rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-item .product-price {
  font-size: 26rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.more-reviews,
.more-btn {
  color: #FF6B6B;
  font-size: 24rpx;
}

.product-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #F0F0F0;
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.action-left {
  display: flex;
  gap: 40rpx;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.action-item image {
  width: 40rpx;
  height: 40rpx;
  margin-bottom: 8rpx;
}

.action-item text {
  font-size: 20rpx;
  color: #666666;
}

.cart-badge {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  background: #FF4444;
  color: #FFFFFF;
  font-size: 18rpx;
  padding: 4rpx 8rpx;
  border-radius: 12rpx;
  min-width: 24rpx;
  text-align: center;
}

.action-right {
  display: flex;
  gap: 16rpx;
  flex: 1;
}

.add-cart-btn,
.buy-now-btn {
  flex: 1;
  padding: 24rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.add-cart-btn {
  background: #F8F9FA;
  color: #666666;
  border: 2rpx solid #E9ECEF;
}

.buy-now-btn {
  background: #FF6B6B;
  color: #FFFFFF;
}

.add-cart-btn:disabled,
.buy-now-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
