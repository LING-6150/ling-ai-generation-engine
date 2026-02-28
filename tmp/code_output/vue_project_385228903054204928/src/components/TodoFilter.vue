<template>
  <div class="todo-filter">
    <div class="filter-tabs">
      <button
        v-for="filter in filters"
        :key="filter.value"
        class="filter-tab"
        :class="{ active: currentFilter === filter.value }"
        @click="$emit('filter-change', filter.value)"
      >
        {{ filter.label }}
        <span v-if="filter.value === 'all'" class="filter-count">{{ totalCount }}</span>
        <span v-if="filter.value === 'active'" class="filter-count">{{ activeCount }}</span>
        <span v-if="filter.value === 'completed'" class="filter-count">{{ completedCount }}</span>
      </button>
    </div>
    
    <div class="filter-stats">
      <div class="stat-item">
        <span class="stat-label">Active:</span>
        <span class="stat-value">{{ activeCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">Completed:</span>
        <span class="stat-value">{{ completedCount }}</span>
      </div>
      <div class="stat-item">
        <span class="stat-label">Total:</span>
        <span class="stat-value">{{ totalCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  filters: {
    type: Array,
    required: true
  },
  currentFilter: {
    type: String,
    required: true
  },
  activeCount: {
    type: Number,
    default: 0
  },
  completedCount: {
    type: Number,
    default: 0
  }
})

defineEmits(['filter-change'])

const totalCount = computed(() => props.activeCount + props.completedCount)
</script>

<style scoped>
.todo-filter {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #eee;
  background: #f8f9fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-tabs {
  display: flex;
  gap: 0.5rem;
}

.filter-tab {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-tab:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-tab.active {
  background: #667eea;
  border-color: #667eea;
  color: white;
}

.filter-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.125rem 0.5rem;
  border-radius: 10px;
  font-size: 0.75rem;
  font-weight: 600;
}

.filter-stats {
  display: flex;
  gap: 1.5rem;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9rem;
}

.stat-label {
  color: #666;
}

.stat-value {
  font-weight: 600;
  color: #333;
}

@media (max-width: 768px) {
  .todo-filter {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .filter-tabs {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .filter-stats {
    justify-content: center;
    flex-wrap: wrap;
    gap: 1rem;
  }
}
</style>