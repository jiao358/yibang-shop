<template>
  <view class="settings-page">
    <view class="settings-list">
      <view class="settings-item">
        <text class="settings-label">通用设置</text>
      </view>
      
      <view class="settings-item" @click="handleItemClick('language')">
        <text class="settings-label">语言设置</text>
        <view class="settings-right">
          <text class="settings-value">简体中文</text>
          <view class="arrow-icon">></view>
        </view>
      </view>
      
      <view class="settings-item" @click="handleItemClick('theme')">
        <text class="settings-label">主题设置</text>
        <view class="settings-right">
          <text class="settings-value">跟随系统</text>
          <view class="arrow-icon">></view>
        </view>
      </view>
      
      <view class="settings-item" @click="handleItemClick('cache')">
        <text class="settings-label">清理缓存</text>
        <view class="settings-right">
          <text class="settings-value">12.5MB</text>
          <view class="arrow-icon">></view>
        </view>
      </view>
      
      <view class="settings-item" @click="handleItemClick('about')">
        <text class="settings-label">关于我们</text>
        <view class="settings-right">
          <view class="arrow-icon">></view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'SettingsPage',
  setup() {
    const handleItemClick = (type) => {
      switch (type) {
        case 'language':
          uni.showToast({
            title: '功能开发中',
            icon: 'none'
          })
          break
        case 'theme':
          uni.showToast({
            title: '功能开发中',
            icon: 'none'
          })
          break
        case 'cache':
          uni.showModal({
            title: '清理缓存',
            content: '确定要清理缓存吗？',
            success: (res) => {
              if (res.confirm) {
                uni.showToast({
                  title: '清理成功',
                  icon: 'success'
                })
              }
            }
          })
          break
        case 'about':
          uni.navigateTo({
            url: '/pages/about/about'
          })
          break
      }
    }

    return {
      handleItemClick
    }
  }
}
</script>

<style scoped>
.settings-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.settings-list {
  background: #FFFFFF;
  margin-top: 20rpx;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.settings-item:last-child {
  border-bottom: none;
}

.settings-label {
  font-size: 32rpx;
  color: #333333;
}

.settings-right {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.settings-value {
  font-size: 28rpx;
  color: #666666;
}

.arrow-icon {
  font-size: 24rpx;
  color: #CCCCCC;
}
</style>
