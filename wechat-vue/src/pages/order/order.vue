<template>
  <view class="order-page">
    <!-- 订单状态筛选 -->
    <view class="order-filter">
      <scroll-view scroll-x="true" class="filter-scroll">
        <view class="filter-list">
          <view 
            class="filter-item" 
            :class="{ active: currentFilter === 'all' }"
            @click="changeFilter('all')"
          >
            全部
          </view>
          <view 
            class="filter-item" 
            v-for="filter in orderFilters" 
            :key="filter.value"
            :class="{ active: currentFilter === filter.value }"
            @click="changeFilter(filter.value)"
          >
            {{ filter.label }}
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view 
        class="order-item" 
        v-for="order in filteredOrders" 
        :key="order.id"
        @click="goToOrderDetail(order.id)"
      >
        <!-- 订单头部 -->
        <view class="order-header">
          <text class="order-number">订单号：{{ order.orderNumber }}</text>
          <view class="order-status" :class="order.status">
            {{ getOrderStatusText(order.status) }}
          </view>
        </view>

        <!-- 商品信息 -->
        <view class="order-products">
          <view 
            class="product-item" 
            v-for="product in order.products" 
            :key="product.id"
          >
            <image :src="product.image" mode="aspectFill" class="product-image"></image>
            <view class="product-info">
              <text class="product-title">{{ product.title }}</text>
              <text class="product-spec" v-if="product.spec">{{ product.spec }}</text>
              <view class="product-price">
                <text class="price">¥{{ product.price }}</text>
                <text class="quantity">x{{ product.quantity }}</text>
              </view>
            </view>
          </view>
        </view>

        <!-- 订单金额 -->
        <view class="order-amount">
          <view class="amount-item">
            <text class="amount-label">商品金额</text>
            <text class="amount-value">¥{{ order.productAmount }}</text>
          </view>
          <view class="amount-item" v-if="order.shippingFee > 0">
            <text class="amount-label">运费</text>
            <text class="amount-value">¥{{ order.shippingFee }}</text>
          </view>
          <view class="amount-item" v-if="order.discount > 0">
            <text class="amount-label">优惠</text>
            <text class="amount-value discount">-¥{{ order.discount }}</text>
          </view>
          <view class="amount-item total">
            <text class="amount-label">实付金额</text>
            <text class="amount-value">¥{{ order.totalAmount }}</text>
          </view>
        </view>

        <!-- 订单时间 -->
        <view class="order-time">
          <text class="time-label">下单时间：{{ order.createTime }}</text>
          <text class="time-label" v-if="order.payTime">支付时间：{{ order.payTime }}</text>
        </view>

        <!-- 订单操作 -->
        <view class="order-actions">
          <button 
            class="action-btn secondary" 
            v-if="order.status === 'pending'"
            @click.stop="cancelOrder(order.id)"
          >
            取消订单
          </button>
          <button 
            class="action-btn primary" 
            v-if="order.status === 'pending'"
            @click.stop="payOrder(order.id)"
          >
            立即支付
          </button>
          <button 
            class="action-btn secondary" 
            v-if="order.status === 'shipped'"
            @click.stop="confirmReceipt(order.id)"
          >
            确认收货
          </button>
          <button 
            class="action-btn secondary" 
            v-if="order.status === 'completed'"
            @click.stop="goToProductDetail(order.products[0].id)"
          >
            再次购买
          </button>
          <button 
            class="action-btn secondary" 
            v-if="order.status === 'completed'"
            @click.stop="goToReview(order.id)"
          >
            评价
          </button>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="filteredOrders.length === 0">
      <image src="/static/images/empty-order.png" mode="aspectFit"></image>
      <text class="empty-text">暂无订单</text>
      <text class="empty-desc">快去购买心仪的商品吧</text>
      <button class="go-shopping-btn" @click="goToMall">去购物</button>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore && filteredOrders.length > 0">
      <text v-if="loading">加载中...</text>
      <text v-else @click="loadMore">加载更多</text>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'OrderPage',
  setup() {
    // 响应式数据
    const currentFilter = ref('all')
    const loading = ref(false)
    const hasMore = ref(true)
    const orders = ref([])

    // 订单筛选选项
    const orderFilters = ref([
      { label: '待支付', value: 'pending' },
      { label: '待发货', value: 'paid' },
      { label: '待收货', value: 'shipped' },
      { label: '已完成', value: 'completed' },
      { label: '已取消', value: 'cancelled' }
    ])

    // 计算属性
    const filteredOrders = computed(() => {
      if (currentFilter.value === 'all') {
        return orders.value
      }
      return orders.value.filter(order => order.status === currentFilter.value)
    })

    // 方法
    const loadOrders = async () => {
      try {
        loading.value = true
        
        // Mock数据
        orders.value = [
          {
            id: 1,
            orderNumber: 'YB202401150001',
            status: 'pending',
            createTime: '2024-01-15 14:30:25',
            payTime: '',
            productAmount: 299.00,
            shippingFee: 0,
            discount: 30.00,
            totalAmount: 269.00,
            products: [
              {
                id: 1,
                title: '高品质商品名称',
                spec: '颜色：红色 尺寸：M',
                price: 299.00,
                quantity: 1,
                image: '/static/images/product1.jpg'
              }
            ]
          },
          {
            id: 2,
            orderNumber: 'YB202401140002',
            status: 'shipped',
            createTime: '2024-01-14 09:15:30',
            payTime: '2024-01-14 09:20:15',
            productAmount: 199.00,
            shippingFee: 10.00,
            discount: 0,
            totalAmount: 209.00,
            products: [
              {
                id: 2,
                title: '另一个商品名称',
                spec: '颜色：蓝色 尺寸：L',
                price: 199.00,
                quantity: 1,
                image: '/static/images/product2.jpg'
              }
            ]
          },
          {
            id: 3,
            orderNumber: 'YB202401130003',
            status: 'completed',
            createTime: '2024-01-13 16:45:20',
            payTime: '2024-01-13 16:50:10',
            productAmount: 399.00,
            shippingFee: 0,
            discount: 50.00,
            totalAmount: 349.00,
            products: [
              {
                id: 3,
                title: '第三个商品名称',
                spec: '颜色：绿色 尺寸：S',
                price: 399.00,
                quantity: 1,
                image: '/static/images/product3.jpg'
              }
            ]
          },
          {
            id: 4,
            orderNumber: 'YB202401120004',
            status: 'cancelled',
            createTime: '2024-01-12 10:20:15',
            payTime: '',
            productAmount: 599.00,
            shippingFee: 15.00,
            discount: 0,
            totalAmount: 614.00,
            products: [
              {
                id: 4,
                title: '第四个商品名称',
                spec: '颜色：黑色 尺寸：XL',
                price: 599.00,
                quantity: 1,
                image: '/static/images/product4.jpg'
              }
            ]
          }
        ]

        hasMore.value = false
        
      } catch (error) {
        console.error('加载订单失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }

    const changeFilter = (filter) => {
      currentFilter.value = filter
    }

    const loadMore = () => {
      loadOrders()
    }

    const getOrderStatusText = (status) => {
      const statusMap = {
        'pending': '待支付',
        'paid': '待发货',
        'shipped': '待收货',
        'completed': '已完成',
        'cancelled': '已取消',
        'refunded': '已退款'
      }
      return statusMap[status] || '未知状态'
    }

    const goToOrderDetail = (orderId) => {
      uni.navigateTo({
        url: `/pages/order-detail/order-detail?id=${orderId}`
      })
    }

    const goToProductDetail = (productId) => {
      uni.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      })
    }

    const cancelOrder = (orderId) => {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消这个订单吗？',
        success: (res) => {
          if (res.confirm) {
            // 更新订单状态
            const order = orders.value.find(o => o.id === orderId)
            if (order) {
              order.status = 'cancelled'
            }
            uni.showToast({
              title: '订单已取消',
              icon: 'success'
            })
          }
        }
      })
    }

    const payOrder = (orderId) => {
      uni.navigateTo({
        url: `/pages/payment/payment?orderId=${orderId}`
      })
    }

    const confirmReceipt = (orderId) => {
      uni.showModal({
        title: '确认收货',
        content: '确定已收到商品吗？',
        success: (res) => {
          if (res.confirm) {
            // 更新订单状态
            const order = orders.value.find(o => o.id === orderId)
            if (order) {
              order.status = 'completed'
            }
            uni.showToast({
              title: '确认收货成功',
              icon: 'success'
            })
          }
        }
      })
    }

    const goToReview = (orderId) => {
      uni.navigateTo({
        url: `/pages/order-review/order-review?orderId=${orderId}`
      })
    }

    const goToMall = () => {
      uni.switchTab({
        url: '/pages/mall/mall'
      })
    }

    // 生命周期
    onMounted(() => {
      loadOrders()
    })

    return {
      currentFilter,
      orderFilters,
      filteredOrders,
      loading,
      hasMore,
      changeFilter,
      loadMore,
      getOrderStatusText,
      goToOrderDetail,
      goToProductDetail,
      cancelOrder,
      payOrder,
      confirmReceipt,
      goToReview,
      goToMall
    }
  }
}
</script>

