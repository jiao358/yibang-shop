<template>
  <view class="task-detail-page">
    <!-- 任务头部信息 -->
    <view class="task-header">
      <view class="task-title">{{ taskDetail.title }}</view>
      <view class="task-reward">+¥{{ taskDetail.reward }}</view>
    </view>

    <!-- 任务状态 -->
    <view class="task-status" :class="taskDetail.status">
      <text>{{ getTaskStatusText(taskDetail.status) }}</text>
    </view>

    <!-- 任务进度 -->
    <view class="task-progress" v-if="taskDetail.status === 'in_progress'">
      <view class="progress-header">
        <text class="progress-label">任务进度</text>
        <text class="progress-text">{{ taskDetail.progress }}%</text>
      </view>
      <view class="progress-bar">
        <view class="progress-fill" :style="{ width: taskDetail.progress + '%' }"></view>
      </view>
    </view>

    <!-- 任务详情 -->
    <view class="task-info">
      <view class="info-section">
        <text class="section-title">任务描述</text>
        <text class="section-content">{{ taskDetail.description }}</text>
      </view>

      <view class="info-section">
        <text class="section-title">任务要求</text>
        <view class="requirements-list">
          <view class="requirement-item" v-for="(req, index) in taskDetail.requirements" :key="index">
            <text class="requirement-text">{{ req }}</text>
          </view>
        </view>
      </view>

      <view class="info-section">
        <text class="section-title">完成步骤</text>
        <view class="steps-list">
          <view class="step-item" v-for="(step, index) in taskDetail.steps" :key="index">
            <view class="step-number">{{ index + 1 }}</view>
            <text class="step-text">{{ step }}</text>
          </view>
        </view>
      </view>

      <view class="info-section">
        <text class="section-title">任务信息</text>
        <view class="info-grid">
          <view class="info-item">
            <text class="info-label">任务类型</text>
            <text class="info-value">{{ getTaskTypeText(taskDetail.type) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">预计时长</text>
            <text class="info-value">{{ taskDetail.duration }}分钟</text>
          </view>
          <view class="info-item">
            <text class="info-label">难度等级</text>
            <text class="info-value">{{ getLevelText(taskDetail.level) }}</text>
          </view>
          <view class="info-item">
            <text class="info-label">截止时间</text>
            <text class="info-value">{{ taskDetail.deadline }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 奖励信息 -->
    <view class="reward-info">
      <view class="reward-header">
        <text class="reward-title">奖励信息</text>
      </view>
      <view class="reward-content">
        <view class="reward-item">
          <text class="reward-label">基础奖励</text>
          <text class="reward-value">¥{{ taskDetail.reward }}</text>
        </view>
        <view class="reward-item" v-if="taskDetail.bonusReward">
          <text class="reward-label">额外奖励</text>
          <text class="reward-value">¥{{ taskDetail.bonusReward }}</text>
        </view>
        <view class="reward-item">
          <text class="reward-label">结算周期</text>
          <text class="reward-value">{{ taskDetail.settlementCycle }}</text>
        </view>
      </view>
    </view>

    <!-- 相关商品 -->
    <view class="related-products" v-if="relatedProducts.length > 0">
      <view class="section-header">
        <text class="section-title">相关商品</text>
        <text class="more-btn" @click="goToMall">更多 ></text>
      </view>
      <scroll-view scroll-x="true" class="products-scroll">
        <view class="products-list">
          <view 
            class="product-item" 
            v-for="product in relatedProducts" 
            :key="product.id"
            @click="goToProductDetail(product.id)"
          >
            <image :src="product.image" mode="aspectFill" class="product-image"></image>
            <text class="product-title">{{ product.title }}</text>
            <text class="product-price">¥{{ product.price }}</text>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 任务评价 -->
    <view class="task-reviews" v-if="taskReviews.length > 0">
      <view class="section-header">
        <text class="section-title">用户评价</text>
        <text class="more-btn" @click="goToReviews">更多 ></text>
      </view>
      <view class="reviews-list">
        <view class="review-item" v-for="review in taskReviews" :key="review.id">
          <view class="review-header">
            <image :src="review.userAvatar" mode="aspectFill" class="review-avatar"></image>
            <view class="review-info">
              <text class="review-username">{{ review.username }}</text>
              <text class="review-time">{{ review.time }}</text>
            </view>
            <view class="review-rating">
              <text>⭐⭐⭐⭐⭐</text>
            </view>
          </view>
          <text class="review-content">{{ review.content }}</text>
        </view>
      </view>
    </view>

    <!-- 底部操作按钮 -->
    <view class="task-actions">
      <button 
        class="action-btn primary" 
        v-if="taskDetail.status === 'available'"
        @click="claimTask"
      >
        立即领取
      </button>
      <button 
        class="action-btn primary" 
        v-if="taskDetail.status === 'in_progress'"
        @click="completeTask"
      >
        完成任务
      </button>
      <button 
        class="action-btn secondary" 
        v-if="taskDetail.status === 'in_progress'"
        @click="abandonTask"
      >
        放弃任务
      </button>
      <button 
        class="action-btn secondary" 
        v-if="taskDetail.status === 'completed'"
        @click="goToTask"
      >
        返回任务大厅
      </button>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useTaskStore } from '@/stores/task'

export default {
  name: 'TaskDetailPage',
  setup() {
    const taskStore = useTaskStore()

    // 响应式数据
    const taskDetail = ref({})
    const relatedProducts = ref([])
    const taskReviews = ref([])

    // 方法
    const loadTaskDetail = async () => {
      try {
        // Mock数据
        taskDetail.value = {
          id: 1,
          title: '观看广告视频任务',
          description: '观看完整的广告视频，了解产品信息，完成任务后即可获得奖励。',
          reward: 5.50,
          type: 'ad',
          level: 'normal',
          duration: 3,
          status: 'available',
          progress: 0,
          deadline: '2024-01-20 23:59',
          settlementCycle: '即时结算',
          bonusReward: 1.00,
          requirements: [
            '观看完整视频（不少于30秒）',
            '不能快进或跳过',
            '需要完成视频后的简单问答'
          ],
          steps: [
            '点击开始观看按钮',
            '完整观看广告视频',
            '回答视频相关问题',
            '提交答案完成任务'
          ]
        }

        relatedProducts.value = [
          {
            id: 1,
            title: '商品名称1',
            price: 99.00,
            image: '/static/images/product1.jpg'
          },
          {
            id: 2,
            title: '商品名称2',
            price: 199.00,
            image: '/static/images/product2.jpg'
          }
        ]

        taskReviews.value = [
          {
            id: 1,
            username: '用户1',
            userAvatar: '/static/images/avatar1.jpg',
            time: '2天前',
            content: '任务很简单，视频内容也很有趣，奖励到账很快！',
            rating: 5
          },
          {
            id: 2,
            username: '用户2',
            userAvatar: '/static/images/avatar2.jpg',
            time: '5天前',
            content: '任务要求明确，完成过程很顺利，推荐！',
            rating: 5
          }
        ]

      } catch (error) {
        console.error('加载任务详情失败:', error)
      }
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

    const getLevelText = (level) => {
      const levelMap = {
        'normal': '普通',
        'signed': '签约',
        'vip': 'VIP'
      }
      return levelMap[level] || '普通'
    }

    const claimTask = async () => {
      try {
        uni.showLoading({ title: '领取中...' })
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        taskDetail.value.status = 'claimed'
        
        uni.hideLoading()
        uni.showToast({
          title: '领取成功',
          icon: 'success'
        })
        
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: '领取失败',
          icon: 'none'
        })
      }
    }

    const completeTask = async () => {
      try {
        uni.showLoading({ title: '完成中...' })
        
        // 模拟API调用
        await new Promise(resolve => setTimeout(resolve, 1000))
        
        taskDetail.value.status = 'completed'
        taskDetail.value.progress = 100
        
        uni.hideLoading()
        uni.showToast({
          title: '任务完成',
          icon: 'success'
        })
        
      } catch (error) {
        uni.hideLoading()
        uni.showToast({
          title: '完成失败',
          icon: 'none'
        })
      }
    }

    const abandonTask = () => {
      uni.showModal({
        title: '确认放弃',
        content: '确定要放弃这个任务吗？',
        success: (res) => {
          if (res.confirm) {
            taskDetail.value.status = 'available'
            taskDetail.value.progress = 0
            uni.showToast({
              title: '已放弃任务',
              icon: 'success'
            })
          }
        }
      })
    }

    const goToMall = () => {
      uni.switchTab({
        url: '/pages/mall/mall'
      })
    }

    const goToProductDetail = (productId) => {
      uni.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      })
    }

    const goToReviews = () => {
      uni.navigateTo({
        url: `/pages/task-reviews/task-reviews?taskId=${taskDetail.value.id}`
      })
    }

    const goToTask = () => {
      uni.switchTab({
        url: '/pages/task/task'
      })
    }

    // 生命周期
    onMounted(() => {
      loadTaskDetail()
    })

    return {
      taskDetail,
      relatedProducts,
      taskReviews,
      getTaskStatusText,
      getTaskTypeText,
      getLevelText,
      claimTask,
      completeTask,
      abandonTask,
      goToMall,
      goToProductDetail,
      goToReviews,
      goToTask
    }
  }
}
</script>

