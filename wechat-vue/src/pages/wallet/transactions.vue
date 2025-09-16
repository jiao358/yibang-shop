<template>
  <view class="wallet-page">
    <!-- 顶部筛选：周期与类型 -->
    <view class="filters">
      <picker mode="date" fields="month" @change="onMonthChange">
        <view class="picker-item">{{ monthLabel }}</view>
      </picker>
      <picker mode="date" @change="onWeekBaseChange">
        <view class="picker-item">{{ weekLabel }}</view>
      </picker>
      <view class="type-switch">
        <view :class="['type-btn', {active: currentType==='all'}]" @click="setType('all')">全部</view>
        <view :class="['type-btn', {active: currentType==='commission'}]" @click="setType('commission')">仅收入</view>
        <view :class="['type-btn', {active: currentType==='withdrawal'}]" @click="setType('withdrawal')">仅提现</view>
      </view>
    </view>

    <!-- 汇总 -->
    <view class="summary">
      <view class="sum-item">
        <text class="label">本期收入</text>
        <text class="value red">+{{ formatAmount(summary.totalIn) }}</text>
      </view>
      <view class="sum-item">
        <text class="label">本期提现</text>
        <text class="value green">-{{ formatAmount(summary.totalOut) }}</text>
      </view>
      <view class="sum-item">
        <text class="label">本期净增</text>
        <text class="value">{{ formatSigned(summary.netChange) }}</text>
      </view>
      <view class="sum-item">
        <text class="label">当前余额</text>
        <text class="value">{{ formatAmount(summary.balance) }}</text>
      </view>
      <view class="sum-item">
        <text class="label">冻结余额</text>
        <text class="value">{{ formatAmount(summary.frozenBalance) }}</text>
      </view>
    </view>

    <!-- 流水列表 -->
    <view class="list">
      <view v-for="item in transactions" :key="item.id" class="row">
        <view class="row-main">
          <text class="title">{{ item.bizType==='commission' ? (item.taskTitle || '任务佣金') : '提现' }}</text>
          <text class="time">{{ item.occurTime }}</text>
        </view>
        <view class="row-side">
          <text :class="['amount', item.bizType==='commission' ? 'red' : 'green']">
            {{ item.bizType==='commission' ? ('+'+formatAmount(item.amountInCents)) : ('-'+formatAmount(item.amountInCents)) }}
          </text>
          <text class="balance">余额 {{ formatAmount(item.balanceAfterInCents) }}</text>
        </view>
      </view>

      <view class="load-more" v-if="hasMore && !loading" @click="loadMore">加载更多</view>
      <view class="loading" v-if="loading">加载中...</view>
      <view class="empty" v-if="!loading && transactions.length===0">暂无数据</view>
    </view>
  </view>
  
</template>

<script>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useWalletStore } from '@/stores/wallet'

