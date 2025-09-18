import request from './request'

export const userApi = {
  // 分页查询用户列表
  getUserList(params) {
    return request.get('/bk/users', { params })
  },

  // 获取用户详情
  getUserDetail(userId) {
    return request.get(`/bk/users/${userId}`)
  },

  // 调整用户余额
  updateUserBalance(userId, data) {
    return request.post(`/bk/users/${userId}/balance`, data)
  },

  // 更新用户等级
  updateUserLevel(userId, data) {
    return request.post(`/bk/users/${userId}/level`, data)
  },

  // 批量操作用户
  batchOperation(userIds, operation) {
    return request.post('/bk/users/batch', null, {
      params: {
        userIds: userIds.join(','),
        operation
      }
    })
  },

  // 获取用户统计
  getUserStats(params) {
    return request.get('/bk/users/stats', { params })
  },

  // 获取用户等级选项
  getUserLevels() {
    return request.get('/bk/users/levels')
  },

  // 获取用户任务完成情况
  getUserTasks(userId, params) {
    return request.get(`/bk/users/${userId}/tasks`, { params })
  },

  // 获取用户邀请关系
  getUserInvites(userId) {
    return request.get(`/bk/users/${userId}/invites`)
  }
}
