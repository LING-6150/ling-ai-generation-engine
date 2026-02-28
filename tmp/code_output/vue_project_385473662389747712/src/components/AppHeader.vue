<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <router-link to="/" class="logo-link">
            <span class="logo-text">ShopSphere</span>
          </router-link>
        </div>
        
        <nav class="nav">
          <ul class="nav-list">
            <li class="nav-item">
              <router-link to="/" class="nav-link">Home</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/" class="nav-link">Products</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/" class="nav-link">Categories</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/" class="nav-link">Deals</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/" class="nav-link">About</router-link>
            </li>
          </ul>
        </nav>
        
        <div class="header-actions">
          <button class="search-btn" @click="toggleSearch">
            🔍
          </button>
          
          <router-link to="/cart" class="cart-btn">
            🛒
            <span v-if="cartItemCount > 0" class="cart-badge">{{ cartItemCount }}</span>
          </router-link>
          
          <button class="user-btn" @click="toggleUserMenu">
            👤
          </button>
        </div>
      </div>
      
      <!-- Search Bar -->
      <div v-if="showSearch" class="search-bar">
        <input 
          type="text" 
          v-model="searchQuery"
          placeholder="Search for products..."
          class="search-input"
          @keyup.enter="performSearch"
        />
        <button class="search-submit" @click="performSearch">Search</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const showSearch = ref(false)
const searchQuery = ref('')

const cartItemCount = computed(() => {
  return cartStore.items.reduce((total, item) => total + item.quantity, 0)
})

const toggleSearch = () => {
  showSearch.value = !showSearch.value
  if (showSearch.value) {
    setTimeout(() => {
      document.querySelector('.search-input')?.focus()
    }, 100)
  }
}

const toggleUserMenu = () => {
  // In a real app, this would show user menu
  router.push('/')
}

const performSearch = () => {
  if (searchQuery.value.trim()) {
    // In a real app, this would navigate to search results
    console.log('Searching for:', searchQuery.value)
    searchQuery.value = ''
    showSearch.value = false
  }
}
</script>

<style scoped>
.header {
  background-color: white;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
}

.logo-link {
  text-decoration: none;
  color: inherit;
}

.logo-text {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--primary-color);
  letter-spacing: -0.025em;
}

.nav-list {
  display: flex;
  list-style: none;
  gap: 2rem;
  margin: 0;
  padding: 0;
}

.nav-link {
  text-decoration: none;
  color: var(--text-primary);
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: var(--primary-color);
}

.nav-link.router-link-active {
  color: var(--primary-color);
}

.nav-link.router-link-active:after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--primary-color);
  border-radius: 1px;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.search-btn,
.cart-btn,
.user-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: var(--radius-md);
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
}

.search-btn:hover,
.cart-btn:hover,
.user-btn:hover {
  background-color: var(--bg-tertiary);
}

.cart-btn {
  position: relative;
  text-decoration: none;
  color: inherit;
}

.cart-badge {
  position: absolute;
  top: -0.25rem;
  right: -0.25rem;
  background-color: var(--primary-color);
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  width: 1.25rem;
  height: 1.25rem;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-bar {
  padding: 1rem 0;
  border-top: 1px solid var(--border-color);
  display: flex;
  gap: 0.5rem;
}

.search-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

.search-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.search-submit {
  padding: 0.75rem 1.5rem;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.search-submit:hover {
  background-color: var(--primary-dark);
}

@media (max-width: 768px) {
  .header-content {
    flex-wrap: wrap;
  }
  
  .nav {
    order: 3;
    width: 100%;
    margin-top: 1rem;
  }
  
  .nav-list {
    justify-content: center;
    gap: 1rem;
  }
  
  .logo-text {
    font-size: 1.5rem;
  }
}

@media (max-width: 480px) {
  .nav-list {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .header-actions {
    gap: 0.5rem;
  }
}
</style>