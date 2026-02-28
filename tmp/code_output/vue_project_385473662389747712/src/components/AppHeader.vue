<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <router-link to="/" class="logo">
          <h1>ShopSphere</h1>
        </router-link>
        
        <nav class="nav">
          <router-link to="/" class="nav-link">Home</router-link>
          <router-link to="/" class="nav-link">Products</router-link>
          <router-link to="/" class="nav-link">Categories</router-link>
          <router-link to="/" class="nav-link">Deals</router-link>
        </nav>
        
        <div class="header-actions">
          <div class="cart-indicator" @click="goToCart">
            <svg class="cart-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <span class="cart-count">{{ cartItemCount }}</span>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const cartItemCount = computed(() => {
  return cartStore.items.reduce((total, item) => total + item.quantity, 0)
})

const goToCart = () => {
  router.push('/cart')
}
</script>

<style scoped>
.header {
  background-color: white;
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: 0;
  z-index: 100;
  padding: 1rem 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  text-decoration: none;
  color: var(--primary-color);
}

.logo h1 {
  font-size: 1.75rem;
  margin: 0;
}

.nav {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: var(--text-secondary);
  font-weight: 500;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: var(--primary-color);
}

.nav-link.router-link-active {
  color: var(--primary-color);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.cart-indicator {
  position: relative;
  cursor: pointer;
  padding: 0.5rem;
}

.cart-icon {
  width: 1.5rem;
  height: 1.5rem;
  color: var(--text-secondary);
}

.cart-count {
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

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav {
    gap: 1rem;
  }
  
  .logo h1 {
    font-size: 1.5rem;
  }
}
</style>