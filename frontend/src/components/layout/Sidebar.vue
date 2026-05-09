<template>
  <aside class="sidebar" :class="{ collapsed: collapsed }">
    <!-- Logo -->
    <div class="logo">
      <img src="@/assets/logo.svg" alt="Logo" class="logo-icon" v-if="!collapsed" />
      <span class="logo-text" v-if="!collapsed">桥梁设备全生命周期管理系统</span>
      <span class="logo-mini" v-if="collapsed">BLC</span>
    </div>

    <!-- 菜单 -->
    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :collapse-transition="false"
      router
      class="sidebar-menu"
    >
      <el-menu-item index="/dashboard">
        <el-icon><Dashboard /></el-icon>
        <template #title>首页仪表盘</template>
      </el-menu-item>

      <el-menu-item index="/lifecycle">
        <el-icon><Timeline /></el-icon>
        <template #title>全生命周期</template>
      </el-menu-item>

      <el-sub-menu index="bridge-group">
        <template #title>
          <el-icon><OfficeBuilding /></el-icon>
          <span>桥梁设备</span>
        </template>
        <el-menu-item index="/bridges">
          <el-icon><Location /></el-icon>
          <template #title>桥梁管理</template>
        </el-menu-item>
        <el-menu-item index="/devices">
          <el-icon><Box /></el-icon>
          <template #title>设备档案</template>
        </el-menu-item>
      </el-sub-menu>

      <el-sub-menu index="maintain-group">
        <template #title>
          <el-icon><Tools /></el-icon>
          <span>运维管理</span>
        </template>
        <el-menu-item index="/maintain">
          <el-icon><Calendar /></el-icon>
          <template #title>保养管理</template>
        </el-menu-item>
        <el-menu-item index="/fault">
          <el-icon><Warning /></el-icon>
          <template #title>故障工单</template>
        </el-menu-item>
      </el-sub-menu>

      <el-menu-item index="/purchase">
        <el-icon><ShoppingCart /></el-icon>
        <template #title>采购入库</template>
      </el-menu-item>

      <el-menu-item index="/scrap">
        <el-icon><DeleteFilled /></el-icon>
        <template #title>报废鉴定</template>
      </el-menu-item>

      <el-menu-item index="/stats">
        <el-icon><DataAnalysis /></el-icon>
        <template #title>统计分析</template>
      </el-menu-item>

      <el-sub-menu index="system-group" v-if="isAdmin">
        <template #title>
          <el-icon><Setting /></el-icon>
          <span>系统管理</span>
        </template>
        <el-menu-item index="/config">
          <el-icon><Operation /></el-icon>
          <template #title>系统配置</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
        <el-menu-item index="/audit">
          <el-icon><Document /></el-icon>
          <template #title>审计日志</template>
        </el-menu-item>
      </el-sub-menu>
    </el-menu>

    <!-- 折叠按钮 -->
    <div class="collapse-btn" @click="$emit('toggle')">
      <el-icon>
        <component :is="collapsed ? 'Expand' : 'Fold'" />
      </el-icon>
    </div>
  </aside>
</template>

<script setup>
/**
 * 侧边栏组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  Dashboard, Timeline, OfficeBuilding, Location, Box,
  Tools, Calendar, Warning, ShoppingCart, DeleteFilled,
  DataAnalysis, Setting, Operation, User, Document,
  Expand, Fold
} from '@element-plus/icons-vue'

defineProps({
  collapsed: Boolean
})

defineEmits(['toggle'])

const route = useRoute()
const userStore = useUserStore()

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 是否为管理员
const isAdmin = computed(() => userStore.roleCode === 'admin')
</script>

<style scoped>
.sidebar {
  width: 220px;
  height: 100vh;
  background-color: #304156;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1001;
  transition: width 0.3s ease;
  overflow: hidden;
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  background-color: #263445;
  color: #fff;
}

.logo-icon {
  width: 32px;
  height: 32px;
}

.logo-text {
  font-size: 14px;
  font-weight: 600;
  margin-left: 10px;
  white-space: nowrap;
}

.logo-mini {
  font-size: 18px;
  font-weight: bold;
}

.sidebar-menu {
  border-right: none;
  background-color: #304156;
  height: calc(100vh - 100px);
}

.sidebar-menu:not(.el-menu--collapse) {
  width: 220px;
}

.el-menu-item,
.el-sub-menu__title {
  color: #bfcbd9;
}

.el-menu-item:hover,
.el-sub-menu__title:hover {
  background-color: #263445;
}

.el-menu-item.is-active {
  color: #409eff;
  background-color: #263445;
}

.collapse-btn {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
  color: #bfcbd9;
  cursor: pointer;
}

.collapse-btn:hover {
  color: #fff;
}
</style>