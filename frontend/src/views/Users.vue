<template>
  <div class="users-page">
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" clearable placeholder="输入用户名搜索" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" clearable>
            <el-option label="正常" value="active" />
            <el-option label="冻结" value="frozen" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">新增用户</el-button>
    </div>

    <el-card class="table-card">
      <el-table :data="userList" v-loading="loading" stripe>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column prop="statusName" label="状态" width="100">
          <template #default="{ row }">
            <StatusTag :status="row.status" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link v-if="row.status === 'active'" @click="handleToggleStatus(row, 'frozen')">
              禁用
            </el-button>
            <el-button type="success" link v-if="row.status === 'frozen'" @click="handleToggleStatus(row, 'active')">
              启用
            </el-button>
            <el-button type="warning" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :total="total" layout="total, prev, pager, next" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <Modal v-model="dialogVisible" :title="dialogTitle" @confirm="handleSubmit">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户名" prop="username" v-if="!isEdit">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="roleCode">
          <el-select v-model="form.roleCode">
            <el-option v-for="role in roles" :key="role.code" :label="role.name" :value="role.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" v-if="!isEdit">
          <el-radio-group v-model="form.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="frozen">冻结</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </Modal>

    <!-- 重置密码弹窗 -->
    <Modal v-model="resetPwdVisible" title="重置密码" @confirm="handleResetPwdSubmit">
      <el-form :model="resetPwdForm" label-width="100px">
        <el-form-item label="新密码">
          <el-input v-model="resetPwdForm.newPassword" type="password" show-password />
        </el-form-item>
      </el-form>
    </Modal>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listUsers, createUser, updateUser, toggleUserStatus, resetPassword, deleteUser, listRoles } from '@/api/user'
import Modal from '@/components/common/Modal.vue'
import StatusTag from '@/components/common/Tag.vue'

const loading = ref(false)
const userList = ref([])
const roles = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({ username: '', status: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)
const form = reactive({ id: '', username: '', password: '', realName: '', roleCode: '', status: 'active' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const resetPwdVisible = ref(false)
const resetPwdUser = ref(null)
const resetPwdForm = reactive({ newPassword: '' })

onMounted(async () => {
  await Promise.all([loadUsers(), loadRoles()])
})

async function loadUsers() {
  loading.value = true
  try {
    const data = await listUsers({ pageNum: pageNum.value, pageSize: pageSize.value, ...searchForm })
    userList.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

async function loadRoles() {
  const data = await listRoles()
  roles.value = data || []
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

function handleSearch() { pageNum.value = 1; loadUsers() }
function handleReset() { searchForm.username = ''; searchForm.status = ''; pageNum.value = 1; loadUsers() }

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  Object.assign(form, { id: '', username: '', password: '', realName: '', roleCode: '', status: 'active' })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  Object.assign(form, { id: row.id, realName: row.realName, roleCode: row.roleCode })
  dialogVisible.value = true
}

async function handleSubmit() {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await updateUser(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await createUser(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadUsers()
  } catch (error) { console.error('提交失败:', error) }
}

async function handleToggleStatus(row, status) {
  try {
    await toggleUserStatus(row.id, status)
    ElMessage.success('状态更新成功')
    loadUsers()
  } catch (error) { console.error('更新失败:', error) }
}

function handleResetPwd(row) {
  resetPwdUser.value = row
  resetPwdForm.newPassword = ''
  resetPwdVisible.value = true
}

async function handleResetPwdSubmit() {
  try {
    await resetPassword(resetPwdUser.value.id, resetPwdForm.newPassword)
    ElMessage.success('密码重置成功')
    resetPwdVisible.value = false
  } catch (error) { console.error('重置失败:', error) }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定要删除该用户吗？', '提示', { type: 'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    loadUsers()
  } catch (error) { if (error !== 'cancel') console.error('删除失败:', error) }
}
</script>

<style scoped>
.users-page { padding: 0; }
.search-card, .table-card { margin-bottom: 20px; }
.action-bar { margin-bottom: 20px; }
</style>