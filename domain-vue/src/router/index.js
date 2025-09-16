import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入布局组件
import Layout from '@/components/Layout/index.vue'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: {
      title: '登录',
      requireAuth: false
    }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: {
          title: '仪表盘',
          icon: 'DataBoard',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/task',
    component: Layout,
    redirect: '/task/list',
    meta: {
      title: '任务管理',
      icon: 'Calendar',
      requireAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'TaskList',
        component: () => import('@/views/task/list.vue'),
        meta: {
          title: '任务列表',
          requireAuth: true
        }
      },
      {
        path: 'create',
        name: 'TaskCreate',
        component: () => import('@/views/task/create.vue'),
        meta: {
          title: '创建任务',
          requireAuth: true
        }
      },
      {
        path: 'edit/:id',
        name: 'TaskEdit',
        component: () => import('@/views/task/edit.vue'),
        meta: {
          title: '编辑任务',
          requireAuth: true,
          hidden: true
        }
      },
      {
        path: 'stats',
        name: 'TaskStats',
        component: () => import('@/views/task/stats.vue'),
        meta: {
          title: '任务统计',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    redirect: '/user/list',
    meta: {
      title: '用户管理',
      icon: 'User',
      requireAuth: true
    },
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue'),
        meta: {
          title: '用户列表',
          requireAuth: true
        }
      },
      {
        path: 'detail/:id',
        name: 'UserDetail',
        component: () => import('@/views/user/detail.vue'),
        meta: {
          title: '用户详情',
          requireAuth: true,
          hidden: true
        }
      },
      {
        path: 'levels',
        name: 'UserLevels',
        component: () => import('@/views/user/levels.vue'),
        meta: {
          title: '等级管理',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/finance',
    component: Layout,
    redirect: '/finance/withdraw',
    meta: {
      title: '财务管理',
      icon: 'Money',
      requireAuth: true
    },
    children: [
      {
        path: 'withdraw',
        name: 'WithdrawApproval',
        component: () => import('@/views/finance/withdraw.vue'),
        meta: {
          title: '提现审核',
          requireAuth: true
        }
      },
      {
        path: 'reports',
        name: 'FinanceReports',
        component: () => import('@/views/finance/reports.vue'),
        meta: {
          title: '财务报表',
          requireAuth: true
        }
      },
      {
        path: 'earnings',
        name: 'EarningsStats',
        component: () => import('@/views/finance/earnings.vue'),
        meta: {
          title: '收益统计',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/config',
    meta: {
      title: '系统管理',
      icon: 'Setting',
      requireAuth: true
    },
    children: [
      {
        path: 'config',
        name: 'SystemConfig',
        component: () => import('@/views/system/config.vue'),
        meta: {
          title: '系统配置',
          requireAuth: true
        }
      },
      {
        path: 'notifications',
        name: 'Notifications',
        component: () => import('@/views/system/notifications.vue'),
        meta: {
          title: '通知管理',
          requireAuth: true
        }
      },
      {
        path: 'logs',
        name: 'SystemLogs',
        component: () => import('@/views/system/logs.vue'),
        meta: {
          title: '操作日志',
          requireAuth: true
        }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: {
      title: '页面不存在',
      requireAuth: false,
      hidden: true
    }
  }
]

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 任务商城后台管理`
  }

  // 权限检查
  if (to.meta.requireAuth) {
    // TODO: 检查用户登录状态和权限
    const token = localStorage.getItem('admin_token')
    if (!token) {
      ElMessage.warning('请先登录')
      next('/login')
      return
    }
    
    // TODO: 检查用户权限
    // const hasPermission = checkPermission(to.meta.permission)
    // if (!hasPermission) {
    //   ElMessage.error('没有访问权限')
    //   next('/dashboard')
    //   return
    // }
  }

  next()
})

router.afterEach((to, from) => {
  // 页面加载完成后的处理
  console.log(`Route changed: ${from.path} -> ${to.path}`)
})

export default router
