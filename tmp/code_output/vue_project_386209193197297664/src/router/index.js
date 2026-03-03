import { createRouter, createWebHashHistory } from 'vue-router'
import Dashboard from '../pages/Dashboard.vue'
import Transactions from '../pages/Transactions.vue'
import Budgets from '../pages/Budgets.vue'
import Goals from '../pages/Goals.vue'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: Dashboard
  },
  {
    path: '/transactions',
    name: 'Transactions',
    component: Transactions
  },
  {
    path: '/budgets',
    name: 'Budgets',
    component: Budgets
  },
  {
    path: '/goals',
    name: 'Goals',
    component: Goals
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router