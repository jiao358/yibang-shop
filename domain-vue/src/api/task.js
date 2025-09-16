import request from './request'

export const taskApi = {
  // 分页查询任务列表
  getTaskList(params) {
    return request({
      url: '/bk/tasks',
      method: 'get',
      params
    })
  },

  // 获取任务详情
  getTaskDetail(taskId) {
    return request({
      url: `/bk/tasks/${taskId}`,
      method: 'get'
    })
  },

  // 创建任务
  createTask(data) {
    return request({
      url: '/bk/tasks',
      method: 'post',
      data
    })
  },

  // 更新任务
  updateTask(taskId, data) {
    return request({
      url: `/bk/tasks/${taskId}`,
      method: 'put',
      data
    })
  },

  // 删除任务
  deleteTask(taskId) {
    return request({
      url: `/bk/tasks/${taskId}`,
      method: 'delete'
    })
  },

  // 批量操作任务
  batchOperation(taskIds, operation) {
    return request({
      url: '/bk/tasks/batch',
      method: 'post',
      params: {
        taskIds: taskIds.join(','),
        operation
      }
    })
  },

  // 获取任务统计
  getTaskStats(params) {
    return request({
      url: '/bk/tasks/stats',
      method: 'get',
      params
    })
  },

  // 获取任务类型选项
  getTaskTypes() {
    return request({
      url: '/bk/tasks/types',
      method: 'get'
    })
  }
}
