<template>
  <div class="todo-list">
    <el-empty v-if="!normalizedItems.length" description="暂无待办事项" />
    <div v-else class="todo-group">
      <div v-for="item in normalizedItems" :key="item.key" class="todo-item">
        <div class="todo-badge" :class="item.colorClass">{{ item.typeLabel }}</div>
        <div class="todo-body">
          <div class="todo-title">{{ item.title }}</div>
          <div class="todo-desc">{{ item.desc }}</div>
        </div>
        <div class="todo-side">
          <strong>{{ item.countText }}</strong>
          <span v-if="item.deadline">截止 {{ formatDate(item.deadline) }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  items: {
    type: Array,
    default: () => []
  }
})

const normalizedItems = computed(() => {
  return props.items.map((item, index) => {
    const urgencyMap = {
      urgent: 'danger',
      high: 'warning',
      medium: 'primary',
      low: 'info'
    }

    return {
      key: item.relatedId || `${item.type || 'todo'}-${index}`,
      typeLabel: item.typeName || item.type || '待办',
      title: item.title || '待处理事项',
      desc: item.relatedId ? `关联编号：${item.relatedId}` : '请尽快处理该事项',
      deadline: item.deadline,
      countText: `${item.count || 1} 项`,
      colorClass: urgencyMap[item.urgency] || 'info'
    }
  })
})

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.todo-list {
  max-height: 360px;
  overflow-y: auto;
}

.todo-group {
  display: grid;
  gap: 12px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  background: #fff;
  transition: all 0.2s ease;
}

.todo-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 24px rgba(15, 23, 42, 0.06);
}

.todo-badge {
  min-width: 92px;
  padding: 8px 10px;
  border-radius: 12px;
  text-align: center;
  font-size: 12px;
  font-weight: 700;
}

.todo-badge.danger {
  background: #fee2e2;
  color: #dc2626;
}

.todo-badge.warning {
  background: #fef3c7;
  color: #b45309;
}

.todo-badge.primary {
  background: #dbeafe;
  color: #1d4ed8;
}

.todo-badge.info {
  background: #e2e8f0;
  color: #475569;
}

.todo-body {
  flex: 1;
}

.todo-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
}

.todo-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
}

.todo-side {
  min-width: 72px;
  text-align: right;
}

.todo-side strong {
  display: block;
  color: #0f172a;
}

.todo-side span {
  display: block;
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
}
</style>
