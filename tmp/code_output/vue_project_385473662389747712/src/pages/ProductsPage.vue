<template>
  <div class="products-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">All Products</h1>
        <p class="page-subtitle">Discover our curated collection of premium products</p>
      </div>

      <div class="products-layout">
        <aside class="sidebar">
          <div class="filter-section">
            <h3 class="filter-title">Categories</h3>
            <div class="filter-options">
              <label v-for="category in categories" :key="category.id" class="filter-option">
                <input
                  type="checkbox"
                  :value="category.id"
                  v-model="selectedCategories"
                  class="filter-checkbox"
                />
                <span class="filter-label">{{ category.name }}</span>
                <span class="filter-count">({{ category.count }})</span>
              </label>
            </div>
          </div>

          <div class="filter-section">
            <h3 class="filter-title">Price Range</h3>
            <div class="price-range">
              <div class="price-values">
                <span class="price-min">${{ priceRange[0] }}</span>
                <span class="price-max">${{ priceRange[1] }}</span>
              </div>
              <input
                type="range"
                v-model="priceRange[0]"
                :min="0"
                :max="priceRange[1]"
                class="price-slider"
                @input="handlePriceChange"
              />
              <input
                type="range"
                v-model="priceRange[1]"
                :min="priceRange[0]"
                :max="1000"
                class="price-slider"
                @input="handlePriceChange"
              />
            </div>
          </div>

          <div class="filter-section">
            <h3 class="filter-title">Sort By</h3>
            <select v-model="sortBy" class="sort-select">
              <option value="featured">Featured</option>
              <option value="price-low">Price: Low to High</option>
              <option value="price-high">Price: High to Low</option>
              <option value="rating">Highest Rated</option>
              <option value="newest">Newest Arrivals</option>
            </select>
          </div>

          <button class="clear-filters-btn" @click="clearFilters">
            Clear All Filters
          </button>
        </aside>

        <main class="products-main">
          <div class="products-header">
            <div class="products-count">
              Showing {{ filteredProducts.length }} of {{ allProducts.length }} products
            </div>
            <div class="view-options">
              <button
                class="view-option-btn"
                :class="{ active: viewMode === 'grid' }"
                @click="viewMode = 'grid'"
              >
                ▦
              </button>
              <button
                class="view-option-btn"
                :class="{ active: viewMode === 'list' }"
                @click="viewMode = 'list'"
              >
                ≡
              </button>
            </div>
          </div>

          <div v-if="filteredProducts.length === 0" class="no-products">
            <div class="no-products-icon">🔍</div>
            <h3 class="no-products-title">No products found</h3>
            <p class="no-products-description">Try adjusting your filters or search term</p>
            <button class="clear-filters-btn" @click="clearFilters">
              Clear All Filters
            </button>
          </div>

          <div v-else :class="['products-grid', { 'list-view': viewMode === 'list' }]">
            <ProductCard
              v-for="product in filteredProducts"
              :key="product.id"
              :product="product"
              :class="{ 'list-view-card': viewMode === 'list' }"
            />
          </div>

          <div v-if="filteredProducts.length > 0" class="pagination">
            <button
              class="pagination-btn"
              :disabled="currentPage === 1"
              @click="currentPage--"
            >
              ← Previous
            </button>
            <div class="page-numbers">
              <span class="page-info">
                Page {{ currentPage }} of {{ totalPages }}
              </span>
            </div>
            <button
              class="pagination-btn"
              :disabled="currentPage === totalPages"
              @click="currentPage++"
            >
              Next →
            </button>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'

const route = useRoute()

