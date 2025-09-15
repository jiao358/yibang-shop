import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { userApi } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 状态
  const userInfo = ref({})
  const isLoggedIn = ref(false)
  const token = ref('')
  const userLevel = ref(0) // 0: 普通用户, 1: 签约用户

  // 计算属性
  const isSignedUser = computed(() => userLevel.value === 1)
  const canWithdraw = computed(() => userInfo.value.balance > 0)

  // 方法
  const login = async (code) => {
    try {
      const response = await userApi.login(code)
      
      token.value = response.token
      userInfo.value = response.userInfo
      userLevel.value = response.userInfo.level || 0
      isLoggedIn.value = true
      
      // 保存到本地存储
      uni.setStorageSync('token', token.value)
      uni.setStorageSync('userInfo', userInfo.value)
      uni.setStorageSync('userLevel', userLevel.value)
      
      return response
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    userLevel.value = 0
    isLoggedIn.value = false
    
    // 清除本地存储
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('userLevel')
  }

  const getUserInfo = async () => {
    try {
      const response = await userApi.getUserInfo()
      userInfo.value = response.userInfo
      userLevel.value = response.userInfo.level || 0
      return response
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  const updateUserInfo = async (data) => {
    try {
      const response = await userApi.updateUserInfo(data)
      userInfo.value = { ...userInfo.value, ...response.userInfo }
      
      // 更新本地存储
      uni.setStorageSync('userInfo', userInfo.value)
      
      return response
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  const checkLoginStatus = () => {
    const savedToken = uni.getStorageSync('token')
    const savedUserInfo = uni.getStorageSync('userInfo')
    const savedUserLevel = uni.getStorageSync('userLevel')
    
    if (savedToken && savedUserInfo) {
      token.value = savedToken
      userInfo.value = savedUserInfo
      userLevel.value = savedUserLevel || 0
      isLoggedIn.value = true
      return true
    }
    
    return false
  }

  const applyForSigning = async (data) => {
    try {
      const response = await userApi.applyForSigning(data)
      return response
    } catch (error) {
      console.error('申请签约失败:', error)
      throw error
    }
  }

  const getSigningStatus = async () => {
    try {
      const response = await userApi.getSigningStatus()
      return response
    } catch (error) {
      console.error('获取签约状态失败:', error)
      throw error
    }
  }

  return {
    // 状态
    userInfo,
    isLoggedIn,
    token,
    userLevel,
    
    // 计算属性
    isSignedUser,
    canWithdraw,
    
    // 方法
    login,
    logout,
    getUserInfo,
    updateUserInfo,
    checkLoginStatus,
    applyForSigning,
    getSigningStatus
  }
})
