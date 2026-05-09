import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

/**
 * 用户状态管理
 */
export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref(null)

  // 菜单权限
  const menus = ref([])

  // 是否已登录
  const isLoggedIn = computed(() => userInfo.value !== null)

  // 用户名
  const username = computed(() => userInfo.value?.username || '')

  // 角色编码
  const roleCode = computed(() => userInfo.value?.roleCode || '')

  // 设置用户信息
  function setUserInfo(info) {
    userInfo.value = info
    menus.value = info.menus || []
    // 存储到 sessionStorage
    sessionStorage.setItem('userInfo', JSON.stringify(info))
  }

  // 清除用户信息
  function clearUserInfo() {
    userInfo.value = null
    menus.value = []
    sessionStorage.removeItem('userInfo')
  }

  // 从 sessionStorage 恢复用户信息
  function restoreUserInfo() {
    const stored = sessionStorage.getItem('userInfo')
    if (stored) {
      const info = JSON.parse(stored)
      userInfo.value = info
      menus.value = info.menus || []
    }
  }

  return {
    userInfo,
    menus,
    isLoggedIn,
    username,
    roleCode,
    setUserInfo,
    clearUserInfo,
    restoreUserInfo
  }
})