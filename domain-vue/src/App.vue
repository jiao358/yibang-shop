<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'

// 全局错误处理
window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled promise rejection:', event.reason)
  ElMessage.error('系统异常，请稍后重试')
})

// 全局错误处理
window.addEventListener('error', (event) => {
  console.error('Global error:', event.error)
})
</script>

<style lang="scss">
@import './styles/index.scss';

#app {
  height: 100vh;
  overflow: hidden;
}
</style>
