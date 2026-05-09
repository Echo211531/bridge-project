<template>
  <div class="maintain-page">
    <!-- 标签页切换 -->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="保养计划" name="plans">
        <!-- 计划列表 -->
        <el-card>
          <el-form :inline="true" style="margin-bottom: 20px">
            <el-form-item label="状态">
              <el-select v-model="planSearch.status" clearable>
                <el-option label="待保养" value="pending" />
                <el-option label="已完成" value="completed" />
                <el-option label="超期" value="overdue" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadPlans">搜索</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="planList" v-loading="loading">
            <el-table-column prop="deviceCode" label="设备编码" width="140" />
            <el-table-column prop="deviceName" label="设备名称" />
            <el-table-column prop="bridgeName" label="所属桥梁" />
            <el-table-column prop="planDate" label="计划日期" width="120" />
            <el-table-column prop="standardCycleDays" label="标准周期" width="100">
              <template #default="{ row }">{{ row.standardCycleDays }} 天</template>
            </el-table-column>
            <el-table-column prop="cycleDays" label="动态周期" width="100">
              <template #default="{ row }">{{ row.cycleDays }} 天</template>
            </el-table-column>
            <el-table-column prop="dynamicFactor" label="调整系数" width="120" />
            <el-table-column prop="statusName" label="状态" width="100">
              <template #default="{ row }">
                <StatusTag :status="row.status" type="maintain" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="{ row }">
                <el-button type="primary" link v-if="row.status === 'pending'" @click="handleMaintain(row)">
                  上报保养
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-model:current-page="planPageNum"
            v-model:page-size="planPageSize"
            :total="planTotal"
            layout="total, prev, pager, next"
            @current-change="loadPlans"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="保养记录" name="records">
        <!-- 记录列表 -->
        <el-card>
          <el-form :inline="true" style="margin-bottom: 20px">
            <el-form-item label="设备">
              <el-select v-model="recordSearch.deviceId" clearable placeholder="请选择设备" filterable>
                <el-option v-for="d in devices" :key="d.id" :label="d.deviceName" :value="d.id" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="loadRecords">搜索</el-button>
            </el-form-item>
          </el-form>
          <el-table :data="recordList" v-loading="recordLoading">
            <el-table-column prop="deviceCode" label="设备编码" width="140" />
            <el-table-column prop="deviceName" label="设备名称" />
            <el-table-column prop="bridgeName" label="所属桥梁" />
            <el-table-column prop="recordDate" label="保养日期" width="120" />
            <el-table-column prop="actualCost" label="实际费用" width="100">
              <template #default="{ row }">{{ row.actualCost?.toFixed(2) }} 元</template>
            </el-table-column>
            <el-table-column prop="manhour" label="工时" width="80">
              <template #default="{ row }">{{ row.manhour || 0 }} 小时</template>
            </el-table-column>
            <el-table-column prop="operator" label="操作人" width="100" />
            <el-table-column prop="content" label="保养内容" />
          </el-table>
          <el-pagination
            v-model:current-page="recordPageNum"
            v-model:page-size="recordPageSize"
            :total="recordTotal"
            layout="total, prev, pager, next"
            @current-change="loadRecords"
          />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 上报保养弹窗 -->
    <Modal v-model="reportVisible" title="上报保养记录" @confirm="handleReportSubmit">
      <el-form ref="reportFormRef" :model="reportForm" :rules="reportRules" label-width="100px">
        <el-form-item label="设备">
          <el-input :value="currentPlan?.deviceName" disabled />
        </el-form-item>
        <el-form-item label="保养日期" prop="recordDate">
          <el-date-picker v-model="reportForm.recordDate" type="date" />
        </el-form-item>
        <el-form-item label="实际费用" prop="actualCost">
          <el-input-number v-model="reportForm.actualCost" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="工时(小时)">
          <el-input-number v-model="reportForm.manhour" :min="0" :precision="1" />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="reportForm.operator" />
        </el-form-item>
        <el-form-item label="保养内容">
          <el-input v-model="reportForm.content" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
    </Modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listPlans, listRecords, submitRecord } from '@/api/maintain'
import { listDevices } from '@/api/device'
import Modal from '@/components/common/Modal.vue'
import StatusTag from '@/components/common/Tag.vue'

const activeTab = ref('plans')
const loading = ref(false)
const recordLoading = ref(false)
const planList = ref([])
const recordList = ref([])
const devices = ref([])

const planPageNum = ref(1)
const planPageSize = ref(10)
const planTotal = ref(0)

const recordPageNum = ref(1)
const recordPageSize = ref(10)
const recordTotal = ref(0)

const planSearch = reactive({ status: '' })
const recordSearch = reactive({ deviceId: '' })

const reportVisible = ref(false)
const currentPlan = ref(null)
const reportFormRef = ref(null)
const reportForm = reactive({
  deviceId: '',
  recordDate: null,
  actualCost: 0,
  manhour: 0,
  operator: '',
  content: ''
})

const reportRules = {
  recordDate: [{ required: true, message: '请选择保养日期', trigger: 'change' }],
  actualCost: [{ required: true, message: '请输入实际费用', trigger: 'blur' }]
}

onMounted(async () => {
  await Promise.all([loadPlans(), loadDevices()])
})

async function loadPlans() {
  loading.value = true
  try {
    const data = await listPlans({
      pageNum: planPageNum.value,
      pageSize: planPageSize.value,
      status: planSearch.status
    })
    planList.value = data.records || []
    planTotal.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadRecords() {
  recordLoading.value = true
  try {
    const data = await listRecords({
      pageNum: recordPageNum.value,
      pageSize: recordPageSize.value,
      deviceId: recordSearch.deviceId
    })
    recordList.value = data.records || []
    recordTotal.value = data.total || 0
  } finally {
    recordLoading.value = false
  }
}

async function loadDevices() {
  const data = await listDevices({ pageNum: 1, pageSize: 100 })
  devices.value = data.records || []
}

function handleMaintain(row) {
  currentPlan.value = row
  Object.assign(reportForm, {
    deviceId: row.deviceId,
    recordDate: new Date(),
    actualCost: 0,
    manhour: 0,
    operator: '',
    content: ''
  })
  reportVisible.value = true
}

async function handleReportSubmit() {
  try {
    await reportFormRef.value.validate()
    await submitRecord(reportForm)
    ElMessage.success('保养记录上报成功')
    reportVisible.value = false
    loadPlans()
    if (activeTab.value === 'records') loadRecords()
  } catch (error) {
    console.error('上报失败:', error)
  }
}
</script>

<style scoped>
.maintain-page {
  padding: 0;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>