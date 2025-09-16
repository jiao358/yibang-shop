<template>
  <div class="user-list-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">用户管理</h1>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <el-form :model="queryForm" inline class="filter-form">
        <el-form-item label="用户等级">
          <el-select v-model="queryForm.level" placeholder="请选择" clearable style="width: 150px">
            <el-option label="普通用户" value="normal" />
            <el-option label="签约用户" value="signed" />
            <el-option label="VIP用户" value="vip" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="用户状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable style="width: 150px">
            <el-option label="正常" value="active" />
            <el-option label="禁用" value="inactive" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="关键词">
          <el-input
            v-model="queryForm.keyword"
            placeholder="昵称/手机号"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        
        <el-form-item label="注册时间">
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
        
        <el-table-column prop="id" label="用户ID" width="80" />
        
        <el-table-column prop="nickname" label="用户昵称" min-width="150" show-overflow-tooltip />
        
        <el-table-column prop="phone" label="手机号" width="130" />
        
        <el-table-column prop="userLevel" label="用户等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.userLevel)">
              {{ getLevelText(row.userLevel) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="availableBalance" label="可用余额" width="120">
          <template #default="{ row }">
            ¥{{ (row.availableBalance / 100).toFixed(2) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="totalTasks" label="完成任务" width="100" />
        
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="lastLoginTime" label="最后登录" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.lastLoginTime) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="text" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="text" size="small" @click="handleEditBalance(row)">
              调余额
            </el-button>
            <el-button type="text" size="small" @click="handleEditLevel(row)">
              改等级
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

    <!-- 余额调整弹窗 -->
    <el-dialog v-model="balanceDialogVisible" title="调整用户余额" width="500px">
      <el-form :model="balanceForm" :rules="balanceRules" ref="balanceFormRef" label-width="100px">
        <el-form-item label="用户昵称">
          <el-input :value="currentUser.nickname" disabled />
        </el-form-item>
        <el-form-item label="当前余额">
          <el-input :value="`¥${(currentUser.availableBalance / 100).toFixed(2)}`" disabled />
        </el-form-item>
        <el-form-item label="调整类型" prop="type">
          <el-radio-group v-model="balanceForm.type">
            <el-radio label="add">增加</el-radio>
            <el-radio label="subtract">减少</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整金额" prop="amount">
          <el-input-number
            v-model="balanceForm.amount"
            :min="1"
            :max="1000000"
            placeholder="请输入调整金额(分)"
            style="width: 100%"
          />
          <div class="form-tip">单位：分，1元=100分</div>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="balanceForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="balanceDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmBalance" :loading="balanceLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 等级调整弹窗 -->
    <el-dialog v-model="levelDialogVisible" title="调整用户等级" width="500px">
      <el-form :model="levelForm" :rules="levelRules" ref="levelFormRef" label-width="100px">
        <el-form-item label="用户昵称">
          <el-input :value="currentUser.nickname" disabled />
        </el-form-item>
        <el-form-item label="当前等级">
          <el-input :value="getLevelText(currentUser.userLevel)" disabled />
        </el-form-item>
        <el-form-item label="新等级" prop="newLevel">
          <el-select v-model="levelForm.newLevel" placeholder="请选择新等级">
            <el-option label="普通用户" value="normal" />
            <el-option label="签约用户" value="signed" />
            <el-option label="VIP用户" value="vip" />
          </el-select>
        </el-form-item>
        <el-form-item label="调整原因" prop="reason">
          <el-input
            v-model="levelForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入调整原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="levelDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmLevel" :loading="levelLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Check, Close } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const selectedRows = ref([])
const dateRange = ref([])

// 弹窗状态
const balanceDialogVisible = ref(false)
const levelDialogVisible = ref(false)
const balanceLoading = ref(false)
const levelLoading = ref(false)
const currentUser = ref({})

// 表单引用
const balanceFormRef = ref()
const levelFormRef = ref()

// 查询表单
const queryForm = reactive({
  level: '',
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

// 余额调整表单
const balanceForm = reactive({
  type: 'add',
  amount: '',
  reason: ''
})

// 等级调整表单
const levelForm = reactive({
  newLevel: '',
  reason: ''
})

// 表单验证规则
const balanceRules = {
  type: [{ required: true, message: '请选择调整类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入调整金额', trigger: 'blur' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

const levelRules = {
  newLevel: [{ required: true, message: '请选择新等级', trigger: 'change' }],
  reason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

// 加载用户列表
const loadUserList = async () => {
  try {
    loading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    // TODO: 分页优化 - 后端使用覆盖索引，只查询列表展示必要字段
    // TODO: Redis缓存 - 键名: bk.users:list:{参数哈希}:{page}:{size}, TTL: 3分钟
    const response = await userApi.getUserList(params)
    
    if (response.code === 200) {
      tableData.value = response.data.records || []
      pagination.total = response.data.total || 0
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

// 重置
const handleReset = () => {
  Object.assign(queryForm, {
    level: '',
    status: '',
    keyword: '',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  pagination.page = 1
  loadUserList()
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
  loadUserList()
}

// 当前页变化
const handleCurrentChange = (page) => {
  pagination.page = page
  loadUserList()
}

// 刷新
const handleRefresh = () => {
  loadUserList()
}

// 查看用户
const handleView = (row) => {
  router.push(`/user/detail/${row.id}`)
}

// 调整余额
const handleEditBalance = (row) => {
  currentUser.value = row
  Object.assign(balanceForm, {
    type: 'add',
    amount: '',
    reason: ''
  })
  balanceDialogVisible.value = true
}

// 确认余额调整
const handleConfirmBalance = async () => {
  try {
    const valid = await balanceFormRef.value.validate()
    if (!valid) return
    
    balanceLoading.value = true
    
    await userApi.updateUserBalance(currentUser.value.id, balanceForm)
    ElMessage.success('余额调整成功')
    balanceDialogVisible.value = false
    loadUserList()
    
  } catch (error) {
    console.error('调整余额失败:', error)
  } finally {
    balanceLoading.value = false
  }
}

// 调整等级
const handleEditLevel = (row) => {
  currentUser.value = row
  Object.assign(levelForm, {
    newLevel: '',
    reason: ''
  })
  levelDialogVisible.value = true
}

// 确认等级调整
const handleConfirmLevel = async () => {
  try {
    const valid = await levelFormRef.value.validate()
    if (!valid) return
    
    levelLoading.value = true
    
    await userApi.updateUserLevel(currentUser.value.id, levelForm)
    ElMessage.success('等级调整成功')
    levelDialogVisible.value = false
    loadUserList()
    
  } catch (error) {
    console.error('调整等级失败:', error)
  } finally {
    levelLoading.value = false
  }
}

// 批量状态操作
const handleBatchStatus = async (status) => {
  try {
    const action = status === 'active' ? '启用' : '禁用'
    await ElMessageBox.confirm(
      `确定要${action}选中的 ${selectedRows.value.length} 个用户吗？`,
      `批量${action}确认`,
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const userIds = selectedRows.value.map(row => row.id)
    await userApi.batchOperation(userIds, status)
    ElMessage.success(`批量${action}成功`)
    loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(`批量${action}失败:`, error)
    }
  }
}

// 工具函数
const getLevelTagType = (level) => {
  const levelMap = {
    normal: 'info',
    signed: 'warning',
    vip: 'danger'
  }
  return levelMap[level] || 'info'
}

const getLevelText = (level) => {
  const levelMap = {
    normal: '普通用户',
    signed: '签约用户',
    vip: 'VIP用户'
  }
  return levelMap[level] || level
}

const getStatusTagType = (status) => {
  const statusMap = {
    active: 'success',
    inactive: 'info'
  }
  return statusMap[status] || 'info'
}

const getStatusText = (status) => {
  const statusMap = {
    active: '正常',
    inactive: '禁用'
  }
  return statusMap[status] || status
}

const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadUserList()
})
</script>

<style lang="scss" scoped>
.user-list-page {
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
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    padding: $spacing-lg;
  }

  .form-tip {
    font-size: $font-size-sm;
    color: $text-color-tertiary;
    margin-top: $spacing-xs;
  }
}
</style>
