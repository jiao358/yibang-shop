<template>
  <view class="task-page">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
      <!-- 状态筛选 -->
      <view class="filter-tabs">
        <view 
          class="filter-tab" 
          :class="{ active: currentFilter === 'all' }"
          @click="changeFilter('all')"
        >
          全部
        </view>
        <view 
          class="filter-tab" 
          :class="{ active: currentFilter === 'available' }"
          @click="changeFilter('available')"
        >
          可领取
        </view>
        <view 
          class="filter-tab" 
          :class="{ active: currentFilter === 'in_progress' }"
          @click="changeFilter('in_progress')"
        >
          进行中
        </view>
        <view 
          class="filter-tab" 
          :class="{ active: currentFilter === 'completed' }"
          @click="changeFilter('completed')"
        >
          已完成
        </view>
      </view>
      
      <!-- 高级筛选 -->
      <view class="advanced-filters">
        <!-- 任务类型筛选 -->
        <view class="filter-section">
          <text class="filter-label">任务类型</text>
          <scroll-view class="filter-options" scroll-x="true" show-scrollbar="false">
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === '' }"
              @click="changeTaskType('')"
            >
              <text>全部</text>
            </view>
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === 'ad' }"
              @click="changeTaskType('ad')"
            >
              <text>广告任务</text>
            </view>
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === 'video' }"
              @click="changeTaskType('video')"
            >
              <text>视频任务</text>
            </view>
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === 'app_install' }"
              @click="changeTaskType('app_install')"
            >
              <text>应用安装</text>
            </view>
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === 'survey' }"
              @click="changeTaskType('survey')"
            >
              <text>问卷调查</text>
            </view>
            <view 
              class="filter-option" 
              :class="{ active: currentTaskType === 'share' }"
              @click="changeTaskType('share')"
            >
              <text>分享任务</text>
            </view>
          </scroll-view>
        </view>
        
        <!-- 佣金等级筛选 -->
        <view class="filter-section">
          <text class="filter-label">佣金等级</text>
          <view class="reward-filter-options">
            <view 
              class="reward-option" 
              :class="{ active: currentRewardLevel === '' }"
              @click="changeRewardLevel('')"
            >
              <text>全部</text>
            </view>
            <view 
              class="reward-option low" 
              :class="{ active: currentRewardLevel === 'low' }"
              @click="changeRewardLevel('low')"
            >
              <text>低佣金</text>
            </view>
            <view 
              class="reward-option medium" 
              :class="{ active: currentRewardLevel === 'medium' }"
              @click="changeRewardLevel('medium')"
            >
              <text>中佣金</text>
            </view>
            <view 
              class="reward-option high" 
              :class="{ active: currentRewardLevel === 'high' }"
              @click="changeRewardLevel('high')"
            >
              <text>高佣金</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view 
        class="task-card" 
        v-for="task in filteredTasks" 
        :key="task.id"
        @click="goToTaskDetail(task.id)"
      >
        <!-- 任务封面图片 -->
        <view class="task-cover">
          <image 
            :src="task.imageUrl || '/static/images/task-placeholder.png'" 
            class="cover-image" 
            mode="aspectFill"
          />
          <view class="reward-badge">
            <text class="reward-amount">¥{{ formatReward(task.rewardAmount) }}</text>
          </view>
          <view class="task-type-badge" :class="getTaskTypeClass(task.type)">
            <text class="type-text">{{ getTaskTypeText(task.type) }}</text>
          </view>
        </view>
        
        <!-- 任务信息 -->
        <view class="task-content">
          <view class="task-header">
            <text class="task-title">{{ task.title }}</text>
            <view class="reward-level" :class="task.rewardLevel">
              <text class="level-text">{{ getRewardLevelText(task.rewardLevel) }}</text>
            </view>
          </view>
          
          <text class="task-desc">{{ task.description }}</text>
          
          <view class="task-meta">
            <view class="meta-item">
              <text class="meta-icon">👥</text>
              <text class="meta-text">{{ task.currentClaimCount || 0 }}/{{ task.maxClaimCount === -1 ? '∞' : task.maxClaimCount }}</text>
            </view>
            <view class="meta-item" v-if="task.expireTime">
              <text class="meta-icon">⏰</text>
              <text class="meta-text">{{ formatTime(task.expireTime) }}</text>
            </view>
          </view>
          
          <view class="task-actions">
            <view class="action-btn primary" v-if="task.canClaim" @click.stop="claimTask(task)">
              <text>立即领取</text>
            </view>
            <view class="action-btn secondary" v-else-if="task.userTaskStatus === 'claimed'">
              <text>已领取</text>
            </view>
            <view class="action-btn secondary" v-else-if="task.userTaskStatus === 'completed'">
              <text>待审核</text>
            </view>
            <view class="action-btn success" v-else-if="task.userTaskStatus === 'verified'">
              <text>已完成</text>
            </view>
            <view class="action-btn disabled" v-else>
              <text>不可领取</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="filteredTasks.length === 0">
      <image src="/static/images/empty-task.png" mode="aspectFit"></image>
      <text class="empty-text">暂无任务</text>
      <text class="empty-desc">请稍后再来看看吧</text>
    </view>

    <!-- 加载更多 -->
    <view class="load-more" v-if="hasMore && filteredTasks.length > 0">
      <text v-if="loading">加载中...</text>
      <text v-else @click="loadMore">加载更多</text>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useTaskStore } from '@/stores/task'

