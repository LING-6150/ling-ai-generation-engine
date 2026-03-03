<template>
  <div class="budgets">
    <div class="page-header">
      <h2>Budget Management</h2>
      <button class="btn btn-primary" @click="showAddModal = true">
        <span class="btn-icon">+</span> Create Budget
      </button>
    </div>

    <div class="budgets-overview">
      <div class="overview-card">
        <h3>Monthly Budget Overview</h3>
        <div class="overview-stats">
          <div class="stat">
            <span class="stat-label">Total Budget</span>
            <span class="stat-value">${{ totalBudget.toFixed(2) }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">Total Spent</span>
            <span class="stat-value">${{ totalSpent.toFixed(2) }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">Remaining</span>
            <span class="stat-value" :class="remainingBudget >= 0 ? 'positive' : 'negative'">
              ${{ remainingBudget.toFixed(2) }}
            </span>
          </div>
          <div class="stat">
            <span class="stat-label">Usage</span>
            <span class="stat-value">{{ budgetUsage }}%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="budgets-grid">
      <div v-for="budget in budgets" :key="budget.id" class="budget-card">
        <div class="budget-header">
          <div class="budget-category">
            <div class="category-icon" :style="{ backgroundColor: getCategoryColor(budget.category) }">
              {{ getCategoryIcon(budget.category) }}
            </div>
            <div>
              <h4>{{ budget.category }}</h4>
              <p class="budget-period">{{ budget.period }}</p>
            </div>
          </div>
          <div class="budget-actions">
            <button class="btn-icon-small" @click="editBudget(budget)">✏️</button>
            <button class="btn-icon-small delete" @click="deleteBudget(budget.id)">🗑️</button>
          </div>
        </div>
        
        <div class="budget-progress">
          <div class="progress-info">
            <span class="progress-label">Spent: ${{ budget.spent.toFixed(2) }} / ${{ budget.amount.toFixed(2) }}</span>
            <span class="progress-percent">{{ budget.percentage }}%</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: budget.percentage + '%' }"
              :class="getProgressClass(budget.percentage)"
            ></div>
          </div>
          <div class="budget-status">
            <span class="status-text" :class="getStatusClass(budget.percentage)">
              {{ getStatusText(budget.percentage) }}
            </span>
            <span class="remaining-amount">
              ${{ (budget.amount - budget.spent).toFixed(2) }} remaining
            </span>
          </div>
        </div>
        
        <div class="budget-details">
          <div class="detail-item">
            <span class="detail-label">Daily Average</span>
            <span class="detail-value">${{ budget.dailyAverage.toFixed(2) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Transactions</span>
            <span class="detail-value">{{ budget.transactionCount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Budget Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingBudget ? 'Edit Budget' : 'Create New Budget' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveBudget">
            <div class="form-group">
              <label for="budget-category">Category</label>
              <select id="budget-category" v-model="newBudget.category" required>
                <option value="">Select Category</option>
                <option v-for="category in availableCategories" :key="category" :value="category">{{ category }}</option>
              </select>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="budget-amount">Budget Amount ($)</label>
                <input
                  id="budget-amount"
                  v-model="newBudget.amount"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                />
              </div>
              
              <div class="form-group">
                <label for="budget-period">Period</label>
                <select id="budget-period" v-model="newBudget.period" required>
                  <option value="Monthly">Monthly</option>
                  <option value="Weekly">Weekly</option>
                  <option value="Yearly">Yearly</option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label for="budget-description">Description (Optional)</label>
              <textarea
                id="budget-description"
                v-model="newBudget.description"
                rows="3"
                placeholder="Add notes about this budget..."
              ></textarea>
            </div>
            
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
              <button type="submit" class="btn btn-primary">
                {{ editingBudget ? 'Update' : 'Create' }} Budget
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const budgets = ref([
  { 
    id: 1, 
    category: 'Food & Dining', 
    amount: 600, 
    spent: 425.75, 
    period: 'Monthly',
    description: 'Groceries, restaurants, and coffee shops',
    transactionCount: 12,
    dailyAverage: 14.19
  },
  { 
    id: 2, 
    category: 'Transportation', 
    amount: 300, 
    spent: 245.00, 
    period: 'Monthly',
    description: 'Gas, public transport, and ride-sharing',
    transactionCount: 8,
    dailyAverage: 8.17
  },
  { 
    id: 3, 
    category: 'Entertainment', 
    amount: 200, 
    spent: 165.99, 
    period: 'Monthly',
    description: 'Streaming services, movies, and events',
    transactionCount: 5,
    dailyAverage: 5.53
  },
  { 
    id: 4, 
    category: 'Utilities', 
    amount: 350, 
    spent: 289.50, 
    period: 'Monthly',
    description: 'Electricity, water, internet, and phone',
    transactionCount: 4,
    dailyAverage: 9.65
  },
  { 
    id: 5, 
    category: 'Shopping', 
    amount: 400, 
    spent: 320.25, 
    period: 'Monthly',
    description: 'Clothing, electronics, and household items',
    transactionCount: 7,
    dailyAverage: 10.68
  },
  { 
    id: 6, 
    category: 'Health & Fitness', 
    amount: 150, 
    spent: 89.99, 
    period: 'Monthly',
    description: 'Gym membership, supplements, and healthcare',
    transactionCount: 3,
    dailyAverage: 3.00
  }
])

const availableCategories = ref([
  'Food & Dining', 'Transportation', 'Entertainment', 'Utilities', 
  'Shopping', 'Health & Fitness', 'Education', 'Travel', 
  'Personal Care', 'Gifts & Donations', 'Housing', 'Insurance'
])

const showAddModal = ref(false)
const editingBudget = ref(null)

const newBudget = ref({
  category: '',
  amount: '',
  period: 'Monthly',
  description: ''
})

// Add computed properties for percentage
budgets.value.forEach(budget => {
  budget.percentage = Math.min(100, (budget.spent / budget.amount) * 100)
})

const totalBudget = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.amount, 0)
})

const totalSpent = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.spent, 0)
})

