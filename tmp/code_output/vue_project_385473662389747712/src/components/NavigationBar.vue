<template>
  <nav class="navigation-bar">
    <div class="container">
      <div class="nav-content">
        <router-link to="/" class="logo">
          <span class="logo-text">ShopSphere</span>
        </router-link>

        <div class="nav-links">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
            Home
          </router-link>
          <router-link to="/products" class="nav-link" :class="{ active: $route.path === '/products' }">
            Products
          </router-link>
        </div>

        <div class="nav-actions">
          <div class="search-container">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Search products..."
              class="search-input"
              @keyup.enter="performSearch"
            />
            <button class="search-button" @click="performSearch">
              🔍
            </button>
          </div>

          <router-link to="/cart" class="cart-button">
            <span class="cart-icon">🛒</span>
            <span v-if="cartStore.totalItems > 0" class="cart-count">
              {{ cartStore.totalItems }}
            </span>
          </router-link>
        </div>

        <button class="mobile-menu-button" @click="toggleMobileMenu">
          ☰
        </button>
      </div>

      <div v-if="isMobileMenuOpen" class="mobile-menu">
        <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">
          Home
        </router-link>
        <router-link to="/products" class="mobile-nav-link" @click="closeMobileMenu">
          Products
        </router-link>
        <div class="mobile-search">
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Search products..."
            class="mobile-search-input"
            @keyup.enter="performSearch"
          />
          <button class="mobile-search-button" @click="performSearch">
            Search
          </button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const searchQuery = ref('')
const isMobileMenuOpen = ref(false)

const performSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      path: '/products',
      query: { search: searchQuery.value.trim() }
    })
    closeMobileMenu()
  }
}

const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

watch(() => route.query.search, (newSearch) => {
  if (newSearch) {
    searchQuery.value = newSearch
  } else {
    searchQuery.value = ''
  }
})
</script>

<style scoped>
.navigation-bar {
  background-color: var(--bg-primary);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-sm);
}

.nav-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
  position: relative;
}

.logo {
  text-decoration: none;
  display: flex;
  align-items: center;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
  letter-spacing: -0.025em;
}

.nav-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  text-decoration: none;
  color: var(--text-secondary);
  font-weight: 500;
  font-size: 0.875rem;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: var(--text-primary);
}

.nav-link.active {
  color: var(--primary-color);
}

.nav-link.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--primary-color);
  border-radius: var(--radius-full);
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.search-container {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 240px;
  padding: 0.5rem 1rem 0.5rem 2.5rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-family: var(--font-sans);
  font-size: 0.875rem;
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
  transition: all 0.2s ease;
}

.search-input:focus {
  outline: none;
  background-color: white;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
  width: 280px;
}

.search-button {
  position: absolute;
  left: 0.75rem;
  background: none;
  border: none;
  color: var(--text-light);
  font-size: 1rem;
  cursor: pointer;
  padding: 0.25rem;
}

.cart-button {
  position: relative;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  text-decoration: none;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-icon {
  font-size: 1.5rem;
  color: var(--text-primary);
}

.cart-count {
  position: absolute;
  top: -0.25rem;
  right: -0.25rem;
  background-color: var(--primary-color);
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  min-width: 1.25rem;
  height: 1.25rem;
  border-radius: var(--radius-full);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 0.25rem;
}

.mobile-menu-button {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--text-primary);
  cursor: pointer;
  padding: 0.5rem;
}

.mobile-menu {
  display: none;
  padding: 1rem 0;
  border-top: 1px solid var(--border-color);
}

.mobile-nav-link {
  display: block;
  text-decoration: none;
  color: var(--text-secondary);
  font-weight: 500;
  padding: 0.75rem 0;
  border-bottom: 1px solid var(--border-color);
}

.mobile-nav-link:last-child {
  border-bottom: none;
}

.mobile-nav-link:hover {
  color: var(--primary-color);
}

.mobile-search {
  margin-top: 1rem;
  display: flex;
  gap: 0.5rem;
}

.mobile-search-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 0.875rem;
}

.mobile-search-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
}

@media (max-width: 768px) {
  .nav-links,
  .nav-actions {
    display: none;
  }
  
  .mobile-menu-button {
    display: block;
  }
  
  .mobile-menu {
    display: block;
  }
  
  .search-input {
    width: 200px;
  }
  
  .search-input:focus {
    width: 200px;
  }
}

@media (max-width: 480px) {
  .logo-text {
    font-size: 1.25rem;
  }
  
  .search-input {
    width: 160px;
  }
  
  .search-input:focus {
    width: 160px;
  }
}
</style>