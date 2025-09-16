<template>
  <div class="task-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">任务管理</h1>
      <div class="page-actions">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新建任务
        </el-button>
      </div>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryForm" inline class="filter-form">
        <el-form-item label="任务类型">
          <el-select v-model="queryForm.type" placeholder="请选择" clearable style="width: 150px">
            <el-option
              v-for="(label, value) in taskTypes"
              :key="value"
              :label="label"
              :value="value"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="任务状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="活跃" value="active" />
            <el-option label="非活跃" value="inactive" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="任务标题/描述"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="创建时间">
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
            type="danger"
            :disabled="selectedRows.length === 0"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
          <el-button
            :disabled="selectedRows.length === 0"
            @click="handleBatchStatus('active')"
          >
            <el-icon><Check /></el-icon>
            批量启用
          </el-button>
          <el-button
            :disabled="selectedRows.length === 0"
            @click="handleBatchStatus('inactive')"
          >
            <el-icon><Close /></el-icon>
            批量禁用
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
        
        <el-table-column prop="id" label="任务ID" width="80" />
        
        <el-table-column prop="title" label="任务标题" min-width="200" show-overflow-tooltip />
        
        <el-table-column prop="type" label="任务类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ taskTypes[row.type] || row.type }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="rewardAmount" label="奖励金额" width="120">
          <template #default="{ row }">
            ¥{{ (row.rewardAmount / 100).toFixed(2) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="rewardLevel" label="佣金等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.rewardLevel)">
              {{ getLevelText(row.rewardLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="text" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="text" size="small" @click="handleDelete(row)" class="danger-btn">
              删除
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Delete, Check, Close } from '@element-plus/icons-vue'
import { taskApi } from '@/api/task'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dateRange = ref([])

// 查询表单
const queryForm = reactive({
  type: '',
  status: '',
  keyword: '',
  startDate: '',
  endDate: ''
})

// 分页数据
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 任务类型选项
const taskTypes = ref({
  ad: '广告任务',
  video: '视频任务',
  app_install: '应用安装',
  survey: '问卷调查',
  share: '分享任务'
})

// 加载任务列表
const loadTaskList = async () => {
  try {
    loading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    // TODO: 分页优化 - 后端只查询必要字段，使用覆盖索引 idx_type_status
    // TODO: Redis缓存 - 键名: bk.tasks:list:{参数哈希}:{page}:{size}, TTL: 5分钟
    const response = await taskApi.getTaskList(params)
    
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载任务列表失败:', error)
    ElMessage.error('加载任务列表失败')
  } finally {
    loading.value = false
  }
}

// 加载任务类型
const loadTaskTypes = async () => {
  try {
    // TODO: Redis缓存 - 键名: bk.tasks:types, TTL: 1小时
    const response = await taskApi.getTaskTypes()
    if (response.code === 200) {
      taskTypes.value = response.data || taskTypes.value
    }
  } catch (error) {
    console.error('加载任务类型失败:', error)
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadTaskList()
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    type: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  pagination.page = 1
  loadTaskList()
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

// 分页大小变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1
  loadTaskList()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadTaskList()
}

// 刷新
const handleRefresh = () => {
  loadTaskList()
}

// 创建任务
const handleCreate = () => {
  router.push('/task/create')
}

// 查看任务
const handleView = (row) => {
  router.push(`/task/edit/${row.id}?mode=view`)
}

// 编辑任务
const handleEdit = (row) => {
  router.push(`/task/edit/${row.id}`)
}

// 删除任务
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除任务"${row.title}"吗？`,
      '删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await taskApi.deleteTask(row.id)
    ElMessage.success('删除成功')
    loadTaskList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除任务失败:', error)
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要删除选中的 ${selectedRows.value.length} 个任务吗？`,
      '批量删除确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const taskIds = selectedRows.value.map(row => row.id)
    await taskApi.batchOperation(taskIds, 'delete')
    ElMessage.success('批量删除成功')
    loadTaskList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
    }
  }
}

// 批量状态操作
const handleBatchStatus = async (status) => {
  try {
    const action = status === 'active' ? '启用' : '禁用'
    await ElMessageBox.confirm(
      `确定要${action}选中的 ${selectedRows.value.length} 个任务吗？`,
      `批量${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const taskIds = selectedRows.value.map(row => row.id)
    await taskApi.batchOperation(taskIds, status)
    ElMessage.success(`批量${action}成功`)
    loadTaskList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`批量${action}失败:`, error)
    }
  }
}

// 工具函数
const getTypeTagType = (type) => {
  const typeMap = {
    ad: 'primary',
    video: 'success',
    app_install: 'warning',
    survey: 'info',
    share: 'danger'
  }
  return typeMap[type] || 'info'
}

const getLevelTagType = (level) => {
  const levelMap = {
    low: 'info',
    medium: 'warning',
    high: 'danger'
  }
  return levelMap[level] || 'info'
}

const getLevelText = (level) => {
  const levelMap = {
    low: '低',
    medium: '中',
    high: '高'
  }
  return levelMap[level] || level
}

const getStatusTagType = (status) => {
  const statusMap = {
    active: 'success',
    inactive: 'info',
    completed: 'warning'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    active: '活跃',
    inactive: '非活跃',
    completed: '已完成'
  }
  return statusMap[status] || status
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadTaskTypes()
  loadTaskList()
})
</script>

<style lang="scss" scoped>
.task-list-page {
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

  .filter-card {
    margin-bottom: $spacing-lg;

    :deep(.el-card__body) {
      padding: $spacing-lg;
    }
  }

  .filter-form {
    :deep(.el-form-item) {
      margin-bottom: 0;
      margin-right: $spacing-lg;
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
    :deep(.el-table__header) {
      th {
        background-color: $bg-color-component;
        color: $text-color-primary;
        font-weight: $font-weight-medium;
      }
    }

    :deep(.el-table__row) {
      &:hover > td {
        background-color: $primary-1;
      }
    }

    .danger-btn {
      color: $error-color;

      &:hover {
        color: $error-dark;
      }
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    padding: $spacing-lg;
  }
}
</style>
