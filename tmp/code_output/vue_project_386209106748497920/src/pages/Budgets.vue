<template>
  <div class="budgets-page">
    <div class="page-header">
      <h2>Budget Management</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddBudget = true">
          <span>+ Create Budget</span>
        </button>
      </div>
    </div>
    
    <div class="budget-summary">
      <div class="summary-card">
        <h3>Total Budget</h3>
        <p class="summary-value">{{ formatCurrency(totalBudget) }}</p>
      </div>
      <div class="summary-card">
        <h3>Total Spent</h3>
        <p class="summary-value">{{ formatCurrency(totalSpent) }}</p>
      </div>
      <div class="summary-card">
        <h3>Remaining</h3>
        <p class="summary-value" :class="remainingBudget >= 0 ? 'positive' : 'negative'">
          {{ formatCurrency(remainingBudget) }}
        </p>
      </div>
      <div class="summary-card">
        <h3>Utilization</h3>
        <p class="summary-value">{{ utilizationRate }}%</p>
      </div>
    </div>
    
    <div class="budgets-grid">
      <div v-for="budget in budgets" :key="budget.id" class="budget-card">
        <div class="budget-header">
          <div class="budget-category">
            <div class="category-icon" :style="{ backgroundColor: budget.color }">
              {{ budget.icon }}
            </div>
            <div>
              <h3>{{ budget.category }}</h3>
              <p class="budget-period">{{ budget.period }}</p>
            </div>
          </div>
          <div class="budget-actions">
            <button class="btn-icon" @click="editBudget(budget)">
              ✏️
            </button>
            <button class="btn-icon delete" @click="deleteBudget(budget.id)">
              🗑️
            </button>
          </div>
        </div>
        
        <div class="budget-progress">
          <div class="progress-info">
            <span class="progress-label">Spent: {{ formatCurrency(budget.spent) }} / {{ formatCurrency(budget.amount) }}</span>
            <span class="progress-percentage">{{ budget.percentage }}%</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ 
                width: Math.min(budget.percentage, 100) + '%',
                backgroundColor: budget.color
              }"
            ></div>
          </div>
          <div class="progress-status">
            <span v-if="budget.percentage < 80" class="status-good">On Track</span>
            <span v-else-if="budget.percentage < 100" class="status-warning">Approaching Limit</span>
            <span v-else class="status-danger">Over Budget</span>
            <span class="remaining-amount">
              {{ formatCurrency(budget.amount - budget.spent) }} remaining
            </span>
          </div>
        </div>
        
        <div class="budget-details">
          <div class="detail-item">
            <span class="detail-label">Daily Average</span>
            <span class="detail-value">{{ formatCurrency(budget.dailyAverage) }}</span>
          </div>
          <div class="detail-item">
            <span class="detail-label">Days Left</span>
            <span class="detail-value">{{ budget.daysLeft }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Add/Edit Budget Modal -->
    <div v-if="showAddBudget" class="modal-overlay" @click="showAddBudget = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingBudget ? 'Edit Budget' : 'Create New Budget' }}</h3>
          <button class="btn-close" @click="showAddBudget = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Category</label>
            <select v-model="newBudget.category">
              <option value="Food">Food</option>
              <option value="Transport">Transport</option>
              <option value="Housing">Housing</option>
              <option value="Entertainment">Entertainment</option>
              <option value="Shopping">Shopping</option>
              <option value="Utilities">Utilities</option>
              <option value="Healthcare">Healthcare</option>
              <option value="Education">Education</option>
              <option value="Other">Other</option>
            </select>
          </div>
          <div class="form-group">
            <label>Budget Amount</label>
            <input v-model="newBudget.amount" type="number" step="0.01" placeholder="0.00">
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
            <label>Current Spending</label>
            <input v-model="newBudget.spent" type="number" step="0.01" placeholder="0.00">
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showAddBudget = false">
            Cancel
          </button>
          <button class="btn btn-primary" @click="saveBudget">
            {{ editingBudget ? 'Update' : 'Create' }} Budget
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const budgets = ref([])
const showAddBudget = ref(false)
const editingBudget = ref(null)

const newBudget = ref({
  category: 'Food',
  amount: 0,
  period: 'Monthly',
  spent: 0
})

// Initialize with sample data
onMounted(() => {
  budgets.value = [
    { 
      id: 1, 
      category: 'Food', 
      amount: 600, 
      spent: 450, 
      period: 'Monthly',
      icon: '🛒',
      color: '#764ba2',
      dailyAverage: 15,
      daysLeft: 10
    },
    { 
      id: 2, 
      category: 'Transport', 
      amount: 300, 
      spent: 210, 
      period: 'Monthly',
      icon: '🚗',
      color: '#f093fb',
      dailyAverage: 7,
      daysLeft: 10
    },
    { 
      id: 3, 
      category: 'Entertainment', 
      amount: 200, 
      spent: 180, 
      period: 'Monthly',
      icon: '🎬',
      color: '#f5576c',
      dailyAverage: 6,
      daysLeft: 10
    },
    { 
      id: 4, 
      category: 'Shopping', 
      amount: 250, 
      spent: 120, 
      period: 'Monthly',
      icon: '🛍️',
      color: '#4facfe',
      dailyAverage: 4,
      daysLeft: 10
    },
    { 
      id: 5, 
      category: 'Utilities', 
      amount: 400, 
      spent: 320, 
      period: 'Monthly',
      icon: '⚡',
      color: '#00f2fe',
      dailyAverage: 10.7,
      daysLeft: 10
    },
    { 
      id: 6, 
      category: 'Healthcare', 
      amount: 150, 
      spent: 45, 
      period: 'Monthly',
      icon: '🏥',
      color: '#38a169',
      dailyAverage: 1.5,
      daysLeft: 10
    }
  ]
})

const totalBudget = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.amount, 0)
})

