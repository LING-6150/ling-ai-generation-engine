<template>
  <div class="home-page">
    <div class="page-header">
      <h1 class="page-title">Task Management Dashboard</h1>
      <p class="page-subtitle">
        Stay organized and boost your productivity with smart task management
      </p>
    </div>
    
    <div class="dashboard-grid">
      <div class="dashboard-main">
        <div class="todo-section">
          <div class="section-header">
            <h2 class="section-title">My Tasks</h2>
            <div class="section-stats">
              <span class="stat-item">
                <span class="stat-label">Total:</span>
                <span class="stat-value">{{ todos.length }}</span>
              </span>
              <span class="stat-item">
                <span class="stat-label">Completed:</span>
                <span class="stat-value">{{ completedCount }}</span>
              </span>
              <span class="stat-item">
                <span class="stat-label">Pending:</span>
                <span class="stat-value">{{ pendingCount }}</span>
              </span>
            </div>
          </div>
          
          <div class="todo-controls">
            <div class="filter-controls">
              <div class="filter-group">
                <label class="filter-label">Filter by:</label>
                <div class="filter-buttons">
                  <button 
                    @click="setFilter('all')"
                    class="filter-btn"
                    :class="{ active: filter === 'all' }"
                  >
                    All Tasks
                  </button>
                  <button 
                    @click="setFilter('pending')"
                    class="filter-btn"
                    :class="{ active: filter === 'pending' }"
                  >
                    Pending
                  </button>
                  <button 
                    @click="setFilter('completed')"
                    class="filter-btn"
                    :class="{ active: filter === 'completed' }"
                  >
                    Completed
                  </button>
                </div>
              </div>
              
              <div class="priority-filter">
                <label class="filter-label">Priority:</label>
                <div class="priority-buttons">
                  <button 
                    @click="togglePriority('high')"
                    class="priority-filter-btn high"
                    :class="{ active: priorityFilter.high }"
                  >
                    High
                  </button>
                  <button 
                    @click="togglePriority('medium')"
                    class="priority-filter-btn medium"
                    :class="{ active: priorityFilter.medium }"
                  >
                    Medium
                  </button>
                  <button 
                    @click="togglePriority('low')"
                    class="priority-filter-btn low"
                    :class="{ active: priorityFilter.low }"
                  >
                    Low
                  </button>
                </div>
              </div>
            </div>
            
            <div class="sort-controls">
              <label class="sort-label">Sort by:</label>
              <select 
                v-model="sortBy"
                class="sort-select"
              >
                <option value="date">Date Added</option>
                <option value="priority">Priority</option>
                <option value="name">Task Name</option>
              </select>
            </div>
          </div>
          
          <TodoInput @add-todo="addTodo" />
          
          <TodoList 
            :todos="filteredTodos"
            @toggle-todo="toggleTodo"
            @edit-todo="editTodo"
            @delete-todo="deleteTodo"
          />
        </div>
      </div>
      
      <div class="dashboard-sidebar">
        <div class="stats-card">
          <h3 class="stats-title">📊 Task Statistics</h3>
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">📋</div>
              <div class="stat-content">
                <div class="stat-number">{{ todos.length }}</div>
                <div class="stat-label">Total Tasks</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon">✅</div>
              <div class="stat-content">
                <div class="stat-number">{{ completedCount }}</div>
                <div class="stat-label">Completed</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon">⏳</div>
              <div class="stat-content">
                <div class="stat-number">{{ pendingCount }}</div>
                <div class="stat-label">Pending</div>
              </div>
            </div>
            
            <div class="stat-card">
              <div class="stat-icon">🎯</div>
              <div class="stat-content">
                <div class="stat-number">{{ highPriorityCount }}</div>
                <div class="stat-label">High Priority</div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="priority-breakdown">
          <h3 class="breakdown-title">📈 Priority Breakdown</h3>
          <div class="breakdown-chart">
            <div class="chart-bar high" :style="{ width: highPercentage + '%' }">
              <span class="chart-label">High: {{ highPriorityCount }}</span>
            </div>
            <div class="chart-bar medium" :style="{ width: mediumPercentage + '%' }">
              <span class="chart-label">Medium: {{ mediumPriorityCount }}</span>
            </div>
            <div class="chart-bar low" :style="{ width: lowPercentage + '%' }">
              <span class="chart-label">Low: {{ lowPriorityCount }}</span>
            </div>
          </div>
        </div>
        
        <div class="quick-tips">
          <h3 class="tips-title">💡 Quick Tips</h3>
          <ul class="tips-list">
            <li class="tip-item">
              Use <code>!high</code>, <code>!medium</code>, or <code>!low</code> to set priority
            </li>
            <li class="tip-item">
              Double-click any task to edit it
            </li>
            <li class="tip-item">
              Your tasks are automatically saved locally
            </li>
            <li class="tip-item">
              Filter tasks by priority or completion status
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import TodoInput from '@/components/TodoInput.vue'
import TodoList from '@/components/TodoList.vue'
import { generateId, parsePriority, saveToLocalStorage, loadFromLocalStorage } from '@/utils/helpers.js'

