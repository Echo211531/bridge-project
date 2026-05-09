/**
 * 保养管理 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 保养计划列表查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.status - 状态
 * @returns {Promise} 计划列表
 */
export function listPlans(params) {
  return api.get('/maintain/plans', { params })
}

/**
 * 保养记录列表查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.deviceId - 设备ID
 * @returns {Promise} 记录列表
 */
export function listRecords(params) {
  return api.get('/maintain/records', { params })
}

/**
 * 保养记录上报（生成生命周期事件）
 * @param {Object} data - 记录数据
 * @returns {Promise}
 */
export function submitRecord(data) {
  return api.post('/maintain/records', data)
}

/**
 * 保养完成率统计
 * @param {number} year - 年份
 * @returns {Promise} 完成率
 */
export function getCompletionRate(year) {
  return api.get('/maintain/completion-rate', { params: { year } })
}