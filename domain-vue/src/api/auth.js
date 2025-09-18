import request from './request'

export const authApi = {
  // 管理端登录
  adminLogin(data) {
    return request.post('/auth/admin-login', data)
  },

  // 获取当前用户信息
  getCurrentUser() {
    return request.get('/auth/me')
  },

  // 退出登录
  logout() {
    return request.post('/auth/logout')
  }
}
