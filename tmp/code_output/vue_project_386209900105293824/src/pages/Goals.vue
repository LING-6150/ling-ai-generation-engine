<template>
  <div class="goals">
    <div class="page-header">
      <h2>Financial Goals</h2>
      <button class="btn btn-primary" @click="showAddModal = true">
        <span class="btn-icon">+</span> Set New Goal
      </button>
    </div>

    <div class="goals-summary">
      <div class="summary-card">
        <h3>Goals Progress Overview</h3>
        <div class="summary-stats">
          <div class="stat">
            <span class="stat-label">Total Goals</span>
            <span class="stat-value">{{ goals.length }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">Completed</span>
            <span class="stat-value">{{ completedGoals }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">Total Target</span>
            <span class="stat-value">${{ totalTarget.toLocaleString() }}</span>
          </div>
          <div class="stat">
            <span class="stat-label">Total Saved</span>
            <span class="stat-value">${{ totalSaved.toLocaleString() }}</span>
          </div>
        </div>
      </div>
    </div>

    <div class="goals-filter">
      <div class="filter-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="filter-tab"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <div class="goals-grid">
      <div 
        v-for="goal in filteredGoals" 
        :key="goal.id" 
        class="goal-card"
        :class="{ completed: goal.progress >= 100 }"
      >
        <div class="goal-header">
          <div class="goal-icon" :style="{ backgroundColor: getGoalColor(goal.type) }">
            {{ getGoalIcon(goal.type) }}
          </div>
          <div class="goal-info">
            <h4>{{ goal.name }}</h4>
            <p class="goal-description">{{ goal.description }}</p>
          </div>
          <div class="goal-actions">
            <button class="btn-icon-small" @click="editGoal(goal)">✏️</button>
            <button class="btn-icon-small delete" @click="deleteGoal(goal.id)">🗑️</button>
          </div>
        </div>
        
        <div class="goal-progress">
          <div class="progress-info">
            <span class="progress-label">
              Saved: ${{ goal.current.toLocaleString() }} / ${{ goal.target.toLocaleString() }}
            </span>
            <span class="progress-percent">{{ goal.progress }}%</span>
          </div>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: goal.progress + '%' }"
            ></div>
          </div>
          <div class="goal-details">
            <div class="detail-item">
              <span class="detail-label">Monthly Target</span>
              <span class="detail-value">${{ goal.monthlyTarget.toLocaleString() }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Deadline</span>
              <span class="detail-value">{{ formatDate(goal.deadline) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Priority</span>
              <span class="priority-badge" :class="goal.priority.toLowerCase()">
                {{ goal.priority }}
              </span>
            </div>
          </div>
        </div>
        
        <div class="goal-actions-footer">
          <button 
            class="btn btn-small btn-secondary"
            @click="addContribution(goal)"
          >
            Add Contribution
          </button>
          <div class="time-remaining">
            <span class="time-label">{{ getTimeRemaining(goal.deadline) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Add/Edit Goal Modal -->
    <div v-if="showAddModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <div class="modal-header">
          <h3>{{ editingGoal ? 'Edit Goal' : 'Set New Goal' }}</h3>
          <button class="modal-close" @click="closeModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveGoal">
            <div class="form-group">
              <label for="goal-name">Goal Name</label>
              <input
                id="goal-name"
                v-model="newGoal.name"
                type="text"
                required
                placeholder="e.g., Emergency Fund, New Car, Vacation"
              />
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="goal-target">Target Amount ($)</label>
                <input
                  id="goal-target"
                  v-model="newGoal.target"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                />
              </div>
              
              <div class="form-group">
                <label for="goal-current">Current Amount ($)</label>
                <input
                  id="goal-current"
                  v-model="newGoal.current"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="goal-type">Goal Type</label>
                <select id="goal-type" v-model="newGoal.type" required>
                  <option value="">Select Type</option>
                  <option v-for="type in goalTypes" :key="type" :value="type">{{ type }}</option>
                </select>
              </div>
              
              <div class="form-group">
                <label for="goal-priority">Priority</label>
                <select id="goal-priority" v-model="newGoal.priority" required>
                  <option value="Low">Low</option>
                  <option value="Medium">Medium</option>
                  <option value="High">High</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label for="goal-deadline">Target Date</label>
                <input
                  id="goal-deadline"
                  v-model="newGoal.deadline"
                  type="date"
                  required
                />
              </div>
              
              <div class="form-group">
                <label for="goal-monthly">Monthly Target ($)</label>
                <input
                  id="goal-monthly"
                  v-model="newGoal.monthlyTarget"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                  placeholder="0.00"
                />
              </div>
            </div>
            
            <div class="form-group">
              <label for="goal-description">Description (Optional)</label>
              <textarea
                id="goal-description"
                v-model="newGoal.description"
                rows="3"
                placeholder="Describe your goal and why it's important..."
              ></textarea>
            </div>
            
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">Cancel</button>
              <button type="submit" class="btn btn-primary">
                {{ editingGoal ? 'Update' : 'Create' }} Goal
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- Contribution Modal -->
    <div v-if="showContributionModal" class="modal-overlay" @click.self="closeContributionModal">
      <div class="modal">
        <div class="modal-header">
          <h3>Add Contribution to {{ selectedGoal?.name }}</h3>
          <button class="modal-close" @click="closeContributionModal">×</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveContribution">
            <div class="form-group">
              <label for="contribution-amount">Contribution Amount ($)</label>
              <input
                id="contribution-amount"
                v-model="contribution.amount"
                type="number"
                step="0.01"
                min="0.01"
                required
                placeholder="0.00"
              />
            </div>
            
            <div class="form-group">
              <label for="contribution-date">Date</label>
              <input
                id="contribution-date"
                v-model="contribution.date"
                type="date"
                required
              />
            </div>
            
            <div class="form-group">
              <label for="contribution-notes">Notes (Optional)</label>
              <textarea
                id="contribution-notes"
                v-model="contribution.notes"
                rows="3"
                placeholder="Add notes about this contribution..."
              ></textarea>
            </div>
            
            <div class="modal-actions">
              <button type="button" class="btn btn-secondary" @click="closeContributionModal">Cancel</button>
              <button type="submit" class="btn btn-primary">Add Contribution</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const goals = ref([
  { 
    id: 1, 
    name: 'Emergency Fund', 
    target: 10000, 
    current: 7500, 
    progress: 75,
    type: 'Savings',
    priority: 'High',
    deadline: '2024-12-31',
    monthlyTarget: 500,
    description: '6 months of living expenses for financial security'
  },
  { 
    id: 2, 
    name: 'New Car', 
    target: 25000, 
    current: 12000, 
    progress: 48,
    type: 'Vehicle',
    priority: 'Medium',
    deadline: '2025-06-30',
    monthlyTarget: 1000,
    description: 'Down payment for a new electric vehicle'
  },
  { 
    id: 3, 
    name: 'Europe Vacation', 
    target: 5000, 
    current: 3200, 
    progress: 64,
    type: 'Travel',
    priority: 'Medium',
    deadline: '2024-08-31',
    monthlyTarget: 600,
    description: 'Two-week trip to Italy and France next summer'
  },
  { 
    id: 4, 
    name: 'Home Renovation', 
    target: 15000, 
    current: 4500, 
    progress: 30,
    type: 'Home',
    priority: 'Low',
    deadline: '2025-03-31',
    monthlyTarget: 750,
    description: 'Kitchen and bathroom updates for our house'
  },
  { 
    id: 5, 
    name: 'Retirement Fund', 
    target: 500000, 
    current: 125000, 
    progress: 25,
    type: 'Retirement',
    priority: 'High',
    deadline: '2040-12-31',
    monthlyTarget: 1500,
    description: 'Long-term retirement savings plan'
  },
  { 
    id: 6, 
    name: 'Education Fund', 
    target: 20000, 
    current: 8000, 
    progress: 40,
    type: 'Education',
    priority: 'Medium',
    deadline: '2026-08-31',
    monthlyTarget: 400,
    description: 'College fund for children\'s education'
  }
])

const tabs = ref([
  { id: 'all', label: 'All Goals' },
  { id: 'active', label: 'Active' },
  { id: 'completed', label: 'Completed' },
  { id: 'high', label: 'High Priority' }
])

const goalTypes = ref(['Savings', 'Vehicle', 'Travel', 'Home', 'Retirement', 'Education', 'Wedding', 'Business', 'Debt', 'Other'])
const activeTab = ref('all')
const showAddModal = ref(false)
const showContributionModal = ref(false)
const editingGoal = ref(null)
const selectedGoal = ref(null)

const newGoal = ref({
  name: '',
  target: '',
  current: '',
  type: '',
  priority: 'Medium',
  deadline: '',
  monthlyTarget: '',
  description: ''
})

const contribution = ref({
  amount: '',
  date: '',
  notes: ''
})

const filteredGoals = computed(() => {
  let filtered = [...goals.value]
  
  switch (activeTab.value) {
    case 'active':
      filtered = filtered.filter(goal => goal.progress < 100)
      break
    case 'completed':
      filtered = filtered.filter(goal => goal.progress >= 100)
      break
    case 'high':
      filtered = filtered.filter(goal => goal.priority === 'High')
      break
  }
  
  return filtered.sort((a, b) => {
    // Sort by priority (High > Medium > Low) then by progress
    const priorityOrder = { 'High': 3, 'Medium': 2, 'Low': 1 }
    if (priorityOrder[b.priority] !== priorityOrder[a.priority]) {
      return priorityOrder[b.priority] - priorityOrder[a.priority]
    }
    return b.progress - a.progress
  })
})

const completedGoals = computed(() => {
  return goals.value.filter(goal => goal.progress >= 100).length
})

const totalTarget = computed(() => {
  return goals.value.reduce((sum, goal) => sum + goal.target, 0)
})

const totalSaved = computed(() => {
  return goals.value.reduce((sum, goal) => sum + goal.current, 0)
})

const getGoalColor = (type) => {
  const colors = {
    'Savings': '#48bb78',
    'Vehicle': '#4299e1',
    'Travel': '#d53f8c',
    'Home': '#ed8936',
    'Retirement': '#805ad5',
    'Education': '#3182ce',
    'Wedding': '#f56565',
    'Business': '#38b2ac',
    'Debt': '#4a5568',
    'Other': '#718096'
  }
  return colors[type] || '#667eea'
}

const getGoalIcon = (type) => {
  const icons = {
    'Savings': '💰',
    'Vehicle': '🚗',
    'Travel': '✈️',
    'Home': '🏠',
    'Retirement': '👴',
    'Education': '📚',
    'Wedding': '💍',
    'Business': '💼',
    'Debt': '💳',
    'Other': '🎯'
  }
  return icons[type] || '💰'
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

const getTimeRemaining = (deadline) => {
  const now = new Date()
  const target = new Date(deadline)
  const diffTime = target - now
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays < 0) return 'Overdue'
  if (diffDays === 0) return 'Due today'
  if (diffDays === 1) return 'Due tomorrow'
  if (diffDays < 30) return `${diffDays} days left`
  if (diffDays < 365) return `${Math.floor(diffDays / 30)} months left`
  return `${Math.floor(diffDays / 365)} years left`
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

const addContribution = (goal) => {
  selectedGoal.value = goal
  contribution.value = {
    amount: '',
    date: new Date().toISOString().split('T')[0],
    notes: ''
  }
  showContributionModal.value = true
}

const saveGoal = () => {
  if (editingGoal.value) {
    // Update existing goal
    const index = goals.value.findIndex(g => g.id === editingGoal.value.id)
    if (index !== -1) {
      const target = parseFloat(newGoal.value.target)
      const current = parseFloat(newGoal.value.current)
      const updatedGoal = {
        ...newGoal.value,
        id: editingGoal.value.id,
        target: target,
        current: current,
        progress: Math.min(100, (current / target) * 100)
      }
      goals.value[index] = updatedGoal
    }
  } else {
    // Add new goal
    const newId = Math.max(...goals.value.map(g => g.id), 0) + 1
    const target = parseFloat(newGoal.value.target)
    const current = parseFloat(newGoal.value.current)
    const newGoalItem = {
      ...newGoal.value,
      id: newId,
      target: target,
      current: current,
      progress: Math.min(100, (current / target) * 100)
    }
    goals.value.push(newGoalItem)
  }
  
  closeModal()
}

const saveContribution = () => {
  if (selectedGoal.value) {
    const amount = parseFloat(contribution.value.amount)
    const index = goals.value.findIndex(g => g.id === selectedGoal.value.id)
    if (index !== -1) {
      goals.value[index].current += amount
      const target = goals.value[index].target
      goals.value[index].progress = Math.min(100, (goals.value[index].current / target) * 100)
    }
  }
  
  closeContributionModal()
}

const closeModal = () => {
  showAddModal.value = false
  editingGoal.value = null
  newGoal.value = {
    name: '',
    target: '',
    current: '',
    type: '',
    priority: 'Medium',
    deadline: '',
    monthlyTarget: '',
    description: ''
  }
}

const closeContributionModal = () => {
  showContributionModal.value = false
  selectedGoal.value = null
  contribution.value = {
    amount: '',
    date: '',
    notes: ''
  }
}

onMounted(() => {
  // Initialize with today's date plus 1 year for new goals
  const nextYear = new Date()
  nextYear.setFullYear(nextYear.getFullYear() + 1)
  newGoal.value.deadline = nextYear.toISOString().split('T')[0]
})
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

.goals-summary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 2rem;
  color: white;
}

.summary-card h3 {
  font-size: 1.25rem;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.summary-stats {
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

.goals-filter {
  background: white;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-tab {
  padding: 0.75rem 1.5rem;
  border: none;
  background: #f7fafc;
  color: #4a5568;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-tab:hover {
  background: #e2e8f0;
}

.filter-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
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
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.goal-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.goal-card.completed {
  border-left: 4px solid #48bb78;
}

.goal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
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
  color: white;
  flex-shrink: 0;
}

.goal-info {
  flex: 1;
  margin: 0 1rem;
}

.goal-info h4 {
  font-size: 1.1rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
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

.goal-progress {
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
  margin-bottom: 1rem;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(to right, #667eea, #764ba2);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.goal-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
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

.priority-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.priority-badge.high {
  background: rgba(245, 101, 101, 0.1);
  color: #e53e3e;
}

.priority-badge.medium {
  background: rgba(237, 137, 54, 0.1);
  color: #ed8936;
}

.priority-badge.low {
  background: rgba(72, 187, 120, 0.1);
  color: #38a169;
}

.goal-actions-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 1rem;
  border-top: 1px solid #e2e8f0;
}

.btn-small {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

.time-remaining {
  font-size: 0.85rem;
  color: #718096;
}

.time-label {
  font-weight: 500;
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
  
  .goals-grid {
    grid-template-columns: 1fr;
  }
  
  .summary-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goal-details {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .goal-actions-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
}

@media (max-width: 480px) {
  .summary-stats {
    grid-template-columns: 1fr;
  }
  
  .filter-tabs {
    flex-direction: column;
  }
  
  .filter-tab {
    width: 100%;
  }
  
  .modal {
    margin: 1rem;
  }
}
</style>