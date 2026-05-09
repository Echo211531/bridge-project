/**
 * 生命周期 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 设备生命周期事件查询
 * @param {string} deviceId - 设备ID
 * @returns {Promise} 事件列表
 */
export function getEvents(deviceId) {
  return api.get(`/lifecycle/events/${deviceId}`)
}

/**
 * 生命周期汇总查询（累计TCO）
 * @param {string} deviceId - 设备ID
 * @returns {Promise} 汇总数据
 */
export function getSummary(deviceId) {
  return api.get(`/lifecycle/summary/${deviceId}`)
}

/**
 * 生命周期成本追溯
 * @param {string} deviceId - 设备ID
 * @returns {Promise} 成本追溯详情
 */
export function getCostTrace(deviceId) {
  return api.get(`/lifecycle/trace/${deviceId}`)
}