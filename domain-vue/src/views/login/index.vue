<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-header">
        <div class="logo">
          <div class="logo-image" />
          <h1 class="logo-text">任务商城后台管理</h1>
        </div>
        <p class="login-subtitle">企业级后台管理系统</p>
      </div>

      <el-card class="login-card" shadow="always">
        <template #header>
          <div class="card-header">
            <h2>系统登录</h2>
          </div>
        </template>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          size="large"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              clearable
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <el-checkbox v-model="loginForm.rememberMe">
              记住登录状态
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              class="login-button"
              :loading="loginLoading"
              @click="handleLogin"
            >
              {{ loginLoading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <el-alert
            title="提示：当前为演示模式，实际将通过ERP系统SSO登录"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </el-card>

      <div class="login-bottom">
        <p class="copyright">© 2024 任务商城后台管理系统. All rights reserved.</p>
      </div>
    </div>

    <!-- 背景装饰 -->
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()
const loginFormRef = ref()
const loginLoading = ref(false)

// 登录表单
const loginForm = reactive({
  username: '',
  password: '',
  rememberMe: false
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return
    
    loginLoading.value = true
    
    // 调用后端登录接口
    const response = await authApi.adminLogin({
      username: loginForm.username,
      password: loginForm.password
    })
    
    if (response.code === 200) {
      const { token, user, expiresIn } = response.data
      
      // 存储登录信息
      localStorage.setItem('admin_token', token)
      localStorage.setItem('admin_user', JSON.stringify(user))
      localStorage.setItem('admin_token_expires', String(Date.now() + expiresIn * 1000))
      
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } else {
      ElMessage.error(response.message || '登录失败')
    }
    
  } catch (error) {
    console.error('登录失败:', error)
    ElMessage.error('登录失败，请检查用户名和密码')
  } finally {
    loginLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  position: relative;
  width: 100%;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  overflow: hidden;
}

.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 400px;
  padding: $spacing-xl;
}

.login-header {
  text-align: center;
  margin-bottom: $spacing-xl;

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: $spacing-md;

    .logo-image {
      width: 48px;
      height: 48px;
      margin-right: $spacing-md;
    }

    .logo-text {
      font-size: $font-size-h2;
      font-weight: $font-weight-bold;
      color: #ffffff;
      margin: 0;
    }
  }

  .login-subtitle {
    color: rgba(255, 255, 255, 0.8);
    font-size: $font-size-base;
    margin: 0;
  }
}

.login-card {
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
  border: none;

  :deep(.el-card__header) {
    padding: $spacing-lg $spacing-lg 0;
    border-bottom: none;

    .card-header {
      text-align: center;

      h2 {
        font-size: $font-size-h3;
        font-weight: $font-weight-semibold;
        color: $text-color-primary;
        margin: 0;
      }
    }
  }

  :deep(.el-card__body) {
    padding: $spacing-lg;
  }
}

.login-form {
  .login-button {
    width: 100%;
    height: 48px;
    font-size: $font-size-lg;
    font-weight: $font-weight-medium;
  }

  :deep(.el-input__wrapper) {
    box-shadow: 0 0 0 1px $border-color-base inset;
    
    &:hover {
      box-shadow: 0 0 0 1px $primary-light inset;
    }
    
    &.is-focus {
      box-shadow: 0 0 0 1px $primary-color inset;
    }
  }
}

.login-footer {
  margin-top: $spacing-lg;
}

.login-bottom {
  text-align: center;
  margin-top: $spacing-xl;

  .copyright {
    color: rgba(255, 255, 255, 0.7);
    font-size: $font-size-sm;
    margin: 0;
  }
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: 1;

  .bg-shape {
    position: absolute;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;

    &.shape-1 {
      width: 200px;
      height: 200px;
      top: 10%;
      left: 10%;
      animation-delay: 0s;
    }

    &.shape-2 {
      width: 150px;
      height: 150px;
      top: 60%;
      right: 10%;
      animation-delay: 2s;
    }

    &.shape-3 {
      width: 100px;
      height: 100px;
      bottom: 20%;
      left: 20%;
      animation-delay: 4s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}
</style>
