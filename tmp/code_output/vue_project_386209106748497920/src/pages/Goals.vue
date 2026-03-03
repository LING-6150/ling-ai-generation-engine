<template>
  <div class="goals-page">
    <div class="page-header">
      <h2>Financial Goals</h2>
      <div class="header-actions">
        <button class="btn btn-primary" @click="showAddGoal = true">
          <span>+ New Goal</span>
        </button>
      </div>
    </div>
    
    <div class="goals-summary">
      <div class="summary-card">
        <h3>Total Goals Value</h3>
        <p class="summary-value">{{ formatCurrency(totalGoalsValue) }}</p>
      </div>
      <div class="summary-card">
        <h3>Total Saved</h3>
        <p class="summary-value">{{ formatCurrency(totalSaved) }}</p>
      </div>
      <div class="summary-card">
        <h3>Overall Progress</h3>
        <p class="summary-value">{{ overallProgress }}%</p>
      </div>
      <div class="summary-card">
        <h3>Goals Completed</h3>
        <p class="summary-value">{{ completedGoalsCount }} / {{ goals.length }}</p>
      </div>
    </div>
    
    <div class="goals-grid">
      <div v-for="goal in goals" :key="goal.id" class="goal-card">
        <div class="goal-header">
          <div class="goal-icon" :style="{ backgroundColor: goal.color }">
            {{ goal.icon }}
          </div>
          <div class="goal-info">
            <h3>{{ goal.name }}</h3>
            <p class="goal-description">{{ goal.description }}</p>
          </div>
          <div class="goal-actions">
            <button class="btn-icon" @click="editGoal(goal)">
              ✏️
            </button>
            <button class="btn-icon delete" @click="deleteGoal(goal.id)">
              🗑️
            </button>
          </div>
        </div>
        
        <div class="goal-progress">
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ 
                width: Math.min(goal.progress, 100) + '%',
                backgroundColor: goal.color
              }"
            ></div>
          </div>
          <div class="progress-details">
            <div class="progress-text">
              <span class="progress-percentage">{{ goal.progress }}%</span>
              <span class="progress-amount">{{ formatCurrency(goal.current) }} / {{ formatCurrency(goal.target) }}</span>
            </div>
            <div class="progress-time">
              <span class="time-label">Target Date:</span>
              <span class="time-value">{{ goal.deadline }}</span>
            </div>
          </div>
        </div>
        
        <div class="goal-stats">
          <div class="stat-item">
            <span class="stat-label">Monthly Target</span>
            <span class="stat-value">{{ formatCurrency(goal.monthlyTarget) }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Time Remaining</span>
            <span class="stat-value">{{ goal.timeRemaining }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">Status</span>
            <span class="stat-status" :class="getStatusClass(goal)">
              {{ getStatusText(goal) }}
            </span>
          </div>
        </div>
        
        <div class="goal-actions-full">
          <button class="btn btn-small" @click="addToGoal(goal)">
            + Add Funds
          </button>
          <button class="btn btn-small btn-secondary" @click="viewGoalDetails(goal)">
            View Details
          </button>
        </div>
      </div>
    </div>
    
    <!-- Add/Edit Goal Modal -->
    <div v-if="showAddGoal" class="modal-overlay" @click="showAddGoal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ editingGoal ? 'Edit Goal' : 'Create New Goal' }}</h3>
          <button class="btn-close" @click="showAddGoal = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Goal Name</label>
            <input v-model="newGoal.name" type="text" placeholder="e.g., Emergency Fund">
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea v-model="newGoal.description" placeholder="Describe your goal..."></textarea>
          </div>
          <div class="form-group">
            <label>Target Amount</label>
            <input v-model="newGoal.target" type="number" step="0.01" placeholder="0.00">
          </div>
          <div class="form-group">
            <label>Current Amount</label>
            <input v-model="newGoal.current" type="number" step="0.01" placeholder="0.00">
          </div>
          <div class="form-group">
            <label>Target Date</label>
            <input v-model="newGoal.deadline" type="date">
          </div>
          <div class="form-group">
            <label>Category</label>
            <select v-model="newGoal.category">
              <option value="Emergency Fund">Emergency Fund</option>
              <option value="Vacation">Vacation</option>
              <option value="Education">Education</option>
              <option value="Home">Home</option>
              <option value="Vehicle">Vehicle</option>
              <option value="Retirement">Retirement</option>
              <option value="Investment">Investment</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showAddGoal = false">
            Cancel
          </button>
          <button class="btn btn-primary" @click="saveGoal">
            {{ editingGoal ? 'Update' : 'Create' }} Goal
          </button>
        </div>
      </div>
    </div>
    
    <!-- Add Funds Modal -->
    <div v-if="showAddFunds" class="modal-overlay" @click="showAddFunds = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>Add Funds to {{ selectedGoal?.name }}</h3>
          <button class="btn-close" @click="showAddFunds = false">×</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>Amount to Add</label>
            <input v-model="fundsToAdd" type="number" step="0.01" placeholder="0.00">
          </div>
          <div class="form-group">
            <label>Source</label>
            <select v-model="fundsSource">
              <option value="Savings">Savings Account</option>
              <option value="Checking">Checking Account</option>
              <option value="Cash">Cash</option>
              <option value="Income">Monthly Income</option>
              <option value="Other">Other Source</option>
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
        </div>
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="showAddFunds = false">
            Cancel
          </button>
          <button class="btn btn-primary" @click="confirmAddFunds">
            Add Funds
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const goals = ref([])
const showAddGoal = ref(false)
const showAddFunds = ref(false)
const editingGoal = ref(null)
const selectedGoal = ref(null)

const newGoal = ref({
  name: '',
  description: '',
  target: 0,
  current: 0,
  deadline: '',
  category: 'Emergency Fund'
})

const fundsToAdd = ref(0)
const fundsSource = ref('Savings')
const fundsDate = ref(new Date().toISOString().split('T')[0])
const fundsNotes = ref('')

// Initialize with sample data
onMounted(() => {
  goals.value = [
    { 
      id: 1, 
      name: 'Emergency Fund', 
      description: '6 months of living expenses for unexpected situations',
      target: 10000, 
      current: 7500, 
      deadline: '2024-12-31',
      category: 'Emergency Fund',
      icon: '🛡️',
      color: '#667eea',
      monthlyTarget: 416.67,
      timeRemaining: '9 months'
    },
    { 
      id: 2, 
      name: 'Europe Vacation', 
      description: 'Two-week trip to Italy and France',
      target: 5000, 
      current: 3200, 
      deadline: '2024-08-15',
      category: 'Vacation',
      icon: '✈️',
      color: '#f5576c',
      monthlyTarget: 833.33,
      timeRemaining: '7 months'
    },
    { 
      id: 3, 
      name: 'New Laptop', 
      description: 'MacBook Pro for work and creative projects',
      target: 2000, 
      current: 1200, 
      deadline: '2024-06-30',
      category: 'Education',
      icon: '💻',
      color: '#4facfe',
      monthlyTarget: 400,
      timeRemaining: '5 months'
    },
    { 
      id: 4, 
      name: 'Car Down Payment', 
      description: '20% down payment for a new hybrid vehicle',
      target: 6000, 
      current: 4500, 
      deadline: '2024-10-31',
      category: 'Vehicle',
      icon: '🚗',
      color: '#38a169',
      monthlyTarget: 750,
      timeRemaining: '8 months'
    },
    { 
      id: 5, 
      name: 'Home Renovation', 
      description: 'Kitchen and bathroom remodeling project',
      target: 15000, 
      current: 5200, 
      deadline: '2025-03-31',
      category: 'Home',
      icon: '🏠',
      color: '#f093fb',
      monthlyTarget: 937.5,
      timeRemaining: '14 months'
    },
    { 
      id: 6, 
      name: 'Retirement Boost', 
      description: 'Additional contribution to retirement account',
      target: 8000, 
      current: 3000, 
      deadline: '2024-12-31',
      category: 'Retirement',
      icon: '💰',
      color: '#f6d365',
      monthlyTarget: 666.67,
      timeRemaining: '9 months'
    }
  ]
})

const totalGoalsValue = computed(() => {
  return goals.value.reduce((sum, goal) => sum + goal.target, 0)
})

const totalSaved = computed(() => {
  return goals.value.reduce((sum, goal) => sum + goal.current, 0)
})

const overallProgress = computed(() => {
  if (totalGoalsValue.value === 0) return 0
  return Math.round((totalSaved.value / totalGoalsValue.value) * 100)
})

const completedGoalsCount = computed(() => {
  return goals.value.filter(goal => goal.progress >= 100).length
})

const formatCurrency = (amount) => {
  return amount.toLocaleString('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 2
  })
}

const getStatusClass = (goal) => {
  if (goal.progress >= 100) return 'status-completed'
  if (goal.progress >= 75) return 'status-good'
  if (goal.progress >= 50) return 'status-fair'
  return 'status-poor'
}

const getStatusText = (goal) => {
  if (goal.progress >= 100) return 'Completed'
  if (goal.progress >= 75) return 'On Track'
  if (goal.progress >= 50) return 'Needs Attention'
  return 'Behind Schedule'
}

const editGoal = (goal) => {
  editingGoal.value = goal
  newGoal.value = { ...goal }
  showAddGoal.value = true
}

const saveGoal = () => {
  if (!newGoal.value.name || newGoal.value.target <= 0) {
    alert('Please fill in all required fields')
    return
  }
  
  const progress = Math.round((newGoal.value.current / newGoal.value.target) * 100)
  const monthlyTarget = Math.round((newGoal.value.target - newGoal.value.current) / getMonthsRemaining(newGoal.value.deadline) * 100) / 100
  
  if (editingGoal.value) {
    // Update existing goal
    const index = goals.value.findIndex(g => g.id === editingGoal.value.id)
    if (index !== -1) {
      goals.value[index] = {
        ...newGoal.value,
        id: editingGoal.value.id,
        progress,
        monthlyTarget,
        timeRemaining: getTimeRemaining(newGoal.value.deadline),
        icon: getIconForCategory(newGoal.value.category),
        color: getColorForCategory(newGoal.value.category)
      }
    }
  } else {
    // Add new goal
    const newId = Math.max(...goals.value.map(g => g.id), 0) + 1
    goals.value.push({
      ...newGoal.value,
      id: newId,
      progress,
      monthlyTarget,
      timeRemaining: getTimeRemaining(newGoal.value.deadline),
      icon: getIconForCategory(newGoal.value.category),
      color: getColorForCategory(newGoal.value.category)
    })
  }
  
  resetForm()
  showAddGoal.value = false
}

const deleteGoal = (id) => {
  if (confirm('Are you sure you want to delete this goal?')) {
    goals.value = goals.value.filter(g => g.id !== id)
  }
}

const addToGoal = (goal) => {
  selectedGoal.value = goal
  fundsToAdd.value = 0
  fundsSource.value = 'Savings'
  fundsDate.value = new Date().toISOString().split('T')[0]
  fundsNotes.value = ''
  showAddFunds.value = true
}

const confirmAddFunds = () => {
  if (fundsToAdd.value <= 0) {
    alert('Please enter a valid amount')
    return
  }
  
  const index = goals.value.findIndex(g => g.id === selectedGoal.value.id)
  if (index !== -1) {
    goals.value[index].current += parseFloat(fundsToAdd.value)
    goals.value[index].progress = Math.round((goals.value[index].current / goals.value[index].target) * 100)
    
    // Record the transaction (in a real app, this would go to a database)
    console.log(`Added ${formatCurrency(fundsToAdd.value)} to ${selectedGoal.value.name} from ${fundsSource.value}`)
  }
  
  showAddFunds.value = false
  selectedGoal.value = null
}

const viewGoalDetails = (goal) => {
  alert(`Goal Details:\n\nName: ${goal.name}\nDescription: ${goal.description}\nProgress: ${goal.progress}%\nTarget: ${formatCurrency(goal.target)}\nCurrent: ${formatCurrency(goal.current)}\nMonthly Target: ${formatCurrency(goal.monthlyTarget)}\nDeadline: ${goal.deadline}`)
}

const resetForm = () => {
  newGoal.value = {
    name: '',
    description: '',
    target: 0,
    current: 0,
    deadline: '',
    category: 'Emergency Fund'
  }
  editingGoal.value = null
}

const getMonthsRemaining = (deadline) => {
  const today = new Date()
  const targetDate = new Date(deadline)
  const months = (targetDate.getFullYear() - today.getFullYear()) * 12 + 
                 (targetDate.getMonth() - today.getMonth())
  return Math.max(months, 1)
}

const getTimeRemaining = (deadline) => {
  const months = getMonthsRemaining(deadline)
  if (months >= 12) {
    const years = Math.floor(months / 12)
    const remainingMonths = months % 12
    return `${years} year${years > 1 ? 's' : ''}${remainingMonths > 0 ? ` ${remainingMonths} month${remainingMonths > 1 ? 's' : ''}` : ''}`
  }
  return `${months} month${months > 1 ? 's' : ''}`
}

const getIconForCategory = (category) => {
  const icons = {
    'Emergency Fund': '🛡️',
    'Vacation': '✈️',
    'Education': '🎓',
    'Home': '🏠',
    'Vehicle': '🚗',
    'Retirement': '💰',
    'Investment': '📈',
    'Other': '🎯'
  }
  return icons[category] || '🎯'
}

const getColorForCategory = (category) => {
  const colors = {
    'Emergency Fund': '#667eea',
    'Vacation': '#f5576c',
    'Education': '#4facfe',
    'Home': '#f093fb',
    'Vehicle': '#38a169',
    'Retirement': '#f6d365',
    'Investment': '#00f2fe',
    'Other': '#718096'
  }
  return colors[category] || '#718096'
}
</script>

<style scoped>
.goals-page {
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

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.btn-secondary {
  background: #e2e8f0;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #cbd5e0;
}

.goals-summary {
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

.goals-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.goal-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.goal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.goal-header {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.goal-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 1.5rem;
  color: white;
  flex-shrink: 0;
}

.goal-info {
  flex: 1;
}

.goal-info h3 {
  font-size: 1.25rem;
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

.goal-progress {
  margin-bottom: 1.5rem;
}

.progress-bar {
  height: 8px;
  background: #e2e8f0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.75rem;
}

.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.progress-text {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.progress-percentage {
  font-weight: 700;
  font-size: 1.1rem;
  color: #2d3748;
}

.progress-amount {
  font-size: 0.9rem;
  color: #718096;
}

.progress-time {
  text-align: right;
}

.time-label {
  display: block;
  font-size: 0.85rem;
  color: #718096;
  margin-bottom: 0.25rem;
}

.time-value {
  font-weight: 600;
  color: #2d3748;
}

.goal-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 8px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.stat-label {
  font-size: 0.85rem;
  color: #718096;
}

.stat-value {
  font-weight: 600;
  color: #2d3748;
}

.stat-status {
  font-weight: 600;
  font-size: 0.9rem;
  padding: 0.25rem 0.5rem;
  border-radius: 12px;
  display: inline-block;
}

.status-completed {
  background: #c6f6d5;
  color: #22543d;
}

.status-good {
  background: #c6f6d5;
  color: #22543d;
}

.status-fair {
  background: #feebc8;
  color: #744210;
}

.status-poor {
  background: #fed7d7;
  color: #742a2a;
}

.goal-actions-full {
  display: flex;
  gap: 0.75rem;
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

/* Responsive design */
@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .goals-summary {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goals-grid {
    grid-template-columns: 1fr;
  }
  
  .goal-stats {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .goal-actions-full {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 1rem;
  }
}

@media (max-width: 480px) {
  .goals-summary {
    grid-template-columns: 1fr;
  }
  
  .goal-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .goal-actions {
    align-self: flex-end;
  }
  
  .progress-details {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .progress-time {
    text-align: left;
  }
}
</style>