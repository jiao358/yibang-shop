<template>
  <div class="layout-header">
    <!-- 左侧操作区 -->
    <div class="header-left">
      <el-button 
        type="text" 
        @click="$emit('toggle')"
        class="toggle-btn"
      >
        <el-icon size="18">
          <Fold v-if="!collapsed" />
          <Expand v-else />
        </el-icon>
      </el-button>

      <!-- 面包屑导航 -->
      <el-breadcrumb separator="/" class="breadcrumb">
        <el-breadcrumb-item
          v-for="item in breadcrumbList"
          :key="item.path"
          :to="item.path"
        >
          {{ item.title }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 右侧操作区 -->
    <div class="header-right">
      <!-- 全屏切换 -->
      <el-tooltip content="全屏" placement="bottom">
        <div class="header-action" @click="toggleFullscreen">
          <el-icon size="18">
            <FullScreen />
          </el-icon>
        </div>
      </el-tooltip>

      <!-- 刷新页面 -->
      <el-tooltip content="刷新" placement="bottom">
        <div class="header-action" @click="refreshPage">
          <el-icon size="18">
            <Refresh />
          </el-icon>
        </div>
      </el-tooltip>

      <!-- 通知消息 -->
      <el-dropdown trigger="click" class="notification-dropdown">
        <div class="header-action">
          <el-badge :value="notificationCount" :hidden="notificationCount === 0">
            <el-icon size="18">
              <Bell />
            </el-icon>
          </el-badge>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="notification-menu">
            <div class="notification-header">
              <span>通知消息</span>
              <el-button type="text" size="small" @click="markAllRead">
                全部已读
              </el-button>
            </div>
            <div class="notification-content">
              <div 
                v-for="item in notifications" 
                :key="item.id"
                class="notification-item"
                :class="{ 'is-read': item.isRead }"
                @click="handleNotificationClick(item)"
              >
                <div class="notification-title">{{ item.title }}</div>
                <div class="notification-time">{{ item.time }}</div>
              </div>
              <div v-if="notifications.length === 0" class="no-notifications">
                暂无新消息
              </div>
            </div>
            <div class="notification-footer">
              <el-button type="text" size="small" @click="viewAllNotifications">
                查看全部
              </el-button>
            </div>
          </el-dropdown-menu>
        </template>
      </el-dropdown>

      <!-- 用户信息 -->
      <el-dropdown trigger="click" class="user-dropdown">
        <div class="user-info">
          <el-avatar :size="32" :src="userInfo.avatar">
            <el-icon><User /></el-icon>
          </el-avatar>
          <span class="username">{{ userInfo.name }}</span>
          <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="viewProfile">
              <el-icon><User /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item @click="changePassword">
              <el-icon><Lock /></el-icon>
              修改密码
            </el-dropdown-item>
            <el-dropdown-item divided @click="handleLogout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Fold,
  Expand,
  FullScreen,
  Refresh,
  Bell,
  User,
  ArrowDown,
  Lock,
  SwitchButton
} from '@element-plus/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref({
  name: '管理员',
  avatar: '/avatar.jpg'
})

// 通知消息
const notifications = ref([
  {
    id: 1,
    title: '有新的提现申请待审核',
    time: '5分钟前',
    isRead: false
  },
  {
    id: 2,
    title: '系统配置已更新',
    time: '1小时前',
    isRead: false
  },
  {
    id: 3,
    title: '用户反馈处理完成',
    time: '2小时前',
    isRead: true
  }
])

// 通知数量
const notificationCount = computed(() => {
  return notifications.value.filter(item => !item.isRead).length
})

// 面包屑导航
const breadcrumbList = computed(() => {
  const matched = route.matched.filter(item => item.meta && item.meta.title)
  const breadcrumbs = []
  
  matched.forEach(item => {
    if (item.meta.title) {
      breadcrumbs.push({
        title: item.meta.title,
        path: item.path
      })
    }
  })
  
  return breadcrumbs
})

