import request from './request'

// 本地兜底常量（管理端专用）
const FALLBACK_DICT = {
  task_type: [
    { code: 'ad', label_zh: '广告任务', label_en: 'Ad' },
    { code: 'video', label_zh: '视频任务', label_en: 'Video' },
    { code: 'app_install', label_zh: '应用安装', label_en: 'App Install' },
    { code: 'survey', label_zh: '问卷调查', label_en: 'Survey' },
    { code: 'share', label_zh: '分享任务', label_en: 'Share' }
  ],
  user_level: [
    { code: 'normal', label_zh: '普通用户', label_en: 'Normal' },
    { code: 'signed', label_zh: '签约用户', label_en: 'Signed' },
    { code: 'vip', label_zh: 'VIP用户', label_en: 'VIP' }
  ],
  commission_level: [
    { code: 'low', label_zh: '低', label_en: 'Low' },
    { code: 'medium', label_zh: '中', label_en: 'Medium' },
    { code: 'high', label_zh: '高', label_en: 'High' }
  ],
  withdraw_status: [
    { code: 'pending', label_zh: '待处理', label_en: 'Pending' },
    { code: 'processing', label_zh: '处理中', label_en: 'Processing' },
    { code: 'completed', label_zh: '已完成', label_en: 'Completed' },
    { code: 'failed', label_zh: '失败', label_en: 'Failed' },
    { code: 'cancelled', label_zh: '已取消', label_en: 'Cancelled' }
  ],
  order_status: [
    { code: 'pending', label_zh: '待支付', label_en: 'Pending' },
    { code: 'paid', label_zh: '已支付', label_en: 'Paid' },
    { code: 'shipped', label_zh: '已发货', label_en: 'Shipped' },
    { code: 'received', label_zh: '已收货', label_en: 'Received' },
    { code: 'completed', label_zh: '已完成', label_en: 'Completed' },
    { code: 'refunding', label_zh: '退款中', label_en: 'Refunding' },
    { code: 'refunded', label_zh: '已退款', label_en: 'Refunded' }
  ],
  notify_type: [
    { code: 'system', label_zh: '系统通知', label_en: 'System' },
    { code: 'task', label_zh: '任务通知', label_en: 'Task' }
  ],
  agreements: [
    { code: 'privacy', label_zh: '隐私协议', label_en: 'Privacy Policy' },
    { code: 'user_guide', label_zh: '用户须知', label_en: 'User Guide' }
  ]
}

// 获取字典（带本地兜底）
export async function getDict(group) {
  try {
    const res = await request.get('/bk/dicts', { params: { group } })
    const list = res?.data || []
    if (Array.isArray(list) && list.length > 0) {
      return list.map(i => ({
        code: i.itemCode,
        label_zh: i.labelZh,
        label_en: i.labelEn,
        enabled: i.enabled,
        sort: i.sort,
      }))
    }
  } catch (e) {
    // ignore
  }
  return FALLBACK_DICT[group] || []
}

// 获取为下拉用的 options（label/value）
export async function getDictOptions(group, lang = 'zh') {
  const list = await getDict(group)
  const labelKey = lang === 'en' ? 'label_en' : 'label_zh'
  return list.map(i => ({ label: i[labelKey] || i.label_zh || i.code, value: i.code }))
}

export default { getDict, getDictOptions }
