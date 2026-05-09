import router from './index'
import { useUserStore } from '@/stores/user'

/**
 * 路由守卫
 */

// 白名单路由（无需登录）
const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isLoggedIn = userStore.isLoggedIn
  const menus = userStore.menus

  // 设置页面标题
  document.title = to.meta.title || '桥梁设备全生命周期管理系统'

  // 白名单路由直接放行
  if (whiteList.includes(to.path)) {
    next()
    return
  }

  // 未登录跳转到登录页
  if (!isLoggedIn) {
    next('/login')
    return
  }

  // 检查菜单权限
  const menuCode = to.meta.menuCode
  if (menuCode && menus.length > 0 && !menus.includes(menuCode)) {
    // 无权限跳转到首页
    next('/dashboard')
    return
  }

  next()
})

export default router