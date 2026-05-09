/**
 * 用户管理 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 用户列表分页查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.username - 用户名
 * @param {string} params.status - 状态
 * @returns {Promise} 用户列表
 */
export function listUsers(params) {
  return api.get('/users', { params })
}

/**
 * 用户详情查询
 * @param {string} id - 用户ID
 * @returns {Promise} 用户信息
 */
export function getUser(id) {
  return api.get(`/users/${id}`)
}

/**
 * 创建用户
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export function createUser(data) {
  return api.post('/users', data)
}

/**
 * 更新用户
 * @param {string} id - 用户ID
 * @param {Object} data - 用户数据
 * @returns {Promise}
 */
export function updateUser(id, data) {
  return api.put(`/users/${id}`, data)
}

/**
 * 启用/禁用用户
 * @param {string} id - 用户ID
 * @param {string} status - 状态
 * @returns {Promise}
 */
export function toggleUserStatus(id, status) {
  return api.put(`/users/${id}/status`, null, { params: { status } })
}

/**
 * 重置密码
 * @param {string} id - 用户ID
 * @param {string} newPassword - 新密码
 * @returns {Promise}
 */
export function resetPassword(id, newPassword) {
  return api.put(`/users/${id}/password`, null, { params: { newPassword } })
}

/**
 * 删除用户
 * @param {string} id - 用户ID
 * @returns {Promise}
 */
export function deleteUser(id) {
  return api.delete(`/users/${id}`)
}

/**
 * 角色列表查询
 * @returns {Promise} 角色列表
 */
export function listRoles() {
  return api.get('/roles')
}