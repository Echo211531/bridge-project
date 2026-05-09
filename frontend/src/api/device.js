/**
 * 设备管理 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 设备列表分页查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.category - 分类
 * @param {string} params.bridgeId - 桥梁ID
 * @param {string} params.status - 状态
 * @returns {Promise} 设备列表
 */
export function listDevices(params) {
  return api.get('/devices', { params })
}

/**
 * 设备详情查询（含残值计算）
 * @param {string} id - 设备ID
 * @returns {Promise} 设备详情
 */
export function getDevice(id) {
  return api.get(`/devices/${id}`)
}

/**
 * 创建设备（自动生成编码和生命周期事件）
 * @param {Object} data - 设备数据
 * @returns {Promise}
 */
export function createDevice(data) {
  return api.post('/devices', data)
}

/**
 * 更新设备
 * @param {string} id - 设备ID
 * @param {Object} data - 设备数据
 * @returns {Promise}
 */
export function updateDevice(id, data) {
  return api.put(`/devices/${id}`, data)
}

/**
 * 超期保养设备查询
 * @returns {Promise} 超期设备列表
 */
export function listOverdueDevices() {
  return api.get('/devices/overdue')
}

/**
 * 设备分类查询
 * @returns {Promise} 分类列表
 */
export function listCategories() {
  return api.get('/devices/categories')
}

/**
 * 设备状态流转
 * @param {string} id - 设备ID
 * @param {string} status - 新状态
 * @returns {Promise}
 */
export function changeDeviceStatus(id, status) {
  return api.put(`/devices/${id}/status`, null, { params: { status } })
}