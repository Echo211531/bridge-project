<template>
  <div class="login-container bg-login">
    <div class="login-brand">
      <div class="brand-title">桥梁设备全生命周期管理系统</div>
      <div class="brand-subtitle">Bridge Equipment Lifecycle Management System</div>
    </div>

    <div class="login-footer">
      © 2026 华东交通大学 软件学院 · 程国忠 · 指导教师：向华萍
    </div>

    <div class="login-card fade-in">
      <div class="intro-panel">
        <div>
          <div class="intro-icon">🌉</div>
          <h2>设备管理 一站到底</h2>
          <p>
            覆盖桥梁附属设备从
            <span>采购入库 → 使用 → 保养 → 维修 → 报废</span>
            的全过程，TCO 决策辅助让“修还是换”有据可依。
          </p>
        </div>
        <div class="intro-list">
          <div>· 5 类系统角色精细分权</div>
          <div>· 全生命周期事件流水追溯</div>
          <div>· TCO 阈值前置算法</div>
          <div>· 8 项关键率真实计算</div>
        </div>
      </div>

      <div class="form-panel">
        <div class="form-head">
          <h1>用户登录</h1>
          <p>请使用您的工作账号登录系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          class="login-form"
          @submit.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <label class="field-label">账号</label>
            <el-input
              v-model="form.username"
              placeholder="请输入账号"
              size="large"
              :prefix-icon="User"
            />
          </el-form-item>

          <el-form-item prop="password">
            <label class="field-label">密码</label>
            <el-input
              v-model="form.password"
              type="password"
              placeholder="请输入密码"
              size="large"
              :prefix-icon="Lock"
              show-password
            />
          </el-form-item>

          <div v-if="errorMessage" class="error-tip">
            {{ errorMessage }}
          </div>

          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-btn"
            @click="handleLogin"
          >
            {{ loading ? '登录中…' : '登 录' }}
          </el-button>
        </el-form>

        <div class="quick-login">
          <div class="quick-title">🔑 测试账号一键登录（毕设答辩演示）</div>
          <div class="quick-grid">
            <button
              v-for="account in demoAccounts"
              :key="account.username"
              type="button"
              class="quick-account"
              @click="fillAccount(account)"
            >
              <div class="quick-role" :class="account.colorClass">{{ account.role }}</div>
              <div class="quick-user">{{ account.username }} / {{ account.password }}</div>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const errorMessage = ref('')

const form = reactive({
  username: '',
  password: ''
})

const demoAccounts = [
  { role: '系统管理员', username: 'admin', password: 'admin123', colorClass: 'admin' },
  { role: '工程管理人员', username: 'engineer', password: 'engineer123', colorClass: 'engineer' },
  { role: '采购人员', username: 'purchaser', password: 'purchase123', colorClass: 'purchaser' },
  { role: '运维人员', username: 'maintainer', password: 'maintain123', colorClass: 'maintainer' },
  { role: '报废鉴定员', username: 'scrap', password: 'scrap123', colorClass: 'scrap' }
]

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

async function handleLogin() {
  try {
    errorMessage.value = ''
    await formRef.value.validate()
    loading.value = true

    const data = await login(form)

    // 存储用户信息
    userStore.setUserInfo(data)

    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    if (error?.message) {
      errorMessage.value = error.message
    }
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

function fillAccount(account) {
  form.username = account.username
  form.password = account.password
  errorMessage.value = ''
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 16px;
}

.login-brand {
  position: absolute;
  top: 24px;
  left: 32px;
  z-index: 2;
  color: #fff;
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.72);
}

.login-footer {
  position: absolute;
  bottom: 18px;
  width: 100%;
  text-align: center;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.62);
  z-index: 2;
}

.login-card {
  position: relative;
  z-index: 1;
  width: 920px;
  max-width: 100%;
  display: grid;
  grid-template-columns: 360px 1fr;
  overflow: hidden;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 30px 80px rgba(15, 23, 42, 0.28);
}

.intro-panel {
  padding: 40px 32px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  color: #fff;
  background: linear-gradient(135deg, #1d4ed8 0%, #3730a3 100%);
}

.intro-icon {
  font-size: 32px;
  margin-bottom: 16px;
}

.intro-panel h2 {
  margin: 0;
  font-size: 28px;
}

.intro-panel p {
  margin: 18px 0 0;
  line-height: 1.8;
  color: rgba(255, 255, 255, 0.78);
}

.intro-panel span {
  display: block;
  font-weight: 600;
  color: #fde68a;
}

.intro-list {
  display: grid;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.72);
}

.form-panel {
  padding: 40px 40px 32px;
}

.form-head {
  margin-bottom: 24px;
}

.form-head h1 {
  margin: 0;
  font-size: 30px;
  color: #111827;
}

.form-head p {
  margin: 10px 0 0;
  color: #6b7280;
}

.field-label {
  display: block;
  margin-bottom: 8px;
  font-size: 12px;
  color: #6b7280;
}

.login-form :deep(.el-form-item) {
  display: block;
  margin-bottom: 18px;
}

.login-form :deep(.el-input__wrapper) {
  min-height: 46px;
  border-radius: 14px;
  box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.2) inset;
}

.error-tip {
  margin-bottom: 16px;
  padding: 10px 14px;
  border: 1px solid #fecaca;
  border-radius: 14px;
  background: #fef2f2;
  color: #dc2626;
  font-size: 13px;
}

.login-btn {
  width: 100%;
  min-height: 46px;
  border-radius: 14px;
  margin-top: 4px;
  background: linear-gradient(90deg, #2563eb 0%, #4f46e5 100%);
  border: none;
}

.quick-login {
  margin-top: 28px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.quick-title {
  margin-bottom: 12px;
  font-size: 12px;
  color: #6b7280;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.quick-account {
  padding: 12px;
  border: 1px solid #e5e7eb;
  border-radius: 16px;
  background: #fff;
  cursor: pointer;
  text-align: left;
  transition: all 0.2s ease;
}

.quick-account:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 24px rgba(15, 23, 42, 0.08);
}

.quick-role {
  font-size: 13px;
  font-weight: 700;
}

.quick-user {
  margin-top: 6px;
  font-size: 12px;
  color: #6b7280;
}

.quick-role.admin { color: #dc2626; }
.quick-role.engineer { color: #1d4ed8; }
.quick-role.purchaser { color: #7c3aed; }
.quick-role.maintainer { color: #0891b2; }
.quick-role.scrap { color: #ea580c; }

@media (max-width: 900px) {
  .login-card {
    grid-template-columns: 1fr;
  }

  .intro-panel {
    display: none;
  }

  .form-panel {
    padding: 32px 24px;
  }

  .quick-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .login-brand {
    left: 18px;
    top: 16px;
  }

  .brand-title {
    font-size: 16px;
  }

  .login-footer {
    display: none;
  }
}
</style>
