<template>
  <view class="service-page">
    <view class="service-content">
      <!-- 客服标题 -->
      <view class="service-header">
        <text class="service-title">{{ serviceConfig.title || '联系客服' }}</text>
        <text class="service-desc">{{ serviceConfig.description || '如有任何问题，请联系客服' }}</text>
      </view>

      <!-- 客服二维码 -->
      <view class="qr-section" v-if="serviceConfig.qrCode">
        <view class="qr-container">
          <image :src="serviceConfig.qrCode" class="qr-image" mode="aspectFit"></image>
          <text class="qr-text">扫码添加客服微信</text>
        </view>
      </view>

      <!-- 联系方式 -->
      <view class="contact-section">
        <view class="contact-item" v-if="serviceConfig.workTime">
          <view class="contact-icon">🕒</view>
          <text class="contact-text">{{ serviceConfig.workTime }}</text>
        </view>
        
        <view class="contact-item" v-if="serviceConfig.phone" @click="callPhone">
          <view class="contact-icon">📞</view>
          <text class="contact-text">{{ serviceConfig.phone }}</text>
        </view>
        
        <view class="contact-item" v-if="serviceConfig.email" @click="copyEmail">
          <view class="contact-icon">📧</view>
          <text class="contact-text">{{ serviceConfig.email }}</text>
        </view>
      </view>

      <!-- 常见问题 -->
      <view class="faq-section">
        <text class="faq-title">常见问题</text>
        <view class="faq-list">
          <view class="faq-item" @click="toggleFaq(0)">
            <view class="faq-question">
              <text>如何完成任务获得收益？</text>
              <view class="faq-arrow" :class="{ active: activeFaq === 0 }">></view>
            </view>
            <view class="faq-answer" v-if="activeFaq === 0">
              <text>在任务页面选择合适的任务，按照要求完成后即可获得相应收益。</text>
            </view>
          </view>
          
          <view class="faq-item" @click="toggleFaq(1)">
            <view class="faq-question">
              <text>如何提现收益？</text>
              <view class="faq-arrow" :class="{ active: activeFaq === 1 }">></view>
            </view>
            <view class="faq-answer" v-if="activeFaq === 1">
              <text>在个人中心点击余额，然后选择提现即可将收益提现到微信零钱。</text>
            </view>
          </view>
          
          <view class="faq-item" @click="toggleFaq(2)">
            <view class="faq-question">
              <text>如何邀请好友获得奖励？</text>
              <view class="faq-arrow" :class="{ active: activeFaq === 2 }">></view>
            </view>
            <view class="faq-answer" v-if="activeFaq === 2">
              <text>分享您的邀请码给好友，好友注册后您将获得邀请奖励。</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { request } from '@/api/request'

export default {
  name: 'ServicePage',
  setup() {
    const serviceConfig = ref({})
    const activeFaq = ref(-1)

    // 获取客服配置
    const loadServiceConfig = async () => {
      try {
        const response = await request({
          url: '/system/config/customer-service',
          method: 'GET'
        })
        serviceConfig.value = response.data || {}
      } catch (error) {
        console.error('获取客服配置失败:', error)
        // 使用默认配置
        serviceConfig.value = {
          title: '联系客服',
          description: '如有任何问题，请联系客服，我们将竭诚为您服务！',
          workTime: '工作时间：周一至周日 9:00-21:00',
          phone: '400-123-4567',
          email: 'service@yibang-taskmall.com'
        }
      }
    }

    // 拨打电话
    const callPhone = () => {
      if (serviceConfig.value.phone) {
        uni.makePhoneCall({
          phoneNumber: serviceConfig.value.phone
        })
      }
    }

    // 复制邮箱
    const copyEmail = () => {
      if (serviceConfig.value.email) {
        uni.setClipboardData({
          data: serviceConfig.value.email,
          success: () => {
            uni.showToast({
              title: '已复制邮箱地址',
              icon: 'success'
            })
          }
        })
      }
    }

    // 切换FAQ显示
    const toggleFaq = (index) => {
      activeFaq.value = activeFaq.value === index ? -1 : index
    }

    onMounted(() => {
      loadServiceConfig()
    })

    return {
      serviceConfig,
      activeFaq,
      callPhone,
      copyEmail,
      toggleFaq
    }
  }
}
</script>

<style scoped>
.service-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.service-content {
  padding: 32rpx;
}

.service-header {
  text-align: center;
  margin-bottom: 48rpx;
}

.service-title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
}

.service-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
  line-height: 1.5;
}

.qr-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 48rpx;
  margin-bottom: 32rpx;
  text-align: center;
}

.qr-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.qr-image {
  width: 300rpx;
  height: 300rpx;
  margin-bottom: 24rpx;
}

.qr-text {
  font-size: 28rpx;
  color: #666666;
}

.contact-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.contact-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-icon {
  font-size: 36rpx;
  margin-right: 24rpx;
  width: 48rpx;
  text-align: center;
}

.contact-text {
  font-size: 28rpx;
  color: #333333;
  flex: 1;
}

.faq-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
}

.faq-title {
  display: block;
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
}

.faq-item {
  border-bottom: 1rpx solid #F0F0F0;
}

.faq-item:last-child {
  border-bottom: none;
}

.faq-question {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
  font-size: 28rpx;
  color: #333333;
}

.faq-arrow {
  font-size: 24rpx;
  color: #CCCCCC;
  transform: rotate(0deg);
  transition: transform 0.3s ease;
}

.faq-arrow.active {
  transform: rotate(90deg);
}

.faq-answer {
  padding: 0 0 24rpx 0;
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
}
</style>
