<template>
  <view class="profile-page">


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
    <view class="balance-section">
      <view class="balance-stats">
        <view class="stat-item" @tap="handleBalanceClick">
          <text class="stat-value">¥{{ isLoggedIn ? (userInfo.balance || '0.00') : '0.00' }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-item" @tap="handleEarningsClick">
          <text class="stat-value">¥{{ isLoggedIn ? (userInfo.taskEarnings || '0.00') : '0.00' }}</text>
          <text class="stat-label">任务总收益</text>
        </view>
        <view class="stat-item" @tap="handleTasksClick">
          <text class="stat-value">{{ isLoggedIn ? (userInfo.completedTasks || '0') : '0' }}</text>
          <text class="stat-label">完成任务数</text>
        </view>
        <view class="stat-item" @tap="handleRewardClick">
          <text class="stat-value">{{ isLoggedIn ? (userInfo.inviteReward || '0') : '0' }}</text>
          <text class="stat-label">邀请奖励</text>
        </view>
      </view>
    </view>

 

    <!-- 推广区域 -->
    <view class="promotion-section" v-if="isLoggedIn">
      <view class="promotion-content">
        <view class="promotion-text">
            <text class="promotion-title">邀请赚佣金</text>
            <text class="promotion-desc">邀请好友完成任务，您可获得佣金奖励</text>
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
          <image src="/static/icons/wechat.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待支付</text>
          </view>
        <view class="menu-item" @click="handleOrderClick('pending-shipment')">
          <image src="/static/icons/cart.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待发货</text>
          </view>
        <view class="menu-item" @click="handleOrderClick('pending-receipt')">
          <image src="/static/icons/task.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待收货</text>
          </view>
        <view class="menu-item" @click="handleOrderClick('refund')">
          <image src="/static/icons/search.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">退款/售后</text>
        </view>
        <view class="menu-item" @click="handleOrderClick('my-orders')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">我的订单</text>
      </view>
        <!-- 保留的功能 -->
        <view class="menu-item" @click="handleMenuClick('address')">
          <image src="/static/icons/home.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">收货地址</text>
            </view>
        <view class="menu-item" @click="handleMenuClick('notification')">
          <image src="/static/icons/notification.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">通知设置</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('guide')">
          <image src="/static/icons/info.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">新手必读</text>
          </view>
        <view class="menu-item" @click="handleMenuClick('service')">
          <image src="/static/icons/service.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">联系客服</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('feedback')">
          <image src="/static/icons/target.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">意见反馈</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('settings')">
          <image src="/static/icons/settings.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">通用设置</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('withdraw')">
          <image src="/static/icons/withdraw.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">提现</text>
        </view>
        </view>
      </view>

    <!-- 登录弹窗 -->
    <view class="login-modal" v-if="showLoginModal" @click="closeLoginModal">
      <view class="login-content" @click.stop>
        <!-- Logo区域 -->
        <view class="logo-section">
          <view class="logo">
            <text class="logo-text">任务商城</text>
          </view>
          <text class="welcome-title">欢迎来到任务商城</text>
          <text class="welcome-desc">完成任务获得收益，邀请好友获得集采购买权益</text>
        </view>

                    <!-- 登录按钮 -->
                    <view class="login-buttons">
                      <button 
                        class="login-btn wechat" 
                        :open-type="agreedToTerms ? 'getPhoneNumber' : ''"
                        @tap="!agreedToTerms && uni.showToast({ title: '请先同意服务政策及用户隐私协议', icon: 'none' })"
                        @getphonenumber="handleWechatLogin"
                      >
                        <image src="/static/icons/wechat.png" class="wechat-icon" mode="aspectFit"></image>
                        微信一键登录
                      </button>
                    </view>

        <!-- 底部区域 -->
        <view class="login-footer">
          <text class="skip-login" @click="skipLogin">暂不登录</text>
          <view class="agreement-section">
            <view class="agreement-label" @tap="toggleAgreement">
              <text class="checkbox-text" :class="{ checked: agreedToTerms }">{{ agreedToTerms ? '☑' : '☐' }}</text>
              <text class="agreement-text">
                我已阅读并同意
                <text class="agreement-link" @tap.stop="showTerms">《服务政策及用户隐私协议》</text>
              </text>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <!-- 客服弹窗 -->
    <view class="service-modal" v-if="showServiceModal" @click="closeServiceModal">
      <view class="service-content" @click.stop>
        <view class="service-header">
          <text class="service-title">{{ serviceInfo.title || '联系客服' }}</text>
          <view class="close-btn" @click="closeServiceModal">×</view>
        </view>
        <view class="service-body">
          <image class="qr-code" :src="serviceInfo.qrCode" mode="aspectFit"></image>
          <text class="service-text">{{ serviceInfo.content }}</text>
          <text class="service-text" v-if="serviceInfo.workTime">{{ serviceInfo.workTime }}</text>
          <text class="service-text" v-if="serviceInfo.phone">电话：{{ serviceInfo.phone }}</text>
          <text class="service-text" v-if="serviceInfo.email">邮箱：{{ serviceInfo.email }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useSystemStore } from '@/stores/system'
import { useOrderStore } from '@/stores/order'

export default {
  name: 'ProfilePage',
  setup() {
    const userStore = useUserStore()
    const systemStore = useSystemStore()
    const orderStore = useOrderStore()

    // 登录状态
    const isLoggedIn = ref(false)
    const showLoginModal = ref(false)
    const agreedToTerms = ref(false)
    
    // 客服相关
    const showServiceModal = ref(false)
    const serviceInfo = ref({
      qrCode: '',
      content: '',
      title: '',
      phone: '',
      email: '',
      workTime: ''
    })
    
    // 用户信息
    const userInfo = ref({
      id: '3585076',
      nickname: '微信用户',
      avatar: '',
      desc: '暂无简介',
      balance: '0.00',
      taskEarnings: '0.00',
      completedTasks: 0,
      inviteReward: 0
    })
    
    // 菜单项
    // 菜单项 - 任务商城平台功能
    const menuItems = ref([
      // 任务相关
      {
        name: '我的任务',
        icon: '/static/icons/task.png',
        action: 'my-tasks'
      },
      {
        name: '任务收益',
        icon: '/static/icons/task-earnings.png',
        action: 'task-earnings'
      },
      {
        name: '提现记录',
        icon: '/static/icons/earnings.png',
        action: 'withdraw-history'
      },
      {
        name: '邀请好友',
        icon: '/static/icons/invite-earnings.png',
        action: 'invite-friends'
      },
      // 商城相关
      {
        name: '我的订单',
        icon: '/static/icons/order.png',
        action: 'my-orders'
      },
      {
        name: '收货地址',
        icon: '/static/icons/home.png',
        action: 'address'
      },
      // 其他功能
      {
        name: '通知设置',
        icon: '/static/icons/notification.png',
        action: 'notification'
      },
      {
        name: '新手必读',
        icon: '/static/icons/info.png',
        action: 'guide'
      },
      {
        name: '联系客服',
        icon: '/static/icons/service.png',
        action: 'service'
      },
      {
        name: '意见反馈',
        icon: '/static/icons/target.png',
        action: 'feedback'
      }
    ])
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        if (userStore.checkLoginStatus()) {
          userInfo.value = {
            ...userInfo.value,
            ...userStore.userInfo
          }
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
        case 'feedback':
          uni.navigateTo({ url: '/pages/feedback/feedback' })
          break
        case 'address':
          uni.navigateTo({ url: '/pages/address/address' })
          break
        case 'notification':
          uni.navigateTo({ url: '/pages/settings/notification' })
          break
        case 'guide':
          uni.navigateTo({ url: '/pages/guide/guide' })
          break
        case 'service':
          handleCustomerService()
          break
        case 'settings':
          uni.navigateTo({ url: '/pages/settings/settings' })
          break
        case 'withdraw':
          uni.navigateTo({ url: '/pages/withdraw/withdraw' })
          break
        default:
          uni.showToast({ title: '功能开发中', icon: 'none' })
      }
    }
    
    // 跳转到认证页面
    const goToVerification = () => { uni.navigateTo({ url: '/pages/verification/verification' }) }

    // 跳转到邀请页面
    const goToInvite = () => { uni.navigateTo({ url: '/pages/invite/invite' }) }

    // 处理用户点击
    const handleUserClick = () => { if (!isLoggedIn.value) { showLoginModal.value = true } }

    // 处理余额区域点击 -> 钱包流水
    const handleBalanceClick = () => {
      if (!isLoggedIn.value) { showLoginModal.value = true; return }
      uni.setStorageSync('walletQuery', { type: 'all' })
      uni.navigateTo({ url: '/pages/wallet/transactions' })
    }

    // 处理收益区域点击 -> 钱包流水（收入）
    const handleEarningsClick = () => {
      if (!isLoggedIn.value) { showLoginModal.value = true; return }
      uni.setStorageSync('walletQuery', { type: 'commission' })
      uni.navigateTo({ url: '/pages/wallet/transactions' })
    }

    // 处理任务区域点击 -> 钱包流水（收入）
    const handleTasksClick = () => {
      if (!isLoggedIn.value) { showLoginModal.value = true; return }
      uni.setStorageSync('walletQuery', { type: 'commission' })
      uni.navigateTo({ url: '/pages/wallet/transactions' })
    }

    // 处理奖励区域点击 -> 钱包流水（收入）
    const handleRewardClick = () => {
      if (!isLoggedIn.value) { showLoginModal.value = true; return }
      uni.setStorageSync('walletQuery', { type: 'commission' })
      uni.navigateTo({ url: '/pages/wallet/transactions' })
    }
    
    // 处理订单点击
    const handleOrderClick = async (type) => {
      if (!isLoggedIn.value) {
        showLoginModal.value = true
        return
      }
      
      try {
        let status = null
        let tabIndex = 0
        
        switch (type) {
          case 'pending-payment': status = 'pending'; tabIndex = 1; break
          case 'pending-shipment': status = 'paid'; tabIndex = 2; break
          case 'pending-receipt': status = 'shipped'; tabIndex = 3; break
          case 'refund': status = 'completed'; tabIndex = 4; break
          case 'my-orders': status = null; tabIndex = 0; break
        }
        
        uni.setStorageSync('orderTab', tabIndex)
        uni.switchTab({ url: '/pages/order/order' })
        
      } catch (error) {
        console.error('预加载订单数据失败:', error)
        uni.setStorageSync('orderTab', 0)
        uni.switchTab({ url: '/pages/order/order' })
      }
    }
    
    // 关闭登录弹窗
    const closeLoginModal = () => { showLoginModal.value = false }
    
    // 处理联系客服
    const handleCustomerService = async () => {
      try {
        const response = await systemStore.getCustomerServiceConfig()
        const data = response.data || {}
        serviceInfo.value = {
          qrCode: data.qr_code || data.qrCode || '',
          content: data.description || '',
          title: data.title || '联系客服',
          phone: data.phone || '',
          email: data.email || '',
          workTime: data.work_time || data.workTime || ''
        }
        showServiceModal.value = true
      } catch (error) {
        console.error('获取客服信息失败:', error)
        serviceInfo.value = {
          qrCode: 'https://example.com/qr-code.png',
          content: '扫描二维码联系客服，工作时间：9:00-18:00'
        }
        showServiceModal.value = true
        uni.showToast({ title: '客服配置加载失败，使用默认配置', icon: 'none' })
      }
    }
    
    // 关闭客服弹窗
    const closeServiceModal = () => { showServiceModal.value = false }
    
    // 微信登录（略）
    const handleWechatLogin = (e) => {
      if (!agreedToTerms.value) { uni.showToast({ title: '请先同意服务政策及用户隐私协议', icon: 'none' }); return }
      uni.showLoading({ title: '登录中...' })
      uni.login({ provider: 'weixin', success: (loginRes) => {
        if (loginRes.code) {
          uni.getUserInfo({ success: (userRes) => {
            const loginData = {
              code: loginRes.code,
              nickname: userRes.userInfo.nickName,
              avatar: userRes.userInfo.avatarUrl,
              phoneCode: e && e.detail ? e.detail.code : null
            }
            userStore.login(loginData.code, loginData, loginData.phoneCode).then(() => {
              userInfo.value = { ...userInfo.value, nickname: userRes.userInfo.nickName, avatar: userRes.userInfo.avatarUrl }
              userStore.userInfo = userInfo.value
              userStore.isLoggedIn = true
              isLoggedIn.value = true
              showLoginModal.value = false
              uni.hideLoading(); uni.showToast({ title: '登录成功', icon: 'success' })
            }).catch((error) => { uni.hideLoading(); uni.showToast({ title: error.message || '登录失败，请重试', icon: 'none' }) })
          }, fail: () => { uni.hideLoading(); uni.showToast({ title: '获取用户信息失败', icon: 'none' }) } })
        }
      }, fail: () => { uni.hideLoading(); uni.showToast({ title: '登录失败，请重试', icon: 'none' }) } })
    }
    
    const handleQuickLogin = () => { uni.showToast({ title: '快捷登录功能开发中', icon: 'none' }) }
    const skipLogin = () => { showLoginModal.value = false }
    const toggleAgreement = (e) => { agreedToTerms.value = !agreedToTerms.value }
    const showTerms = () => { uni.navigateTo({ url: '/pages/terms/terms' }) }
    
    onMounted(() => {
      if (userStore.checkLoginStatus()) {
        isLoggedIn.value = true
        userInfo.value = userStore.userInfo
      } else {
        loadUserInfo()
      }
    })

    return {
      isLoggedIn,
      showLoginModal,
      agreedToTerms,
      showServiceModal,
      serviceInfo,
      userInfo,
      menuItems,
      handleMenuClick,
      handleUserClick,
      handleBalanceClick,
      handleEarningsClick,
      handleTasksClick,
      handleRewardClick,
      handleOrderClick,
      closeLoginModal,
      handleCustomerService,
      closeServiceModal,
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
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
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
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
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

.login-btn.wechat {
  background: #07C160;
  color: #FFFFFF;
}

.wechat-icon {
  width: 40rpx;
  height: 40rpx;
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
  margin: 40rpx auto 0;
  text-align: center;
}

.agreement-label {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.checkbox-text {
  font-size: 32rpx;
  color: #CCCCCC;
  margin-right: 12rpx;
  transition: all 0.2s ease;
}

.checkbox-text.checked {
  color: #FF6B6B;
}

.agreement-text {
  font-size: 24rpx;
  color: #666666;
  line-height: 1.4;
  flex: 1;
}

.agreement-link {
  color: #FF6B6B;
  text-decoration: underline;
}

/* 客服弹窗样式 */
.service-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1001;
}

.service-content {
  background: #FFFFFF;
  border-radius: 20rpx;
  width: 600rpx;
  max-width: 90vw;
  overflow: hidden;
}

.service-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 40rpx 40rpx 20rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.service-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.close-btn {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  background: #F5F5F5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #999999;
  cursor: pointer;
}

.service-body {
  padding: 40rpx;
  text-align: center;
}

.qr-code {
  width: 300rpx;
  height: 300rpx;
  border-radius: 20rpx;
  margin-bottom: 30rpx;
}

.service-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.5;
}
</style>