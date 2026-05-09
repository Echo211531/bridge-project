<template>
  <el-timeline>
    <el-timeline-item
      v-for="event in events"
      :key="event.id"
      :timestamp="formatTime(event.eventTime)"
      :type="getTimelineType(event.eventType)"
      placement="top"
    >
      <el-card>
        <h4>{{ event.title }}</h4>
        <p>{{ event.description }}</p>
        <div class="event-meta">
          <span class="cost" v-if="event.cost">
            成本: {{ event.cost.toFixed(2) }} 元
          </span>
          <span class="operator" v-if="event.operator">
            操作人: {{ event.operator }}
          </span>
        </div>
      </el-card>
    </el-timeline-item>
  </el-timeline>
</template>

<script setup>
/**
 * 生命周期时间轴组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
defineProps({
  events: Array
})

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function getTimelineType(eventType) {
  const types = {
    purchase: 'primary',
    commission: 'success',
    maintain: 'warning',
    repair: 'danger',
    inspect: 'info',
    scrap: 'info'
  }
  return types[eventType] || 'primary'
}
</script>

<style scoped>
.el-timeline {
  max-height: 500px;
  overflow-y: auto;
}

.el-card h4 {
  margin: 0 0 8px 0;
  color: #303133;
}

.el-card p {
  margin: 0;
  color: #606266;
  font-size: 14px;
}

.event-meta {
  margin-top: 8px;
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: #909399;
}
</script>