const allProducts = [
  {
    id: 1,
    name: 'Wireless Bluetooth Headphones',
    category: 'electronics',
    price: 89.99,
    rating: 4.5,
    image: 'https://picsum.photos/400/300?random=4',
    features: ['Noise Cancelling', '30hr Battery', 'Wireless'],
    inStock: true
  },
  {
    id: 2,
    name: 'Premium Leather Backpack',
    category: 'fashion',
    price: 129.99,
    rating: 4.8,
    image: 'https://picsum.photos/400/300?random=5',
    features: ['Waterproof', 'Laptop Sleeve', 'Multiple Pockets'],
    inStock: true
  },
  {
    id: 3,
    name: 'Smart Fitness Watch',
    category: 'electronics',
    price: 199.99,
    rating: 4.6,
    image: 'https://picsum.photos/400/300?random=6',
    features: ['Heart Rate Monitor', 'GPS', 'Water Resistant'],
    inStock: true
  },
  {
    id: 4,
    name: 'Ceramic Coffee Mug Set',
    category: 'home',
    price: 34.99,
    rating: 4.7,
    image: 'https://picsum.photos/400/300?random=7',
    features: ['Microwave Safe', 'Dishwasher Safe', '4 Pieces'],
    inStock: true
  },
  {
    id: 5,
    name: 'Ergonomic Office Chair',
    category: 'home',
    price: 299.99,
    rating: 4.9,
    image: 'https://picsum.photos/400/300?random=8',
    features: ['Adjustable Height', 'Lumbar Support', 'Swivel Base'],
    inStock: true
  },
  {
    id: 6,
    name: 'Professional Camera',
    category: 'electronics',
    price: 899.99,
    rating: 4.8,
    image: 'https://picsum.photos/400/300?random=9',
    features: ['24MP Sensor', '4K Video', 'Interchangeable Lenses'],
    inStock: false
  },
  {
    id: 7,
    name: 'Designer Sunglasses',
    category: 'fashion',
    price: 159.99,
    rating: 4.4,
    image: 'https://picsum.photos/400/300?random=10',
    features: ['UV Protection', 'Polarized Lenses', 'Lightweight'],
    inStock: true
  },
  {
    id: 8,
    name: 'Portable Bluetooth Speaker',
    category: 'electronics',
    price: 79.99,
    rating: 4.3,
    image: 'https://picsum.photos/400/300?random=11',
    features: ['360° Sound', 'IPX7 Waterproof', '20hr Battery'],
    inStock: true
  },
  {
    id: 9,
    name: 'Yoga Mat Premium',
    category: 'sports',
    price: 49.99,
    rating: 4.5,
    image: 'https://picsum.photos/400/300?random=12',
    features: ['Non-Slip Surface', 'Eco-Friendly', 'Extra Thick'],
    inStock: true
  },
  {
    id: 10,
    name: 'Stainless Steel Cookware Set',
    category: 'home',
    price: 249.99,
    rating: 4.7,
    image: 'https://picsum.photos/400/300?random=13',
    features: ['Induction Compatible', 'Oven Safe', '10 Pieces'],
    inStock: true
  },
  {
    id: 11,
    name: 'Running Shoes',
    category: 'sports',
    price: 119.99,
    rating: 4.6,
    image: 'https://picsum.photos/400/300?random=14',
    features: ['Cushioned Sole', 'Breathable Mesh', 'Lightweight'],
    inStock: true
  },
  {
    id: 12,
    name: 'Gaming Laptop',
    category: 'electronics',
    price: 1299.99,
    rating: 4.9,
    image: 'https://picsum.photos/400/300?random=15',
    features: ['RTX Graphics', '144Hz Display', 'RGB Keyboard'],
    inStock: true
  }
]

const categories = [
  { id: 'all', name: 'All Categories', count: allProducts.length },
  { id: 'electronics', name: 'Electronics', count: allProducts.filter(p => p.category === 'electronics').length },
  { id: 'fashion', name: 'Fashion', count: allProducts.filter(p => p.category === 'fashion').length },
  { id: 'home', name: 'Home & Living', count: allProducts.filter(p => p.category === 'home').length },
  { id: 'sports', name: 'Sports & Outdoors', count: allProducts.filter(p => p.category === 'sports').length }
]

const selectedCategories = ref(['all'])
const priceRange = ref([0, 1000])
const sortBy = ref('featured')
const viewMode = ref('grid')
const currentPage = ref(1)
const itemsPerPage = 8

