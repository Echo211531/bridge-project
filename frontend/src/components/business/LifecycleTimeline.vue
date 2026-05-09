<template>
  <div class="timeline-layout" v-if="device">
    <section class="device-summary">
      <div class="summary-top">
        <div>
          <div class="summary-code">{{ summary?.deviceCode || device.deviceCode }}</div>
          <h3>{{ summary?.deviceName || device.deviceName }}</h3>
          <p>{{ summary?.bridgeName || device.bridgeName }} · {{ summary?.categoryName || device.categoryName }}</p>
        </div>
        <StatusTag :status="summary?.currentStatus || device.status" />
      </div>

      <div class="summary-grid">
        <div class="summary-item">
          <span>购置成本</span>
          <strong>{{ formatMoney(summary?.purchaseCost || device.purchaseCost) }}</strong>
        </div>
        <div class="summary-item">
          <span>累计保养</span>
          <strong>{{ formatMoney(summary?.totalMaintainCost) }}</strong>
        </div>
        <div class="summary-item">
          <span>累计维修</span>
          <strong>{{ formatMoney(summary?.totalRepairCost) }}</strong>
        </div>
        <div class="summary-item">
          <span>累计 TCO</span>
          <strong>{{ formatMoney(summary?.totalTco) }}</strong>
        </div>
        <div class="summary-item">
          <span>保养次数</span>
          <strong>{{ summary?.maintainCount ?? 0 }} 次</strong>
        </div>
        <div class="summary-item">
          <span>维修次数</span>
          <strong>{{ summary?.repairCount ?? 0 }} 次</strong>
        </div>
        <div class="summary-item">
          <span>当前残值</span>
          <strong>{{ formatMoney(summary?.currentResidualValue || device.residualValue) }}</strong>
        </div>
        <div class="summary-item">
          <span>TCO 分析</span>
          <strong>{{ summary?.tcoAnalysis || '待生成' }}</strong>
        </div>
      </div>
    </section>

    <section class="timeline-panel">
      <div class="timeline-head">
        <div>
          <h4>生命周期事件流水</h4>
          <p>覆盖采购、投用、保养、维修与报废等全过程事件</p>
        </div>
        <span>{{ events.length }} 个事件</span>
      </div>

      <div class="timeline-body scrollbar-thin">
        <div v-if="!events.length" class="empty-state">暂无生命周期事件</div>
        <div v-for="event in events" :key="event.id" class="timeline-item">
          <div class="timeline-dot" :class="eventColorClass(event.eventType)">
            {{ eventIcon(event.eventType) }}
          </div>
          <div class="timeline-card">
            <div class="timeline-card-head">
              <strong>{{ event.title || event.eventTypeName }}</strong>
              <span>{{ formatTime(event.eventTime) }}</span>
            </div>
            <p>{{ event.description || '暂无描述' }}</p>
            <div class="timeline-meta">
              <span>{{ event.eventTypeName || event.eventType }}</span>
              <span v-if="event.operator">操作人：{{ event.operator }}</span>
              <span v-if="event.cost">费用：{{ formatMoney(event.cost) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import StatusTag from '@/components/common/Tag.vue'

defineProps({
  device: {
    type: Object,
    default: null
  },
  summary: {
    type: Object,
    default: null
  },
  events: {
    type: Array,
    default: () => []
  }
})

function formatTime(time) {
  if (!time) return '-'
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function formatMoney(value) {
  const num = Number(value)
  return Number.isFinite(num) ? `${num.toFixed(2)} 元` : '0.00 元'
}

function eventColorClass(eventType) {
  const colorMap = {
    purchase: 'purchase',
    commission: 'commission',
    maintain: 'maintain',
    repair: 'repair',
    inspect: 'inspect',
    scrap: 'scrap'
  }
  return colorMap[eventType] || 'inspect'
}

function eventIcon(eventType) {
  const iconMap = {
    purchase: '📦',
    commission: '🚀',
    maintain: '🔧',
    repair: '🛠',
    inspect: '📋',
    scrap: '🗑'
  }
  return iconMap[eventType] || '•'
}
</script>

<style scoped>
.timeline-layout {
  display: grid;
  gap: 18px;
}

.device-summary,
.timeline-panel {
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.95);
}

.device-summary {
  padding: 22px;
  background: linear-gradient(135deg, #eff6ff 0%, #f8fafc 100%);
}

.summary-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.summary-code {
  font-size: 12px;
  color: #64748b;
}

.summary-top h3 {
  margin: 10px 0 8px;
  font-size: 24px;
  color: #0f172a;
}

.summary-top p {
  margin: 0;
  color: #475569;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}

.summary-item {
  padding: 14px 16px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.88);
}

.summary-item span {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  color: #64748b;
}

.summary-item strong {
  color: #0f172a;
}

.timeline-panel {
  padding: 22px;
}

.timeline-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.timeline-head h4 {
  margin: 0;
  font-size: 18px;
}

.timeline-head p {
  margin: 8px 0 0;
  font-size: 13px;
  color: #64748b;
}

.timeline-head span {
  font-size: 12px;
  color: #64748b;
}

.timeline-body {
  max-height: 520px;
  margin-top: 20px;
  overflow-y: auto;
  padding-left: 12px;
  border-left: 2px solid #e2e8f0;
}

.timeline-item {
  position: relative;
  padding-left: 28px;
  margin-bottom: 18px;
}

.timeline-dot {
  position: absolute;
  left: -14px;
  top: 4px;
  width: 28px;
  height: 28px;
  border-radius: 999px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  box-shadow: 0 10px 18px rgba(15, 23, 42, 0.12);
}

.timeline-dot.purchase { background: #7c3aed; }
.timeline-dot.commission { background: #2563eb; }
.timeline-dot.maintain { background: #16a34a; }
.timeline-dot.repair { background: #dc2626; }
.timeline-dot.inspect { background: #64748b; }
.timeline-dot.scrap { background: #475569; }

.timeline-card {
  padding: 16px 18px;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  background: #fff;
}

.timeline-card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.timeline-card-head strong {
  color: #0f172a;
}

.timeline-card-head span {
  font-size: 12px;
  color: #64748b;
}

.timeline-card p {
  margin: 10px 0 0;
  line-height: 1.7;
  color: #475569;
}

.timeline-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 12px;
  font-size: 12px;
  color: #64748b;
}

.empty-state {
  padding: 40px 0;
  text-align: center;
  color: #94a3b8;
}

@media (max-width: 1200px) {
  .summary-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .summary-grid {
    grid-template-columns: 1fr;
  }

  .summary-top,
  .timeline-head,
  .timeline-card-head {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
