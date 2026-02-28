<template>
  <section class="featured-products">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">Featured Products</h2>
        <p class="section-subtitle">Discover our most popular and highly rated products</p>
      </div>
      
      <div class="category-filters">
        <button 
          v-for="category in categories" 
          :key="category"
          class="category-filter"
          :class="{ active: activeCategory === category }"
          @click="setActiveCategory(category)"
        >
          {{ category }}
        </button>
      </div>
      
      <div class="products-grid">
        <ProductCard 
          v-for="product in filteredProducts" 
          :key="product.id"
          :product="product"
        />
      </div>
      
      <div class="section-footer">
        <router-link to="/" class="btn btn-secondary btn-lg">View All Products</router-link>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed } from 'vue'
import ProductCard from './ProductCard.vue'
import { products, categories } from '../utils/products'

const activeCategory = ref('All Categories')

const filteredProducts = computed(() => {
  if (activeCategory.value === 'All Categories') {
    return products.slice(0, 8)
  }
  return products
    .filter(product => product.category === activeCategory.value)
    .slice(0, 8)
})

const setActiveCategory = (category) => {
  activeCategory.value = category
}
</script>

<style scoped>
.featured-products {
  padding: 4rem 0;
  background-color: white;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.section-subtitle {
  font-size: 1.125rem;
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto;
}

.category-filters {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 3rem;
}

.category-filter {
  padding: 0.5rem 1.5rem;
  background-color: white;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-weight: 500;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-filter:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.category-filter.active {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  margin-bottom: 3rem;
}

.section-footer {
  text-align: center;
}

@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .category-filters {
    gap: 0.25rem;
  }
  
  .category-filter {
    padding: 0.375rem 1rem;
    font-size: 0.875rem;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>