<style scoped>
.order-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.order-filter {
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-list {
  display: flex;
  padding: 20rpx 30rpx;
  gap: 40rpx;
}

.filter-item {
  color: #666666;
  font-size: 28rpx;
  font-weight: 500;
  padding: 12rpx 0;
  white-space: nowrap;
  position: relative;
}

.filter-item.active {
  color: #FF6B6B;
  font-weight: 600;
}

.filter-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: #FF6B6B;
  border-radius: 2rpx;
}

.order-list {
  padding: 20rpx 30rpx;
}

.order-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.order-number {
  font-size: 24rpx;
  color: #999999;
}

.order-status {
  font-size: 26rpx;
  font-weight: 600;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
}

.order-status.pending {
  background: #FFF3CD;
  color: #FFC107;
}

.order-status.paid {
  background: #D1ECF1;
  color: #17A2B8;
}

.order-status.shipped {
  background: #E8F5E8;
  color: #28A745;
}

.order-status.completed {
  background: #D4EDDA;
  color: #155724;
}

.order-status.cancelled {
  background: #F8D7DA;
  color: #DC3545;
}

.order-products {
  margin-bottom: 20rpx;
}

.product-item {
  display: flex;
  margin-bottom: 16rpx;
}

.product-item:last-child {
  margin-bottom: 0;
}

