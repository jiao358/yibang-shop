<template>
  <view id="app">
    <!-- 全局组件 -->
  </view>
</template>

<script>
export default {
  name: 'App',
  onLaunch() {
    console.log('App Launch')
    // 初始化应用
    this.initApp()
  },
  onShow() {
    console.log('App Show')
  },
  onHide() {
    console.log('App Hide')
  },
  methods: {
    initApp() {
      // 检查登录状态
      this.checkLoginStatus()
      // 初始化用户信息
      this.initUserInfo()
    },
    
    checkLoginStatus() {
      // 检查微信登录状态
      const token = uni.getStorageSync('token')
      if (!token) {
        // 未登录，跳转到登录页面
        console.log('用户未登录')
      }
    },
    
    initUserInfo() {
      // 初始化用户信息
      console.log('初始化用户信息')
    }
  }
}
</script>

<style>
/* 全局样式 */
@import url('./static/styles/global.css');

page {
  background-color: #F5F5F5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial, sans-serif;
}

/* 通用样式 */
.container {
  padding: 20rpx;
}

.card {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.btn-primary {
  background: linear-gradient(135deg, #FF6B6B 0%, #FF8E8E 100%);
  color: #FFFFFF;
  border-radius: 24rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.btn-secondary {
  background: #F8F9FA;
  color: #6C757D;
  border: 2rpx solid #E9ECEF;
  border-radius: 24rpx;
  padding: 20rpx 40rpx;
  font-size: 28rpx;
  font-weight: 500;
}

.text-primary {
  color: #FF6B6B;
}

.text-secondary {
  color: #6C757D;
}

.text-success {
  color: #28A745;
}

.text-warning {
  color: #FFC107;
}

.text-danger {
  color: #DC3545;
}
</style>
