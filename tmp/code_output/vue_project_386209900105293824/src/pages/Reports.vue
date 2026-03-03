<template>
  <div class="reports">
    <div class="page-header">
      <h2>Financial Reports</h2>
      <div class="header-actions">
        <div class="date-range">
          <select v-model="selectedPeriod" class="period-select">
            <option value="monthly">This Month</option>
            <option value="quarterly">This Quarter</option>
            <option value="yearly">This Year</option>
            <option value="custom">Custom Range</option>
          </select>
          <div v-if="selectedPeriod === 'custom'" class="custom-dates">
            <input v-model="customStart" type="date" class="date-input" />
            <span class="date-separator">to</span>
            <input v-model="customEnd" type="date" class="date-input" />
          </div>
        </div>
        <button class="btn btn-primary" @click="generateReport">
          <span class="btn-icon">📊</span> Generate Report
        </button>
      </div>
    </div>

    <div class="reports-grid">
      <div class="report-card summary">
        <h3>Financial Summary</h3>
        <div class="summary-stats">
          <div class="summary-item">
            <span class="summary-label">Total Income</span>
            <span class="summary-value income">+${{ reportData.totalIncome.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Total Expenses</span>
            <span class="summary-value expense">-${{ reportData.totalExpenses.toFixed(2) }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Net Savings</span>
            <span class="summary-value" :class="reportData.netSavings >= 0 ? 'income' : 'expense'">
              {{ reportData.netSavings >= 0 ? '+' : '' }}${{ reportData.netSavings.toFixed(2) }}
            </span>
          </div>
          <div class="summary-item">
            <span class="summary-label">Savings Rate</span>
            <span class="summary-value">{{ reportData.savingsRate }}%</span>
          </div>
        </div>
      </div>

      <div class="report-card spending">
        <h3>Spending by Category</h3>
        <div class="spending-chart">
          <div class="chart-container">
            <div class="chart-bars">
              <div 
                v-for="category in reportData.spendingByCategory" 
                :key="category.name"
                class="chart-bar-container"
              >
                <div class="chart-bar-wrapper">
                  <div 
                    class="chart-bar" 
                    :style="{ height: category.percentage + '%' }"
                    :title="`${category.name}: $${category.amount.toFixed(2)} (${category.percentage}%)`"
                  ></div>
                </div>
                <div class="chart-label">{{ category.name }}</div>
                <div class="chart-amount">${{ category.amount.toFixed(2) }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="report-card trends">
        <h3>Income vs Expenses Trend</h3>
        <div class="trend-chart">
          <div class="chart-lines">
            <div class="chart-line income-line" :style="{ height: '100%' }">
              <div class="line-label">Income</div>
              <div class="line-points">
                <div 
                  v-for="(point, index) in reportData.incomeTrend" 
                  :key="'income-' + index"
                  class="line-point"
                  :style="{ left: (index * 20) + '%', bottom: point + '%' }"
                ></div>
              </div>
            </div>
            <div class="chart-line expense-line" :style="{ height: '100%' }">
              <div class="line-label">Expenses</div>
              <div class="line-points">
                <div 
                  v-for="(point, index) in reportData.expenseTrend" 
                  :key="'expense-' + index"
                  class="line-point"
                  :style="{ left: (index * 20) + '%', bottom: point + '%' }"
                ></div>
              </div>
            </div>
          </div>
          <div class="chart-months">
            <span v-for="month in ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun']" :key="month" class="month-label">
              {{ month }}
            </span>
          </div>
        </div>
      </div>

      <div class="report-card insights">
        <h3>Key Insights</h3>
        <div class="insights-list">
          <div v-for="insight in reportData.insights" :key="insight.id" class="insight-item">
            <div class="insight-icon" :class="insight.type">
              {{ insight.type === 'positive' ? '✅' : insight.type === 'warning' ? '⚠️' : '💡' }}
            </div>
            <div class="insight-content">
              <h4>{{ insight.title }}</h4>
              <p>{{ insight.description }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="report-card top-expenses">
        <h3>Top Expenses</h3>
        <div class="expenses-list">
          <div v-for="expense in reportData.topExpenses" :key="expense.id" class="expense-item">
            <div class="expense-category">
              <div class="category-dot" :style="{ backgroundColor: getCategoryColor(expense.category) }"></div>
              <span>{{ expense.category }}</span>
            </div>
            <div class="expense-amount">${{ expense.amount.toFixed(2) }}</div>
            <div class="expense-percentage">{{ expense.percentage }}%</div>
          </div>
        </div>
      </div>

      <div class="report-card goals-progress">
        <h3>Goals Progress Report</h3>
        <div class="goals-list">
          <div v-for="goal in reportData.goalsProgress" :key="goal.id" class="goal-item">
            <div class="goal-info">
              <h4>{{ goal.name }}</h4>
              <div class="goal-progress-bar">
                <div 
                  class="goal-progress-fill" 
                  :style="{ width: goal.progress + '%' }"
                  :class="getGoalProgressClass(goal.progress)"
                ></div>
              </div>
            </div>
            <div class="goal-stats">
              <span class="goal-current">${{ goal.current.toLocaleString() }}</span>
              <span class="goal-target">/ ${{ goal.target.toLocaleString() }}</span>
              <span class="goal-percent">{{ goal.progress }}%</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="report-actions">
      <button class="btn btn-secondary" @click="exportToPDF">
        <span class="btn-icon">📄</span> Export as PDF
      </button>
      <button class="btn btn-secondary" @click="exportToCSV">
        <span class="btn-icon">📊</span> Export as CSV
      </button>
      <button class="btn btn-primary" @click="printReport">
        <span class="btn-icon">🖨️</span> Print Report
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const selectedPeriod = ref('monthly')
const customStart = ref('')
const customEnd = ref('')

const reportData = ref({
  totalIncome: 4850.00,
  totalExpenses: 3210.50,
  netSavings: 1639.50,
  savingsRate: 33.8,
  spendingByCategory: [
    { name: 'Housing', amount: 1200, percentage: 37 },
    { name: 'Food & Dining', amount: 650, percentage: 20 },
    { name: 'Transportation', amount: 450, percentage: 14 },
    { name: 'Entertainment', amount: 320, percentage: 10 },
    { name: 'Shopping', amount: 280, percentage: 9 },
    { name: 'Utilities', amount: 210, percentage: 7 },
    { name: 'Other', amount: 100.50, percentage: 3 }
  ],
  incomeTrend: [65, 70, 75, 80, 85, 90],
  expenseTrend: [60, 65, 70, 75, 80, 85],
  insights: [
    { id: 1, type: 'positive', title: 'Savings Rate Improved', description: 'Your savings rate increased by 5% compared to last month.' },
    { id: 2, type: 'warning', title: 'High Dining Expenses', description: 'Dining expenses are 15% above your budget target.' },
    { id: 3, type: 'info', title: 'Transportation Savings', description: 'You saved $45 on transportation compared to last month.' },
    { id: 4, type: 'positive', title: 'Income Growth', description: 'Your income increased by 12% this month.' }
  ],
  topExpenses: [
    { id: 1, category: 'Rent', amount: 1200, percentage: 37 },
    { id: 2, category: 'Groceries', amount: 450, percentage: 14 },
    { id: 3, category: 'Car Payment', amount: 350, percentage: 11 },
    { id: 4, category: 'Dining Out', amount: 200, percentage: 6 },
    { id: 5, category: 'Entertainment', amount: 180, percentage: 6 }
  ],
  goalsProgress: [
    { id: 1, name: 'Emergency Fund', target: 10000, current: 7500, progress: 75 },
    { id: 2, name: 'New Car', target: 25000, current: 12000, progress: 48 },
    { id: 3, name: 'Vacation', target: 5000, current: 3200, progress: 64 },
    { id: 4, name: 'Home Renovation', target: 15000, current: 4500, progress: 30 }
  ]
})

const getCategoryColor = (category) => {
  const colors = {
    'Housing': '#667eea',
    'Food & Dining': '#ed8936',
    'Transportation': '#4299e1',
    'Entertainment': '#d53f8c',
    'Shopping': '#d69e2e',
    'Utilities': '#38a169',
    'Other': '#718096',
    'Rent': '#667eea',
    'Groceries': '#ed8936',
    'Car Payment': '#4299e1',
    'Dining Out': '#e53e3e'
  }
  return colors[category] || '#718096'
}

const getGoalProgressClass = (progress) => {
  if (progress >= 75) return 'excellent'
  if (progress >= 50) return 'good'
  if (progress >= 25) return 'fair'
  return 'poor'
}

const generateReport = () => {
  // In a real app, this would fetch data based on selected period
  console.log('Generating report for period:', selectedPeriod.value)
  
  // Simulate data update
  reportData.value = {
    ...reportData.value,
    totalIncome: Math.random() * 1000 + 4500,
    totalExpenses: Math.random() * 500 + 3000,
    netSavings: Math.random() * 500 + 1500,
    savingsRate: Math.round(Math.random() * 10 + 30)
  }
}

const exportToPDF = () => {
  alert('PDF export functionality would be implemented here')
  // In a real app, this would generate and download a PDF report
}

const exportToCSV = () => {
  alert('CSV export functionality would be implemented here')
  // In a real app, this would generate and download a CSV file
}

const printReport = () => {
  window.print()
}

onMounted(() => {
  // Set default custom dates to current month
  const now = new Date()
  const firstDay = new Date(now.getFullYear(), now.getMonth(), 1)
  const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0)
  
  customStart.value = firstDay.toISOString().split('T')[0]
  customEnd.value = lastDay.toISOString().split('T')[0]
})
</script>

<style scoped>
.reports {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-header h2 {
  font-size: 2rem;
  color: #2d3748;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.period-select {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 0.9rem;
  min-width: 150px;
}

.period-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.custom-dates {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.date-input {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.9rem;
}

.date-separator {
  color: #718096;
  font-size: 0.9rem;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

.btn-icon {
  font-size: 1.2rem;
}

.reports-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 1.5rem;
}

.report-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.report-card h3 {
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.summary-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.summary-label {
  font-size: 0.9rem;
  color: #718096;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.summary-value.income {
  color: #38a169;
}

.summary-value.expense {
  color: #e53e3e;
}

.spending-chart {
  height: 250px;
}

.chart-container {
  height: 100%;
  display: flex;
  align-items: flex-end;
  padding: 1rem 0;
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
  flex: 1;
}

.chart-bar-wrapper {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.chart-bar {
  width: 30px;
  background: linear-gradient(to top, #667eea, #764ba2);
  border-radius: 4px 4px 0 0;
  transition: height 0.3s ease;
  cursor: pointer;
}

.chart-bar:hover {
  opacity: 0.8;
}

.chart-label {
  font-size: 0.8rem;
  color: #718096;
  text-align: center;
  word-break: break-word;
  max-width: 60px;
}

.chart-amount {
  font-size: 0.75rem;
  color: #4a5568;
  font-weight: 500;
}

.trend-chart {
  height: 200px;
  position: relative;
  padding-top: 2rem;
}

.chart-lines {
  height: calc(100% - 2rem);
  position: relative;
}

.chart-line {
  position: absolute;
  width: 100%;
}

.line-label {
  position: absolute;
  top: -1.5rem;
  font-size: 0.85rem;
  font-weight: 500;
}

.income-line .line-label {
  color: #38a169;
  left: 0;
}

.expense-line .line-label {
  color: #e53e3e;
  right: 0;
}

.line-points {
  position: relative;
  height: 100%;
}

.line-point {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  transform: translate(-50%, 50%);
}

.income-line .line-point {
  background: #38a169;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #38a169;
}

.expense-line .line-point {
  background: #e53e3e;
  border: 2px solid white;
  box-shadow: 0 0 0 2px #e53e3e;
}

.chart-months {
  display: flex;
  justify-content: space-between;
  padding-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
}

.month-label {
  font-size: 0.8rem;
  color: #718096;
  flex: 1;
  text-align: center;
}

.insights-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.insight-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
  transition: background-color 0.3s ease;
}

.insight-item:hover {
  background: #edf2f7;
}

.insight-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.insight-icon.positive {
  color: #38a169;
}

.insight-icon.warning {
  color: #ed8936;
}

.insight-icon.info {
  color: #4299e1;
}

.insight-content {
  flex: 1;
}

.insight-content h4 {
  font-size: 1rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.insight-content p {
  font-size: 0.9rem;
  color: #718096;
  line-height: 1.4;
}

.expenses-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.expense-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.75rem;
  background: #f7fafc;
  border-radius: 6px;
}

.expense-category {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  flex: 1;
  font-weight: 500;
  color: #2d3748;
}

.category-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.expense-amount {
  font-weight: 600;
  color: #e53e3e;
  min-width: 80px;
  text-align: right;
}

.expense-percentage {
  font-size: 0.85rem;
  color: #718096;
  min-width: 40px;
  text-align: right;
}

.goals-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.goal-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.goal-info {
  flex: 1;
}

.goal-info h4 {
  font-size: 1rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.goal-progress-bar {
  height: 6px;
  background: #e2e8f0;
  border-radius: 3px;
  overflow: hidden;
}

.goal-progress-fill {
  height: 100%;
  border-radius: 3px;
  transition: width 0.3s ease;
}

.goal-progress-fill.excellent {
  background: linear-gradient(to right, #48bb78, #38a169);
}

.goal-progress-fill.good {
  background: linear-gradient(to right, #4299e1, #3182ce);
}

.goal-progress-fill.fair {
  background: linear-gradient(to right, #ed8936, #dd6b20);
}

.goal-progress-fill.poor {
  background: linear-gradient(to right, #f56565, #e53e3e);
}

.goal-stats {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.goal-current {
  font-weight: 600;
  color: #2d3748;
}

.goal-target {
  color: #718096;
}

.goal-percent {
  font-weight: 600;
  color: #667eea;
  min-width: 40px;
  text-align: right;
}

.report-actions {
  display: flex;
  justify-content: center;
  gap: 1rem;
  padding-top: 2rem;
  border-top: 1px solid #e2e8f0;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .date-range {
    flex-direction: column;
    align-items: stretch;
  }
  
  .custom-dates {
    flex-direction: column;
  }
  
  .reports-grid {
    grid-template-columns: 1fr;
  }
  
  .summary-stats {
    grid-template-columns: 1fr;
  }
  
  .report-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .chart-bar {
    width: 20px;
  }
  
  .chart-label {
    font-size: 0.7rem;
    max-width: 40px;
  }
  
  .goal-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .goal-stats {
    justify-content: space-between;
  }
}
</style>