<style scoped>
.task-detail-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.task-header {
  background: #FFFFFF;
  padding: 40rpx 30rpx;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.task-title {
  flex: 1;
  font-size: 36rpx;
  font-weight: 700;
  color: #333333;
  line-height: 1.4;
  margin-right: 20rpx;
}

.task-reward {
  font-size: 48rpx;
  font-weight: 700;
  color: #FF6B6B;
}

.task-status {
  background: #FFFFFF;
  margin: 0 30rpx 20rpx;
  padding: 20rpx 30rpx;
  border-radius: 16rpx;
  text-align: center;
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

.task-progress {
  background: #FFFFFF;
  margin: 0 30rpx 20rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.progress-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
}

.progress-text {
  font-size: 24rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.progress-bar {
  height: 12rpx;
  background: #F0F0F0;
  border-radius: 6rpx;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF6B6B 0%, #FF8E8E 100%);
  border-radius: 6rpx;
  transition: width 0.3s ease;
}

.task-info,
.reward-info,
.related-products,
.task-reviews {
  background: #FFFFFF;
  margin: 0 30rpx 20rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.info-section {
  margin-bottom: 30rpx;
}

.info-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
  display: block;
}

.section-content {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
}

.requirements-list,
.steps-list {
  display: flex;
  flex-direction: column;
}

.requirement-item,
.step-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12rpx;
}

.step-item {
  align-items: center;
}

.step-number {
  width: 40rpx;
  height: 40rpx;
  background: #FF6B6B;
  color: #FFFFFF;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
  font-weight: 600;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.requirement-text,
.step-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20rpx;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-label {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
}

.info-value {
  font-size: 26rpx;
  color: #333333;
  font-weight: 500;
}

.reward-header {
  margin-bottom: 20rpx;
}

.reward-content {
  display: flex;
  flex-direction: column;
}

.reward-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.reward-item:last-child {
  border-bottom: none;
}

.reward-label {
  font-size: 26rpx;
  color: #666666;
}

.reward-value {
  font-size: 28rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.more-btn {
  color: #FF6B6B;
  font-size: 24rpx;
}

.products-scroll {
  white-space: nowrap;
}

.products-list {
  display: flex;
  gap: 20rpx;
}

.product-item {
  width: 200rpx;
  flex-shrink: 0;
}

.product-image {
  width: 200rpx;
  height: 200rpx;
  border-radius: 12rpx;
  margin-bottom: 12rpx;
}

.product-title {
  font-size: 24rpx;
  color: #333333;
  margin-bottom: 8rpx;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  font-size: 26rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.reviews-list {
  display: flex;
  flex-direction: column;
}

.review-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.review-item:last-child {
  border-bottom: none;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.review-avatar {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  margin-right: 16rpx;
}

.review-info {
  flex: 1;
}

.review-username {
  font-size: 26rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 4rpx;
  display: block;
}

.review-time {
  font-size: 22rpx;
  color: #999999;
}

.review-rating {
  font-size: 20rpx;
}

.review-content {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
}

.task-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #F0F0F0;
  display: flex;
  gap: 20rpx;
}

.action-btn {
  flex: 1;
  padding: 24rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.action-btn.primary {
  background: #FF6B6B;
  color: #FFFFFF;
}

.action-btn.secondary {
  background: #F8F9FA;
  color: #666666;
  border: 2rpx solid #E9ECEF;
}
</style>
