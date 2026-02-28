<template>
  <div class="todo-list-container">
    <div v-if="todos.length === 0" class="empty-state">
      <div class="empty-icon">📝</div>
      <h3 class="empty-title">No tasks found</h3>
      <p class="empty-description">
        {{ getEmptyMessage() }}
      </p>
    </div>
    
    <div v-else class="todo-items">
      <div 
        v-for="todo in todos" 
        :key="todo.id"
        class="todo-item"
        :class="{
          completed: todo.completed,
          editing: todo.editing,
          [todo.priority]: true
        }"
      >
        <div class="todo-item-content">
          <div class="todo-checkbox">
            <input
              type="checkbox"
              :id="'todo-' + todo.id"
              :checked="todo.completed"
              @change="$emit('toggle-todo', todo.id)"
              class="checkbox-input"
            />
            <label :for="'todo-' + todo.id" class="checkbox-label">
              <span class="checkmark"></span>
            </label>
          </div>
          
          <div class="todo-text-container">
            <div v-if="!todo.editing" class="todo-text-wrapper">
              <span 
                class="todo-text"
                :class="{ 'completed-text': todo.completed }"
                @dblclick="startEditing(todo)"
              >
                {{ todo.text }}
              </span>
              
              <div class="todo-meta">
                <span class="todo-priority" :class="todo.priority">
                  {{ formatPriority(todo.priority) }}
                </span>
                <span class="todo-date">
                  {{ formatDate(todo.createdAt) }}
                </span>
              </div>
            </div>
            
            <div v-else class="todo-edit-wrapper">
              <input
                type="text"
                v-model="editText"
                @keydown.enter="saveEdit(todo)"
                @keydown.escape="cancelEdit(todo)"
                @blur="saveEdit(todo)"
                class="edit-input"
                ref="editInputRef"
              />
              <div class="edit-hints">
                <span class="hint-text">Press Enter to save, Esc to cancel</span>
              </div>
            </div>
          </div>
          
          <div class="todo-actions">
            <button 
              v-if="!todo.editing"
              @click="startEditing(todo)"
              class="action-btn edit-btn"
              title="Edit task"
            >
              ✏️
            </button>
            
            <button 
              @click="$emit('delete-todo', todo.id)"
              class="action-btn delete-btn"
              title="Delete task"
            >
              🗑️
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { formatDate } from '@/utils/helpers.js'

const props = defineProps({
  todos: {
    type: Array,
    required: true,
    default: () => []
  }
})

const emit = defineEmits(['toggle-todo', 'edit-todo', 'delete-todo'])

const editText = ref('')
const editInputRef = ref(null)
let editingTodoId = null

const formatPriority = (priority) => {
  const priorityMap = {
    high: 'High Priority',
    medium: 'Medium Priority',
    low: 'Low Priority'
  }
  return priorityMap[priority] || priority
}

const getEmptyMessage = () => {
  if (props.todos.length === 0) {
    return 'Start by adding your first task above!'
  }
  return 'No tasks match your current filter. Try changing your filter settings.'
}

const startEditing = (todo) => {
  if (todo.completed) return
  
  editingTodoId = todo.id
  todo.editing = true
  editText.value = todo.text
  
  nextTick(() => {
    if (editInputRef.value) {
      const input = editInputRef.value
      input.focus()
      input.select()
    }
  })
}

const saveEdit = (todo) => {
  if (editingTodoId === todo.id && editText.value.trim()) {
    emit('edit-todo', todo.id, editText.value.trim())
  }
  cancelEdit(todo)
}

const cancelEdit = (todo) => {
  todo.editing = false
  editingTodoId = null
  editText.value = ''
}
</script>

<style scoped>
.todo-list-container {
  min-height: 200px;
}

.empty-state {
  text-align: center;
  padding: 3rem 1rem;
  background: white;
  border-radius: 12px;
  border: 2px dashed #e0e0e0;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-title {
  font-size: 1.5rem;
  color: #666;
  margin-bottom: 0.5rem;
}

.empty-description {
  color: #999;
  max-width: 400px;
  margin: 0 auto;
  line-height: 1.5;
}

.todo-items {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.todo-item {
  background: white;
  border-radius: 8px;
  padding: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.2s;
  border-left: 4px solid #e0e0e0;
}

.todo-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.todo-item.completed {
  opacity: 0.7;
  background-color: #f8f9fa;
}

.todo-item.high {
  border-left-color: #d32f2f;
}

.todo-item.medium {
  border-left-color: #f57c00;
}

.todo-item.low {
  border-left-color: #388e3c;
}

.todo-item.editing {
  border-left-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.todo-item-content {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.todo-checkbox {
  flex-shrink: 0;
}

.checkbox-input {
  display: none;
}

.checkbox-label {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border: 2px solid #e0e0e0;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.checkbox-label:hover {
  border-color: #667eea;
}

.checkbox-input:checked + .checkbox-label {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: transparent;
}

.checkbox-input:checked + .checkbox-label .checkmark {
  opacity: 1;
  transform: scale(1);
}

.checkmark {
  width: 12px;
  height: 12px;
  background: white;
  clip-path: polygon(14% 44%, 0 65%, 50% 100%, 100% 16%, 80% 0%, 43% 62%);
  opacity: 0;
  transform: scale(0);
  transition: all 0.2s;
}

.todo-text-container {
  flex: 1;
  min-width: 0;
}

.todo-text-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.todo-text {
  font-size: 1rem;
  line-height: 1.5;
  color: #333;
  word-break: break-word;
  cursor: text;
  user-select: text;
}

.todo-text.completed-text {
  text-decoration: line-through;
  color: #999;
}

.todo-text:hover {
  color: #667eea;
}

.todo-meta {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.todo-priority {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.25rem 0.75rem;
  border-radius: 12px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.todo-priority.high {
  background-color: #fee;
  color: #d32f2f;
}

.todo-priority.medium {
  background-color: #fff3e0;
  color: #f57c00;
}

.todo-priority.low {
  background-color: #e8f5e9;
  color: #388e3c;
}

.todo-date {
  font-size: 0.75rem;
  color: #999;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.todo-date::before {
  content: '📅';
  font-size: 0.7rem;
}

.todo-edit-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.edit-input {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 2px solid #667eea;
  border-radius: 6px;
  font-size: 1rem;
  outline: none;
  transition: all 0.2s;
}

.edit-input:focus {
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.edit-hints {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint-text {
  font-size: 0.75rem;
  color: #999;
  font-style: italic;
}

.todo-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  background: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1rem;
  transition: all 0.2s;
  opacity: 0.7;
}

.action-btn:hover {
  opacity: 1;
  transform: scale(1.1);
}

.edit-btn:hover {
  background-color: #e3f2fd;
  color: #1976d2;
}

.delete-btn:hover {
  background-color: #ffebee;
  color: #d32f2f;
}

@media (max-width: 768px) {
  .todo-item-content {
    flex-wrap: wrap;
  }
  
  .todo-actions {
    margin-left: auto;
  }
  
  .todo-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
}

@media (max-width: 480px) {
  .todo-item {
    padding: 0.75rem;
  }
  
  .todo-item-content {
    gap: 0.75rem;
  }
  
  .todo-text {
    font-size: 0.9rem;
  }
  
  .action-btn {
    width: 28px;
    height: 28px;
    font-size: 0.9rem;
  }
}
</style>