import request from './request'

export const taskApi = {
  // 分页查询任务列表
  getTaskList(params) {
    return request.get('/bk/tasks', { params })
  },

  // 获取任务详情
  getTaskDetail(taskId) {
    return request.get(`/bk/tasks/${taskId}`)
  },

  // 创建任务
  createTask(data) {
    return request.post('/bk/tasks', data)
  },

  // 更新任务
  updateTask(taskId, data) {
    return request.put(`/bk/tasks/${taskId}`, data)
  },

  // 删除任务
  deleteTask(taskId) {
    return request.delete(`/bk/tasks/${taskId}`)
  },

  // 批量操作任务
  batchOperation(taskIds, operation) {
    return request.post('/bk/tasks/batch', null, {
      params: {
        taskIds: taskIds.join(','),
        operation
      }
    })
  },

  // 获取任务统计
  getTaskStats(params) {
    return request.get('/bk/tasks/stats', { params })
  },

  // 获取任务类型选项
  getTaskTypes() {
    return request.get('/bk/tasks/types')
  }
}
