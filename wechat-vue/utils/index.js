// 工具函数集合

/**
 * 格式化金额
 * @param {number} amount 金额
 * @param {number} decimals 小数位数
 * @returns {string} 格式化后的金额字符串
 */
export const formatMoney = (amount, decimals = 2) => {
  if (isNaN(amount)) return '0.00'
  return Number(amount).toFixed(decimals)
}

/**
 * 格式化时间
 * @param {string|Date} time 时间
 * @param {string} format 格式
 * @returns {string} 格式化后的时间字符串
 */
export const formatTime = (time, format = 'YYYY-MM-DD HH:mm:ss') => {
  const date = new Date(time)
  if (isNaN(date.getTime())) return ''
  
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 相对时间格式化
 * @param {string|Date} time 时间
 * @returns {string} 相对时间字符串
 */
export const formatRelativeTime = (time) => {
  const now = new Date()
  const target = new Date(time)
  const diff = now - target
  
  if (diff < 0) return '刚刚'
  
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day
  
  if (diff < minute) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / minute)}分钟前`
  if (diff < day) return `${Math.floor(diff / hour)}小时前`
  if (diff < week) return `${Math.floor(diff / day)}天前`
  if (diff < month) return `${Math.floor(diff / week)}周前`
  if (diff < year) return `${Math.floor(diff / month)}个月前`
  return `${Math.floor(diff / year)}年前`
}

/**
 * 防抖函数
 * @param {Function} func 要防抖的函数
 * @param {number} delay 延迟时间
 * @returns {Function} 防抖后的函数
 */
export const debounce = (func, delay = 300) => {
  let timeoutId
  return function (...args) {
    clearTimeout(timeoutId)
    timeoutId = setTimeout(() => func.apply(this, args), delay)
  }
}

/**
 * 节流函数
 * @param {Function} func 要节流的函数
 * @param {number} delay 延迟时间
 * @returns {Function} 节流后的函数
 */
export const throttle = (func, delay = 300) => {
  let lastCall = 0
  return function (...args) {
    const now = Date.now()
    if (now - lastCall >= delay) {
      lastCall = now
      func.apply(this, args)
    }
  }
}

/**
 * 深拷贝
 * @param {any} obj 要拷贝的对象
 * @returns {any} 拷贝后的对象
 */
export const deepClone = (obj) => {
  if (obj === null || typeof obj !== 'object') return obj
  if (obj instanceof Date) return new Date(obj.getTime())
  if (obj instanceof Array) return obj.map(item => deepClone(item))
  if (typeof obj === 'object') {
    const clonedObj = {}
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key])
      }
    }
    return clonedObj
  }
}

/**
 * 生成随机字符串
 * @param {number} length 长度
 * @returns {string} 随机字符串
 */
export const randomString = (length = 8) => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  let result = ''
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length))
  }
  return result
}

/**
 * 验证手机号
 * @param {string} phone 手机号
 * @returns {boolean} 是否有效
 */
export const validatePhone = (phone) => {
  const phoneReg = /^1[3-9]\d{9}$/
  return phoneReg.test(phone)
}

/**
 * 验证邮箱
 * @param {string} email 邮箱
 * @returns {boolean} 是否有效
 */
export const validateEmail = (email) => {
  const emailReg = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailReg.test(email)
}

/**
 * 获取文件扩展名
 * @param {string} filename 文件名
 * @returns {string} 扩展名
 */
export const getFileExtension = (filename) => {
  return filename.slice((filename.lastIndexOf('.') - 1 >>> 0) + 2)
}

/**
 * 计算文件大小
 * @param {number} bytes 字节数
 * @returns {string} 格式化后的大小
 */
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 获取URL参数
 * @param {string} url URL字符串
 * @returns {Object} 参数对象
 */
export const getUrlParams = (url) => {
  const params = {}
  const urlObj = new URL(url)
  urlObj.searchParams.forEach((value, key) => {
    params[key] = value
  })
  return params
}

/**
 * 生成UUID
 * @returns {string} UUID字符串
 */
export const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

/**
 * 数组去重
 * @param {Array} arr 数组
 * @param {string} key 去重字段
 * @returns {Array} 去重后的数组
 */
export const uniqueArray = (arr, key) => {
  if (!key) {
    return [...new Set(arr)]
  }
  const seen = new Set()
  return arr.filter(item => {
    const val = item[key]
    if (seen.has(val)) {
      return false
    }
    seen.add(val)
    return true
  })
}

/**
 * 对象转URL参数
 * @param {Object} obj 对象
 * @returns {string} URL参数字符串
 */
export const objectToUrlParams = (obj) => {
  return Object.keys(obj)
    .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(obj[key])}`)
    .join('&')
}

/**
 * 检查是否为空值
 * @param {any} value 值
 * @returns {boolean} 是否为空
 */
export const isEmpty = (value) => {
  if (value === null || value === undefined) return true
  if (typeof value === 'string') return value.trim() === ''
  if (Array.isArray(value)) return value.length === 0
  if (typeof value === 'object') return Object.keys(value).length === 0
  return false
}

/**
 * 获取设备信息
 * @returns {Object} 设备信息
 */
export const getDeviceInfo = () => {
  const systemInfo = uni.getSystemInfoSync()
  return {
    platform: systemInfo.platform,
    system: systemInfo.system,
    version: systemInfo.version,
    model: systemInfo.model,
    pixelRatio: systemInfo.pixelRatio,
    screenWidth: systemInfo.screenWidth,
    screenHeight: systemInfo.screenHeight,
    windowWidth: systemInfo.windowWidth,
    windowHeight: systemInfo.windowHeight,
    statusBarHeight: systemInfo.statusBarHeight,
    safeAreaInsets: systemInfo.safeAreaInsets
  }
}

/**
 * 显示加载提示
 * @param {string} title 提示文字
 */
export const showLoading = (title = '加载中...') => {
  uni.showLoading({ title })
}

/**
 * 隐藏加载提示
 */
export const hideLoading = () => {
  uni.hideLoading()
}

/**
 * 显示成功提示
 * @param {string} title 提示文字
 */
export const showSuccess = (title = '操作成功') => {
  uni.showToast({
    title,
    icon: 'success',
    duration: 2000
  })
}

/**
 * 显示错误提示
 * @param {string} title 提示文字
 */
export const showError = (title = '操作失败') => {
  uni.showToast({
    title,
    icon: 'none',
    duration: 2000
  })
}

/**
 * 显示确认对话框
 * @param {string} content 内容
 * @param {string} title 标题
 * @returns {Promise<boolean>} 是否确认
 */
export const showConfirm = (content, title = '提示') => {
  return new Promise((resolve) => {
    uni.showModal({
      title,
      content,
      success: (res) => {
        resolve(res.confirm)
      }
    })
  })
}

export default {
  formatMoney,
  formatTime,
  formatRelativeTime,
  debounce,
  throttle,
  deepClone,
  randomString,
  validatePhone,
  validateEmail,
  getFileExtension,
  formatFileSize,
  getUrlParams,
  generateUUID,
  uniqueArray,
  objectToUrlParams,
  isEmpty,
  getDeviceInfo,
  showLoading,
  hideLoading,
  showSuccess,
  showError,
  showConfirm
}
