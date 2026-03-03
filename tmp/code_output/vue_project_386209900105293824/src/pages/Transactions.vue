<template>
  <div class="transactions">
    <div class="page-header">
      <h2>Transaction History</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddModal = true">
          <span class="btn-icon">+</span> Add Transaction
        </button>
        <div class="filter-controls">
          <select v-model="selectedCategory" class="filter-select">
            <option value="">All Categories</option>
            <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
          </select>
          <select v-model="selectedType" class="filter-select">
            <option value="">All Types</option>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
          </select>
        </div>
      </div>
    </div>

    <div class="transactions-summary">
      <div class="summary-card">
        <h4>Total Income</h4>
        <p class="summary-amount income">+${{ totalIncome.toFixed(2) }}</p>
      </div>
      <div class="summary-card">
        <h4>Total Expenses</h4>
        <p class="summary-amount expense">-${{ totalExpenses.toFixed(2) }}</p>
      </div>
      <div class="summary-card">
        <h4>Net Balance</h4>
        <p class="summary-amount" :class="netBalance >= 0 ? 'income' : 'expense'">
          {{ netBalance >= 0 ? '+' : '' }}${{ netBalance.toFixed(2) }}
        </p>
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
            <td>{{ formatDate(transaction.date) }}</td>
            <td>{{ transaction.description }}</td>
            <td>
              <span class="category-badge" :style="{ backgroundColor: getCategoryColor(transaction.category) }">
                {{ transaction.category }}
              </span>
            </td>
            <td>
              <span class="type-badge" :class="transaction.type">
                {{ transaction.type === 'income' ? 'Income' : 'Expense' }}
              </span>
            </td>
            <td :class="transaction.type === 'income' ? 'income' : 'expense'">
              {{ transaction.type === 'income' ? '+' : '-' }}${{ transaction.amount.toFixed(2) }}
            </td>
            <td>
              <button class="btn-icon-small" @click="editTransaction(transaction)">
                ✏️
              </button>
              <button class="btn-icon-small delete" @click="deleteTransaction(transaction.id)">
                🗑️
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Add/Edit Transaction Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingTransaction ? 'Edit Transaction' : 'Add New Transaction' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveTransaction">
            <div class="form-group">
              <label for="description">Description</label>
              <input
                id="description"
                v-model="newTransaction.description"
                type="text"
                required
                placeholder="Enter transaction description"
              />
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="amount">Amount ($)</label>
                <input
                  id="amount"
                  v-model="newTransaction.amount"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                />
              </div>
              
              <div class="form-group">
                <label for="type">Type</label>
                <select id="type" v-model="newTransaction.type" required>
                  <option value="income">Income</option>
                  <option value="expense">Expense</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="category">Category</label>
                <select id="category" v-model="newTransaction.category" required>
                  <option value="">Select Category</option>
                  <option v-for="category in categories" :key="category" :value="category">{{ category }}</option>
                </select>
              </div>
              
              <div class="form-group">
                <label for="date">Date</label>
                <input
                  id="date"
                  v-model="newTransaction.date"
                  type="date"
                  required
                />
              </div>
            </div>
            
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
              <button type="submit" class="btn btn-primary">
                {{ editingTransaction ? 'Update' : 'Add' }} Transaction
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const transactions = ref([
  { id: 1, description: 'Salary Deposit', category: 'Income', amount: 2500, type: 'income', date: '2024-06-15' },
  { id: 2, description: 'Grocery Shopping', category: 'Food', amount: 125.75, type: 'expense', date: '2024-06-14' },
  { id: 3, description: 'Electricity Bill', category: 'Utilities', amount: 89.50, type: 'expense', date: '2024-06-13' },
  { id: 4, description: 'Freelance Work', category: 'Income', amount: 850, type: 'income', date: '2024-06-12' },
  { id: 5, description: 'Restaurant Dinner', category: 'Dining', amount: 65.25, type: 'expense', date: '2024-06-11' },
  { id: 6, description: 'Gas Station', category: 'Transportation', amount: 45.00, type: 'expense', date: '2024-06-10' },
  { id: 7, description: 'Netflix Subscription', category: 'Entertainment', amount: 15.99, type: 'expense', date: '2024-06-09' },
  { id: 8, description: 'Bonus Payment', category: 'Income', amount: 500, type: 'income', date: '2024-06-08' },
  { id: 9, description: 'Gym Membership', category: 'Health', amount: 29.99, type: 'expense', date: '2024-06-07' },
  { id: 10, description: 'Online Course', category: 'Education', amount: 199.99, type: 'expense', date: '2024-06-06' }
])

