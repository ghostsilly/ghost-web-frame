<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'
import { roleLabel } from '../../constants/userRole'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

const roleText = computed(() => roleLabel(userStore.userInfo?.role))

onMounted(async () => {
  if (!userStore.isLoggedIn) {
    return
  }
  if (!userStore.userInfo) {
    loading.value = true
    try {
      await userStore.fetchMe()
    } catch {
      await userStore.logout()
      router.push('/login')
    } finally {
      loading.value = false
    }
  }
})
</script>

<template>
  <div v-loading="loading" class="dashboard">
    <header class="page-head">
      <h1>首页</h1>
    </header>

    <el-card shadow="never">
      <template #header>
        <span>个人信息</span>
      </template>

      <el-descriptions v-if="userStore.userInfo" :column="1" border>
        <el-descriptions-item label="昵称">{{ userStore.userInfo.nickname }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ userStore.userInfo.username }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag>{{ roleText }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<style scoped>
.page-head {
  margin-bottom: 16px;
}

.page-head h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}
</style>
