<template>
  <view class="order-submit-page">
    <!-- 状态栏 -->
    <view class="status-bar">
      <text class="time">17:29</text>
      <view class="battery">
        <text class="battery-text">100</text>
        <view class="battery-bar">
          <view class="battery-fill"></view>
        </view>
      </view>
    </view>

    <!-- 头部 -->
    <view class="header">
      <view class="header-left" @click="goBack">
        <image src="/static/icons/order.png" class="back-icon" mode="aspectFit"></image>
      </view>
      <text class="header-title">确认订单</text>
      <view class="header-right"></view>
    </view>

    <!-- 收货地址 -->
    <view class="address-section" @click="selectAddress">
      <view class="address-header">
        <image src="/static/icons/home.png" class="location-icon" mode="aspectFit"></image>
        <text class="address-title">收货地址</text>
        <image src="/static/icons/arrow-right.png" class="arrow-icon" mode="aspectFit"></image>
      </view>
      <view v-if="selectedAddress" class="address-content">
        <view class="address-info">
          <text class="name">{{ selectedAddress.name }}</text>
          <text class="phone">{{ selectedAddress.phone }}</text>
        </view>
        <text class="address-detail">{{ selectedAddress.fullAddress }}</text>
      </view>
      <view v-else class="no-address">
        <text class="no-address-text">请选择收货地址</text>
      </view>
    </view>

    <!-- 商品信息 -->
    <view class="product-section">
      <view class="section-header">
        <text class="section-title">商品信息</text>
      </view>
      <view class="product-list">
        <view v-for="item in orderItems" :key="item.id" class="product-item">
          <image :src="item.image" class="product-image" mode="aspectFill"></image>
          <view class="product-info">
            <text class="product-name">{{ item.name }}</text>
            <text class="product-spec">{{ item.spec }}</text>
            <view class="product-price-row">
              <text class="product-price">¥{{ item.price }}</text>
              <text class="product-quantity">x{{ item.quantity }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 配送方式 -->
    <view class="delivery-section">
      <view class="section-header">
        <text class="section-title">配送方式</text>
      </view>
      <view class="delivery-options">
        <view 
          v-for="option in deliveryOptions" 
          :key="option.id"
          class="delivery-option"
          :class="{ active: selectedDelivery === option.id }"
          @click="selectDelivery(option.id)"
        >
          <view class="delivery-info">
            <text class="delivery-name">{{ option.name }}</text>
            <text class="delivery-desc">{{ option.desc }}</text>
          </view>
          <view class="delivery-price">
            <text class="price-text">¥{{ option.price }}</text>
          </view>
          <view class="radio-btn" :class="{ checked: selectedDelivery === option.id }">
            <image v-if="selectedDelivery === option.id" src="/static/icons/check.png" class="check-icon" mode="aspectFit"></image>
          </view>
        </view>
      </view>
    </view>

    <!-- 优惠券 -->
    <view class="coupon-section" @click="selectCoupon">
      <view class="coupon-header">
        <image src="/static/icons/mall.png" class="coupon-icon" mode="aspectFit"></image>
        <text class="coupon-title">优惠券</text>
        <view class="coupon-info">
          <text v-if="selectedCoupon" class="coupon-name">{{ selectedCoupon.name }}</text>
          <text v-else class="coupon-placeholder">选择优惠券</text>
          <image src="/static/icons/arrow-right.png" class="arrow-icon" mode="aspectFit"></image>
        </view>
      </view>
    </view>

    <!-- 订单备注 -->
    <view class="remark-section">
      <view class="section-header">
        <text class="section-title">订单备注</text>
      </view>
      <textarea 
        v-model="orderRemark" 
        class="remark-input" 
        placeholder="请输入订单备注（选填）"
        maxlength="100"
      />
    </view>

    <!-- 费用明细 -->
    <view class="cost-section">
      <view class="section-header">
        <text class="section-title">费用明细</text>
      </view>
      <view class="cost-list">
        <view class="cost-item">
          <text class="cost-label">商品金额</text>
          <text class="cost-value">¥{{ orderSummary.productTotal }}</text>
        </view>
        <view class="cost-item">
          <text class="cost-label">配送费</text>
          <text class="cost-value">¥{{ orderSummary.deliveryFee }}</text>
        </view>
        <view v-if="orderSummary.couponDiscount > 0" class="cost-item">
          <text class="cost-label">优惠券</text>
          <text class="cost-value discount">-¥{{ orderSummary.couponDiscount }}</text>
        </view>
        <view class="cost-item total">
          <text class="cost-label">实付金额</text>
          <text class="cost-value total-price">¥{{ orderSummary.totalAmount }}</text>
        </view>
      </view>
    </view>

    <!-- 底部提交栏 -->
    <view class="submit-bar">
      <view class="total-info">
        <text class="total-label">实付：</text>
        <text class="total-amount">¥{{ orderSummary.totalAmount }}</text>
      </view>
      <button class="submit-btn" @click="submitOrder" :disabled="!canSubmit">
        {{ canSubmit ? '提交订单' : '请完善信息' }}
      </button>
    </view>

    <!-- 地址选择弹窗 -->
    <view v-if="showAddressPicker" class="address-picker-modal">
      <view class="modal-mask" @click="closeAddressPicker"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">选择收货地址</text>
          <view class="close-btn" @click="closeAddressPicker">
            <image src="/static/icons/settings.png" class="close-icon" mode="aspectFit"></image>
          </view>
        </view>
        <view class="address-list">
          <view 
            v-for="address in addressList" 
            :key="address.id"
            class="address-item"
            :class="{ selected: selectedAddress && selectedAddress.id === address.id }"
            @click="selectAddressItem(address)"
          >
            <view class="address-content">
              <view class="address-header">
                <text class="name">{{ address.name }}</text>
                <text class="phone">{{ address.phone }}</text>
                <view v-if="address.isDefault" class="default-tag">默认</view>
              </view>
              <text class="address-detail">{{ address.fullAddress }}</text>
            </view>
            <view class="radio-btn" :class="{ checked: selectedAddress && selectedAddress.id === address.id }">
              <image v-if="selectedAddress && selectedAddress.id === address.id" src="/static/icons/check.png" class="check-icon" mode="aspectFit"></image>
            </view>
          </view>
        </view>
        <view class="modal-actions">
          <button class="add-address-btn" @click="addNewAddress">新增地址</button>
        </view>
      </view>
    </view>

    <!-- 优惠券选择弹窗 -->
    <view v-if="showCouponPicker" class="coupon-picker-modal">
      <view class="modal-mask" @click="closeCouponPicker"></view>
      <view class="modal-content">
        <view class="modal-header">
          <text class="modal-title">选择优惠券</text>
          <view class="close-btn" @click="closeCouponPicker">
            <image src="/static/icons/settings.png" class="close-icon" mode="aspectFit"></image>
          </view>
        </view>
        <view class="coupon-list">
          <view 
            v-for="coupon in availableCoupons" 
            :key="coupon.id"
            class="coupon-item"
            :class="{ selected: selectedCoupon && selectedCoupon.id === coupon.id }"
            @click="selectCouponItem(coupon)"
          >
            <view class="coupon-content">
              <view class="coupon-info">
                <text class="coupon-name">{{ coupon.name }}</text>
                <text class="coupon-desc">{{ coupon.desc }}</text>
                <text class="coupon-expire">有效期至：{{ coupon.expireTime }}</text>
              </view>
              <view class="coupon-discount">
                <text class="discount-amount">¥{{ coupon.discountAmount }}</text>
              </view>
            </view>
            <view class="radio-btn" :class="{ checked: selectedCoupon && selectedCoupon.id === coupon.id }">
              <image v-if="selectedCoupon && selectedCoupon.id === coupon.id" src="/static/icons/check.png" class="check-icon" mode="aspectFit"></image>
            </view>
          </view>
        </view>
        <view class="modal-actions">
          <button class="no-coupon-btn" @click="selectCouponItem(null)">不使用优惠券</button>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useAddressStore } from '@/stores/address'

