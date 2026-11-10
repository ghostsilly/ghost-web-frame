<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, HomeFilled, SwitchButton, UserFilled } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 菜单高亮跟随当前路由；子路由为空路径时 route.path 就是 /admin
const activeMenu = computed(() => route.path)

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-header class="admin-header">
      <span class="brand">Ghost Frame 管理后台</span>
      <el-dropdown @command="handleLogout">
        <span class="user-entry">
          <el-icon><UserFilled /></el-icon>
          <span>{{ userStore.nickname || userStore.userInfo?.username || '已登录用户' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout" :icon="SwitchButton">登出</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </el-header>

    <el-container class="admin-body">
      <el-aside width="220px" class="admin-aside">
        <el-menu :default-active="activeMenu" router>
          <el-menu-item index="/admin">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--el-border-color);
}

.brand {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.user-entry {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: var(--el-text-color-primary);
  cursor: pointer;
  outline: none;
}

.admin-aside {
  border-right: 1px solid var(--el-border-color);
}

.admin-aside {
  border-right: none;
}

.admin-main {
  background: var(--el-fill-color-lighter);
}
</style>
