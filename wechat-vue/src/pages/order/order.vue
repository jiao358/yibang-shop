<template>
  <view class="order-page">

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
        <view v-if="tab.count > 0" class="tab-badge">{{ displayCount(tab.count) }}</view>
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
                @click.stop="onCancel(order.id)"
              >
                取消订单
              </button>
              <button 
                v-if="order.status === 'pending'"
                class="action-btn pay"
                @click.stop="onPay(order.id)"
              >
                立即支付
              </button>
              <button 
                v-if="order.status === 'paid'"
                class="action-btn confirm"
                @click.stop="onConfirm(order.id)"
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
import { onShow } from '@dcloudio/uni-app'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'

export default {
  name: 'OrderPage',
  setup() {
    const orderStore = useOrderStore()
    const userStore = useUserStore()
    
    // 当前选中的标签
    const currentTab = ref(0)
    
    // 登录状态
    const isLoggedIn = ref(false)
    
    // 订单标签
    const orderTabs = ref([
      { name: '全部', count: 0 },
      { name: '待支付', count: 0 },
      { name: '待发货', count: 0 },
      { name: '待收货', count: 0 },
      { name: '已完成', count: 0 }
    ])
    
    // 订单数据
    const orders = ref([])
    
    // 当前显示的订单
    const currentOrders = computed(() => {
      if (!isLoggedIn.value) {
        return [] // 未登录时不显示任何订单
      }
      
      if (currentTab.value === 0) {
        return orders.value
      }
      const statusMap = ['', 'pending', 'paid', 'shipped', 'completed']
      const status = statusMap[currentTab.value]
      return orders.value.filter(order => order.status === status)
    })
    
    // 数字显示自适应（避免过长覆盖）
    const displayCount = (n) => {
      if (n > 999) return '999+'
      if (n > 99) return '99+'
      return String(n)
    }
    
    // 切换标签
    const switchTab = async (index) => {
      currentTab.value = index
      if (isLoggedIn.value) {
        await loadOrderList()
      }
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

    // 实际调用后端取消/支付/确认接口
    const onCancel = (orderId) => {
      uni.showModal({
        title: '确认取消',
        content: '确定要取消这个订单吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderStore.cancelOrder(orderId)
              await loadOrderList()
              uni.showToast({ title: '订单已取消', icon: 'success' })
            } catch (e) {}
          }
        }
      })
    }

    const onPay = async (orderId) => {
      try {
        await orderStore.payOrder(orderId, { channel: 'wxpay' })
        await loadOrderList()
        uni.showToast({ title: '支付成功', icon: 'success' })
      } catch (e) {
        uni.showToast({ title: '支付失败', icon: 'none' })
      }
    }

    const onConfirm = (orderId) => {
      uni.showModal({
        title: '确认收货',
        content: '确定已收到商品吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderStore.confirmOrder(orderId)
              await loadOrderList()
              uni.showToast({ title: '确认收货成功', icon: 'success' })
            } catch (e) {}
          }
        }
      })
    }
    
    // 评价订单
    const reviewOrder = (orderId) => {
      uni.navigateTo({ url: `/pages/review/review?orderId=${orderId}` })
    }
    
    // 检查登录状态并按当前tab加载（仅一次精准请求）
    const checkLoginAndLoad = async () => {
      userStore.checkLoginStatus()
      const token = uni.getStorageSync('token')
      if (!token) {
        isLoggedIn.value = false
        orders.value = []
        return
      }
      isLoggedIn.value = true
      await loadOrderList()  // 仅按当前tab状态请求一次
    }
    
    // 根据当前tab加载订单列表
    const loadOrderList = async () => {
      if (!isLoggedIn.value) return
      
      try {
        const statusMap = ['', 'pending', 'paid', 'shipped', 'completed']
        const status = statusMap[currentTab.value]
        const params = status === '' ? {} : { status }
        await orderStore.getUserOrders(params)
        orders.value = orderStore.orders
        updateTabCounts()
      } catch (error) {
        console.error('加载订单列表失败:', error)
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
    
    onMounted(async () => {
      const pages = getCurrentPages()
      const currentPage = pages[pages.length - 1]
      if (currentPage.options && currentPage.options.tab) {
        const tabIndex = parseInt(currentPage.options.tab)
        if (tabIndex >= 0 && tabIndex < orderTabs.value.length) {
          currentTab.value = tabIndex
        }
      }
      await checkLoginAndLoad()
    })
    
    onShow(async () => {
      const savedTab = uni.getStorageSync('orderTab')
      if (savedTab !== undefined && savedTab !== null) {
        const tabIndex = parseInt(savedTab)
        if (tabIndex >= 0 && tabIndex < orderTabs.value.length) {
          currentTab.value = tabIndex
        }
        uni.removeStorageSync('orderTab')
        await checkLoginAndLoad()
      } else {
        await checkLoginAndLoad()
      }
    })
    
    return {
      currentTab,
      orderTabs,
      currentOrders,
      switchTab,
      getOrderStatusText,
      goToOrderDetail,
      onCancel,
      onPay,
      onConfirm,
      reviewOrder,
      displayCount
    }
  }
}
</script>

