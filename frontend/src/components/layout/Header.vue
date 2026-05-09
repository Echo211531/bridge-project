<template>
  <header class="header">
    <!-- 左侧折叠按钮 -->
    <div class="left">
      <el-icon class="toggle-btn" @click="$emit('toggle')">
        <component :is="collapsed ? 'Expand' : 'Fold'" />
      </el-icon>
      <span class="title">{{ pageTitle }}</span>
    </div>

    <!-- 右侧用户信息 -->
    <div class="right">
      <!-- 用户下拉菜单 -->
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-info">
          <el-avatar :size="32" class="avatar">
            {{ username.charAt(0).toUpperCase() }}
          </el-avatar>
          <span class="username">{{ username }}</span>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>
              个人信息
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup>
/**
 * 顶部栏组件
 *
 * @author 程国忠
 * @since 2026-05-09
 */
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Expand, Fold, ArrowDown, User, SwitchButton } from '@element-plus/icons-vue'
import { logout } from '@/api/auth'

defineProps({
  collapsed: Boolean
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 页面标题
const pageTitle = computed(() => route.meta?.title || '桥梁设备全生命周期管理系统')

// 用户名
const username = computed(() => userStore.username || '用户')

// 处理下拉菜单命令
async function handleCommand(command) {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })

      // 调用后端登出接口
      await logout()

      // 清除用户信息
      userStore.clearUserInfo()

      ElMessage.success('已退出登录')
      router.push('/login')
    } catch (error) {
      if (error !== 'cancel') {
        console.error('退出登录失败:', error)
      }
    }
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中')
  }
}
</script>

<style scoped>
.header {
  height: 60px;
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.left {
  display: flex;
  align-items: center;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 4px;
}

.user-info:hover {
  background-color: #f5f7fa;
}

.avatar {
  background-color: #409eff;
  color: #fff;
  margin-right: 8px;
}

.username {
  margin-right: 8px;
  color: #303133;
}
</style>