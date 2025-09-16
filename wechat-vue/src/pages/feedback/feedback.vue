<template>
  <view class="feedback-page">
    <view class="feedback-content">
      <view class="feedback-header">
        <text class="feedback-title">意见反馈</text>
        <text class="feedback-desc">您的反馈是我们改进的动力</text>
      </view>

      <view class="feedback-form">
        <view class="form-group">
          <text class="form-label">反馈类型</text>
          <view class="feedback-types">
            <view 
              v-for="(type, index) in feedbackTypes" 
              :key="index"
              class="type-item"
              :class="{ active: selectedType === index }"
              @click="selectType(index)"
            >
              <text>{{ type }}</text>
            </view>
          </view>
        </view>

        <view class="form-group">
          <text class="form-label">问题描述 <text class="required">*</text></text>
          <textarea
            v-model="feedbackContent"
            class="feedback-textarea"
            placeholder="请详细描述您遇到的问题或建议..."
            maxlength="500"
          ></textarea>
          <text class="char-count">{{ feedbackContent.length }}/500</text>
        </view>

        <view class="form-group">
          <text class="form-label">联系方式</text>
          <input
            v-model="contact"
            class="feedback-input"
            placeholder="请留下您的联系方式（选填）"
            type="text"
          />
        </view>

        <view class="form-group">
          <text class="form-label">上传图片</text>
          <view class="image-upload">
            <view 
              v-for="(image, index) in uploadedImages" 
              :key="index"
              class="uploaded-image"
            >
              <image :src="image" mode="aspectFill"></image>
              <view class="delete-btn" @click="removeImage(index)">×</view>
            </view>
            <view v-if="uploadedImages.length < 3" class="upload-btn" @click="chooseImage">
              <text>+</text>
              <text>添加图片</text>
            </view>
          </view>
          <text class="upload-tip">最多上传3张图片，每张不超过2MB</text>
        </view>
      </view>

      <view class="feedback-actions">
        <button class="submit-btn" @click="submitFeedback" :disabled="!canSubmit">
          提交反馈
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, computed } from 'vue'

export default {
  name: 'FeedbackPage',
  setup() {
    const feedbackTypes = ref(['功能建议', '问题反馈', '界面优化', '其他'])
    const selectedType = ref(0)
    const feedbackContent = ref('')
    const contact = ref('')
    const uploadedImages = ref([])

    const canSubmit = computed(() => {
      return feedbackContent.value.trim().length >= 10
    })

    const selectType = (index) => {
      selectedType.value = index
    }

    const chooseImage = () => {
      uni.chooseImage({
        count: 3 - uploadedImages.value.length,
        sizeType: ['compressed'],
        sourceType: ['album', 'camera'],
        success: (res) => {
          uploadedImages.value.push(...res.tempFilePaths)
        }
      })
    }

    const removeImage = (index) => {
      uploadedImages.value.splice(index, 1)
    }

    const submitFeedback = () => {
      if (!canSubmit.value) {
        uni.showToast({
          title: '请详细描述问题（至少10个字）',
          icon: 'none'
        })
        return
      }

      uni.showLoading({
        title: '提交中...'
      })

      // TODO: 调用后端API提交反馈
      setTimeout(() => {
        uni.hideLoading()
        uni.showToast({
          title: '反馈提交成功',
          icon: 'success'
        })
        
        // 重置表单
        selectedType.value = 0
        feedbackContent.value = ''
        contact.value = ''
        uploadedImages.value = []
      }, 1500)
    }

    return {
      feedbackTypes,
      selectedType,
      feedbackContent,
      contact,
      uploadedImages,
      canSubmit,
      selectType,
      chooseImage,
      removeImage,
      submitFeedback
    }
  }
}
</script>

<style scoped>
.feedback-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.feedback-content {
  padding: 32rpx;
}

.feedback-header {
  text-align: center;
  margin-bottom: 48rpx;
}

.feedback-title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
}

.feedback-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
}

.feedback-form {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 32rpx;
}

.form-group {
  margin-bottom: 48rpx;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 16rpx;
}

.required {
  color: #FF6B6B;
}

.feedback-types {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.type-item {
  padding: 16rpx 32rpx;
  background: #F5F5F5;
  border-radius: 24rpx;
  font-size: 26rpx;
  color: #666666;
}

.type-item.active {
  background: #FF6B6B;
  color: #FFFFFF;
}

.feedback-textarea {
  width: 100%;
  min-height: 200rpx;
  padding: 24rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.5;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

.feedback-input {
  width: 100%;
  padding: 24rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
}

.image-upload {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.uploaded-image {
  position: relative;
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  overflow: hidden;
}

.uploaded-image image {
  width: 100%;
  height: 100%;
}

.delete-btn {
  position: absolute;
  top: -8rpx;
  right: -8rpx;
  width: 32rpx;
  height: 32rpx;
  background: #FF6B6B;
  color: #FFFFFF;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20rpx;
}

.upload-btn {
  width: 120rpx;
  height: 120rpx;
  background: #F5F5F5;
  border: 2rpx dashed #CCCCCC;
  border-radius: 12rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #999999;
}

.upload-tip {
  display: block;
  font-size: 24rpx;
  color: #999999;
  margin-top: 16rpx;
}

.feedback-actions {
  padding: 0 32rpx;
}

.submit-btn {
  width: 100%;
  padding: 24rpx;
  background: #FF6B6B;
  color: #FFFFFF;
  border-radius: 12rpx;
  font-size: 32rpx;
  border: none;
}

.submit-btn:disabled {
  background: #CCCCCC;
}
</style>
