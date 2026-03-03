<template>
  <div class="budgets">
    <div class="page-header">
      <h2>Budget Management</h2>
      <div class="header-actions">
        <select v-model="selectedPeriod" class="period-select">
          <option value="monthly">Monthly Budgets</option>
          <option value="weekly">Weekly Budgets</option>
          <option value="yearly">Yearly Budgets</option>
        </select>
        <button class="add-btn" @click="showAddModal = true">
          <span>+ Create Budget</span>
        </button>
      </div>
    </div>

    <div class="budget-overview">
      <div class="overview-card">
        <h3>Total Budget</h3>
        <p class="overview-amount">${{ formatCurrency(totalBudget) }}</p>
      </div>
      <div class="overview-card">
        <h3>Total Spent</h3>
        <p class="overview-amount spent">${{ formatCurrency(totalSpent) }}</p>
      </div>
      <div class="overview-card">
        <h3>Remaining</h3>
        <p class="overview-amount remaining">${{ formatCurrency(totalRemaining) }}</p>
      </div>
      <div class="overview-card">
        <h3>Utilization</h3>
        <p class="overview-amount">{{ utilizationPercentage }}%</p>
      </div>
    </div>

    <div class="budgets-grid">
      <div v-for="budget in budgets" :key="budget.id" class="budget-card">
        <div class="budget-header">
          <div class="budget-category">
            <span class="category-icon">{{ getCategoryIcon(budget.category) }}</span>
            <h3>{{ budget.category }}</h3>
          </div>
          <div class="budget-actions">
            <button class="action-btn edit" @click="editBudget(budget)">✏️</button>
            <button class="action-btn delete" @click="deleteBudget(budget.id)">🗑️</button>
          </div>
        </div>
        
        <div class="budget-progress">
          <div class="progress-info">
            <span class="progress-text">${{ formatCurrency(budget.spent) }} of ${{ formatCurrency(budget.total) }}</span>
            <span class="progress-percentage">{{ budget.percentage }}%</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: budget.percentage + '%' }"
              :class="{ warning: budget.percentage > 80, danger: budget.percentage > 95 }"
            ></div>
          </div>
          <div class="progress-details">
            <span class="remaining">${{ formatCurrency(budget.remaining) }} remaining</span>
            <span class="period">{{ budget.period }}</span>
          </div>
        </div>
        
        <div class="budget-transactions">
          <h4>Recent Transactions</h4>
          <div v-if="getCategoryTransactions(budget.category).length > 0" class="transactions-list">
            <div 
              v-for="transaction in getCategoryTransactions(budget.category).slice(0, 3)" 
              :key="transaction.id" 
              class="transaction-item"
            >
              <span class="transaction-title">{{ transaction.title }}</span>
              <span class="transaction-amount">-${{ formatCurrency(transaction.amount) }}</span>
            </div>
          </div>
          <div v-else class="no-transactions">
            No transactions this period
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Budget Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingBudget ? 'Edit Budget' : 'Create New Budget' }}</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>Category</label>
            <select v-model="newBudget.category">
              <option value="Food & Dining">Food & Dining</option>
              <option value="Transportation">Transportation</option>
              <option value="Entertainment">Entertainment</option>
              <option value="Shopping">Shopping</option>
              <option value="Utilities">Utilities</option>
              <option value="Healthcare">Healthcare</option>
              <option value="Education">Education</option>
              <option value="Housing">Housing</option>
              <option value="Personal Care">Personal Care</option>
              <option value="Other">Other</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Budget Amount</label>
            <div class="amount-input">
              <span class="currency">$</span>
              <input v-model="newBudget.total" type="number" step="0.01" placeholder="0.00">
            </div>
          </div>
          
          <div class="form-group">
            <label>Period</label>
            <select v-model="newBudget.period">
              <option value="Monthly">Monthly</option>
              <option value="Weekly">Weekly</option>
              <option value="Yearly">Yearly</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Start Date</label>
            <input v-model="newBudget.startDate" type="date">
          </div>
          
          <div class="form-group">
            <label>Description (Optional)</label>
            <textarea v-model="newBudget.description" placeholder="Add a description for this budget..."></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="showAddModal = false">Cancel</button>
          <button class="save-btn" @click="saveBudget">
            {{ editingBudget ? 'Update' : 'Create' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Mock budgets data
const budgets = ref([
  { 
    id: 1, 
    category: 'Food & Dining', 
    total: 600, 
    spent: 425, 
    remaining: 175, 
    percentage: 71,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Groceries, restaurants, coffee shops'
  },
  { 
    id: 2, 
    category: 'Entertainment', 
    total: 200, 
    spent: 85, 
    remaining: 115, 
    percentage: 43,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Movies, streaming services, games'
  },
  { 
    id: 3, 
    category: 'Transportation', 
    total: 300, 
    spent: 210, 
    remaining: 90, 
    percentage: 70,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Gas, public transport, ride sharing'
  },
  { 
    id: 4, 
    category: 'Shopping', 
    total: 400, 
    spent: 150, 
    remaining: 250, 
    percentage: 38,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Clothing, electronics, household items'
  },
  { 
    id: 5, 
    category: 'Utilities', 
    total: 350, 
    spent: 289, 
    remaining: 61, 
    percentage: 83,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Electricity, water, internet, phone'
  },
  { 
    id: 6, 
    category: 'Healthcare', 
    total: 150, 
    spent: 45, 
    remaining: 105, 
    percentage: 30,
    period: 'Monthly',
    startDate: '2024-10-01',
    description: 'Medications, doctor visits, insurance'
  }
])

// Mock transactions for budget tracking
const transactions = ref([
  { id: 1, title: 'Grocery Shopping', category: 'Food & Dining', amount: 125.75, date: '2024-10-14' },
  { id: 2, title: 'Restaurant Dinner', category: 'Food & Dining', amount: 68.25, date: '2024-10-10' },
  { id: 3, title: 'Netflix Subscription', category: 'Entertainment', amount: 15.99, date: '2024-10-13' },
  { id: 4, title: 'Movie Tickets', category: 'Entertainment', amount: 32.50, date: '2024-10-08' },
  { id: 5, title: 'Gas Station', category: 'Transportation', amount: 45.50, date: '2024-10-11' },
  { id: 6, title: 'Uber Ride', category: 'Transportation', amount: 28.75, date: '2024-10-09' },
  { id: 7, title: 'New Shoes', category: 'Shopping', amount: 89.99, date: '2024-10-07' },
  { id: 8, title: 'Electricity Bill', category: 'Utilities', amount: 89.75, date: '2024-10-09' },
  { id: 9, title: 'Internet Bill', category: 'Utilities', amount: 65.00, date: '2024-10-05' },
  { id: 10, title: 'Pharmacy', category: 'Healthcare', amount: 25.50, date: '2024-10-12' }
])

const selectedPeriod = ref('monthly')
const showAddModal = ref(false)
const editingBudget = ref(null)

const newBudget = ref({
  category: 'Food & Dining',
  total: 0,
  period: 'Monthly',
  startDate: new Date().toISOString().split('T')[0],
  description: ''
})

const totalBudget = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.total, 0)
})

