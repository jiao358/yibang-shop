<template>
  <view id="app">
    <!-- 应用根组件 -->
  </view>
</template>

<script>
export default {
  name: 'App',
  onLaunch: function() {
    console.log('App Launch')
  },
  onShow: function() {
    console.log('App Show')
  },
  onHide: function() {
    console.log('App Hide')
  }
}
</script>

<style>
/* 全局样式 */
@import './static/styles/global.css';
</style>
