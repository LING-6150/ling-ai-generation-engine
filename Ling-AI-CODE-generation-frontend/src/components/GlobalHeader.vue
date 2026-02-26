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
      <a-button type="primary" @click="() => $router.push('/user/login')">
        Sign In
      </a-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const selectedKeys = ref<string[]>(['/'])

router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

const menuItems = ref([
  { key: '/', label: 'Home', title: 'Home' },
  { key: '/explore', label: 'Explore', title: 'Explore' },
])

const handleMenuClick = ({ key }: { key: string }) => {
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
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
