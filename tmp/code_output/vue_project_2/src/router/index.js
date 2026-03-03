import { createRouter, createWebHashHistory } from 'vue-router'
import HomePage from '../pages/HomePage.vue'
import ProductsPage from '../pages/ProductsPage.vue'
import CartPage from '../pages/CartPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/products',
    name: 'Products',
    component: ProductsPage
  },
  {
    path: '/cart',
    name: 'Cart',
    component: CartPage
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router