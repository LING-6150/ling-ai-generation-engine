<template>
  <div class="todo-input-container">
    <form @submit.prevent="handleSubmit" class="todo-input-form">
      <div class="input-wrapper">
        <input
          type="text"
          v-model="inputText"
          @keydown.enter="handleSubmit"
          placeholder="Add a new task... (Use !high, !medium, or !low for priority)"
          class="todo-input"
          ref="inputRef"
        />
        
        <div class="input-actions">
          <button 
            type="button"
            @click="showPriorityMenu = !showPriorityMenu"
            class="priority-btn"
            :class="selectedPriority"
            :title="`Current priority: ${selectedPriority}`"
          >
            <span class="priority-icon">🎯</span>
            <span class="priority-text">{{ formatPriority(selectedPriority) }}</span>
          </button>
          
          <button 
            type="submit"
            class="submit-btn"
            :disabled="!inputText.trim()"
            title="Add task"
          >
            <span class="submit-icon">+</span>
            <span class="submit-text">Add Task</span>
          </button>
        </div>
      </div>
      
      <div v-if="showPriorityMenu" class="priority-menu">
        <div class="priority-options">
          <button
            type="button"
            @click="setPriority('high')"
            class="priority-option high"
            :class="{ active: selectedPriority === 'high' }"
          >
            <span class="option-icon">🔥</span>
            <span class="option-text">High Priority</span>
            <span class="option-desc">Urgent & important</span>
          </button>
          
          <button
            type="button"
            @click="setPriority('medium')"
            class="priority-option medium"
            :class="{ active: selectedPriority === 'medium' }"
          >
            <span class="option-icon">📋</span>
            <span class="option-text">Medium Priority</span>
            <span class="option-desc">Important but not urgent</span>
          </button>
          
          <button
            type="button"
            @click="setPriority('low')"
            class="priority-option low"
            :class="{ active: selectedPriority === 'low' }"
          >
            <span class="option-icon">🌱</span>
            <span class="option-text">Low Priority</span>
            <span class="option-desc">Can be done later</span>
          </button>
        </div>
      </div>
      
      <div class="input-hints">
        <div class="hint-item">
          <span class="hint-icon">💡</span>
          <span class="hint-text">
            Type <code>!high</code>, <code>!medium</code>, or <code>!low</code> in your task to set priority
          </span>
        </div>
        <div class="hint-item">
          <span class="hint-icon">⏎</span>
          <span class="hint-text">Press Enter to add task</span>
        </div>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, nextTick, watch } from 'vue'

const emit = defineEmits(['add-todo'])

const inputText = ref('')
const selectedPriority = ref('medium')
const showPriorityMenu = ref(false)
const inputRef = ref(null)

const formatPriority = (priority) => {
  const priorityMap = {
    high: 'High',
    medium: 'Medium',
    low: 'Low'
  }
  return priorityMap[priority] || priority
}

const setPriority = (priority) => {
  selectedPriority.value = priority
  showPriorityMenu.value = false
  inputRef.value?.focus()
}

const handleSubmit = () => {
  const text = inputText.value.trim()
  if (!text) return
  
  // Add priority tag if not already present
  let finalText = text
  if (!text.includes('!high') && !text.includes('!medium') && !text.includes('!low')) {
    finalText = `${text} !${selectedPriority.value}`
  }
  
  emit('add-todo', finalText)
  inputText.value = ''
  selectedPriority.value = 'medium'
  
  // Focus back on input
  nextTick(() => {
    inputRef.value?.focus()
  })
}

// Watch for priority tags in input
watch(inputText, (newText) => {
  if (newText.includes('!high')) {
    selectedPriority.value = 'high'
  } else if (newText.includes('!medium')) {
    selectedPriority.value = 'medium'
  } else if (newText.includes('!low')) {
    selectedPriority.value = 'low'
  }
})
</script>

<style scoped>
.todo-input-container {
  margin-bottom: 2rem;
}

