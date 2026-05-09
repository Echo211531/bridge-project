<template>
  <div class="audit-page">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.action" clearable>
            <el-option label="创建" value="create" />
            <el-option label="更新" value="update" />
            <el-option label="删除" value="delete" />
            <el-option label="状态变更" value="status_change" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="searchForm.operator" clearable />
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="searchForm.startTime" type="datetime" placeholder="开始时间" />
          至
          <el-date-picker v-model="searchForm.endTime" type="datetime" placeholder="结束时间" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="logList" v-loading="loading" stripe>
        <el-table-column prop="operator" label="操作人" width="120" />
        <el-table-column prop="operatorName" label="姓名" width="120" />
        <el-table-column prop="actionName" label="操作类型" width="100" />
        <el-table-column prop="targetType" label="目标类型" width="120" />
        <el-table-column prop="targetId" label="目标ID" width="200" />
        <el-table-column prop="detail" label="操作详情">
          <template #default="{ row }">
            <el-text truncated>{{ row.detail }}</el-text>
          </template>
        </el-table-column>
        <el-table-column prop="operateTime" label="操作时间" width="180">
          <template #default="{ row }">{{ formatTime(row.operateTime) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listLogs } from '@/api/audit'

const loading = ref(false)
const logList = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)

const searchForm = reactive({
  action: '',
  operator: '',
  startTime: null,
  endTime: null
})

onMounted(async () => {
  await loadLogs()
})

async function loadLogs() {
  loading.value = true
  try {
    const data = await listLogs({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      ...searchForm
    })
    logList.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}:${String(date.getSeconds()).padStart(2, '0')}`
}

function handleSearch() { pageNum.value = 1; loadLogs() }
function handleReset() {
  searchForm.action = ''
  searchForm.operator = ''
  searchForm.startTime = null
  searchForm.endTime = null
  pageNum.value = 1
  loadLogs()
}
</script>

<style scoped>
.audit-page { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
</style>