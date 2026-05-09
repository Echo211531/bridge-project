<template>
  <div class="scrap-page">
    <!-- 待鉴定设备列表 -->
    <el-card>
      <template #header>
        <span>待鉴定设备列表</span>
      </template>
      <el-table :data="candidates" v-loading="loading" stripe>
        <el-table-column prop="deviceCode" label="设备编码" width="140" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="categoryName" label="设备分类" width="120" />
        <el-table-column prop="bridgeName" label="所属桥梁" />
        <el-table-column prop="purchaseCost" label="购置成本" width="100">
          <template #default="{ row }">{{ row.purchaseCost?.toFixed(2) }} 元</template>
        </el-table-column>
        <el-table-column prop="residualValue" label="残值估算" width="100">
          <template #default="{ row }">{{ row.residualValue?.toFixed(2) }} 元</template>
        </el-table-column>
        <el-table-column prop="residualRate" label="残值率" width="80">
          <template #default="{ row }">{{ row.residualRate?.toFixed(1) }}%</template>
        </el-table-column>
        <el-table-column prop="faultCount" label="故障次数" width="80" />
        <el-table-column prop="alertType" label="预警类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getAlertType(row.alertType)">{{ row.alertType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleDecision(row)">TCO决策</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadCandidates"
        @current-change="loadCandidates"
      />
    </el-card>

    <!-- TCO决策抽屉 -->
    <el-drawer v-model="decisionVisible" title="TCO决策面板" size="60%">
      <div class="decision-content" v-if="currentDevice">
        <!-- 设备信息 -->
        <el-card style="margin-bottom: 20px">
          <el-descriptions :column="4" border>
            <el-descriptions-item label="设备编码">{{ tcoData.deviceCode }}</el-descriptions-item>
            <el-descriptions-item label="设备名称">{{ tcoData.deviceName }}</el-descriptions-item>
            <el-descriptions-item label="当前残值">{{ tcoData.currentResidualValue?.toFixed(2) }} 元</el-descriptions-item>
            <el-descriptions-item label="残值警戒线">{{ tcoData.residualThreshold }}%</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 三栏决策面板 -->
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

        <!-- 提交结论 -->
        <el-card style="margin-top: 20px">
          <template #header><span>提交鉴定结论</span></template>
          <el-form :model="decisionForm" label-width="100px">
            <el-form-item label="鉴定结论">
              <el-radio-group v-model="decisionForm.recommendation">
                <el-radio label="repair">维修方案</el-radio>
                <el-radio label="replace">更换方案</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="鉴定备注">
              <el-input v-model="decisionForm.conclusionNotes" type="textarea" rows="3" />
            </el-form-item>
            <el-form-item label="鉴定人">
              <el-input v-model="decisionForm.decider" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmitDecision">提交鉴定结论</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listCandidates, getTcoDecision, submitDecision } from '@/api/scrap'
import TcoPanel from '@/components/business/TcoPanel.vue'

const loading = ref(false)
const candidates = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const decisionVisible = ref(false)
const currentDevice = ref(null)
const tcoData = ref({})
const decisionForm = reactive({
  deviceId: '',
  tcoRepair: 0,
  tcoReplace: 0,
  recommendation: '',
  conclusionNotes: '',
  decider: ''
})

onMounted(async () => {
  await loadCandidates()
})

async function loadCandidates() {
  loading.value = true
  try {
    const data = await listCandidates({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    candidates.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function handleDecision(row) {
  currentDevice.value = row
  decisionForm.deviceId = row.deviceId
  decisionForm.recommendation = ''
  decisionForm.conclusionNotes = ''
  decisionForm.decider = ''

  try {
    const data = await getTcoDecision(row.deviceId)
    tcoData.value = data
    decisionForm.tcoRepair = data.tcoRepair
    decisionForm.tcoReplace = data.tcoReplace
    decisionForm.recommendation = data.recommendation
  } catch (error) {
    console.error('获取TCO数据失败:', error)
  }

  decisionVisible.value = true
}

async function handleSubmitDecision() {
  try {
    await submitDecision(decisionForm)
    ElMessage.success('鉴定结论提交成功')
    decisionVisible.value = false
    await loadCandidates()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

function getAlertType(type) {
  const types = { life: 'warning', residual: 'danger', fault: 'danger' }
  return types[type] || 'info'
}
</script>

<style scoped>
.scrap-page { padding: 0; }
.decision-content { padding: 20px; }
</style>
