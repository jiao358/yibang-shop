<template>
  <view class="address-page">
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
        <image src="/static/icons/arrow-left.svg" class="back-icon" mode="aspectFit"></image>
      </view>
      <text class="header-title">收货地址</text>
      <view class="header-right" @click="addAddress">
        <text class="add-text">新增</text>
      </view>
    </view>

    <!-- 地址列表 -->
    <view class="address-list">
      <view v-if="addressList.length === 0" class="empty-state">
        <image src="/static/images/empty-address.svg" class="empty-image" mode="aspectFit"></image>
        <text class="empty-text">暂无收货地址</text>
        <text class="empty-desc">点击右上角"新增"添加收货地址</text>
        <button class="add-btn" @click="addAddress">添加地址</button>
      </view>
      
      <view v-else>
        <view 
          v-for="address in addressList" 
          :key="address.id"
          class="address-item"
          @click="selectAddress(address)"
        >
          <view class="address-content">
            <view class="address-header">
              <text class="name">{{ address.name }}</text>
              <text class="phone">{{ address.phone }}</text>
              <view v-if="address.isDefault" class="default-tag">默认</view>
            </view>
            <view class="address-detail">
              <text class="address-text">{{ address.fullAddress }}</text>
            </view>
          </view>
          <view class="address-actions">
            <view class="action-btn" @click.stop="editAddress(address)">
              <image src="/static/icons/edit.svg" class="action-icon" mode="aspectFit"></image>
              <text>编辑</text>
            </view>
            <view class="action-btn" @click.stop="deleteAddress(address.id)">
              <image src="/static/icons/delete.svg" class="action-icon" mode="aspectFit"></image>
              <text>删除</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 新增/编辑地址弹窗 -->
    <view v-if="showAddressForm" class="address-form-modal">
      <view class="modal-mask" @click="closeAddressForm"></view>
      <view class="modal-content">
        <view class="form-header">
          <text class="form-title">{{ isEdit ? '编辑地址' : '新增地址' }}</text>
          <view class="close-btn" @click="closeAddressForm">
            <image src="/static/icons/close.svg" class="close-icon" mode="aspectFit"></image>
          </view>
        </view>
        
        <view class="form-content">
          <view class="form-item">
            <text class="form-label">收货人</text>
            <input 
              v-model="formData.name" 
              class="form-input" 
              placeholder="请输入收货人姓名"
              maxlength="20"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">手机号</text>
            <input 
              v-model="formData.phone" 
              class="form-input" 
              placeholder="请输入手机号"
              type="number"
              maxlength="11"
            />
          </view>
          
          <view class="form-item">
            <text class="form-label">所在地区</text>
            <view class="region-selector" @click="openRegionPicker">
              <text class="region-text">{{ formData.region || '请选择省市区' }}</text>
              <image src="/static/icons/arrow-right.svg" class="arrow-icon" mode="aspectFit"></image>
            </view>
          </view>
          
          <view class="form-item">
            <text class="form-label">详细地址</text>
            <textarea 
              v-model="formData.detail" 
              class="form-textarea" 
              placeholder="请输入详细地址（街道、门牌号等）"
              maxlength="100"
            />
          </view>
          
          <view class="form-item">
            <view class="checkbox-item" @click="toggleDefault">
              <view class="checkbox" :class="{ checked: formData.isDefault }">
                <image v-if="formData.isDefault" src="/static/icons/check.svg" class="check-icon" mode="aspectFit"></image>
              </view>
              <text class="checkbox-text">设为默认地址</text>
            </view>
          </view>
        </view>
        
        <view class="form-actions">
          <button class="cancel-btn" @click="closeAddressForm">取消</button>
          <button class="save-btn" @click="saveAddress">保存</button>
        </view>
      </view>
    </view>

    <!-- 地区选择器 -->
    <picker 
      v-if="showRegionPicker" 
      mode="region" 
      @change="onRegionChange"
      @cancel="showRegionPicker = false"
    >
    </picker>
  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useAddressStore } from '@/stores/address'

