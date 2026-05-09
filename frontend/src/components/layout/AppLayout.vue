<template>
  <div class="app-layout">
    <Sidebar :collapsed="isCollapsed" @toggle="toggleSidebar" />

    <div class="main-container" :class="{ collapsed: isCollapsed }">
      <Header :collapsed="isCollapsed" @toggle="toggleSidebar" />
      <main class="content scrollbar-thin">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <div class="page-shell">
              <component :is="Component" />
            </div>
          </transition>
        </router-view>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const isCollapsed = ref(false)

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.app-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(180deg, #e2e8f0 0%, #f8fafc 280px);
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-left: 220px;
  transition: margin-left 0.3s ease;
  min-height: 100vh;
}

.main-container.collapsed {
  margin-left: 88px;
}

.content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.page-shell {
  min-height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(6px);
}

@media (max-width: 768px) {
  .main-container,
  .main-container.collapsed {
    margin-left: 0;
  }

  .content {
    padding: 16px;
  }
}
</style>