const filteredProducts = computed(() => {
  let filtered = [...allProducts]

  if (route.query.search) {
    const searchTerm = route.query.search.toLowerCase()
    filtered = filtered.filter(product =>
      product.name.toLowerCase().includes(searchTerm) ||
      product.category.toLowerCase().includes(searchTerm)
    )
  }

  if (!selectedCategories.value.includes('all')) {
    filtered = filtered.filter(product =>
      selectedCategories.value.includes(product.category)
    )
  }

  filtered = filtered.filter(product =>
    product.price >= priceRange.value[0] &&
    product.price <= priceRange.value[1]
  )

  switch (sortBy.value) {
    case 'price-low':
      filtered.sort((a, b) => a.price - b.price)
      break
    case 'price-high':
      filtered.sort((a, b) => b.price - a.price)
      break
    case 'rating':
      filtered.sort((a, b) => b.rating - a.rating)
      break
    case 'newest':
      filtered.sort((a, b) => b.id - a.id)
      break
    default:
      filtered.sort((a, b) => a.id - b.id)
  }

  return filtered
})

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return filteredProducts.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(filteredProducts.value.length / itemsPerPage)
})

const handlePriceChange = () => {
  if (priceRange.value[0] > priceRange.value[1]) {
    priceRange.value[0] = priceRange.value[1]
  }
}

const clearFilters = () => {
  selectedCategories.value = ['all']
  priceRange.value = [0, 1000]
  sortBy.value = 'featured'
  currentPage.value = 1
}

watch(filteredProducts, () => {
  currentPage.value = 1
})

watch(totalPages, (newTotal) => {
  if (currentPage.value > newTotal && newTotal > 0) {
    currentPage.value = newTotal
  }
})
</script>

<style scoped>
.products-page {
  padding: 3rem 0;
}

.page-header {
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: 1.125rem;
}

.products-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 3rem;
}

.sidebar {
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  padding: 2rem;
  border: 1px solid var(--border-color);
  height: fit-content;
  position: sticky;
  top: 2rem;
}

.filter-section {
  margin-bottom: 2rem;
}

.filter-section:last-child {
  margin-bottom: 0;
}

.filter-title {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
}

.filter-options {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.filter-option {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.25rem 0;
}

.filter-checkbox {
  width: 1rem;
  height: 1rem;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
  cursor: pointer;
}

.filter-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  flex: 1;
}

.filter-count {
  font-size: 0.75rem;
  color: var(--text-light);
}

.price-range {
  padding: 0.5rem 0;
}

.price-values {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.price-min,
.price-max {
  font-size: 0.875rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.price-slider {
  width: 100%;
  height: 4px;
  background: var(--border-color);
  border-radius: var(--radius-full);
  outline: none;
  -webkit-appearance: none;
  margin: 0.5rem 0;
}

.price-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 16px;
  height: 16px;
  background: var(--primary-color);
  border-radius: var(--radius-full);
  cursor: pointer;
}

.sort-select {
  width: 100%;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 0.875rem;
  background-color: white;
  color: var(--text-primary);
  cursor: pointer;
}

.sort-select:focus {
  outline: none;
  border-color: var(--primary-color);
}

.clear-filters-btn {
  width: 100%;
  background-color: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 1.5rem;
}

.clear-filters-btn:hover {
  background-color: var(--bg-tertiary);
  border-color: var(--text-secondary);
}

.products-main {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.products-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.products-count {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.view-options {
  display: flex;
  gap: 0.5rem;
}

.view-option-btn {
  background: none;
  border: 1px solid var(--border-color);
  padding: 0.5rem 1rem;
  border-radius: var(--radius-md);
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s ease;
  color: var(--text-secondary);
}

.view-option-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.view-option-btn.active {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.products-grid.list-view {
  grid-template-columns: 1fr;
  gap: 1rem;
}

.list-view-card {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 2rem;
}

.no-products {
  text-align: center;
  padding: 4rem 2rem;
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.no-products-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.no-products-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.no-products-description {
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 2rem;
  padding: 2rem 0;
  border-top: 1px solid var(--border-color);
}

.pagination-btn {
  background-color: var(--bg-primary);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 0.5rem;
}

.page-info {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

@media (max-width: 1024px) {
  .products-layout {
    grid-template-columns: 1fr;
  }
  
  .sidebar {
    position: static;
  }
  
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .products-page {
    padding: 2rem 0;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .list-view-card {
    grid-template-columns: 1fr;
  }
  
  .pagination {
    flex-direction: column;
    gap: 1rem;
  }
}
</style>