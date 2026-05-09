<template>
  <div class="fault-page">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable>
            <el-option label="待处理" value="open" />
            <el-option label="处理中" value="processing" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="action-bar">
      <el-button type="danger" @click="handleReport">故障申报</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="工单编号" width="150" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="bridgeName" label="所属桥梁" />
        <el-table-column prop="reportDate" label="申报日期" width="120" />
        <el-table-column prop="faultDesc" label="故障描述" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :status="row.status" type="fault" />
          </template>
        </el-table-column>
        <el-table-column prop="durationDays" label="处理时长" width="100">
          <template #default="{ row }">{{ row.durationDays }} 天</template>
        </el-table-column>
        <el-table-column prop="repairCost" label="维修费用" width="100">
          <template #default="{ row }">{{ row.repairCost?.toFixed(2) || 0 }} 元</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link v-if="row.status === 'open'" @click="handleProcess(row, 'processing')">
              开始处理
            </el-button>
            <el-button type="success" link v-if="row.status === 'processing'" @click="handleClose(row)">
              关闭工单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" />
    </el-card>

    <!-- 故障申报弹窗 -->
    <Modal v-model="reportVisible" title="故障申报" @confirm="handleReportSubmit">
      <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="100px">
        <el-form-item label="设备" prop="deviceId">
          <el-select v-model="reportForm.deviceId" placeholder="请选择故障设备" filterable>
            <el-option v-for="d in devices" :key="d.id" :label="`${d.deviceCode} - ${d.deviceName}`" :value="d.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="reportForm.faultDesc" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
    </Modal>

    <!-- 关闭工单弹窗 -->
    <Modal v-model="closeVisible" title="关闭工单" @confirm="handleCloseSubmit">
      <el-form ref="closeFormRef" :model="closeForm" label-width="100px">
        <el-form-item label="维修费用">
          <el-input-number v-model="closeForm.repairCost" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="维修内容">
          <el-input v-model="closeForm.repairContent" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="处理人">
          <el-input v-model="closeForm.handler" />
        </el-form-item>
      </el-form>
    </Modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listFaults, reportFault, processOrder, closeOrder } from '@/api/fault'
import { listDevices } from '@/api/device'
import Modal from '@/components/common/Modal.vue'
import StatusTag from '@/components/common/Tag.vue'

const loading = ref(false)
const orderList = ref([])
const devices = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ status: '' })

const reportVisible = ref(false)
const reportFormRef = ref(null)
const reportForm = reactive({ deviceId: '', faultDesc: '' })
const reportRules = {
  deviceId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  faultDesc: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
}

const closeVisible = ref(false)
const closeFormRef = ref(null)
const currentOrder = ref(null)
const closeForm = reactive({
  repairCost: 0,
  repairContent: '',
  handler: ''
})

onMounted(async () => {
  await Promise.all([loadOrders(), loadDevices()])
})

async function loadOrders() {
  loading.value = true
  try {
    const data = await listFaults({ pageNum: pageNum.value, pageSize: pageSize.value, status: searchForm.status })
    orderList.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadDevices() {
  const data = await listDevices({ pageNum: 1, pageSize: 100 })
  devices.value = data.records || []
}

function handleSearch() { pageNum.value = 1; loadOrders() }
function handleReset() { searchForm.status = ''; pageNum.value = 1; loadOrders() }

function handleReport() {
  Object.assign(reportForm, { deviceId: '', faultDesc: '' })
  reportVisible.value = true
}

async function handleReportSubmit() {
  try {
    await reportFormRef.value.validate()
    await reportFault(reportForm)
    ElMessage.success('故障申报成功')
    reportVisible.value = false
    loadOrders()
  } catch (error) { console.error('申报失败:', error) }
}

async function handleProcess(row, status) {
  try {
    await processOrder(row.id, status)
    ElMessage.success('工单状态更新')
    loadOrders()
  } catch (error) { console.error('更新失败:', error) }
}

function handleClose(row) {
  currentOrder.value = row
  Object.assign(closeForm, { repairCost: 0, repairContent: '', handler: '' })
  closeVisible.value = true
}

async function handleCloseSubmit() {
  try {
    await closeOrder(currentOrder.value.id, closeForm)
    ElMessage.success('工单关闭成功')
    closeVisible.value = false
    loadOrders()
  } catch (error) { console.error('关闭失败:', error) }
}
</script>

<style scoped>
.fault-page { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.action-bar { margin-bottom: 20px; }
</style>