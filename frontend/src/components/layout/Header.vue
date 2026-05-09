<template>
  <header class="header">
    <div class="left">
      <el-icon class="toggle-btn" @click="$emit('toggle')">
        <component :is="collapsed ? 'Expand' : 'Fold'" />
      </el-icon>
      <div class="title-group">
        <span class="system-name">🌉 桥梁设备全生命周期管理系统</span>
        <span class="title">{{ pageTitle }}</span>
      </div>
    </div>

    <div class="right">
      <span class="clock">{{ clockText }}</span>
      <div class="user-info">
        <el-avatar :size="36" class="avatar">
          {{ userInitial }}
        </el-avatar>
        <div class="user-meta">
          <span class="username">{{ displayName }}</span>
          <span class="role">{{ roleName }}</span>
        </div>
      </div>
      <el-button text class="logout-btn" @click="handleLogout">
        <el-icon><SwitchButton /></el-icon>
        退出
      </el-button>
    </div>
  </header>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Expand, Fold, SwitchButton } from '@element-plus/icons-vue'
import { logout } from '@/api/auth'

defineProps({
  collapsed: Boolean
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const now = ref(new Date())

const pageTitle = computed(() => route.meta?.title || '桥梁设备全生命周期管理系统')
const displayName = computed(() => userStore.userInfo?.realName || userStore.username || '用户')
const roleName = computed(() => {
  const roleMap = {
    admin: '系统管理员',
    engineer: '工程管理人员',
    purchaser: '采购人员',
    maintainer: '运维人员',
    scrap: '报废鉴定员'
  }

  return roleMap[userStore.roleCode] || '已登录用户'
})
const userInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())
const clockText = computed(() => now.value.toLocaleString('zh-CN', { hour12: false }))

let timer = null

onMounted(() => {
  timer = window.setInterval(() => {
    now.value = new Date()
  }, 1000)
})

onBeforeUnmount(() => {
  if (timer) {
    window.clearInterval(timer)
  }
})

async function handleLogout() {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await logout()
    userStore.clearUserInfo()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退出登录失败:', error)
    }
  }
}
</script>

<style scoped>
.header {
  min-height: 72px;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(12px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: #475569;
  padding: 10px;
  border-radius: 12px;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.toggle-btn:hover {
  background: #eff6ff;
  color: #2563eb;
}

.title-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.system-name {
  font-size: 12px;
  color: #64748b;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}

.right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.clock {
  font-size: 12px;
  color: #64748b;
  font-variant-numeric: tabular-nums;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border-radius: 999px;
  background: rgba(241, 245, 249, 0.9);
}

.user-meta {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.avatar {
  background: linear-gradient(135deg, #3b82f6 0%, #4f46e5 100%);
  color: #fff;
}

.username {
  color: #0f172a;
  font-size: 14px;
  font-weight: 600;
}

.role {
  color: #64748b;
  font-size: 12px;
}

.logout-btn {
  color: #475569;
}

@media (max-width: 768px) {
  .header {
    padding: 0 16px;
  }

  .clock,
  .system-name,
  .role {
    display: none;
  }
}
</style>
