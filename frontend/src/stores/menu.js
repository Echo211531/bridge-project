import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * 菜单配置
 */
const allMenus = [
  { code: 'dashboard', name: '首页仪表盘', icon: 'HomeFilled', path: '/dashboard' },
  { code: 'lifecycle', name: '全生命周期', icon: 'Clock', path: '/lifecycle' },
  { code: 'bridges', name: '桥梁管理', icon: 'OfficeBuilding', path: '/bridges' },
  { code: 'devices', name: '设备档案', icon: 'Box', path: '/devices' },
  { code: 'maintain', name: '保养管理', icon: 'Tools', path: '/maintain' },
  { code: 'fault', name: '故障工单', icon: 'Warning', path: '/fault' },
  { code: 'purchase', name: '采购入库', icon: 'ShoppingCart', path: '/purchase' },
  { code: 'scrap', name: '报废鉴定', icon: 'Delete', path: '/scrap' },
  { code: 'stats', name: '统计分析', icon: 'DataLine', path: '/stats' },
  { code: 'config', name: '系统配置', icon: 'Setting', path: '/config' },
  { code: 'users', name: '用户管理', icon: 'User', path: '/users' },
  { code: 'audit', name: '审计日志', icon: 'Document', path: '/audit' }
]

/**
 * 菜单状态管理
 */
export const useMenuStore = defineStore('menu', () => {
  // 当前菜单列表（根据权限过滤）
  const menuList = ref([])

  // 当前激活菜单
  const activeMenu = ref('/dashboard')

  // 是否折叠
  const isCollapse = ref(false)

  // 根据权限设置菜单列表
  function setMenusByPermission(permissionMenus) {
    menuList.value = allMenus.filter(menu =>
      permissionMenus.includes(menu.code)
    )
  }

  // 设置激活菜单
  function setActiveMenu(path) {
    activeMenu.value = path
  }

  // 切换折叠状态
  function toggleCollapse() {
    isCollapse.value = !isCollapse.value
  }

  return {
    menuList,
    activeMenu,
    isCollapse,
    setMenusByPermission,
    setActiveMenu,
    toggleCollapse
  }
})