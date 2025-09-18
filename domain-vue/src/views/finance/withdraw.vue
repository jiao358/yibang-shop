<template>
  <div class="withdraw-approval-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">提现审核</h1>
      <div class="page-actions">
        <el-badge :value="pendingCount" :hidden="pendingCount === 0" type="danger">
          <el-button type="primary" @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </el-badge>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stats-cards">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value text-warning">{{ pendingCount || 0 }}</div>
            <div class="stat-label">待审核</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#FA8C16"><Clock /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value text-success">{{ stats.byStatus?.completed || 0 }}</div>
            <div class="stat-label">已完成</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#52C41A"><Check /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value text-info">{{ stats.byStatus?.processing || 0 }}</div>
            <div class="stat-label">处理中</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#1890FF"><Loading /></el-icon>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value text-error">{{ stats.byStatus?.failed || 0 }}</div>
            <div class="stat-label">失败</div>
          </div>
          <div class="stat-icon">
            <el-icon size="40" color="#FF4D4F"><Close /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryForm" inline class="filter-form">
        <el-form-item label="提现状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="待审核" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="失败" value="failed" />
            <el-option label="已取消" value="cancelled" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="用户ID">
          <el-input
            v-model="queryForm.userId"
            placeholder="请输入用户ID"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        
        <el-form-item label="申请时间">
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
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <!-- 表格工具栏 -->
      <div class="table-toolbar">
        <div class="toolbar-left">
          <el-button
            type="success"
            :disabled="selectedRows.length === 0"
            @click="handleBatchApprove(true)"
          >
            <el-icon><Check /></el-icon>
            批量通过
          </el-button>
          <el-button
            type="danger"
            :disabled="selectedRows.length === 0"
            @click="handleBatchApprove(false)"
          >
            <el-icon><Close /></el-icon>
            批量拒绝
          </el-button>
          <el-button @click="handleExport">
            <el-icon><Download /></el-icon>
            导出Excel
          </el-button>
        </div>
        
        <div class="toolbar-right">
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </div>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        @selection-change="handleSelectionChange"
        row-key="id"
        class="data-table"
      >
        <el-table-column type="selection" width="55" />
        
        <el-table-column prop="id" label="申请ID" width="80" />
        
        <el-table-column prop="userId" label="用户ID" width="100" />
        
        <el-table-column prop="amount" label="提现金额" width="120">
          <template #default="{ row }">
            <span class="amount-text">¥{{ (row.amount / 100).toFixed(2) }}</span>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="申请时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="handleApprove(row, true)"
              class="success-btn"
            >
              通过
            </el-button>
            <el-button 
              v-if="row.status === 'pending'"
              type="text" 
              size="small" 
              @click="handleApprove(row, false)"
              class="danger-btn"
            >
              拒绝
            </el-button>
            <el-button 
              v-if="row.status === 'processing'"
              type="text" 
              size="small" 
              @click="openConfirmPayment(row)"
              class="primary-btn"
            >
              确认打款
            </el-button>
            <el-button 
              v-if="row.status === 'processing'"
              type="text" 
              size="small" 
              @click="handleMarkFailed(row)"
              class="danger-btn"
            >
              标记失败
            </el-button>
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

    <!-- 审核弹窗 -->
    <el-dialog v-model="approvalDialogVisible" title="审核提现申请" width="500px">
      <div class="approval-info">
        <p><strong>申请ID：</strong>{{ currentWithdraw.id }}</p>
        <p><strong>用户ID：</strong>{{ currentWithdraw.userId }}</p>
        <p><strong>提现金额：</strong>¥{{ (currentWithdraw.amount / 100).toFixed(2) }}</p>
        <p><strong>申请时间：</strong>{{ formatDateTime(currentWithdraw.createdAt) }}</p>
      </div>
      
      <el-form :model="approvalForm" :rules="approvalRules" ref="approvalFormRef" label-width="100px">
        <el-form-item label="审核结果" prop="approved">
          <el-radio-group v-model="approvalForm.approved">
            <el-radio :label="true">通过</el-radio>
            <el-radio :label="false">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="approvalForm.remark"
            type="textarea"
            :rows="3"
            :placeholder="approvalForm.approved ? '通过备注(可选)' : '请输入拒绝原因'"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmApproval" :loading="approvalLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 确认打款弹窗 -->
    <el-dialog v-model="paymentDialogVisible" title="确认打款完成" width="500px">
      <div class="payment-info">
        <p><strong>申请ID：</strong>{{ currentWithdraw.id }}</p>
        <p><strong>提现金额：</strong>¥{{ (currentWithdraw.amount / 100).toFixed(2) }}</p>
      </div>
      
      <el-form :model="paymentForm" :rules="paymentRules" ref="paymentFormRef" label-width="120px">
        <el-form-item label="微信交易单号" prop="wechatTransactionId">
          <el-input
            v-model="paymentForm.wechatTransactionId"
            placeholder="请输入微信交易单号"
          />
        </el-form-item>
        <el-form-item label="微信支付单号">
          <el-input
            v-model="paymentForm.wechatPaymentNo"
            placeholder="请输入微信支付单号(可选)"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPayment" :loading="paymentLoading">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Refresh, Search, Check, Close, Download, Clock, Loading 
} from '@element-plus/icons-vue'
import { withdrawApi } from '@/api/withdraw'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dateRange = ref([])
const pendingCount = ref(0)