export default {
  name: 'AddressPage',
  setup() {
    const addressStore = useAddressStore()
    
    // 响应式数据
    const addressList = ref([])
    const showAddressForm = ref(false)
    const isEdit = ref(false)
    const showRegionPicker = ref(false)
    const formData = ref({
      id: null,
      name: '',
      phone: '',
      region: '',
      detail: '',
      isDefault: false
    })
    
    // 模拟地址数据
    const mockAddresses = ref([
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
    
    // 加载地址列表
    const loadAddressList = async () => {
      try {
        await addressStore.getAddressList()
        addressList.value = addressStore.addressList.length > 0 ? addressStore.addressList : mockAddresses.value
      } catch (error) {
        console.error('加载地址列表失败:', error)
        addressList.value = mockAddresses.value
      }
    }
    
    // 返回上一页
    const goBack = () => {
      uni.navigateBack()
    }
    
    // 新增地址
    const addAddress = () => {
      isEdit.value = false
      formData.value = {
        id: null,
        name: '',
        phone: '',
        region: '',
        detail: '',
        isDefault: false
      }
      showAddressForm.value = true
    }
    
    // 编辑地址
    const editAddress = (address) => {
      isEdit.value = true
      formData.value = { ...address }
      showAddressForm.value = true
    }
    
    // 关闭地址表单
    const closeAddressForm = () => {
      showAddressForm.value = false
      showRegionPicker.value = false
    }
    
    // 选择地址
    const selectAddress = (address) => {
      // 如果是从订单页面跳转过来的，选择地址后返回
      const pages = getCurrentPages()
      if (pages.length > 1) {
        const prevPage = pages[pages.length - 2]
        if (prevPage.route.includes('order')) {
          uni.$emit('addressSelected', address)
          uni.navigateBack()
          return
        }
      }
      
      // 否则编辑地址
      editAddress(address)
    }
    
    // 删除地址
    const deleteAddress = (addressId) => {
      uni.showModal({
        title: '确认删除',
        content: '确定要删除这个地址吗？',
        success: (res) => {
          if (res.confirm) {
            addressList.value = addressList.value.filter(item => item.id !== addressId)
            uni.showToast({
              title: '删除成功',
              icon: 'success'
            })
          }
        }
      })
    }
    
    // 显示地区选择器
    const openRegionPicker = () => {
      showRegionPicker.value = true
    }
    
    // 地区选择
    const onRegionChange = (e) => {
      const region = e.detail.value
      formData.value.region = region.join(' ')
      showRegionPicker.value = false
    }
    
    // 切换默认地址
    const toggleDefault = () => {
      formData.value.isDefault = !formData.value.isDefault
    }
    
    // 保存地址
    const saveAddress = () => {
      // 表单验证
      if (!formData.value.name.trim()) {
        uni.showToast({
          title: '请输入收货人姓名',
          icon: 'none'
        })
        return
      }
      
      if (!formData.value.phone.trim()) {
        uni.showToast({
          title: '请输入手机号',
          icon: 'none'
        })
        return
      }
      
      if (!formData.value.region.trim()) {
        uni.showToast({
          title: '请选择所在地区',
          icon: 'none'
        })
        return
      }
      
      if (!formData.value.detail.trim()) {
        uni.showToast({
          title: '请输入详细地址',
          icon: 'none'
        })
        return
      }
      
      // 手机号验证
      const phoneReg = /^1[3-9]\d{9}$/
      if (!phoneReg.test(formData.value.phone)) {
        uni.showToast({
          title: '请输入正确的手机号',
          icon: 'none'
        })
        return
      }
      
      // 保存地址
      const address = {
        ...formData.value,
        fullAddress: `${formData.value.region} ${formData.value.detail}`,
        phone: formData.value.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
      }
      
      if (isEdit.value) {
        // 编辑地址
        const index = addressList.value.findIndex(item => item.id === address.id)
        if (index !== -1) {
          addressList.value[index] = address
        }
      } else {
        // 新增地址
        address.id = Date.now()
        addressList.value.unshift(address)
      }
      
      // 如果设为默认地址，取消其他地址的默认状态
      if (address.isDefault) {
        addressList.value.forEach(item => {
          if (item.id !== address.id) {
            item.isDefault = false
          }
        })
      }
      
      closeAddressForm()
      uni.showToast({
        title: isEdit.value ? '编辑成功' : '添加成功',
        icon: 'success'
      })
    }
    
    onMounted(() => {
      loadAddressList()
    })
    
    return {
      addressList,
      showAddressForm,
      isEdit,
      showRegionPicker,
      formData,
      goBack,
      addAddress,
      editAddress,
      closeAddressForm,
      selectAddress,
      deleteAddress,
      openRegionPicker,
      onRegionChange,
      toggleDefault,
      saveAddress
    }
  }
}
</script>

<style scoped>
.address-page {
  background: #F5F5F5;
  min-height: 100vh;
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
  display: flex;
  align-items: center;
}

.add-text {
  font-size: 28rpx;
  color: #FF6B6B;
}

.address-list {
  padding: 24rpx 32rpx;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0;
}

.empty-image {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: 32rpx;
}

.empty-text {
  font-size: 32rpx;
  color: #999999;
  margin-bottom: 16rpx;
}

.empty-desc {
  font-size: 24rpx;
  color: #CCCCCC;
  margin-bottom: 40rpx;
}

.add-btn {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 24rpx 48rpx;
  border-radius: 24rpx;
  font-size: 28rpx;
  border: none;
}

.address-item {
  background: #FFFFFF;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  padding: 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.address-content {
  flex: 1;
  margin-right: 24rpx;
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 16rpx;
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
  margin-right: 16rpx;
}

.default-tag {
  background: #FF6B6B;
  color: #FFFFFF;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 20rpx;
}

.address-detail {
  margin-bottom: 16rpx;
}

.address-text {
  font-size: 28rpx;
  color: #666666;
  line-height: 1.5;
}

.address-actions {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
  padding: 16rpx;
  border-radius: 8rpx;
  background: #F5F5F5;
}

.action-icon {
  width: 32rpx;
  height: 32rpx;
}

.action-btn text {
  font-size: 20rpx;
  color: #666666;
}

.address-form-modal {
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

.form-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 32rpx;
  border-bottom: 1rpx solid #F0F0F0;
}

.form-title {
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

.form-content {
  padding: 32rpx;
  max-height: 60vh;
  overflow-y: auto;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  margin-bottom: 16rpx;
  font-weight: 500;
}

.form-input {
  width: 100%;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
  border: none;
}

.form-textarea {
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

.region-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 80rpx;
  background: #F5F5F5;
  border-radius: 12rpx;
  padding: 0 24rpx;
}

.region-text {
  font-size: 28rpx;
  color: #333333;
}

.arrow-icon {
  width: 24rpx;
  height: 24rpx;
}

.checkbox-item {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.checkbox {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #DDDDDD;
  border-radius: 8rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.checkbox.checked {
  background: #FF6B6B;
  border-color: #FF6B6B;
}

.check-icon {
  width: 24rpx;
  height: 24rpx;
}

.checkbox-text {
  font-size: 28rpx;
  color: #333333;
}

.form-actions {
  display: flex;
  gap: 24rpx;
  padding: 32rpx;
  border-top: 1rpx solid #F0F0F0;
}

.cancel-btn {
  flex: 1;
  height: 80rpx;
  background: #F5F5F5;
  color: #666666;
  border-radius: 12rpx;
  font-size: 28rpx;
  border: none;
}

.save-btn {
  flex: 1;
  height: 80rpx;
  background: #FF6B6B;
  color: #FFFFFF;
  border-radius: 12rpx;
  font-size: 28rpx;
  border: none;
}
</style>
