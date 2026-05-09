/**
 * 审计日志 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 审计日志分页查询
 * @param {Object} params - 查询参数
 * @param {number} params.pageNum - 页码
 * @param {number} params.pageSize - 每页数量
 * @param {string} params.action - 操作类型
 * @param {string} params.operator - 操作人
 * @returns {Promise} 日志列表
 */
export function listLogs(params) {
  return api.get('/audit', { params })
}