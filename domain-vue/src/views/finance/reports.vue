<template>
  <div class="finance-reports-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">财务报表</h1>
      <div class="page-actions">
        <el-button @click="handleExportAll">
          <el-icon><Download /></el-icon>
          导出报表
        </el-button>
      </div>
    </div>

    <!-- 财务概览 -->
    <el-row :gutter="24" class="overview-cards">
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <div class="overview-value text-success">¥{{ formatAmount(overview.totalIncome) }}</div>
            <div class="overview-label">总收入</div>
          </div>
          <div class="overview-icon">
            <el-icon size="40" color="#52C41A"><TrendCharts /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <div class="overview-value text-error">¥{{ formatAmount(overview.totalExpense) }}</div>
            <div class="overview-label">总支出</div>
          </div>
          <div class="overview-icon">
            <el-icon size="40" color="#FF4D4F"><Money /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <div class="overview-value text-primary">¥{{ formatAmount(overview.netProfit) }}</div>
            <div class="overview-label">净利润</div>
          </div>
          <div class="overview-icon">
            <el-icon size="40" color="#1890FF"><Wallet /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="overview-card" shadow="hover">
          <div class="overview-content">
            <div class="overview-value">{{ ((overview.profitMargin || 0) * 100).toFixed(1) }}%</div>
            <div class="overview-label">利润率</div>
          </div>
          <div class="overview-icon">
            <el-icon size="40" color="#FA8C16"><DataAnalysis /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryForm" inline class="filter-form">
        <el-form-item label="报表类型">
          <el-select v-model="queryForm.type" placeholder="请选择" style="width: 150px">
            <el-option label="收支明细" value="detail" />
            <el-option label="收入汇总" value="income" />
            <el-option label="支出汇总" value="expense" />
            <el-option label="利润分析" value="profit" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="统计周期">
          <el-select v-model="queryForm.period" placeholder="请选择" style="width: 150px">
            <el-option label="按日" value="daily" />
            <el-option label="按周" value="weekly" />
            <el-option label="按月" value="monthly" />
            <el-option label="按年" value="yearly" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="时间范围">
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
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 图表展示 -->
    <el-row :gutter="24" class="chart-section">
      <el-col :span="24">
        <el-card class="chart-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>收支趋势图</span>
              <div class="chart-controls">
                <el-radio-group v-model="chartType" size="small" @change="handleChartTypeChange">
                  <el-radio-button label="income">收入</el-radio-button>
                  <el-radio-button label="expense">支出</el-radio-button>
                  <el-radio-button label="profit">利润</el-radio-button>
                </el-radio-group>
              </div>
            </div>
          </template>
          <div class="chart-container">
            <v-chart 
              class="chart" 
              :option="trendChartOption" 
              :loading="chartLoading"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>收支明细</span>
          <el-button type="text" @click="handleExportTable">
            <el-icon><Download /></el-icon>
            导出当前数据
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="tableLoading"
        :data="tableData"
        class="data-table"
        max-height="400"
      >
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'income' ? 'success' : 'danger'">
              {{ row.type === 'income' ? '收入' : '支出' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="row.type === 'income' ? 'text-success' : 'text-error'">
              {{ row.type === 'income' ? '+' : '-' }}¥{{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="记录时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Download, Search, Refresh, TrendCharts, Money, Wallet, DataAnalysis 
} from '@element-plus/icons-vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart } from 'echarts/charts'
import { 
  TitleComponent, 
  TooltipComponent, 
  LegendComponent, 
  GridComponent 
} from 'echarts/components'
import VChart from 'vue-echarts'
import { financeApi } from '@/api/finance'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 响应式数据
const tableLoading = ref(false)
const chartLoading = ref(false)
const tableData = ref([])
const dateRange = ref([])
const chartType = ref('income')

// 查询表单
const queryForm = reactive({
  type: 'detail',
  period: 'daily',
  startDate: '',
  endDate: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 20,
  total: 0
})

// 概览数据
const overview = ref({
  totalIncome: 0,
  totalExpense: 0,
  netProfit: 0,
  profitMargin: 0
})

// 图表配置
const trendChartOption = ref({
  tooltip: { 
    trigger: 'axis',
    formatter: function(params) {
      let result = params[0].name + '<br/>'
      params.forEach(param => {
        result += `${param.seriesName}: ¥${(param.value / 100).toFixed(2)}<br/>`
      })
      return result
    }
  },
  legend: { data: [] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: [] },
  yAxis: { 
    type: 'value',
    axisLabel: {
      formatter: function(value) {
        return '¥' + (value / 100).toFixed(0)
      }
    }
  },
  series: []
})

// 加载财务概览
const loadOverview = async () => {
  try {
    const params = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    // TODO: Redis缓存 - 键名: bk.finance:overview:{startDate}:{endDate}, TTL: 15分钟
    const response = await financeApi.getFinanceOverview(params)
    if (response.code === 200) {
      overview.value = response.data
    }
  } catch (error) {
    console.error('加载财务概览失败:', error)
  }
}

// 加载收支明细
const loadIncomeExpenseList = async () => {
  try {
    tableLoading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    // TODO: 分页优化 - 大数据量报表查询使用只读库，分库分表策略
    // TODO: Redis缓存 - 键名: bk.finance:list:{参数哈希}:{page}:{size}, TTL: 10分钟
    const response = await financeApi.getIncomeExpenseList(params)
    
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载收支明细失败:', error)
    ElMessage.error('加载收支明细失败')
  } finally {
    tableLoading.value = false
  }
}

// 加载趋势图表
const loadTrendChart = async () => {
  try {
    chartLoading.value = true
    
    const params = {
      period: queryForm.period
    }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    
    let response
    // TODO: 图表缓存分层 - 不同时间粒度使用不同TTL
    switch (chartType.value) {
      case 'income':
        response = await financeApi.getIncomeTrend(params)
        break
      case 'expense':
        response = await financeApi.getExpenseAnalysis(params)
        break
      case 'profit':
        response = await financeApi.getProfitAnalysis(params)
        break
    }
    
    if (response?.code === 200) {
      const data = response.data
      trendChartOption.value.xAxis.data = data.xAxis || []
      trendChartOption.value.series = data.series || []
      trendChartOption.value.legend.data = data.legend || []
    }
  } catch (error) {
    console.error('加载趋势图表失败:', error)
  } finally {
    chartLoading.value = false
  }
}

// 查询
const handleQuery = () => {
  pagination.page = 1
  loadIncomeExpenseList()
  loadTrendChart()
  loadOverview()
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    type: 'detail',
    period: 'daily',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  chartType.value = 'income'
  pagination.page = 1
  handleQuery()
}

// 日期范围变化
const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startDate = dates[0]
    queryForm.endDate = dates[1]
  } else {
    queryForm.startDate = ''
    queryForm.endDate = ''
  }
}

// 图表类型变化
const handleChartTypeChange = () => {
  loadTrendChart()
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadIncomeExpenseList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadIncomeExpenseList()
}

// 导出全部报表
const handleExportAll = async () => {
  try {
    // TODO: 导出异步任务 - 大数据量导出使用异步任务+进度查询
    const params = {
      ...queryForm,
      exportAll: true
    }
    
    const response = await financeApi.exportIncomeExpense(params)
    
    // 处理文件下载
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `财务报表_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 导出当前表格数据
const handleExportTable = async () => {
  try {
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await financeApi.exportIncomeExpense(params)
    
    // 处理文件下载
    const blob = new Blob([response], { 
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
    })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `收支明细_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 工具函数
const formatAmount = (amount) => {
  if (!amount) return '0.00'
  return (amount / 100).toFixed(2)
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  // 默认查询最近30天数据
  const endDate = new Date()
  const startDate = new Date()
  startDate.setDate(startDate.getDate() - 30)
  
  dateRange.value = [
    startDate.toISOString().slice(0, 10),
    endDate.toISOString().slice(0, 10)
  ]
  
  handleDateChange(dateRange.value)
  handleQuery()
})
</script>

<style lang="scss" scoped>
.finance-reports-page {
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

    .overview-card {
      :deep(.el-card__body) {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-lg;
      }

      .overview-content {
        .overview-value {
          font-size: 32px;
          font-weight: $font-weight-bold;
          line-height: 1;
        }

        .overview-label {
          font-size: $font-size-base;
          color: $text-color-secondary;
          margin-top: $spacing-xs;
        }
      }

      .overview-icon {
        opacity: 0.8;
      }
    }
  }

  .filter-card {
    margin-bottom: $spacing-lg;

    :deep(.el-card__body) {
      padding: $spacing-lg;
    }
  }

  .chart-section {
    margin-bottom: $spacing-lg;

    .chart-card {
      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: $font-size-h4;
        font-weight: $font-weight-medium;
        color: $text-color-primary;

        .chart-controls {
          :deep(.el-radio-group) {
            .el-radio-button__inner {
              padding: 8px 12px;
            }
          }
        }
      }

      .chart-container {
        height: 400px;
        
        .chart {
          height: 100%;
          width: 100%;
        }
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: $font-size-h4;
      font-weight: $font-weight-medium;
      color: $text-color-primary;
    }

    .data-table {
      // TODO: 大数据量虚拟滚动 - 当数据量超过1000条时使用虚拟滚动
      :deep(.el-table__header) {
        th {
          background-color: $bg-color-component;
          color: $text-color-primary;
          font-weight: $font-weight-medium;
        }
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: flex-end;
      padding: $spacing-lg;
      border-top: 1px solid $border-color-split;
    }
  }
}
</style>
