<template>
  <div class="transactions-page">
    <div class="page-header">
      <h2>Transaction History</h2>
      <div class="header-actions">
        <div class="filter-group">
          <select v-model="filterCategory" class="filter-select">
            <option value="">All Categories</option>
            <option value="Food">Food</option>
            <option value="Transport">Transport</option>
            <option value="Housing">Housing</option>
            <option value="Entertainment">Entertainment</option>
            <option value="Shopping">Shopping</option>
            <option value="Utilities">Utilities</option>
            <option value="Salary">Salary</option>
            <option value="Other">Other</option>
          </select>
          <select v-model="filterType" class="filter-select">
            <option value="">All Types</option>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
          </select>
          <input v-model="filterDate" type="date" class="date-input">
        </div>
        <button class="btn btn-primary" @click="showAddTransaction = true">
          <span>+ Add Transaction</span>
        </button>
      </div>
    </div>
    
    <div class="transactions-summary">
      <div class="summary-card">
        <h3>Total Income</h3>
        <p class="summary-value positive">{{ formatCurrency(totalIncome) }}</p>
      </div>
      <div class="summary-card">
        <h3>Total Expenses</h3>
        <p class="summary-value negative">{{ formatCurrency(totalExpenses) }}</p>
      </div>
      <div class="summary-card">
        <h3>Net Balance</h3>
        <p class="summary-value" :class="netBalance >= 0 ? 'positive' : 'negative'">
          {{ formatCurrency(netBalance) }}
        </p>
      </div>
      <div class="summary-card">
        <h3>Transactions</h3>
        <p class="summary-value">{{ filteredTransactions.length }}</p>
      </div>
    </div>
    
    <div class="transactions-table-container">
      <table class="transactions-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Category</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="transaction in filteredTransactions" :key="transaction.id">
            <td class="date-cell">{{ formatDate(transaction.date) }}</td>
            <td class="description-cell">
              <div class="transaction-info">
                <div class="transaction-icon" :style="{ backgroundColor: transaction.color }">
                  {{ transaction.icon }}
                </div>
                <div>
                  <div class="transaction-description">{{ transaction.description }}</div>
                  <div class="transaction-notes" v-if="transaction.notes">{{ transaction.notes }}</div>
                </div>
              </div>
            </td>
            <td class="category-cell">
              <span class="category-badge" :style="{ backgroundColor: transaction.color + '20', color: transaction.color }">
                {{ transaction.category }}
              </span>
            </td>
            <td class="type-cell">
              <span class="type-badge" :class="transaction.type">
                {{ transaction.type === 'income' ? 'Income' : 'Expense' }}
              </span>
            </td>
            <td class="amount-cell" :class="transaction.type">
              {{ transaction.type === 'income' ? '+' : '-' }}{{ formatCurrency(transaction.amount) }}
            </td>
            <td class="actions-cell">
              <button class="btn-icon" @click="editTransaction(transaction)">
                ✏️
              </button>
              <button class="btn-icon delete" @click="deleteTransaction(transaction.id)">
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>
      
      <div v-if="filteredTransactions.length === 0" class="empty-state">
        <div class="empty-icon">📊</div>
        <h3>No transactions found</h3>
        <p>Try adjusting your filters or add a new transaction</p>
        <button class="btn btn-primary" @click="showAddTransaction = true">
          + Add Your First Transaction
        </button>
      </div>
    </div>
    
    <!-- Add/Edit Transaction Modal -->
    <div v-if="showAddTransaction" class="modal-overlay" @click="showAddTransaction = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingTransaction ? 'Edit Transaction' : 'Add New Transaction' }}</h3>
          <button class="btn-close" @click="showAddTransaction = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Description</label>
            <input v-model="newTransaction.description" type="text" placeholder="e.g., Grocery shopping">
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Amount</label>
              <input v-model="newTransaction.amount" type="number" step="0.01" placeholder="0.00">
            </div>
            <div class="form-group">
              <label>Type</label>
              <select v-model="newTransaction.type">
                <option value="expense">Expense</option>
                <option value="income">Income</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Category</label>
              <select v-model="newTransaction.category">
                <option value="Food">Food</option>
                <option value="Transport">Transport</option>
                <option value="Housing">Housing</option>
                <option value="Entertainment">Entertainment</option>
                <option value="Shopping">Shopping</option>
                <option value="Utilities">Utilities</option>
                <option value="Healthcare">Healthcare</option>
                <option value="Education">Education</option>
                <option value="Salary">Salary</option>
                <option value="Other">Other</option>
              </select>
            </div>
            <div class="form-group">
              <label>Date</label>
              <input v-model="newTransaction.date" type="date">
            </div>
          </div>
          <div class="form-group">
            <label>Notes (Optional)</label>
            <textarea v-model="newTransaction.notes" placeholder="Add any notes..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showAddTransaction = false">
            Cancel
          </button>
          <button class="btn btn-primary" @click="saveTransaction">
            {{ editingTransaction ? 'Update' : 'Add' }} Transaction
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const transactions = ref([])
const showAddTransaction = ref(false)
const editingTransaction = ref(null)
const filterCategory = ref('')
const filterType = ref('')
const filterDate = ref('')

