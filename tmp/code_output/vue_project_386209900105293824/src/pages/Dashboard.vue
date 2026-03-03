<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h2>Financial Dashboard</h2>
      <p class="subtitle">Overview of your financial health</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card income">
        <div class="stat-icon">💰</div>
        <div class="stat-content">
          <h3>Total Income</h3>
          <p class="stat-amount">$4,850.00</p>
          <p class="stat-change">+12% from last month</p>
        </div>
      </div>

      <div class="stat-card expenses">
        <div class="stat-icon">💸</div>
        <div class="stat-content">
          <h3>Total Expenses</h3>
          <p class="stat-amount">$3,210.50</p>
          <p class="stat-change">-5% from last month</p>
        </div>
      </div>

      <div class="stat-card savings">
        <div class="stat-icon">🏦</div>
        <div class="stat-content">
          <h3>Monthly Savings</h3>
          <p class="stat-amount">$1,639.50</p>
          <p class="stat-change">+28% from last month</p>
        </div>
      </div>

      <div class="stat-card budget">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <h3>Budget Usage</h3>
          <p class="stat-amount">78%</p>
          <p class="stat-change">On track</p>
        </div>
      </div>
    </div>

    <div class="dashboard-content">
      <div class="chart-section">
        <h3>Monthly Spending Trend</h3>
        <div class="chart-container">
          <div class="chart-placeholder">
            <div class="chart-bars">
              <div v-for="(month, index) in spendingData" :key="month.name" class="chart-bar-container">
                <div class="chart-bar" :style="{ height: month.value + '%' }"></div>
                <span class="chart-label">{{ month.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="recent-transactions">
        <h3>Recent Transactions</h3>
        <div class="transactions-list">
          <div v-for="transaction in recentTransactions" :key="transaction.id" class="transaction-item">
            <div class="transaction-icon" :class="transaction.type">
              {{ transaction.type === 'income' ? '💰' : '💸' }}
            </div>
            <div class="transaction-details">
              <h4>{{ transaction.description }}</h4>
              <p class="transaction-category">{{ transaction.category }}</p>
            </div>
            <div class="transaction-amount" :class="transaction.type">
              {{ transaction.type === 'income' ? '+' : '-' }}${{ transaction.amount.toFixed(2) }}
            </div>
          </div>
        </div>
        <router-link to="/transactions" class="view-all-link">View All Transactions →</router-link>
      </div>
    </div>

    <div class="goals-section">
      <h3>Financial Goals Progress</h3>
      <div class="goals-grid">
        <div v-for="goal in financialGoals" :key="goal.id" class="goal-card">
          <div class="goal-header">
            <h4>{{ goal.name }}</h4>
            <span class="goal-target">${{ goal.target.toLocaleString() }}</span>
          </div>
          <div class="goal-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: goal.progress + '%' }"></div>
            </div>
            <div class="goal-stats">
              <span class="current-amount">${{ goal.current.toLocaleString() }}</span>
              <span class="progress-percent">{{ goal.progress }}%</span>
            </div>
          </div>
          <p class="goal-description">{{ goal.description }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const spendingData = ref([
  { name: 'Jan', value: 65 },
  { name: 'Feb', value: 80 },
  { name: 'Mar', value: 75 },
  { name: 'Apr', value: 90 },
  { name: 'May', value: 85 },
  { name: 'Jun', value: 70 }
])

const recentTransactions = ref([
  { id: 1, description: 'Salary Deposit', category: 'Income', amount: 2500, type: 'income', date: '2024-06-15' },
  { id: 2, description: 'Grocery Shopping', category: 'Food', amount: 125.75, type: 'expense', date: '2024-06-14' },
  { id: 3, description: 'Electricity Bill', category: 'Utilities', amount: 89.50, type: 'expense', date: '2024-06-13' },
  { id: 4, description: 'Freelance Work', category: 'Income', amount: 850, type: 'income', date: '2024-06-12' },
  { id: 5, description: 'Restaurant Dinner', category: 'Dining', amount: 65.25, type: 'expense', date: '2024-06-11' }
])

const financialGoals = ref([
  { id: 1, name: 'Emergency Fund', target: 10000, current: 7500, progress: 75, description: '6 months of living expenses' },
  { id: 2, name: 'New Car', target: 25000, current: 12000, progress: 48, description: 'Down payment for a new vehicle' },
  { id: 3, name: 'Vacation Fund', target: 5000, current: 3200, progress: 64, description: 'Trip to Europe next summer' },
  { id: 4, name: 'Home Renovation', target: 15000, current: 4500, progress: 30, description: 'Kitchen and bathroom updates' }
])
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.dashboard-header {
  margin-bottom: 1rem;
}

.dashboard-header h2 {
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.subtitle {
  color: #718096;
  font-size: 1.1rem;
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
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
.savings .stat-icon { background: rgba(56, 178, 172, 0.1); }
.budget .stat-icon { background: rgba(159, 122, 234, 0.1); }

.stat-content h3 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.stat-amount {
  font-size: 1.8rem;
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

.dashboard-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.chart-section,
.recent-transactions {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.chart-section h3,
.recent-transactions h3 {
  margin-bottom: 1.5rem;
  color: #2d3748;
}

.chart-container {
  height: 300px;
}

.chart-placeholder {
  height: 100%;
  display: flex;
  align-items: flex-end;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  width: 100%;
  height: 100%;
}

.chart-bar-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  justify-content: flex-end;
  gap: 0.5rem;
}

.chart-bar {
  width: 40px;
  background: linear-gradient(to top, #667eea, #764ba2);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
}

.chart-label {
  font-size: 0.85rem;
  color: #718096;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.transaction-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.transaction-item:hover {
  background: #edf2f7;
}

.transaction-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
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
  font-size: 1.1rem;
}

.transaction-amount.income {
  color: #38a169;
}

.transaction-amount.expense {
  color: #e53e3e;
}

.view-all-link {
  display: inline-block;
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.view-all-link:hover {
  color: #5a67d8;
  text-decoration: underline;
}

.goals-section {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.goals-section h3 {
  margin-bottom: 1.5rem;
  color: #2d3748;
}

.goals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1.5rem;
}

.goal-card {
  padding: 1.5rem;
  background: #f7fafc;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.goal-header h4 {
  font-size: 1.1rem;
  color: #2d3748;
}

.goal-target {
  font-weight: 600;
  color: #667eea;
}

.goal-progress {
  margin-bottom: 1rem;
}

.progress-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.goal-stats {
  display: flex;
  justify-content: space-between;
  font-size: 0.9rem;
}

.current-amount {
  color: #2d3748;
  font-weight: 500;
}

.progress-percent {
  color: #48bb78;
  font-weight: 600;
}

.goal-description {
  font-size: 0.9rem;
  color: #718096;
  line-height: 1.4;
}

@media (max-width: 1024px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goals-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-bar {
    width: 30px;
  }
}
</style>