export default {
  name: 'WalletTransactionsPage',
  setup() {
    const walletStore = useWalletStore()
    const loading = ref(false)
    const page = ref(1)
    const size = ref(10)
    const hasMore = ref(true)
    const currentType = ref('all') // commission | withdrawal | all
    const periodType = ref('month') // month | week
    const periodValue = ref('')
    const monthLabel = ref('选择月份')
    const weekLabel = ref('选择自然周')

    const transactions = ref([])
    const summary = ref({ totalIn:0, totalOut:0, netChange:0, balance:0, frozenBalance:0 })

    const fetchSummary = async () => {
      try {
        await walletStore.getSummary({ periodType: periodType.value, periodValue: periodValue.value })
        summary.value = walletStore.summary
      } catch (e) {
        uni.showToast({ title: '获取汇总失败', icon: 'none' })
      }
    }

    const fetchList = async (reset=false) => {
      if (loading.value) return
      loading.value = true
      try {
        if (reset) { page.value = 1; hasMore.value = true; transactions.value = [] }
        const params = {
          type: currentType.value,
          page: page.value,
          size: size.value,
          periodType: periodType.value,
          periodValue: periodValue.value
        }
        await walletStore.getTransactions(params)
        const rows = walletStore.transactions || []
        transactions.value = reset ? rows : [...transactions.value, ...rows]
        const total = walletStore.total || 0
        hasMore.value = transactions.value.length < total
        page.value++
      } catch (e) {
        uni.showToast({ title: '获取流水失败', icon: 'none' })
      } finally {
        loading.value = false
      }
    }

    const setType = (t) => {
      currentType.value = t
      fetchList(true)
      fetchSummary()
    }

    const onMonthChange = (e) => {
      periodType.value = 'month'
      periodValue.value = e.detail.value // YYYY-MM
      monthLabel.value = e.detail.value
      fetchList(true)
      fetchSummary()
    }

    // 以选中日期推导自然周口径（周一-周日），传 YYYY-ww
    const onWeekBaseChange = (e) => {
      periodType.value = 'week'
      const date = new Date(e.detail.value)
      const weekNo = getWeekNumber(date)
      const year = date.getFullYear()
      periodValue.value = `${year}-${String(weekNo).padStart(2,'0')}`
      weekLabel.value = periodValue.value
      fetchList(true)
      fetchSummary()
    }

    const getWeekNumber = (d) => {
      const date = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()))
      const dayNum = (date.getUTCDay() || 7)
      date.setUTCDate(date.getUTCDate() + 4 - dayNum)
      const yearStart = new Date(Date.UTC(date.getUTCFullYear(),0,1))
      return Math.ceil((((date - yearStart) / 86400000) + 1)/7)
    }

    const loadMore = () => fetchList(false)

    const formatAmount = (cents) => ((cents||0)/100).toFixed(2)
    const formatSigned = (cents) => {
      const v = (cents||0)
      const s = ((Math.abs(v))/100).toFixed(2)
      return v>=0 ? `+${s}` : `-${s}`
    }

    onLoad((query) => {
      if (query && Object.keys(query).length > 0) {
        if (query.type) {
          const allowed = ['all','commission','withdrawal']
          currentType.value = allowed.includes(query.type) ? query.type : 'all'
        }
        if (query.periodType) {
          const allowedPt = ['month','week']
          periodType.value = allowedPt.includes(query.periodType) ? query.periodType : 'month'
        }
        if (query.periodValue) {
          periodValue.value = query.periodValue
          if (periodType.value === 'month') monthLabel.value = query.periodValue
          if (periodType.value === 'week') weekLabel.value = query.periodValue
        }
      } else {
        const stored = uni.getStorageSync('walletQuery')
        if (stored && stored.type) {
          const allowed = ['all','commission','withdrawal']
          currentType.value = allowed.includes(stored.type) ? stored.type : 'all'
        }
        uni.removeStorageSync('walletQuery')
      }
    })

    onMounted(async () => {
      if (!periodValue.value) {
        const today = new Date()
        monthLabel.value = `${today.getFullYear()}-${String(today.getMonth()+1).padStart(2,'0')}`
        periodType.value = 'month'
        periodValue.value = monthLabel.value
      }
      await fetchSummary()
      await fetchList(true)
    })

    return {
      loading, transactions, summary, hasMore,
      monthLabel, weekLabel, currentType,
      setType, onMonthChange, onWeekBaseChange,
      loadMore, formatAmount, formatSigned
    }
  }
}
</script>

<style scoped>
.wallet-page{background:#F5F5F5;min-height:100vh}
.filters{display:flex;gap:16rpx;padding:20rpx;background:#fff}
.picker-item{padding:12rpx 20rpx;background:#F5F5F5;border-radius:12rpx;color:#666}
.type-switch{display:flex;gap:12rpx;margin-left:auto}
.type-btn{padding:12rpx 20rpx;background:#F5F5F5;border-radius:12rpx;color:#666}
.type-btn.active{background:#FF6B6B;color:#fff}
.summary{display:grid;grid-template-columns:repeat(2,1fr);gap:16rpx;padding:20rpx;background:#fff;margin-top:12rpx}
.sum-item{display:flex;justify-content:space-between;color:#666}
.sum-item .value{font-weight:600;color:#333}
.red{color:#E53935}
.green{color:#2E7D32}
.list{margin-top:12rpx;background:#fff}
.row{display:flex;justify-content:space-between;align-items:center;padding:20rpx;border-bottom:1rpx solid #F0F0F0}
.row-main{display:flex;flex-direction:column}
.title{font-size:28rpx;color:#333;font-weight:600}
.time{font-size:22rpx;color:#999;margin-top:6rpx}
.row-side{display:flex;flex-direction:column;align-items:flex-end}
.amount{font-size:28rpx;font-weight:700}
.balance{font-size:22rpx;color:#999;margin-top:6rpx}
.load-more,.loading,.empty{padding:24rpx;text-align:center;color:#666}
</style>


