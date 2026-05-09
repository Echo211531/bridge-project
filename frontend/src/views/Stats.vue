<template>
  <div class="stats-page">
    <!-- 8项关键率 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <StatCard title="设备在用率" :value="stats.inUseRate?.toFixed(1)" unit="%" icon="CircleCheck" color="#67c23a" />
      </el-col>
      <el-col :span="6">
        <StatCard title="设备完好率" :value="stats.goodConditionRate?.toFixed(1)" unit="%" icon="SuccessFilled" color="#67c23a" />
      </el-col>
      <el-col :span="6">
        <StatCard title="年故障率" :value="stats.faultRate?.toFixed(1)" unit="%" icon="Warning" color="#e6a23c" />
      </el-col>
      <el-col :span="6">
        <StatCard title="保养完成率" :value="stats.maintainCompletionRate?.toFixed(1)" unit="%" icon="Calendar" color="#67c23a" />
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="6">
        <StatCard title="工单关闭率" :value="stats.orderCloseRate?.toFixed(1)" unit="%" icon="DocumentChecked" color="#67c23a" />
      </el-col>
      <el-col :span="6">
        <StatCard title="MTBF" :value="stats.mtbf?.toFixed(0)" unit="小时" icon="Timer" color="#909399" />
      </el-col>
      <el-col :span="6">
        <StatCard title="MTTR" :value="stats.mttr?.toFixed(0)" unit="小时" icon="Stopwatch" color="#909399" />
      </el-col>
      <el-col :span="6">
        <StatCard title="累计运维成本" :value="stats.totalOperationCost?.toFixed(0)" unit="元" icon="Money" color="#409eff" />
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <template #header><span>设备分类分布</span></template>
          <Chart :option="categoryChartOption" height="300px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header><span>TCO对比分析</span></template>
          <el-table :data="tcoComparison" size="small">
            <el-table-column prop="categoryName" label="分类" />
            <el-table-column prop="avgPurchaseCost" label="平均购置" width="100">
              <template #default="{ row }">{{ row.avgPurchaseCost?.toFixed(0) }}</template>
            </el-table-column>
            <el-table-column prop="avgMaintainCost" label="平均保养" width="100">
              <template #default="{ row }">{{ row.avgMaintainCost?.toFixed(0) }}</template>
            </el-table-column>
            <el-table-column prop="avgRepairCost" label="平均维修" width="100">
              <template #default="{ row }">{{ row.avgRepairCost?.toFixed(0) }}</template>
            </el-table-column>
            <el-table-column prop="avgTco" label="平均TCO" width="100">
              <template #default="{ row }">{{ row.avgTco?.toFixed(0) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api/stats'
import StatCard from '@/components/common/StatCard.vue'
import Chart from '@/components/common/Chart.vue'

const stats = ref({})
const categoryDistribution = ref([])
const tcoComparison = ref([])

const categoryChartOption = computed(() => ({
  tooltip: { trigger: 'item' },
  legend: { orient: 'vertical', left: 'left' },
  series: [{
    type: 'pie',
    radius: '50%',
    data: categoryDistribution.value.map(item => ({
      name: item.categoryName,
      value: item.deviceCount
    }))
  }]
}))

onMounted(async () => {
  try {
    const data = await getDashboard()
    stats.value = data.overview || {}
    categoryDistribution.value = data.categoryDistribution || []
    tcoComparison.value = data.tcoComparison || []
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
})
</script>

<style scoped>
.stats-page { padding: 0; }
.el-card { margin-bottom: 20px; }
</style>