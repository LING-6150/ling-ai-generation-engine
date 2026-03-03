import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/pages/Dashboard.vue')
  },
  {
    path: '/transactions',
    name: 'Transactions',
    component: () => import('@/pages/Transactions.vue')
  },
  {
    path: '/budgets',
    name: 'Budgets',
    component: () => import('@/pages/Budgets.vue')
  },
  {
    path: '/goals',
    name: 'Goals',
    component: () => import('@/pages/Goals.vue')
  },
  {
    path: '/reports',
    name: 'Reports',
    component: () => import('@/pages/Reports.vue')
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router