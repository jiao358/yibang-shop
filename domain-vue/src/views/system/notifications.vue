<template>
  <div class="notifications-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>通知管理</span>
          <el-button type="primary" @click="handleCreate">新增通知</el-button>
        </div>
      </template>
      
      <!-- 搜索筛选 -->
      <div class="search-form">
        <el-form :model="queryForm" inline>
          <el-form-item label="通知类型">
            <el-select v-model="queryForm.type" placeholder="请选择通知类型" clearable>
              <el-option label="系统通知" value="system" />
              <el-option label="活动通知" value="activity" />
              <el-option label="任务通知" value="task" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadNotifications">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 通知列表 -->
      <el-table :data="notificationsList" v-loading="loading">
        <el-table-column prop="id" label="通知ID" width="100" />
        <el-table-column prop="title" label="标题" width="200" />
        <el-table-column prop="content" label="内容" />
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            <el-tag>{{ getTypeName(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'warning'">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" @click="handleSend(row)">发送</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
          @size-change="loadNotifications"
          @current-change="loadNotifications"
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
const notificationsList = ref([])

const queryForm = reactive({
  type: ''
})

const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const loadNotifications = async () => {
  try {
    loading.value = true
    
    const params = {
      ...queryForm,
      page: pagination.page,
      size: pagination.size
    }
    
    const response = await systemApi.getNotifications(params)
    notificationsList.value = response.data.records || []
    pagination.total = response.data.total || 0
    
  } catch (error) {
    console.error('加载通知列表失败:', error)
    ElMessage.error('加载通知列表失败')
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.type = ''
  pagination.page = 1
  loadNotifications()
}

const handleCreate = () => {
  ElMessage.info('创建通知功能开发中')
}

const handleEdit = (row) => {
  ElMessage.info('编辑通知功能开发中')
}

const handleSend = (row) => {
  ElMessage.info('发送通知功能开发中')
}

const handleDelete = (row) => {
  ElMessage.info('删除通知功能开发中')
}

const getTypeName = (type) => {
  const typeMap = {
    system: '系统通知',
    activity: '活动通知',
    task: '任务通知'
  }
  return typeMap[type] || '未知'
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.notifications-page {
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
