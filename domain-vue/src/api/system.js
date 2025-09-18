import request from './request'

export const systemApi = {
  // 获取系统配置列表
  getSystemConfigs(group) {
    return request.get('/bk/system/configs', { params: { group } })
  },

  // 更新系统配置
  updateSystemConfig(key, data) {
    return request.put(`/bk/system/configs/${key}`, data)
  },

  // 批量更新系统配置
  batchUpdateConfigs(data) {
    return request.put('/bk/system/configs/batch', data)
  },

  // 获取配置分组
  getConfigGroups() {
    return request.get('/bk/system/config-groups')
  },

  // 获取通知消息列表
  getNotifications(params) {
    return request.get('/bk/system/notifications', { params })
  },

  // 创建通知消息
  createNotification(data) {
    return request.post('/bk/system/notifications', data)
  },

  // 更新通知消息
  updateNotification(notificationId, data) {
    return request.put(`/bk/system/notifications/${notificationId}`, data)
  },

  // 删除通知消息
  deleteNotification(notificationId) {
    return request.delete(`/bk/system/notifications/${notificationId}`)
  },

  // 发送通知消息
  sendNotification(notificationId, userIds) {
    return request.post(`/bk/system/notifications/${notificationId}/send`, null, {
      params: { userIds: userIds?.join(',') }
    })
  },

  // 文件上传
  uploadFile(file, type = 'image') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    
    return request.post('/bk/system/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 批量文件上传
  batchUploadFiles(files, type = 'image') {
    const formData = new FormData()
    files.forEach(file => {
      formData.append('files', file)
    })
    formData.append('type', type)
    
    return request.post('/bk/system/upload/batch', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取操作日志
  getOperationLogs(params) {
    return request.get('/bk/system/logs', { params })
  },

  // 清除缓存
  clearCache(cacheType) {
    return request.post('/bk/system/cache/clear', null, {
      params: { cacheType }
    })
  },

  // 获取系统信息
  getSystemInfo() {
    return request.get('/bk/system/info')
  }
}
