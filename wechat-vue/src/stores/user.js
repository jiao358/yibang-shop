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
  const canWithdraw = computed(() => (userInfo.value.balance || 0) > 0)

  // 方法
  const login = async (code, userInfoArg = {}, phoneCode = null) => {
    try {
      const response = await userApi.login(code, userInfoArg, phoneCode)
      const data = response.data || response
      token.value = data.accessToken
      userInfo.value = {
        id: data.userId,
        nickname: data.nickname,
        avatar: data.avatar,
        level: data.userLevel
      }
      userLevel.value = data.userLevel === 'signed' ? 1 : (data.userLevel || 0)
      isLoggedIn.value = true
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
    uni.removeStorageSync('token')
    uni.removeStorageSync('userInfo')
    uni.removeStorageSync('userLevel')
  }

  const getUserInfo = async () => {
    try {
      // 未登录直接返回，不请求接口
      const savedToken = uni.getStorageSync('token')
      if (!savedToken) {
        isLoggedIn.value = false
        return { data: null }
      }
      const res = await userApi.getUserInfo()
      const data = res.data || res.userInfo || {}
      userInfo.value = {
        id: data.id || data.userId,
        nickname: data.nickname || data.name || '微信用户',
        avatar: data.avatar || '',
        level: data.userLevel || data.level || 0,
        balance: data.balance || 0,
        frozenBalance: data.frozenBalance || 0
      }
      userLevel.value = userInfo.value.level || 0
      isLoggedIn.value = true
      return res
    } catch (error) {
      console.error('获取用户信息失败:', error)
      // 401/404等异常视为未登录，不弹错
      isLoggedIn.value = false
      return { data: null }
    }
  }

  const updateUserInfo = async (data) => {
    try {
      const response = await userApi.updateUserInfo(data)
      const resp = response.data || response.userInfo || {}
      userInfo.value = { ...userInfo.value, ...resp }
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
    try { const response = await userApi.applyForSigning(data); return response } catch (error) { console.error('申请签约失败:', error); throw error }
  }

  const getSigningStatus = async () => {
    try { const response = await userApi.getSigningStatus(); return response } catch (error) { console.error('获取签约状态失败:', error); throw error }
  }

  return {
    userInfo,
    isLoggedIn,
    token,
    userLevel,
    isSignedUser,
    canWithdraw,
    login,
    logout,
    getUserInfo,
    updateUserInfo,
    checkLoginStatus,
    applyForSigning,
    getSigningStatus
  }
})
