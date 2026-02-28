<template>
  <div id="app">
    <nav class="navbar">
      <div class="container">
        <router-link to="/" class="logo">
          <h1>ShopVue</h1>
        </router-link>
        <div class="nav-links">
          <router-link to="/">Home</router-link>
          <router-link to="/products">Products</router-link>
          <router-link to="/cart" class="cart-link">
            <span class="cart-icon">🛒</span>
            <span v-if="cartStore.totalItems > 0" class="cart-count">{{ cartStore.totalItems }}</span>
          </router-link>
        </div>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="footer">
      <div class="container">
        <p>&copy; 2023 ShopVue E-Commerce Platform. All rights reserved.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useCartStore } from './stores/cart'

const cartStore = useCartStore()
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  line-height: 1.6;
  color: #333;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  color: white;
  text-decoration: none;
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-links a {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-links a:hover {
  background-color: rgba(255,255,255,0.1);
}

.nav-links a.router-link-active {
  background-color: rgba(255,255,255,0.2);
}

.cart-link {
  position: relative;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.cart-count {
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  position: absolute;
  top: -8px;
  right: -8px;
}

.main-content {
  min-height: calc(100vh - 140px);
  padding: 2rem 0;
}

.footer {
  background: #f8f9fa;
  padding: 2rem 0;
  text-align: center;
  border-top: 1px solid #e9ecef;
}

/* Responsive design */
@media (max-width: 768px) {
  .navbar .container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav-links {
    width: 100%;
    justify-content: center;
  }
  
  .container {
    padding: 0 15px;
  }
}

@media (max-width: 480px) {
  .nav-links {
    flex-wrap: wrap;
    gap: 1rem;
  }
  
  .nav-links a {
    padding: 0.5rem;
  }
}
</style>