/**
 * 系统配置 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 配置列表查询
 * @returns {Promise} 配置列表
 */
export function listConfigs() {
  return api.get('/config')
}

/**
 * 根据键查询配置
 * @param {string} key - 配置键
 * @returns {Promise} 配置信息
 */
export function getConfig(key) {
  return api.get(`/config/${key}`)
}

/**
 * 配置更新
 * @param {Object} data - 配置数据
 * @returns {Promise}
 */
export function updateConfig(data) {
  return api.put('/config', data)
}