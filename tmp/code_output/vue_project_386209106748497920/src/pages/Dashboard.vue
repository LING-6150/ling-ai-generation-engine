<template>
  <div class="dashboard">
    <div class="welcome-section">
      <h1>Welcome back, John!</h1>
      <p class="subtitle">Here's your financial overview for today</p>
    </div>
    
    <div class="summary-cards">
      <div class="summary-card">
        <div class="card-header">
          <span class="card-icon">💰</span>
          <h3>Total Balance</h3>
        </div>
        <p class="card-value">{{ formatCurrency(totalBalance) }}</p>
        <p class="card-change positive">+12.5% from last month</p>
      </div>
      
      <div class="summary-card">
        <div class="card-header">
          <span class="card-icon">📈</span>
          <h3>Monthly Income</h3>
        </div>
        <p class="card-value">{{ formatCurrency(monthlyIncome) }}</p>
        <p class="card-change positive">+8.2% from last month</p>
      </div>
      
      <div class="summary-card">
        <div class="card-header">
          <span class="card-icon">📉</span>
          <h3>Monthly Expenses</h3>
        </div>
        <p class="card-value">{{ formatCurrency(monthlyExpenses) }}</p>
        <p class="card-change negative">-3.1% from last month</p>
      </div>
      
      <div class="summary-card">
        <div class="card-header">
          <span class="card-icon">🎯</span>
          <h3>Goals Progress</h3>
        </div>
        <p class="card-value">{{ goalsProgress }}%</p>
        <p class="card-change positive">{{ completedGoals }} goals completed</p>
      </div>
    </div>
    
    <div class="dashboard-grid">
      <div class="chart-section">
        <div class="section-header">
          <h2>Spending Overview</h2>
          <select v-model="chartPeriod" class="period-select">
            <option value="monthly">This Month</option>
            <option value="quarterly">Last 3 Months</option>
            <option value="yearly">This Year</option>
          </select>
        </div>
        <div class="chart-container">
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div v-for="(bar, index) in chartData" :key="index" class="chart-bar">
                <div class="bar-income" :style="{ height: bar.income + '%' }"></div>
                <div class="bar-expense" :style="{ height: bar.expense + '%' }"></div>
                <span class="bar-label">{{ bar.label }}</span>
              </div>
            </div>
            <div class="chart-legend">
              <div class="legend-item">
                <span class="legend-color income"></span>
                <span>Income</span>
              </div>
              <div class="legend-item">
                <span class="legend-color expense"></span>
                <span>Expenses</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="recent-transactions">
        <div class="section-header">
          <h2>Recent Transactions</h2>
          <router-link to="/transactions" class="view-all">View All →</router-link>
        </div>
        <div class="transactions-list">
          <div v-for="transaction in recentTransactions" :key="transaction.id" class="transaction-item">
            <div class="transaction-icon" :style="{ backgroundColor: transaction.color }">
              {{ transaction.icon }}
            </div>
            <div class="transaction-details">
              <h4>{{ transaction.description }}</h4>
              <p class="transaction-category">{{ transaction.category }}</p>
            </div>
            <div class="transaction-amount" :class="transaction.type">
              {{ transaction.type === 'income' ? '+' : '-' }}{{ formatCurrency(transaction.amount) }}
            </div>
          </div>
        </div>
      </div>
      
      <div class="budget-overview">
        <div class="section-header">
          <h2>Budget Status</h2>
          <router-link to="/budgets" class="view-all">View All →</router-link>
        </div>
        <div class="budgets-list">
          <div v-for="budget in budgetStatus" :key="budget.id" class="budget-item">
            <div class="budget-info">
              <h4>{{ budget.category }}</h4>
              <p class="budget-amount">{{ formatCurrency(budget.spent) }} / {{ formatCurrency(budget.total) }}</p>
            </div>
            <div class="budget-progress">
              <div class="progress-bar">
                <div 
                  class="progress-fill" 
                  :style="{ 
                    width: Math.min(budget.percentage, 100) + '%',
                    backgroundColor: budget.color
                  }"
                ></div>
              </div>
              <span class="progress-percentage">{{ budget.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="goals-overview">
        <div class="section-header">
          <h2>Active Goals</h2>
          <router-link to="/goals" class="view-all">View All →</router-link>
        </div>
        <div class="goals-list">
          <div v-for="goal in activeGoals" :key="goal.id" class="goal-item">
            <div class="goal-icon" :style="{ backgroundColor: goal.color }">
              {{ goal.icon }}
            </div>
            <div class="goal-details">
              <h4>{{ goal.name }}</h4>
              <p class="goal-target">{{ formatCurrency(goal.current) }} / {{ formatCurrency(goal.target) }}</p>
              <div class="goal-progress">
                <div class="progress-bar">
                  <div 
                    class="progress-fill" 
                    :style="{ 
                      width: Math.min(goal.progress, 100) + '%',
                      backgroundColor: goal.color
                    }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="quick-actions">
      <h2>Quick Actions</h2>
      <div class="actions-grid">
        <button class="action-btn" @click="addTransaction">
          <span class="action-icon">➕</span>
          <span class="action-text">Add Transaction</span>
        </button>
        <button class="action-btn" @click="createBudget">
          <span class="action-icon">📋</span>
          <span class="action-text">Create Budget</span>
        </button>
        <button class="action-btn" @click="setGoal">
          <span class="action-icon">🎯</span>
          <span class="action-text">Set New Goal</span>
        </button>
        <button class="action-btn" @click="generateReport">
          <span class="action-icon">📊</span>
          <span class="action-text">Generate Report</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const chartPeriod = ref('monthly')

const totalBalance = ref(12543.67)
const monthlyIncome = ref(3425.50)
const monthlyExpenses = ref(2189.75)
const goalsProgress = ref(65)
const completedGoals = ref(2)

const chartData = ref([
  { label: 'Jan', income: 80, expense: 60 },
  { label: 'Feb', income: 75, expense: 55 },
  { label: 'Mar', income: 90, expense: 70 },
  { label: 'Apr', income: 85, expense: 65 },
  { label: 'May', income: 95, expense: 75 },
  { label: 'Jun', income: 100, expense: 80 }
])

const recentTransactions = ref([
  { id: 1, description: 'Grocery Shopping', amount: 85.50, type: 'expense', category: 'Food', icon: '🛒', color: '#764ba2' },
  { id: 2, description: 'Monthly Salary', amount: 2425.00, type: 'income', category: 'Salary', icon: '💰', color: '#38a169' },
  { id: 3, description: 'Electric Bill', amount: 120.75, type: 'expense', category: 'Utilities', icon: '⚡', color: '#667eea' },
  { id: 4, description: 'Restaurant Dinner', amount: 68.90, type: 'expense', category: 'Food', icon: '🍽️', color: '#764ba2' },
  { id: 5, description: 'Freelance Work', amount: 850.00, type: 'income', category: 'Salary', icon: '💼', color: '#38a169' }
])

const budgetStatus = ref([
  { id: 1, category: 'Food', spent: 450, total: 600, percentage: 75, color: '#764ba2' },
  { id: 2, category: 'Transport', spent: 210, total: 300, percentage: 70, color: '#f093fb' },
  { id: 3, category: 'Entertainment', spent: 180, total: 200, percentage: 90, color: '#f5576c' },
  { id: 4, category: 'Shopping', spent: 120, total: 250, percentage: 48, color: '#4facfe' }
])

const activeGoals = ref([
  { id: 1, name: 'Emergency Fund', current: 7500, target: 10000, progress: 75, icon: '🛡️', color: '#667eea' },
  { id: 2, name: 'Europe Vacation', current: 3200, target: 5000, progress: 64, icon: '✈️', color: '#f5576c' },
  { id: 3, name: 'New Laptop', current: 1200, target: 2000, progress: 60, icon: '💻', color: '#4facfe' }
])

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
  })
}