const newTransaction = ref({
  description: '',
  amount: 0,
  type: 'expense',
  category: 'Food',
  date: new Date().toISOString().split('T')[0],
  notes: ''
})

// Initialize with sample data
onMounted(() => {
  transactions.value = [
    { 
      id: 1, 
      description: 'Grocery Shopping', 
      amount: 85.50, 
      type: 'expense', 
      category: 'Food', 
      date: '2024-03-15',
      notes: 'Weekly groceries',
      icon: '🛒',
      color: '#764ba2'
    },
    { 
      id: 2, 
      description: 'Monthly Salary', 
      amount: 2425.00, 
      type: 'income', 
      category: 'Salary', 
      date: '2024-03-01',
      notes: 'Regular salary deposit',
      icon: '💰',
      color: '#38a169'
    },
    { 
      id: 3, 
      description: 'Electric Bill', 
      amount: 120.75, 
      type: 'expense', 
      category: 'Utilities', 
      date: '2024-03-10',
      notes: 'Monthly electricity',
      icon: '⚡',
      color: '#667eea'
    },
    { 
      id: 4, 
      description: 'Restaurant Dinner', 
      amount: 68.90, 
      type: 'expense', 
      category: 'Food', 
      date: '2024-03-14',
      notes: 'Date night',
      icon: '🍽️',
      color: '#764ba2'
    },
    { 
      id: 5, 
      description: 'Freelance Work', 
      amount: 850.00, 
      type: 'income', 
      category: 'Salary', 
      date: '2024-03-05',
      notes: 'Web design project',
      icon: '💼',
      color: '#38a169'
    },
    { 
      id: 6, 
      description: 'Gas Station', 
      amount: 45.25, 
      type: 'expense', 
      category: 'Transport', 
      date: '2024-03-12',
      notes: 'Car fuel',
      icon: '⛽',
      color: '#f093fb'
    },
    { 
      id: 7, 
      description: 'Netflix Subscription', 
      amount: 15.99, 
      type: 'expense', 
      category: 'Entertainment', 
      date: '2024-03-01',
      notes: 'Monthly subscription',
      icon: '🎬',
      color: '#f5576c'
    },
    { 
      id: 8, 
      description: 'Clothing Store', 
      amount: 89.99, 
      type: 'expense', 
      category: 'Shopping', 
      date: '2024-03-08',
      notes: 'New shoes',
      icon: '👕',
      color: '#4facfe'
    }
  ]
})

const filteredTransactions = computed(() => {
  return transactions.value.filter(transaction => {
    const matchesCategory = !filterCategory.value || transaction.category === filterCategory.value
    const matchesType = !filterType.value || transaction.type === filterType.value
    const matchesDate = !filterDate.value || transaction.date === filterDate.value
    
    return matchesCategory && matchesType && matchesDate
  }).sort((a, b) => new Date(b.date) - new Date(a.date))
})