// State
const todos = ref([])
const filter = ref('all')
const sortBy = ref('date')
const priorityFilter = ref({
  high: true,
  medium: true,
  low: true
})

// Computed properties
const completedCount = computed(() => {
  return todos.value.filter(todo => todo.completed).length
})

const pendingCount = computed(() => {
  return todos.value.filter(todo => !todo.completed).length
})

const highPriorityCount = computed(() => {
  return todos.value.filter(todo => todo.priority === 'high').length
})

const mediumPriorityCount = computed(() => {
  return todos.value.filter(todo => todo.priority === 'medium').length
})

const lowPriorityCount = computed(() => {
  return todos.value.filter(todo => todo.priority === 'low').length
})

const highPercentage = computed(() => {
  const total = todos.value.length
  return total > 0 ? (highPriorityCount.value / total) * 100 : 0
})

const mediumPercentage = computed(() => {
  const total = todos.value.length
  return total > 0 ? (mediumPriorityCount.value / total) * 100 : 0
})

const lowPercentage = computed(() => {
  const total = todos.value.length
  return total > 0 ? (lowPriorityCount.value / total) * 100 : 0
})

const filteredTodos = computed(() => {
  let filtered = todos.value
  
  // Apply completion filter
  if (filter.value === 'completed') {
    filtered = filtered.filter(todo => todo.completed)
  } else if (filter.value === 'pending') {
    filtered = filtered.filter(todo => !todo.completed)
  }
  
  // Apply priority filter
  const activePriorities = Object.keys(priorityFilter.value)
    .filter(priority => priorityFilter.value[priority])
  
  if (activePriorities.length < 3) {
    filtered = filtered.filter(todo => activePriorities.includes(todo.priority))
  }
  
  // Apply sorting
  filtered = [...filtered].sort((a, b) => {
    if (sortBy.value === 'date') {
      return new Date(b.createdAt) - new Date(a.createdAt)
    } else if (sortBy.value === 'priority') {
      const priorityOrder = { high: 3, medium: 2, low: 1 }
      return priorityOrder[b.priority] - priorityOrder[a.priority]
    } else if (sortBy.value === 'name') {
      return a.text.localeCompare(b.text)
    }
    return 0
  })
  
  return filtered
})

// Methods
const addTodo = (text) => {
  const { text: cleanText, priority } = parsePriority(text)
  
  if (!cleanText) return
  
  const newTodo = {
    id: generateId(),
    text: cleanText,
    priority,
    completed: false,
    createdAt: new Date().toISOString(),
    editing: false
  }
  
  todos.value.unshift(newTodo)
}

const toggleTodo = (id) => {
  const todo = todos.value.find(t => t.id === id)
  if (todo) {
    todo.completed = !todo.completed
  }
}

const editTodo = (id, newText) => {
  const todo = todos.value.find(t => t.id === id)
  if (todo) {
    const { text: cleanText, priority } = parsePriority(newText)
    todo.text = cleanText
    todo.priority = priority
    todo.editing = false
  }
}

const deleteTodo = (id) => {
  todos.value = todos.value.filter(todo => todo.id !== id)
}

const setFilter = (filterType) => {
  filter.value = filterType
}

const togglePriority = (priority) => {
  priorityFilter.value[priority] = !priorityFilter.value[priority]
}

// Initialize with sample data
const initializeSampleData = () => {
  const sampleTodos = [
    {
      id: generateId(),
      text: 'Finish quarterly report',
      priority: 'high',
      completed: false,
      createdAt: new Date(Date.now() - 86400000).toISOString(),
      editing: false
    },
    {
      id: generateId(),
      text: 'Schedule team meeting',
      priority: 'medium',
      completed: true,
      createdAt: new Date(Date.now() - 172800000).toISOString(),
      editing: false
    },
    {
      id: generateId(),
      text: 'Update project documentation',
      priority: 'medium',
      completed: false,
      createdAt: new Date(Date.now() - 259200000).toISOString(),
      editing: false
    },
    {
      id: generateId(),
      text: 'Review client feedback',
      priority: 'high',
      completed: false,
      createdAt: new Date(Date.now() - 345600000).toISOString(),
      editing: false
    },
    {
      id: generateId(),
      text: 'Organize digital files',
      priority: 'low',
      completed: true,
      createdAt: new Date(Date.now() - 432000000).toISOString(),
      editing: false
    },
    {
      id: generateId(),
      text: 'Plan next sprint tasks',
      priority: 'medium',
      completed: false,
      createdAt: new Date(Date.now() - 518400000).toISOString(),
      editing: false
    }
  ]
  
  return sampleTodos
}

// Load and save todos
const loadTodos = () => {
  const savedTodos = loadFromLocalStorage('todos')
  if (savedTodos && savedTodos.length > 0) {
    todos.value = savedTodos
  } else {
    todos.value = initializeSampleData()
  }
}

