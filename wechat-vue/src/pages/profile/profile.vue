<template>
  <view class="profile-page">
    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-info">
        <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
        <view class="user-details">
          <text class="username">{{ userInfo.nickname || '用户' }}</text>
          <text class="user-level">{{ getUserLevelText(userInfo.level) }}</text>
          <text class="user-id">ID: {{ userInfo.id || '123456' }}</text>
        </view>
      </view>
      <view class="user-stats">
        <view class="stat-item">
          <text class="stat-value">{{ userStats.totalTasks || 0 }}</text>
          <text class="stat-label">完成任务</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">¥{{ userStats.totalEarnings || '0.00' }}</text>
          <text class="stat-label">累计收益</text>
        </view>
        <view class="stat-item">
          <text class="stat-value">{{ userStats.signInDays || 0 }}</text>
          <text class="stat-label">签到天数</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-sections">
      <!-- 账户管理 -->
      <view class="menu-section">
        <view class="section-title">账户管理</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToUserInfo">
            <image src="/static/icons/user-info.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">个人信息</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToSecurity">
            <image src="/static/icons/security.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">安全设置</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToSigning" v-if="!userInfo.isSigned">
            <image src="/static/icons/signing.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">申请签约</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToSigningStatus" v-if="userInfo.isSigned">
            <image src="/static/icons/signing-status.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">签约状态</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>

      <!-- 收益相关 -->
      <view class="menu-section">
        <view class="section-title">收益相关</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToEarnings">
            <image src="/static/icons/earnings.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">收益管理</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToWithdraw">
            <image src="/static/icons/withdraw.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">提现记录</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToInvite">
            <image src="/static/icons/invite.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">邀请好友</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>

      <!-- 订单相关 -->
      <view class="menu-section">
        <view class="section-title">订单相关</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToOrders">
            <image src="/static/icons/orders.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">我的订单</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToFavorites">
            <image src="/static/icons/favorites.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">我的收藏</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToBrowsingHistory">
            <image src="/static/icons/history.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">浏览历史</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>

      <!-- 应用设置 -->
      <view class="menu-section">
        <view class="section-title">应用设置</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToNotifications">
            <image src="/static/icons/notifications.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">消息通知</text>
            <view class="menu-right">
              <switch :checked="notificationSettings.enabled" @change="toggleNotification" color="#FF6B6B" />
            </view>
          </view>
          <view class="menu-item" @click="goToPrivacy">
            <image src="/static/icons/privacy.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">隐私设置</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToAbout">
            <image src="/static/icons/about.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">关于我们</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>

      <!-- 其他功能 -->
      <view class="menu-section">
        <view class="section-title">其他功能</view>
        <view class="menu-list">
          <view class="menu-item" @click="goToHelp">
            <image src="/static/icons/help.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">帮助中心</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToFeedback">
            <image src="/static/icons/feedback.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">意见反馈</text>
            <text class="menu-arrow">></text>
          </view>
          <view class="menu-item" @click="goToContact">
            <image src="/static/icons/contact.png" mode="aspectFit" class="menu-icon"></image>
            <text class="menu-text">联系我们</text>
            <text class="menu-arrow">></text>
          </view>
        </view>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="logout-section">
      <button class="logout-btn" @click="logout">退出登录</button>
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
    const userInfo = ref({})
    const userStats = ref({})
    const notificationSettings = ref({
      enabled: true
    })

    // 方法
    const loadUserData = async () => {
      try {
        // Mock数据
        userInfo.value = {
          id: '123456',
          nickname: '用户昵称',
          avatar: '/static/images/default-avatar.png',
          level: 1,
          isSigned: false
        }

        userStats.value = {
          totalTasks: 156,
          totalEarnings: 1256.80,
          signInDays: 30
        }

      } catch (error) {
        console.error('加载用户数据失败:', error)
      }
    }

    const getUserLevelText = (level) => {
      const levelMap = {
        0: '普通用户',
        1: '签约用户',
        2: 'VIP用户'
      }
      return levelMap[level] || '普通用户'
    }

    const toggleNotification = (e) => {
      notificationSettings.value.enabled = e.detail.value
      // 这里可以调用API保存设置
    }

    const goToUserInfo = () => {
      uni.navigateTo({
        url: '/pages/user-info/user-info'
      })
    }

    const goToSecurity = () => {
      uni.navigateTo({
        url: '/pages/security/security'
      })
    }

    const goToSigning = () => {
      uni.navigateTo({
        url: '/pages/signing/signing'
      })
    }

    const goToSigningStatus = () => {
      uni.navigateTo({
        url: '/pages/signing-status/signing-status'
      })
    }

    const goToEarnings = () => {
      uni.switchTab({
        url: '/pages/earnings/earnings'
      })
    }

    const goToWithdraw = () => {
      uni.navigateTo({
        url: '/pages/withdraw/withdraw'
      })
    }

    const goToInvite = () => {
      uni.navigateTo({
        url: '/pages/invite/invite'
      })
    }

    const goToOrders = () => {
      uni.navigateTo({
        url: '/pages/orders/orders'
      })
    }

    const goToFavorites = () => {
      uni.navigateTo({
        url: '/pages/favorites/favorites'
      })
    }

    const goToBrowsingHistory = () => {
      uni.navigateTo({
        url: '/pages/browsing-history/browsing-history'
      })
    }

    const goToNotifications = () => {
      uni.navigateTo({
        url: '/pages/notifications/notifications'
      })
    }

    const goToPrivacy = () => {
      uni.navigateTo({
        url: '/pages/privacy/privacy'
      })
    }

    const goToAbout = () => {
      uni.navigateTo({
        url: '/pages/about/about'
      })
    }

    const goToHelp = () => {
      uni.navigateTo({
        url: '/pages/help/help'
      })
    }

    const goToFeedback = () => {
      uni.navigateTo({
        url: '/pages/feedback/feedback'
      })
    }

    const goToContact = () => {
      uni.navigateTo({
        url: '/pages/contact/contact'
      })
    }

    const logout = () => {
      uni.showModal({
        title: '确认退出',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            userStore.logout()
            uni.reLaunch({
              url: '/pages/login/login'
            })
          }
        }
      })
    }

    // 生命周期
    onMounted(() => {
      loadUserData()
    })

    return {
      userInfo,
      userStats,
      notificationSettings,
      getUserLevelText,
      toggleNotification,
      goToUserInfo,
      goToSecurity,
      goToSigning,
      goToSigningStatus,
      goToEarnings,
      goToWithdraw,
      goToInvite,
      goToOrders,
      goToFavorites,
      goToBrowsingHistory,
      goToNotifications,
      goToPrivacy,
      goToAbout,
      goToHelp,
      goToFeedback,
      goToContact,
      logout
    }
  }
}
</script>