const addTransaction = () => {
  router.push('/transactions')
}

const createBudget = () => {
  router.push('/budgets')
}

const setGoal = () => {
  router.push('/goals')
}

const generateReport = () => {
  alert('Report generation feature coming soon!')
}
</script>

<style scoped>
.dashboard {
  padding: 1rem;
}

.welcome-section {
  margin-bottom: 2rem;
}

.welcome-section h1 {
  font-size: 2.5rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.subtitle {
  font-size: 1.1rem;
  color: #718096;
}

.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.card-icon {
  font-size: 1.5rem;
}

.card-header h3 {
  font-size: 1rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.card-value {
  font-size: 2rem;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.card-change {
  font-size: 0.9rem;
  font-weight: 500;
}

.card-change.positive {
  color: #38a169;
}

.card-change.negative {
  color: #e53e3e;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.chart-section,
.recent-transactions,
.budget-overview,
.goals-overview {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  font-size: 1.25rem;
  color: #2d3748;
}

.view-all {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.9rem;
  transition: color 0.2s;
}

.view-all:hover {
  color: #764ba2;
  text-decoration: underline;
}

.period-select {
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 0.9rem;
}

.chart-container {
  height: 250px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chart-bars {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  padding: 1rem 0;
  border-bottom: 1px solid #e2e8f0;
}

.chart-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  width: 40px;
}

.bar-income {
  width: 12px;
  background: #38a169;
  border-radius: 4px 4px 0 0;
  margin-bottom: 2px;
}

.bar-expense {
  width: 12px;
  background: #e53e3e;
  border-radius: 0 0 4px 4px;
}

.bar-label {
  margin-top: 0.5rem;
  font-size: 0.85rem;
  color: #718096;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
  color: #4a5568;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.income {
  background: #38a169;
}

.legend-color.expense {
  background: #e53e3e;
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
  transition: background-color 0.2s;
}

.transaction-item:hover {
  background: #f7fafc;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1.2rem;
  color: white;
  flex-shrink: 0;
}

.transaction-details {
  flex: 1;
}

.transaction-details h4 {
  font-size: 1rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.transaction-category {
  font-size: 0.85rem;
  color: #718096;
}

.transaction-amount {
  font-weight: 600;
  font-size: 1rem;
}

.transaction-amount.income {
  color: #38a169;
}

.transaction-amount.expense {
  color: #e53e3e;
}

.budgets-list,
.goals-list {
  display: flex;
  flex-direction: column;
  gap: 1.25rem;
}

.budget-item,
.goal-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.budget-info,
.goal-details {
  flex: 1;
}

.budget-info h4,
.goal-details h4 {
  font-size: 1rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.budget-amount,
.goal-target {
  font-size: 0.85rem;
  color: #718096;
}

.budget-progress {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 150px;
}

.progress-bar {
  flex: 1;
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.progress-percentage {
  font-size: 0.85rem;
  font-weight: 600;
  color: #2d3748;
  min-width: 40px;
  text-align: right;
}

.goal-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1.5rem;
  color: white;
  flex-shrink: 0;
}

.goal-progress {
  margin-top: 0.5rem;
}

.quick-actions {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.quick-actions h2 {
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
  justify-content: center;
  gap: 0.75rem;
  padding: 1.5rem;
  background: #f7fafc;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: white;
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.action-icon {
  font-size: 2rem;
}

.action-text {
  font-weight: 600;
  color: #2d3748;
  text-align: center;
}

/* Responsive design */
@media (max-width: 768px) {
  .welcome-section h1 {
    font-size: 2rem;
  }
  
  .summary-cards {
    grid-template-columns: 1fr;
  }
  
  .dashboard-grid {
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
  
  .budget-progress {
    width: 120px;
  }
  
  .chart-bars {
    gap: 0.5rem;
  }
  
  .chart-bar {
    width: 30px;
  }
}
</style>