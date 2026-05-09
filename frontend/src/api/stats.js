/**
 * 统计分析 API
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import api from './index'

/**
 * 获取仪表盘完整数据
 * @returns {Promise} 仪表盘数据
 */
export function getDashboard() {
  return api.get('/stats/dashboard')
}

/**
 * 设备在用率
 * @returns {Promise} 在用率
 */
export function getInUseRate() {
  return api.get('/stats/in-use-rate')
}

/**
 * 设备完好率
 * @returns {Promise} 完好率
 */
export function getGoodConditionRate() {
  return api.get('/stats/good-condition-rate')
}

/**
 * 年故障率
 * @param {number} year - 年份
 * @returns {Promise} 故障率
 */
export function getFaultRate(year) {
  return api.get('/stats/fault-rate', { params: { year } })
}

/**
 * 保养完成率
 * @param {number} year - 年份
 * @returns {Promise} 完成率
 */
export function getMaintainCompletionRate(year) {
  return api.get('/stats/maintain-completion-rate', { params: { year } })
}

/**
 * 工单关闭率
 * @param {number} year - 年份
 * @returns {Promise} 关闭率
 */
export function getOrderCloseRate(year) {
  return api.get('/stats/order-close-rate', { params: { year } })
}

/**
 * MTBF（平均故障间隔时间）
 * @param {number} year - 年份
 * @returns {Promise} MTBF值
 */
export function getMTBF(year) {
  return api.get('/stats/mtbf', { params: { year } })
}

/**
 * MTTR（平均修复时间）
 * @param {number} year - 年份
 * @returns {Promise} MTTR值
 */
export function getMTTR(year) {
  return api.get('/stats/mttr', { params: { year } })
}

/**
 * 累计运维成本
 * @returns {Promise} 成本值
 */
export function getTotalOperationCost() {
  return api.get('/stats/total-operation-cost')
}

/**
 * 30天费用趋势
 * @returns {Promise} 费用趋势列表
 */
export function get30DayCostTrend() {
  return api.get('/stats/cost-trend')
}

/**
 * 待办事项
 * @returns {Promise} 待办列表
 */
export function getTodoItems() {
  return api.get('/stats/todo-items')
}

/**
 * 设备分类分布
 * @returns {Promise} 分类分布列表
 */
export function getCategoryDistribution() {
  return api.get('/stats/category-distribution')
}

/**
 * 故障排行
 * @param {number} limit - 限制数量
 * @returns {Promise} 故障排行列表
 */
export function getFaultRanking(limit) {
  return api.get('/stats/fault-ranking', { params: { limit } })
}

/**
 * TCO对比统计
 * @returns {Promise} TCO对比列表
 */
export function getTcoComparison() {
  return api.get('/stats/tco-comparison')
}