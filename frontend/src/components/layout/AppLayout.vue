<template>
  <div class="app-layout">
    <!-- 侧边栏 -->
    <Sidebar :collapsed="isCollapsed" @toggle="toggleSidebar" />

    <!-- 主内容区 -->
    <div class="main-container" :class="{ collapsed: isCollapsed }">
      <!-- 顶部栏 -->
      <Header :collapsed="isCollapsed" @toggle="toggleSidebar" />

      <!-- 内容区 -->
      <main class="content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
/**
 * 应用布局组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 切换折叠状态
function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.app-layout {
  display: flex;
  height: 100vh;
  width: 100%;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 220px;
  transition: margin-left 0.3s ease;
  background-color: #f5f7fa;
}

.main-container.collapsed {
  margin-left: 64px;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>