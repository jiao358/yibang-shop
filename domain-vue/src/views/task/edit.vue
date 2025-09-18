<template>
  <div class="task-edit-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">{{ isViewMode ? '查看任务' : '编辑任务' }}</h1>
      <div class="page-actions">
        <el-button @click="handleCancel">{{ isViewMode ? '返回' : '取消' }}</el-button>
        <el-button v-if="!isViewMode" type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </div>
    </div>

    <!-- 表单区域 -->
    <el-card class="form-card" shadow="never" v-loading="loading">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        :disabled="isViewMode"
        label-width="120px"
        class="task-form"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="任务ID">
              <el-input v-model="formData.id" disabled />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input :value="formatDateTime(formData.createdAt)" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="任务标题" prop="title">
              <el-input
                v-model="formData.title"
                placeholder="请输入任务标题"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="任务类型" prop="type">
              <el-select v-model="formData.type" placeholder="请选择任务类型">
                <el-option
                  v-for="(label, value) in taskTypes"
                  :key="value"
                  :label="label"
                  :value="value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="奖励金额" prop="rewardAmount">
              <el-input-number
                v-model="formData.rewardAmount"
                :min="0"
                :max="100000"
                :step="100"
                placeholder="请输入奖励金额(分)"
                style="width: 100%"
              />
              <div class="form-tip">单位：分，1元=100分</div>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="佣金等级" prop="rewardLevel">
              <el-select v-model="formData.rewardLevel" placeholder="请选择佣金等级">
                <el-option label="低佣金" value="low" />
                <el-option label="中佣金" value="medium" />
                <el-option label="高佣金" value="high" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="适用用户等级" prop="userLevel">
              <el-select v-model="formData.userLevel" placeholder="请选择用户等级">
                <el-option label="所有用户" value="all" />
                <el-option label="普通用户" value="normal" />
                <el-option label="签约用户" value="signed" />
                <el-option label="VIP用户" value="vip" />
              </el-select>
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item label="任务状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择任务状态">
                <el-option label="活跃" value="active" />
                <el-option label="非活跃" value="inactive" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="任务图片">
          <el-upload
            class="image-uploader"
            :http-request="handleImageUpload"
            :show-file-list="false"
            :before-upload="beforeImageUpload"
            :disabled="isViewMode"
          >
            <img v-if="formData.imageUrl" :src="formData.imageUrl" class="uploaded-image" />
            <el-icon v-else class="image-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入任务描述"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="任务要求" prop="requirements">
          <el-input
            v-model="formData.requirements"
            type="textarea"
            :rows="6"
            placeholder="请输入任务要求配置(JSON格式)"
          />
          <div class="form-tip">
            JSON格式示例：{"steps": ["打开应用", "完成注册", "上传截图"], "timeLimit": 300}
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { taskApi } from '@/api/task'
import { systemApi } from '@/api/system'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const saving = ref(false)

// 是否为查看模式
const isViewMode = computed(() => route.query.mode === 'view')

// 表单数据
const formData = reactive({
  id: '',
  title: '',
  description: '',
  imageUrl: '',
  type: '',
  rewardAmount: 0,
  rewardLevel: 'low',
  requirements: '',
  userLevel: 'all',
  status: 'active',
  createdAt: '',
  updatedAt: ''
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入任务标题', trigger: 'blur' },
    { min: 2, max: 200, message: '标题长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择任务类型', trigger: 'change' }
  ],
  rewardAmount: [
    { required: true, message: '请输入奖励金额', trigger: 'blur' },
    { type: 'number', min: 1, message: '奖励金额必须大于0', trigger: 'blur' }
  ],
  rewardLevel: [
    { required: true, message: '请选择佣金等级', trigger: 'change' }
  ],
  userLevel: [
    { required: true, message: '请选择适用用户等级', trigger: 'change' }
  ]
}

// 任务类型选项
const taskTypes = ref({
  ad: '广告任务',
  video: '视频任务',
  app_install: '应用安装',
  survey: '问卷调查',
  share: '分享任务'
})

// 加载任务详情
const loadTaskDetail = async () => {
  try {
    loading.value = true
    
    const taskId = route.params.id
    // TODO: Redis缓存 - 键名: bk.task:detail:{taskId}, TTL: 10分钟
    const response = await taskApi.getTaskDetail(taskId)
    
    if (response.code === 200) {
      Object.assign(formData, response.data)
    }
  } catch (error) {
    console.error('加载任务详情失败:', error)
    ElMessage.error('加载任务详情失败')
  } finally {
    loading.value = false
  }
}

// 自定义图片上传
const handleImageUpload = async (options) => {
  try {
    const response = await systemApi.uploadFile(options.file, 'image')
    if (response.code === 200) {
      formData.imageUrl = response.data.url
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
  }
}

// 图片上传前验证
const beforeImageUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 保存任务
const handleSave = async () => {
  try {
    const valid = await formRef.value.validate()
    if (!valid) return
    
    // 验证JSON格式
    if (formData.requirements) {
      try {
        JSON.parse(formData.requirements)
      } catch (error) {
        ElMessage.error('任务要求必须是有效的JSON格式')
        return
      }
    }
    
    saving.value = true
    
    const { id, createdAt, updatedAt, ...updateData } = formData
    await taskApi.updateTask(id, updateData)
    
    ElMessage.success('任务更新成功')
    router.push('/task/list')
    
  } catch (error) {
    console.error('更新任务失败:', error)
  } finally {
    saving.value = false
  }
}

// 取消
const handleCancel = () => {
  router.back()
}

// 工具函数
const formatDateTime = (datetime) => {
  if (!datetime) return '-'
  return new Date(datetime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadTaskDetail()
})
</script>

<style lang="scss" scoped>
.task-edit-page {
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

  .form-card {
    :deep(.el-card__body) {
      padding: $spacing-xl;
    }
  }

  .task-form {
    max-width: 800px;

    .form-tip {
      font-size: $font-size-sm;
      color: $text-color-tertiary;
      margin-top: $spacing-xs;
    }

    .image-uploader {
      :deep(.el-upload) {
        border: 1px dashed $border-color-base;
        border-radius: $border-radius-base;
        cursor: pointer;
        position: relative;
        overflow: hidden;
        transition: border-color $motion-duration-fast $ease-out;

        &:hover {
          border-color: $primary-color;
        }
      }

      .image-uploader-icon {
        font-size: 28px;
        color: $text-color-tertiary;
        width: 178px;
        height: 178px;
        text-align: center;
        line-height: 178px;
      }

      .uploaded-image {
        width: 178px;
        height: 178px;
        display: block;
        object-fit: cover;
      }
    }
  }
}
</style>
