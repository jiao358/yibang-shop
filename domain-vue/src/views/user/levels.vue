<template>
  <div class="user-levels">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户等级管理</span>
          <el-button type="primary" @click="handleCreate">新增等级</el-button>
        </div>
      </template>
      
      <el-table :data="levelList" v-loading="loading">
        <el-table-column prop="code" label="等级代码" width="120" />
        <el-table-column prop="name" label="等级名称" width="150" />
        <el-table-column prop="description" label="等级描述" />
        <el-table-column prop="userCount" label="用户数量" width="100" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const loading = ref(false)
const levelList = ref([])

const loadLevels = async () => {
  try {
    loading.value = true
    const response = await userApi.getUserLevels()
    
    // 转换为数组格式
    levelList.value = Object.entries(response.data || {}).map(([code, name]) => ({
      code,
      name,
      description: `${name}用户权限`,
      userCount: 0 // TODO: 统计每个等级的用户数量
    }))
  } catch (error) {
    console.error('加载用户等级失败:', error)
    ElMessage.error('加载用户等级失败')
  } finally {
    loading.value = false
  }
}

const handleCreate = () => {
  ElMessage.info('创建等级功能开发中')
}

const handleEdit = (row) => {
  ElMessage.info('编辑等级功能开发中')
}

const handleDelete = (row) => {
  ElMessage.info('删除等级功能开发中')
}

onMounted(() => {
  loadLevels()
})
</script>

<style scoped>
.user-levels {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
