import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user.js'

const routes = [
    {
        path: '/',
        name: 'home',
        component: () => import('../views/HomeView.vue'),
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/login/LoginView.vue'),
        meta: { public: true },
    },
    {
        path: '/register',
        name: 'register',
        component: () => import('../views/login/RegisterView.vue'),
        meta: { public: true },
    },
    {
        // 管理区：不加 meta.public，未登录访问会被守卫拦下并带 redirect 跳登录页
        path: '/admin',
        component: () => import('../layouts/AdminLayout.vue'),
        children: [
            {
                path: '',
                name: 'admin-dashboard',
                component: () => import('../views/admin/DashboardView.vue'),
            },
        ],
    },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
})

router.beforeEach((to) => {
    const userStore = useUserStore()

    if (to.meta.public) {
        if (userStore.isLoggedIn && (to.name === 'login' || to.name === 'register')) {
            // 管理员/超级管理员默认进管理后台，与登录后的跳转规则保持一致
            const role = userStore.userInfo?.role
            return role === 'ADMIN' || role === 'SUPER_ADMIN'
                ? { path: '/admin' }
                : { name: 'home' }
        }
        return true
    }

    if (!userStore.isLoggedIn) {
        // 目标是首页时不带 redirect：否则 admin 登录后会被 redirect=/ 抢先送回首页，
        // 轮不到登录页按角色分流；普通用户默认落点同样是 /，行为不变
        return to.fullPath === '/'
            ? { name: 'login' }
            : { name: 'login', query: { redirect: to.fullPath } }
    }

    return true
})

export default router