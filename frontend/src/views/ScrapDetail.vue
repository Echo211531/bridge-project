<template>
  <div class="scrap-detail-page">
    <el-card v-if="tcoData">
      <template #header>
        <span>TCO决策详情 - {{ tcoData.deviceName }}</span>
      </template>
      <TcoPanel
        :tcoRepair="tcoData.tcoRepair"
        :tcoReplace="tcoData.tcoReplace"
        :currentRepairCost="tcoData.currentRepairCost"
        :newDevicePrice="tcoData.newDevicePrice"
        :salvageValue="tcoData.salvageValue"
        :recommendation="tcoData.recommendation"
        :recommendationName="tcoData.recommendationName"
        :tcoDifference="tcoData.tcoDifference"
        :decisionReason="tcoData.decisionReason"
        :belowResidualThreshold="tcoData.belowResidualThreshold"
        :aboveRepairThreshold="tcoData.aboveRepairThreshold"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getTcoDecision } from '@/api/scrap'
import TcoPanel from '@/components/business/TcoPanel.vue'

const route = useRoute()
const tcoData = ref(null)

onMounted(async () => {
  const deviceId = route.params.id
  try {
    tcoData.value = await getTcoDecision(deviceId)
  } catch (error) {
    console.error('获取TCO数据失败:', error)
  }
})
</script>

<style scoped>
.scrap-detail-page { padding: 0; }
</style>