import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: () => import('../view/Login.vue'),
    meta: { public: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../view/Register.vue'),
    meta: { public: true },
  },
  {
    path: '/',
    name: 'home',
    component: () => import('../view/UserHome.vue'),
  },
  {
    path: '/ranking',
    name: 'ranking',
    component: () => import('../view/Ranking.vue'),
  },
  {
    path: '/movie/:id',
    name: 'detail',
    component: () => import('../view/MovieDetail.vue'),
  },
  {
    path: '/admin',
    component: () => import('../view/admin/AdminLayout.vue'),
    meta: { requiresAdmin: true },
    redirect: '/admin/users',
    children: [
      { path: 'users', name: 'admin-users', component: () => import('../view/admin/AdminPendingUsers.vue') },
      { path: 'movies', name: 'admin-movies', component: () => import('../view/admin/AdminMovies.vue') },
      { path: 'reviews', name: 'admin-reviews', component: () => import('../view/admin/AdminReviews.vue') },
      { path: 'logs', name: 'admin-logs', component: () => import('../view/admin/AdminLogs.vue') },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 全局路由守卫：未登录跳登录页，非管理员禁止进入后台
router.beforeEach((to) => {
  const userStore = useUserStore()
  if (!userStore.token) {
    userStore.restoreAuth()
  }

  if (to.meta.public) {
    return true
  }

  if (!userStore.isLoggedIn) {
    return { name: 'login', query: { redirect: to.fullPath } }
  }

  if (to.meta.requiresAdmin && !userStore.isAdmin) {
    return { name: 'home' }
  }

  return true
})

export default router
