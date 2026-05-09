<template>
  <div class="devices-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="设备分类">
          <el-select v-model="searchForm.category" placeholder="请选择分类" clearable>
            <el-option v-for="cat in categories" :key="cat.code" :label="cat.name" :value="cat.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属桥梁">
          <el-select v-model="searchForm.bridgeId" placeholder="请选择桥梁" clearable>
            <el-option v-for="br in bridges" :key="br.id" :label="br.bridgeName" :value="br.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="在用" value="in_use" />
            <el-option label="库存" value="in_stock" />
            <el-option label="保养中" value="maintaining" />
            <el-option label="故障" value="fault" />
            <el-option label="已报废" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">新增设备</el-button>
      <el-button type="warning" @click="loadOverdueDevices">超期保养设备</el-button>
    </div>

    <!-- 设备列表 -->
    <el-card class="table-card">
      <el-table :data="deviceList" v-loading="loading" stripe>
        <el-table-column prop="deviceCode" label="设备编码" width="140" />
        <el-table-column prop="deviceName" label="设备名称" />
        <el-table-column prop="categoryName" label="设备分类" width="120" />
        <el-table-column prop="bridgeName" label="所属桥梁" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="purchaseCost" label="购置成本" width="120">
          <template #default="{ row }">{{ row.purchaseCost?.toFixed(2) }} 元</template>
        </el-table-column>
        <el-table-column prop="residualValue" label="残值估算" width="120">
          <template #default="{ row }">{{ row.residualValue?.toFixed(2) }} 元</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleLifecycle(row)">生命周期</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadDevices"
        @current-change="loadDevices"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <Modal v-model="dialogVisible" :title="dialogTitle" width="700px" @confirm="handleSubmit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" />
        </el-form-item>
        <el-form-item label="设备分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.code" :label="cat.name" :value="cat.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属桥梁" prop="bridgeId">
          <el-select v-model="form.bridgeId" placeholder="请选择桥梁">
            <el-option v-for="br in bridges" :key="br.id" :label="br.bridgeName" :value="br.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="购置成本" prop="purchaseCost">
          <el-input-number v-model="form.purchaseCost" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="生产厂商">
          <el-input v-model="form.manufacturer" />
        </el-form-item>
        <el-form-item label="型号规格">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="采购日期">
          <el-date-picker v-model="form.purchaseDate" type="date" />
        </el-form-item>
        <el-form-item label="投入使用日期">
          <el-date-picker v-model="form.inUseDate" type="date" />
        </el-form-item>
        <el-form-item label="桥梁位置">
          <el-input v-model="form.locationOnBridge" />
        </el-form-item>
      </el-form>
    </Modal>

    <!-- 详情抽屉 -->
    <Drawer v-model="detailVisible" title="设备详情" size="50%">
      <DeviceDetail :device="currentDevice" v-if="currentDevice" />
    </Drawer>
  </div>
</template>

<script setup>
/**
 * 设备档案页面
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { listDevices, getDevice, createDevice, updateDevice, listCategories, listOverdueDevices } from '@/api/device'
import { listBridges } from '@/api/bridge'
import Modal from '@/components/common/Modal.vue'
import Drawer from '@/components/common/Drawer.vue'
import DeviceDetail from '@/components/business/DeviceDetail.vue'
import StatusTag from '@/components/common/Tag.vue'

const router = useRouter()
const loading = ref(false)
const deviceList = ref([])
const categories = ref([])
const bridges = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  category: '',
  bridgeId: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({})

const detailVisible = ref(false)
const currentDevice = ref(null)

const rules = {
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择设备分类', trigger: 'change' }],
  bridgeId: [{ required: true, message: '请选择所属桥梁', trigger: 'change' }],
  purchaseCost: [{ required: true, message: '请输入购置成本', trigger: 'blur' }]
}

onMounted(async () => {
  await Promise.all([loadDevices(), loadCategories(), loadBridges()])
})

async function loadDevices() {
  loading.value = true
  try {
    const data = await listDevices({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    deviceList.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const data = await listCategories()
  categories.value = data || []
}

async function loadBridges() {
  const data = await listBridges({ pageNum: 1, pageSize: 100 })
  bridges.value = data.records || []
}

async function loadOverdueDevices() {
  router.push('/maintain?status=overdue')
}

function handleSearch() {
  pageNum.value = 1
  loadDevices()
}

function handleReset() {
  Object.assign(searchForm, { category: '', bridgeId: '', status: '' })
  pageNum.value = 1
  loadDevices()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  Object.assign(form, {
    id: '',
    deviceName: '',
    category: '',
    bridgeId: '',
    purchaseCost: 0,
    manufacturer: '',
    model: '',
    purchaseDate: null,
    inUseDate: null,
    locationOnBridge: ''
  })
  dialogVisible.value = true
}

async function handleView(row) {
  const data = await getDevice(row.id)
  currentDevice.value = data
  detailVisible.value = true
}

async function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  const data = await getDevice(row.id)
  Object.assign(form, data)
  dialogVisible.value = true
}

function handleLifecycle(row) {
  router.push(`/lifecycle?deviceId=${row.id}`)
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateDevice(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createDevice(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadDevices()
  } catch (error) {
    console.error('提交失败:', error)
  }
}
</script>

<style scoped>
.devices-page {
  padding: 0;
}
.search-card, .table-card {
  margin-bottom: 20px;
}
.action-bar {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
