<template>
  <view class="profile-page">
    <!-- 状态栏 -->
    <view class="status-bar">
      <text class="time">17:29</text>
      <view class="notification-badge">
        <text class="badge-text">10+</text>
      </view>
      <view class="battery">
        <text class="battery-text">100</text>
        <view class="battery-bar">
          <view class="battery-fill"></view>
        </view>
      </view>
    </view>

    <!-- 头部 -->
    <view class="header">
      <view class="header-actions">
        <image src="/static/icons/more.png" class="action-icon" mode="aspectFit"></image>
        <image src="/static/icons/target.png" class="action-icon" mode="aspectFit"></image>
      </view>
    </view>

    <!-- 用户信息区域 -->
    <view class="user-section">
      <view class="user-info" @click="handleUserClick">
        <view class="user-avatar">
          <image 
            :src="isLoggedIn ? (userInfo.avatar || '/static/images/default-avatar.png') : '/static/images/default-avatar.png'"
            class="avatar-image"
            mode="aspectFill"
          ></image>
        </view>
        <view class="user-details">
          <text class="username">{{ isLoggedIn ? (userInfo.nickname || '微信用户') : '注册/登录' }}</text>
          <text class="user-id" v-if="isLoggedIn">ID：{{ userInfo.id || '3585076' }}</text>
          <text class="user-desc" v-if="isLoggedIn">{{ userInfo.desc || '暂无简介' }}</text>
        </view>
        <image src="/static/icons/target.png" class="target-icon" mode="aspectFit"></image>
      </view>
    </view>

    <!-- 余额统计区域 -->
    <view class="balance-section" v-if="isLoggedIn">
      <view class="balance-stats">
        <view class="stat-item">
          <text class="stat-value">¥{{ userInfo.balance || '0.00' }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ userInfo.completedOrders || '0' }}</text>
          <text class="stat-label">完成 {{ userInfo.completedOrders || '0' }} 单</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">¥{{ userInfo.inviteReward || '0.00' }}</text>
          <text class="stat-label">邀请奖励</text>
        </view>
      </view>
    </view>

    <!-- 认证区域 -->
    <view class="verification-section" v-if="isLoggedIn">
      <view class="verification-content">
        <view class="verification-icon">
          <text class="icon-text">认</text>
        </view>
        <text class="verification-text">请先完成模特认证</text>
      </view>
      <button class="verify-btn" @click="goToVerification">
        立即认证
      </button>
    </view>

    <!-- 推广区域 -->
    <view class="promotion-section" v-if="isLoggedIn">
      <view class="promotion-content">
        <view class="promotion-text">
          <text class="promotion-title">邀联通告</text>
          <text class="promotion-desc">海量商家等您/种草通告，邀请与您合作</text>
        </view>
        <button class="join-btn" @click="goToInvite">
          立即加入
        </button>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-grid">
        <!-- 订单状态区域 -->
        <view class="menu-item" @click="handleOrderClick('pending-payment')">
          <image src="/static/icons/pending-payment.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待支付</text>
        </view>
        <view class="menu-item" @click="handleOrderClick('pending-shipment')">
          <image src="/static/icons/pending-shipment.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待发货</text>
        </view>
        <view class="menu-item" @click="handleOrderClick('pending-receipt')">
          <image src="/static/icons/pending-receipt.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待收货</text>
        </view>
        <view class="menu-item" @click="handleOrderClick('refund')">
          <image src="/static/icons/refund.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">退款/售后</text>
        </view>
        <view class="menu-item" @click="handleOrderClick('my-orders')">
          <image src="/static/icons/order.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">我的订单</text>
        </view>
        <!-- 保留的功能 -->
        <view class="menu-item" @click="handleMenuClick('address')">
          <image src="/static/icons/address.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">收货地址</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('notification')">
          <image src="/static/icons/notification.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">通知设置</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('guide')">
          <image src="/static/icons/guide.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">新手必读</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('service')">
          <image src="/static/icons/service.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">联系客服</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('feedback')">
          <image src="/static/icons/feedback.svg" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">意见反馈</text>
        </view>
      </view>
    </view>

    <!-- 登录弹窗 -->
    <view class="login-modal" v-if="showLoginModal" @click="closeLoginModal">
      <view class="login-content" @click.stop>
        <!-- Logo区域 -->
        <view class="logo-section">
          <view class="logo">
            <text class="logo-text">易捷商城</text>
          </view>
          <text class="welcome-title">Hi! 欢迎登录易捷商城</text>
          <text class="welcome-desc">登录后可享受易捷商城完整服务体验</text>
        </view>

        <!-- 登录按钮 -->
        <view class="login-buttons">
          <button class="login-btn primary" @click="handleWechatLogin">
            手机验证码登录
          </button>
          <view class="or-divider">
            <text class="or-text">or</text>
          </view>
          <button class="login-btn secondary" @click="handleQuickLogin">
            手机快捷登录
          </button>
        </view>

        <!-- 底部区域 -->
        <view class="login-footer">
          <text class="skip-login" @click="skipLogin">暂不登录</text>
          <view class="agreement-section">
            <checkbox 
              :checked="agreedToTerms" 
              @change="toggleAgreement"
              class="agreement-checkbox"
            />
            <text class="agreement-text">
              我已阅读并同意
              <text class="agreement-link" @click="showTerms">《服务政策及用户隐私协议》</text>
            </text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

