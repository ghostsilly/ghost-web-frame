import request from './request'

/** 登录：POST /ghost/auth/login */
export function login(data) {
    return request.post('/auth/login', data)
}

/** 注册：POST /ghost/auth/register */
export function register(data) {
    return request.post('/auth/register', data)
}

/** 获取当前登录用户：GET /ghost/auth/me */
export function getMe(config) {
    return request.get('/auth/me', config)
}