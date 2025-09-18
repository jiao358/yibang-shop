<template>
  <div class="user-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户详情</span>
          <el-button type="primary" @click="goBack">返回</el-button>
        </div>
      </template>
      
      <div v-if="userDetail" class="user-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ userDetail.id }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ userDetail.nickname }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ userDetail.phone }}</el-descriptions-item>
          <el-descriptions-item label="用户等级">{{ userDetail.userLevel }}</el-descriptions-item>
          <el-descriptions-item label="可用余额">{{ formatAmount(userDetail.availableBalance) }}</el-descriptions-item>
          <el-descriptions-item label="冻结余额">{{ formatAmount(userDetail.frozenBalance) }}</el-descriptions-item>
          <el-descriptions-item label="累计收益">{{ formatAmount(userDetail.totalEarnings) }}</el-descriptions-item>
          <el-descriptions-item label="完成任务数">{{ userDetail.totalTasks }}</el-descriptions-item>
          <el-descriptions-item label="邀请人数">{{ userDetail.inviteCount }}</el-descriptions-item>
          <el-descriptions-item label="用户状态">
            <el-tag :type="userDetail.status === 'active' ? 'success' : 'danger'">
              {{ userDetail.status === 'active' ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDate(userDetail.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="最后登录">{{ formatDate(userDetail.lastLoginTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <div v-else class="loading">
        <el-skeleton :rows="8" animated />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { userApi } from '@/api/user'

const route = useRoute()
const router = useRouter()

const userDetail = ref(null)
const loading = ref(false)

const loadUserDetail = async () => {
  const userId = route.params.id
  
  if (!userId || userId === ':id') {
    ElMessage.error('用户ID不能为空或无效')
    return
  }
  
  try {
    loading.value = true
    const response = await userApi.getUserDetail(userId)
    userDetail.value = response.data
  } catch (error) {
    console.error('加载用户详情失败:', error)
    ElMessage.error('加载用户详情失败')
  } finally {
    loading.value = false
  }
}

const formatAmount = (amount) => {
  if (amount == null) return '0.00'
  return (amount / 100).toFixed(2)
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const goBack = () => {
  router.go(-1)
}

onMounted(() => {
  loadUserDetail()
})
</script>

<style scoped>
.user-detail {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  margin-top: 20px;
}

.loading {
  padding: 20px;
}
</style>
