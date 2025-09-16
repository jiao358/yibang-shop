import request from './request'

export const systemApi = {
  // 获取系统配置列表
  getSystemConfigs(group) {
    return request({
      url: '/bk/system/configs',
      method: 'get',
      params: { group }
    })
  },

  // 更新系统配置
  updateSystemConfig(key, data) {
    return request({
      url: `/bk/system/configs/${key}`,
      method: 'put',
      data
    })
  },

  // 批量更新系统配置
  batchUpdateConfigs(data) {
    return request({
      url: '/bk/system/configs/batch',
      method: 'put',
      data
    })
  },

  // 获取配置分组
  getConfigGroups() {
    return request({
      url: '/bk/system/config-groups',
      method: 'get'
    })
  },

  // 获取通知消息列表
  getNotifications(params) {
    return request({
      url: '/bk/system/notifications',
      method: 'get',
      params
    })
  },

  // 创建通知消息
  createNotification(data) {
    return request({
      url: '/bk/system/notifications',
      method: 'post',
      data
    })
  },

  // 更新通知消息
  updateNotification(notificationId, data) {
    return request({
      url: `/bk/system/notifications/${notificationId}`,
      method: 'put',
      data
    })
  },

  // 删除通知消息
  deleteNotification(notificationId) {
    return request({
      url: `/bk/system/notifications/${notificationId}`,
      method: 'delete'
    })
  },

  // 发送通知消息
  sendNotification(notificationId, userIds) {
    return request({
      url: `/bk/system/notifications/${notificationId}/send`,
      method: 'post',
      params: { userIds: userIds?.join(',') }
    })
  },

  // 文件上传
  uploadFile(file, type = 'image') {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('type', type)
    
    return request({
      url: '/bk/system/upload',
      method: 'post',
      data: formData,
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
    
    return request({
      url: '/bk/system/upload/batch',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取操作日志
  getOperationLogs(params) {
    return request({
      url: '/bk/system/logs',
      method: 'get',
      params
    })
  },

  // 清除缓存
  clearCache(cacheType) {
    return request({
      url: '/bk/system/cache/clear',
      method: 'post',
      params: { cacheType }
    })
  },

  // 获取系统信息
  getSystemInfo() {
    return request({
      url: '/bk/system/info',
      method: 'get'
    })
  }
}
