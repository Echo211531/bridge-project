/**
 * 故障工单 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 工单列表分页查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.status - 状态
 * @returns {Promise} 工单列表
 */
export function listFaults(params) {
  return api.get('/faults', { params })
}

/**
 * 故障申报（设备状态变更、生成事件）
 * @param {Object} data - 工单数据
 * @returns {Promise}
 */
export function reportFault(data) {
  return api.post('/faults', data)
}

/**
 * 工单处理（状态流转）
 * @param {string} id - 工单ID
 * @param {string} status - 新状态
 * @returns {Promise}
 */
export function processOrder(id, status) {
  return api.put(`/faults/${id}/process`, null, { params: { status } })
}

/**
 * 工单关闭（设备状态恢复、MTTR计算）
 * @param {string} id - 工单ID
 * @param {Object} data - 关闭数据
 * @returns {Promise}
 */
export function closeOrder(id, data) {
  return api.put(`/faults/${id}/close`, data)
}

/**
 * MTTR计算（平均修复时间）
 * @param {number} year - 年份
 * @returns {Promise} MTTR值
 */
export function getMTTR(year) {
  return api.get('/faults/mttr', { params: { year } })
}

/**
 * MTBF计算（平均故障间隔时间）
 * @param {string} deviceId - 设备ID
 * @returns {Promise} MTBF值
 */
export function getMTBF(deviceId) {
  return api.get('/faults/mtbf', { params: { deviceId } })
}

/**
 * 年故障率统计
 * @param {number} year - 年份
 * @returns {Promise} 故障率
 */
export function getFaultRate(year) {
  return api.get('/faults/fault-rate', { params: { year } })
}

/**
 * 工单关闭率统计
 * @param {number} year - 年份
 * @returns {Promise} 关闭率
 */
export function getCloseRate(year) {
  return api.get('/faults/close-rate', { params: { year } })
}