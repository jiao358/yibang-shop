<template>
  <view class="address-page">
    <!-- 使用原生标题栏：移除自定义状态栏与头部 -->

    <!-- 地址列表 -->
    <view class="address-list">
      <view v-if="addressList.length === 0" class="empty-state">
        <!-- 暂时移除图片，避免404错误 -->
        <view class="empty-icon">📦</view>
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
              <text class="name">{{ address.receiverName }}</text>
              <text class="phone">{{ formatPhoneNumber(address.receiverPhone) }}</text>
              <view v-if="address.isDefault" class="default-tag">默认</view>
            </view>
            <view class="address-detail">
              <text class="address-text">{{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}</text>
            </view>
          </view>
          <view class="address-actions">
            <view class="action-btn" @click.stop="editAddress(address)">
              <view class="action-icon">✏️</view>
              <text>编辑</text>
            </view>
            <view class="action-btn" @click.stop="deleteAddress(address.id)">
              <view class="action-icon">🗑️</view>
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
            <view class="close-icon">✕</view>
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
            <picker 
              mode="region" 
              :value="regionValue"
              @change="onRegionChange"
            >
              <view class="region-selector">
                <text class="region-text">{{ formData.region || '请选择省市区' }}</text>
                <image src="/static/icons/arrow-right.png" class="arrow-icon" mode="aspectFit"></image>
              </view>
            </picker>
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
                <view v-if="formData.isDefault" class="check-icon">✓</view>
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

  </view>
</template>

<script>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAddressStore } from '@/stores/address'
import { useUserStore } from '@/stores/user'

export default {
  name: 'AddressPage',
  setup() {
    const addressStore = useAddressStore()
    const userStore = useUserStore()
    
    // 响应式数据
    const addressList = ref([])
    const showAddressForm = ref(false)
    const isEdit = ref(false)
    const regionValue = ref([])
    const formData = ref({
      id: null,
      name: '',
      phone: '',
      region: '',
      detail: '',
      isDefault: false
    })
    
    // 加载地址列表
    const loadAddressList = async () => {
      try {
        await addressStore.getAddressList()
        addressList.value = addressStore.addressList
      } catch (error) {
        console.error('加载地址列表失败:', error)
        addressList.value = []
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
    const editAddress = async (address) => {
      try {
        isEdit.value = true
        
        // 从后端获取完整的地址信息（包括真实手机号）
        const response = await addressStore.getAddressDetail(address.id)
        const fullAddress = response.data
        
        formData.value = {
          id: fullAddress.id,
          name: fullAddress.receiverName,
          phone: fullAddress.receiverPhone, // 使用真实手机号，不是加密版本
          region: `${fullAddress.province} ${fullAddress.city} ${fullAddress.district}`,
          detail: fullAddress.detailAddress,
          isDefault: fullAddress.isDefault
        }
        
        showAddressForm.value = true
      } catch (error) {
        console.error('获取地址详情失败:', error)
        uni.showToast({
          title: '获取地址信息失败',
          icon: 'none'
        })
      }
    }
    
    // 关闭地址表单
    const closeAddressForm = () => {
      showAddressForm.value = false
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
        success: async (res) => {
          if (res.confirm) {
            try {
              // 调用后端API删除地址
              await addressStore.deleteAddress(addressId)
              
              // 重新加载地址列表
              await loadAddressList()
              
              uni.showToast({
                title: '删除成功',
                icon: 'success'
              })
            } catch (error) {
              console.error('删除地址失败:', error)
              uni.showToast({
                title: '删除失败，请重试',
                icon: 'none'
              })
            }
          }
        }
      })
    }
    
    // 格式化手机号（显示时加密）
    const formatPhoneNumber = (phone) => {
      if (!phone) return ''
      return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
    }
    
    // 地区选择
    const onRegionChange = (e) => {
      const region = e.detail.value
      regionValue.value = region
      formData.value.region = region.join(' ')
    }
    
    // 切换默认地址
    const toggleDefault = () => {
      formData.value.isDefault = !formData.value.isDefault
    }
    
    // 保存地址
    const saveAddress = async () => {
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
      
      try {
        // 构建地址数据
        const [province, city, district] = formData.value.region.split(' ')
        const addressData = {
          receiverName: formData.value.name,
          receiverPhone: formData.value.phone, // 不加密，直接保存真实手机号
          province: province,
          city: city,
          district: district,
          detailAddress: formData.value.detail,
          isDefault: formData.value.isDefault
        }
        
        if (isEdit.value) {
          // 编辑地址 - 调用后端API
          await addressStore.updateAddress(formData.value.id, addressData)
          uni.showToast({
            title: '编辑成功',
            icon: 'success'
          })
        } else {
          // 新增地址 - 调用后端API
          await addressStore.createAddress(addressData)
          uni.showToast({
            title: '添加成功',
            icon: 'success'
          })
        }
        
        // 重新加载地址列表
        await loadAddressList()
        closeAddressForm()
        
      } catch (error) {
        console.error('保存地址失败:', error)
        uni.showToast({
          title: '保存失败，请重试',
          icon: 'none'
        })
      }
    }
    
    // 页面显示时检查登录状态
    const checkLoginAndLoad = () => {
      console.log('地址页面onShow')
      // 检查登录状态
      userStore.checkLoginStatus()
      const token = uni.getStorageSync('token')
      console.log('地址页面检查token:', token ? token.substring(0, 20) + '...' : 'null')
      
      if (!token) {
        console.log('没有token，跳转到登录页面')
        uni.showToast({
          title: '请先登录',
          icon: 'none'
        })
        // 跳转到个人中心页面进行登录（使用switchTab因为profile是tabbar页面）
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/profile/profile'
          })
        }, 1500)
        return
      }
      
      console.log('有token，开始加载地址列表')
      loadAddressList()
    }

    // 页面是否已初始化
    const pageInitialized = ref(false)

    onMounted(() => {
      checkLoginAndLoad()
      pageInitialized.value = true
    })

    onShow(() => {
      // 只有在页面已初始化且不是第一次加载时才重新加载
      if (pageInitialized.value) {
        const token = uni.getStorageSync('token')
        if (token) {
          loadAddressList()
        }
      }
    })
    
    return {
      addressList,
      showAddressForm,
      isEdit,
      regionValue,
      formData,
      goBack,
      addAddress,
      editAddress,
      closeAddressForm,
      selectAddress,
      deleteAddress,
      formatPhoneNumber,
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

.empty-icon {
  font-size: 120rpx;
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
