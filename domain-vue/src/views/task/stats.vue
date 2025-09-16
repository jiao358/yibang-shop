<template>
  <div class="task-stats-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">任务统计</h1>
      <div class="page-actions">
        <el-button @click="handleRefresh">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ stats.totalTasks || 0 }}</div>
            <div class="stat-label">总任务数</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#1890FF"><Calendar /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ stats.byStatus?.active || 0 }}</div>
            <div class="stat-label">活跃任务</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#52C41A"><Check /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ stats.byStatus?.completed || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#FA8C16"><Trophy /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ stats.byStatus?.inactive || 0 }}</div>
            <div class="stat-label">非活跃</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#8C8C8C"><Close /></el-icon>
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
              <span>任务类型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="taskTypeChartOption" 
              :loading="chartLoading"
            />
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>任务状态分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="taskStatusChartOption" 
              :loading="chartLoading"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 时间筛选 -->
    <el-card class="filter-card" shadow="never">
      <el-form inline>
        <el-form-item label="统计时间范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Calendar, Check, Trophy, Close, Search } from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import VChart from 'vue-echarts'
import { taskApi } from '@/api/task'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
])

// 响应式数据
const chartLoading = ref(false)
const dateRange = ref([])

// 统计数据
const stats = ref({
  totalTasks: 0,
  byStatus: {},
  byType: {}
})

// 图表配置
const taskTypeChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '任务类型',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

const taskStatusChartOption = ref({
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 'left'
  },
  series: [
    {
      name: '任务状态',
      type: 'pie',
      radius: '50%',
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
})

// 加载统计数据
const loadStats = async () => {
  try {
    chartLoading.value = true
    
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    // TODO: Redis缓存 - 键名: bk.tasks:stats:{type}:{startDate}:{endDate}, TTL: 30分钟
    const response = await taskApi.getTaskStats(params)
    
    if (response.code === 200) {
      stats.value = response.data
      
      // 更新图表数据
      updateChartData()
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
    ElMessage.error('加载统计数据失败')
  } finally {
    chartLoading.value = false
  }
}

// 更新图表数据
const updateChartData = () => {
  // 任务类型分布
  const typeData = Object.entries(stats.value.byType || {}).map(([key, value]) => ({
    name: getTypeLabel(key),
    value
  }))
  taskTypeChartOption.value.series[0].data = typeData
  
  // 任务状态分布
  const statusData = Object.entries(stats.value.byStatus || {}).map(([key, value]) => ({
    name: getStatusLabel(key),
    value
  }))
  taskStatusChartOption.value.series[0].data = statusData
}

// 日期范围变化
const handleDateChange = () => {
  // 自动查询
  handleQuery()
}

// 查询
const handleQuery = () => {
  loadStats()
}

// 刷新
const handleRefresh = () => {
  loadStats()
}

// 工具函数
const getTypeLabel = (type) => {
  const typeMap = {
    ad: '广告任务',
    video: '视频任务',
    app_install: '应用安装',
    survey: '问卷调查',
    share: '分享任务'
  }
  return typeMap[type] || type
}

const getStatusLabel = (status) => {
  const statusMap = {
    active: '活跃',
    inactive: '非活跃',
    completed: '已完成'
  }
  return statusMap[status] || status
}

// 生命周期
onMounted(() => {
  loadStats()
})
</script>

<style lang="scss" scoped>
.task-stats-page {
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

  .stats-cards {
    margin-bottom: $spacing-lg;

    .stat-card {
      :deep(.el-card__body) {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-lg;
      }

      .stat-content {
        .stat-value {
          font-size: 32px;
          font-weight: $font-weight-bold;
          color: $text-color-primary;
          line-height: 1;
        }

        .stat-label {
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-top: $spacing-xs;
        }
      }

      .stat-icon {
        opacity: 0.8;
      }
    }
  }

  .chart-section {
    margin-bottom: $spacing-lg;

    .chart-card {
      .card-header {
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

  .filter-card {
    :deep(.el-card__body) {
      padding: $spacing-lg;
    }
  }
}
</style>
