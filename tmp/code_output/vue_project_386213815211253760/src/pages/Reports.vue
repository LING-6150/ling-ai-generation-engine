<template>
  <div class="reports">
    <div class="page-header">
      <h2>Financial Reports</h2>
      <div class="header-actions">
        <div class="filter-controls">
          <select v-model="selectedPeriod" class="period-select">
            <option value="monthly">This Month</option>
            <option value="quarterly">This Quarter</option>
            <option value="yearly">This Year</option>
            <option value="custom">Custom Range</option>
          </select>
          <button class="export-btn" @click="exportReport">
            <span>📊 Export Report</span>
          </button>
        </div>
      </div>
    </div>

    <div class="reports-summary">
      <div class="summary-card">
        <h3>Income vs Expenses</h3>
        <div class="comparison-chart">
          <div class="comparison-bar">
            <div class="bar-label">Income</div>
            <div class="bar-container">
              <div 
                class="bar income" 
                :style="{ width: incomePercentage + '%' }"
              >
                <span class="bar-value">${{ formatCurrency(totalIncome) }}</span>
              </div>
            </div>
          </div>
          <div class="comparison-bar">
            <div class="bar-label">Expenses</div>
            <div class="bar-container">
              <div 
                class="bar expense" 
                :style="{ width: expensePercentage + '%' }"
              >
                <span class="bar-value">${{ formatCurrency(totalExpenses) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="net-amount" :class="{ positive: netAmount >= 0, negative: netAmount < 0 }">
          Net: {{ netAmount >= 0 ? '+' : '-' }}${{ formatCurrency(Math.abs(netAmount)) }}
        </div>
      </div>

      <div class="summary-card">
        <h3>Savings Rate</h3>
        <div class="savings-chart">
          <div class="savings-circle">
            <div class="circle-progress" :style="{ '--percentage': savingsRate }">
              <span class="percentage">{{ savingsRate }}%</span>
            </div>
          </div>
          <div class="savings-details">
            <div class="detail-item">
              <span class="label">Target</span>
              <span class="value">20%</span>
            </div>
            <div class="detail-item">
              <span class="label">Monthly</span>
              <span class="value">${{ formatCurrency(monthlySavings) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Yearly</span>
              <span class="value">${{ formatCurrency(yearlySavings) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="reports-grid">
      <div class="report-card">
        <div class="report-header">
          <h3>Spending by Category</h3>
          <select v-model="categoryPeriod" class="small-select">
            <option value="month">This Month</option>
            <option value="quarter">This Quarter</option>
            <option value="year">This Year</option>
          </select>
        </div>
        <div class="category-chart">
          <div v-for="category in spendingByCategory" :key="category.name" class="category-item">
            <div class="category-info">
              <span class="category-name">{{ category.name }}</span>
              <span class="category-percentage">{{ category.percentage }}%</span>
            </div>
            <div class="category-bar">
              <div 
                class="bar-fill" 
                :style="{ width: category.percentage + '%' }"
                :class="category.name.toLowerCase()"
              ></div>
            </div>
            <div class="category-amount">${{ formatCurrency(category.amount) }}</div>
          </div>
        </div>
      </div>

      <div class="report-card">
        <div class="report-header">
          <h3>Monthly Trends</h3>
          <select v-model="trendYear" class="small-select">
            <option value="2024">2024</option>
            <option value="2023">2023</option>
            <option value="2022">2022</option>
          </select>
        </div>
        <div class="trend-chart">
          <div class="chart-container">
            <div class="chart-bars">
              <div 
                v-for="month in monthlyTrends" 
                :key="month.name" 
                class="chart-bar-group"
              >
                <div class="bar-group">
                  <div 
                    class="trend-bar income" 
                    :style="{ height: month.incomePercentage + '%' }"
                    :title="'Income: $' + formatCurrency(month.income)"
                  ></div>
                  <div 
                    class="trend-bar expense" 
                    :style="{ height: month.expensePercentage + '%' }"
                    :title="'Expenses: $' + formatCurrency(month.expenses)"
                  ></div>
                </div>
                <div class="month-label">{{ month.name }}</div>
              </div>
            </div>
          </div>
          <div class="chart-legend">
            <div class="legend-item">
              <div class="legend-color income"></div>
              <span>Income</span>
            </div>
            <div class="legend-item">
              <div class="legend-color expense"></div>
              <span>Expenses</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="detailed-reports">
      <div class="report-card full-width">
        <div class="report-header">
          <h3>Transaction Analysis</h3>
          <div class="analysis-filters">
            <select v-model="analysisType" class="small-select">
              <option value="top-expenses">Top Expenses</option>
              <option value="recurring">Recurring Payments</option>
              <option value="largest-income">Largest Income</option>
            </select>
          </div>
        </div>
        <div class="analysis-table">
          <div class="table-header">
            <div class="table-cell">Date</div>
            <div class="table-cell">Description</div>
            <div class="table-cell">Category</div>
            <div class="table-cell">Type</div>
            <div class="table-cell">Amount</div>
            <div class="table-cell">Frequency</div>
          </div>
          <div class="table-body">
            <div v-for="transaction in analyzedTransactions" :key="transaction.id" class="table-row">
              <div class="table-cell">{{ formatDate(transaction.date) }}</div>
              <div class="table-cell">{{ transaction.title }}</div>
              <div class="table-cell">
                <span class="category-badge" :class="transaction.category.toLowerCase()">
                  {{ transaction.category }}
                </span>
              </div>
              <div class="table-cell">
                <span class="type-badge" :class="transaction.type">
                  {{ transaction.type === 'income' ? 'Income' : 'Expense' }}
                </span>
              </div>
              <div class="table-cell amount" :class="transaction.type">
                {{ transaction.type === 'income' ? '+' : '-' }}${{ formatCurrency(transaction.amount) }}
              </div>
              <div class="table-cell">
                <span class="frequency" :class="transaction.frequency">
                  {{ transaction.frequency }}
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="insights-section">
      <h3>Financial Insights</h3>
      <div class="insights-grid">
        <div class="insight-card">
          <div class="insight-icon">💡</div>
          <div class="insight-content">
            <h4>Spending Alert</h4>
            <p>Your {{ highestSpendingCategory.name }} spending is {{ highestSpendingCategory.percentage }}% above average this month.</p>
          </div>
        </div>
        <div class="insight-card">
          <div class="insight-icon">🎯</div>
          <div class="insight-content">
            <h4>Savings Opportunity</h4>
            <p>You could save ${{ formatCurrency(savingsOpportunity) }} more per month by reducing discretionary spending.</p>
          </div>
        </div>
        <div class="insight-card">
          <div class="insight-icon">📈</div>
          <div class="insight-content">
            <h4>Income Growth</h4>
            <p>Your income has increased by {{ incomeGrowth }}% compared to last month.</p>
          </div>
        </div>
        <div class="insight-card">
          <div class="insight-icon">💰</div>
          <div class="insight-content">
            <h4>Budget Performance</h4>
            <p>{{ budgetPerformance }}% of your budgets are on track this month.</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Report configuration
const selectedPeriod = ref('monthly')
const categoryPeriod = ref('month')
const trendYear = ref('2024')
const analysisType = ref('top-expenses')

// Mock data for reports
const totalIncome = ref(4850.00)
const totalExpenses = ref(3125.50)
const monthlySavings = ref(1724.50)
const yearlySavings = ref(20694.00)

const spendingByCategory = ref([
  { name: 'Food & Dining', amount: 625.75, percentage: 20 },
  { name: 'Transportation', amount: 485.50, percentage: 16 },
  { name: 'Entertainment', amount: 325.99, percentage: 10 },
  { name: 'Shopping', amount: 450.25, percentage: 14 },
  { name: 'Utilities', amount: 289.75, percentage: 9 },
  { name: 'Healthcare', amount: 185.50, percentage: 6 },
  { name: 'Education', amount: 245.99, percentage: 8 },
  { name: 'Other', amount: 517.77, percentage: 17 }
])

const monthlyTrends = ref([
  { name: 'Jan', income: 4200, expenses: 2800, incomePercentage: 70, expensePercentage: 47 },
  { name: 'Feb', income: 4500, expenses: 2950, incomePercentage: 75, expensePercentage: 49 },
  { name: 'Mar', income: 4800, expenses: 3100, incomePercentage: 80, expensePercentage: 52 },
  { name: 'Apr', income: 4600, expenses: 2900, incomePercentage: 77, expensePercentage: 48 },
  { name: 'May', income: 4900, expenses: 3200, incomePercentage: 82, expensePercentage: 53 },
  { name: 'Jun', income: 5100, expenses: 3300, incomePercentage: 85, expensePercentage: 55 },
  { name: 'Jul', income: 4700, expenses: 3000, incomePercentage: 78, expensePercentage: 50 },
  { name: 'Aug', income: 5000, expenses: 3150, incomePercentage: 83, expensePercentage: 53 },
  { name: 'Sep', income: 5200, expenses: 3400, incomePercentage: 87, expensePercentage: 57 },
  { name: 'Oct', income: 4850, expenses: 3125, incomePercentage: 81, expensePercentage: 52 },
  { name: 'Nov', income: 0, expenses: 0, incomePercentage: 0, expensePercentage: 0 },
  { name: 'Dec', income: 0, expenses: 0, incomePercentage: 0, expensePercentage: 0 }
])

const analyzedTransactions = ref([
  { id: 1, title: 'Grocery Shopping', category: 'Food & Dining', amount: 125.75, type: 'expense', date: '2024-10-14', frequency: 'Weekly' },
  { id: 2, title: 'Netflix Subscription', category: 'Entertainment', amount: 15.99, type: 'expense', date: '2024-10-13', frequency: 'Monthly' },
  { id: 3, title: 'Electricity Bill', category: 'Utilities', amount: 89.75, type: 'expense', date: '2024-10-09', frequency: 'Monthly' },
  { id: 4, title: 'Gas Station', category: 'Transportation', amount: 45.50, type: 'expense', date: '2024-10-11', frequency: 'Weekly' },
  { id: 5, title: 'Salary Deposit', category: 'Income', amount: 3200.00, type: 'income', date: '2024-10-15', frequency: 'Monthly' },
  { id: 6, title: 'Restaurant Dinner', category: 'Food & Dining', amount: 68.25, type: 'expense', date: '2024-10-10', frequency: 'Occasional' },
  { id: 7, title: 'Gym Membership', category: 'Healthcare', amount: 29.99, type: 'expense', date: '2024-10-06', frequency: 'Monthly' },
  { id: 8, title: 'Freelance Work', category: 'Income', amount: 850.00, type: 'income', date: '2024-10-12', frequency: 'Irregular' },
  { id: 9, title: 'New Shoes', category: 'Shopping', amount: 89.99, type: 'expense', date: '2024-10-07', frequency: 'Occasional' },
  { id: 10, title: 'Investment Dividend', category: 'Income', amount: 150.00, type: 'income', date: '2024-10-07', frequency: 'Quarterly' }
])

// Computed properties
const netAmount = computed(() => totalIncome.value - totalExpenses.value)
const savingsRate = computed(() => {
  return totalIncome.value > 0 ? Math.round((monthlySavings.value / totalIncome.value) * 100) : 0
})

const incomePercentage = computed(() => {
  const max = Math.max(totalIncome.value, totalExpenses.value)
  return max > 0 ? Math.round((totalIncome.value / max) * 100) : 0
})

const expensePercentage = computed(() => {
  const max = Math.max(totalIncome.value, totalExpenses.value)
  return max > 0 ? Math.round((totalExpenses.value / max) * 100) : 0
})

const highestSpendingCategory = computed(() => {
  return spendingByCategory.value.reduce((max, category) => 
    category.amount > max.amount ? category : max, 
    { name: '', amount: 0, percentage: 0 }
  )
})

const savingsOpportunity = computed(() => {
  // Calculate potential savings from discretionary categories
  const discretionaryCategories = ['Entertainment', 'Shopping', 'Food & Dining']
  const discretionarySpending = spendingByCategory.value
    .filter(cat => discretionaryCategories.includes(cat.name))
    .reduce((sum, cat) => sum + cat.amount, 0)
  
  return Math.round(discretionarySpending * 0.2) // 20% reduction opportunity
})

const incomeGrowth = computed(() => {
  const currentMonth = monthlyTrends.value[9] // October
  const previousMonth = monthlyTrends.value[8] // September
  return previousMonth.income > 0 ? 
    Math.round(((currentMonth.income - previousMonth.income) / previousMonth.income) * 100) : 0
})

const budgetPerformance = computed(() => {
  // Mock budget performance
  return 75 // 75% of budgets on track
})

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric'
  })
}

const exportReport = () => {
  const reportData = {
    period: selectedPeriod.value,
    income: totalIncome.value,
    expenses: totalExpenses.value,
    savings: monthlySavings.value,
    savingsRate: savingsRate.value,
    categories: spendingByCategory.value,
    trends: monthlyTrends.value,
    insights: {
      highestSpendingCategory: highestSpendingCategory.value.name,
      savingsOpportunity: savingsOpportunity.value,
      incomeGrowth: incomeGrowth.value,
      budgetPerformance: budgetPerformance.value
    }
  }
  
  const dataStr = JSON.stringify(reportData, null, 2)
  const dataUri = 'data:application/json;charset=utf-8,'+ encodeURIComponent(dataStr)
  
  const exportFileDefaultName = `financial-report-${new Date().toISOString().split('T')[0]}.json`
  
  const linkElement = document.createElement('a')
  linkElement.setAttribute('href', dataUri)
  linkElement.setAttribute('download', exportFileDefaultName)
  linkElement.click()
  
  alert('Report exported successfully!')
}
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
  gap: 1rem;
  align-items: center;
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.period-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  font-size: 0.9rem;
  color: #4a5568;
}

.export-btn {
  padding: 0.5rem 1rem;
  background: #48bb78;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s;
}

.export-btn:hover {
  background: #38a169;
}

.reports-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 1.5rem;
}

.summary-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.summary-card h3 {
  font-size: 1.1rem;
  color: #4a5568;
  margin-bottom: 1rem;
  font-weight: 600;
}

.comparison-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1rem;
}

