<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h2>Financial Overview</h2>
      <p class="subtitle">Welcome back! Here's your financial summary for October 2024</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card income">
        <div class="stat-icon">📈</div>
        <div class="stat-content">
          <h3>Total Income</h3>
          <p class="stat-amount">${{ formatCurrency(totalIncome) }}</p>
          <p class="stat-change">+12% from last month</p>
        </div>
      </div>

      <div class="stat-card expenses">
        <div class="stat-icon">📉</div>
        <div class="stat-content">
          <h3>Total Expenses</h3>
          <p class="stat-amount">${{ formatCurrency(totalExpenses) }}</p>
          <p class="stat-change">-5% from last month</p>
        </div>
      </div>

      <div class="stat-card savings">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <h3>Monthly Savings</h3>
          <p class="stat-amount">${{ formatCurrency(monthlySavings) }}</p>
          <p class="stat-change">+18% from last month</p>
        </div>
      </div>

      <div class="stat-card goals">
        <div class="stat-icon">🎯</div>
        <div class="stat-content">
          <h3>Goals Progress</h3>
          <p class="stat-amount">{{ goalsProgress }}%</p>
          <p class="stat-change">3 of 5 goals on track</p>
        </div>
      </div>
    </div>

    <div class="content-grid">
      <div class="recent-transactions">
        <div class="section-header">
          <h3>Recent Transactions</h3>
          <router-link to="/transactions" class="view-all">View All →</router-link>
        </div>
        <div class="transactions-list">
          <div v-for="transaction in recentTransactions" :key="transaction.id" class="transaction-item">
            <div class="transaction-icon" :class="transaction.type">
              {{ transaction.type === 'income' ? '⬆️' : '⬇️' }}
            </div>
            <div class="transaction-details">
              <div class="transaction-title">{{ transaction.title }}</div>
              <div class="transaction-category">{{ transaction.category }}</div>
            </div>
            <div class="transaction-amount" :class="transaction.type">
              {{ transaction.type === 'income' ? '+' : '-' }}${{ formatCurrency(transaction.amount) }}
            </div>
          </div>
        </div>
      </div>

      <div class="budget-overview">
        <div class="section-header">
          <h3>Budget Overview</h3>
          <router-link to="/budgets" class="view-all">View All →</router-link>
        </div>
        <div class="budget-list">
          <div v-for="budget in budgets" :key="budget.id" class="budget-item">
            <div class="budget-header">
              <span class="budget-category">{{ budget.category }}</span>
              <span class="budget-progress">{{ budget.used }}/{{ budget.total }}</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: budget.percentage + '%' }"></div>
            </div>
            <div class="budget-footer">
              <span class="budget-remaining">${{ formatCurrency(budget.remaining) }} remaining</span>
              <span class="budget-percentage">{{ budget.percentage }}% used</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <h3>Quick Actions</h3>
      <div class="actions-grid">
        <button class="action-btn" @click="addTransaction">
          <span class="action-icon">➕</span>
          <span>Add Transaction</span>
        </button>
        <button class="action-btn" @click="viewReports">
          <span class="action-icon">📊</span>
          <span>View Reports</span>
        </button>
        <button class="action-btn" @click="setBudget">
          <span class="action-icon">🎯</span>
          <span>Set Budget</span>
        </button>
        <button class="action-btn" @click="addGoal">
          <span class="action-icon">⭐</span>
          <span>Add Goal</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Mock data
const totalIncome = ref(4850.00)
const totalExpenses = ref(3125.50)
const monthlySavings = computed(() => totalIncome.value - totalExpenses.value)
const goalsProgress = ref(65)

const recentTransactions = ref([
  { id: 1, title: 'Salary Deposit', category: 'Income', amount: 3200.00, type: 'income', date: '2024-10-15' },
  { id: 2, title: 'Grocery Shopping', category: 'Food', amount: 125.75, type: 'expense', date: '2024-10-14' },
  { id: 3, title: 'Netflix Subscription', category: 'Entertainment', amount: 15.99, type: 'expense', date: '2024-10-13' },
  { id: 4, title: 'Freelance Work', category: 'Income', amount: 850.00, type: 'income', date: '2024-10-12' },
  { id: 5, title: 'Gas Station', category: 'Transportation', amount: 45.50, type: 'expense', date: '2024-10-11' }
])

const budgets = ref([
  { id: 1, category: 'Food & Dining', total: 600, used: 425, remaining: 175, percentage: 71 },
  { id: 2, category: 'Entertainment', total: 200, used: 85, remaining: 115, percentage: 43 },
  { id: 3, category: 'Transportation', total: 300, used: 210, remaining: 90, percentage: 70 },
  { id: 4, category: 'Shopping', total: 400, used: 150, remaining: 250, percentage: 38 }
])

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const addTransaction = () => {
  router.push('/transactions')
}

const viewReports = () => {
  router.push('/reports')
}

const setBudget = () => {
  router.push('/budgets')
}

const addGoal = () => {
  router.push('/goals')
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.dashboard-header {
  text-align: center;
  margin-bottom: 1rem;
}

.dashboard-header h2 {
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #718096;
  font-size: 1rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  font-size: 2rem;
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(102, 126, 234, 0.1);
}

.income .stat-icon { background: rgba(72, 187, 120, 0.1); }
.expenses .stat-icon { background: rgba(245, 101, 101, 0.1); }
.savings .stat-icon { background: rgba(237, 137, 54, 0.1); }
.goals .stat-icon { background: rgba(159, 122, 234, 0.1); }

.stat-content h3 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.25rem;
  font-weight: 600;
}

.stat-amount {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.stat-change {
  font-size: 0.85rem;
  color: #48bb78;
}

.expenses .stat-change {
  color: #f56565;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h3 {
  font-size: 1.25rem;
  color: #2d3748;
}

.view-all {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
}

.view-all:hover {
  text-decoration: underline;
}

.recent-transactions,
.budget-overview {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 8px;
  background: #f7fafc;
  transition: background-color 0.3s;
}

.transaction-item:hover {
  background: #edf2f7;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.transaction-icon.income {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.transaction-icon.expense {
  background: rgba(245, 101, 101, 0.1);
  color: #e53e3e;
}

.transaction-details {
  flex: 1;
}

.transaction-title {
  font-weight: 600;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.transaction-category {
  font-size: 0.85rem;
  color: #718096;
}

.transaction-amount {
  font-weight: 700;
  font-size: 1.1rem;
}

.transaction-amount.income {
  color: #38a169;
}

.transaction-amount.expense {
  color: #e53e3e;
}

.budget-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.budget-item {
  padding: 1rem;
  border-radius: 8px;
  background: #f7fafc;
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.budget-category {
  font-weight: 600;
  color: #2d3748;
}

.budget-progress {
  font-size: 0.9rem;
  color: #718096;
}

.progress-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.75rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.3s;
}

.budget-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
}

.budget-remaining {
  color: #48bb78;
}

.budget-percentage {
  color: #718096;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.quick-actions h3 {
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.75rem;
  padding: 1.5rem;
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 500;
  color: #2d3748;
}

.action-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
  transform: translateY(-2px);
}

.action-icon {
  font-size: 2rem;
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .content-grid {
    grid-template-columns: 1fr;
  }
  
  .actions-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .actions-grid {
    grid-template-columns: 1fr;
  }
}
</style>