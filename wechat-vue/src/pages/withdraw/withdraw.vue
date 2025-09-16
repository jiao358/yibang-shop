<template>
  <view class="withdraw-page">
    <!-- 提现说明 -->
    <view class="withdraw-notice">
      <view class="notice-header">
        <image src="/static/icons/info.png" mode="aspectFit" class="notice-icon"></image>
        <text class="notice-title">提现说明</text>
      </view>
      <text class="notice-content">提现申请将在1-3个工作日内处理，请确保微信账号信息正确</text>
    </view>

    <!-- 账户余额 -->
    <view class="balance-card">
      <view class="balance-header">
        <text class="balance-label">可提现余额</text>
        <text class="balance-amount">¥{{ userEarnings.balance || '0.00' }}</text>
      </view>
      <view class="balance-details">
        <view class="detail-item">
          <text class="detail-label">累计收益</text>
          <text class="detail-value">¥{{ userEarnings.totalEarnings || '0.00' }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">已提现</text>
          <text class="detail-value">¥{{ userEarnings.withdrawnAmount || '0.00' }}</text>
        </view>
        <view class="detail-item">
          <text class="detail-label">冻结金额</text>
          <text class="detail-value">¥{{ userEarnings.frozenAmount || '0.00' }}</text>
        </view>
      </view>
    </view>

    <!-- 提现金额输入 -->
    <view class="withdraw-form">
      <view class="form-section">
        <text class="form-label">提现金额</text>
        <view class="amount-input">
          <text class="currency-symbol">¥</text>
          <input 
            type="digit" 
            v-model="withdrawAmount" 
            placeholder="请输入提现金额"
            @input="onAmountInput"
            class="amount-field"
          />
        </view>
        <view class="amount-tips">
          <text class="tip-text">最低提现金额：¥{{ minWithdrawAmount }}</text>
          <text class="tip-text">手续费：{{ withdrawFeeRate }}%</text>
        </view>
      </view>

      <!-- 快捷金额选择 -->
      <view class="quick-amounts">
        <text class="quick-label">快捷选择</text>
        <view class="amount-buttons">
          <button 
            class="amount-btn" 
            v-for="amount in quickAmounts" 
            :key="amount"
            :class="{ active: withdrawAmount == amount }"
            @click="selectAmount(amount)"
          >
            ¥{{ amount }}
          </button>
        </view>
      </view>

      <!-- 全部提现 -->
      <view class="withdraw-all">
        <button class="withdraw-all-btn" @click="withdrawAll">
          全部提现 (¥{{ maxWithdrawAmount }})
        </button>
      </view>
    </view>

    <!-- 提现信息 -->
    <view class="withdraw-info">
      <view class="info-section">
        <text class="info-title">提现信息</text>
        <view class="info-item">
          <text class="info-label">提现金额</text>
          <text class="info-value">¥{{ withdrawAmount || '0.00' }}</text>
        </view>
        <view class="info-item">
          <text class="info-label">手续费</text>
          <text class="info-value">¥{{ calculateFee() }}</text>
        </view>
        <view class="info-item total">
          <text class="info-label">实际到账</text>
          <text class="info-value">¥{{ calculateActualAmount() }}</text>
        </view>
      </view>
    </view>

    <!-- 微信账号信息 -->
    <view class="wechat-info">
      <view class="info-header">
        <text class="info-title">微信账号</text>
        <text class="change-btn" @click="changeWechatAccount">更换</text>
      </view>
      <view class="wechat-account">
        <image :src="wechatAccount.avatar" mode="aspectFill" class="wechat-avatar"></image>
        <view class="wechat-details">
          <text class="wechat-nickname">{{ wechatAccount.nickname }}</text>
          <text class="wechat-id">微信号：{{ wechatAccount.wechatId }}</text>
        </view>
        <image src="/static/icons/check.png" mode="aspectFit" class="check-icon"></image>
      </view>
    </view>

    <!-- 提现记录 -->
    <view class="withdraw-history">
      <view class="history-header">
        <text class="history-title">最近提现</text>
        <button class="more-btn" @click="goToWithdrawHistory">更多</button>
      </view>
      <view class="history-list">
        <view class="history-item" v-for="record in recentWithdrawals" :key="record.id">
          <view class="record-info">
            <text class="record-amount">¥{{ record.amount }}</text>
            <text class="record-time">{{ record.time }}</text>
          </view>
          <view class="record-status" :class="record.status">
            <text>{{ getStatusText(record.status) }}</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 提现按钮 -->
    <view class="withdraw-actions">
      <button 
        class="withdraw-btn" 
        :class="{ disabled: !canWithdraw }"
        :disabled="!canWithdraw"
        @click="submitWithdraw"
      >
        {{ getWithdrawBtnText() }}
      </button>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useEarningsStore } from '@/stores/earnings'
