import { get, post, put } from './request'

export const userApi = {
  // 微信登录
  login(code) {
    return post('/api/user/login', { code })
  },

  // 获取用户信息
  getUserInfo() {
    return get('/api/user/info')
  },

  // 更新用户信息
  updateUserInfo(data) {
    return put('/api/user/info', data)
  },

  // 申请签约
  applyForSigning(data) {
    return post('/api/user/signing/apply', data)
  },

  // 获取签约状态
  getSigningStatus() {
    return get('/api/user/signing/status')
  },

  // 获取用户等级信息
  getUserLevel() {
    return get('/api/user/level')
  },

  // 获取用户统计信息
  getUserStats() {
    return get('/api/user/stats')
  },

  // 更新用户头像
  updateAvatar(filePath) {
    return post('/api/user/avatar', { filePath })
  },

  // 获取用户设置
  getUserSettings() {
    return get('/api/user/settings')
  },

  // 更新用户设置
  updateUserSettings(data) {
    return put('/api/user/settings', data)
  },

  // 获取用户通知设置
  getNotificationSettings() {
    return get('/api/user/notifications/settings')
  },

  // 更新用户通知设置
  updateNotificationSettings(data) {
    return put('/api/user/notifications/settings', data)
  },

  // 获取用户隐私设置
  getPrivacySettings() {
    return get('/api/user/privacy/settings')
  },

  // 更新用户隐私设置
  updatePrivacySettings(data) {
    return put('/api/user/privacy/settings', data)
  },

  // 注销账户
  deleteAccount() {
    return post('/api/user/delete')
  }
}