export default {
  name: 'ProfilePage',
  setup() {
    const userStore = useUserStore()
    
    // 登录状态
    const isLoggedIn = ref(false)
    const showLoginModal = ref(false)
    const agreedToTerms = ref(false)
    
    // 用户信息
    const userInfo = ref({
      id: '3585076',
      nickname: '微信用户',
      avatar: '',
      desc: '暂无简介',
      balance: '0.00',
      completedOrders: 0,
      inviteReward: '0.00'
    })
    
    // 菜单项
    const menuItems = ref([
      {
        name: '网拍相册',
        icon: '/static/icons/album.png',
        action: 'album'
      },
      {
        name: '种草相册',
        icon: '/static/icons/grass.png',
        action: 'grass'
      },
      {
        name: '意见反馈',
        icon: '/static/icons/feedback.svg',
        action: 'feedback'
      },
      {
        name: '浏览记录',
        icon: '/static/icons/history.svg',
        action: 'history'
      },
      {
        name: '收货地址',
        icon: '/static/icons/address.svg',
        action: 'address'
      },
      {
        name: '通知设置',
        icon: '/static/icons/notification.png',
        action: 'notification'
      },
      {
        name: '新手必读',
        icon: '/static/icons/guide.svg',
        action: 'guide'
      },
      {
        name: '联系客服',
        icon: '/static/icons/service.svg',
        action: 'service'
      }
    ])
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        await userStore.getUserInfo()
        userInfo.value = {
          ...userInfo.value,
          ...userStore.userInfo
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }
    
    // 处理菜单点击
    const handleMenuClick = (action) => {
      if (!isLoggedIn.value) {
        showLoginModal.value = true
        return
      }
      
      switch (action) {
        case 'album':
          uni.navigateTo({
            url: '/pages/album/album'
          })
          break
        case 'grass':
          uni.navigateTo({
            url: '/pages/grass/grass'
          })
          break
        case 'feedback':
          uni.navigateTo({
            url: '/pages/feedback/feedback'
          })
          break
        case 'history':
          uni.navigateTo({
            url: '/pages/history/history'
          })
          break
        case 'address':
          uni.navigateTo({
            url: '/pages/address/address'
          })
          break
        case 'notification':
          uni.navigateTo({
            url: '/pages/settings/notification'
          })
          break
        case 'guide':
          uni.navigateTo({
            url: '/pages/guide/guide'
          })
          break
        case 'service':
          uni.navigateTo({
            url: '/pages/service/service'
          })
          break
        default:
          uni.showToast({
            title: '功能开发中',
            icon: 'none'
          })
      }
    }
    
    // 跳转到认证页面
    const goToVerification = () => {
      uni.navigateTo({
        url: '/pages/verification/verification'
      })
    }
    
    // 跳转到邀请页面
    const goToInvite = () => {
      uni.navigateTo({
        url: '/pages/invite/invite'
      })
    }
    
    // 处理用户点击
    const handleUserClick = () => {
      if (!isLoggedIn.value) {
        showLoginModal.value = true
      }
    }
    
    // 处理订单点击
    const handleOrderClick = (type) => {
      if (!isLoggedIn.value) {
        showLoginModal.value = true
        return
      }
      
      // 根据类型跳转到对应页面
      switch (type) {
        case 'pending-payment':
          uni.navigateTo({
            url: '/pages/order/order?status=pending-payment'
          })
          break
        case 'pending-shipment':
          uni.navigateTo({
            url: '/pages/order/order?status=pending-shipment'
          })
          break
        case 'pending-receipt':
          uni.navigateTo({
            url: '/pages/order/order?status=pending-receipt'
          })
          break
        case 'refund':
          uni.navigateTo({
            url: '/pages/order/order?status=refund'
          })
          break
        case 'my-orders':
          uni.navigateTo({
            url: '/pages/order/order'
          })
          break
      }
    }
    
    // 关闭登录弹窗
    const closeLoginModal = () => {
      showLoginModal.value = false
    }
    
    // 微信登录
    const handleWechatLogin = async () => {
      if (!agreedToTerms.value) {
        uni.showToast({
          title: '请先同意服务政策及用户隐私协议',
          icon: 'none'
        })
        return
      }
      
      try {
        // 调用微信登录
        const loginRes = await uni.login({
          provider: 'weixin'
        })
        
        if (loginRes.code) {
          // 获取用户信息
          const userRes = await uni.getUserProfile({
            desc: '用于完善用户资料'
          })
          
          // 更新用户信息
          userInfo.value = {
            ...userInfo.value,
            nickname: userRes.userInfo.nickName,
            avatar: userRes.userInfo.avatarUrl
          }
          
          // 设置登录状态
          isLoggedIn.value = true
          showLoginModal.value = false
          
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          })
        }
      } catch (error) {
        console.error('微信登录失败:', error)
        uni.showToast({
          title: '登录失败，请重试',
          icon: 'none'
        })
      }
    }
    
    // 快捷登录
    const handleQuickLogin = () => {
      uni.showToast({
        title: '快捷登录功能开发中',
        icon: 'none'
      })
    }
    
    // 跳过登录
    const skipLogin = () => {
      showLoginModal.value = false
    }
    
    // 切换协议同意状态
    const toggleAgreement = (e) => {
      agreedToTerms.value = e.detail.value
    }
    
    // 显示服务条款
    const showTerms = () => {
      uni.navigateTo({
        url: '/pages/terms/terms'
      })
    }
    
    onMounted(() => {
      loadUserInfo()
    })
    
    return {
      isLoggedIn,
      showLoginModal,
      agreedToTerms,
      userInfo,
      menuItems,
      handleMenuClick,
      handleUserClick,
      handleOrderClick,
      closeLoginModal,
      handleWechatLogin,
      handleQuickLogin,
      skipLogin,
      toggleAgreement,
      showTerms,
      goToVerification,
      goToInvite
    }
  }
}
</script>

