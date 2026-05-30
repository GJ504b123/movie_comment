import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'home',
    // @ts-ignore  可以加上这个本地不报错
    component: () => import('../view/UserHome.vue')
  },
  {
    path: '/movie/:id',
    name: 'detail',
    component: () => import('../view/MovieDetail.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router