const remainingBudget = computed(() => totalBudget.value - totalSpent.value)

const budgetUsage = computed(() => {
  return totalBudget.value > 0 ? Math.round((totalSpent.value / totalBudget.value) * 100) : 0
})

const getCategoryColor = (category) => {
  const colors = {
    'Food & Dining': '#ed8936',
    'Transportation': '#4299e1',
    'Entertainment': '#d53f8c',
    'Utilities': '#38a169',
    'Shopping': '#d69e2e',
    'Health & Fitness': '#e53e3e',
    'Education': '#3182ce',
    'Travel': '#805ad5',
    'Personal Care': '#38b2ac',
    'Gifts & Donations': '#f56565',
    'Housing': '#4a5568',
    'Insurance': '#667eea'
  }
  return colors[category] || '#718096'
}

const getCategoryIcon = (category) => {
  const icons = {
    'Food & Dining': '🍽️',
    'Transportation': '🚗',
    'Entertainment': '🎬',
    'Utilities': '💡',
    'Shopping': '🛍️',
    'Health & Fitness': '💪',
    'Education': '📚',
    'Travel': '✈️',
    'Personal Care': '💅',
    'Gifts & Donations': '🎁',
    'Housing': '🏠',
    'Insurance': '🛡️'
  }
  return icons[category] || '💰'
}

const getProgressClass = (percentage) => {
  if (percentage <= 70) return 'good'
  if (percentage <= 90) return 'warning'
  return 'danger'
}

const getStatusClass = (percentage) => {
  if (percentage <= 70) return 'good'
  if (percentage <= 90) return 'warning'
  return 'danger'
}

const getStatusText = (percentage) => {
  if (percentage <= 70) return 'On Track'
  if (percentage <= 90) return 'Approaching Limit'
  return 'Over Budget'
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
  if (editingBudget.value) {
    // Update existing budget
    const index = budgets.value.findIndex(b => b.id === editingBudget.value.id)
    if (index !== -1) {
      const updatedBudget = {
        ...newBudget.value,
        id: editingBudget.value.id,
        amount: parseFloat(newBudget.value.amount),
        spent: editingBudget.value.spent || 0,
        transactionCount: editingBudget.value.transactionCount || 0,
        dailyAverage: editingBudget.value.dailyAverage || 0
      }
      updatedBudget.percentage = Math.min(100, (updatedBudget.spent / updatedBudget.amount) * 100)
      budgets.value[index] = updatedBudget
    }
  } else {
    // Add new budget
    const newId = Math.max(...budgets.value.map(b => b.id), 0) + 1
    const budgetAmount = parseFloat(newBudget.value.amount)
    const newBudgetItem = {
      ...newBudget.value,
      id: newId,
      amount: budgetAmount,
      spent: 0,
      transactionCount: 0,
      dailyAverage: 0,
      percentage: 0
    }
    budgets.value.push(newBudgetItem)
  }
  
  closeModal()
}

const closeModal = () => {
  showAddModal.value = false
  editingBudget.value = null
  newBudget.value = {
    category: '',
    amount: '',
    period: 'Monthly',
    description: ''
  }
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

.budgets-overview {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 2rem;
  color: white;
}

.overview-card h3 {
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.overview-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.stat {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
}

.stat-value.positive {
  color: #a0e9a0;
}

.stat-value.negative {
  color: #ff9a9a;
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
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.budget-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.budget-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1.5rem;
}

.budget-category {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.budget-category h4 {
  font-size: 1.1rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.budget-period {
  font-size: 0.85rem;
  color: #718096;
}

.budget-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon-small {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.btn-icon-small:hover {
  background: #f7fafc;
}

.btn-icon-small.delete:hover {
  background: rgba(245, 101, 101, 0.1);
  color: #e53e3e;
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

.progress-label {
  font-size: 0.9rem;
  color: #4a5568;
}

.progress-percent {
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
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-fill.good {
  background: linear-gradient(to right, #48bb78, #38a169);
}

.progress-fill.warning {
  background: linear-gradient(to right, #ed8936, #dd6b20);
}

.progress-fill.danger {
  background: linear-gradient(to right, #f56565, #e53e3e);
}

.budget-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.85rem;
}

.status-text {
  font-weight: 500;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
}

.status-text.good {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.status-text.warning {
  background: rgba(237, 137, 54, 0.1);
  color: #ed8936;
}

.status-text.danger {
  background: rgba(245, 101, 101, 0.1);
  color: #e53e3e;
}

.remaining-amount {
  color: #718096;
}

.budget-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.detail-label {
  font-size: 0.85rem;
  color: #718096;
}

.detail-value {
  font-weight: 600;
  color: #2d3748;
}

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
  padding: 1rem;
}

.modal {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
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

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.modal-close:hover {
  background: #f7fafc;
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
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e2e8f0;
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .budgets-grid {
    grid-template-columns: 1fr;
  }
  
  .overview-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .overview-stats {
    grid-template-columns: 1fr;
  }
  
  .budget-details {
    grid-template-columns: 1fr;
  }
  
  .modal {
    margin: 1rem;
  }
}
</style>