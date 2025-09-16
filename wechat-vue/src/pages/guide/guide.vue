<template>
  <view class="guide-page">
    <view class="guide-content">
      <view class="guide-header">
        <text class="guide-title">新手必读</text>
        <text class="guide-desc">快速了解任务商城的使用方法</text>
      </view>

      <view class="guide-sections">
        <view class="guide-section">
          <view class="section-title">🎯 如何完成任务获得收益？</view>
          <view class="section-content">
            <text>1. 点击"任务"Tab，浏览可用任务</text>
            <text>2. 选择适合的任务，仔细阅读任务要求</text>
            <text>3. 按照要求完成任务，上传相关截图</text>
            <text>4. 等待审核通过，收益自动到账</text>
          </view>
        </view>

        <view class="guide-section">
          <view class="section-title">💰 如何提现收益？</view>
          <view class="section-content">
            <text>1. 在个人中心点击"余额"区域</text>
            <text>2. 选择"提现"功能</text>
            <text>3. 输入提现金额（最低10元）</text>
            <text>4. 收益将转入您的微信零钱</text>
          </view>
        </view>

        <view class="guide-section">
          <view class="section-title">🛒 如何享受集采价格？</view>
          <view class="section-content">
            <text>1. 完成任务提升用户等级</text>
            <text>2. 等级越高，享受的价格优惠越大</text>
            <text>3. 在商城购买商品享受1级代理价</text>
            <text>4. 邀请好友还可获得更多购买权益</text>
          </view>
        </view>

        <view class="guide-section">
          <view class="section-title">👥 如何邀请好友获得奖励？</view>
          <view class="section-content">
            <text>1. 点击个人中心的"邀请好友"</text>
            <text>2. 分享您的专属邀请码</text>
            <text>3. 好友注册后您将获得邀请奖励</text>
            <text>4. 好友完成任务您还可获得提成</text>
          </view>
        </view>
      </view>

      <view class="guide-tips">
        <view class="tips-title">💡 温馨提示</view>
        <text>• 保持良好的任务完成记录，提升信用等级</text>
        <text>• 及时提现收益，避免账户余额过多</text>
        <text>• 遵守平台规则，诚信完成每一个任务</text>
        <text>• 如有疑问，请联系客服获得帮助</text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  name: 'GuidePage'
}
</script>

<style scoped>
.guide-page {
  background: #F5F5F5;
  min-height: 100vh;
}

.guide-content {
  padding: 32rpx;
}

.guide-header {
  text-align: center;
  margin-bottom: 48rpx;
}

.guide-title {
  display: block;
  font-size: 40rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 16rpx;
}

.guide-desc {
  display: block;
  font-size: 28rpx;
  color: #666666;
}

.guide-sections {
  margin-bottom: 48rpx;
}

.guide-section {
  background: #FFFFFF;
  border-radius: 16rpx;
  padding: 32rpx;
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
}

.section-content {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.section-content text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
}

.guide-tips {
  background: #FFF7ED;
  border-radius: 16rpx;
  padding: 32rpx;
  border-left: 8rpx solid #FF6B6B;
}

.tips-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 24rpx;
}

.guide-tips text {
  display: block;
  font-size: 28rpx;
  color: #666666;
  line-height: 1.6;
  margin-bottom: 12rpx;
}

.guide-tips text:last-child {
  margin-bottom: 0;
}
</style>
