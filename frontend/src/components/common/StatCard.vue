<template>
  <div class="stat-card">
    <div class="icon" :style="{ backgroundColor: color }">
      <el-icon :size="24">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="content">
      <div class="title">{{ title }}</div>
      <div class="value">
        {{ value }}<span class="unit">{{ unit }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
/**
 * 统计卡片组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
defineProps({
  title: String,
  value: [Number, String],
  unit: String,
  icon: String,
  color: String
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #fff;
  margin-right: 16px;
}

.content {
  flex: 1;
}

.title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.unit {
  font-size: 14px;
  color: #909399;
  margin-left: 4px;
}
</style>