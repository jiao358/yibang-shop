<template>
  <view class="order-page">
    <!-- 状态栏 -->
    <view class="status-bar">
      <text class="time">17:29</text>
      <view class="battery">
        <text class="battery-text">100</text>
        <view class="battery-bar">
          <view class="battery-fill"></view>
        </view>
      </view>
    </view>

    <!-- 头部 -->
    <view class="header">
      <text class="title">我的订单</text>
      <view class="header-actions">
        <image src="/static/icons/search.png" class="action-icon" mode="aspectFit"></image>
        <image src="/static/icons/more.png" class="action-icon" mode="aspectFit"></image>
      </view>
    </view>

    <!-- 订单状态标签 -->
    <view class="order-tabs">
      <view 
        v-for="(tab, index) in orderTabs" 
        :key="index"
        class="tab-item"
        :class="{ active: currentTab === index }"
        @click="switchTab(index)"
      >
        <text class="tab-text">{{ tab.name }}</text>
        <view v-if="tab.count > 0" class="tab-badge">{{ tab.count }}</view>
      </view>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view v-if="currentOrders.length === 0" class="empty-state">
        <image src="/static/images/empty-order.png" class="empty-image" mode="aspectFit"></image>
        <text class="empty-text">暂无订单</text>
        <text class="empty-desc">快去完成任务获取收益吧</text>
      </view>
      
      <view v-else>
        <view 
          v-for="order in currentOrders" 
          :key="order.id"
          class="order-item"
          @click="goToOrderDetail(order.id)"
        >
          <!-- 订单头部 -->
          <view class="order-header">
            <view class="order-info">
              <text class="order-number">订单号：{{ order.orderNo }}</text>
              <text class="order-time">{{ order.createTime }}</text>
            </view>
            <view class="order-status" :class="order.status">
              <text>{{ getOrderStatusText(order.status) }}</text>
            </view>
          </view>

          <!-- 商品信息 -->
          <view class="product-info">
            <image :src="order.productImage" class="product-image" mode="aspectFill"></image>
            <view class="product-details">
              <text class="product-name">{{ order.productName }}</text>
              <text class="product-spec">{{ order.productSpec }}</text>
              <view class="product-price">
                <text class="price">¥{{ order.price }}</text>
                <text class="quantity">x{{ order.quantity }}</text>
              </view>
            </view>
          </view>

          <!-- 订单操作 -->
          <view class="order-actions">
            <view class="total-info">
              <text class="total-label">实付款：</text>
              <text class="total-price">¥{{ order.totalAmount }}</text>
            </view>
            <view class="action-buttons">
              <button 
                v-if="order.status === 'pending'"
                class="action-btn cancel"
                @click.stop="cancelOrder(order.id)"
              >
                取消订单
              </button>
              <button 
                v-if="order.status === 'pending'"
                class="action-btn pay"
                @click.stop="payOrder(order.id)"
              >
                立即支付
              </button>
              <button 
                v-if="order.status === 'paid'"
                class="action-btn confirm"
                @click.stop="confirmOrder(order.id)"
              >
                确认收货
              </button>
              <button 
                v-if="order.status === 'completed'"
                class="action-btn review"
                @click.stop="reviewOrder(order.id)"
              >
                评价
              </button>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useOrderStore } from '@/stores/order'

