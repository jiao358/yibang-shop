<template>
  <view class="profile-page">
    <!-- 用户信息区域 -->
    <view class="user-section">
      <view class="user-info">
        <view class="user-avatar">
          <image 
            :src="userInfo.avatar || '/static/images/default-avatar.png'"
            class="avatar-image"
            mode="aspectFill"
          ></image>
        </view>
        <view class="user-details">
          <text class="username">{{ userInfo.nickname || '微信用户' }}</text>
          <text class="user-id">ID：{{ userInfo.id || '3585076' }}</text>
          <text class="user-desc">{{ userInfo.desc || '暂无简介' }}</text>
        </view>
        <image src="/static/icons/target.png" class="target-icon" mode="aspectFit"></image>
      </view>

      <!-- 余额统计 -->
      <view class="balance-stats">
        <view class="stat-item" @click="handleBalanceClick">
          <text class="stat-value">¥{{ userInfo.balance || '0.00' }}</text>
          <text class="stat-label">余额</text>
        </view>
        <view class="stat-item" @click="handleEarningsClick">
          <text class="stat-value">¥{{ userInfo.totalEarnings || '0.00' }}</text>
          <text class="stat-label">任务总收益</text>
        </view>
        <view class="stat-item" @click="handleTasksClick">
          <text class="stat-value">{{ userInfo.completedTasks || '0' }}</text>
          <text class="stat-label">完成任务数</text>
        </view>
        <view class="stat-item" @click="handleRewardClick">
          <text class="stat-value">{{ userInfo.inviteRewards || '0' }}</text>
          <text class="stat-label">邀请奖励</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-group">
        <view class="menu-item" @click="handleMenuClick('order')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待支付</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('order')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待发货</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('order')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">待收货</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('order')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">退款/售后</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('order')">
          <image src="/static/icons/order.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">我的订单</text>
        </view>
      </view>

      <view class="menu-group">
        <view class="menu-item" @click="handleMenuClick('address')">
          <image src="/static/icons/more.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">收货地址</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('notification')">
          <image src="/static/icons/notification.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">通知设置</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('guide')">
          <image src="/static/icons/more.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">新手必读</text>
        </view>
        <view class="menu-item" @click="handleMenuClick('service')">
          <image src="/static/icons/more.png" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">联系客服</text>
        </view>
      </view>
    </view>

    <!-- 登录弹窗 -->
    <view v-if="showLoginModal" class="login-modal">
      <view class="modal-mask" @click="closeLoginModal"></view>
      <view class="modal-content">
        <view class="login-header">
          <text class="login-title">任务商城</text>
          <view class="close-btn" @click="closeLoginModal">
            <image src="/static/icons/close.png" class="close-icon" mode="aspectFit"></image>
          </view>
        </view>
        
        <view class="login-body">
          <view class="welcome-section">
            <text class="welcome-title">欢迎来到任务商城</text>
            <text class="welcome-desc">完成任务获得收益，邀请好友获得集采购买权益</text>
          </view>

          <!-- 登录按钮 -->
          <view class="login-buttons">
            <button class="login-btn wechat" @click="handleWechatLogin">
              <image src="/static/icons/wechat.png" class="wechat-icon" mode="aspectFit"></image>
              微信一键登录
            </button>
          </view>

          <!-- 底部区域 -->
          <view class="login-footer">
            <text class="skip-login" @click="skipLogin">暂不登录</text>
            <view class="agreement-section">
              <view class="agreement-label" @tap="toggleAgreement">
                <view class="checkbox" :class="{ checked: agreedToTerms }">
                  <text class="checkbox-text">{{ agreedToTerms ? '☑' : '☐' }}</text>
                </view>
                <text class="agreement-text">我已阅读并同意</text>
                <text class="agreement-link" @click.stop="showTerms">《服务政策及用户隐私协议》</text>
              </view>
            </view>
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
    
    // 响应式数据
    const userInfo = ref({
      id: '',
      nickname: '',
      avatar: '',
      balance: '0.00',
      totalEarnings: '0.00',
      completedTasks: '0',
      inviteRewards: '0',
      desc: '暂无简介'
    })
    const showLoginModal = ref(false)
    const agreedToTerms = ref(false)
    
    // 加载用户信息
    const loadUserInfo = async () => {
      try {
        // 先检查本地登录状态
        if (userStore.checkLoginStatus()) {
          userInfo.value = { ...userInfo.value, ...userStore.userInfo }
          return
        }
        
        // 如果没有登录，显示登录弹窗
        showLoginModal.value = true
      } catch (error) {
        console.error('加载用户信息失败:', error)
        showLoginModal.value = true
      }
    }
    
    // 处理菜单点击
    const handleMenuClick = (action) => {
      // 如果未登录，显示登录弹窗
      if (!userStore.isLoggedIn) {
        showLoginModal.value = true
        return
      }
      
      switch (action) {
        case 'order':
          uni.navigateTo({
            url: '/pages/order/order'
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
    
    // 处理余额点击
    const handleBalanceClick = () => {
      if (!userStore.isLoggedIn) {
        showLoginModal.value = true
        return
      }
      uni.navigateTo({
        url: '/pages/wallet/wallet'
      })
    }
    
    // 处理收益点击
    const handleEarningsClick = () => {
      if (!userStore.isLoggedIn) {
        showLoginModal.value = true
        return
      }
      uni.navigateTo({
        url: '/pages/earnings/earnings'
      })
    }
    
    // 处理任务点击
    const handleTasksClick = () => {
      if (!userStore.isLoggedIn) {
        showLoginModal.value = true
        return
      }
      uni.navigateTo({
        url: '/pages/task/task'
      })
    }
    
    // 处理奖励点击
    const handleRewardClick = () => {
      if (!userStore.isLoggedIn) {
        showLoginModal.value = true
        return
      }
      uni.navigateTo({
        url: '/pages/invite/invite'
      })
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
        // 显示加载状态
        uni.showLoading({
          title: '登录中...'
        })
        
        // 先调用微信登录获取 code
        const loginRes = await new Promise((resolve, reject) => {
          uni.login({
            provider: 'weixin',
            success: resolve,
            fail: reject
          })
        })
        
        if (loginRes.code) {
          // 获取用户详细信息
          const userRes = await new Promise((resolve, reject) => {
            uni.getUserInfo({
              success: resolve,
              fail: reject
            })
          })
          
          // 调用后端登录API
          const response = await userStore.login(loginRes.code, userRes.userInfo)
          
          // 更新用户信息
          userInfo.value = {
            ...userInfo.value,
            nickname: userRes.userInfo.nickName,
            avatar: userRes.userInfo.avatarUrl,
            gender: userRes.userInfo.gender,
            country: userRes.userInfo.country,
            province: userRes.userInfo.province,
            city: userRes.userInfo.city
          }
          
          // 设置登录状态
          showLoginModal.value = false
          
          uni.hideLoading()
          uni.showToast({
            title: '登录成功',
            icon: 'success'
          })
        }
      } catch (error) {
        console.error('微信登录失败:', error)
        uni.hideLoading()
        uni.showToast({
          title: error.message || '登录失败，请重试',
          icon: 'none'
        })
      }
    }
    
    // 暂不登录
    const skipLogin = () => {
      showLoginModal.value = false
    }
    
    // 切换协议同意状态
    const toggleAgreement = () => {
      agreedToTerms.value = !agreedToTerms.value
    }
    
    // 显示服务条款
    const showTerms = () => {
      uni.showModal({
        title: '服务政策及用户隐私协议',
        content: '这里是服务政策及用户隐私协议的内容...',
        showCancel: false
      })
    }
    
    onMounted(() => {
      loadUserInfo()
    })
    
    return {
      userInfo,
      showLoginModal,
      agreedToTerms,
      handleMenuClick,
      handleBalanceClick,
      handleEarningsClick,
      handleTasksClick,
      handleRewardClick,
      closeLoginModal,
      handleWechatLogin,
      skipLogin,
      toggleAgreement,
      showTerms
    }
  }
}
</script>

<style scoped>
.profile-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.user-section {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  padding: 40rpx 32rpx;
  margin-bottom: 24rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 32rpx;
}

.user-avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  overflow: hidden;
  margin-right: 24rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.avatar-image {
  width: 100%;
  height: 100%;
}

.user-details {
  flex: 1;
}

.username {
  display: block;
  font-size: 36rpx;
  font-weight: 600;
  color: #FFFFFF;
  margin-bottom: 8rpx;
}

.user-id {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 8rpx;
}

.user-desc {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.7);
}