<style scoped>
.profile-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.user-card {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  margin: 20rpx 30rpx;
  padding: 40rpx;
  border-radius: 20rpx;
  color: #FFFFFF;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  margin-right: 24rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.3);
}

.user-details {
  flex: 1;
}

.username {
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  display: block;
}

.user-level {
  font-size: 24rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
  display: block;
}

.user-id {
  font-size: 22rpx;
  opacity: 0.6;
}

.user-stats {
  display: flex;
  justify-content: space-between;
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 32rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
  display: block;
}

.stat-label {
  font-size: 22rpx;
  opacity: 0.8;
}

.menu-sections {
  padding: 0 30rpx;
}

.menu-section {
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
  padding-left: 8rpx;
}

.menu-list {
  background: #FFFFFF;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx 24rpx;
  border-bottom: 1rpx solid #F0F0F0;
  position: relative;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:active {
  background: #F8F9FA;
}

.menu-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 24rpx;
}

.menu-text {
  flex: 1;
  font-size: 28rpx;
  color: #333333;
}

.menu-arrow {
  font-size: 24rpx;
  color: #CCCCCC;
}

.menu-right {
  display: flex;
  align-items: center;
}

.logout-section {
  padding: 40rpx 30rpx;
}

.logout-btn {
  width: 100%;
  background: #FFFFFF;
  color: #DC3545;
  padding: 24rpx;
  border-radius: 16rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: 2rpx solid #DC3545;
}

.logout-btn:active {
  background: #F8D7DA;
}
</style>
