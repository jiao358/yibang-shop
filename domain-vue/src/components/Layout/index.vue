<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="layout-sidebar" :class="{ 'is-collapsed': isCollapsed }">
      <Sidebar :collapsed="isCollapsed" @toggle="toggleSidebar" />
    </div>

    <!-- 主内容区 -->
    <div class="layout-main">
      <!-- 顶部导航 -->
      <div class="layout-header">
        <Header :collapsed="isCollapsed" @toggle="toggleSidebar" />
      </div>

      <!-- 内容区域 -->
      <div class="layout-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>

      <!-- 底部信息 -->
      <div class="layout-footer">
        <Footer />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
import Footer from './Footer.vue'

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background-color: $bg-color-body;
}

.layout-sidebar {
  width: $layout-sidebar-width;
  background-color: $bg-color-container;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  transition: width $motion-duration-mid $ease-out;
  overflow: hidden;
  z-index: $z-index-fixed;

  &.is-collapsed {
    width: $layout-sidebar-collapsed-width;
  }
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.layout-header {
  height: $layout-header-height;
  background-color: $bg-color-container;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  z-index: $z-index-sticky;
}

.layout-content {
  flex: 1;
  padding: $spacing-lg;
  overflow: auto;
  background-color: $bg-color-body;
}

.layout-footer {
  height: $layout-footer-height;
  background-color: $bg-color-container;
  border-top: 1px solid $border-color-split;
}
</style>
