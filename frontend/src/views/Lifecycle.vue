<template>
  <div class="lifecycle-page app-page fade-in">
    <section class="page-header">
      <div class="page-title-group">
        <h1>全生命周期</h1>
        <p>覆盖采购入库、投入使用、例行保养、故障维修到报废处置的全过程事件流水。</p>
      </div>
    </section>

    <section class="intro-banner">
      <div class="intro-icon">♻️</div>
      <div>
        <h3>设备全生命周期视图</h3>
        <p>通过左侧设备列表快速定位任一设备，右侧查看其生命周期概览、成本汇总和事件时间轴。</p>
      </div>
    </section>

    <el-card class="search-card soft-panel">
      <div class="filter-row">
        <el-input v-model="filters.keyword" clearable placeholder="搜索设备名称或编码" class="filter-item" />
        <el-select v-model="filters.bridgeId" clearable placeholder="全部桥梁" class="filter-item">
          <el-option v-for="bridge in bridges" :key="bridge.id" :label="bridge.bridgeName" :value="bridge.id" />
        </el-select>
        <el-button type="primary" @click="applyFilters">筛选</el-button>
        <el-button @click="resetFilters">重置</el-button>
        <span class="filter-meta">共 {{ filteredDevices.length }} 台设备</span>
      </div>
    </el-card>

    <section class="lifecycle-layout">
      <div class="device-panel">
        <div class="device-panel-head">
          <h3>设备列表</h3>
          <span>点击查看完整时间轴</span>
        </div>
        <div class="device-list scrollbar-thin">
          <button
            v-for="device in filteredDevices"
            :key="device.id"
            type="button"
            class="device-item"
            :class="{ active: selectedDeviceId === device.id }"
            @click="selectDevice(device.id)"
          >
            <div class="device-main">
              <div>
                <div class="device-name">{{ device.deviceName }}</div>
                <div class="device-code">{{ device.deviceCode }}</div>
              </div>
              <span class="device-status" :class="statusClass(device.status)">
                {{ device.statusName || device.status }}
              </span>
            </div>
            <div class="device-meta">{{ device.bridgeName }} · {{ device.categoryName }}</div>
            <div class="device-footer">
              <span>残值：{{ formatMoney(device.residualValue) }}</span>
              <span>采购：{{ formatShortDate(device.purchaseDate) }}</span>
            </div>
          </button>
          <div v-if="!filteredDevices.length" class="empty-device">暂无匹配设备</div>
        </div>
      </div>

      <div class="timeline-content">
        <div v-if="selectedDevice" class="timeline-wrapper">
          <LifecycleTimeline :device="selectedDevice" :summary="summary" :events="events" />
        </div>
        <div v-else class="empty-selection">
          <div class="empty-icon">👈</div>
          <h3>请选择一台设备</h3>
          <p>从左侧列表中选择设备后，这里将展示其生命周期概览和事件时间轴。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { listDevices } from '@/api/device'
import { listBridges } from '@/api/bridge'
import { getEvents, getSummary } from '@/api/lifecycle'
import LifecycleTimeline from '@/components/business/LifecycleTimeline.vue'

const route = useRoute()

const filters = reactive({
  keyword: '',
  bridgeId: ''
})

const devices = ref([])
const bridges = ref([])
const selectedDeviceId = ref('')
const events = ref([])
const summary = ref(null)

const filteredDevices = computed(() => {
  return devices.value.filter(device => {
    const keyword = filters.keyword.trim()
    const matchedKeyword = !keyword || device.deviceName?.includes(keyword) || device.deviceCode?.includes(keyword)
    const matchedBridge = !filters.bridgeId || device.bridgeId === filters.bridgeId
    return matchedKeyword && matchedBridge
  })
})

const selectedDevice = computed(() => {
  return devices.value.find(device => device.id === selectedDeviceId.value) || null
})

onMounted(async () => {
  await Promise.all([loadDevices(), loadBridges()])

  const initialId = typeof route.query.deviceId === 'string' ? route.query.deviceId : ''
  if (initialId) {
    await selectDevice(initialId)
  } else if (devices.value.length) {
    await selectDevice(devices.value[0].id)
  }
})

