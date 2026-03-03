<template>
  <div class="goals">
    <div class="page-header">
      <h2>Financial Goals</h2>
      <div class="header-actions">
        <div class="filter-controls">
          <select v-model="selectedStatus" class="filter-select">
            <option value="all">All Goals</option>
            <option value="active">Active</option>
            <option value="completed">Completed</option>
            <option value="behind">Behind Schedule</option>
          </select>
        </div>
        <button class="add-btn" @click="showAddModal = true">
          <span>+ New Goal</span>
        </button>
      </div>
    </div>

    <div class="goals-summary">
      <div class="summary-card">
        <h3>Total Goals</h3>
        <p class="summary-number">{{ totalGoals }}</p>
      </div>
      <div class="summary-card">
        <h3>Completed</h3>
        <p class="summary-number completed">{{ completedGoals }}</p>
      </div>
      <div class="summary-card">
        <h3>In Progress</h3>
        <p class="summary-number progress">{{ activeGoals }}</p>
      </div>
      <div class="summary-card">
        <h3>Success Rate</h3>
        <p class="summary-number">{{ successRate }}%</p>
      </div>
    </div>

    <div class="goals-grid">
      <div v-for="goal in filteredGoals" :key="goal.id" class="goal-card">
        <div class="goal-header">
          <div class="goal-icon" :class="goal.type">
            {{ getGoalIcon(goal.type) }}
          </div>
          <div class="goal-info">
            <h3>{{ goal.title }}</h3>
            <p class="goal-description">{{ goal.description }}</p>
          </div>
          <div class="goal-actions">
            <button class="action-btn edit" @click="editGoal(goal)">✏️</button>
            <button class="action-btn delete" @click="deleteGoal(goal.id)">🗑️</button>
          </div>
        </div>

        <div class="goal-progress">
          <div class="progress-info">
            <span class="progress-text">${{ formatCurrency(goal.current) }} of ${{ formatCurrency(goal.target) }}</span>
            <span class="progress-percentage">{{ goal.percentage }}%</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: goal.percentage + '%' }"
              :class="{ completed: goal.percentage >= 100 }"
            ></div>
          </div>
          <div class="progress-details">
            <span class="remaining">${{ formatCurrency(goal.remaining) }} to go</span>
            <span class="deadline">{{ formatDate(goal.deadline) }}</span>
          </div>
        </div>

        <div class="goal-metrics">
          <div class="metric">
            <span class="metric-label">Monthly Target</span>
            <span class="metric-value">${{ formatCurrency(goal.monthlyTarget) }}</span>
          </div>
          <div class="metric">
            <span class="metric-label">Days Left</span>
            <span class="metric-value" :class="{ warning: goal.daysLeft < 30 }">{{ goal.daysLeft }}</span>
          </div>
          <div class="metric">
            <span class="metric-label">Status</span>
            <span class="metric-value status" :class="goal.status">{{ goal.status }}</span>
          </div>
        </div>

        <div class="goal-actions-full">
          <button class="action-btn add-funds" @click="addFunds(goal)">
            <span>+ Add Funds</span>
          </button>
          <button class="action-btn view-details" @click="viewGoalDetails(goal)">
            <span>View Details</span>
          </button>
        </div>
      </div>
    </div>

    <!-- Add/Edit Goal Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingGoal ? 'Edit Goal' : 'Create New Goal' }}</h3>
          <button class="close-btn" @click="showAddModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>Goal Title</label>
            <input v-model="newGoal.title" type="text" placeholder="e.g., Emergency Fund, Vacation, New Car">
          </div>
          
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="newGoal.description" placeholder="Describe your goal..."></textarea>
          </div>
          
          <div class="form-group">
            <label>Goal Type</label>
            <select v-model="newGoal.type">
              <option value="savings">Savings Goal</option>
              <option value="debt">Debt Repayment</option>
              <option value="investment">Investment</option>
              <option value="purchase">Major Purchase</option>
              <option value="emergency">Emergency Fund</option>
              <option value="retirement">Retirement</option>
            </select>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Target Amount</label>
              <div class="amount-input">
                <span class="currency">$</span>
                <input v-model="newGoal.target" type="number" step="0.01" placeholder="0.00">
              </div>
            </div>
            
            <div class="form-group">
              <label>Current Amount</label>
              <div class="amount-input">
                <span class="currency">$</span>
                <input v-model="newGoal.current" type="number" step="0.01" placeholder="0.00">
              </div>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>Start Date</label>
              <input v-model="newGoal.startDate" type="date">
            </div>
            
            <div class="form-group">
              <label>Target Date</label>
              <input v-model="newGoal.deadline" type="date">
            </div>
          </div>
          
          <div class="form-group">
            <label>Priority</label>
            <div class="priority-selector">
              <button 
                v-for="priority in priorities" 
                :key="priority.value"
                class="priority-btn" 
                :class="{ active: newGoal.priority === priority.value }"
                @click="newGoal.priority = priority.value"
              >
                {{ priority.label }}
              </button>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="showAddModal = false">Cancel</button>
          <button class="save-btn" @click="saveGoal">
            {{ editingGoal ? 'Update' : 'Create' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Add Funds Modal -->
    <div v-if="showFundsModal" class="modal-overlay" @click="showFundsModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Add Funds to "{{ selectedGoal?.title }}"</h3>
          <button class="close-btn" @click="showFundsModal = false">×</button>
        </div>
        
        <div class="modal-body">
          <div class="form-group">
            <label>Amount to Add</label>
            <div class="amount-input">
              <span class="currency">$</span>
              <input v-model="fundsAmount" type="number" step="0.01" placeholder="0.00">
            </div>
          </div>
          
          <div class="form-group">
            <label>Source</label>
            <select v-model="fundsSource">
              <option value="savings">Savings Account</option>
              <option value="checking">Checking Account</option>
              <option value="income">Monthly Income</option>
              <option value="bonus">Bonus/Extra Income</option>
              <option value="other">Other Source</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>Date</label>
            <input v-model="fundsDate" type="date">
          </div>
          
          <div class="form-group">
            <label>Notes (Optional)</label>
            <textarea v-model="fundsNotes" placeholder="Add any notes..."></textarea>
          </div>
          
          <div class="funds-preview">
            <h4>Preview</h4>
            <div class="preview-info">
              <div class="preview-item">
                <span>Current:</span>
                <span>${{ formatCurrency(selectedGoal?.current || 0) }}</span>
              </div>
              <div class="preview-item">
                <span>Adding:</span>
                <span>+${{ formatCurrency(fundsAmount) }}</span>
              </div>
              <div class="preview-item total">
                <span>New Total:</span>
                <span>${{ formatCurrency((selectedGoal?.current || 0) + fundsAmount) }}</span>
              </div>
              <div class="preview-item">
                <span>New Progress:</span>
                <span>{{ Math.min(100, Math.round(((selectedGoal?.current || 0) + fundsAmount) / (selectedGoal?.target || 1) * 100)) }}%</span>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="cancel-btn" @click="showFundsModal = false">Cancel</button>
          <button class="save-btn" @click="saveFunds">
            Add Funds
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// Mock goals data
const goals = ref([
  {
    id: 1,
    title: 'Emergency Fund',
    description: '3 months of living expenses',
    type: 'emergency',
    target: 15000,
    current: 8750,
    startDate: '2024-01-01',
    deadline: '2024-12-31',
    priority: 'high',
    status: 'active'
  },
  {
    id: 2,
    title: 'New Car Down Payment',
    description: 'Save for a new car purchase',
    type: 'purchase',
    target: 10000,
    current: 4200,
    startDate: '2024-03-01',
    deadline: '2025-03-01',
    priority: 'medium',
    status: 'active'
  },
  {
    id: 3,
    title: 'Vacation to Japan',
    description: 'Two-week trip to Tokyo and Kyoto',
    type: 'savings',
    target: 5000,
    current: 5000,
    startDate: '2023-06-01',
    deadline: '2024-06-01',
    priority: 'medium',
    status: 'completed'
  },
  {
    id: 4,
    title: 'Credit Card Debt',
    description: 'Pay off high-interest credit card',
    type: 'debt',
    target: 7500,
    current: 2500,
    startDate: '2024-07-01',
    deadline: '2025-01-01',
    priority: 'high',
    status: 'behind'
  },
  {
    id: 5,
    title: 'Home Renovation',
    description: 'Kitchen and bathroom remodel',
    type: 'purchase',
    target: 25000,
    current: 8500,
    startDate: '2024-05-01',
    deadline: '2025-12-01',
    priority: 'low',
    status: 'active'
  },
  {
    id: 6,
    title: 'Retirement Contribution',
    description: 'Max out IRA for the year',
    type: 'retirement',
    target: 6500,
    current: 3250,
    startDate: '2024-01-01',
    deadline: '2024-12-31',
    priority: 'high',
    status: 'active'
  }
])

const selectedStatus = ref('all')
const showAddModal = ref(false)
const showFundsModal = ref(false)
const editingGoal = ref(null)
const selectedGoal = ref(null)

const newGoal = ref({
  title: '',
  description: '',
  type: 'savings',
  target: 0,
  current: 0,
  startDate: new Date().toISOString().split('T')[0],
  deadline: new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split('T')[0],
  priority: 'medium'
})

const fundsAmount = ref(0)
const fundsSource = ref('savings')
const fundsDate = ref(new Date().toISOString().split('T')[0])
const fundsNotes = ref('')

const priorities = [
  { value: 'high', label: 'High' },
  { value: 'medium', label: 'Medium' },
  { value: 'low', label: 'Low' }
]

const filteredGoals = computed(() => {
  let filtered = goals.value.map(goal => {
    const deadline = new Date(goal.deadline)
    const today = new Date()
    const daysLeft = Math.ceil((deadline - today) / (1000 * 60 * 60 * 24))
    const remaining = Math.max(0, goal.target - goal.current)
    const percentage = Math.min(100, Math.round((goal.current / goal.target) * 100))
    const monthlyTarget = goal.target / 12 // Simplified calculation
    
    return {
      ...goal,
      daysLeft,
      remaining,
      percentage,
      monthlyTarget
    }
  })
  
  if (selectedStatus.value !== 'all') {
    filtered = filtered.filter(goal => goal.status === selectedStatus.value)
  }
  
  return filtered.sort((a, b) => {
    // Sort by priority: high > medium > low
    const priorityOrder = { high: 1, medium: 2, low: 3 }
    return priorityOrder[a.priority] - priorityOrder[b.priority] || a.daysLeft - b.daysLeft
  })
})

const totalGoals = computed(() => goals.value.length)
const completedGoals = computed(() => goals.value.filter(g => g.status === 'completed').length)
const activeGoals = computed(() => goals.value.filter(g => g.status === 'active').length)
const successRate = computed(() => {
  const completed = goals.value.filter(g => g.status === 'completed').length
  const total = goals.value.length
  return total > 0 ? Math.round((completed / total) * 100) : 0
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

const getGoalIcon = (type) => {
  const icons = {
    savings: '💰',
    debt: '💳',
    investment: '📈',
    purchase: '🛒',
    emergency: '🚨',
    retirement: '🏖️'
  }
  return icons[type] || '🎯'
}

const editGoal = (goal) => {
  editingGoal.value = goal
  newGoal.value = { ...goal }
  showAddModal.value = true
}

const deleteGoal = (id) => {
  if (confirm('Are you sure you want to delete this goal?')) {
    goals.value = goals.value.filter(g => g.id !== id)
  }
}

const addFunds = (goal) => {
  selectedGoal.value = goal
  fundsAmount.value = 0
  fundsSource.value = 'savings'
  fundsDate.value = new Date().toISOString().split('T')[0]
  fundsNotes.value = ''
  showFundsModal.value = true
}

const viewGoalDetails = (goal) => {
  alert(`Goal Details:\n\n${goal.title}\n${goal.description}\n\nProgress: ${goal.percentage}%\nTarget: $${formatCurrency(goal.target)}\nCurrent: $${formatCurrency(goal.current)}\nRemaining: $${formatCurrency(goal.remaining)}\nDeadline: ${formatDate(goal.deadline)}`)
}

const saveGoal = () => {
  if (!newGoal.value.title || !newGoal.value.target) {
    alert('Please fill in all required fields')
    return
  }

  const percentage = Math.min(100, Math.round((newGoal.value.current / newGoal.value.target) * 100))
  const status = percentage >= 100 ? 'completed' : 
                 new Date(newGoal.value.deadline) < new Date() ? 'behind' : 'active'

  if (editingGoal.value) {
    // Update existing goal
    const index = goals.value.findIndex(g => g.id === editingGoal.value.id)
    if (index !== -1) {
      goals.value[index] = {
        ...newGoal.value,
        id: editingGoal.value.id,
        status
      }
    }
  } else {
    // Add new goal
    const newId = Math.max(...goals.value.map(g => g.id), 0) + 1
    goals.value.push({
      ...newGoal.value,
      id: newId,
      status
    })
  }

  resetForm()
  showAddModal.value = false
}

const saveFunds = () => {
  if (!fundsAmount.value || fundsAmount.value <= 0) {
    alert('Please enter a valid amount')
    return
  }

  const index = goals.value.findIndex(g => g.id === selectedGoal.value.id)
  if (index !== -1) {
    goals.value[index].current += fundsAmount.value
    
    // Update status if completed
    if (goals.value[index].current >= goals.value[index].target) {
      goals.value[index].status = 'completed'
    }
    
    alert(`Successfully added $${formatCurrency(fundsAmount.value)} to "${selectedGoal.value.title}"`)
    showFundsModal.value = false
    selectedGoal.value = null
  }
}

const resetForm = () => {
  newGoal.value = {
    title: '',
    description: '',
    type: 'savings',
    target: 0,
    current: 0,
    startDate: new Date().toISOString().split('T')[0],
    deadline: new Date(new Date().setFullYear(new Date().getFullYear() + 1)).toISOString().split('T')[0],
    priority: 'medium'
  }
  editingGoal.value = null
}
</script>

<style scoped>
.goals {
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

.filter-select {
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

.goals-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.summary-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.summary-card h3 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.summary-number {
  font-size: 1.5rem;
  font-weight: 700;
  color: #2d3748;
}

.summary-number.completed {
  color: #38a169;
}

.summary-number.progress {
  color: #667eea;
}

.goals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.goal-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
}

.goal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.goal-header {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.goal-icon {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  flex-shrink: 0;
}

.goal-icon.savings { background: rgba(72, 187, 120, 0.1); color: #38a169; }
.goal-icon.debt { background: rgba(245, 101, 101, 0.1); color: #e53e3e; }
.goal-icon.investment { background: rgba(102, 126, 234, 0.1); color: #667eea; }
.goal-icon.purchase { background: rgba(237, 137, 54, 0.1); color: #ed8936; }
.goal-icon.emergency { background: rgba(246, 173, 85, 0.1); color: #f6ad55; }
.goal-icon.retirement { background: rgba(159, 122, 234, 0.1); color: #9f7aea; }

.goal-info {
  flex: 1;
}

.goal-info h3 {
  font-size: 1.1rem;
  color: #2d3748;
  margin-bottom: 0.25rem;
}

.goal-description {
  font-size: 0.9rem;
  color: #718096;
  line-height: 1.4;
}

.goal-actions {
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

.goal-progress {
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
  background: linear-gradient(90deg, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.3s;
}

.progress-fill.completed {
  background: linear-gradient(90deg, #48bb78, #38a169);
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

.deadline {
  color: #718096;
}

.goal-metrics {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.metric {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.metric-label {
  font-size: 0.75rem;
  color: #718096;
  margin-bottom: 0.25rem;
}

.metric-value {
  font-weight: 600;
  color: #2d3748;
}

.metric-value.warning {
  color: #ed8936;
}

.metric-value.status {
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 500;
}

.metric-value.status.active {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.metric-value.status.completed {
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.metric-value.status.behind {
  background: rgba(245, 101, 101, 0.1);
  color: #f56565;
}

.goal-actions-full {
  display: flex;
  gap: 0.75rem;
}

.goal-actions-full .action-btn {
  flex: 1;
  padding: 0.75rem;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.action-btn.add-funds {
  background: #667eea;
  color: white;
}

.action-btn.add-funds:hover {
  background: #5a67d8;
}

.action-btn.view-details {
  background: #e2e8f0;
  color: #4a5568;
}

.action-btn.view-details:hover {
  background: #cbd5e0;
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
  max-width: 600px;
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

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
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

.priority-selector {
  display: flex;
  gap: 0.5rem;
}

.priority-btn {
  flex: 1;
  padding: 0.75rem;
  border: 2px solid #e2e8f0;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s;
}

.priority-btn.active {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  color: #667eea;
}

.funds-preview {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.funds-preview h4 {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 0.75rem;
  font-weight: 600;
}

.preview-info {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.preview-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.preview-item span:first-child {
  color: #718096;
}

.preview-item span:last-child {
  font-weight: 500;
  color: #2d3748;
}

.preview-item.total {
  padding-top: 0.5rem;
  border-top: 1px solid #e2e8f0;
  font-weight: 600;
  font-size: 1rem;
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
  
  .filter-select {
    width: 100%;
  }
  
  .goals-summary {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goals-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .goal-metrics {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .goals-summary {
    grid-template-columns: 1fr;
  }
  
  .goal-metrics {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}
</style>