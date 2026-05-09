<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <div class="stats-cards">
      <StatCard
        title="设备总数"
        :value="overview.totalDevices || 0"
        unit="台"
        icon="Box"
        color="#409eff"
      />
      <StatCard
        title="设备在用率"
        :value="(overview.inUseRate || 0).toFixed(1)"
        unit="%"
        icon="CircleCheck"
        color="#67c23a"
      />
      <StatCard
        title="设备完好率"
        :value="(overview.goodConditionRate || 0).toFixed(1)"
        unit="%"
        icon="SuccessFilled"
        color="#67c23a"
      />
      <StatCard
        title="年故障率"
        :value="(overview.faultRate || 0).toFixed(1)"
        unit="%"
        icon="Warning"
        color="#e6a23c"
      />
      <StatCard
        title="保养完成率"
        :value="(overview.maintainCompletionRate || 0).toFixed(1)"
        unit="%"
        icon="Calendar"
        color="#67c23a"
      />
      <StatCard
        title="工单关闭率"
        :value="(overview.orderCloseRate || 0).toFixed(1)"
        unit="%"
        icon="DocumentChecked"
        color="#67c23a"
      />
      <StatCard
        title="MTBF"
        :value="(overview.mtbf || 0).toFixed(0)"
        unit="小时"
        icon="Timer"
        color="#909399"
      />
      <StatCard
        title="MTTR"
        :value="(overview.mttr || 0).toFixed(0)"
        unit="小时"
        icon="Stopwatch"
        color="#909399"
      />
    </div>

    <!-- 费用趋势图 -->
    <div class="chart-section">
      <el-card class="chart-card">
        <template #header>
          <span>30天费用趋势</span>
        </template>
        <Chart :option="costTrendOption" height="300px" />
      </el-card>
    </div>

    <!-- 待办事项 & 故障排行 -->
    <div class="info-section">
      <el-card class="info-card">
        <template #header>
          <span>待办事项</span>
        </template>
        <TodoList :items="todoItems" />
      </el-card>

      <el-card class="info-card">
        <template #header>
          <span>故障排行 TOP10</span>
        </template>
        <el-table :data="faultRanking" size="small">
          <el-table-column prop="rank" label="排名" width="60" />
          <el-table-column prop="deviceName" label="设备名称" />
          <el-table-column prop="faultCount" label="故障次数" width="100" />
          <el-table-column prop="totalRepairCost" label="累计费用" width="120">
            <template #default="{ row }">
              {{ row.totalRepairCost?.toFixed(2) || 0 }} 元
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/**
 * 首页仪表盘
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api/stats'
import StatCard from '@/components/common/StatCard.vue'
import Chart from '@/components/common/Chart.vue'
import TodoList from '@/components/business/TodoList.vue'

const dashboardData = ref({})
const overview = ref({})
const costTrend = ref([])
const todoItems = ref([])
const faultRanking = ref([])

// 费用趋势图配置
const costTrendOption = computed(() => ({
  tooltip: {
    trigger: 'axis'
  },
  legend: {
    data: ['保养费用', '维修费用', '总费用']
  },
  xAxis: {
    type: 'category',
    data: costTrend.value.map(item => {
      const date = new Date(item.date)
      return `${date.getMonth() + 1}/${date.getDate()}`
    })
  },
  yAxis: {
    type: 'value',
    name: '费用(元)'
  },
  series: [
    {
      name: '保养费用',
      type: 'line',
      data: costTrend.value.map(item => item.maintainCost || 0),
      smooth: true
    },
    {
      name: '维修费用',
      type: 'line',
      data: costTrend.value.map(item => item.repairCost || 0),
      smooth: true
    },
    {
      name: '总费用',
      type: 'line',
      data: costTrend.value.map(item => item.totalCost || 0),
      smooth: true,
      lineStyle: { width: 3 }
    }
  ]
}))

onMounted(async () => {
  try {
    const data = await getDashboard()
    dashboardData.value = data
    overview.value = data.overview || {}
    costTrend.value = data.costTrend || []
    todoItems.value = data.todoItems || []
    faultRanking.value = data.faultRanking || []
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.chart-section {
  margin-bottom: 20px;
}

.chart-card {
  height: 380px;
}

.info-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.info-card {
  min-height: 300px;
}
</style>