<template>
  <div class="todo-list">
    <el-empty v-if="!items.length" description="暂无待办事项" />
    <div v-else>
      <div v-for="item in items" :key="item.type" class="todo-item">
        <div class="todo-header">
          <el-tag :type="getUrgencyType(item.urgency)" size="small">
            {{ item.typeName }}
          </el-tag>
          <span class="todo-count">{{ item.count }} 项</span>
        </div>
        <div class="todo-title">{{ item.title }}</div>
        <div class="todo-deadline" v-if="item.deadline">
          截止: {{ formatDate(item.deadline) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 待办事项列表组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
defineProps({
  items: Array
})

function getUrgencyType(urgency) {
  const types = {
    urgent: 'danger',
    high: 'warning',
    medium: 'primary',
    low: 'info'
  }
  return types[urgency] || 'info'
}

function formatDate(date) {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
</script>

<style scoped>
.todo-list {
  max-height: 300px;
  overflow-y: auto;
}

.todo-item {
  padding: 12px;
  border-bottom: 1px solid #ebeef5;
}

.todo-item:last-child {
  border-bottom: none;
}

.todo-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 8px;
}

.todo-count {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.todo-title {
  font-size: 14px;
  color: #606266;
}

.todo-deadline {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}
</script>