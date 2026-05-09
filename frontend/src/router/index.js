import { createRouter, createWebHistory } from 'vue-router'

/**
 * 路由配置
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    name: 'Root',
    redirect: '/dashboard',
    meta: { requiresAuth: true }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页仪表盘', menuCode: 'dashboard' }
  },
  {
    path: '/lifecycle',
    name: 'Lifecycle',
    component: () => import('@/views/Lifecycle.vue'),
    meta: { title: '全生命周期', menuCode: 'lifecycle' }
  },
  {
    path: '/bridges',
    name: 'Bridges',
    component: () => import('@/views/Bridges.vue'),
    meta: { title: '桥梁管理', menuCode: 'bridges' }
  },
  {
    path: '/devices',
    name: 'Devices',
    component: () => import('@/views/Devices.vue'),
    meta: { title: '设备档案', menuCode: 'devices' }
  },
  {
    path: '/maintain',
    name: 'Maintain',
    component: () => import('@/views/Maintain.vue'),
    meta: { title: '保养管理', menuCode: 'maintain' }
  },
  {
    path: '/fault',
    name: 'Fault',
    component: () => import('@/views/Fault.vue'),
    meta: { title: '故障工单', menuCode: 'fault' }
  },
  {
    path: '/purchase',
    name: 'Purchase',
    component: () => import('@/views/Purchase.vue'),
    meta: { title: '采购入库', menuCode: 'purchase' }
  },
  {
    path: '/scrap',
    name: 'Scrap',
    component: () => import('@/views/Scrap.vue'),
    meta: { title: '报废鉴定', menuCode: 'scrap' }
  },
  {
    path: '/scrap/:id',
    name: 'ScrapDetail',
    component: () => import('@/views/ScrapDetail.vue'),
    meta: { title: 'TCO决策详情', menuCode: 'scrap' }
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import('@/views/Stats.vue'),
    meta: { title: '统计分析', menuCode: 'stats' }
  },
  {
    path: '/config',
    name: 'Config',
    component: () => import('@/views/Config.vue'),
    meta: { title: '系统配置', menuCode: 'config' }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@/views/Users.vue'),
    meta: { title: '用户管理', menuCode: 'users' }
  },
  {
    path: '/audit',
    name: 'Audit',
    component: () => import('@/views/Audit.vue'),
    meta: { title: '审计日志', menuCode: 'audit' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    redirect: '/dashboard'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router