.target-icon {
  width: 48rpx;
  height: 48rpx;
}

.balance-stats {
  display: flex;
  justify-content: space-between;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16rpx;
  padding: 32rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 600;
  color: #FFFFFF;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 20rpx;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
}

.menu-section {
  padding: 0 32rpx;
}

.menu-group {
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 48rpx;
  height: 48rpx;
  margin-right: 24rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.arrow-icon {
  width: 24rpx;
  height: 24rpx;
}

/* 登录弹窗样式 */
.login-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: #FFFFFF;
  border-radius: 24rpx;
  width: 600rpx;
  max-height: 80vh;
  overflow: hidden;
}

.login-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.login-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  width: 32rpx;
  height: 32rpx;
}

.login-body {
  padding: 32rpx;
}

.welcome-section {
  text-align: center;
  margin-bottom: 48rpx;
}

.welcome-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
}

.welcome-desc {
  display: block;
  font-size: 24rpx;
  color: #666666;
  line-height: 1.5;
}

.login-buttons {
  margin-bottom: 48rpx;
}

.login-btn {
  width: 100%;
  height: 88rpx;
  background: #07C160;
  color: #FFFFFF;
  border-radius: 44rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16rpx;
}

.wechat-icon {
  width: 40rpx;
  height: 40rpx;
}

.login-footer {
  text-align: center;
}

.skip-login {
  display: block;
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 32rpx;
}

.agreement-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.agreement-label {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.checkbox {
  width: 32rpx;
  height: 32rpx;
  border: 2rpx solid #DDDDDD;
  border-radius: 6rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox.checked {
  background: #FF6B6B;
  border-color: #FF6B6B;
}

.checkbox-text {
  font-size: 20rpx;
  color: #FFFFFF;
}

.agreement-text {
  font-size: 24rpx;
  color: #666666;
}

.agreement-link {
  font-size: 24rpx;
  color: #FF6B6B;
}
</style>