export default {
  name: 'OrderSubmitPage',
  setup() {
    const addressStore = useAddressStore()
    
    // 响应式数据
    const selectedAddress = ref(null)
    const selectedDelivery = ref(1)
    const selectedCoupon = ref(null)
    const orderRemark = ref('')
    const showAddressPicker = ref(false)
    const showCouponPicker = ref(false)
    
    // 订单商品数据
    const orderItems = ref([
      {
        id: 1,
        name: '苹果iPhone 15 Pro Max',
        spec: '深空黑色 256GB',
        price: '9999.00',
        quantity: 1,
        image: '/static/images/product1.jpg'
      },
      {
        id: 2,
        name: 'AirPods Pro 2',
        spec: '白色',
        price: '1899.00',
        quantity: 2,
        image: '/static/images/product2.jpg'
      }
    ])
    
    // 配送方式
    const deliveryOptions = ref([
      {
        id: 1,
        name: '标准配送',
        desc: '预计3-5天送达',
        price: '0.00'
      },
      {
        id: 2,
        name: '加急配送',
        desc: '预计1-2天送达',
        price: '15.00'
      },
      {
        id: 3,
        name: '极速配送',
        desc: '预计当日送达',
        price: '30.00'
      }
    ])
    
    // 地址列表
    const addressList = ref([
      {
        id: 1,
        name: '张三',
        phone: '138****8888',
        region: '北京市 朝阳区',
        detail: '三里屯街道工体北路8号院',
        fullAddress: '北京市 朝阳区 三里屯街道工体北路8号院',
        isDefault: true
      },
      {
        id: 2,
        name: '李四',
        phone: '139****9999',
        region: '上海市 浦东新区',
        detail: '陆家嘴环路1000号恒生银行大厦',
        fullAddress: '上海市 浦东新区 陆家嘴环路1000号恒生银行大厦',
        isDefault: false
      }
    ])
    
    // 可用优惠券
    const availableCoupons = ref([
      {
        id: 1,
        name: '新用户专享券',
        desc: '满1000减100',
        discountAmount: 100,
        expireTime: '2024-12-31'
      },
      {
        id: 2,
        name: '会员专享券',
        desc: '满2000减200',
        discountAmount: 200,
        expireTime: '2024-12-31'
      }
    ])
    
    // 计算订单汇总
    const orderSummary = computed(() => {
      const productTotal = orderItems.value.reduce((sum, item) => {
        return sum + (parseFloat(item.price) * item.quantity)
      }, 0)
      
      const deliveryFee = parseFloat(deliveryOptions.value.find(option => option.id === selectedDelivery.value)?.price || 0)
      
      const couponDiscount = selectedCoupon.value ? selectedCoupon.value.discountAmount : 0
      
      const totalAmount = productTotal + deliveryFee - couponDiscount
      
      return {
        productTotal: productTotal.toFixed(2),
        deliveryFee: deliveryFee.toFixed(2),
        couponDiscount: couponDiscount,
        totalAmount: totalAmount.toFixed(2)
      }
    })
    
    // 是否可以提交订单
    const canSubmit = computed(() => {
      return selectedAddress.value !== null
    })
    
    // 返回上一页
    const goBack = () => {
      uni.navigateBack()
    }
    
    // 选择地址
    const selectAddress = () => {
      showAddressPicker.value = true
    }
    
    // 关闭地址选择器
    const closeAddressPicker = () => {
      showAddressPicker.value = false
    }
    
    // 选择地址项
    const selectAddressItem = (address) => {
      selectedAddress.value = address
      closeAddressPicker()
    }
    
    // 新增地址
    const addNewAddress = () => {
      closeAddressPicker()
      uni.navigateTo({
        url: '/pages/address-list/address-list'
      })
    }
    
    // 选择配送方式
    const selectDelivery = (deliveryId) => {
      selectedDelivery.value = deliveryId
    }
    
    // 选择优惠券
    const selectCoupon = () => {
      showCouponPicker.value = true
    }
    
    // 关闭优惠券选择器
    const closeCouponPicker = () => {
      showCouponPicker.value = false
    }
    
    // 选择优惠券项
    const selectCouponItem = (coupon) => {
      selectedCoupon.value = coupon
      closeCouponPicker()
    }
    
    // 提交订单
    const submitOrder = () => {
      if (!canSubmit.value) {
        uni.showToast({
          title: '请选择收货地址',
          icon: 'none'
        })
        return
      }
      
      const orderData = {
        address: selectedAddress.value,
        items: orderItems.value,
        delivery: deliveryOptions.value.find(option => option.id === selectedDelivery.value),
        coupon: selectedCoupon.value,
        remark: orderRemark.value,
        summary: orderSummary.value
      }
      
      console.log('提交订单数据:', orderData)
      
      uni.showLoading({
        title: '提交中...'
      })
      
      // 模拟提交订单
      setTimeout(() => {
        uni.hideLoading()
        uni.showToast({
          title: '订单提交成功',
          icon: 'success'
        })
        
        // 跳转到订单详情或支付页面
        setTimeout(() => {
          uni.redirectTo({
            url: '/pages/order/order'
          })
        }, 1500)
      }, 2000)
    }
    
    onMounted(() => {
      // 加载地址列表
      loadAddressList()
      
      // 设置默认地址
      const defaultAddress = addressList.value.find(addr => addr.isDefault)
      if (defaultAddress) {
        selectedAddress.value = defaultAddress
      }
    })
    
    // 加载地址列表
    const loadAddressList = async () => {
      try {
        await addressStore.getAddressList()
        if (addressStore.addressList.length > 0) {
          addressList.value = addressStore.addressList
        }
      } catch (error) {
        console.error('加载地址列表失败:', error)
      }
    }
    
    return {
      selectedAddress,
      selectedDelivery,
      selectedCoupon,
      orderRemark,
      showAddressPicker,
      showCouponPicker,
      orderItems,
      deliveryOptions,
      addressList,
      availableCoupons,
      orderSummary,
      canSubmit,
      goBack,
      selectAddress,
      closeAddressPicker,
      selectAddressItem,
      addNewAddress,
      selectDelivery,
      selectCoupon,
      closeCouponPicker,
      selectCouponItem,
      submitOrder
    }
  }
}
</script>

