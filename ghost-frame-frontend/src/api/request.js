import axios from "axios";
import router from "../router";

/**
 * 登录token的key,token在localStorage中，stores/user.js也要使用
 */
export const TOKEN_KEY = 'ghost-token'
export const REFRESH_TOKEN_KEY = 'ghost-refresh-token'

/** 并发 401 共用同一次刷新，避免把 refreshToken 用掉多次 */
let refreshing = null

const service = axios.create({
    baseURL: '/ghost',
    timeout: 10000,
})

/**
 * 统一处理401错误
 * 处理未授权的请求
 * 清除token，跳转到登录页
 */
function handleUnauthorized() {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    const current = router.currentRoute.value
    if (current.name !== 'login') {
        router.push({ name: 'login', query: { redirect: current.fullPath } })
    }
}

function canTryRefresh(config) {
    return !config?.skipAuthRedirect
        && !config?.skipRefresh
        && !config?._retry
        && !!localStorage.getItem(REFRESH_TOKEN_KEY)
}

function refreshAccessToken() {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!refreshing) {
        refreshing = service.post('/auth/refresh', { refreshToken }, {
            skipAuth: true,
            skipAuthRedirect: true,
            skipRefresh: true,
        }).then((data) => {
            if (!data?.token) {
                throw new Error('刷新登录状态失败')
            }
            localStorage.setItem(TOKEN_KEY, data.token)
            if (data.refreshToken) {
                localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
            }
            return data.token
        }).finally(() => {
            refreshing = null
        })
    }
    return refreshing
}

/** 401 时刷新一次并重放原请求；没有 refreshToken 或刷新失败则去登录页 */
function refreshAndRetry(config) {
    if (!canTryRefresh(config)) {
        if (!config?.skipAuthRedirect) {
            handleUnauthorized()
        }
        return Promise.reject(new Error('登录已过期'))
    }
    config._retry = true
    return refreshAccessToken().then((token) => {
        config.headers.Authorization = `Bearer ${token}`
        return service(config)
    }).catch((error) => {
        handleUnauthorized()
        return Promise.reject(error)
    })
}
// ==============请求拦截器：自动带上token==============
service.interceptors.request.use(
    (config) => {
        if (!config.skipAuth) {
            const token = localStorage.getItem(TOKEN_KEY)
            if (token) {
                config.headers.Authorization = `Bearer ${token}`
            }
        }
        return config
    },
    (error) => Promise.reject(error)
)

// ===== 响应拦截器：解包 Response + 分流错误 =====
service.interceptors.response.use(
    (response) => {
        const res = response.data

        // 成功：剥掉 {code,message,data} 外壳，直接把 data 交给调用方
        if (res && res.code === 200) {
            return res.data
        }

        // HTTP 200 但业务失败（后端全局异常处理器不改状态码）
        if (res && res.code === 401) {
            return refreshAndRetry(response.config)
        }
        return Promise.reject(new Error(res?.message || '请求失败'))
    },
    (error) => {
        // HTTP 层面的失败，目前主要是 Security 返回的 401
        if (error.response?.status === 401) {
            return refreshAndRetry(error.config)
        }
        const message = error.response?.data?.message || error.message || '网络异常'
        return Promise.reject(new Error(message))
    }
)

export default service