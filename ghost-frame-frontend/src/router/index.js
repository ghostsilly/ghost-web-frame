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
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
})

router.beforeEach((to) => {
    const userStore = useUserStore()

    if (to.meta.public) {
        if (userStore.isLoggedIn && (to.name === 'login' || to.name === 'register')) {
            return { name: 'home' }
        }
        return true
    }

    if (!userStore.isLoggedIn) {
        return { name: 'login', query: { redirect: to.fullPath } }
    }

    return true
})

export default router