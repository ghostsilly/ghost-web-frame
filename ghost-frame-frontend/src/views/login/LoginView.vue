<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 表单数据：一组字段用 reactive，取值时写 form.username，不用 .value
const form = reactive({
  username: '',
  password: '',
})

const loading = ref(false)   // 提交中：禁用按钮，防重复点击
const errorMsg = ref('')     // 错误提示
const errorField = ref('')   // username | password，决定提示框出现在哪个输入框下
const passwordInput = ref(null)

/** 用户名输入完成后按回车，焦点移到密码框，不提交表单 */
function focusPassword() {
  passwordInput.value?.focus()
}

async function handleSubmit() {
  if (loading.value) return          // 防重复提交
  errorMsg.value = ''
  errorField.value = ''

  // 前置校验：后端登录接口只要求非空（@NotBlank）
  if (!form.username) {
    errorField.value = 'username'
    errorMsg.value = '请输入用户名'
    return
  }
  if (!form.password) {
    errorField.value = 'password'
    errorMsg.value = '请输入密码'
    return
  }

  loading.value = true
  try {
    await userStore.login({
      username: form.username,
      password: form.password,
    })
    router.push(resolveRedirect())
  } catch (err) {
    // request.js 已把后端 message 包成 Error，这里直接取
    errorField.value = 'password'
    errorMsg.value = err.message || '登录失败，请稍后重试'
  } finally {
    loading.value = false            // 成功失败都要恢复按钮
  }
}

/** 跳回原页面；redirect 来自路由守卫，需校验防开放重定向 */
function resolveRedirect() {
  const target = route.query.redirect
  if (typeof target === 'string' && target.startsWith('/') && !target.startsWith('//')) {
    return target
  }
  return '/'
}
</script>

<template>
  <div class="auth-wrap">
    <div class="auth-card">
      <h1>登录</h1>
      <p class="auth-sub">欢迎回来，请登录你的账号。</p>

      <form @submit.prevent="handleSubmit">
        <div class="field">
          <label for="username">用户名</label>
          <input id="username" v-model.trim="form.username" type="text" autocomplete="username" placeholder="请输入用户名"
            :class="{ 'input-invalid': errorField === 'username' }"
            :aria-invalid="errorField === 'username'"
            :aria-describedby="errorField === 'username' ? 'username-error' : undefined"
            @keydown.enter.prevent="focusPassword" />
          <p v-if="errorField === 'username'" id="username-error" class="field-error">{{ errorMsg }}</p>
        </div>

        <div class="field">
          <label for="password">密码</label>
          <input id="password" ref="passwordInput" v-model="form.password" type="password" autocomplete="current-password"
            placeholder="请输入密码"
            :class="{ 'input-invalid': errorField === 'password' }"
            :aria-invalid="errorField === 'password'"
            :aria-describedby="errorField === 'password' ? 'password-error' : undefined" />
          <p v-if="errorField === 'password'" id="password-error" class="field-error">{{ errorMsg }}</p>
        </div>

        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? '登录中…' : '登录' }}
        </button>
      </form>

      <p class="auth-footer">
        还没有账号？<router-link to="/register">去注册</router-link>
      </p>
    </div>
  </div>
</template>