import { useUserStore } from '@/stores/user'
import { withdrawApi } from '@/api/withdraw'
import { userApi } from '@/api/user'

export default {
  name: 'WithdrawPage',
  setup() {
    const earningsStore = useEarningsStore()
    const userStore = useUserStore()

    // 响应式数据
    const withdrawAmount = ref('')
    const userEarnings = ref({})
    const wechatAccount = ref({})
    const recentWithdrawals = ref([])
    const minWithdrawAmount = ref(10)
    const maxWithdrawAmount = ref(0)
    const withdrawFeeRate = ref(0.6)
    const quickAmounts = ref([10, 50, 100, 200, 500])
    const loading = ref(false)
    const withdrawConfig = ref({})

    // 计算属性
    const canWithdraw = computed(() => {
      const amount = parseFloat(withdrawAmount.value) || 0
      return amount >= minWithdrawAmount.value && amount <= maxWithdrawAmount.value
    })

    // 方法
    const loadWithdrawData = async () => {
      try {
        loading.value = true
        
        // 加载用户收益信息
        await earningsStore.getUserEarnings()
        userEarnings.value = earningsStore.userEarnings
        
        // 计算可提现金额（余额 - 冻结金额）
        const availableBalance = (userEarnings.value.balance || 0) - (userEarnings.value.frozenAmount || 0)
        maxWithdrawAmount.value = Math.max(0, availableBalance)
        
        // 加载提现配置
        const configResponse = await withdrawApi.getWithdrawConfig()
        withdrawConfig.value = configResponse.data
        minWithdrawAmount.value = withdrawConfig.value.minAmount || 10
        withdrawFeeRate.value = withdrawConfig.value.feeRate || 0.6
        quickAmounts.value = withdrawConfig.value.quickAmounts || [10, 50, 100, 200, 500]
        
        // 加载用户微信信息
        if (userStore.isLoggedIn) {
          const userInfo = userStore.userInfo
          wechatAccount.value = {
            avatar: userInfo.avatar || '/static/images/default-avatar.png',
            nickname: userInfo.nickname || '微信用户',
            wechatId: userInfo.openid || 'wxid_未获取'
          }
        }
        
        // 加载最近提现记录
        const withdrawHistory = await withdrawApi.getRecentWithdraws(3)
        recentWithdrawals.value = (withdrawHistory.data?.records || []).map(item => ({
          id: item.id,
          amount: (item.amount / 100).toFixed(2), // 分转元
          status: item.status,
          time: item.createdAt
        }))

      } catch (error) {
        console.error('加载提现数据失败:', error)
        uni.showToast({
          title: '加载数据失败',
          icon: 'none'
        })
      } finally {
        loading.value = false
      }
    }

    const onAmountInput = (e) => {
      const value = e.detail.value
      const amount = parseFloat(value) || 0
      
      if (amount > maxWithdrawAmount.value) {
        withdrawAmount.value = maxWithdrawAmount.value.toString()
        uni.showToast({
          title: '提现金额不能超过余额',
          icon: 'none'
        })
      }
    }

    const selectAmount = (amount) => {
      if (amount <= maxWithdrawAmount.value) {
        withdrawAmount.value = amount.toString()
      } else {
        uni.showToast({
          title: '提现金额不能超过余额',
          icon: 'none'
        })
      }
    }

    const withdrawAll = () => {
      withdrawAmount.value = maxWithdrawAmount.value.toString()
    }

    const calculateFee = () => {
      const amount = parseFloat(withdrawAmount.value) || 0
      return (amount * withdrawFeeRate.value / 100).toFixed(2)
    }

    const calculateActualAmount = () => {
      const amount = parseFloat(withdrawAmount.value) || 0
      const fee = parseFloat(calculateFee())
      return (amount - fee).toFixed(2)
    }

    const changeWechatAccount = () => {
      uni.showModal({
        title: '更换微信账号',
        content: '请重新授权微信账号信息',
        success: (res) => {
          if (res.confirm) {
            // 重新获取微信账号信息
            uni.showToast({
              title: '功能开发中',
              icon: 'none'
            })
          }
        }
      })
    }

    const getStatusText = (status) => {
      const statusMap = {
        'completed': '已完成',
        'processing': '处理中',
        'failed': '失败',
        'pending': '待审核'
      }
      return statusMap[status] || '未知'
    }

    const getWithdrawBtnText = () => {
      if (!withdrawAmount.value) {
        return '请输入提现金额'
      }
      const amount = parseFloat(withdrawAmount.value) || 0
      if (amount < minWithdrawAmount.value) {
        return `最低提现金额¥${minWithdrawAmount.value}`
      }
      if (amount > maxWithdrawAmount.value) {
        return '提现金额超出余额'
      }
      return '确认提现'
    }

    const submitWithdraw = async () => {
      if (!canWithdraw.value) {
        return
      }

      // 检查用户是否登录
      if (!userStore.isLoggedIn) {
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        return
      }

      // 最终确认
      const confirmResult = await new Promise(resolve => {
        uni.showModal({
          title: '确认提现',
          content: `提现金额：¥${withdrawAmount.value}\n手续费：¥${calculateFee()}\n实际到账：¥${calculateActualAmount()}\n\n提现申请提交后需要工作人员审核，审核通过后将通过微信支付打款到您的微信账户。`,
          success: (res) => resolve(res.confirm),
          fail: () => resolve(false)
        })
      })

      if (!confirmResult) return

      try {
        uni.showLoading({ title: '提交中...' })

        const withdrawData = {
          amount: Math.round(parseFloat(withdrawAmount.value) * 100), // 转换为分
          withdrawType: 'wechat_pay',
          remark: '用户申请提现'
        }

        await withdrawApi.applyWithdraw(withdrawData)

        uni.hideLoading()
        
        uni.showModal({
          title: '提现申请已提交',
          content: `提现金额：¥${withdrawAmount.value}\n实际到账：¥${calculateActualAmount()}\n\n您的提现申请已提交成功，请耐心等待工作人员审核。审核通过后，款项将在1-3个工作日内到账。`,
          showCancel: false,
          success: () => {
            withdrawAmount.value = ''
            // 刷新数据
            loadWithdrawData()
          }
        })

      } catch (error) {
        uni.hideLoading()
        console.error('提现申请失败:', error)
        
        let errorMessage = '提现申请失败'
        if (error.response?.data?.message) {
          errorMessage = error.response.data.message
        } else if (error.message) {
          errorMessage = error.message
        }
        
        uni.showToast({
          title: errorMessage,
          icon: 'none',
          duration: 3000
        })
      }
    }

    const goToWithdrawHistory = () => {
      // 跳转到钱包明细页面的提现tab
      uni.setStorageSync('walletQuery', { type: 'withdrawal' })
      uni.navigateTo({
        url: '/pages/wallet/transactions'
      })
    }

    // 生命周期
    onMounted(() => {
      loadWithdrawData()
    })

    return {
      withdrawAmount,
      userEarnings,
      wechatAccount,
      recentWithdrawals,
      minWithdrawAmount,
      maxWithdrawAmount,
      withdrawFeeRate,
      quickAmounts,
      canWithdraw,
      onAmountInput,
      selectAmount,
      withdrawAll,
      calculateFee,
      calculateActualAmount,
      changeWechatAccount,
      getStatusText,
      getWithdrawBtnText,
      submitWithdraw,
      goToWithdrawHistory
    }
  }
}
</script>

