<template>
  <view class="earnings-page">
    <!-- 收益概览卡片 -->
    <view class="earnings-overview">
      <view class="overview-header">
        <text class="overview-title">我的收益</text>
        <view class="overview-actions">
          <text class="action-btn" @click="goToWithdraw">提现</text>
          <text class="action-btn" @click="goToEarningsDetail">明细</text>
        </view>
      </view>
      
      <view class="overview-content">
        <view class="balance-section">
          <text class="balance-label">可提现余额</text>
          <text class="balance-amount">¥{{ userEarnings.balance || '0.00' }}</text>
        </view>
        
        <view class="stats-row">
          <view class="stat-item">
            <text class="stat-value">¥{{ userEarnings.totalEarnings || '0.00' }}</text>
            <text class="stat-label">累计收益</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">¥{{ userEarnings.todayEarnings || '0.00' }}</text>
            <text class="stat-label">今日收益</text>
          </view>
          <view class="stat-item">
            <text class="stat-value">¥{{ userEarnings.frozenAmount || '0.00' }}</text>
            <text class="stat-label">冻结金额</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 收益趋势图 -->
    <view class="earnings-chart">
      <view class="chart-header">
        <text class="chart-title">收益趋势</text>
        <view class="chart-tabs">
          <text 
            class="chart-tab" 
            :class="{ active: chartPeriod === '7d' }"
            @click="changeChartPeriod('7d')"
          >
            7天
          </text>
          <text 
            class="chart-tab" 
            :class="{ active: chartPeriod === '30d' }"
            @click="changeChartPeriod('30d')"
          >
            30天
          </text>
        </view>
      </view>
      <view class="chart-placeholder">
        <image src="/static/images/chart-placeholder.png" mode="aspectFit"></image>
        <text class="chart-desc">收益趋势图</text>
      </view>
    </view>

    <!-- 收益来源统计 -->
    <view class="earnings-sources">
      <view class="section-header">
        <text class="section-title">收益来源</text>
      </view>
      <view class="sources-list">
        <view class="source-item" v-for="source in earningsSources" :key="source.type">
          <view class="source-info">
            <image :src="source.icon" mode="aspectFit" class="source-icon"></image>
            <text class="source-name">{{ source.name }}</text>
          </view>
          <view class="source-amount">
            <text class="amount">¥{{ source.amount }}</text>
            <text class="percentage">{{ source.percentage }}%</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 最近收益记录 -->
    <view class="recent-earnings">
      <view class="section-header">
        <text class="section-title">最近收益</text>
        <text class="more-btn" @click="goToEarningsList">更多 ></text>
      </view>
      <view class="earnings-list">
        <view class="earnings-item" v-for="earning in recentEarnings" :key="earning.id">
          <view class="earning-info">
            <text class="earning-title">{{ earning.title }}</text>
            <text class="earning-time">{{ earning.time }}</text>
          </view>
          <view class="earning-amount" :class="earning.type">
            <text>{{ earning.type === 'income' ? '+' : '-' }}¥{{ earning.amount }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 提现记录 -->
    <view class="withdrawal-history">
      <view class="section-header">
        <text class="section-title">提现记录</text>
        <text class="more-btn" @click="goToWithdrawalList">更多 ></text>
      </view>
      <view class="withdrawal-list">
        <view class="withdrawal-item" v-for="withdrawal in recentWithdrawals" :key="withdrawal.id">
          <view class="withdrawal-info">
            <text class="withdrawal-amount">¥{{ withdrawal.amount }}</text>
            <text class="withdrawal-time">{{ withdrawal.time }}</text>
          </view>
          <view class="withdrawal-status" :class="withdrawal.status">
            <text>{{ getWithdrawalStatusText(withdrawal.status) }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useEarningsStore } from '@/stores/earnings'

export default {
  name: 'EarningsPage',
  setup() {
    const earningsStore = useEarningsStore()

    // 响应式数据
    const userEarnings = ref({})
    const chartPeriod = ref('7d')
    const earningsSources = ref([])
    const recentEarnings = ref([])
    const recentWithdrawals = ref([])

    // 方法
    const loadEarningsData = async () => {
      try {
        // Mock数据
        userEarnings.value = {
          balance: 128.50,
          totalEarnings: 1256.80,
          todayEarnings: 45.20,
          frozenAmount: 12.30
        }

        earningsSources.value = [
          {
            type: 'task',
            name: '任务收益',
            amount: 856.30,
            percentage: 68.1,
            icon: '/static/icons/task-earnings.png'
          },
          {
            type: 'invite',
            name: '邀请奖励',
            amount: 234.50,
            percentage: 18.7,
            icon: '/static/icons/invite-earnings.png'
          },
          {
            type: 'mall',
            name: '商城返利',
            amount: 166.00,
            percentage: 13.2,
            icon: '/static/icons/mall-earnings.png'
          }
        ]

        recentEarnings.value = [
          {
            id: 1,
            title: '完成广告任务',
            amount: 5.50,
            type: 'income',
            time: '2小时前'
          },
          {
            id: 2,
            title: '邀请好友注册',
            amount: 10.00,
            type: 'income',
            time: '5小时前'
          },
          {
            id: 3,
            title: '商城购买返利',
            amount: 2.30,
            type: 'income',
            time: '1天前'
          },
          {
            id: 4,
            title: '提现手续费',
            amount: 1.00,
            type: 'expense',
            time: '2天前'
          }
        ]

        recentWithdrawals.value = [
          {
            id: 1,
            amount: 100.00,
            status: 'completed',
            time: '2024-01-15 14:30'
          },
          {
            id: 2,
            amount: 50.00,
            status: 'processing',
            time: '2024-01-14 09:15'
          },
          {
            id: 3,
            amount: 200.00,
            status: 'failed',
            time: '2024-01-12 16:45'
          }
        ]

      } catch (error) {
        console.error('加载收益数据失败:', error)
      }
    }

    const changeChartPeriod = (period) => {
      chartPeriod.value = period
      // 这里可以重新加载图表数据
    }

    const goToWithdraw = () => {
      uni.navigateTo({
        url: '/pages/withdraw/withdraw'
      })
    }

    const goToEarningsDetail = () => {
      uni.navigateTo({
        url: '/pages/earnings-detail/earnings-detail'
      })
    }

    const goToEarningsList = () => {
      uni.navigateTo({
        url: '/pages/earnings-list/earnings-list'
      })
    }

    const goToWithdrawalList = () => {
      uni.navigateTo({
        url: '/pages/withdrawal-list/withdrawal-list'
      })
    }

    const getWithdrawalStatusText = (status) => {
      const statusMap = {
        'completed': '已完成',
        'processing': '处理中',
        'failed': '失败',
        'pending': '待审核'
      }
      return statusMap[status] || '未知'
    }

    // 生命周期
    onMounted(() => {
      loadEarningsData()
    })

    return {
      userEarnings,
      chartPeriod,
      earningsSources,
      recentEarnings,
      recentWithdrawals,
      changeChartPeriod,
      goToWithdraw,
      goToEarningsDetail,
      goToEarningsList,
      goToWithdrawalList,
      getWithdrawalStatusText
    }
  }
}
</script>

<style scoped>
.earnings-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.earnings-overview {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  margin: 20rpx 30rpx;
  padding: 40rpx;
  border-radius: 20rpx;
  color: #FFFFFF;
}

.overview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.overview-title {
  font-size: 36rpx;
  font-weight: 700;
}

.overview-actions {
  display: flex;
  gap: 30rpx;
}

.action-btn {
  font-size: 26rpx;
  padding: 12rpx 24rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
}

.balance-section {
  text-align: center;
  margin-bottom: 40rpx;
}

.balance-label {
  font-size: 24rpx;
  opacity: 0.8;
  margin-bottom: 12rpx;
  display: block;
}

.balance-amount {
  font-size: 64rpx;
  font-weight: 700;
}

.stats-row {
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

.earnings-chart {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.chart-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.chart-tabs {
  display: flex;
  gap: 20rpx;
}

.chart-tab {
  font-size: 24rpx;
  color: #666666;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
}

.chart-tab.active {
  background: #FF6B6B;
  color: #FFFFFF;
}

.chart-placeholder {
  height: 300rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #F8F9FA;
  border-radius: 12rpx;
}

.chart-placeholder image {
  width: 120rpx;
  height: 120rpx;
  margin-bottom: 20rpx;
}

.chart-desc {
  color: #999999;
  font-size: 24rpx;
}

.earnings-sources,
.recent-earnings,
.withdrawal-history {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
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
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.more-btn {
  color: #FF6B6B;
  font-size: 24rpx;
}

.sources-list {
  display: flex;
  flex-direction: column;
}

.source-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.source-item:last-child {
  border-bottom: none;
}

.source-info {
  display: flex;
  align-items: center;
}

.source-icon {
  width: 40rpx;
  height: 40rpx;
  margin-right: 16rpx;
}

.source-name {
  font-size: 28rpx;
  color: #333333;
}

.source-amount {
  text-align: right;
}

.amount {
  font-size: 28rpx;
  font-weight: 600;
  color: #FF6B6B;
  margin-bottom: 4rpx;
  display: block;
}

.percentage {
  font-size: 22rpx;
  color: #999999;
}

.earnings-list,
.withdrawal-list {
  display: flex;
  flex-direction: column;
}

.earnings-item,
.withdrawal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.earnings-item:last-child,
.withdrawal-item:last-child {
  border-bottom: none;
}

.earning-info,
.withdrawal-info {
  flex: 1;
}

.earning-title,
.withdrawal-amount {
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 8rpx;
  display: block;
}

.earning-time,
.withdrawal-time {
  font-size: 22rpx;
  color: #999999;
}

.earning-amount {
  font-size: 28rpx;
  font-weight: 600;
}

.earning-amount.income {
  color: #28A745;
}

.earning-amount.expense {
  color: #DC3545;
}

.withdrawal-status {
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.withdrawal-status.completed {
  background: #E8F5E8;
  color: #28A745;
}

.withdrawal-status.processing {
  background: #FFF3CD;
  color: #FFC107;
}

.withdrawal-status.failed {
  background: #F8D7DA;
  color: #DC3545;
}
</style>
