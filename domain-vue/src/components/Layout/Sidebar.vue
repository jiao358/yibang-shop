<template>
  <div class="sidebar">
    <!-- Logo 区域 -->
    <div class="sidebar-logo">
      <div class="logo-content" :class="{ 'is-collapsed': collapsed }">
        <img src="/logo.png" alt="Logo" class="logo-image" />
        <span v-if="!collapsed" class="logo-text">任务商城后台</span>
      </div>
    </div>

    <!-- 菜单区域 -->
    <div class="sidebar-menu">
      <el-menu
        :default-active="$route.path"
        :collapse="collapsed"
        :unique-opened="true"
        router
        class="menu"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <!-- 单级菜单 -->
          <el-menu-item 
            v-if="!route.children || route.children.length === 1" 
            :index="getMenuIndex(route)"
            :class="{ 'is-active': isActive(route) }"
          >
            <el-icon>
              <component :is="route.meta.icon" />
            </el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>

          <!-- 多级菜单 -->
          <el-sub-menu 
            v-else 
            :index="route.path"
            :class="{ 'is-active': isParentActive(route) }"
          >
            <template #title>
              <el-icon>
                <component :is="route.meta.icon" />
              </el-icon>
              <span>{{ route.meta.title }}</span>
            </template>
            
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="route.path + '/' + child.path"
              :class="{ 'is-active': isActive(child, route.path) }"
            >
              <template #title>{{ child.meta.title }}</template>
            </el-menu-item>
          </el-sub-menu>
        </template>
      </el-menu>
    </div>

    <!-- 底部操作 -->
    <div class="sidebar-footer">
      <el-tooltip content="折叠菜单" placement="right" :disabled="!collapsed">
        <div class="toggle-btn" @click="$emit('toggle')">
          <el-icon>
            <Fold v-if="!collapsed" />
            <Expand v-else />
          </el-icon>
        </div>
      </el-tooltip>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useRouter } from 'vue-router'
import { Fold, Expand } from '@element-plus/icons-vue'

defineProps({
  collapsed: {
    type: Boolean,
    default: false
  }
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()

// 获取菜单路由
const menuRoutes = computed(() => {
  return router.options.routes.filter(route => {
    return route.meta && route.meta.title && !route.meta.hidden && route.path !== '/login'
  })
})

// 获取菜单索引
const getMenuIndex = (route) => {
  if (route.children && route.children.length === 1) {
    return route.path + '/' + route.children[0].path
  }
  return route.path
}

// 判断菜单是否激活
const isActive = (menuRoute, parentPath = '') => {
  const fullPath = parentPath ? `${parentPath}/${menuRoute.path}` : getMenuIndex(menuRoute)
  return route.path === fullPath || route.path.startsWith(fullPath + '/')
}

// 判断父菜单是否激活
const isParentActive = (parentRoute) => {
  return route.path.startsWith(parentRoute.path + '/')
}
</script>

<style lang="scss" scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: $bg-color-container;
}

.sidebar-logo {
  height: $layout-header-height;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid $border-color-split;
  padding: 0 $spacing-md;

  .logo-content {
    display: flex;
    align-items: center;
    transition: all $motion-duration-mid $ease-out;

    &.is-collapsed {
      justify-content: center;
    }
  }

  .logo-image {
    width: 32px;
    height: 32px;
    margin-right: $spacing-sm;
    transition: margin $motion-duration-mid $ease-out;

    .is-collapsed & {
      margin-right: 0;
    }
  }

  .logo-text {
    font-size: $font-size-h4;
    font-weight: $font-weight-semibold;
    color: $primary-color;
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;

  :deep(.el-menu) {
    border-right: none;
    background-color: transparent;

    .el-menu-item,
    .el-sub-menu__title {
      height: 48px;
      line-height: 48px;
      margin: 2px $spacing-sm;
      border-radius: $border-radius-base;
      transition: all $motion-duration-fast $ease-out;

      &:hover {
        background-color: $primary-1 !important;
        color: $primary-color !important;
      }

      &.is-active {
        background-color: $primary-1 !important;
        color: $primary-color !important;
        font-weight: $font-weight-medium;

        &::after {
          content: '';
          position: absolute;
          right: 8px;
          top: 50%;
          transform: translateY(-50%);
          width: 3px;
          height: 20px;
          background-color: $primary-color;
          border-radius: 2px;
        }
      }
    }

    .el-sub-menu {
      .el-menu-item {
        padding-left: 48px !important;
        
        &::after {
          right: 8px;
        }
      }

      &.is-active > .el-sub-menu__title {
        color: $primary-color !important;
        background-color: $primary-1 !important;
      }
    }

    .el-menu-item-group__title {
      padding: 0;
    }

    &.el-menu--collapse {
      .el-menu-item,
      .el-sub-menu__title {
        margin: 2px 8px;
        text-align: center;
      }
    }
  }
}

.sidebar-footer {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid $border-color-split;

  .toggle-btn {
    width: 32px;
    height: 32px;
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
}
</style>
