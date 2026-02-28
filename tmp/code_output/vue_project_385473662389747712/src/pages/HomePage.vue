<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">Discover Amazing Products</h1>
            <p class="hero-subtitle">Shop the latest trends in electronics, fashion, home goods, and more with free shipping on orders over $50.</p>
            <div class="hero-actions">
              <router-link to="/" class="btn btn-primary btn-lg">Shop Now</router-link>
              <router-link to="/" class="btn btn-secondary btn-lg">View Deals</router-link>
            </div>
          </div>
          <div class="hero-image">
            <img src="https://picsum.photos/600/400?random=hero" alt="Shopping Experience" />
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Categories -->
    <section class="categories-section">
      <div class="container">
        <h2 class="section-title">Shop by Category</h2>
        <div class="categories-grid">
          <div 
            v-for="category in categories" 
            :key="category" 
            class="category-card"
            :class="{ active: selectedCategory === category }"
            @click="selectCategory(category)"
          >
            <div class="category-icon">
              <span>{{ getCategoryIcon(category) }}</span>
            </div>
            <h3 class="category-name">{{ category }}</h3>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="products-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Featured Products</h2>
          <div class="view-all">
            <router-link to="/" class="btn btn-secondary">View All Products</router-link>
          </div>
        </div>
        
        <div class="products-grid">
          <ProductCard 
            v-for="product in filteredProducts" 
            :key="product.id" 
            :product="product"
          />
        </div>
      </div>
    </section>

    <!-- Promo Banner -->
    <section class="promo-section">
      <div class="container">
        <div class="promo-banner">
          <div class="promo-content">
            <h2 class="promo-title">Summer Sale</h2>
            <p class="promo-text">Up to 50% off on selected items. Limited time offer!</p>
            <router-link to="/" class="btn btn-primary btn-lg">Shop the Sale</router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Testimonials -->
    <section class="testimonials-section">
      <div class="container">
        <h2 class="section-title">What Our Customers Say</h2>
        <div class="testimonials-grid">
          <div class="testimonial-card">
            <div class="testimonial-text">
              "Excellent service and fast shipping! The product quality exceeded my expectations."
            </div>
            <div class="testimonial-author">
              <div class="author-name">Sarah Johnson</div>
              <div class="author-location">New York, NY</div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="testimonial-text">
              "Great selection of products and competitive prices. Will definitely shop here again!"
            </div>
            <div class="testimonial-author">
              <div class="author-name">Michael Chen</div>
              <div class="author-location">San Francisco, CA</div>
            </div>
          </div>
          <div class="testimonial-card">
            <div class="testimonial-text">
              "The customer support team was very helpful when I had questions about my order."
            </div>
            <div class="testimonial-author">
              <div class="author-name">Emily Rodriguez</div>
              <div class="author-location">Miami, FL</div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import ProductCard from '../components/ProductCard.vue'
import { products, categories, getProductsByCategory } from '../utils/products'

const selectedCategory = ref('All Categories')

const filteredProducts = computed(() => {
  return getProductsByCategory(selectedCategory.value)
})

const selectCategory = (category) => {
  selectedCategory.value = category
}

const getCategoryIcon = (category) => {
  const icons = {
    'Electronics': '📱',
    'Clothing': '👕',
    'Home & Kitchen': '🏠',
    'Wearables': '⌚',
    'Photography': '📷',
    'Gaming': '🎮',
    'Fitness': '💪',
    'All Categories': '🛍️'
  }
  return icons[category] || '📦'
}
</script>

<style scoped>
.home-page {
  padding-bottom: 4rem;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  padding: 4rem 0;
  margin-bottom: 4rem;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-title {
  font-size: 3rem;
  color: white;
  margin-bottom: 1.5rem;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 2rem;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 1rem;
}

.hero-image img {
  width: 100%;
  height: auto;
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
}

/* Categories Section */
.categories-section {
  margin-bottom: 4rem;
}

.section-title {
  text-align: center;
  margin-bottom: 2rem;
  color: var(--text-primary);
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.category-card {
  background: white;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.category-card:hover {
  border-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.category-card.active {
  border-color: var(--primary-color);
  background-color: rgba(37, 99, 235, 0.05);
}

.category-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.category-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
}

/* Products Section */
.products-section {
  margin-bottom: 4rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

/* Promo Section */
.promo-section {
  margin-bottom: 4rem;
}

.promo-banner {
  background: linear-gradient(135deg, var(--secondary-color) 0%, #059669 100%);
  border-radius: var(--radius-xl);
  padding: 4rem;
  text-align: center;
  color: white;
}

.promo-title {
  font-size: 2.5rem;
  color: white;
  margin-bottom: 1rem;
}

.promo-text {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

/* Testimonials Section */
.testimonials-section {
  margin-bottom: 4rem;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.testimonial-card {
  background: white;
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.testimonial-text {
  font-size: 1.125rem;
  font-style: italic;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
  line-height: 1.6;
}

.testimonial-author {
  border-top: 1px solid var(--border-color);
  padding-top: 1rem;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
}

.author-location {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .hero-image {
    order: -1;
  }
  
  .categories-grid,
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .testimonials-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .categories-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .promo-banner {
    padding: 2rem;
  }
  
  .promo-title {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .categories-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
}
</style>