export default {
  name: 'TaskPage',
  setup() {
    const taskStore = useTaskStore()

    // 响应式数据
    const currentFilter = ref('all')
    const currentTaskType = ref('')
    const currentRewardLevel = ref('')
    const loading = ref(false)
    const hasMore = ref(true)
    const page = ref(1)

    // 计算属性
    const filteredTasks = computed(() => {
      let tasks = taskStore.tasks || [] // 确保tasks是数组
      
      if (currentFilter.value === 'all') {
        return tasks
      }
      
      return tasks.filter(task => task.status === currentFilter.value)
    })

    // 方法
    const changeFilter = (filter) => {
      currentFilter.value = filter
      loadTasks(true)
    }

    const changeTaskType = (taskType) => {
      currentTaskType.value = taskType
      loadTasks(true)
    }

    const changeRewardLevel = (rewardLevel) => {
      currentRewardLevel.value = rewardLevel
      loadTasks(true)
    }

    const loadTasks = async (reset = false) => {
      if (loading.value) return
      
      loading.value = true
      
      try {
        if (reset) {
          page.value = 1
          hasMore.value = true
        }
        
        await taskStore.getTasks({
          page: page.value,
          status: currentFilter.value === 'all' ? null : currentFilter.value,
          taskType: currentTaskType.value || null,
          rewardLevel: currentRewardLevel.value || null
        })
        
        // taskStore.getTasks已经更新了tasks，不需要重复赋值
        hasMore.value = (taskStore.tasks || []).length < (taskStore.total || 0)
        page.value++
        
      } catch (error) {
        console.error('加载任务失败:', error)
        uni.showToast({
          title: '加载失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }

    const loadMore = () => {
      loadTasks(false)
    }

    const goToTaskDetail = (taskId) => {
      uni.navigateTo({
        url: `/pages/task-detail/task-detail?id=${taskId}`
      })
    }

    const claimTask = async (taskId) => {
      try {
        uni.showLoading({ title: '领取中...' })
        
        await taskStore.claimTask(taskId)
        
        uni.hideLoading()
        uni.showToast({
          title: '领取成功',
          icon: 'success'
        })
        
        // 刷新任务列表
        loadTasks(true)
        
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: error.message || '领取失败',
          icon: 'none'
        })
      }
    }

    // 格式化奖励金额
    const formatReward = (rewardAmount) => {
      if (!rewardAmount) return '0.00'
      return (rewardAmount / 100).toFixed(2)
    }

    // 获取任务类型样式类
    const getTaskTypeClass = (type) => {
      return `type-${type}`
    }

    const getTaskTypeText = (type) => {
      const typeMap = {
        'ad': '广告任务',
        'video': '视频任务',
        'app_install': '应用安装',
        'survey': '问卷调查',
        'share': '分享任务'
      }
      return typeMap[type] || '未知类型'
    }

    // 获取佣金等级文本
    const getRewardLevelText = (rewardLevel) => {
      const levelMap = {
        'low': '低佣金',
        'medium': '中佣金',
        'high': '高佣金'
      }
      return levelMap[rewardLevel] || rewardLevel
    }

    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return ''
      const date = new Date(timeStr)
      const now = new Date()
      const diff = date.getTime() - now.getTime()
      
      if (diff <= 0) return '已过期'
      
      const days = Math.floor(diff / (1000 * 60 * 60 * 24))
      const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      
      if (days > 0) return `${days}天后`
      if (hours > 0) return `${hours}小时后`
      return '即将过期'
    }

    const getTaskStatusText = (status) => {
      const statusMap = {
        'available': '可领取',
        'in_progress': '进行中',
        'completed': '已完成',
        'expired': '已过期',
        'claimed': '已领取'
      }
      return statusMap[status] || '未知状态'
    }

    const getLevelText = (level) => {
      const levelMap = {
        'normal': '普通',
        'signed': '签约',
        'vip': 'VIP'
      }
      return levelMap[level] || '普通'
    }

    // 页面是否已初始化
    const pageInitialized = ref(false)

    // 生命周期
    onMounted(() => {
      loadTasks(true)
      pageInitialized.value = true
    })

    onShow(() => {
      // 避免首次加载重复请求
      if (pageInitialized.value) {
        loadTasks(true)
      }
    })

    return {
      currentFilter,
      currentTaskType,
      currentRewardLevel,
      filteredTasks,
      loading,
      hasMore,
      changeFilter,
      changeTaskType,
      changeRewardLevel,
      loadMore,
      goToTaskDetail,
      claimTask,
      formatReward,
      getTaskTypeClass,
      getTaskTypeText,
      getRewardLevelText,
      formatTime,
      getTaskStatusText,
      getLevelText
    }
  }
}
</script>

<style scoped>
.task-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.filter-bar {
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.advanced-filters {
  margin-top: 24rpx;
}

.filter-section {
  margin-bottom: 20rpx;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-label {
  display: block;
  font-size: 24rpx;
  color: #666666;
  margin-bottom: 12rpx;
}

.filter-options {
  white-space: nowrap;
}

.filter-option {
  display: inline-block;
  padding: 8rpx 20rpx;
  margin-right: 16rpx;
  background: #F5F5F5;
  border-radius: 20rpx;
  font-size: 24rpx;
  color: #666666;
}

.filter-option.active {
  background: #FF6B6B;
  color: #FFFFFF;
}

.reward-filter-options {
  display: flex;
  gap: 16rpx;
}

.reward-option {
  flex: 1;
  padding: 12rpx 20rpx;
  text-align: center;
  border-radius: 16rpx;
  font-size: 24rpx;
  background: #F5F5F5;
  color: #666666;
}

.reward-option.active {
  background: #FF6B6B;
  color: #FFFFFF;
}

.reward-option.low.active {
  background: #4CAF50;
}

.reward-option.medium.active {
  background: #FF9800;
}

.reward-option.high.active {
  background: #F44336;
}

.filter-tabs {
  display: flex;
  gap: 40rpx;
}

.filter-tab {
  color: #666666;
  font-size: 28rpx;
  font-weight: 500;
  padding: 12rpx 0;
  position: relative;
}

.filter-tab.active {
  color: #FF6B6B;
  font-weight: 600;
}

.filter-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: #FF6B6B;
  border-radius: 2rpx;
}

.task-list {
  padding: 20rpx 30rpx;
}

.task-card {
  background: #FFFFFF;
  border-radius: 24rpx;
  overflow: hidden;
  margin-bottom: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.task-card:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.12);
}

.task-cover {
  position: relative;
  height: 200rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.cover-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reward-badge {
  position: absolute;
  top: 20rpx;
  right: 20rpx;
  background: rgba(255, 107, 107, 0.9);
  backdrop-filter: blur(10rpx);
  border-radius: 20rpx;
  padding: 8rpx 16rpx;
  min-width: 80rpx;
  text-align: center;
}

.reward-amount {
  color: #FFFFFF;
  font-size: 24rpx;
  font-weight: 600;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.2);
}

.task-type-badge {
  position: absolute;
  bottom: 20rpx;
  left: 20rpx;
  border-radius: 16rpx;
  padding: 6rpx 12rpx;
  backdrop-filter: blur(10rpx);
}

.task-type-badge.type-ad {
  background: rgba(255, 193, 7, 0.9);
}

.task-type-badge.type-video {
  background: rgba(76, 175, 80, 0.9);
}

.task-type-badge.type-app_install {
  background: rgba(33, 150, 243, 0.9);
}

.task-type-badge.type-survey {
  background: rgba(156, 39, 176, 0.9);
}

.task-type-badge.type-share {
  background: rgba(255, 87, 34, 0.9);
}

.type-text {
  color: #FFFFFF;
  font-size: 20rpx;
  font-weight: 500;
}

.task-content {
  padding: 32rpx;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.task-title {
  color: #333333;
  font-size: 32rpx;
  font-weight: 600;
  flex: 1;
  margin-right: 20rpx;
  line-height: 1.4;
}

.reward-level {
  border-radius: 12rpx;
  padding: 4rpx 12rpx;
  min-width: 60rpx;
  text-align: center;
}

.reward-level.low {
  background: #E8F5E8;
  color: #4CAF50;
}

.reward-level.medium {
  background: #FFF3E0;
  color: #FF9800;
}

.reward-level.high {
  background: #FFEBEE;
  color: #F44336;
}

.level-text {
  font-size: 20rpx;
  font-weight: 500;
}

.task-desc {
  color: #666666;
  font-size: 28rpx;
  line-height: 1.5;
  margin-bottom: 24rpx;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.task-meta {
  display: flex;
  gap: 24rpx;
  margin-bottom: 24rpx;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.meta-icon {
  font-size: 24rpx;
}

.meta-text {
  color: #999999;
  font-size: 24rpx;
}

.task-actions {
  display: flex;
  justify-content: flex-end;
}

.action-btn {
  padding: 16rpx 32rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  font-weight: 500;
  text-align: center;
  min-width: 120rpx;
}

.action-btn.primary {
  background: #FF6B6B;
  color: #FFFFFF;
}

.action-btn.secondary {
  background: #F5F5F5;
  color: #666666;
}

.action-btn.success {
  background: #E8F5E8;
  color: #4CAF50;
}

.action-btn.disabled {
  background: #F0F0F0;
  color: #CCCCCC;
}

.task-reward {
  color: #FF6B6B;
  font-size: 36rpx;
  font-weight: 700;
}

.task-description {
  color: #666666;
  font-size: 26rpx;
  line-height: 1.5;
  margin-bottom: 20rpx;
}

.task-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.task-info {
  display: flex;
  gap: 20rpx;
}

.task-type,
.task-duration,
.task-level {
  color: #999999;
  font-size: 22rpx;
  padding: 6rpx 12rpx;
  background: #F8F9FA;
  border-radius: 8rpx;
}

.task-level.signed {
  background: #E8F5E8;
  color: #28A745;
}

.task-level.vip {
  background: #FFF3CD;
  color: #FFC107;
}

.task-status {
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
  font-weight: 500;
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

.task-status.expired {
  background: #F8D7DA;
  color: #DC3545;
}

.task-progress {
  display: flex;
  align-items: center;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.progress-bar {
  flex: 1;
  height: 8rpx;
  background: #F0F0F0;
  border-radius: 4rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF6B6B 0%, #FF8E8E 100%);
  border-radius: 4rpx;
  transition: width 0.3s ease;
}

.progress-text {
  color: #666666;
  font-size: 22rpx;
  min-width: 60rpx;
  text-align: right;
}

.task-actions {
  display: flex;
  justify-content: flex-end;
}

.claim-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 16rpx 32rpx;
  border-radius: 24rpx;
  font-size: 26rpx;
  font-weight: 600;
  border: none;
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
}

.load-more {
  text-align: center;
  padding: 40rpx;
  color: #666666;
  font-size: 26rpx;
}
</style>
