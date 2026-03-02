<template>
  <div class="category-filter">
    <h3 class="filter-title">Categories</h3>
    <div class="categories">
      <button
        v-for="category in categories"
        :key="category.id"
        class="category-btn"
        :class="{ active: selectedCategory === category.id }"
        :style="{
          '--category-color': category.color,
          'border-color': category.color
        }"
        @click="selectCategory(category.id)"
      >
        {{ category.name }}
        <span class="count">{{ getArticleCount(category.id) }}</span>
      </button>
    </div>
    <button 
      v-if="selectedCategory" 
      class="clear-filter"
      @click="clearFilter"
    >
      Clear Filter
    </button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { categories, articles } from '@/utils/data'

const props = defineProps({
  selectedCategory: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['category-change'])

const getArticleCount = (categoryId) => {
  return articles.filter(article => article.category === categoryId).length
}

const selectCategory = (categoryId) => {
  emit('category-change', categoryId)
}

const clearFilter = () => {
  emit('category-change', '')
}
</script>

<style scoped>
.category-filter {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.filter-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
}

.categories {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.category-btn {
  padding: 0.6rem 1rem;
  border: 2px solid;
  border-radius: 25px;
  background: white;
  color: #555;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.category-btn:hover {
  background: var(--category-color);
  color: white;
  transform: translateY(-2px);
}

.category-btn.active {
  background: var(--category-color);
  color: white;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.count {
  background: rgba(255, 255, 255, 0.2);
  padding: 0.1rem 0.5rem;
  border-radius: 10px;
  font-size: 0.8rem;
  font-weight: 600;
}

.clear-filter {
  width: 100%;
  padding: 0.8rem;
  background: #f8f9fa;
  border: 2px solid #ddd;
  border-radius: 8px;
  color: #666;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.clear-filter:hover {
  background: #e9ecef;
  border-color: #999;
  color: #333;
}

/* Responsive design */
@media (max-width: 768px) {
  .category-filter {
    padding: 1.2rem;
  }
  
  .categories {
    gap: 0.4rem;
  }
  
  .category-btn {
    padding: 0.5rem 0.8rem;
    font-size: 0.9rem;
  }
}
</style>