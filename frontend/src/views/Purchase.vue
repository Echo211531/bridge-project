<template>
  <div class="purchase-page">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="订单编码">
          <el-input v-model="searchForm.orderCode" clearable placeholder="输入订单编码搜索" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" clearable>
            <el-option label="待发货" value="pending" />
            <el-option label="运输中" value="shipping" />
            <el-option label="已收货" value="received" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">新增采购订单</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="orderList" v-loading="loading" stripe>
        <el-table-column prop="orderCode" label="订单编码" width="180" />
        <el-table-column prop="deviceName" label="设备名称" width="150" />
        <el-table-column prop="deviceCategory" label="设备分类" width="100" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="unitPrice" label="单价(元)" width="100">
          <template #default="{ row }">{{ row.unitPrice?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额(元)" width="120">
          <template #default="{ row }">{{ row.totalAmount?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="warning" link v-if="row.status === 'pending'" @click="handleShip(row)">
              发货
            </el-button>
            <el-button type="success" link v-if="row.status === 'shipping'" @click="handleReceive(row)">
              收货
            </el-button>
            <el-button type="danger" link v-if="row.status === 'pending'" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" />
    </el-card>

    <!-- 新增弹窗 -->
    <Modal v-model="dialogVisible" title="新增采购订单" @confirm="handleSubmit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备名称" prop="deviceName">
          <el-input v-model="form.deviceName" />
        </el-form-item>
        <el-form-item label="设备分类" prop="deviceCategory">
          <el-select v-model="form.deviceCategory">
            <el-option v-for="cat in categories" :key="cat.code" :label="cat.name" :value="cat.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="单价(元)" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="form.supplier" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
    </Modal>

    <!-- 详情弹窗 -->
    <Drawer v-model="detailVisible" title="订单详情" width="600px">
      <el-descriptions :column="1" border v-if="currentOrder">
        <el-descriptions-item label="订单编码">{{ currentOrder.orderCode }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ currentOrder.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备分类">{{ currentOrder.deviceCategory }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="单价">{{ currentOrder.unitPrice?.toFixed(2) }} 元</el-descriptions-item>
        <el-descriptions-item label="总金额">{{ currentOrder.totalAmount?.toFixed(2) }} 元</el-descriptions-item>
        <el-descriptions-item label="供应商">{{ currentOrder.supplier || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <StatusTag :status="currentOrder.status" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ formatTime(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ currentOrder.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </Drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listOrders, createOrder, shipOrder, receiveOrder, deleteOrder } from '@/api/purchase'
import { listCategories } from '@/api/device'
import Modal from '@/components/common/Modal.vue'
import Drawer from '@/components/common/Drawer.vue'
import StatusTag from '@/components/common/Tag.vue'

const loading = ref(false)
const orderList = ref([])
const categories = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ orderCode: '', status: '' })

const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({ deviceName: '', deviceCategory: '', quantity: 1, unitPrice: 0, supplier: '', remark: '' })

const rules = {
  deviceName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceCategory: [{ required: true, message: '请选择设备分类', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }]
}

const detailVisible = ref(false)
const currentOrder = ref(null)

onMounted(async () => {
  await Promise.all([loadOrders(), loadCategories()])
})

async function loadOrders() {
  loading.value = true
  try {
    const data = await listOrders({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    orderList.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const data = await listCategories()
  categories.value = data || []
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function handleSearch() { pageNum.value = 1; loadOrders() }
function handleReset() { searchForm.orderCode = ''; searchForm.status = ''; pageNum.value = 1; loadOrders() }

function handleAdd() {
  Object.assign(form, { deviceName: '', deviceCategory: '', quantity: 1, unitPrice: 0, supplier: '', remark: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    await createOrder(form)
    ElMessage.success('采购订单创建成功')
    dialogVisible.value = false
    loadOrders()
  } catch (error) { console.error('提交失败:', error) }
}

function handleView(row) {
  currentOrder.value = row
  detailVisible.value = true
}

async function handleShip(row) {
  try {
    await ElMessageBox.confirm('确认发货该订单吗?', '提示', { type: 'warning' })
    await shipOrder(row.id)
    ElMessage.success('订单已发货')
    loadOrders()
  } catch (error) { if (error !== 'cancel') console.error('发货失败:', error) }
}

async function handleReceive(row) {
  try {
    await ElMessageBox.confirm('确认收货该订单吗?', '提示', { type: 'warning' })
    await receiveOrder(row.id)
    ElMessage.success('订单已收货')
    loadOrders()
  } catch (error) { if (error !== 'cancel') console.error('收货失败:', error) }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗?', '提示', { type: 'warning' })
    await deleteOrder(row.id)
    ElMessage.success('删除成功')
    loadOrders()
  } catch (error) { if (error !== 'cancel') console.error('删除失败:', error) }
}
</script>

<style scoped>
.purchase-page { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.action-bar { margin-bottom: 20px; }
</style>