const saveTodos = () => {
  saveToLocalStorage('todos', todos.value)
}

// Lifecycle hooks
onMounted(() => {
  loadTodos()
})

// Watch todos for changes and save
watch(todos, () => {
  saveTodos()
}, { deep: true })
</script>

<style scoped>
.home-page {
  min-height: 100%;
}

.page-header {
  text-align: center;
  margin-bottom: 2rem;
  padding: 1rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

.dashboard-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

.dashboard-main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.dashboard-sidebar {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.todo-section {
  background: white;
  border-radius: 16px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.section-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: #333;
}

.section-stats {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 0.9rem;
}

.stat-label {
  color: #666;
  font-weight: 500;
}

.stat-value {
  font-weight: 600;
  color: #333;
}

.todo-controls {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 12px;
}

.filter-controls {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.filter-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #666;
}

.filter-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.filter-btn:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
  color: white;
}

.priority-filter {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.priority-buttons {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.priority-filter-btn {
  padding: 0.5rem 1rem;
  border: 2px solid;
  background: white;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.priority-filter-btn.high {
  border-color: #d32f2f;
  color: #d32f2f;
}

.priority-filter-btn.high:hover {
  background-color: #fee;
}

.priority-filter-btn.high.active {
  background-color: #d32f2f;
  color: white;
}

.priority-filter-btn.medium {
  border-color: #f57c00;
  color: #f57c00;
}

.priority-filter-btn.medium:hover {
  background-color: #fff3e0;
}

.priority-filter-btn.medium.active {
  background-color: #f57c00;
  color: white;
}

.priority-filter-btn.low {
  border-color: #388e3c;
  color: #388e3c;
}

.priority-filter-btn.low:hover {
  background-color: #e8f5e9;
}

.priority-filter-btn.low.active {
  background-color: #388e3c;
  color: white;
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.sort-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #666;
}

.sort-select {
  padding: 0.5rem 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.9rem;
  color: #333;
  background: white;
  cursor: pointer;
  outline: none;
  transition: all 0.2s;
}

.sort-select:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.stats-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 1.5rem;
  color: white;
}

.stats-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.stat-card {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 1rem;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stat-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
}

.stat-label {
  font-size: 0.8rem;
  opacity: 0.9;
  margin-top: 0.25rem;
}

.priority-breakdown {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.breakdown-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #333;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.breakdown-chart {
  height: 40px;
  background: #f0f0f0;
  border-radius: 20px;
  overflow: hidden;
  display: flex;
  position: relative;
}

.chart-bar {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: width 0.3s ease;
  position: relative;
}

.chart-bar.high {
  background: linear-gradient(90deg, #d32f2f, #f44336);
}

.chart-bar.medium {
  background: linear-gradient(90deg, #f57c00, #ff9800);
}

.chart-bar.low {
  background: linear-gradient(90deg, #388e3c, #4caf50);
}

.chart-label {
  font-size: 0.75rem;
  font-weight: 600;
  color: white;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
  white-space: nowrap;
  padding: 0 0.5rem;
}

.quick-tips {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.tips-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #333;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tips-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.tip-item {
  padding: 0.75rem;
  background: #f8f9fa;
  border-radius: 8px;
  font-size: 0.9rem;
  color: #555;
  line-height: 1.5;
  border-left: 3px solid #667eea;
}

.tip-item code {
  background: #e3f2fd;
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.85em;
  color: #1976d2;
}

@media (min-width: 1024px) {
  .dashboard-grid {
    grid-template-columns: 2fr 1fr;
  }
  
  .todo-controls {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }
  
  .filter-controls {
    flex-direction: row;
    align-items: center;
    gap: 2rem;
  }
  
  .filter-group {
    flex-direction: row;
    align-items: center;
    gap: 1rem;
  }
  
  .priority-filter {
    flex-direction: row;
    align-items: center;
    gap: 1rem;
  }
}

@media (max-width: 1024px) {
  .page-title {
    font-size: 2rem;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .section-stats {
    width: 100%;
    justify-content: space-between;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 1.75rem;
  }
  
  .page-subtitle {
    font-size: 1rem;
  }
  
  .todo-section {
    padding: 1.5rem;
  }
  
  .section-title {
    font-size: 1.5rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .chart-label {
    font-size: 0.7rem;
  }
}

@media (max-width: 480px) {
  .page-title {
    font-size: 1.5rem;
  }
  
  .todo-section {
    padding: 1rem;
  }
  
  .section-title {
    font-size: 1.25rem;
  }
  
  .stat-item {
    padding: 0.5rem;
    font-size: 0.8rem;
  }
  
  .filter-buttons,
  .priority-buttons {
    justify-content: center;
  }
  
  .filter-btn,
  .priority-filter-btn {
    flex: 1;
    text-align: center;
    min-width: 100px;
  }
  
  .sort-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .sort-select {
    width: 100%;
  }
}
</style>