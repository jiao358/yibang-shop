<template>
  <div class="system-logs">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="primary" @click="loadLogs">刷新</el-button>
        </div>
      </template>
      
      <!-- 搜索筛选 -->
      <div class="search-form">
        <el-form :model="queryForm" inline>
          <el-form-item label="操作类型">
            <el-input v-model="queryForm.action" placeholder="请输入操作类型" clearable />
          </el-form-item>
          <el-form-item label="操作人">
            <el-input v-model="queryForm.operator" placeholder="请输入操作人" clearable />
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
            <el-button type="primary" @click="loadLogs">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 日志列表 -->
      <el-table :data="logsList" v-loading="loading">
        <el-table-column prop="id" label="日志ID" width="100" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="action" label="操作类型" width="150" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="targetName" label="操作对象" width="200" />
        <el-table-column prop="ipAddress" label="IP地址" width="140" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'success' ? 'success' : 'danger'">
              {{ row.status === 'success' ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">详情</el-button>
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
          @size-change="loadLogs"
          @current-change="loadLogs"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { systemApi } from '@/api/system'

const loading = ref(false)
const logsList = ref([])
const dateRange = ref([])

const queryForm = reactive({
  action: '',
  operator: '',
  startTime: '',
  endTime: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadLogs = async () => {
  try {
    loading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await systemApi.getOperationLogs(params)
    logsList.value = response.data.records || []
    pagination.total = response.data.total || 0
    
  } catch (error) {
    console.error('加载操作日志失败:', error)
    ElMessage.error('加载操作日志失败')
  } finally {
    loading.value = false
  }
}

const handleDateChange = (dates) => {
  if (dates && dates.length === 2) {
    queryForm.startTime = dates[0]
    queryForm.endTime = dates[1]
  } else {
    queryForm.startTime = ''
    queryForm.endTime = ''
  }
}

const resetQuery = () => {
  Object.assign(queryForm, {
    action: '',
    operator: '',
    startTime: '',
    endTime: ''
  })
  dateRange.value = []
  pagination.page = 1
  loadLogs()
}

const handleView = (row) => {
  ElMessage.info('查看日志详情功能开发中')
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadLogs()
})
</script>

<style scoped>
.system-logs {
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
