<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-container">
        <div class="nav-brand">
          <h1>💰 Finance Tracker</h1>
        </div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">Dashboard</router-link>
          <router-link to="/transactions" class="nav-link">Transactions</router-link>
          <router-link to="/budgets" class="nav-link">Budgets</router-link>
          <router-link to="/goals" class="nav-link">Goals</router-link>
        </div>
        <div class="nav-actions">
          <div class="balance-display">
            <span class="balance-label">Current Balance:</span>
            <span class="balance-amount" :class="{ positive: totalBalance >= 0, negative: totalBalance < 0 }">
              {{ formatCurrency(totalBalance) }}
            </span>
          </div>
        </div>
      </div>
    </nav>

    <main class="main-content">
      <router-view />
    </main>

    <footer class="footer">
      <p>© 2024 Personal Finance Tracker. Track your money, achieve your goals.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Mock data for total balance calculation
const transactions = ref([
  { id: 1, amount: 2500, type: 'income', category: 'Salary', date: '2024-01-15' },
  { id: 2, amount: -120, type: 'expense', category: 'Groceries', date: '2024-01-16' },
  { id: 3, amount: -45, type: 'expense', category: 'Transportation', date: '2024-01-17' },
  { id: 4, amount: -200, type: 'expense', category: 'Entertainment', date: '2024-01-18' },
  { id: 5, amount: 500, type: 'income', category: 'Freelance', date: '2024-01-19' },
])

const totalBalance = computed(() => {
  return transactions.value.reduce((sum, transaction) => sum + transaction.amount, 0)
})

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
  }).format(amount)
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
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.nav-brand h1 {
  font-size: 1.5rem;
  font-weight: 600;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.3s ease;
}

.nav-link:hover {
  color: #e0e7ff;
}

.nav-link.router-link-active {
  color: #e0e7ff;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background-color: #e0e7ff;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.balance-display {
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem 1rem;
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.balance-label {
  font-size: 0.875rem;
  opacity: 0.9;
  margin-right: 0.5rem;
}

.balance-amount {
  font-weight: 600;
  font-size: 1.125rem;
}

.balance-amount.positive {
  color: #10b981;
}

.balance-amount.negative {
  color: #ef4444;
}

.main-content {
  flex: 1;
  max-width: 1200px;
  width: 100%;
  margin: 2rem auto;
  padding: 0 1rem;
}

.footer {
  background-color: #1f2937;
  color: #9ca3af;
  text-align: center;
  padding: 1.5rem;
  margin-top: auto;
}

.footer p {
  font-size: 0.875rem;
}

/* Responsive Design */
@media (max-width: 768px) {
  .nav-container {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }

  .nav-links {
    gap: 1rem;
  }

  .nav-brand h1 {
    font-size: 1.25rem;
  }

  .balance-display {
    padding: 0.5rem;
  }

  .balance-amount {
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .nav-links {
    flex-wrap: wrap;
    justify-content: center;
  }

  .nav-link {
    font-size: 0.875rem;
  }

  .main-content {
    margin: 1rem auto;
    padding: 0 0.5rem;
  }
}
</style>