// 切换全屏
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 刷新页面
const refreshPage = () => {
  window.location.reload()
}

// 处理通知点击
const handleNotificationClick = (item) => {
  item.isRead = true
  // TODO: 跳转到相关页面
}

// 标记全部已读
const markAllRead = () => {
  notifications.value.forEach(item => {
    item.isRead = true
  })
  ElMessage.success('已标记全部消息为已读')
}

// 查看全部通知
const viewAllNotifications = () => {
  router.push('/system/notifications')
}

// 查看个人信息
const viewProfile = () => {
  // TODO: 打开个人信息模态框或跳转页面
  ElMessage.info('功能开发中')
}

// 修改密码
const changePassword = () => {
  // TODO: 打开修改密码模态框
  ElMessage.info('功能开发中')
}

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    // TODO: 调用登出接口
    localStorage.removeItem('admin_token')
    localStorage.removeItem('admin_user')
    
    ElMessage.success('已安全退出')
    router.push('/login')
  } catch {
    // 用户取消
  }
}

// 初始化
onMounted(() => {
  // TODO: 加载用户信息和通知
})
</script>

<style lang="scss" scoped>
.layout-header {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 $spacing-lg;
  background-color: $bg-color-container;
  border-bottom: 1px solid $border-color-split;
}

.header-left {
  display: flex;
  align-items: center;
  flex: 1;

  .toggle-btn {
    margin-right: $spacing-md;
    padding: $spacing-sm;
    
    &:hover {
      background-color: $primary-1;
    }
  }

  .breadcrumb {
    :deep(.el-breadcrumb__item) {
      .el-breadcrumb__inner {
        color: $text-color-secondary;
        
        &:hover {
          color: $primary-color;
        }
      }
      
      &:last-child .el-breadcrumb__inner {
        color: $text-color-primary;
        font-weight: $font-weight-medium;
      }
    }
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
}

.header-action {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: $border-radius-base;
  cursor: pointer;
  transition: all $motion-duration-fast $ease-out;

  &:hover {
    background-color: $primary-1;
    color: $primary-color;
  }
}

.notification-dropdown {
  .notification-menu {
    width: 300px;
    padding: 0;
  }

  .notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: $spacing-md;
    border-bottom: 1px solid $border-color-split;
    font-weight: $font-weight-medium;
  }

  .notification-content {
    max-height: 300px;
    overflow-y: auto;
  }

  .notification-item {
    padding: $spacing-md;
    border-bottom: 1px solid $border-color-split;
    cursor: pointer;
    transition: background-color $motion-duration-fast $ease-out;

    &:hover {
      background-color: $bg-color-component;
    }

    &.is-read {
      opacity: 0.6;
    }

    .notification-title {
      font-size: $font-size-base;
      color: $text-color-primary;
      margin-bottom: $spacing-xs;
    }

    .notification-time {
      font-size: $font-size-sm;
      color: $text-color-tertiary;
    }
  }

  .no-notifications {
    padding: $spacing-xl;
    text-align: center;
    color: $text-color-tertiary;
  }

  .notification-footer {
    padding: $spacing-sm $spacing-md;
    border-top: 1px solid $border-color-split;
    text-align: center;
  }
}

.user-dropdown {
  .user-info {
    display: flex;
    align-items: center;
    padding: $spacing-sm $spacing-md;
    border-radius: $border-radius-base;
    cursor: pointer;
    transition: all $motion-duration-fast $ease-out;

    &:hover {
      background-color: $primary-1;
    }

    .username {
      margin: 0 $spacing-sm;
      font-size: $font-size-base;
      color: $text-color-primary;
    }

    .dropdown-icon {
      font-size: 12px;
      color: $text-color-tertiary;
      transition: transform $motion-duration-fast $ease-out;
    }

    &:hover .dropdown-icon {
      color: $primary-color;
    }
  }
}
</style>