<style scoped>
.order-submit-page {
  background: #F5F5F5;
  min-height: 100vh;
  padding-bottom: 120rpx;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8rpx 32rpx;
  background: #FFFFFF;
  font-size: 24rpx;
  color: #666666;
}

.battery {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.battery-bar {
  width: 32rpx;
  height: 16rpx;
  border: 1rpx solid #999999;
  border-radius: 4rpx;
  overflow: hidden;
}

.battery-fill {
  width: 100%;
  height: 100%;
  background: #4CAF50;
  border-radius: 2rpx;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 32rpx;
  background: #FFFFFF;
  border-bottom: 1rpx solid #F0F0F0;
}

.header-left {
  display: flex;
  align-items: center;
}

.back-icon {
  width: 40rpx;
  height: 40rpx;
}

.header-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.header-right {
  width: 40rpx;
}

.address-section {
  background: #FFFFFF;
  margin: 24rpx 32rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
}

.location-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 12rpx;
}

.address-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  flex: 1;
}

.arrow-icon {
  width: 24rpx;
  height: 24rpx;
}

.address-content {
  margin-top: 16rpx;
}

.address-info {
  display: flex;
  align-items: center;
  margin-bottom: 8rpx;
}

.name {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-right: 16rpx;
}

.phone {
  font-size: 28rpx;
  color: #666666;
}