.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 8rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-spec {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 12rpx;
  display: block;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 26rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.quantity {
  font-size: 24rpx;
  color: #666666;
}

.order-amount {
  background: #F8F9FA;
  padding: 20rpx;
  border-radius: 12rpx;
  margin-bottom: 20rpx;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8rpx;
}

.amount-item:last-child {
  margin-bottom: 0;
}

.amount-item.total {
  border-top: 1rpx solid #E9ECEF;
  padding-top: 12rpx;
  margin-top: 12rpx;
}

.amount-label {
  font-size: 24rpx;
  color: #666666;
}

.amount-value {
  font-size: 24rpx;
  color: #333333;
  font-weight: 500;
}

.amount-value.discount {
  color: #28A745;
}

.amount-item.total .amount-value {
  font-size: 28rpx;
  color: #FF6B6B;
  font-weight: 700;
}

.order-time {
  margin-bottom: 20rpx;
}

.time-label {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
  display: block;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16rpx;
}

.action-btn {
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 500;
  border: none;
}

.action-btn.primary {
  background: #FF6B6B;
  color: #FFFFFF;
}

.action-btn.secondary {
  background: #F8F9FA;
  color: #666666;
  border: 2rpx solid #E9ECEF;
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
  margin-bottom: 40rpx;
}

.go-shopping-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 20rpx 40rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.load-more {
  text-align: center;
  padding: 40rpx;
  color: #666666;
  font-size: 26rpx;
}
</style>