// 弹窗状态
const approvalDialogVisible = ref(false)
const paymentDialogVisible = ref(false)
const approvalLoading = ref(false)
const paymentLoading = ref(false)
const currentWithdraw = ref({})

// 表单引用
const approvalFormRef = ref()
const paymentFormRef = ref()

// 查询表单
const queryForm = reactive({
  status: '',
  userId: '',
  startDate: '',
  endDate: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 统计数据
const stats = ref({
  byStatus: {}
})

// 审核表单
const approvalForm = reactive({
  approved: true,
  remark: ''
})

// 打款确认表单
const paymentForm = reactive({
  wechatTransactionId: '',
  wechatPaymentNo: ''
})

// 表单验证规则
const approvalRules = {
  approved: [{ required: true, message: '请选择审核结果', trigger: 'change' }],
  remark: [
    { 
      validator: (rule, value, callback) => {
        if (!approvalForm.approved && !value) {
          callback(new Error('拒绝时必须填写拒绝原因'))
        } else {
          callback()
        }
      }, 
      trigger: 'blur' 
    }
  ]
}

const paymentRules = {
  wechatTransactionId: [
    { required: true, message: '请输入微信交易单号', trigger: 'blur' }
  ]
}

// 加载提现列表
const loadWithdrawList = async () => {
  try {
    loading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    // TODO: 分页优化 - 待审核状态不缓存确保实时性，其他状态短缓存
    // TODO: Redis缓存 - 键名: bk.withdraws:list:{参数哈希}:{page}:{size}, TTL: 3分钟
    const response = await withdrawApi.getWithdrawList(params)
    
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载提现列表失败:', error)
    ElMessage.error('加载提现列表失败')
  } finally {
    loading.value = false
  }
}

// 加载待审核数量
const loadPendingCount = async () => {
  try {
    const response = await withdrawApi.getPendingCount()
    if (response.code === 200) {
      pendingCount.value = response.data || 0
    }
  } catch (error) {
    console.error('加载待审核数量失败:', error)
  }
}

// 加载统计数据
const loadStats = async () => {
  try {
    // TODO: Redis缓存 - 键名: bk.withdraws:stats:overview, TTL: 10分钟
    const response = await withdrawApi.getWithdrawStats({ type: 'overview' })
    if (response.code === 200) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadWithdrawList()
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    status: '',
    userId: '',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  pagination.page = 1
  loadWithdrawList()
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

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

// 分页变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadWithdrawList()
}

const handleCurrentChange = (page) => {
  pagination.page = page
  loadWithdrawList()
}

// 刷新
const handleRefresh = () => {
  loadWithdrawList()
  loadPendingCount()
  loadStats()
}

// 查看详情
const handleView = (row) => {
  // TODO: 实现查看详情弹窗或跳转页面
  ElMessage.info('查看详情功能开发中')
}

// 审核申请
const handleApprove = (row, approved) => {
  currentWithdraw.value = row
  Object.assign(approvalForm, {
    approved,
    remark: ''
  })
  approvalDialogVisible.value = true
}

// 确认审核
const handleConfirmApproval = async () => {
  try {
    const valid = await approvalFormRef.value.validate()
    if (!valid) return
    
    approvalLoading.value = true
    
    // TODO: 审核操作二次确认 - 特别是批量操作时的强提醒
    await withdrawApi.approveWithdraw(currentWithdraw.value.id, approvalForm)
    
    ElMessage.success(approvalForm.approved ? '审核通过' : '审核拒绝')
    approvalDialogVisible.value = false
    loadWithdrawList()
    loadPendingCount()
    
  } catch (error) {
    console.error('审核失败:', error)
  } finally {
    approvalLoading.value = false
  }
}

// 批量审核
const handleBatchApprove = async (approved) => {
  try {
    const action = approved ? '通过' : '拒绝'
    const result = await ElMessageBox.prompt(
      `确定要${action}选中的 ${selectedRows.value.length} 个提现申请吗？`,
      `批量${action}`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: approved ? '备注(可选)' : '请输入拒绝原因',
        inputValidator: (value) => {
          if (!approved && !value) {
            return '拒绝时必须填写原因'
          }
          return true
        }
      }
    )
    
    const withdrawIds = selectedRows.value.map(row => row.id)
    await withdrawApi.batchApprove({
      withdrawIds,
      approved,
      remark: result.value
    })
    
    ElMessage.success(`批量${action}成功`)
    loadWithdrawList()
    loadPendingCount()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量审核失败:', error)
    }
  }
}

// 打开确认打款弹窗
const openConfirmPayment = (row) => {
  currentWithdraw.value = row
  Object.assign(paymentForm, {
    wechatTransactionId: '',
    wechatPaymentNo: ''
  })
  paymentDialogVisible.value = true
}

// 确认打款完成
const handleConfirmPayment = async () => {
  try {
    const valid = await paymentFormRef.value.validate()
    if (!valid) return
    
    paymentLoading.value = true
    
    await withdrawApi.confirmPayment(currentWithdraw.value.id, paymentForm)
    ElMessage.success('打款确认成功')
    paymentDialogVisible.value = false
    loadWithdrawList()
    
  } catch (error) {
    console.error('确认打款失败:', error)
  } finally {
    paymentLoading.value = false
  }
}

// 标记失败
const handleMarkFailed = async (row) => {
  try {
    const result = await ElMessageBox.prompt(
      '请输入失败原因',
      '标记打款失败',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '请输入失败原因',
        inputValidator: (value) => {
          if (!value) {
            return '请输入失败原因'
          }
          return true
        }
      }
    )
    
    await withdrawApi.markFailed(row.id, result.value)
    ElMessage.success('标记失败成功')
    loadWithdrawList()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记失败失败:', error)
    }
  }
}

