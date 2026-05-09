<template>
  <div class="bridges-page">
    <!-- 搜索区域 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="桥梁名称">
          <el-input v-model="searchForm.bridgeName" placeholder="输入桥梁名称搜索" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">新增桥梁</el-button>
    </div>

    <!-- 桥梁列表 -->
    <el-card class="table-card">
      <el-table :data="bridgeList" v-loading="loading" stripe>
        <el-table-column prop="bridgeCode" label="桥梁编码" width="120" />
        <el-table-column prop="bridgeName" label="桥梁名称" />
        <el-table-column prop="climateZoneName" label="气候带" width="120" />
        <el-table-column prop="alphaCoef" label="α系数" width="80" />
        <el-table-column prop="aadt" label="AADT" width="100" />
        <el-table-column prop="betaCoef" label="β系数" width="80" />
        <el-table-column prop="location" label="地理位置" />
        <el-table-column prop="buildYear" label="建成年份" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleAadt(row)">更新AADT</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadBridges"
        @current-change="loadBridges"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="桥梁编码" prop="bridgeCode" v-if="!isEdit">
          <el-input v-model="form.bridgeCode" />
        </el-form-item>
        <el-form-item label="桥梁名称" prop="bridgeName">
          <el-input v-model="form.bridgeName" />
        </el-form-item>
        <el-form-item label="气候带" prop="climateZone">
          <el-select v-model="form.climateZone" placeholder="请选择气候带">
            <el-option label="寒冷气候带" value="cold" />
            <el-option label="严寒气候带" value="severe_cold" />
            <el-option label="温带气候带" value="temperate" />
            <el-option label="湿润气候带" value="humid" />
            <el-option label="沿海气候带" value="coastal" />
          </el-select>
        </el-form-item>
        <el-form-item label="AADT" prop="aadt" v-if="!isEdit">
          <el-input-number v-model="form.aadt" :min="0" />
        </el-form-item>
        <el-form-item label="地理位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="建成年份">
          <el-input-number v-model="form.buildYear" :min="1900" :max="2100" />
        </el-form-item>
        <el-form-item label="桥梁长度(米)">
          <el-input-number v-model="form.length" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- AADT更新弹窗 -->
    <el-dialog v-model="aadtDialogVisible" title="更新AADT" width="400px">
      <el-form :model="aadtForm" label-width="100px">
        <el-form-item label="当前AADT">
          <el-input :value="currentBridge?.aadt" disabled />
        </el-form-item>
        <el-form-item label="新AADT">
          <el-input-number v-model="aadtForm.newAadt" :min="0" />
        </el-form-item>
        <el-form-item label="变更原因">
          <el-input v-model="aadtForm.reason" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="aadtDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAadtSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 桥梁管理页面
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listBridges, getBridge, createBridge, updateBridge, updateAadt } from '@/api/bridge'

const loading = ref(false)
const bridgeList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  bridgeName: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({
  id: '',
  bridgeCode: '',
  bridgeName: '',
  climateZone: '',
  aadt: 5000,
  location: '',
  buildYear: 2020,
  length: 0
})

const rules = {
  bridgeCode: [{ required: true, message: '请输入桥梁编码', trigger: 'blur' }],
  bridgeName: [{ required: true, message: '请输入桥梁名称', trigger: 'blur' }],
  climateZone: [{ required: true, message: '请选择气候带', trigger: 'change' }],
  aadt: [{ required: true, message: '请输入AADT', trigger: 'blur' }]
}

const aadtDialogVisible = ref(false)
const currentBridge = ref(null)
const aadtForm = reactive({
  newAadt: 0,
  reason: ''
})

onMounted(async () => {
  await loadBridges()
})

async function loadBridges() {
  loading.value = true
  try {
    const data = await listBridges({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      bridgeName: searchForm.bridgeName
    })
    bridgeList.value = data.records || []
    total.value = data.total || 0
  } catch (error) {
    console.error('加载桥梁列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNum.value = 1
  loadBridges()
}

function handleReset() {
  searchForm.bridgeName = ''
  pageNum.value = 1
  loadBridges()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增桥梁'
  Object.assign(form, {
    id: '',
    bridgeCode: '',
    bridgeName: '',
    climateZone: '',
    aadt: 5000,
    location: '',
    buildYear: 2020,
    length: 0
  })
  dialogVisible.value = true
}

async function handleView(row) {
  // TODO: 查看详情
}

async function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑桥梁'
  const data = await getBridge(row.id)
  Object.assign(form, data)
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateBridge(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createBridge(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadBridges()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

function handleAadt(row) {
  currentBridge.value = row
  aadtForm.newAadt = row.aadt
  aadtForm.reason = ''
  aadtDialogVisible.value = true
}

async function handleAadtSubmit() {
  try {
    await updateAadt(currentBridge.value.id, aadtForm)
    ElMessage.success('AADT更新成功')
    aadtDialogVisible.value = false
    loadBridges()
  } catch (error) {
    console.error('AADT更新失败:', error)
  }
}
</script>

<style scoped>
.bridges-page {
  padding: 0;
}

.search-card {
  margin-bottom: 20px;
}

.action-bar {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>