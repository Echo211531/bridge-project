<template>
  <div class="stat-card">
    <div class="stat-accent" :style="{ background: gradientColor }"></div>
    <div class="icon" :style="{ background: gradientColor }">
      <el-icon :size="22">
        <component :is="icon" />
      </el-icon>
    </div>
    <div class="content">
      <div class="title">{{ title }}</div>
      <div class="value">
        {{ value }}<span class="unit">{{ unit }}</span>
      </div>
      <div v-if="sub" class="sub">{{ sub }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: String,
  value: [Number, String],
  unit: String,
  icon: String,
  color: String,
  sub: String
})

const gradientColor = computed(() => {
  const base = props.color || '#2563eb'
  return `linear-gradient(135deg, ${base} 0%, rgba(15, 23, 42, 0.9) 180%)`
})
</script>

<style scoped>
.stat-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  min-height: 124px;
  padding: 24px 22px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 22px;
  box-shadow: 0 18px 30px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}

.stat-accent {
  position: absolute;
  inset: auto 0 0 0;
  height: 4px;
  opacity: 0.9;
}

.icon {
  width: 52px;
  height: 52px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 18px;
  color: #fff;
  box-shadow: 0 12px 24px rgba(37, 99, 235, 0.24);
}

.content {
  flex: 1;
}

.title {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 10px;
}

.value {
  font-size: 28px;
  font-weight: 700;
  color: #0f172a;
}

.unit {
  font-size: 13px;
  color: #64748b;
  margin-left: 6px;
}

.sub {
  margin-top: 10px;
  font-size: 12px;
  color: #64748b;
  line-height: 1.6;
}
</style>
