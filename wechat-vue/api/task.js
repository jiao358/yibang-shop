import { get, post, put, del } from './request'

export const taskApi = {
  // 获取任务列表
  getTasks(params = {}) {
    return get('/api/tasks', params)
  },

  // 获取任务详情
  getTaskDetail(taskId) {
    return get(`/api/tasks/${taskId}`)
  },

  // 领取任务
  claimTask(taskId) {
    return post(`/api/tasks/${taskId}/claim`)
  },

  // 完成任务
  completeTask(taskId, data) {
    return post(`/api/tasks/${taskId}/complete`, data)
  },

  // 获取推荐任务
  getRecommendedTasks() {
    return get('/api/tasks/recommended')
  },

  // 获取任务统计
  getTaskStats() {
    return get('/api/tasks/stats')
  },

  // 更新任务进度
  updateTaskProgress(taskId, data) {
    return put(`/api/tasks/${taskId}/progress`, data)
  },

  // 放弃任务
  abandonTask(taskId) {
    return post(`/api/tasks/${taskId}/abandon`)
  },

  // 获取任务历史
  getTaskHistory(params = {}) {
    return get('/api/tasks/history', params)
  },

  // 获取用户任务列表
  getUserTasks(params = {}) {
    return get('/api/user/tasks', params)
  },

  // 获取任务类型列表
  getTaskTypes() {
    return get('/api/tasks/types')
  },

  // 获取任务等级要求
  getTaskLevelRequirements() {
    return get('/api/tasks/level-requirements')
  },

  // 获取任务完成条件
  getTaskCompletionConditions(taskId) {
    return get(`/api/tasks/${taskId}/conditions`)
  },

  // 验证任务完成
  verifyTaskCompletion(taskId, data) {
    return post(`/api/tasks/${taskId}/verify`, data)
  },

  // 获取任务奖励信息
  getTaskReward(taskId) {
    return get(`/api/tasks/${taskId}/reward`)
  },

  // 获取任务相关商品
  getTaskProducts(taskId) {
    return get(`/api/tasks/${taskId}/products`)
  },

  // 获取任务分享信息
  getTaskShareInfo(taskId) {
    return get(`/api/tasks/${taskId}/share`)
  },

  // 分享任务
  shareTask(taskId, data) {
    return post(`/api/tasks/${taskId}/share`, data)
  },

  // 获取任务评价
  getTaskReviews(taskId, params = {}) {
    return get(`/api/tasks/${taskId}/reviews`, params)
  },

  // 评价任务
  reviewTask(taskId, data) {
    return post(`/api/tasks/${taskId}/review`, data)
  }
}