<style scoped>
.profile-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding: 0;
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

.notification-badge {
  width: 32rpx;
  height: 32rpx;
  background: #FF6B6B;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16rpx;
}

.badge-text {
  font-size: 20rpx;
  color: #FFFFFF;
  font-weight: 600;
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
  justify-content: flex-end;
  align-items: center;
  padding: 24rpx 32rpx;
  background: #FFFFFF;
}

.header-actions {
  display: flex;
  gap: 24rpx;
}

.action-icon {
  width: 40rpx;
  height: 40rpx;
}

.user-section {
  background: linear-gradient(135deg, #FDF2F8 0%, #FCE7F3 100%);
  padding: 40rpx 32rpx;
  margin-bottom: 20rpx;
  border-radius: 16rpx;
  margin: 0 32rpx 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(244, 114, 182, 0.1);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 24rpx;
  margin-bottom: 40rpx;
}

.user-avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background: #FFB6C1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.user-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.username {
  font-size: 36rpx;
  color: #000000;
  font-weight: 600;
}

.user-id {
  font-size: 24rpx;
  color: #6B7280;
}

.user-desc {
  font-size: 24rpx;
  color: #9CA3AF;
}

.target-icon {
  width: 48rpx;
  height: 48rpx;
}

.balance-section {
  background: #FFFFFF;
  padding: 32rpx;
  margin: 0 32rpx 20rpx;
  border-radius: 16rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.balance-stats {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  flex: 1;
}

.stat-value {
  font-size: 36rpx;
  color: #FF6B6B;
  font-weight: 600;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #6B7280;
}

.verification-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx;
  background: #FFF0F5;
  border-radius: 12rpx;
  margin: 0 32rpx 20rpx;
  border: 1rpx solid #FFE0E6;
}

