// 请求配置和拦截器
const BASE_URL = process.env.NODE_ENV === 'development' 
  ? 'http://127.0.0.1:8080' // 开发环境使用本地服务器（更稳妥的本地地址）
  : 'https://api.yibang-taskmall.com' // 生产环境使用实际的后端地址

// 请求拦截器
const request = (options) => {
  return new Promise((resolve, reject) => {
    // 添加token（登录与鉴权接口不带Authorization）
    const isAuthEndpoint = typeof options.url === 'string' && 
      (options.url.indexOf('/auth/') >= 0 || options.url.indexOf('/api/auth/') >= 0)
    const token = uni.getStorageSync('token')
    
    console.log('Request URL:', options.url)
    console.log('Is auth endpoint:', isAuthEndpoint)
    console.log('Token exists:', !!token)
    console.log('Token value:', token ? token.substring(0, 20) + '...' : 'null')
    
    if (token && !isAuthEndpoint) {
      options.header = {
        ...options.header,
        'Authorization': `Bearer ${token}`
      }
      console.log('Added Authorization header with token length:', token.length)
    } else {
      console.log('No Authorization header added. Token:', !!token, 'IsAuth:', isAuthEndpoint)
    }

    // 添加默认配置（确保最终 url 不会被 options.url 覆盖）
    const config = {
      method: options.method || 'GET',
      data: options.data || {},
      timeout: 10000,
      ...options,
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      url: BASE_URL + options.url
    }
    
    console.log('Final config header:', config.header)

    uni.request({
      ...config,
      success: (res) => {
        if (res.statusCode === 200) {
          if (res.data.code === 200) {
            resolve(res.data)
          } else {
            // 业务错误
            uni.showToast({
              title: res.data.message || '请求失败',
              icon: 'none'
            })
            reject(new Error(res.data.message || '请求失败'))
          }
        } else if (res.statusCode === 401) {
          // 未授权，清除token并提示（不跳转不存在的登录页）
          uni.removeStorageSync('token')
          uni.removeStorageSync('userInfo')
          uni.showToast({
            title: '登录已过期',
            icon: 'none'
          })
          reject(new Error('登录已过期'))
        } else {
          // 其他HTTP错误
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(new Error('网络错误'))
        }
      },
      fail: (err) => {
        console.error('请求失败:', err)
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// GET请求
export const get = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'GET',
    data,
    ...options
  })
}

// POST请求
export const post = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'POST',
    data,
    ...options
  })
}

// PUT请求
export const put = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'PUT',
    data,
    ...options
  })
}

// DELETE请求
export const del = (url, data = {}, options = {}) => {
  return request({
    url,
    method: 'DELETE',
    data,
    ...options
  })
}

// 文件上传
export const upload = (url, filePath, formData = {}) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    uni.uploadFile({
      url: BASE_URL + url,
      filePath,
      name: 'file',
      formData,
      header: {
        'Authorization': token ? `Bearer ${token}` : ''
      },
      success: (res) => {
        const data = JSON.parse(res.data)
        if (data.code === 0) {
          resolve(data)
        } else {
          uni.showToast({
            title: data.message || '上传失败',
            icon: 'none'
          })
          reject(new Error(data.message || '上传失败'))
        }
      },
      fail: (err) => {
        console.error('上传失败:', err)
        uni.showToast({
          title: '上传失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default {
  get,
  post,
  put,
  del,
  upload
}
