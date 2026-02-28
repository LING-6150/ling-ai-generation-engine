<template>
  <div class="header-inner">
    <!-- Left: Logo -->
    <div class="header-left">
      <router-link to="/">
        <div class="logo-area">
          <img src="@/assets/logo.png" alt="Logo" class="logo" />
          <span class="site-title">AI Code Generator</span>
        </div>
      </router-link>
    </div>

    <!-- Center: Nav Menu -->
    <div class="header-center">
      <a-menu
        v-model:selectedKeys="selectedKeys"
        mode="horizontal"
        :items="menuItems"
        @click="handleMenuClick"
        class="nav-menu"
      />
    </div>

    <!-- Right: User Actions -->
    <div class="header-right">
      <div v-if="loginUserStore.loginUser.id">
        <a-dropdown>
          <a-space>
            <a-avatar :src="loginUserStore.loginUser.userAvatar" />
            {{ loginUserStore.loginUser.userName ?? 'Anonymous' }}
          </a-space>
          <template #overlay>
            <a-menu>
              <a-menu-item @click="doLogout">
                Sign Out
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </div>
      <div v-else>
        <a-button type="primary" @click="() => $router.push('/user/login')">
          Sign In
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { userLogout } from '@/api/userController'
import { message } from 'ant-design-vue'
import checkAccess from '@/access/checkAccess'
import ACCESS_ENUM from '@/access/accessEnum'
import type { MenuProps } from 'ant-design-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const selectedKeys = ref<string[]>(['/'])

router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/my/apps',
    label: 'My Apps',
    title: 'My Apps',
  },
  {
    key: '/admin/userManage',
    label: 'User Management',
    title: 'User Management',
  },
  {
    key: '/admin/appManage',
    label: 'App Management',
    title: 'App Management',
  },
  {
    key: '/admin/chatManage',
    label: 'Chat Management',
    title: 'Chat Management',
  },
]

// 过滤菜单项
const filterMenus = (menus: typeof originItems) => {
  return menus.filter((menu) => {
    const menuKey = menu.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== ACCESS_ENUM.ADMIN) {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

const handleMenuClick = ({ key }: { key: string }) => {
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 用户注销
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({ userName: 'Not logged in' })
    message.success('Signed out successfully')
    await router.push('/user/login')
  } else {
    message.error('Sign out failed, ' + res.data.message)
  }
}
</script>

<style scoped>
.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
}

.logo {
  height: 36px;
  width: 36px;
  object-fit: contain;
}

.site-title {
  font-size: 18px;
  font-weight: 700;
  color: #1677ff;
}

.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-menu {
  border-bottom: none;
  min-width: 200px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
</style>
