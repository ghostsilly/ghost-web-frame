<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)

// 后端 UserRole 枚举经 Jackson 序列化为枚举名字符串
const ROLE_LABEL = {
  USER: '普通用户',
  ADMIN: '管理员',
  SUPER_ADMIN: '超级管理员',
}

const roleText = computed(() => ROLE_LABEL[userStore.userInfo?.role] ?? '未知角色')

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
  <el-card v-loading="loading" class="dashboard-card" shadow="never">
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
</template>

<style scoped>
.dashboard-card {
  max-width: 640px;
}
</style>
