<template>
  <div ref="chartRef" class="chart-container" :style="{ height: height }"></div>
</template>

<script setup>
/**
 * ECharts图表容器组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref, onMounted, onUnmounted, watch } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  option: Object,
  height: {
    type: String,
    default: '400px'
  }
})

const chartRef = ref(null)
let chartInstance = null

onMounted(() => {
  if (chartRef.value) {
    chartInstance = echarts.init(chartRef.value)
    if (props.option) {
      chartInstance.setOption(props.option)
    }
  }
})

onUnmounted(() => {
  if (chartInstance) {
    chartInstance.dispose()
  }
})

watch(() => props.option, (newOption) => {
  if (chartInstance && newOption) {
    chartInstance.setOption(newOption)
  }
}, { deep: true })
</script>

<style scoped>
.chart-container {
  width: 100%;
}
</style>