const totalSpent = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.spent, 0)
})

const totalRemaining = computed(() => {
  return totalBudget.value - totalSpent.value
})

const utilizationPercentage = computed(() => {
  return totalBudget.value > 0 ? Math.round((totalSpent.value / totalBudget.value) * 100) : 0
})

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  })
}

const getCategoryIcon = (category) => {
  const icons = {
    'Food & Dining': '🍽️',
    'Transportation': '🚗',
    'Entertainment': '🎬',
    'Shopping': '🛍️',
    'Utilities': '💡',
    'Healthcare': '🏥',
    'Education': '📚',
    'Housing': '🏠',
    'Personal Care': '💇',
    'Other': '📊'
  }
  return icons[category] || '💰'
}

const getCategoryTransactions = (category) => {
  return transactions.value.filter(t => t.category === category)
}

const editBudget = (budget) => {
  editingBudget.value = budget
  newBudget.value = { ...budget }
  showAddModal.value = true
}

const deleteBudget = (id) => {
  if (confirm('Are you sure you want to delete this budget?')) {
    budgets.value = budgets.value.filter(b => b.id !== id)
  }
}

const saveBudget = () => {
  if (!newBudget.value.category || !newBudget.value.total) {
    alert('Please fill in all required fields')
    return
  }

  // Calculate spent amount from transactions
  const categoryTransactions = getCategoryTransactions(newBudget.value.category)
  const spent = categoryTransactions.reduce((sum, t) => sum + t.amount, 0)
  const remaining = Math.max(0, newBudget.value.total - spent)
  const percentage = newBudget.value.total > 0 ? Math.round((spent / newBudget.value.total) * 100) : 0

  if (editingBudget.value) {
    // Update existing budget
    const index = budgets.value.findIndex(b => b.id === editingBudget.value.id)
    if (index !== -1) {
      budgets.value[index] = {
        ...newBudget.value,
        id: editingBudget.value.id,
        spent,
        remaining,
        percentage
      }
    }
  } else {
    // Add new budget
    const newId = Math.max(...budgets.value.map(b => b.id), 0) + 1
    budgets.value.push({
      ...newBudget.value,
      id: newId,
      spent,
      remaining,
      percentage
    })
  }

  resetForm()
  showAddModal.value = false
}

