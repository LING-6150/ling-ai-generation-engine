<template>
  <div class="app">
    <nav class="navbar">
      <div class="navbar-container">
        <div class="navbar-brand">
          <div class="brand-logo">💰</div>
          <h1 class="brand-title">Finance Tracker</h1>
        </div>
        
        <div class="navbar-menu">
          <router-link 
            v-for="item in navItems" 
            :key="item.path"
            :to="item.path"
            class="nav-link"
            :class="{ active: $route.path === item.path }"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-text">{{ item.label }}</span>
          </router-link>
        </div>
        
        <div class="navbar-user">
          <div class="user-avatar">👤</div>
          <div class="user-info">
            <span class="user-name">John Doe</span>
            <span class="user-email">john@example.com</span>
          </div>
        </div>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="footer">
      <div class="footer-content">
        <p>© 2024 Finance Tracker. All rights reserved.</p>
        <p>Track your finances, achieve your goals.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const navItems = ref([
  { path: '/', label: 'Dashboard', icon: '📊' },
  { path: '/transactions', label: 'Transactions', icon: '💳' },
  { path: '/budgets', label: 'Budgets', icon: '📋' },
  { path: '/goals', label: 'Goals', icon: '🎯' },
  { path: '/reports', label: 'Reports', icon: '📈' }
])
</script>

<style scoped>
.app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 0;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.brand-logo {
  font-size: 2rem;
}

.brand-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin: 0;
}

.navbar-menu {
  display: flex;
  gap: 1rem;
  flex: 1;
  justify-content: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.active {
  background: rgba(255, 255, 255, 0.2);
  color: white;
}

.nav-icon {
  font-size: 1.2rem;
}

.nav-text {
  font-size: 1rem;
}

.navbar-user {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  font-size: 0.9rem;
}

.user-email {
  font-size: 0.8rem;
  opacity: 0.8;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.footer {
  background: #f7fafc;
  padding: 2rem 1rem;
  border-top: 1px solid #e2e8f0;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
  color: #718096;
}

.footer-content p {
  margin: 0.5rem 0;
}

@media (max-width: 768px) {
  .navbar-container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .navbar-menu {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .nav-link {
    padding: 0.5rem 1rem;
  }
  
  .nav-text {
    display: none;
  }
  
  .navbar-user {
    flex-direction: column;
    text-align: center;
  }
  
  .user-info {
    display: none;
  }
}

@media (max-width: 480px) {
  .navbar-menu {
    gap: 0.5rem;
  }
  
  .nav-link {
    padding: 0.5rem;
  }
  
  .brand-title {
    font-size: 1.2rem;
  }
}
</style>