const totalIncome = computed(() => {
  return filteredTransactions.value
    .filter(t => t.type === 'income')
    .reduce((sum, t) => sum + t.amount, 0)
})

const totalExpenses = computed(() => {
  return filteredTransactions.value
    .filter(t => t.type === 'expense')
    .reduce((sum, t) => sum + t.amount, 0)
})

const netBalance = computed(() => {
  return totalIncome.value - totalExpenses.value
})

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
  })
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const editTransaction = (transaction) => {
  editingTransaction.value = transaction
  newTransaction.value = { ...transaction }
  showAddTransaction.value = true
}

const saveTransaction = () => {
  if (!newTransaction.value.description || newTransaction.value.amount <= 0) {
    alert('Please fill in all required fields')
    return
  }
  
  const icon = getIconForCategory(newTransaction.value.category)
  const color = getColorForCategory(newTransaction.value.category)
  
  if (editingTransaction.value) {
    // Update existing transaction
    const index = transactions.value.findIndex(t => t.id === editingTransaction.value.id)
    if (index !== -1) {
      transactions.value[index] = {
        ...newTransaction.value,
        id: editingTransaction.value.id,
        icon,
        color
      }
    }
  } else {
    // Add new transaction
    const newId = Math.max(...transactions.value.map(t => t.id), 0) + 1
    transactions.value.push({
      ...newTransaction.value,
      id: newId,
      icon,
      color
    })
  }
  
  resetForm()
  showAddTransaction.value = false
}

const deleteTransaction = (id) => {
  if (confirm('Are you sure you want to delete this transaction?')) {
    transactions.value = transactions.value.filter(t => t.id !== id)
  }
}

const resetForm = () => {
  newTransaction.value = {
    description: '',
    amount: 0,
    type: 'expense',
    category: 'Food',
    date: new Date().toISOString().split('T')[0],
    notes: ''
  }
  editingTransaction.value = null
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
    'Salary': '💰',
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
    'Salary': '#38a169',
    'Other': '#718096'
  }
  return colors[category] || '#718096'
}
</script>

<style scoped>
.transactions-page {
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-select,
.date-input {
  padding: 0.5rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 0.9rem;
  min-width: 120px;
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

.transactions-summary {
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

.transactions-table-container {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.transactions-table {
  width: 100%;
  border-collapse: collapse;
}

.transactions-table th {
  background: #f7fafc;
  padding: 1rem;
  text-align: left;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid #e2e8f0;
}

.transactions-table td {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
}

.transactions-table tr:last-child td {
  border-bottom: none;
}

.transactions-table tr:hover {
  background: #f7fafc;
}

.transaction-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.transaction-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1rem;
  color: white;
  flex-shrink: 0;
}

.transaction-description {
  font-weight: 500;
  color: #2d3748;
}

.transaction-notes {
  font-size: 0.85rem;
  color: #718096;
  margin-top: 0.25rem;
}

.category-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge.income {
  background: #c6f6d5;
  color: #22543d;
}

.type-badge.expense {
  background: #fed7d7;
  color: #742a2a;
}

.amount-cell {
  font-weight: 600;
  font-size: 1rem;
}

.amount-cell.income {
  color: #38a169;
}

.amount-cell.expense {
  color: #e53e3e;
}

.actions-cell {
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

.empty-state {
  padding: 4rem 2rem;
  text-align: center;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.empty-state p {
  color: #718096;
  margin-bottom: 1.5rem;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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
  transition: border-color 0.2s;
}

.form-group textarea {
  min-height: 80px;
  resize: vertical;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
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
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-group {
    flex-direction: column;
  }
  
  .transactions-summary {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .transactions-table {
    display: block;
    overflow-x: auto;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}

@media (max-width: 480px) {
  .transactions-summary {
    grid-template-columns: 1fr;
  }
  
  .transactions-table th,
  .transactions-table td {
    padding: 0.75rem 0.5rem;
  }
  
  .transaction-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .actions-cell {
    flex-direction: column;
    gap: 0.25rem;
  }
}
</style>