<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const router = useRouter()
const userStore = useUserStore()

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: '',   // 仅前端比对用，不会发给后端
})

const loading = ref(false)
const errorMsg = ref('')

async function handleSubmit() {
  if (loading.value) return
  errorMsg.value = ''

  // 校验规则与后端 RegisterRequestDTO 的注解逐条对齐
  if (!form.username) {
    errorMsg.value = '请输入用户名'
    return
  }
  if (form.username.length < 5 || form.username.length > 20) {
    errorMsg.value = '用户名长度需在 5 到 20 个字符之间'
    return
  }
  if (!form.nickname) {
    errorMsg.value = '请输入昵称'
    return
  }
  if (!form.password) {
    errorMsg.value = '请输入密码'
    return
  }
  if (form.password !== form.confirmPassword) {
    errorMsg.value = '两次输入的密码不一致'
    return
  }

  loading.value = true
  try {
    // 只发后端 DTO 里存在的三个字段
    await userStore.register({
      username: form.username,
      nickname: form.nickname,
      password: form.password,
    })
    router.push('/')
  } catch (err) {
    errorMsg.value = err.message || '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <h1>注册</h1>
      <p class="auth-sub">创建一个新账号，注册成功后自动登录。</p>

      <p v-if="errorMsg" class="auth-error">{{ errorMsg }}</p>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label for="reg-username">用户名</label>
          <input id="reg-username" v-model.trim="form.username" type="text" autocomplete="username"
            placeholder="5–20 个字符" />
        </div>

        <div class="field">
          <label for="reg-nickname">昵称</label>
          <input id="reg-nickname" v-model.trim="form.nickname" type="text" autocomplete="nickname"
            placeholder="用于展示的昵称" />
        </div>

        <div class="field">
          <label for="reg-password">密码</label>
          <input id="reg-password" v-model="form.password" type="password" autocomplete="new-password"
            placeholder="请输入密码" />
        </div>

        <div class="field">
          <label for="reg-confirm">确认密码</label>
          <input id="reg-confirm" v-model="form.confirmPassword" type="password" autocomplete="new-password"
            placeholder="请再次输入密码" />
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '注册中…' : '注册' }}
        </button>
      </form>

      <p class="auth-footer">
        已有账号？<router-link to="/login">去登录</router-link>
      </p>
    </div>
  </div>
</template>