const resetForm = () => {
  newBudget.value = {
    category: 'Food & Dining',
    total: 0,
    period: 'Monthly',
    startDate: new Date().toISOString().split('T')[0],
    description: ''
  }
  editingBudget.value = null
}
</script>

<style scoped>
.budgets {
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

.period-select {
  padding: 0.5rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  font-size: 0.9rem;
  color: #4a5568;
}

.add-btn {
  padding: 0.75rem 1.5rem;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-btn:hover {
  background: #5a67d8;
}

.budget-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.overview-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.overview-card h3 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.overview-amount {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.overview-amount.spent {
  color: #e53e3e;
}

.overview-amount.remaining {
  color: #38a169;
}

.budgets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.budget-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.budget-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.budget-category {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.category-icon {
  font-size: 1.5rem;
}

.budget-category h3 {
  font-size: 1.1rem;
  color: #2d3748;
  margin: 0;
}

.budget-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  padding: 0.5rem;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.action-btn.edit:hover {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.action-btn.delete:hover {
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
}

.budget-progress {
  margin-bottom: 1.5rem;
}

.progress-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.5rem;
}

.progress-text {
  font-size: 0.9rem;
  color: #4a5568;
}

.progress-percentage {
  font-weight: 600;
  color: #2d3748;
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
  background: linear-gradient(90deg, #48bb78, #38a169);
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-fill.warning {
  background: linear-gradient(90deg, #ed8936, #dd6b20);
}

.progress-fill.danger {
  background: linear-gradient(90deg, #f56565, #e53e3e);
}

.progress-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
}

.remaining {
  color: #48bb78;
  font-weight: 500;
}

.period {
  color: #718096;
}

.budget-transactions h4 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.transactions-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem;
  background: #f7fafc;
  border-radius: 4px;
  font-size: 0.85rem;
}

.transaction-title {
  color: #4a5568;
}

.transaction-amount {
  color: #e53e3e;
  font-weight: 500;
}

.no-transactions {
  text-align: center;
  padding: 1rem;
  color: #a0aec0;
  font-style: italic;
  background: #f7fafc;
  border-radius: 4px;
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  font-size: 1.25rem;
  color: #2d3748;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
}

.close-btn:hover {
  color: #4a5568;
}

.modal-body {
  padding: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #4a5568;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  color: #4a5568;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.amount-input {
  position: relative;
}

.amount-input .currency {
  position: absolute;
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #4a5568;
  font-weight: 500;
}

.amount-input input {
  padding-left: 2rem;
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.cancel-btn,
.save-btn {
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #e2e8f0;
  color: #4a5568;
  border: none;
}

.cancel-btn:hover {
  background: #cbd5e0;
}

.save-btn {
  background: #667eea;
  color: white;
  border: none;
}

.save-btn:hover {
  background: #5a67d8;
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
  
  .period-select {
    width: 100%;
  }
  
  .budget-overview {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .budgets-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .budget-overview {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}
</style>