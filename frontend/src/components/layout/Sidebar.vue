<template>
  <aside class="sidebar" :class="{ collapsed: collapsed }">
    <div class="logo">
      <div class="logo-mark">🌉</div>
      <div v-if="!collapsed" class="logo-copy">
        <div class="logo-text">桥梁设备管理系统</div>
        <div class="logo-sub">v1.0 · 全生命周期</div>
      </div>
      <span class="logo-mini" v-else>BLC</span>
    </div>

    <div class="user-panel" v-if="userDisplay">
      <div class="avatar">{{ userDisplay.initial }}</div>
      <div v-if="!collapsed" class="user-copy">
        <div class="user-name">{{ userDisplay.name }}</div>
        <div class="user-role">{{ userDisplay.role }}</div>
      </div>
    </div>

    <nav class="menu-list scrollbar-thin">
      <button
        v-for="item in visibleMenus"
        :key="item.code"
        class="menu-item"
        :class="{ active: activeMenu === item.path, collapsed }"
        type="button"
        @click="router.push(item.path)"
      >
        <span class="menu-icon">{{ item.icon }}</span>
        <span v-if="!collapsed" class="menu-label">{{ item.name }}</span>
      </button>
    </nav>

    <div class="menu-summary" v-if="!collapsed">
      共 {{ visibleMenus.length }} 个菜单 · 当前角色权限范围
    </div>

    <div class="collapse-btn" @click="$emit('toggle')">
      <el-icon>
        <component :is="collapsed ? 'Expand' : 'Fold'" />
      </el-icon>
    </div>
  </aside>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { Expand, Fold } from '@element-plus/icons-vue'

defineProps({
  collapsed: Boolean
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const roleNames = {
  admin: '系统管理员',
  engineer: '工程管理人员',
  purchaser: '采购人员',
  maintainer: '运维人员',
  scrap: '报废鉴定员'
}

const allMenus = [
  { code: 'dashboard', name: '首页仪表盘', icon: '📊', path: '/dashboard' },
  { code: 'lifecycle', name: '全生命周期', icon: '♻️', path: '/lifecycle' },
  { code: 'bridges', name: '桥梁基础参数', icon: '🌉', path: '/bridges' },
  { code: 'devices', name: '设备档案管理', icon: '⚙️', path: '/devices' },
  { code: 'maintain', name: '保养计划管理', icon: '🔧', path: '/maintain' },
  { code: 'fault', name: '故障维修工单', icon: '🚨', path: '/fault' },
  { code: 'purchase', name: '采购入库', icon: '📦', path: '/purchase' },
  { code: 'scrap', name: '报废技术鉴定', icon: '🗑️', path: '/scrap' },
  { code: 'stats', name: '统计分析', icon: '📈', path: '/stats' },
  { code: 'config', name: '阈值参数配置', icon: '⚖️', path: '/config' },
  { code: 'users', name: '用户与角色', icon: '👥', path: '/users' },
  { code: 'audit', name: '审计日志', icon: '📜', path: '/audit' }
]

const visibleMenus = computed(() => {
  const granted = Array.isArray(userStore.menus) ? userStore.menus : []
  if (granted.length === 0) {
    return allMenus
  }

  return allMenus.filter(item => granted.includes(item.code))
})

const userDisplay = computed(() => {
  const rawName = userStore.userInfo?.realName || userStore.username || ''
  if (!rawName) {
    return null
  }

  return {
    name: rawName,
    role: roleNames[userStore.roleCode] || '已登录用户',
    initial: rawName.slice(0, 1).toUpperCase()
  }
})
</script>

<style scoped>
.sidebar {
  width: 220px;
  min-height: 100vh;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1001;
  transition: width 0.3s ease;
  display: flex;
  flex-direction: column;
  color: #fff;
  box-shadow: 24px 0 60px rgba(15, 23, 42, 0.18);
}

.sidebar.collapsed {
  width: 88px;
}

.logo {
  min-height: 84px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
}

.logo-mark {
  width: 40px;
  height: 40px;
  border-radius: 14px;
  background: linear-gradient(135deg, #3b82f6 0%, #4f46e5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
}

.logo-sub {
  margin-top: 4px;
  font-size: 11px;
  color: #94a3b8;
}

.logo-mini {
  margin-left: auto;
  font-size: 16px;
  font-weight: 700;
}

.user-panel {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 999px;
  background: linear-gradient(135deg, #60a5fa 0%, #4f46e5 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #eff6ff;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
}

.user-role {
  margin-top: 4px;
  font-size: 11px;
  color: #94a3b8;
}

.menu-list {
  flex: 1;
  padding: 16px 12px;
  overflow-y: auto;
}

.menu-item {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border: none;
  border-radius: 16px;
  background: transparent;
  color: #cbd5e1;
  cursor: pointer;
  transition: all 0.25s ease;
  text-align: left;
  margin-bottom: 8px;
}

.menu-item:hover {
  background: rgba(51, 65, 85, 0.75);
}

.menu-item.active {
  background: linear-gradient(90deg, rgba(37, 99, 235, 0.95) 0%, rgba(79, 70, 229, 0.92) 100%);
  color: #fff;
  box-shadow: 0 16px 24px rgba(37, 99, 235, 0.24);
}

.menu-item.collapsed {
  justify-content: center;
  padding: 12px;
}

.menu-icon {
  font-size: 18px;
}

.menu-label {
  font-size: 14px;
  white-space: nowrap;
}

.menu-summary {
  padding: 12px 18px;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
  color: #94a3b8;
  font-size: 11px;
}

.collapse-btn {
  min-height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid rgba(148, 163, 184, 0.16);
  color: #cbd5e1;
  cursor: pointer;
}

.collapse-btn:hover {
  color: #fff;
  background: rgba(30, 41, 59, 0.9);
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(v-bind('collapsed ? "-100%" : "0"'));
    width: 260px;
  }

  .sidebar.collapsed {
    width: 260px;
  }
}
</style>
