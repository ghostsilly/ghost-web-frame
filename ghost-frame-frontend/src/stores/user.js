import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, getMe, logout as logoutApi } from '../api/auth'
import { REFRESH_TOKEN_KEY, TOKEN_KEY } from '../api/request'

export const useUserStore = defineStore('user', () => {
    // ===== state =====
    // 初始化时从 localStorage 读回，这样刷新页面不会掉登录态
    const token = ref(localStorage.getItem(TOKEN_KEY) || '')
    const refreshToken = ref(localStorage.getItem(REFRESH_TOKEN_KEY) || '')
    const userInfo = ref(null)

    // ===== getters =====
    const isLoggedIn = computed(() => !!token.value)
    const nickname = computed(() => userInfo.value?.nickname ?? '')

    // ===== 内部方法 =====
    function setToken(newToken) {
        token.value = newToken
        localStorage.setItem(TOKEN_KEY, newToken)
    }

    function setRefreshToken(newToken) {
        refreshToken.value = newToken || ''
        if (newToken) {
            localStorage.setItem(REFRESH_TOKEN_KEY, newToken)
        } else {
            localStorage.removeItem(REFRESH_TOKEN_KEY)
        }
    }

    function clear() {
        token.value = ''
        refreshToken.value = ''
        userInfo.value = null
        localStorage.removeItem(TOKEN_KEY)
        localStorage.removeItem(REFRESH_TOKEN_KEY)
    }

    // ===== actions =====
    async function login(payload) {
        const data = await loginApi(payload)
        setToken(data.token)
        setRefreshToken(data.refreshToken)
        userInfo.value = data
        return data
    }

    async function register(payload) {
        const data = await registerApi(payload)
        setToken(data.token)
        setRefreshToken(data.refreshToken)
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

    async function logout() {
        try {
            await logoutApi({ skipAuthRedirect: true, skipRefresh: true })
        } catch {
            // 登出接口尚未提供或 token 已失效时，仍然清掉本地登录态
        }
        clear()
    }

    return {
        token, refreshToken, userInfo,
        isLoggedIn, nickname,
        login, register, fetchMe, logout,
    }
})