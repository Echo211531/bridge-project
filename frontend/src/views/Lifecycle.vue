<template>
  <div class="lifecycle-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="设备编码">
          <el-input v-model="searchForm.deviceCode" placeholder="输入设备编码搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 设备选择 -->
    <el-card class="device-select-card">
      <template #header>
        <span>选择设备查看生命周期</span>
      </template>
      <el-select v-model="selectedDeviceId" placeholder="请选择设备" filterable @change="loadLifecycleData">
        <el-option
          v-for="device in devices"
          :key="device.id"
          :label="`${device.deviceCode} - ${device.deviceName}`"
          :value="device.id"
        />
      </el-select>
    </el-card>

    <!-- 生命周期汇总 -->
    <el-card v-if="selectedDeviceId && summary" class="summary-card">
      <template #header>
        <span>生命周期汇总</span>
      </template>
      <el-descriptions :column="4" border>
        <el-descriptions-item label="设备编码">{{ summary.deviceCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ summary.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备分类">{{ summary.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="所属桥梁">{{ summary.bridgeName }}</el-descriptions-item>
        <el-descriptions-item label="购置成本">{{ summary.purchaseCost }} 元</el-descriptions-item>
        <el-descriptions-item label="累计保养费用">{{ summary.totalMaintainCost }} 元</el-descriptions-item>
        <el-descriptions-item label="累计维修费用">{{ summary.totalRepairCost }} 元</el-descriptions-item>
        <el-descriptions-item label="累计TCO">{{ summary.totalTco }} 元</el-descriptions-item>
        <el-descriptions-item label="保养次数">{{ summary.maintainCount }} 次</el-descriptions-item>
        <el-descriptions-item label="维修次数">{{ summary.repairCount }} 次</el-descriptions-item>
        <el-descriptions-item label="当前残值">{{ summary.currentResidualValue }} 元</el-descriptions-item>
        <el-descriptions-item label="当前状态">{{ summary.statusName }}</el-descriptions-item>
      </el-descriptions>
      <div class="tco-analysis" v-if="summary.tcoAnalysis">
        <el-tag type="info">{{ summary.tcoAnalysis }}</el-tag>
      </div>
    </el-card>

    <!-- 生命周期时间轴 -->
    <el-card v-if="selectedDeviceId && events.length" class="timeline-card">
      <template #header>
        <span>生命周期事件时间轴</span>
      </template>
      <LifecycleTimeline :events="events" />
    </el-card>
  </div>
</template>

<script setup>
/**
 * 全生命周期页面
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref, reactive, onMounted } from 'vue'
import { listDevices } from '@/api/device'
import { getEvents, getSummary } from '@/api/lifecycle'
import LifecycleTimeline from '@/components/business/LifecycleTimeline.vue'

const searchForm = reactive({
  deviceCode: ''
})

const devices = ref([])
const selectedDeviceId = ref('')
const events = ref([])
const summary = ref(null)

onMounted(async () => {
  await loadDevices()
})

async function loadDevices() {
  try {
    const data = await listDevices({ pageNum: 1, pageSize: 100 })
    devices.value = data.records || []
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

async function loadLifecycleData() {
  if (!selectedDeviceId.value) {
    events.value = []
    summary.value = null
    return
  }

  try {
    const [eventsData, summaryData] = await Promise.all([
      getEvents(selectedDeviceId.value),
      getSummary(selectedDeviceId.value)
    ])
    events.value = eventsData || []
    summary.value = summaryData || null
  } catch (error) {
    console.error('加载生命周期数据失败:', error)
  }
}

async function handleSearch() {
  try {
    const data = await listDevices({
      pageNum: 1,
      pageSize: 100,
      deviceCode: searchForm.deviceCode
    })
    devices.value = data.records || []
  } catch (error) {
    console.error('搜索失败:', error)
  }
}
</script>

<style scoped>
.lifecycle-page {
  padding: 0;
}

.search-card,
.device-select-card,
.summary-card,
.timeline-card {
  margin-bottom: 20px;
}

.tco-analysis {
  margin-top: 16px;
}
</style>