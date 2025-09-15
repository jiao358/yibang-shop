<template>
  <view class="task-page">
    <!-- 顶部筛选栏 -->
    <view class="filter-bar">
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
    </view>

    <!-- 任务列表 -->
    <view class="task-list">
      <view 
        class="task-item" 
        v-for="task in filteredTasks" 
        :key="task.id"
        @click="goToTaskDetail(task.id)"
      >
        <view class="task-header">
          <view class="task-title">{{ task.title }}</view>
          <view class="task-reward">+¥{{ task.reward }}</view>
        </view>
        
        <view class="task-description">{{ task.description }}</view>
        
        <view class="task-meta">
          <view class="task-info">
            <text class="task-type">{{ getTaskTypeText(task.type) }}</text>
            <text class="task-duration">{{ task.duration }}分钟</text>
            <text class="task-level" :class="task.level">{{ getLevelText(task.level) }}</text>
          </view>
          <view class="task-status" :class="task.status">
            {{ getTaskStatusText(task.status) }}
          </view>
        </view>
        
        <view class="task-progress" v-if="task.status === 'in_progress'">
          <view class="progress-bar">
            <view class="progress-fill" :style="{ width: task.progress + '%' }"></view>
          </view>
          <text class="progress-text">{{ task.progress }}%</text>
        </view>
        
        <view class="task-actions" v-if="task.status === 'available'">
          <button class="claim-btn" @click.stop="claimTask(task.id)">立即领取</button>
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
import { useTaskStore } from '@/stores/task'

export default {
  name: 'TaskPage',
  setup() {
    const taskStore = useTaskStore()

    // 响应式数据
    const currentFilter = ref('all')
    const loading = ref(false)
    const hasMore = ref(true)
    const page = ref(1)

    // 计算属性
    const filteredTasks = computed(() => {
      let tasks = taskStore.tasks
      
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
          status: currentFilter.value === 'all' ? null : currentFilter.value
        })
        
        if (reset) {
          taskStore.tasks = taskStore.tasks
        } else {
          taskStore.tasks = [...taskStore.tasks, ...taskStore.tasks]
        }
        
        hasMore.value = taskStore.tasks.length < taskStore.total
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

    // 生命周期
    onMounted(() => {
      loadTasks(true)
    })

    return {
      currentFilter,
      filteredTasks,
      loading,
      hasMore,
      changeFilter,
      loadMore,
      goToTaskDetail,
      claimTask,
      getTaskTypeText,
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

.task-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.task-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.task-title {
  color: #333333;
  font-size: 32rpx;
  font-weight: 600;
  flex: 1;
  margin-right: 20rpx;
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
