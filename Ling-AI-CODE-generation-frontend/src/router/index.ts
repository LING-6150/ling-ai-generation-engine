import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '@/layouts/BasicLayout.vue'
import HomeView from '@/views/HomeView.vue'
import ACCESS_ENUM from '@/access/accessEnum'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: BasicLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: HomeView,
        },
        {
          path: '/user/login',
          name: 'userLogin',
          component: () => import('@/pages/user/UserLoginPage.vue'),
        },
        {
          path: '/user/register',
          name: 'userRegister',
          component: () => import('@/pages/user/UserRegisterPage.vue'),
        },
        {
          path: '/noAuth',
          name: 'noAuth',
          component: () => import('@/pages/NoAuthPage.vue'),
        },
        {
          path: '/admin/userManage',
          name: 'adminUserManage',
          component: () => import('@/pages/admin/UserManagePage.vue'),
          meta: {
            access: ACCESS_ENUM.ADMIN,
          },
        },
        {
          path: '/admin/chatManage',
          name: 'adminChatManage',
          component: () => import('@/views/admin/ChatManagePage.vue'),
          meta: {
            access: ACCESS_ENUM.ADMIN,
          },
        },
        {
          path: '/chat/:appId',
          name: 'chat',
          component: () => import('@/views/AppChatView.vue'),
        },
        {
          path: '/my/apps',
          name: 'myApps',
          component: () => import('@/views/MyAppsView.vue'),
        },
        {
          path: '/app/:appId',
          name: 'appDetail',
          component: () => import('@/views/AppDetailView.vue'),
        },
        {
          path: '/admin/appManage',
          name: 'adminAppManage',
          component: () => import('@/views/admin/AppManageView.vue'),
          meta: {
            access: ACCESS_ENUM.ADMIN,
          },
        },
      ],
    },
  ],
})

export default router