.address-detail {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.5;
}

.no-address {
  margin-top: 16rpx;
  padding: 24rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  text-align: center;
}

.no-address-text {
  font-size: 28rpx;
  color: #999999;
}

.product-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.section-header {
  margin-bottom: 24rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.product-item {
  display: flex;
  gap: 24rpx;
}

.product-image {
  width: 120rpx;
  height: 120rpx;
  border-radius: 12rpx;
  background: #F5F5F5;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 8rpx;
}

.product-spec {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 16rpx;
}

.product-price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.product-price {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.product-quantity {
  font-size: 24rpx;
  color: #666666;
}

.delivery-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.delivery-options {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.delivery-option {
  display: flex;
  align-items: center;
  padding: 24rpx;
  border: 2rpx solid #F0F0F0;
  border-radius: 12rpx;
  transition: all 0.3s;
}

.delivery-option.active {
  border-color: #FF6B6B;
  background: #FFF5F5;
}

.delivery-info {
  flex: 1;
  margin-right: 16rpx;
}

.delivery-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 4rpx;
}

.delivery-desc {
  font-size: 24rpx;
  color: #999999;
}

.delivery-price {
  margin-right: 16rpx;
}

.price-text {
  font-size: 28rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.radio-btn {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #DDDDDD;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.radio-btn.checked {
  border-color: #FF6B6B;
  background: #FF6B6B;
}

.check-icon {
  width: 24rpx;
  height: 24rpx;
}

.coupon-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.coupon-header {
  display: flex;
  align-items: center;
}

.coupon-icon {
  width: 32rpx;
  height: 32rpx;
  margin-right: 12rpx;
}

.coupon-title {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  flex: 1;
}

.coupon-info {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.coupon-name {
  font-size: 24rpx;
  color: #FF6B6B;
}

.coupon-placeholder {
  font-size: 24rpx;
  color: #999999;
}

.remark-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.remark-input {
  width: 100%;
  min-height: 120rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #333333;
  border: none;
  resize: none;
}

.cost-section {
  background: #FFFFFF;
  margin: 0 32rpx 24rpx;
  border-radius: 16rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.cost-list {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.cost-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cost-item.total {
  padding-top: 16rpx;
  border-top: 1rpx solid #F0F0F0;
  margin-top: 8rpx;
}

.cost-label {
  font-size: 28rpx;
  color: #666666;
}

.cost-value {
  font-size: 28rpx;
  color: #333333;
}

.cost-value.discount {
  color: #FF6B6B;
}

.cost-value.total-price {
  font-size: 32rpx;
  font-weight: 600;
  color: #FF6B6B;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #FFFFFF;
  padding: 24rpx 32rpx;
  border-top: 1rpx solid #F0F0F0;
  display: flex;
  align-items: center;
  gap: 24rpx;
  z-index: 100;
}

.total-info {
  flex: 1;
  display: flex;
  align-items: center;
}

.total-label {
  font-size: 28rpx;
  color: #666666;
}

.total-amount {
  font-size: 36rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.submit-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  font-weight: 600;
  border: none;
}

.submit-btn:disabled {
  background: #CCCCCC;
  color: #999999;
}

.address-picker-modal,
.coupon-picker-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  align-items: flex-end;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: #FFFFFF;
  border-radius: 24rpx 24rpx 0 0;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.modal-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #333333;
}

.close-btn {
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-icon {
  width: 32rpx;
  height: 32rpx;
}

.address-list,
.coupon-list {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0 32rpx;
}

.address-item,
.coupon-item {
  display: flex;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #F0F0F0;
}

.address-item:last-child,
.coupon-item:last-child {
  border-bottom: none;
}

.address-item.selected,
.coupon-item.selected {
  background: #FFF5F5;
  margin: 0 -32rpx;
  padding: 24rpx 32rpx;
}

.address-content,
.coupon-content {
  flex: 1;
  margin-right: 16rpx;
}

.coupon-info {
  margin-bottom: 8rpx;
}

.coupon-name {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
  margin-bottom: 4rpx;
}

.coupon-desc {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 4rpx;
}

.coupon-expire {
  font-size: 20rpx;
  color: #CCCCCC;
}

.coupon-discount {
  margin-right: 16rpx;
}

.discount-amount {
  font-size: 32rpx;
  color: #FF6B6B;
  font-weight: 600;
}

.modal-actions {
  padding: 32rpx;
  border-top: 1rpx solid #F0F0F0;
}

.add-address-btn,
.no-coupon-btn {
  width: 100%;
  height: 80rpx;
  background: #F5F5F5;
  color: #666666;
  border-radius: 12rpx;
  font-size: 28rpx;
  border: none;
}

.add-address-btn {
  background: #FF6B6B;
  color: #FFFFFF;
}
</style>
