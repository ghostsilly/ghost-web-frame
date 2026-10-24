import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMe } from '../api/auth'
import { TOKEN_KEY } from '../api/request'

export const useUserStore = defineStore('user', () => {
    // ===== state =====
    // 初始化时从 localStorage 读回，这样刷新页面不会掉登录态
    const token = ref(localStorage.getItem(TOKEN_KEY) || '')
    const userInfo = ref(null)

    // ===== getters =====
    const isLoggedIn = computed(() => !!token.value)
    const nickname = computed(() => userInfo.value?.nickname ?? '')

    // ===== 内部方法 =====
    function setToken(newToken) {
        token.value = newToken
        localStorage.setItem(TOKEN_KEY, newToken)
    }

    function clear() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem(TOKEN_KEY)
    }

    // ===== actions =====
    async function login(payload) {
        const data = await loginApi(payload)
        setToken(data.token)
        userInfo.value = data
        return data
    }

    async function register(payload) {
        const data = await registerApi(payload)
        setToken(data.token)
        userInfo.value = data
        return data
    }

    async function fetchMe() {
        //skipAuthRedirect: true 表示不跳转到登录页，直接抛出错误
        const data = await getMe({ skipAuthRedirect: true })
        // /me 的 resultMap 不返回 token，只能合并、不能整体覆盖
        userInfo.value = { ...userInfo.value, ...data }
        return userInfo.value
    }

    function logout() {
        clear()
    }

    return {
        token, userInfo,
        isLoggedIn, nickname,
        login, register, fetchMe, logout,
    }
})