<style scoped>
.order-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.order-tabs {
  display: flex;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20rpx 48rpx 20rpx 16rpx; /* 为右侧徽标预留空间 */
  position: relative;
}

.tab-item.active .tab-text {
  color: #FF6B6B;
  font-weight: 600;
}

.tab-text {
  font-size: 28rpx;
  color: #666666;
}

.tab-badge {
  position: absolute;
  top: 6rpx;
  right: 12rpx; /* 靠右放置，避免覆盖文本 */
  background: #FF6B6B;
  color: #FFFFFF;
  font-size: 20rpx;
  padding: 4rpx 8rpx; /* 自适应宽度 */
  border-radius: 12rpx;
  min-width: 24rpx;
  text-align: center;
}

.order-list { padding: 24rpx 32rpx; }

.empty-state { display: flex; flex-direction: column; align-items: center; padding: 120rpx 0; }
.empty-image { width: 200rpx; height: 200rpx; margin-bottom: 32rpx; }
.empty-text { font-size: 32rpx; color: #999999; margin-bottom: 16rpx; }
.empty-desc { font-size: 24rpx; color: #CCCCCC; }

.order-item { background: #FFFFFF; border-radius: 16rpx; margin-bottom: 24rpx; padding: 32rpx; box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1); }
.order-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; padding-bottom: 24rpx; border-bottom: 1rpx solid #F0F0F0; }
.order-info { display: flex; flex-direction: column; gap: 8rpx; }
.order-number { font-size: 28rpx; color: #333333; font-weight: 500; }
.order-time { font-size: 24rpx; color: #999999; }
.order-status { padding: 8rpx 16rpx; border-radius: 12rpx; font-size: 24rpx; }
.order-status.pending { background: #FFF3CD; color: #FFC107; }
.order-status.paid { background: #D1ECF1; color: #17A2B8; }
.order-status.shipped { background: #D4EDDA; color: #28A745; }
.order-status.completed { background: #E8F5E8; color: #28A745; }

.product-info { display: flex; gap: 24rpx; margin-bottom: 24rpx; }
.product-image { width: 120rpx; height: 120rpx; border-radius: 12rpx; background: #F5F5F5; }
.product-details { flex: 1; display: flex; flex-direction: column; gap: 8rpx; }
.product-name { font-size: 28rpx; color: #333333; font-weight: 500; line-height: 1.4; }
.product-spec { font-size: 24rpx; color: #999999; }
.product-price { display: flex; justify-content: space-between; align-items: center; margin-top: 8rpx; }
.price { font-size: 32rpx; color: #FF6B6B; font-weight: 600; }
.quantity { font-size: 24rpx; color: #999999; }

.order-actions { display: flex; justify-content: space-between; align-items: center; padding-top: 24rpx; border-top: 1rpx solid #F0F0F0; }
.total-info { display: flex; align-items: center; gap: 8rpx; }
.total-label { font-size: 24rpx; color: #666666; }
.total-price { font-size: 32rpx; color: #FF6B6B; font-weight: 600; }

.action-buttons { display: flex; gap: 16rpx; }
.action-btn { padding: 16rpx 32rpx; border-radius: 24rpx; font-size: 24rpx; border: none; min-width: 120rpx; }
.action-btn.cancel { background: #F5F5F5; color: #666666; }
.action-btn.pay { background: #FF6B6B; color: #FFFFFF; }
.action-btn.confirm { background: #4CAF50; color: #FFFFFF; }
.action-btn.review { background: #2196F3; color: #FFFFFF; }
</style>