.verification-content {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.verification-icon {
  width: 64rpx;
  height: 64rpx;
  background: #FFB6C1;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-text {
  font-size: 24rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.verification-text {
  font-size: 28rpx;
  color: #374151;
}

.verify-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  border: none;
}

.promotion-section {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  border-radius: 12rpx;
  padding: 32rpx;
  margin: 0 32rpx 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(255, 107, 107, 0.3);
}

.promotion-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.promotion-text {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.promotion-title {
  font-size: 36rpx;
  color: #FFFFFF;
  font-weight: 600;
  margin-bottom: 8rpx;
}

.promotion-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
  opacity: 0.9;
}

.join-btn {
  background: rgba(255, 255, 255, 0.2);
  color: #FFFFFF;
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  border: none;
}

.menu-section {
  background: #FFFFFF;
  margin: 0 32rpx 20rpx;
  border-radius: 12rpx;
  padding: 32rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 32rpx;
  row-gap: 32rpx;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12rpx;
  min-width: 120rpx;
}

.menu-icon {
  width: 40rpx;
  height: 40rpx;
  opacity: 0.8;
}

.menu-text {
  font-size: 24rpx;
  color: #4B5563;
  text-align: center;
  line-height: 1.2;
  font-weight: 400;
  white-space: nowrap;
}


/* 登录弹窗 */
.login-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 1000;
}

.login-content {
  background: #FFFFFF;
  border-radius: 32rpx 32rpx 0 0;
  padding: 48rpx 32rpx 32rpx;
  width: 100%;
  max-height: 80vh;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.logo-section {
  text-align: center;
  margin-bottom: 48rpx;
}

.logo {
  width: 120rpx;
  height: 120rpx;
  background: linear-gradient(135deg, #4CAF50 0%, #FFC107 100%);
  border-radius: 24rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24rpx;
}

.logo-text {
  font-size: 32rpx;
  color: #FFFFFF;
  font-weight: 600;
}

.welcome-title {
  font-size: 48rpx;
  color: #000000;
  font-weight: 600;
  margin-bottom: 16rpx;
  display: block;
}

.welcome-desc {
  font-size: 28rpx;
  color: #666666;
  display: block;
}

.login-buttons {
  margin-bottom: 48rpx;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  margin-bottom: 24rpx;
}

.login-btn.primary {
  background: #2E7D32;
  color: #FFFFFF;
}

.login-btn.secondary {
  background: #E8F5E8;
  color: #2E7D32;
}

.or-divider {
  text-align: center;
  margin: 24rpx 0;
}

.or-text {
  font-size: 24rpx;
  color: #999999;
}

.login-footer {
  text-align: center;
}

.skip-login {
  font-size: 28rpx;
  color: #666666;
  margin-bottom: 32rpx;
  display: block;
}

.agreement-section {
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  text-align: left;
}

.agreement-checkbox {
  margin-top: 4rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.4;
  flex: 1;
}

.agreement-link {
  color: #2E7D32;
  text-decoration: underline;
}
</style>