const totalSpent = computed(() => {
  return budgets.value.reduce((sum, budget) => sum + budget.spent, 0)
})

const remainingBudget = computed(() => {
  return totalBudget.value - totalSpent.value
})

const utilizationRate = computed(() => {
  if (totalBudget.value === 0) return 0
  return Math.round((totalSpent.value / totalBudget.value) * 100)
})

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
  })
}

const editBudget = (budget) => {
  editingBudget.value = budget
  newBudget.value = { ...budget }
  showAddBudget.value = true
}

const saveBudget = () => {
  if (!newBudget.value.category || newBudget.value.amount <= 0) {
    alert('Please fill in all required fields')
    return
  }
  
  const percentage = Math.round((newBudget.value.spent / newBudget.value.amount) * 100)
  const dailyAverage = Math.round((newBudget.value.spent / 20) * 100) / 100 // Assuming 20 days passed
  
  if (editingBudget.value) {
    // Update existing budget
    const index = budgets.value.findIndex(b => b.id === editingBudget.value.id)
    if (index !== -1) {
      budgets.value[index] = {
        ...newBudget.value,
        id: editingBudget.value.id,
        percentage,
        dailyAverage,
        daysLeft: 10,
        icon: getIconForCategory(newBudget.value.category),
        color: getColorForCategory(newBudget.value.category)
      }
    }
  } else {
    // Add new budget
    const newId = Math.max(...budgets.value.map(b => b.id), 0) + 1
    budgets.value.push({
      ...newBudget.value,
      id: newId,
      percentage,
      dailyAverage,
      daysLeft: 10,
      icon: getIconForCategory(newBudget.value.category),
      color: getColorForCategory(newBudget.value.category)
    })
  }
  
  resetForm()
  showAddBudget.value = false
}

const deleteBudget = (id) => {
  if (confirm('Are you sure you want to delete this budget?')) {
    budgets.value = budgets.value.filter(b => b.id !== id)
  }
}

const resetForm = () => {
  newBudget.value = {
    category: 'Food',
    amount: 0,
    period: 'Monthly',
    spent: 0
  }
  editingBudget.value = null
}

const getIconForCategory = (category) => {
  const icons = {
    'Food': '🛒',
    'Transport': '🚗',
    'Housing': '🏠',
    'Entertainment': '🎬',
    'Shopping': '🛍️',
    'Utilities': '⚡',
    'Healthcare': '🏥',
    'Education': '📚',
    'Other': '📝'
  }
  return icons[category] || '📝'
}

const getColorForCategory = (category) => {
  const colors = {
    'Food': '#764ba2',
    'Transport': '#f093fb',
    'Housing': '#667eea',
    'Entertainment': '#f5576c',
    'Shopping': '#4facfe',
    'Utilities': '#00f2fe',
    'Healthcare': '#38a169',
    'Education': '#f6d365',
    'Other': '#718096'
  }
  return colors[category] || '#718096'
}
</script>

<style scoped>
.budgets-page {
  padding: 1rem;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.page-header h2 {
  font-size: 2rem;
  color: #2d3748;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
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

.budget-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.summary-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  text-align: center;
}

.summary-card h3 {
  font-size: 0.9rem;
  color: #718096;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 0.5rem;
}

.summary-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #2d3748;
}

.summary-value.positive {
  color: #38a169;
}

.summary-value.negative {
  color: #e53e3e;
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
  transition: transform 0.2s, box-shadow 0.2s;
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
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1.5rem;
  color: white;
}

.budget-category h3 {
  font-size: 1.25rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.budget-period {
  font-size: 0.9rem;
  color: #718096;
}

.budget-actions {
  display: flex;
  gap: 0.5rem;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  font-size: 1.1rem;
  opacity: 0.7;
  transition: opacity 0.2s;
  border-radius: 4px;
}

.btn-icon:hover {
  opacity: 1;
  background: #f7fafc;
}

.btn-icon.delete {
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

.progress-percentage {
  font-weight: 700;
  font-size: 1.1rem;
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

.progress-status {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.status-good {
  color: #38a169;
  font-weight: 500;
}

.status-warning {
  color: #d69e2e;
  font-weight: 500;
}

.status-danger {
  color: #e53e3e;
  font-weight: 500;
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
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e2e8f0;
}

.modal-header h3 {
  color: #2d3748;
  font-size: 1.25rem;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #718096;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}

.btn-close:hover {
  background: #f7fafc;
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
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.modal-footer {
  padding: 1.5rem;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

/* Responsive design */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .budget-summary {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .budgets-grid {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}

@media (max-width: 480px) {
  .budget-summary {
    grid-template-columns: 1fr;
  }
  
  .budget-header {
    flex-direction: column;
    gap: 1rem;
  }
  
  .budget-actions {
    align-self: flex-end;
  }
  
  .budget-details {
    grid-template-columns: 1fr;
  }
}
</style>