// 导出
const handleExport = async () => {
  try {
    // TODO: 导出限流 - 防止频繁导出影响系统性能
    const response = await withdrawApi.exportWithdraws(queryForm)
    
    // 处理文件下载
    const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `提现记录_${new Date().toISOString().slice(0, 10)}.xlsx`
    link.click()
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

// 工具函数
const getStatusTagType = (status) => {
  const statusMap = {
    pending: 'warning',
    processing: 'primary',
    completed: 'success',
    failed: 'danger',
    cancelled: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    pending: '待审核',
    processing: '处理中',
    completed: '已完成',
    failed: '失败',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadWithdrawList()
  loadPendingCount()
  loadStats()
})
</script>

<style lang="scss" scoped>
.withdraw-approval-page {
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

  .filter-card {
    margin-bottom: $spacing-lg;

    :deep(.el-card__body) {
      padding: $spacing-lg;
    }
  }

  .table-card {
    :deep(.el-card__body) {
      padding: 0;
    }
  }

  .table-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-lg;
    border-bottom: 1px solid $border-color-split;

    .toolbar-left {
      display: flex;
      gap: $spacing-sm;
    }
  }

  .data-table {
    .amount-text {
      font-weight: $font-weight-medium;
      color: $primary-color;
    }

    .success-btn {
      color: $success-color;
      &:hover { color: $success-dark; }
    }

    .danger-btn {
      color: $error-color;
      &:hover { color: $error-dark; }
    }

    .primary-btn {
      color: $primary-color;
      &:hover { color: $primary-dark; }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    padding: $spacing-lg;
  }

  .approval-info,
  .payment-info {
    background-color: $bg-color-component;
    padding: $spacing-md;
    border-radius: $border-radius-base;
    margin-bottom: $spacing-lg;

    p {
      margin: $spacing-xs 0;
      color: $text-color-primary;
    }
  }
}
</style>