.todo-input-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.todo-input {
  width: 100%;
  padding: 1rem 1.5rem;
  font-size: 1rem;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  outline: none;
  transition: all 0.2s;
  background: white;
  color: #333;
}

.todo-input:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.todo-input::placeholder {
  color: #999;
  font-size: 0.9rem;
}

.input-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.priority-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 120px;
}

.priority-btn.high {
  border-color: #d32f2f;
  color: #d32f2f;
  background-color: #fee;
}

.priority-btn.medium {
  border-color: #f57c00;
  color: #f57c00;
  background-color: #fff3e0;
}

.priority-btn.low {
  border-color: #388e3c;
  color: #388e3c;
  background-color: #e8f5e9;
}

.priority-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.priority-icon {
  font-size: 1rem;
}

.priority-text {
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.submit-btn {
  flex: 2;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border: none;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 140px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.submit-btn:active:not(:disabled) {
  transform: translateY(0);
}

.submit-icon {
  font-size: 1.25rem;
  font-weight: 700;
}

.submit-text {
  font-weight: 600;
}

.priority-menu {
  background: white;
  border-radius: 12px;
  padding: 1rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  border: 1px solid #e0e0e0;
  animation: slideDown 0.2s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.priority-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.priority-option {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 2px solid #e0e0e0;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  text-align: left;
}

.priority-option:hover {
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.priority-option.high {
  border-color: #d32f2f;
}

.priority-option.high:hover {
  background-color: #fee;
}

.priority-option.high.active {
  background-color: #d32f2f;
  color: white;
}

.priority-option.medium {
  border-color: #f57c00;
}

.priority-option.medium:hover {
  background-color: #fff3e0;
}

.priority-option.medium.active {
  background-color: #f57c00;
  color: white;
}

.priority-option.low {
  border-color: #388e3c;
}

.priority-option.low:hover {
  background-color: #e8f5e9;
}

.priority-option.low.active {
  background-color: #388e3c;
  color: white;
}

.option-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.option-text {
  flex: 1;
  font-weight: 600;
  font-size: 0.95rem;
}

.option-desc {
  font-size: 0.8rem;
  color: #666;
  opacity: 0.8;
}

.priority-option.active .option-desc {
  color: rgba(255, 255, 255, 0.9);
}

.input-hints {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 3px solid #667eea;
}

.hint-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.85rem;
  color: #666;
}

.hint-icon {
  font-size: 0.9rem;
  opacity: 0.7;
}

.hint-text {
  flex: 1;
}

.hint-text code {
  background: #e3f2fd;
  padding: 0.125rem 0.375rem;
  border-radius: 4px;
  font-family: monospace;
  font-size: 0.9em;
  color: #1976d2;
}

@media (min-width: 768px) {
  .input-wrapper {
    flex-direction: row;
    align-items: center;
  }
  
  .todo-input {
    flex: 3;
  }
  
  .input-actions {
    flex: 2;
  }
  
  .priority-options {
    flex-direction: row;
  }
  
  .priority-option {
    flex: 1;
    flex-direction: column;
    text-align: center;
    gap: 0.5rem;
  }
  
  .option-text {
    font-size: 0.9rem;
  }
  
  .option-desc {
    font-size: 0.75rem;
  }
  
  .input-hints {
    flex-direction: row;
    justify-content: space-between;
  }
}

@media (max-width: 480px) {
  .todo-input {
    padding: 0.875rem 1.25rem;
    font-size: 0.95rem;
  }
  
  .priority-btn,
  .submit-btn {
    padding: 0.625rem 1rem;
    font-size: 0.875rem;
  }
  
  .priority-text,
  .submit-text {
    display: none;
  }
  
  .priority-icon,
  .submit-icon {
    margin: 0;
  }
  
  .priority-option {
    padding: 0.75rem;
    gap: 0.75rem;
  }
  
  .option-icon {
    font-size: 1.25rem;
  }
  
  .option-text {
    font-size: 0.85rem;
  }
  
  .option-desc {
    display: none;
  }
}
</style>