async function loadDevices() {
  try {
    const data = await listDevices({ pageNum: 1, pageSize: 200 })
    devices.value = data.records || []
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

async function loadBridges() {
  try {
    const data = await listBridges({ pageNum: 1, pageSize: 200 })
    bridges.value = data.records || []
  } catch (error) {
    console.error('加载桥梁列表失败:', error)
  }
}

async function selectDevice(deviceId) {
  selectedDeviceId.value = deviceId
  if (!deviceId) {
    events.value = []
    summary.value = null
    return
  }

  try {
    const [eventsData, summaryData] = await Promise.all([
      getEvents(deviceId),
      getSummary(deviceId)
    ])
    events.value = eventsData || []
    summary.value = summaryData || null
  } catch (error) {
    console.error('加载生命周期数据失败:', error)
  }
}

async function applyFilters() {
  if (!filteredDevices.value.length) {
    selectedDeviceId.value = ''
    summary.value = null
    events.value = []
    return
  }

  if (!filteredDevices.value.some(device => device.id === selectedDeviceId.value)) {
    await selectDevice(filteredDevices.value[0].id)
  }
}

async function resetFilters() {
  filters.keyword = ''
  filters.bridgeId = ''

  if (devices.value.length && !selectedDeviceId.value) {
    await selectDevice(devices.value[0].id)
  }
}

function formatMoney(value) {
  const num = Number(value)
  return Number.isFinite(num) ? `${num.toFixed(2)} 元` : '0.00 元'
}

function formatShortDate(value) {
  return value ? String(value).slice(0, 10) : '-'
}

function statusClass(status) {
  const map = {
    in_use: 'success',
    in_stock: 'info',
    maintaining: 'warning',
    fault: 'danger',
    scrapped: 'muted'
  }
  return map[status] || 'info'
}
</script>

<style scoped>
.intro-banner {
  display: flex;
  gap: 16px;
  align-items: center;
  padding: 20px 22px;
  border: 1px solid rgba(37, 99, 235, 0.15);
  border-radius: 22px;
  background: linear-gradient(135deg, rgba(219, 234, 254, 0.96) 0%, rgba(239, 246, 255, 0.95) 100%);
}

.intro-icon {
  width: 52px;
  height: 52px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2563eb 0%, #4f46e5 100%);
  color: #fff;
  font-size: 24px;
}

.intro-banner h3 {
  margin: 0;
}

.intro-banner p {
  margin: 8px 0 0;
  color: #475569;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.filter-item {
  width: 240px;
}

.filter-meta {
  margin-left: auto;
  font-size: 13px;
  color: #64748b;
}

.lifecycle-layout {
  display: grid;
  grid-template-columns: 360px minmax(0, 1fr);
  gap: 20px;
}

.device-panel,
.timeline-content {
  min-height: 720px;
}

.device-panel {
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.95);
  overflow: hidden;
}

.device-panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  border-bottom: 1px solid #e2e8f0;
}

.device-panel-head h3 {
  margin: 0;
}

.device-panel-head span {
  font-size: 12px;
  color: #64748b;
}

.device-list {
  max-height: 660px;
  padding: 12px;
  overflow-y: auto;
}

.device-item {
  width: 100%;
  padding: 16px;
  margin-bottom: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 20px;
  background: #fff;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.device-item:hover {
  border-color: #93c5fd;
  box-shadow: 0 16px 28px rgba(15, 23, 42, 0.06);
}

.device-item.active {
  background: linear-gradient(135deg, rgba(239, 246, 255, 0.96) 0%, rgba(224, 231, 255, 0.9) 100%);
  border-color: #60a5fa;
}

.device-main {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.device-name {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.device-code {
  margin-top: 6px;
  font-size: 12px;
  color: #64748b;
}

.device-status {
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  white-space: nowrap;
}

.device-status.success { background: #dcfce7; color: #15803d; }
.device-status.info { background: #e2e8f0; color: #475569; }
.device-status.warning { background: #fef3c7; color: #b45309; }
.device-status.danger { background: #fee2e2; color: #dc2626; }
.device-status.muted { background: #e5e7eb; color: #4b5563; }

.device-meta,
.device-footer {
  margin-top: 10px;
  font-size: 12px;
  color: #64748b;
}

.device-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.timeline-wrapper {
  height: 100%;
}

.timeline-content {
  border-radius: 24px;
}

.empty-selection,
.empty-device {
  display: flex;
  min-height: 100%;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  text-align: center;
  color: #94a3b8;
}

.empty-icon {
  font-size: 52px;
  margin-bottom: 12px;
}

@media (max-width: 1100px) {
  .lifecycle-layout {
    grid-template-columns: 1fr;
  }

  .device-panel,
  .timeline-content {
    min-height: auto;
  }

  .device-list {
    max-height: 360px;
  }
}

@media (max-width: 768px) {
  .filter-item {
    width: 100%;
  }

  .filter-meta {
    margin-left: 0;
  }

  .device-main,
  .device-footer {
    flex-direction: column;
  }
}
</style>
