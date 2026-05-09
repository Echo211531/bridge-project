<template>
  <el-tag :type="tagType" effect="light">
    {{ statusName }}
  </el-tag>
</template>

<script setup>
/**
 * 状态标签组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { computed } from 'vue'

const props = defineProps({
  status: String,
  type: {
    type: String,
    default: 'default' // default, device, fault, maintain, purchase
  }
})

// 状态名称映射
const statusNames = {
  // 用户状态
  active: '正常',
  frozen: '冻结',
  // 设备状态
  in_use: '在用',
  in_stock: '库存',
  maintaining: '保养中',
  fault: '故障',
  scrapped: '已报废',
  disabled: '禁用',
  // 工单状态
  open: '待处理',
  processing: '处理中',
  closed: '已关闭',
  // 保养状态
  pending: '待保养',
  completed: '已完成',
  overdue: '超期',
  // 采购状态
  pending: '待发货',
  shipping: '运输中',
  received: '已入库',
  // 鉴定状态
  pending_approval: '待审批',
  approved: '已通过',
  rejected: '已拒绝'
}

// 状态类型映射
const statusTypes = {
  active: 'success',
  frozen: 'danger',
  in_use: 'success',
  in_stock: 'info',
  maintaining: 'warning',
  fault: 'danger',
  scrapped: 'info',
  disabled: 'info',
  open: 'danger',
  processing: 'warning',
  closed: 'success',
  pending: 'warning',
  completed: 'success',
  overdue: 'danger',
  shipping: 'warning',
  received: 'success',
  pending_approval: 'warning',
  approved: 'success',
  rejected: 'danger'
}

const statusName = computed(() => statusNames[props.status] || props.status)

const tagType = computed(() => statusTypes[props.status] || 'info')
</script>