.comparison-bar {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.bar-label {
  width: 80px;
  font-size: 0.9rem;
  color: #718096;
  font-weight: 500;
}

.bar-container {
  flex: 1;
  height: 30px;
  background: #e2e8f0;
  border-radius: 15px;
  overflow: hidden;
  position: relative;
}

.bar {
  height: 100%;
  border-radius: 15px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 1rem;
  transition: width 0.5s ease;
}

.bar.income {
  background: linear-gradient(90deg, #48bb78, #38a169);
}

.bar.expense {
  background: linear-gradient(90deg, #f56565, #e53e3e);
}

.bar-value {
  color: white;
  font-size: 0.8rem;
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.net-amount {
  text-align: center;
  font-size: 1.2rem;
  font-weight: 700;
  padding: 0.5rem;
  border-radius: 6px;
  background: #f7fafc;
}

.net-amount.positive {
  color: #38a169;
}

.net-amount.negative {
  color: #e53e3e;
}

.savings-chart {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.savings-circle {
  width: 120px;
  height: 120px;
  position: relative;
}

.circle-progress {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(#667eea calc(var(--percentage) * 3.6deg), #e2e8f0 0deg);
  display: flex;
  align-items: center;
  justify-content: center;
}

.circle-progress::before {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  background: white;
  border-radius: 50%;
}

.percentage {
  position: relative;
  z-index: 1;
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.savings-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: #f7fafc;
  border-radius: 4px;
}

.detail-item .label {
  font-size: 0.85rem;
  color: #718096;
}

.detail-item .value {
  font-weight: 600;
  color: #2d3748;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.report-card.full-width {
  grid-column: 1 / -1;
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.report-header h3 {
  font-size: 1.1rem;
  color: #4a5568;
  font-weight: 600;
}

.small-select {
  padding: 0.25rem 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 4px;
  background: white;
  font-size: 0.8rem;
  color: #4a5568;
}

.category-chart {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-info {
  width: 120px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-name {
  font-size: 0.85rem;
  color: #4a5568;
}

.category-percentage {
  font-size: 0.8rem;
  font-weight: 600;
  color: #2d3748;
}

.category-bar {
  flex: 1;
  height: 20px;
  background: #e2e8f0;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.bar-fill.food & dining { background: linear-gradient(90deg, #48bb78, #38a169); }
.bar-fill.transportation { background: linear-gradient(90deg, #ed8936, #dd6b20); }
.bar-fill.entertainment { background: linear-gradient(90deg, #9f7aea, #805ad5); }
.bar-fill.shopping { background: linear-gradient(90deg, #f56565, #e53e3e); }
.bar-fill.utilities { background: linear-gradient(90deg, #4299e1, #3182ce); }
.bar-fill.healthcare { background: linear-gradient(90deg, #f6ad55, #ed8936); }
.bar-fill.education { background: linear-gradient(90deg, #38b2ac, #319795); }
.bar-fill.other { background: linear-gradient(90deg, #a0aec0, #718096); }

.category-amount {
  width: 80px;
  text-align: right;
  font-size: 0.85rem;
  font-weight: 600;
  color: #2d3748;
}

.trend-chart {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.chart-container {
  height: 200px;
  display: flex;
  align-items: flex-end;
  gap: 0.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.chart-bars {
  flex: 1;
  display: flex;
  justify-content: space-around;
  align-items: flex-end;
  height: 100%;
}

.chart-bar-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  width: 30px;
}

.bar-group {
  display: flex;
  gap: 2px;
  height: 100%;
  align-items: flex-end;
}

.trend-bar {
  width: 12px;
  border-radius: 3px 3px 0 0;
  transition: height 0.5s ease;
}

.trend-bar.income {
  background: #48bb78;
}

.trend-bar.expense {
  background: #f56565;
}

.month-label {
  font-size: 0.75rem;
  color: #718096;
  text-align: center;
}

.chart-legend {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #4a5568;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.income {
  background: #48bb78;
}

.legend-color.expense {
  background: #f56565;
}

.analysis-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
  background: #f7fafc;
  padding: 1rem;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
  font-size: 0.85rem;
}

.table-body {
  max-height: 300px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #e2e8f0;
  align-items: center;
  transition: background-color 0.3s;
  font-size: 0.85rem;
}

.table-row:hover {
  background: #f7fafc;
}

.table-cell {
  padding: 0.25rem;
}

.category-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.category-badge.food & dining {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.category-badge.transportation {
  background: rgba(237, 137, 54, 0.1);
  color: #ed8936;
}

.category-badge.entertainment {
  background: rgba(159, 122, 234, 0.1);
  color: #9f7aea;
}

.category-badge.shopping {
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
}

.category-badge.utilities {
  background: rgba(66, 153, 225, 0.1);
  color: #4299e1;
}

.category-badge.healthcare {
  background: rgba(246, 173, 85, 0.1);
  color: #f6ad55;
}

.category-badge.education {
  background: rgba(56, 178, 172, 0.1);
  color: #38b2ac;
}

.category-badge.income {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.type-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.type-badge.income {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.type-badge.expense {
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
}

.amount {
  font-weight: 600;
}

.amount.income {
  color: #38a169;
}

.amount.expense {
  color: #e53e3e;
}

.frequency {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
  background: #f7fafc;
  color: #4a5568;
}

.frequency.weekly {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.frequency.monthly {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.frequency.quarterly {
  background: rgba(237, 137, 54, 0.1);
  color: #ed8936;
}

.insights-section {
  margin-top: 1rem;
}

.insights-section h3 {
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 1rem;
}

.insights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.insight-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 1rem;
  align-items: flex-start;
}

.insight-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.insight-content h4 {
  font-size: 1rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.insight-content p {
  font-size: 0.9rem;
  color: #718096;
  line-height: 1.4;
}

/* Responsive Design */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    flex-direction: column;
  }
  
  .filter-controls {
    width: 100%;
  }
  
  .period-select {
    flex: 1;
  }
  
  .reports-summary {
    grid-template-columns: 1fr;
  }
  
  .reports-grid {
    grid-template-columns: 1fr;
  }
  
  .savings-chart {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .table-header {
    display: none;
  }
  
  .table-row {
    grid-template-columns: 1fr;
    gap: 0.5rem;
    padding: 1rem;
    border-bottom: 2px solid #e2e8f0;
  }
  
  .table-cell {
    display: flex;
    justify-content: space-between;
    padding: 0.25rem 0;
  }
  
  .table-cell::before {
    content: attr(data-label);
    font-weight: 600;
    color: #4a5568;
  }
  
  .insights-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .chart-container {
    height: 150px;
  }
  
  .chart-bar-group {
    width: 20px;
  }
  
  .trend-bar {
    width: 8px;
  }
}
</style>