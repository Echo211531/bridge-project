<template>
  <div class="dashboard app-page fade-in">
    <section class="page-header">
      <div class="page-title-group">
        <h1>首页仪表盘</h1>
        <p>聚合桥梁设备全生命周期的核心指标、费用趋势和待办事项。</p>
      </div>
      <div class="hero-note">
        <strong>{{ formatNumber(overview.totalDevices) }}</strong>
        <span>设备总量</span>
      </div>
    </section>

    <section class="metric-grid-4">
      <StatCard title="设备总量" :value="formatNumber(overview.totalDevices)" unit="台" icon="Box" color="#2563eb" sub="覆盖桥梁附属设备全量档案" />
      <StatCard title="本月新增成本" :value="formatMoneyValue(overview.monthlyCost)" unit="元" icon="Money" color="#7c3aed" sub="近 30 天新增运维支出" />
      <StatCard title="累计运维成本" :value="formatMoneyValue(overview.totalOperationCost)" unit="元" icon="Coin" color="#ea580c" sub="保养与维修累计成本" />
      <StatCard title="设备完好率" :value="formatDecimal(overview.goodConditionRate)" unit="%" icon="CircleCheckFilled" color="#16a34a" sub="（在用 + 在库）/ 总设备数" />
    </section>

    <section class="metric-panel soft-panel">
      <div class="panel-head">
        <div>
          <h3>关键运行指标</h3>
          <p>基于真实业务数据计算的 8 项关键率和效率指标</p>
        </div>
      </div>
      <div class="overview-grid">
        <div v-for="item in overviewMetrics" :key="item.label" class="overview-item">
          <div class="overview-label">{{ item.label }}</div>
          <div class="overview-value" :style="{ color: item.color }">{{ item.value }}</div>
          <div class="overview-desc">{{ item.desc }}</div>
        </div>
      </div>
    </section>

    <section class="chart-grid">
      <el-card class="chart-card soft-panel">
        <template #header>
          <div class="panel-head">
            <div>
              <h3>设备分类分布</h3>
              <p>按当前设备分类统计数量占比</p>
            </div>
          </div>
        </template>
        <Chart :option="categoryOption" height="300px" />
      </el-card>

      <el-card class="chart-card soft-panel">
        <template #header>
          <div class="panel-head">
            <div>
              <h3>近 30 天费用趋势</h3>
              <p>保养费用、维修费用与总费用走势</p>
            </div>
          </div>
        </template>
        <Chart :option="costTrendOption" height="300px" />
      </el-card>
    </section>

    <section class="detail-grid">
      <el-card class="info-card soft-panel">
        <template #header>
          <div class="panel-head">
            <div>
              <h3>待办事项</h3>
              <p>按紧急程度排序的运维与审批事项</p>
            </div>
          </div>
        </template>
        <TodoList :items="todoItems" />
      </el-card>

      <el-card class="info-card soft-panel">
        <template #header>
          <div class="panel-head">
            <div>
              <h3>故障排行 TOP10</h3>
              <p>累计维修费用与故障次数联动观察</p>
            </div>
          </div>
        </template>
        <el-table :data="faultRanking" size="small">
          <el-table-column prop="rank" label="排名" width="70" />
          <el-table-column prop="deviceName" label="设备名称" min-width="160" />
          <el-table-column prop="faultCount" label="故障次数" width="100" />
          <el-table-column prop="totalRepairCost" label="累计费用" width="140">
            <template #default="{ row }">
              {{ formatMoney(row.totalRepairCost) }}
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getDashboard } from '@/api/stats'
import StatCard from '@/components/common/StatCard.vue'
import Chart from '@/components/common/Chart.vue'
import TodoList from '@/components/business/TodoList.vue'

const overview = ref({})
const costTrend = ref([])
const categoryDistribution = ref([])
const todoItems = ref([])
const faultRanking = ref([])

