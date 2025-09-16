<template>
  <div class="dashboard-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">数据仪表盘</h1>
      <div class="page-actions">
        <el-button @click="handleRefreshCache">
          <el-icon><Refresh /></el-icon>
          刷新缓存
        </el-button>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <el-row :gutter="24" class="overview-cards">
      <el-col :span="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-value">{{ overview.newUsers || 0 }}</div>
            <div class="metric-label">新增用户</div>
            <div class="metric-trend">
              <el-icon color="#52C41A"><TrendCharts /></el-icon>
              <span class="trend-text">+12.5%</span>
            </div>
          </div>
          <div class="metric-icon">
            <el-icon size="48" color="#1890FF"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-value">{{ overview.activeUsers || 0 }}</div>
            <div class="metric-label">活跃用户</div>
            <div class="metric-trend">
              <el-icon color="#52C41A"><TrendCharts /></el-icon>
              <span class="trend-text">+8.3%</span>
            </div>
          </div>
          <div class="metric-icon">
            <el-icon size="48" color="#52C41A"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-value">{{ overview.completedTasks || 0 }}</div>
            <div class="metric-label">完成任务</div>
            <div class="metric-trend">
              <el-icon color="#52C41A"><TrendCharts /></el-icon>
              <span class="trend-text">+15.7%</span>
            </div>
          </div>
          <div class="metric-icon">
            <el-icon size="48" color="#FA8C16"><Trophy /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="metric-card" shadow="hover">
          <div class="metric-content">
            <div class="metric-value">¥{{ ((overview.gmv || 0) / 100).toFixed(0) }}</div>
            <div class="metric-label">GMV</div>
            <div class="metric-trend">
              <el-icon color="#52C41A"><TrendCharts /></el-icon>
              <span class="trend-text">+22.1%</span>
            </div>
          </div>
          <div class="metric-icon">
            <el-icon size="48" color="#FF4D4F"><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24" class="chart-section">
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户增长趋势</span>
              <el-button type="text" @click="refreshUserGrowthChart">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="userGrowthChartOption" 
              :loading="chartLoading.userGrowth"
            />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>任务完成趋势</span>
              <el-button type="text" @click="refreshTaskCompletionChart">
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="taskCompletionChartOption" 
              :loading="chartLoading.taskCompletion"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="24" class="chart-section">
      <el-col :span="8">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>用户等级分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="userLevelChartOption" 
              :loading="chartLoading.userLevel"
            />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>任务类型统计</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="taskTypeChartOption" 
              :loading="chartLoading.taskType"
            />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>提现趋势</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="withdrawTrendChartOption" 
              :loading="chartLoading.withdrawTrend"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 排行榜区域 -->
    <el-row :gutter="24" class="ranking-section">
      <el-col :span="12">
        <el-card class="ranking-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>热门任务排行</span>
            </div>
          </template>
          <div class="ranking-list">
            <div 
              v-for="(task, index) in topTasks" 
              :key="task.id"
              class="ranking-item"
            >
              <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
              <div class="rank-content">
                <div class="rank-title">{{ task.title }}</div>
                <div class="rank-meta">完成次数: {{ task.completedCount }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="ranking-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>活跃用户排行</span>
            </div>
          </template>
          <div class="ranking-list">
            <div 
              v-for="(user, index) in topUsers" 
              :key="user.id"
              class="ranking-item"
            >
              <div class="rank-number" :class="`rank-${index + 1}`">{{ index + 1 }}</div>
              <div class="rank-content">
                <div class="rank-title">{{ user.nickname }}</div>
                <div class="rank-meta">收益: ¥{{ (user.earnings / 100).toFixed(2) }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Refresh, User, UserFilled, Trophy, Money, TrendCharts 
} from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent, 
  GridComponent 
} from 'echarts/components'
import VChart from 'vue-echarts'
import { dashboardApi } from '@/api/dashboard'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 响应式数据
const overview = ref({})
const topTasks = ref([])
const topUsers = ref([])
const refreshTimer = ref(null)

// 图表加载状态
const chartLoading = reactive({
  userGrowth: false,
  taskCompletion: false,
  userLevel: false,
  taskType: false,
  withdrawTrend: false
})

