import { createRouter, createWebHashHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import ArticlePage from '@/pages/ArticlePage.vue'
import AboutPage from '@/pages/AboutPage.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: HomePage
  },
  {
    path: '/article/:id',
    name: 'Article',
    component: ArticlePage,
    props: true
  },
  {
    path: '/about',
    name: 'About',
    component: AboutPage
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router