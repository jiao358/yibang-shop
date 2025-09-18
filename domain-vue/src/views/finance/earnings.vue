<template>
  <div class="earnings-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收益管理</span>
        </div>
      </template>
      
      <!-- 搜索筛选 -->
      <div class="search-form">
        <el-form :model="queryForm" inline>
          <el-form-item label="用户ID">
            <el-input v-model="queryForm.userId" placeholder="请输入用户ID" clearable />
          </el-form-item>
          <el-form-item label="收益类型">
            <el-select v-model="queryForm.type" placeholder="请选择收益类型" clearable>
              <el-option label="任务收益" value="task" />
              <el-option label="邀请收益" value="invite" />
              <el-option label="其他收益" value="other" />
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
            <el-button type="primary" @click="loadEarningsList">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 收益列表 -->
      <el-table :data="earningsList" v-loading="loading">
        <el-table-column prop="id" label="收益ID" width="100" />
        <el-table-column prop="userId" label="用户ID" width="100" />
        <el-table-column prop="userNickname" label="用户昵称" width="150" />
        <el-table-column prop="type" label="收益类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ getTypeName(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="收益金额" width="120">
          <template #default="{ row }">
            ¥{{ formatAmount(row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'confirmed' ? 'success' : 'warning'">
              {{ row.status === 'confirmed' ? '已确认' : '待确认' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadEarningsList"
          @current-change="loadEarningsList"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const earningsList = ref([])
const dateRange = ref([])

const queryForm = reactive({
  userId: '',
  type: '',
  startDate: '',
  endDate: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadEarningsList = async () => {
  try {
    loading.value = true
    
    // TODO: 调用收益列表API
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    // Mock数据
    earningsList.value = []
    pagination.total = 0
    
    ElMessage.success('数据加载完成')
  } catch (error) {
    console.error('加载收益列表失败:', error)
    ElMessage.error('加载收益列表失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startDate = dates[0]
    queryForm.endDate = dates[1]
  } else {
    queryForm.startDate = ''
    queryForm.endDate = ''
  }
}

const resetQuery = () => {
  Object.assign(queryForm, {
    userId: '',
    type: '',
    startDate: '',
    endDate: ''
  })
  dateRange.value = []
  pagination.page = 1
  loadEarningsList()
}

const handleView = (row) => {
  ElMessage.info('查看收益详情功能开发中')
}

const getTypeTagType = (type) => {
  const typeMap = {
    task: 'success',
    invite: 'warning',
    other: 'info'
  }
  return typeMap[type] || 'info'
}

const getTypeName = (type) => {
  const typeMap = {
    task: '任务收益',
    invite: '邀请收益',
    other: '其他收益'
  }
  return typeMap[type] || '未知'
}

const formatAmount = (amount) => {
  if (amount == null) return '0.00'
  return (amount / 100).toFixed(2)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadEarningsList()
})
</script>

<style scoped>
.earnings-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