// 图表配置
const userGrowthChartOption = ref({
  title: { text: '' },
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    name: '新增用户',
    type: 'line',
    data: [],
    smooth: true,
    itemStyle: { color: '#1890FF' }
  }]
})

const taskCompletionChartOption = ref({
  title: { text: '' },
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    name: '完成任务',
    type: 'bar',
    data: [],
    itemStyle: { color: '#52C41A' }
  }]
})

const userLevelChartOption = ref({
  tooltip: { trigger: 'item' },
  series: [{
    name: '用户等级',
    type: 'pie',
    radius: '60%',
    data: [],
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
})

const taskTypeChartOption = ref({
  tooltip: { trigger: 'item' },
  series: [{
    name: '任务类型',
    type: 'pie',
    radius: '60%',
    data: [],
    emphasis: {
      itemStyle: {
        shadowBlur: 10,
        shadowOffsetX: 0,
        shadowColor: 'rgba(0, 0, 0, 0.5)'
      }
    }
  }]
})

const withdrawTrendChartOption = ref({
  tooltip: { trigger: 'axis' },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { type: 'value' },
  series: [{
    name: '提现金额',
    type: 'line',
    data: [],
    smooth: true,
    itemStyle: { color: '#FA8C16' }
  }]
})

// 加载概览数据
const loadOverview = async () => {
  try {
    // TODO: Redis缓存 - 键名: bk.dashboard:overview, TTL: 5分钟
    const response = await dashboardApi.getOverview()
    if (response.code === 200) {
      overview.value = response.data
    }
  } catch (error) {
    console.error('加载概览数据失败:', error)
  }
}

// 加载用户增长图表
const loadUserGrowthChart = async () => {
  try {
    chartLoading.userGrowth = true
    // TODO: Redis缓存 - 键名: bk.dashboard:chart:user-growth:30, TTL: 10分钟
    // TODO: 图表数据降采样 - 大时间跨度时按天/周/月聚合
    const response = await dashboardApi.getUserGrowthChart(30)
    if (response.code === 200) {
      const data = response.data
      userGrowthChartOption.value.xAxis.data = data.xAxis || []
      userGrowthChartOption.value.series[0].data = data.series?.[0]?.data || []
    }
  } catch (error) {
    console.error('加载用户增长图表失败:', error)
  } finally {
    chartLoading.userGrowth = false
  }
}

// 加载任务完成图表
const loadTaskCompletionChart = async () => {
  try {
    chartLoading.taskCompletion = true
    const response = await dashboardApi.getTaskCompletionChart(30)
    if (response.code === 200) {
      const data = response.data
      taskCompletionChartOption.value.xAxis.data = data.xAxis || []
      taskCompletionChartOption.value.series[0].data = data.series?.[0]?.data || []
    }
  } catch (error) {
    console.error('加载任务完成图表失败:', error)
  } finally {
    chartLoading.taskCompletion = false
  }
}

// 加载用户等级分布
const loadUserLevelChart = async () => {
  try {
    chartLoading.userLevel = true
    const response = await dashboardApi.getUserLevelDistribution()
    if (response.code === 200) {
      userLevelChartOption.value.series[0].data = response.data.series?.[0]?.data || []
    }
  } catch (error) {
    console.error('加载用户等级分布失败:', error)
  } finally {
    chartLoading.userLevel = false
  }
}

// 加载任务类型统计
const loadTaskTypeChart = async () => {
  try {
    chartLoading.taskType = true
    const response = await dashboardApi.getTaskTypeStats()
    if (response.code === 200) {
      taskTypeChartOption.value.series[0].data = response.data.series?.[0]?.data || []
    }
  } catch (error) {
    console.error('加载任务类型统计失败:', error)
  } finally {
    chartLoading.taskType = false
  }
}

// 加载提现趋势
const loadWithdrawTrendChart = async () => {
  try {
    chartLoading.withdrawTrend = true
    const response = await dashboardApi.getWithdrawTrend(30)
    if (response.code === 200) {
      const data = response.data
      withdrawTrendChartOption.value.xAxis.data = data.xAxis || []
      withdrawTrendChartOption.value.series[0].data = data.series?.[0]?.data || []
    }
  } catch (error) {
    console.error('加载提现趋势失败:', error)
  } finally {
    chartLoading.withdrawTrend = false
  }
}

// 加载排行榜数据
const loadRankingData = async () => {
  try {
    // 热门任务排行
    const tasksResponse = await dashboardApi.getTopTasks(10)
    if (tasksResponse.code === 200) {
      topTasks.value = tasksResponse.data.list || []
    }
    
    // 活跃用户排行
    const usersResponse = await dashboardApi.getTopUsers(10, 'earnings')
    if (usersResponse.code === 200) {
      topUsers.value = usersResponse.data.list || []
    }
  } catch (error) {
    console.error('加载排行榜数据失败:', error)
  }
}

// 刷新所有数据
const refreshAllData = async () => {
  await Promise.all([
    loadOverview(),
    loadUserGrowthChart(),
    loadTaskCompletionChart(),
    loadUserLevelChart(),
    loadTaskTypeChart(),
    loadWithdrawTrendChart(),
    loadRankingData()
  ])
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await dashboardApi.refreshCache()
    ElMessage.success('缓存刷新成功')
    refreshAllData()
  } catch (error) {
    console.error('刷新缓存失败:', error)
    ElMessage.error('刷新缓存失败')
  }
}

// 单独刷新图表
const refreshUserGrowthChart = () => loadUserGrowthChart()
const refreshTaskCompletionChart = () => loadTaskCompletionChart()

// 设置定时刷新
const setupAutoRefresh = () => {
  // 每5分钟自动刷新概览数据
  refreshTimer.value = setInterval(() => {
    loadOverview()
  }, 5 * 60 * 1000)
}

// 清除定时器
const clearAutoRefresh = () => {
  if (refreshTimer.value) {
    clearInterval(refreshTimer.value)
    refreshTimer.value = null
  }
}

// 生命周期
onMounted(() => {
  refreshAllData()
  setupAutoRefresh()
})

onUnmounted(() => {
  clearAutoRefresh()
})
</script>

<style lang="scss" scoped>
.dashboard-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-lg;

    .page-title {
      font-size: $font-size-h2;
      font-weight: $font-weight-semibold;
      color: $text-color-primary;
      margin: 0;
    }
  }

  .overview-cards {
    margin-bottom: $spacing-xl;

    .metric-card {
      :deep(.el-card__body) {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-lg;
      }

      .metric-content {
        .metric-value {
          font-size: 36px;
          font-weight: $font-weight-bold;
          color: $text-color-primary;
          line-height: 1;
        }

        .metric-label {
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin: $spacing-xs 0;
        }

        .metric-trend {
          display: flex;
          align-items: center;
          gap: $spacing-xs;

          .trend-text {
            font-size: $font-size-sm;
            color: $success-color;
            font-weight: $font-weight-medium;
          }
        }
      }

      .metric-icon {
        opacity: 0.8;
      }
    }
  }

  .chart-section {
    margin-bottom: $spacing-xl;

    .chart-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: $font-size-h4;
        font-weight: $font-weight-medium;
        color: $text-color-primary;
      }

      .chart-container {
        height: 300px;
        
        .chart {
          height: 100%;
          width: 100%;
        }
      }
    }
  }

  .ranking-section {
    .ranking-card {
      .ranking-list {
        .ranking-item {
          display: flex;
          align-items: center;
          padding: $spacing-md 0;
          border-bottom: 1px solid $border-color-split;

          &:last-child {
            border-bottom: none;
          }

          .rank-number {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: $font-weight-bold;
            color: #ffffff;
            margin-right: $spacing-md;

            &.rank-1 { background-color: #FFD700; }
            &.rank-2 { background-color: #C0C0C0; }
            &.rank-3 { background-color: #CD7F32; }
            &:not(.rank-1):not(.rank-2):not(.rank-3) { 
              background-color: $text-color-tertiary; 
            }
          }

          .rank-content {
            flex: 1;

            .rank-title {
              font-size: $font-size-base;
              color: $text-color-primary;
              font-weight: $font-weight-medium;
            }

            .rank-meta {
              font-size: $font-size-sm;
              color: $text-color-secondary;
              margin-top: $spacing-xs;
            }
          }
        }
      }
    }
  }
}
</style>
