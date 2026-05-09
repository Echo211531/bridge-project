/**
 * 报废鉴定 API（TCO核心功能）
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 待鉴定设备筛选（寿命警戒线、故障次数）
 * @returns {Promise} 候选设备列表
 */
export function listCandidates() {
  return api.get('/scrap/candidates')
}

/**
 * 获取TCO决策面板数据（三栏决策面板）
 * @param {string} deviceId - 设备ID
 * @returns {Promise} TCO决策数据
 */
export function getTcoDecision(deviceId) {
  return api.get(`/scrap/tco/${deviceId}`)
}

/**
 * 提交鉴定结论
 * @param {Object} data - 鉴定数据
 * @returns {Promise}
 */
export function submitDecision(data) {
  return api.post('/scrap/decision', data)
}

/**
 * 查询鉴定历史
 * @param {string} deviceId - 设备ID
 * @returns {Promise} 鉴定历史列表
 */
export function getDecisionHistory(deviceId) {
  return api.get(`/scrap/history/${deviceId}`)
}

/**
 * 报废审批
 * @param {string} decisionId - 鉴定ID
 * @param {Object} params - 审批参数
 * @returns {Promise}
 */
export function approveDecision(decisionId, params) {
  return api.post(`/scrap/approve/${decisionId}`, null, { params })
}