export default {
  name: 'OrderPage',
  setup() {
    const orderStore = useOrderStore()
    
    // 当前选中的标签
    const currentTab = ref(0)
    
    // 订单标签
    const orderTabs = ref([
      { name: '全部', count: 0 },
      { name: '待支付', count: 0 },
      { name: '待发货', count: 0 },
      { name: '待收货', count: 0 },
      { name: '已完成', count: 0 }
    ])
    
    // 模拟订单数据
    const orders = ref([
      {
        id: 1,
        orderNo: 'YB202501140001',
        createTime: '2025-01-14 17:30',
        status: 'pending',
        productName: '苹果iPhone 15 Pro',
        productSpec: '深空黑色 256GB',
        productImage: '/static/images/iphone15.jpg',
        price: 8999,
        quantity: 1,
        totalAmount: 8999
      },
      {
        id: 2,
        orderNo: 'YB202501140002',
        createTime: '2025-01-14 16:45',
        status: 'paid',
        productName: '小米13 Ultra',
        productSpec: '陶瓷黑 512GB',
        productImage: '/static/images/mi13.jpg',
        price: 5999,
        quantity: 1,
        totalAmount: 5999
      },
      {
        id: 3,
        orderNo: 'YB202501140003',
        createTime: '2025-01-14 15:20',
        status: 'completed',
        productName: '华为Mate 60 Pro',
        productSpec: '雅川青 256GB',
        productImage: '/static/images/mate60.jpg',
        price: 6999,
        quantity: 1,
        totalAmount: 6999
      }
    ])
    
    // 当前显示的订单
    const currentOrders = computed(() => {
      if (currentTab.value === 0) {
        return orders.value
      }
      const statusMap = ['', 'pending', 'paid', 'shipped', 'completed']
      const status = statusMap[currentTab.value]
      return orders.value.filter(order => order.status === status)
    })
    
    // 切换标签
    const switchTab = (index) => {
      currentTab.value = index
    }
    
    // 获取订单状态文本
    const getOrderStatusText = (status) => {
      const statusMap = {
        'pending': '待支付',
        'paid': '待发货',
        'shipped': '待收货',
        'completed': '已完成',
        'cancelled': '已取消'
      }
      return statusMap[status] || '未知状态'
    }
    
    // 跳转到订单详情
    const goToOrderDetail = (orderId) => {
      uni.navigateTo({
        url: `/pages/order-detail/order-detail?id=${orderId}`
      })
    }
    
    // 取消订单
    const cancelOrder = (orderId) => {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消这个订单吗？',
        success: (res) => {
          if (res.confirm) {
            // 调用取消订单API
            console.log('取消订单:', orderId)
            uni.showToast({
              title: '订单已取消',
              icon: 'success'
            })
          }
        }
      })
    }
    
    // 支付订单
    const payOrder = (orderId) => {
      uni.navigateTo({
        url: `/pages/pay/pay?orderId=${orderId}`
      })
    }
    
    // 确认收货
    const confirmOrder = (orderId) => {
      uni.showModal({
        title: '确认收货',
        content: '确定已收到商品吗？',
        success: (res) => {
          if (res.confirm) {
            // 调用确认收货API
            console.log('确认收货:', orderId)
            uni.showToast({
              title: '确认收货成功',
              icon: 'success'
            })
          }
        }
      })
    }
    
    // 评价订单
    const reviewOrder = (orderId) => {
      uni.navigateTo({
        url: `/pages/review/review?orderId=${orderId}`
      })
    }
    
    // 加载订单数据
    const loadOrders = async () => {
      try {
        await orderStore.getUserOrders()
        // 更新订单数据
        orders.value = orderStore.orders
        // 更新标签计数
        updateTabCounts()
      } catch (error) {
        console.error('加载订单失败:', error)
      }
    }
    
    // 更新标签计数
    const updateTabCounts = () => {
      orderTabs.value[0].count = orders.value.length
      orderTabs.value[1].count = orders.value.filter(o => o.status === 'pending').length
      orderTabs.value[2].count = orders.value.filter(o => o.status === 'paid').length
      orderTabs.value[3].count = orders.value.filter(o => o.status === 'shipped').length
      orderTabs.value[4].count = orders.value.filter(o => o.status === 'completed').length
    }
    
    onMounted(() => {
      loadOrders()
    })
    
    return {
      currentTab,
      orderTabs,
      currentOrders,
      switchTab,
      getOrderStatusText,
      goToOrderDetail,
      cancelOrder,
      payOrder,
      confirmOrder,
      reviewOrder
    }
  }
}
</script>

<style scoped>
.order-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8rpx 32rpx;
  background: #FFFFFF;
  font-size: 24rpx;
  color: #666666;
}

.battery {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.battery-bar {
  width: 32rpx;
  height: 16rpx;
  border: 1rpx solid #999999;
  border-radius: 4rpx;
  overflow: hidden;
}

.battery-fill {
  width: 100%;
  height: 100%;
  background: #4CAF50;
  border-radius: 2rpx;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.header-actions {
  display: flex;
  gap: 24rpx;
}

.action-icon {
  width: 40rpx;
  height: 40rpx;
}

.order-tabs {
  display: flex;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24rpx 0;
  position: relative;
}

.tab-item.active .tab-text {
  color: #FF6B6B;
  font-weight: 600;
}

.tab-text {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 8rpx;
}

.tab-badge {
  position: absolute;
  top: 16rpx;
  right: 50%;
  transform: translateX(50%);
  background: #FF6B6B;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 4rpx 8rpx;
  border-radius: 12rpx;
  min-width: 24rpx;
  text-align: center;
}

.order-list {
  padding: 24rpx 32rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #CCCCCC;
}

.order-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.order-number {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.order-time {
  font-size: 24rpx;
  color: #999999;
}

.order-status {
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  font-size: 24rpx;
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
  background: #D4EDDA;
  color: #28A745;
}

.order-status.completed {
  background: #E8F5E8;
  color: #28A745;
}

.product-info {
  display: flex;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  background: #F5F5F5;
}

.product-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.product-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  line-height: 1.4;
}

.product-spec {
  font-size: 24rpx;
  color: #999999;
}

.product-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8rpx;
}

.price {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.quantity {
  font-size: 24rpx;
  color: #999999;
}

.order-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 24rpx;
  border-top: 1rpx solid #F0F0F0;
}

.total-info {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.total-label {
  font-size: 24rpx;
  color: #666666;
}

.total-price {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.action-buttons {
  display: flex;
  gap: 16rpx;
}

.action-btn {
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  border: none;
  min-width: 120rpx;
}

.action-btn.cancel {
  background: #F5F5F5;
  color: #666666;
}

.action-btn.pay {
  background: #FF6B6B;
  color: #FFFFFF;
}

.action-btn.confirm {
  background: #4CAF50;
  color: #FFFFFF;
}

.action-btn.review {
  background: #2196F3;
  color: #FFFFFF;
}
</style>