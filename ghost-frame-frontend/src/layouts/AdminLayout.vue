<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  ArrowDown,
  Connection,
  Expand,
  Fold,
  HomeFilled,
  Setting,
  SwitchButton,
  User,
  UserFilled,
} from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const NARROW_QUERY = '(max-width: 768px)'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)
// 菜单高亮跟随当前路由；子路由为空路径时 route.path 就是 /admin
const activeMenu = computed(() => route.path)

let narrowMedia

function syncCollapsed() {
  collapsed.value = narrowMedia.matches
}

function toggleCollapsed() {
  collapsed.value = !collapsed.value
}

function goFront() {
  router.push('/')
}

async function handleCommand(command) {
  if (command !== 'logout') {
    return
  }
  await userStore.logout()
  router.push('/login')
}

onMounted(() => {
  narrowMedia = window.matchMedia(NARROW_QUERY)
  syncCollapsed()
  narrowMedia.addEventListener('change', syncCollapsed)
})

onBeforeUnmount(() => {
  narrowMedia?.removeEventListener('change', syncCollapsed)
})
</script>

<template>
  <el-container class="admin-layout">
    <el-header class="admin-header">
      <div class="header-side">
        <el-button
          text
          :icon="collapsed ? Expand : Fold"
          aria-label="折叠侧栏"
          @click="toggleCollapsed"
        />
        <span class="brand">Ghost Frame 管理后台</span>
      </div>
      <div class="header-side">
        <el-button text @click="goFront">返回前台</el-button>
        <el-dropdown @command="handleCommand">
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
      </div>
    </el-header>

    <el-container class="admin-body">
      <el-aside :width="collapsed ? '64px' : '220px'" class="admin-aside">
        <el-menu
          class="admin-menu"
          :default-active="activeMenu"
          :default-openeds="['system']"
          :collapse="collapsed"
          router
        >
          <el-menu-item index="/admin">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-sub-menu index="system">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统管理</span>
            </template>
            <el-menu-item index="/admin/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/api-resources">
              <el-icon><Connection /></el-icon>
              <span>功能管理</span>
            </el-menu-item>
          </el-sub-menu>
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

.header-side {
  display: flex;
  align-items: center;
  gap: 8px;
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

.admin-body {
  min-height: 0;
}

.admin-aside {
  border-right: 1px solid var(--el-border-color);
  overflow: hidden;
  transition: width 0.2s;
}

.admin-menu {
  height: 100%;
  border-right: none;
}

.admin-main {
  background: var(--el-fill-color-lighter);
}
</style>
