<template>
  <view class="profile-page">
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
      <view class="header-actions">
        <image src="/static/icons/more.png" class="action-icon" mode="aspectFit"></image>
        <image src="/static/icons/target.png" class="action-icon" mode="aspectFit"></image>
      </view>
    </view>

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

      <!-- 认证区域 -->
      <view class="verification-section">
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
      <view class="promotion-section">
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
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
      <view class="menu-grid">
        <view 
          v-for="(menu, index) in menuItems" 
          :key="index"
          class="menu-item"
          @click="handleMenuClick(menu.action)"
        >
          <image :src="menu.icon" class="menu-icon" mode="aspectFit"></image>
          <text class="menu-text">{{ menu.name }}</text>
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
        icon: '/static/icons/feedback.png',
        action: 'feedback'
      },
      {
        name: '浏览记录',
        icon: '/static/icons/history.png',
        action: 'history'
      },
      {
        name: '收货地址',
        icon: '/static/icons/address.png',
        action: 'address'
      },
      {
        name: '通知设置',
        icon: '/static/icons/notification.png',
        action: 'notification'
      },
      {
        name: '新手必读',
        icon: '/static/icons/guide.png',
        action: 'guide'
      },
      {
        name: '联系客服',
        icon: '/static/icons/service.png',
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
    
    onMounted(() => {
      loadUserInfo()
    })
    
    return {
      userInfo,
      menuItems,
      handleMenuClick,
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
  background: #FFFFFF;
  padding: 48rpx 32rpx;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 32rpx;
  margin-bottom: 48rpx;
}

.user-avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 64rpx;
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
  color: #999999;
}

.user-desc {
  font-size: 24rpx;
  color: #CCCCCC;
}

.target-icon {
  width: 48rpx;
  height: 48rpx;
}

.balance-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 48rpx;
  padding: 0 32rpx;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.stat-value {
  font-size: 40rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.stat-label {
  font-size: 24rpx;
  color: #999999;
}

.verification-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx;
  background: #FFF0F5;
  border-radius: 16rpx;
  margin-bottom: 32rpx;
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
  color: #666666;
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
  border-radius: 16rpx;
  padding: 32rpx;
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
}

.promotion-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
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
  margin: 32rpx;
  border-radius: 16rpx;
  padding: 32rpx;
}

.menu-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 48rpx;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16rpx;
  width: calc(25% - 36rpx);
  min-width: 120rpx;
}

.menu-icon {
  width: 48rpx;
  height: 48rpx;
}

.menu-text {
  font-size: 24rpx;
  color: #666666;
  text-align: center;
}
</style>