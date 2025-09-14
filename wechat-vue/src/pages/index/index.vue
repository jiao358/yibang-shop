<template>
  <view class="home-page">
    <!-- 顶部用户信息 -->
    <view class="user-header">
      <view class="user-info">
        <image class="avatar" :src="userInfo.avatar || '/static/images/default-avatar.png'" mode="aspectFill"></image>
        <view class="user-details">
          <text class="username">{{ userInfo.nickname || '用户' }}</text>
          <text class="user-level">{{ userLevelText }}</text>
        </view>
      </view>
      <view class="notification-btn" @click="goToNotifications">
        <image src="/static/icons/notification.png" mode="aspectFit"></image>
      </view>
    </view>

    <!-- 收益卡片 -->
    <view class="earnings-card">
      <view class="earnings-info">
        <text class="earnings-label">我的收益</text>
        <text class="earnings-amount">¥{{ userEarnings.balance || '0.00' }}</text>
        <text class="earnings-desc">可提现余额</text>
      </view>
      <view class="withdraw-btn" @click="goToWithdraw">
        <text>提现</text>
      </view>
    </view>

    <!-- 任务统计 -->
    <view class="stats-card">
      <view class="stat-item" v-for="(stat, index) in taskStats" :key="index">
        <text class="stat-value">{{ stat.value }}</text>
        <text class="stat-label">{{ stat.label }}</text>
      </view>
    </view>

    <!-- 轮播图 -->
    <view class="banner-section">
      <swiper class="banner-swiper" indicator-dots="true" autoplay="true" interval="3000" duration="500">
        <swiper-item v-for="(banner, index) in banners" :key="index" @click="onBannerClick(banner)">
          <image :src="banner.image" mode="aspectFill" class="banner-image"></image>
        </swiper-item>
      </swiper>
    </view>

    <!-- 快捷功能 -->
    <view class="quick-actions">
      <view class="action-item" @click="goToTask">
        <image src="/static/icons/task-quick.png" mode="aspectFit"></image>
        <text>任务大厅</text>
      </view>
      <view class="action-item" @click="goToMall">
        <image src="/static/icons/mall-quick.png" mode="aspectFit"></image>
        <text>商城购物</text>
      </view>
      <view class="action-item" @click="goToEarnings">
        <image src="/static/icons/earnings-quick.png" mode="aspectFit"></image>
        <text>收益管理</text>
      </view>
      <view class="action-item" @click="goToInvite">
        <image src="/static/icons/invite-quick.png" mode="aspectFit"></image>
        <text>邀请好友</text>
      </view>
    </view>

    <!-- 推荐任务 -->
    <view class="recommended-tasks">
      <view class="section-header">
        <text class="section-title">推荐任务</text>
        <text class="more-btn" @click="goToTask">更多 ></text>
      </view>
      <view class="task-list">
        <view class="task-item" v-for="task in recommendedTasks" :key="task.id" @click="goToTaskDetail(task.id)">
          <view class="task-info">
            <text class="task-title">{{ task.title }}</text>
            <text class="task-reward">+¥{{ task.reward }}</text>
          </view>
          <view class="task-status" :class="task.status">
            <text>{{ getTaskStatusText(task.status) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { useTaskStore } from '@/stores/task'
import { useEarningsStore } from '@/stores/earnings'

export default {
  name: 'HomePage',
  setup() {
    const userStore = useUserStore()
    const taskStore = useTaskStore()
    const earningsStore = useEarningsStore()

    // 响应式数据
    const userInfo = ref({})
    const userEarnings = ref({})
    const taskStats = ref([
      { value: '5', label: '今日任务' },
      { value: '3', label: '已完成' },
      { value: '¥128', label: '总收益' }
    ])
    const banners = ref([
      { id: 1, image: '/static/images/banner1.jpg', link: '/pages/task/task' },
      { id: 2, image: '/static/images/banner2.jpg', link: '/pages/mall/mall' }
    ])
    const recommendedTasks = ref([])

    // 计算属性
    const userLevelText = computed(() => {
      const level = userInfo.value.level || 0
      return level === 1 ? '签约用户' : '普通用户'
    })

    // 方法
    const loadUserInfo = async () => {
      try {
        await userStore.getUserInfo()
        userInfo.value = userStore.userInfo
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    const loadEarnings = async () => {
      try {
        await earningsStore.getUserEarnings()
        userEarnings.value = earningsStore.userEarnings
      } catch (error) {
        console.error('加载收益信息失败:', error)
      }
    }

    const loadRecommendedTasks = async () => {
      try {
        await taskStore.getRecommendedTasks()
        recommendedTasks.value = taskStore.recommendedTasks
      } catch (error) {
        console.error('加载推荐任务失败:', error)
      }
    }

    const goToTask = () => {
      uni.switchTab({ url: '/pages/task/task' })
    }

    const goToMall = () => {
      uni.switchTab({ url: '/pages/mall/mall' })
    }

    const goToEarnings = () => {
      uni.switchTab({ url: '/pages/earnings/earnings' })
    }

    const goToWithdraw = () => {
      uni.navigateTo({ url: '/pages/withdraw/withdraw' })
    }

    const goToNotifications = () => {
      uni.navigateTo({ url: '/pages/notifications/notifications' })
    }

    const goToInvite = () => {
      uni.navigateTo({ url: '/pages/invite/invite' })
    }

    const goToTaskDetail = (taskId) => {
      uni.navigateTo({ url: `/pages/task-detail/task-detail?id=${taskId}` })
    }

    const onBannerClick = (banner) => {
      if (banner.link) {
        uni.navigateTo({ url: banner.link })
      }
    }

    const getTaskStatusText = (status) => {
      const statusMap = {
        'available': '可领取',
        'in_progress': '进行中',
        'completed': '已完成',
        'expired': '已过期'
      }
      return statusMap[status] || '未知'
    }

    // 生命周期
    onMounted(() => {
      loadUserInfo()
      loadEarnings()
      loadRecommendedTasks()
    })

    return {
      userInfo,
      userEarnings,
      taskStats,
      banners,
      recommendedTasks,
      userLevelText,
      goToTask,
      goToMall,
      goToEarnings,
      goToWithdraw,
      goToNotifications,
      goToInvite,
      goToTaskDetail,
      onBannerClick,
      getTaskStatusText
    }
  }
}
</script>

<style scoped>
.home-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 40rpx 30rpx 20rpx;
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.user-details {
  display: flex;
  flex-direction: column;
}

.username {
  color: #FFFFFF;
  font-size: 32rpx;
  font-weight: 600;
  margin-bottom: 8rpx;
}

.user-level {
  color: rgba(255, 255, 255, 0.8);
  font-size: 24rpx;
}

.notification-btn {
  width: 60rpx;
  height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-btn image {
  width: 40rpx;
  height: 40rpx;
}

.earnings-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.earnings-info {
  display: flex;
  flex-direction: column;
}

.earnings-label {
  color: #666666;
  font-size: 24rpx;
  margin-bottom: 8rpx;
}

.earnings-amount {
  color: #FF6B6B;
  font-size: 48rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.earnings-desc {
  color: #999999;
  font-size: 22rpx;
}

.withdraw-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
}

.stats-card {
  display: flex;
  background: #FFFFFF;
  margin: 0 30rpx 20rpx;
  padding: 30rpx 0;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.stat-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.stat-value {
  color: #FF6B6B;
  font-size: 36rpx;
  font-weight: 700;
  margin-bottom: 8rpx;
}

.stat-label {
  color: #666666;
  font-size: 24rpx;
}

.banner-section {
  margin: 0 30rpx 30rpx;
}

.banner-swiper {
  height: 300rpx;
  border-radius: 16rpx;
  overflow: hidden;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.quick-actions {
  display: flex;
  background: #FFFFFF;
  margin: 0 30rpx 30rpx;
  padding: 30rpx 0;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.action-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.action-item image {
  width: 60rpx;
  height: 60rpx;
  margin-bottom: 12rpx;
}

.action-item text {
  color: #666666;
  font-size: 24rpx;
}

.recommended-tasks {
  background: #FFFFFF;
  margin: 0 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  color: #333333;
  font-size: 32rpx;
  font-weight: 600;
}

.more-btn {
  color: #FF6B6B;
  font-size: 24rpx;
}

.task-list {
  display: flex;
  flex-direction: column;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.task-item:last-child {
  border-bottom: none;
}

.task-info {
  flex: 1;
}

.task-title {
  color: #333333;
  font-size: 28rpx;
  font-weight: 500;
  margin-bottom: 8rpx;
  display: block;
}

.task-reward {
  color: #FF6B6B;
  font-size: 24rpx;
  font-weight: 600;
}

.task-status {
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.task-status.available {
  background: #E8F5E8;
  color: #28A745;
}

.task-status.in_progress {
  background: #FFF3CD;
  color: #FFC107;
}

.task-status.completed {
  background: #D1ECF1;
  color: #17A2B8;
}
</style>
