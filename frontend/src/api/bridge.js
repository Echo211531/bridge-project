/**
 * 桥梁管理 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 桥梁列表查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.bridgeName - 桥梁名称
 * @returns {Promise} 桥梁列表
 */
export function listBridges(params) {
  return api.get('/bridges', { params })
}

/**
 * 桥梁详情查询（含AADT历史）
 * @param {string} id - 桥梁ID
 * @returns {Promise} 桥梁详情
 */
export function getBridge(id) {
  return api.get(`/bridges/${id}`)
}

/**
 * 创建桥梁
 * @param {Object} data - 桥梁数据
 * @returns {Promise}
 */
export function createBridge(data) {
  return api.post('/bridges', data)
}

/**
 * 更新桥梁
 * @param {string} id - 桥梁ID
 * @param {Object} data - 桥梁数据
 * @returns {Promise}
 */
export function updateBridge(id, data) {
  return api.put(`/bridges/${id}`, data)
}

/**
 * 更新AADT
 * @param {string} id - 桥梁ID
 * @param {Object} data - AADT数据
 * @returns {Promise}
 */
export function updateAadt(id, data) {
  return api.put(`/bridges/${id}/aadt`, data)
}

/**
 * 查询AADT历史
 * @param {string} id - 桥梁ID
 * @returns {Promise} AADT历史列表
 */
export function getAadtHistory(id) {
  return api.get(`/bridges/${id}/aadt-history`)
}