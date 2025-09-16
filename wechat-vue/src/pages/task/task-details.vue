<template>
  <view class="task-details-page">
    <!-- 顶部统计卡片 -->
    <view class="stats-header">
      <view class="stat-item">
        <text class="stat-value">{{ totalTasks }}</text>
        <text class="stat-label">总任务数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ completedTasks }}</text>
        <text class="stat-label">已完成</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">¥{{ totalEarnings }}</text>
        <text class="stat-label">总收益</text>
      </view>
    </view>

    <!-- 筛选栏 -->
    <view class="filter-bar">
      <scroll-view scroll-x="true" class="filter-scroll">
        <view class="filter-tabs">
          <view 
            class="filter-tab" 
            :class="{ active: currentStatus === 'all' }"
            @tap="changeStatus('all')"
          >
            全部
          </view>
          <view 
            class="filter-tab" 
            :class="{ active: currentStatus === 'completed' }"
            @tap="changeStatus('completed')"
          >
            已完成
          </view>
          <view 
            class="filter-tab" 
            :class="{ active: currentStatus === 'verified' }"
            @tap="changeStatus('verified')"
          >
            已结算
          </view>
          <view 
            class="filter-tab" 
            :class="{ active: currentStatus === 'failed' }"
            @tap="changeStatus('failed')"
          >
            已失败
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 任务明细列表 -->
    <scroll-view 
      scroll-y="true" 
      class="task-list"
      @scrolltolower="loadMore"
      lower-threshold="100"
    >
      <view v-if="taskDetails.length === 0 && !loading" class="empty-state">
        <image src="/static/images/empty-task.png" class="empty-image" mode="aspectFit"></image>
        <text class="empty-text">暂无任务记录</text>
      </view>

      <view v-for="task in taskDetails" :key="task.id" class="task-item">
        <!-- 任务信息 -->
        <view class="task-info">
          <view class="task-header">
            <text class="task-title">{{ task.taskTitle }}</text>
            <view class="task-status" :class="getStatusClass(task.status)">
              <text>{{ getStatusText(task.status) }}</text>
            </view>
          </view>
          
          <view class="task-details">
            <view class="detail-row">
              <text class="detail-label">任务类型:</text>
              <text class="detail-value">{{ getTaskTypeText(task.taskType) }}</text>
            </view>
            <view class="detail-row">
              <text class="detail-label">领取时间:</text>
              <text class="detail-value">{{ formatTime(task.claimedAt) }}</text>
            </view>
            <view class="detail-row" v-if="task.completedAt">
              <text class="detail-label">完成时间:</text>
              <text class="detail-value">{{ formatTime(task.completedAt) }}</text>
            </view>
            <view class="detail-row" v-if="task.verifiedAt">
              <text class="detail-label">结算时间:</text>
              <text class="detail-value">{{ formatTime(task.verifiedAt) }}</text>
            </view>
            <view class="detail-row" v-if="task.failureReason">
              <text class="detail-label">失败原因:</text>
              <text class="detail-value failure-reason">{{ task.failureReason }}</text>
            </view>
          </view>
        </view>

        <!-- 收益信息 -->
        <view class="earnings-info">
          <view class="earnings-row">
            <text class="earnings-label">佣金收益</text>
            <text class="earnings-amount" :class="{ 'positive': task.status === 'verified' }">
              {{ task.status === 'verified' ? '+' : '' }}¥{{ task.rewardAmountDisplay || '0.00' }}
            </text>
          </view>
          <view class="earnings-row" v-if="task.status === 'verified'">
            <text class="earnings-label">余额变化</text>
            <text class="earnings-amount positive">+¥{{ task.rewardAmountDisplay || '0.00' }}</text>
          </view>
        </view>
      </view>

      <!-- 加载更多 -->
      <view v-if="loading" class="loading-more">
        <text>加载中...</text>
      </view>
      <view v-if="!hasMore && taskDetails.length > 0" class="no-more">
        <text>没有更多数据了</text>
      </view>
    </scroll-view>
  </view>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import { onLoad, onReachBottom } from '@dcloudio/uni-app'
import { taskApi } from '@/api/task'