const overviewMetrics = computed(() => [
  { label: '设备在用率', value: `${formatDecimal(overview.value.inUseRate)}%`, desc: '在用设备 / 设备总量', color: '#2563eb' },
  { label: '设备完好率', value: `${formatDecimal(overview.value.goodConditionRate)}%`, desc: '在用与在库设备占比', color: '#16a34a' },
  { label: '年故障率', value: `${formatDecimal(overview.value.faultRate)}%`, desc: '近一年故障工单 / 设备总数', color: '#dc2626' },
  { label: '保养完成率', value: `${formatDecimal(overview.value.maintainCompletionRate)}%`, desc: '实际保养 / 计划保养', color: '#0891b2' },
  { label: '工单关闭率', value: `${formatDecimal(overview.value.orderCloseRate)}%`, desc: '已关闭工单 / 工单总量', color: '#7c3aed' },
  { label: '平均无故障 MTBF', value: `${formatDecimal(overview.value.mtbf, 0)} h`, desc: '总运行时长 / 故障次数', color: '#4f46e5' },
  { label: '平均修复 MTTR', value: `${formatDecimal(overview.value.mttr, 0)} h`, desc: '总停机时长 / 故障次数', color: '#ea580c' },
  { label: '累计运维成本', value: `${formatMoneyValue(overview.value.totalOperationCost)} 元`, desc: '保养 + 维修累计支出', color: '#be123c' }
])

const categoryOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { bottom: 0, icon: 'circle' },
  series: [
    {
      type: 'pie',
      radius: ['48%', '74%'],
      center: ['50%', '42%'],
      itemStyle: { borderRadius: 12, borderColor: '#fff', borderWidth: 4 },
      label: {
        formatter: '{b}\n{d}%'
      },
      data: categoryDistribution.value.map(item => ({
        name: item.categoryName,
        value: item.deviceCount
      }))
    }
  ]
}))

const costTrendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: {
    data: ['保养费用', '维修费用', '总费用']
  },
  grid: {
    left: 24,
    right: 24,
    top: 48,
    bottom: 24,
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: costTrend.value.map(item => formatShortDate(item.date))
  },
  yAxis: {
    type: 'value',
    name: '元'
  },
  series: [
    {
      name: '保养费用',
      type: 'line',
      smooth: true,
      data: costTrend.value.map(item => item.maintainCost || 0)
    },
    {
      name: '维修费用',
      type: 'line',
      smooth: true,
      data: costTrend.value.map(item => item.repairCost || 0)
    },
    {
      name: '总费用',
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      data: costTrend.value.map(item => item.totalCost || 0)
    }
  ]
}))

onMounted(async () => {
  try {
    const data = await getDashboard()
    overview.value = data.overview || {}
    costTrend.value = data.costTrend || []
    categoryDistribution.value = data.categoryDistribution || []
    todoItems.value = data.todoItems || []
    faultRanking.value = data.faultRanking || []
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
})

function formatDecimal(value, digits = 1) {
  const num = Number(value)
  return Number.isFinite(num) ? num.toFixed(digits) : digits === 0 ? '0' : '0.0'
}

function formatNumber(value) {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

function formatMoney(value) {
  const num = Number(value)
  return Number.isFinite(num) ? `${num.toFixed(2)} 元` : '0.00 元'
}

function formatMoneyValue(value) {
  const num = Number(value)
  return Number.isFinite(num) ? num.toFixed(2) : '0.00'
}

function formatShortDate(value) {
  if (!value) return ''
  const date = new Date(value)
  return `${date.getMonth() + 1}/${date.getDate()}`
}
</script>

<style scoped>
.hero-note {
  min-width: 140px;
  padding: 16px 18px;
  border: 1px solid rgba(37, 99, 235, 0.15);
  border-radius: 18px;
  background: rgba(239, 246, 255, 0.9);
  text-align: right;
}

.hero-note strong {
  display: block;
  font-size: 28px;
  color: #1d4ed8;
}

.hero-note span {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #64748b;
}

.metric-panel {
  padding: 24px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 24px;
}

.panel-head h3 {
  margin: 0;
  font-size: 18px;
  color: #0f172a;
}

.panel-head p {
  margin: 8px 0 0;
  font-size: 13px;
  color: #64748b;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 14px;
  margin-top: 18px;
}

.overview-item {
  padding: 16px;
  border: 1px solid #e2e8f0;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.82);
}

.overview-label {
  font-size: 12px;
  color: #64748b;
}

.overview-value {
  margin-top: 10px;
  font-size: 24px;
  font-weight: 700;
}

.overview-desc {
  margin-top: 8px;
  font-size: 12px;
  line-height: 1.6;
  color: #64748b;
}

.chart-grid,
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.chart-card,
.info-card {
  min-height: 380px;
}

@media (max-width: 1200px) {
  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .chart-grid,
  .detail-grid,
  .overview-grid {
    grid-template-columns: 1fr;
  }

  .hero-note {
    width: 100%;
    text-align: left;
  }
}
</style>