<style scoped>
.withdraw-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.withdraw-notice {
  background: #E3F2FD;
  margin: 20rpx 30rpx;
  padding: 24rpx;
  border-radius: 12rpx;
  border-left: 6rpx solid #2196F3;
}

.notice-header {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
}

.notice-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 12rpx;
}

.notice-title {
  font-size: 28rpx;
  font-weight: 600;
  color: #1976D2;
}

.notice-content {
  font-size: 24rpx;
  color: #1976D2;
  line-height: 1.5;
}

.balance-card {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  margin: 20rpx 30rpx;
  padding: 40rpx;
  border-radius: 20rpx;
  color: #FFFFFF;
}

.balance-header {
  text-align: center;
  margin-bottom: 30rpx;
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

.balance-details {
  display: flex;
  justify-content: space-between;
}

.detail-item {
  text-align: center;
  flex: 1;
}

.detail-label {
  font-size: 22rpx;
  opacity: 0.8;
  margin-bottom: 8rpx;
  display: block;
}

.detail-value {
  font-size: 26rpx;
  font-weight: 600;
}

.withdraw-form {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.form-section {
  margin-bottom: 30rpx;
}

.form-label {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.amount-input {
  display: flex;
  align-items: center;
  border: 2rpx solid #E9ECEF;
  border-radius: 12rpx;
  padding: 0 20rpx;
  margin-bottom: 16rpx;
}

.currency-symbol {
  font-size: 36rpx;
  color: #FF6B6B;
  font-weight: 600;
  margin-right: 12rpx;
}

.amount-field {
  flex: 1;
  font-size: 36rpx;
  color: #333333;
  padding: 20rpx 0;
}

.amount-tips {
  display: flex;
  justify-content: space-between;
}

.tip-text {
  font-size: 22rpx;
  color: #999999;
}

.quick-amounts {
  margin-bottom: 30rpx;
}

.quick-label {
  font-size: 26rpx;
  color: #666666;
  margin-bottom: 16rpx;
  display: block;
}

.amount-buttons {
  display: flex;
  gap: 16rpx;
  flex-wrap: wrap;
}

.amount-btn {
  padding: 16rpx 32rpx;
  border: 2rpx solid #E9ECEF;
  border-radius: 24rpx;
  font-size: 26rpx;
  color: #666666;
  background: #FFFFFF;
}

.amount-btn.active {
  border-color: #FF6B6B;
  color: #FF6B6B;
  background: #FFF5F5;
}

.withdraw-all {
  text-align: center;
}

.withdraw-all-btn {
  background: transparent;
  color: #FF6B6B;
  font-size: 26rpx;
  padding: 16rpx 32rpx;
  border: 2rpx solid #FF6B6B;
  border-radius: 24rpx;
}

.withdraw-info {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.info-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 20rpx;
  display: block;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item.total {
  border-top: 2rpx solid #F0F0F0;
  margin-top: 16rpx;
  padding-top: 20rpx;
}

.info-label {
  font-size: 26rpx;
  color: #666666;
}

.info-value {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.info-item.total .info-value {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 700;
}

.wechat-info {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.change-btn {
  color: #FF6B6B;
  font-size: 24rpx;
}

.wechat-account {
  display: flex;
  align-items: center;
}

.wechat-avatar {
  width: 80rpx;
  height: 80rpx;
  border-radius: 40rpx;
  margin-right: 20rpx;
}

.wechat-details {
  flex: 1;
}

.wechat-nickname {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 8rpx;
  display: block;
}

.wechat-id {
  font-size: 22rpx;
  color: #999999;
}

.check-icon {
  width: 32rpx;
  height: 32rpx;
}

.withdraw-history {
  background: #FFFFFF;
  margin: 20rpx 30rpx;
  padding: 30rpx;
  border-radius: 16rpx;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.history-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.more-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  font-size: 24rpx;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  border: none;
  line-height: 1;
}

.history-list {
  display: flex;
  flex-direction: column;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.history-item:last-child {
  border-bottom: none;
}

.record-info {
  flex: 1;
}

.record-amount {
  font-size: 28rpx;
  color: #333333;
  font-weight: 600;
  margin-bottom: 8rpx;
  display: block;
}

.record-time {
  font-size: 22rpx;
  color: #999999;
}

.record-status {
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  font-size: 22rpx;
}

.record-status.completed {
  background: #E8F5E8;
  color: #28A745;
}

.record-status.processing {
  background: #FFF3CD;
  color: #FFC107;
}

.record-status.failed {
  background: #F8D7DA;
  color: #DC3545;
}

.withdraw-actions {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 20rpx 30rpx;
  border-top: 1rpx solid #F0F0F0;
}

.withdraw-btn {
  width: 100%;
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 24rpx;
  border-radius: 24rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
}

.withdraw-btn.disabled {
  background: #E9ECEF;
  color: #6C757D;
}

.withdraw-btn:disabled {
  background: #E9ECEF;
  color: #6C757D;
}
</style>
