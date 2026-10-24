<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

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
  if (!userStore.isLoggedIn || userStore.userInfo) {
    return
  }
  loading.value = true
  try {
    await userStore.fetchMe()
  } catch {
    await userStore.logout()
    router.push('/login')
  } finally {
    loading.value = false
  }
})

async function handleLogout() {
  await userStore.logout()
  router.push('/login')
}
</script>

<template>
  <main class="home">
    <h1>Ghost Frame</h1>

    <section v-if="userStore.userInfo" class="card">
      <p><span class="k">昵称</span>{{ userStore.userInfo.nickname }}</p>
      <p><span class="k">用户名</span>{{ userStore.userInfo.username }}</p>
      <p><span class="k">角色</span>{{ roleText }}</p>
    </section>

    <p v-else-if="loading" class="tip">正在加载用户信息</p>

    <div class="actions">
      <button type="button" class="btn-ghost" @click="handleLogout">登出</button>
    </div>
  </main>
</template>

<style scoped>
.home {
  max-width: 640px;
  margin: 0 auto;
  padding: 64px 24px;
}

.card {
  margin-top: 24px;
  padding: 20px 24px;
  border: 1px solid var(--border);
  border-radius: 10px;
}

.card p {
  display: flex;
  gap: 12px;
  padding: 4px 0;
}

.k {
  flex: 0 0 64px;
  color: var(--text);
}

.tip {
  margin-top: 24px;
  color: var(--text);
}

.actions {
  margin-top: 32px;
}

.btn-ghost {
  padding: 8px 20px;
  font: inherit;
  color: var(--text-h);
  background: transparent;
  border: 1px solid var(--border);
  border-radius: 6px;
  cursor: pointer;
}

.btn-ghost:hover {
  border-color: var(--accent);
  color: var(--accent);
}
</style>