const categories = ref(['Income', 'Food', 'Utilities', 'Dining', 'Transportation', 'Entertainment', 'Health', 'Education', 'Shopping', 'Housing'])
const selectedCategory = ref('')
const selectedType = ref('')
const showAddModal = ref(false)
const editingTransaction = ref(null)

const newTransaction = ref({
  description: '',
  amount: '',
  type: 'expense',
  category: '',
  date: new Date().toISOString().split('T')[0]
})

const filteredTransactions = computed(() => {
  return transactions.value.filter(transaction => {
    const matchesCategory = !selectedCategory.value || transaction.category === selectedCategory.value
    const matchesType = !selectedType.value || transaction.type === selectedType.value
    return matchesCategory && matchesType
  }).sort((a, b) => new Date(b.date) - new Date(a.date))
})

const totalIncome = computed(() => {
  return transactions.value
    .filter(t => t.type === 'income')
    .reduce((sum, t) => sum + t.amount, 0)
})

const totalExpenses = computed(() => {
  return transactions.value
    .filter(t => t.type === 'expense')
    .reduce((sum, t) => sum + t.amount, 0)
})

const netBalance = computed(() => totalIncome.value - totalExpenses.value)

const getCategoryColor = (category) => {
  const colors = {
    'Income': '#48bb78',
    'Food': '#ed8936',
    'Utilities': '#4299e1',
    'Dining': '#e53e3e',
    'Transportation': '#805ad5',
    'Entertainment': '#d53f8c',
    'Health': '#38a169',
    'Education': '#3182ce',
    'Shopping': '#d69e2e',
    'Housing': '#4a5568'
  }
  return colors[category] || '#718096'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

const editTransaction = (transaction) => {
  editingTransaction.value = transaction
  newTransaction.value = { ...transaction }
  showAddModal.value = true
}

const deleteTransaction = (id) => {
  if (confirm('Are you sure you want to delete this transaction?')) {
    transactions.value = transactions.value.filter(t => t.id !== id)
  }
}

const saveTransaction = () => {
  if (editingTransaction.value) {
    // Update existing transaction
    const index = transactions.value.findIndex(t => t.id === editingTransaction.value.id)
    if (index !== -1) {
      transactions.value[index] = { ...newTransaction.value, id: editingTransaction.value.id }
    }
  } else {
    // Add new transaction
    const newId = Math.max(...transactions.value.map(t => t.id), 0) + 1
    transactions.value.push({
      ...newTransaction.value,
      id: newId,
      amount: parseFloat(newTransaction.value.amount)
    })
  }
  
  closeModal()
}

const closeModal = () => {
  showAddModal.value = false
  editingTransaction.value = null
  newTransaction.value = {
    description: '',
    amount: '',
    type: 'expense',
    category: '',
    date: new Date().toISOString().split('T')[0]
  }
}

onMounted(() => {
  // Initialize with today's date
  newTransaction.value.date = new Date().toISOString().split('T')[0]
})
</script>

<style scoped>
.transactions {
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

.filter-controls {
  display: flex;
  gap: 0.75rem;
}

.filter-select {
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: white;
  color: #4a5568;
  font-size: 0.9rem;
  min-width: 150px;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.transactions-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.summary-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.summary-card h4 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.summary-amount {
  font-size: 1.8rem;
  font-weight: 700;
}

.summary-amount.income {
  color: #38a169;
}

.summary-amount.expense {
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
  border-bottom: 2px solid #e2e8f0;
}

.transactions-table td {
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  color: #2d3748;
}

.transactions-table tbody tr:hover {
  background: #f7fafc;
}

.category-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  color: white;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge.income {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.type-badge.expense {
  background: rgba(245, 101, 101, 0.1);
  color: #e53e3e;
}

.btn-icon-small {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.25rem;
  margin: 0 0.25rem;
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
.form-group select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
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

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-controls {
    flex-direction: column;
  }
  
  .filter-select {
    min-width: auto;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .transactions-table {
    display: block;
    overflow-x: auto;
  }
}

@media (max-width: 480px) {
  .transactions-summary {
    grid-template-columns: 1fr;
  }
  
  .modal {
    margin: 1rem;
  }
}
</style>