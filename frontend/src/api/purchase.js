/**
 * 采购订单 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 订单列表查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.status - 状态
 * @returns {Promise} 订单列表
 */
export function listPurchases(params) {
  return api.get('/purchases', { params })
}

/**
 * 订单详情查询
 * @param {string} id - 订单ID
 * @returns {Promise} 订单详情
 */
export function getPurchase(id) {
  return api.get(`/purchases/${id}`)
}

/**
 * 订单创建（总金额计算）
 * @param {Object} data - 订单数据
 * @returns {Promise}
 */
export function createPurchase(data) {
  return api.post('/purchases', data)
}

/**
 * 订单状态流转（pending→shipping→received）
 * @param {string} id - 订单ID
 * @param {Object} data - 状态数据
 * @returns {Promise}
 */
export function updateStatus(id, data) {
  return api.put(`/purchases/${id}/status`, data)
}