export default {
  name: 'TaskDetailsPage',
  setup() {
    const loading = ref(false)
    const currentStatus = ref('all')
    const taskDetails = ref([])
    const totalTasks = ref(0)
    const completedTasks = ref(0)
    const totalEarnings = ref('0.00')
    const page = ref(1)
    const size = ref(10)
    const hasMore = ref(true)

    // 获取任务明细列表
    const loadTaskDetails = async (reset = false) => {
      if (loading.value) return
      
      loading.value = true
      try {
        if (reset) {
          page.value = 1
          hasMore.value = true
          taskDetails.value = []
        }

        const params = {
          status: currentStatus.value === 'all' ? undefined : currentStatus.value,
          page: page.value,
          size: size.value
        }
        
        const response = await taskApi.getUserTasks(params)
        const newTasks = response.data?.list || []
        
        if (reset) {
          taskDetails.value = newTasks
        } else {
          taskDetails.value = [...taskDetails.value, ...newTasks]
        }
        
        // 更新分页信息
        const total = response.data?.total || 0
        hasMore.value = taskDetails.value.length < total
        
        if (newTasks.length > 0) {
          page.value++
        }
        
        // 更新统计信息
        updateStats()
        
      } catch (error) {
        console.error('获取任务明细失败:', error)
        uni.showToast({
          title: '获取任务明细失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }

    // 更新统计信息
    const updateStats = () => {
      totalTasks.value = taskDetails.value.length
      completedTasks.value = taskDetails.value.filter(task => 
        task.status === 'completed' || task.status === 'verified'
      ).length
      
      const earnings = taskDetails.value
        .filter(task => task.status === 'verified')
        .reduce((sum, task) => {
          const amount = parseFloat(task.rewardAmountDisplay || '0')
          return sum + amount
        }, 0)
      
      totalEarnings.value = earnings.toFixed(2)
    }

    // 切换状态筛选
    const changeStatus = (status) => {
      if (currentStatus.value === status) return
      currentStatus.value = status
      loadTaskDetails(true)
    }

    // 加载更多
    const loadMore = () => {
      if (hasMore.value && !loading.value) {
        loadTaskDetails(false)
      }
    }

    // 获取状态样式类
    const getStatusClass = (status) => {
      const statusMap = {
        'claimed': 'status-claimed',
        'in_progress': 'status-progress',
        'completed': 'status-completed',
        'verified': 'status-verified',
        'failed': 'status-failed'
      }
      return statusMap[status] || 'status-default'
    }

    // 获取状态文本
    const getStatusText = (status) => {
      const statusMap = {
        'claimed': '已领取',
        'in_progress': '进行中',
        'completed': '待审核',
        'verified': '已结算',
        'failed': '已失败'
      }
      return statusMap[status] || '未知状态'
    }

    // 获取任务类型文本
    const getTaskTypeText = (taskType) => {
      const typeMap = {
        'view': '浏览任务',
        'register': '注册任务',
        'download': '下载任务',
        'share': '分享任务',
        'review': '评价任务',
        'survey': '问卷任务'
      }
      return typeMap[taskType] || taskType || '其他任务'
    }

    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return '--'
      const date = new Date(timeStr)
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
    }

    onMounted(() => {
      loadTaskDetails(true)
    })

    onReachBottom(() => {
      loadMore()
    })

    return {
      loading,
      currentStatus,
      taskDetails,
      totalTasks,
      completedTasks,
      totalEarnings,
      hasMore,
      changeStatus,
      loadMore,
      getStatusClass,
      getStatusText,
      getTaskTypeText,
      formatTime
    }
  }
}
</script>

<style scoped>
.task-details-page {
  background: #F5F5F5;
  min-height: 100vh;
}

/* 顶部统计 */
.stats-header {
  display: flex;
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 40rpx;
  border-radius: 20rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 48rpx;
  font-weight: 600;
  color: #FF6B6B;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 28rpx;
  color: #666666;
}

/* 筛选栏 */
.filter-bar {
  background: #FFFFFF;
  margin: 0 30rpx 20rpx;
  border-radius: 20rpx;
  padding: 20rpx 0;
}

.filter-scroll {
  white-space: nowrap;
}

.filter-tabs {
  display: flex;
  padding: 0 30rpx;
}

.filter-tab {
  padding: 16rpx 32rpx;
  margin-right: 20rpx;
  background: #F8F8F8;
  border-radius: 40rpx;
  font-size: 28rpx;
  color: #666666;
  white-space: nowrap;
}

.filter-tab.active {
  background: #FF6B6B;
  color: #FFFFFF;
}

/* 任务列表 */
.task-list {
  height: calc(100vh - 400rpx);
  padding: 0 30rpx;
}

.task-item {
  background: #FFFFFF;
  margin-bottom: 20rpx;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.05);
}

/* 任务信息 */
.task-info {
  margin-bottom: 30rpx;
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.task-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  flex: 1;
  margin-right: 20rpx;
}

.task-status {
  padding: 8rpx 16rpx;
  border-radius: 20rpx;
  font-size: 24rpx;
}

.status-claimed {
  background: #E3F2FD;
  color: #1976D2;
}

.status-progress {
  background: #FFF3E0;
  color: #F57C00;
}

.status-completed {
  background: #E8F5E8;
  color: #388E3C;
}

.status-verified {
  background: #E8F5E8;
  color: #2E7D32;
}

.status-failed {
  background: #FFEBEE;
  color: #D32F2F;
}

.task-details {
  border-top: 1rpx solid #F0F0F0;
  padding-top: 20rpx;
}

.detail-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.detail-label {
  font-size: 28rpx;
  color: #666666;
  width: 160rpx;
}

.detail-value {
  font-size: 28rpx;
  color: #333333;
  flex: 1;
}

.failure-reason {
  color: #D32F2F;
}

/* 收益信息 */
.earnings-info {
  border-top: 1rpx solid #F0F0F0;
  padding-top: 20rpx;
}

.earnings-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.earnings-label {
  font-size: 28rpx;
  color: #666666;
}

.earnings-amount {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.earnings-amount.positive {
  color: #2E7D32;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 100rpx 50rpx;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 30rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}

/* 加载状态 */
.loading-more, .no-more {
  text-align: center;
  padding: 30rpx;
  font-size: 28rpx;
  color: #999999;
}
</style>
