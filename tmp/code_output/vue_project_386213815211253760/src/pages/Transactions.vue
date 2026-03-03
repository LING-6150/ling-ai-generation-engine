<template>
  <div class="transactions">
    <div class="page-header">
      <h2>Transactions</h2>
      <div class="header-actions">
        <div class="filter-controls">
          <select v-model="selectedFilter" class="filter-select">
            <option value="all">All Transactions</option>
            <option value="income">Income Only</option>
            <option value="expense">Expenses Only</option>
            <option value="food">Food & Dining</option>
            <option value="entertainment">Entertainment</option>
            <option value="transportation">Transportation</option>
          </select>
          <input 
            type="month" 
            v-model="selectedMonth" 
            class="month-select"
          >
        </div>
        <button class="add-btn" @click="showAddModal = true">
          <span>+ Add Transaction</span>
        </button>
      </div>
    </div>

    <div class="transactions-summary">
      <div class="summary-card">
        <h3>Total This Month</h3>
        <p class="summary-amount">${{ formatCurrency(filteredTransactions.reduce((sum, t) => sum + (t.type === 'income' ? t.amount : -t.amount), 0)) }}</p>
      </div>
      <div class="summary-card">
        <h3>Income</h3>
        <p class="summary-amount income">+${{ formatCurrency(filteredTransactions.filter(t => t.type === 'income').reduce((sum, t) => sum + t.amount, 0)) }}</p>
      </div>
      <div class="summary-card">
        <h3>Expenses</h3>
        <p class="summary-amount expense">-${{ formatCurrency(filteredTransactions.filter(t => t.type === 'expense').reduce((sum, t) => sum + t.amount, 0)) }}</p>
      </div>
    </div>

    <div class="transactions-table">
      <div class="table-header">
        <div class="table-cell">Date</div>
        <div class="table-cell">Description</div>
        <div class="table-cell">Category</div>
        <div class="table-cell">Type</div>
        <div class="table-cell">Amount</div>
        <div class="table-cell">Actions</div>
      </div>
      
      <div class="table-body">
        <div v-for="transaction in filteredTransactions" :key="transaction.id" class="table-row">
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
          <div class="table-cell actions">
            <button class="action-btn edit" @click="editTransaction(transaction)">✏️</button>
            <button class="action-btn delete" @click="deleteTransaction(transaction.id)">🗑️</button>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Transaction Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingTransaction ? 'Edit Transaction' : 'Add New Transaction' }}</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>Description</label>
            <input v-model="newTransaction.title" type="text" placeholder="Enter description">
          </div>
          
          <div class="form-group">
            <label>Amount</label>
            <input v-model="newTransaction.amount" type="number" step="0.01" placeholder="0.00">
          </div>
          
          <div class="form-group">
            <label>Type</label>
            <div class="type-selector">
              <button 
                class="type-btn" 
                :class="{ active: newTransaction.type === 'income' }"
                @click="newTransaction.type = 'income'"
              >
                Income
              </button>
              <button 
                class="type-btn" 
                :class="{ active: newTransaction.type === 'expense' }"
                @click="newTransaction.type = 'expense'"
              >
                Expense
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <label>Category</label>
            <select v-model="newTransaction.category">
              <option value="Food">Food & Dining</option>
              <option value="Transportation">Transportation</option>
              <option value="Entertainment">Entertainment</option>
              <option value="Shopping">Shopping</option>
              <option value="Utilities">Utilities</option>
              <option value="Healthcare">Healthcare</option>
              <option value="Education">Education</option>
              <option value="Income">Income</option>
              <option value="Other">Other</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Date</label>
            <input v-model="newTransaction.date" type="date">
          </div>
          
          <div class="form-group">
            <label>Notes (Optional)</label>
            <textarea v-model="newTransaction.notes" placeholder="Add any notes..."></textarea>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="showAddModal = false">Cancel</button>
          <button class="save-btn" @click="saveTransaction">
            {{ editingTransaction ? 'Update' : 'Save' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

// Mock transactions data
const transactions = ref([
  { id: 1, title: 'Salary Deposit', category: 'Income', amount: 3200.00, type: 'income', date: '2024-10-15', notes: 'Monthly salary' },
  { id: 2, title: 'Grocery Shopping', category: 'Food', amount: 125.75, type: 'expense', date: '2024-10-14', notes: 'Weekly groceries' },
  { id: 3, title: 'Netflix Subscription', category: 'Entertainment', amount: 15.99, type: 'expense', date: '2024-10-13', notes: '' },
  { id: 4, title: 'Freelance Work', category: 'Income', amount: 850.00, type: 'income', date: '2024-10-12', notes: 'Web design project' },
  { id: 5, title: 'Gas Station', category: 'Transportation', amount: 45.50, type: 'expense', date: '2024-10-11', notes: '' },
  { id: 6, title: 'Restaurant Dinner', category: 'Food', amount: 68.25, type: 'expense', date: '2024-10-10', notes: 'Birthday celebration' },
  { id: 7, title: 'Electricity Bill', category: 'Utilities', amount: 89.75, type: 'expense', date: '2024-10-09', notes: 'Monthly bill' },
  { id: 8, title: 'Book Purchase', category: 'Education', amount: 24.99, type: 'expense', date: '2024-10-08', notes: 'Programming book' },
  { id: 9, title: 'Investment Dividend', category: 'Income', amount: 150.00, type: 'income', date: '2024-10-07', notes: 'Stock dividend' },
  { id: 10, title: 'Gym Membership', category: 'Healthcare', amount: 29.99, type: 'expense', date: '2024-10-06', notes: 'Monthly fee' }
])

const selectedFilter = ref('all')
const selectedMonth = ref('2024-10')
const showAddModal = ref(false)
const editingTransaction = ref(null)

const newTransaction = ref({
  title: '',
  amount: 0,
  type: 'expense',
  category: 'Food',
  date: new Date().toISOString().split('T')[0],
  notes: ''
})

const filteredTransactions = computed(() => {
  let filtered = [...transactions.value]
  
  // Filter by type/category
  if (selectedFilter.value !== 'all') {
    if (selectedFilter.value === 'income' || selectedFilter.value === 'expense') {
      filtered = filtered.filter(t => t.type === selectedFilter.value)
    } else {
      filtered = filtered.filter(t => t.category.toLowerCase().includes(selectedFilter.value))
    }
  }
  
  // Filter by month
  if (selectedMonth.value) {
    const [year, month] = selectedMonth.value.split('-')
    filtered = filtered.filter(t => {
      const tDate = new Date(t.date)
      return tDate.getFullYear() === parseInt(year) && 
             tDate.getMonth() + 1 === parseInt(month)
    })
  }
  
  // Sort by date (newest first)
  return filtered.sort((a, b) => new Date(b.date) - new Date(a.date))
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
    day: 'numeric',
    year: 'numeric'
  })
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
  if (!newTransaction.value.title || !newTransaction.value.amount) {
    alert('Please fill in all required fields')
    return
  }

  if (editingTransaction.value) {
    // Update existing transaction
    const index = transactions.value.findIndex(t => t.id === editingTransaction.value.id)
    if (index !== -1) {
      transactions.value[index] = {
        ...newTransaction.value,
        id: editingTransaction.value.id
      }
    }
  } else {
    // Add new transaction
    const newId = Math.max(...transactions.value.map(t => t.id), 0) + 1
    transactions.value.push({
      ...newTransaction.value,
      id: newId
    })
  }

  resetForm()
  showAddModal.value = false
}

const resetForm = () => {
  newTransaction.value = {
    title: '',
    amount: 0,
    type: 'expense',
    category: 'Food',
    date: new Date().toISOString().split('T')[0],
    notes: ''
  }
  editingTransaction.value = null
}

onMounted(() => {
  // Set current month as default
  const now = new Date()
  selectedMonth.value = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
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
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-controls {
  display: flex;
  gap: 0.5rem;
}

.filter-select,
.month-select {
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

.transactions-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.summary-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.summary-card h3 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.summary-amount {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.summary-amount.income {
  color: #38a169;
}

.summary-amount.expense {
  color: #e53e3e;
}

.transactions-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
  background: #f7fafc;
  padding: 1rem;
  font-weight: 600;
  color: #4a5568;
  border-bottom: 1px solid #e2e8f0;
}

.table-body {
  max-height: 500px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
  padding: 1rem;
  border-bottom: 1px solid #e2e8f0;
  align-items: center;
  transition: background-color 0.3s;
}

.table-row:hover {
  background: #f7fafc;
}

.table-cell {
  padding: 0.5rem;
}

.category-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.category-badge.food {
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

.category-badge.income {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.category-badge.utilities {
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
}

.category-badge.education {
  background: rgba(56, 178, 172, 0.1);
  color: #38b2ac;
}

.category-badge.healthcare {
  background: rgba(246, 173, 85, 0.1);
  color: #f6ad55;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
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
  font-size: 1.1rem;
}

.amount.income {
  color: #38a169;
}

.amount.expense {
  color: #e53e3e;
}

.actions {
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
  min-height: 100px;
  resize: vertical;
}

.type-selector {
  display: flex;
  gap: 0.5rem;
}

.type-btn {
  flex: 1;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.type-btn.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
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
@media (max-width: 1024px) {
  .table-header,
  .table-row {
    grid-template-columns: 1fr 2fr 1fr 1fr 1fr 1fr;
  }
}

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
  
  .filter-select,
  .month-select {
    flex: 1;
  }
  
  .transactions-summary {
    grid-template-columns: 1fr;
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
  
  .table-cell[data-label="Date"]::before { content: "Date: "; }
  .table-cell[data-label="Description"]::before { content: "Description: "; }
  .table-cell[data-label="Category"]::before { content: "Category: "; }
  .table-cell[data-label="Type"]::before { content: "Type: "; }
  .table-cell[data-label="Amount"]::before { content: "Amount: "; }
  .table-cell[data-label="Actions"]::before { content: "Actions: "; }
}

@media (max-width: 480px) {
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}
</style>