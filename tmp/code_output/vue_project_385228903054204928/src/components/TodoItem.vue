<template>
  <div class="todo-item" :class="{ completed: task.completed, editing: task.editing }">
    <div class="todo-content" v-if="!task.editing">
      <div class="todo-checkbox">
        <input
          type="checkbox"
          :id="'task-' + task.id"
          :checked="task.completed"
          @change="$emit('toggle', task.id)"
          class="checkbox-input"
        />
        <label :for="'task-' + task.id" class="checkbox-label"></label>
      </div>
      
      <div class="todo-text" @dblclick="$emit('edit', task)">
        <span class="todo-title" :class="{ 'completed-text': task.completed }">
          {{ task.title }}
        </span>
        <div class="todo-meta">
          <span class="todo-status" :class="task.completed ? 'status-completed' : 'status-active'">
            {{ task.completed ? 'Completed' : 'Active' }}
          </span>
          <span class="todo-date">Added: {{ formatDate(task.createdAt) }}</span>
        </div>
      </div>
      
      <div class="todo-actions">
        <button class="btn-icon" @click="$emit('edit', task)" title="Edit">
          ✏️
        </button>
        <button class="btn-icon btn-delete" @click="$emit('delete', task.id)" title="Delete">
          🗑️
        </button>
      </div>
    </div>
    
    <div class="todo-edit" v-else>
      <input
        type="text"
        v-model="editTitle"
        ref="editInput"
        class="edit-input"
        @keyup.enter="saveEdit"
        @keyup.escape="cancelEdit"
        @blur="saveEdit"
      />
      <div class="edit-actions">
        <button class="btn btn-primary" @click="saveEdit">
          Save
        </button>
        <button class="btn btn-outline" @click="cancelEdit">
          Cancel
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'

const props = defineProps({
  task: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['toggle', 'edit', 'delete', 'update'])

const editTitle = ref(props.task.title)
const editInput = ref(null)

const formatDate = (date) => {
  if (!date) return 'Today'
  return new Date(date).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  })
}

const saveEdit = () => {
  if (editTitle.value.trim()) {
    emit('update', {
      ...props.task,
      title: editTitle.value.trim()
    })
  } else {
    cancelEdit()
  }
}

const cancelEdit = () => {
  editTitle.value = props.task.title
  emit('update', { ...props.task, editing: false })
}

onMounted(() => {
  if (props.task.editing) {
    nextTick(() => {
      editInput.value?.focus()
      editInput.value?.select()
    })
  }
})
</script>

<style scoped>
.todo-item {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
  transition: all 0.2s;
}

.todo-item:hover {
  background: #f9f9f9;
}

.todo-item.completed {
  opacity: 0.8;
}

.todo-content {
  display: flex;
  align-items: flex-start;
  gap: 1rem;
}

.todo-checkbox {
  position: relative;
  margin-top: 0.25rem;
}

.checkbox-input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkbox-label {
  display: block;
  width: 24px;
  height: 24px;
  border: 2px solid #ddd;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s;
  position: relative;
}

.checkbox-label:hover {
  border-color: #667eea;
}

.checkbox-label::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) scale(0);
  width: 12px;
  height: 12px;
  background: #667eea;
  border-radius: 50%;
  transition: transform 0.2s;
}

.checkbox-input:checked + .checkbox-label {
  border-color: #667eea;
  background: #667eea;
}

.checkbox-input:checked + .checkbox-label::after {
  transform: translate(-50%, -50%) scale(1);
  background: white;
}

.todo-text {
  flex: 1;
  cursor: pointer;
}

.todo-title {
  display: block;
  font-size: 1rem;
  color: #333;
  margin-bottom: 0.25rem;
  transition: color 0.2s;
}

.completed-text {
  text-decoration: line-through;
  color: #999;
}

.todo-meta {
  display: flex;
  gap: 1rem;
  font-size: 0.85rem;
  color: #888;
}

.todo-status {
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.status-active {
  background: #e6fffa;
  color: #0d9488;
}

.status-completed {
  background: #f0f9ff;
  color: #0369a1;
}

.todo-actions {
  display: flex;
  gap: 0.5rem;
  opacity: 0;
  transition: opacity 0.2s;
}

.todo-item:hover .todo-actions {
  opacity: 1;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 4px;
  font-size: 1rem;
  transition: background 0.2s;
}

.btn-icon:hover {
  background: #f0f0f0;
}

.btn-delete:hover {
  background: #fee2e2;
  color: #dc2626;
}

.todo-edit {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.edit-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 2px solid #667eea;
  border-radius: 6px;
  font-size: 1rem;
  outline: none;
}

.edit-input:focus {
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.edit-actions {
  display: flex;
  gap: 0.5rem;
}

.edit-actions .btn {
  padding: 0.5rem 1rem;
  font-size: 0.9rem;
}

@media (max-width: 768px) {
  .todo-content {
    flex-wrap: wrap;
  }
  
  .todo-actions {
    opacity: 1;
    margin-top: 0.5rem;
    width: 100%;
    justify-content: flex-end;
  }
  
  .todo-edit {
    flex-direction: column;
    gap: 0.75rem;
  }
  
  .edit-input {
    width: 100%;
  }
  
  .edit-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>