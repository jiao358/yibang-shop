import request from './request'

export const userApi = {
  // 分页查询用户列表
  getUserList(params) {
    return request({
      url: '/bk/users',
      method: 'get',
      params
    })
  },

  // 获取用户详情
  getUserDetail(userId) {
    return request({
      url: `/bk/users/${userId}`,
      method: 'get'
    })
  },

  // 调整用户余额
  updateUserBalance(userId, data) {
    return request({
      url: `/bk/users/${userId}/balance`,
      method: 'post',
      data
    })
  },

  // 更新用户等级
  updateUserLevel(userId, data) {
    return request({
      url: `/bk/users/${userId}/level`,
      method: 'post',
      data
    })
  },

  // 批量操作用户
  batchOperation(userIds, operation) {
    return request({
      url: '/bk/users/batch',
      method: 'post',
      params: {
        userIds: userIds.join(','),
        operation
      }
    })
  },

  // 获取用户统计
  getUserStats(params) {
    return request({
      url: '/bk/users/stats',
      method: 'get',
      params
    })
  },

  // 获取用户等级选项
  getUserLevels() {
    return request({
      url: '/bk/users/levels',
      method: 'get'
    })
  },

  // 获取用户任务完成情况
  getUserTasks(userId, params) {
    return request({
      url: `/bk/users/${userId}/tasks`,
      method: 'get',
      params
    })
  },

  // 获取用户邀请关系
  getUserInvites(userId) {
    return request({
      url: `/bk/users/${userId}/invites`,
      method: 'get'
    })
  }
}
