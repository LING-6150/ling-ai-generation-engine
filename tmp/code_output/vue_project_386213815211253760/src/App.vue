<template>
  <div id="app">
    <header class="app-header">
      <div class="container">
        <div class="header-content">
          <h1 class="logo">💰 Finance Tracker</h1>
          <nav class="main-nav">
            <router-link to="/" class="nav-link">Dashboard</router-link>
            <router-link to="/transactions" class="nav-link">Transactions</router-link>
            <router-link to="/budgets" class="nav-link">Budgets</router-link>
            <router-link to="/goals" class="nav-link">Goals</router-link>
            <router-link to="/reports" class="nav-link">Reports</router-link>
          </nav>
          <div class="user-info">
            <span class="balance">${{ formatCurrency(totalBalance) }}</span>
            <div class="user-avatar">👤</div>
          </div>
        </div>
      </div>
    </header>

    <main class="app-main">
      <div class="container">
        <router-view />
      </div>
    </main>

    <footer class="app-footer">
      <div class="container">
        <p>© 2024 Personal Finance Tracker. Track your money, achieve your goals.</p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Mock data for total balance
const totalBalance = ref(12543.67)

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}
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
  background-color: #f5f7fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
}

.main-nav {
  display: flex;
  gap: 2rem;
  flex-wrap: wrap;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 0;
  transition: color 0.3s;
  position: relative;
}

.nav-link:hover {
  color: white;
}

.nav-link.router-link-active {
  color: white;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: white;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.balance {
  font-size: 1.2rem;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.app-main {
  min-height: calc(100vh - 140px);
  padding: 2rem 0;
}

.app-footer {
  background: #2d3748;
  color: #a0aec0;
  text-align: center;
  padding: 1.5rem 0;
  font-size: 0.9rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    text-align: center;
  }
  
  .main-nav {
    justify-content: center;
    gap: 1rem;
  }
  
  .user-info {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .main-nav {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .nav-link {
    padding: 0.25rem 0;
  }
}
</style>