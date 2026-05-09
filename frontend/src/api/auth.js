/**
 * 登录认证 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 用户登录
 * @param {Object} data - 登录数据
 * @param {string} data.username - 用户名
 * @param {string} data.password - 密码
 * @returns {Promise} 登录响应
 */
export function login(data) {
  return api.post('/auth/login', data)
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return api.post('/auth/logout')
}

/**
 * 获取菜单权限
 * @param {string} roleCode - 角色编码
 * @returns {Promise} 菜单列表
 */
export function getMenus(roleCode) {
  return api.get(`/auth/menus/${roleCode}`)
}