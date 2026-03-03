<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">Premium Electronics for Modern Living</h1>
          <p class="hero-subtitle">Discover the latest tech gadgets and accessories at unbeatable prices</p>
          <div class="hero-actions">
            <router-link to="/products" class="btn btn-primary btn-large">
              <i class="fas fa-shopping-bag"></i>
              Shop Now
            </router-link>
            <router-link to="/products" class="btn btn-outline btn-large">
              <i class="fas fa-star"></i>
              Featured Products
            </router-link>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://images.pexels.com/photos/356056/pexels-photo-356056.jpeg?auto=compress&cs=tinysrgb&h=600" alt="Modern Electronics">
        </div>
      </div>
    </section>

    <!-- Featured Categories -->
    <section class="categories-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Shop by Category</h2>
          <p class="section-subtitle">Browse our wide range of electronic categories</p>
        </div>
        
        <div class="categories-grid">
          <div class="category-card" v-for="category in categories" :key="category.id">
            <div class="category-icon">
              <i :class="category.icon"></i>
            </div>
            <h3 class="category-name">{{ category.name }}</h3>
            <p class="category-description">{{ category.description }}</p>
            <router-link to="/products" class="category-link">
              Browse Products <i class="fas fa-arrow-right"></i>
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="featured-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Featured Products</h2>
          <p class="section-subtitle">Our most popular items this month</p>
        </div>
        
        <div class="products-grid">
          <div v-for="product in featuredProducts" :key="product.id" class="product-card">
            <div class="product-image">
              <img :src="product.image" :alt="product.name">
              <div v-if="product.stock < 5" class="featured-badge">Popular</div>
            </div>
            <div class="product-info">
              <div class="product-header">
                <h3 class="product-name">{{ product.name }}</h3>
                <span class="product-category">{{ product.category }}</span>
              </div>
              <p class="product-description">{{ product.description }}</p>
              
              <div class="product-footer">
                <div class="product-price">${{ product.price.toFixed(2) }}</div>
                <div class="product-stock">
                  <i class="fas fa-box"></i>
                  {{ product.stock }} in stock
                </div>
              </div>
              
              <div class="product-actions">
                <button 
                  @click="addToCart(product)" 
                  class="btn btn-primary"
                  :disabled="product.stock === 0"
                >
                  <i class="fas fa-cart-plus"></i>
                  Add to Cart
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <div class="section-footer">
          <router-link to="/products" class="btn btn-outline">
            View All Products <i class="fas fa-arrow-right"></i>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-shipping-fast"></i>
            </div>
            <h3 class="feature-title">Free Shipping</h3>
            <p class="feature-description">Free delivery on orders over $50</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-shield-alt"></i>
            </div>
            <h3 class="feature-title">Secure Payment</h3>
            <p class="feature-description">100% secure payment processing</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-undo"></i>
            </div>
            <h3 class="feature-title">30-Day Returns</h3>
            <p class="feature-description">Easy returns within 30 days</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-headset"></i>
            </div>
            <h3 class="feature-title">24/7 Support</h3>
            <p class="feature-description">Round-the-clock customer support</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Newsletter Section -->
    <section class="newsletter-section">
      <div class="container">
        <div class="newsletter-content">
          <h2 class="newsletter-title">Stay Updated</h2>
          <p class="newsletter-subtitle">Subscribe to our newsletter for the latest deals and product releases</p>
          
          <form @submit.prevent="subscribeNewsletter" class="newsletter-form">
            <div class="form-group">
              <input 
                type="email" 
                v-model="email" 
                placeholder="Enter your email address" 
                required
                class="form-input"
              >
              <button type="submit" class="btn btn-primary">
                Subscribe <i class="fas fa-paper-plane"></i>
              </button>
            </div>
            <p class="form-note">By subscribing, you agree to our Privacy Policy</p>
          </form>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const email = ref('')

// Categories data
const categories = [
  {
    id: 1,
    name: 'Electronics',
    description: 'Headphones, speakers, and audio equipment',
    icon: 'fas fa-headphones'
  },
  {
    id: 2,
    name: 'Wearables',
    description: 'Smart watches and fitness trackers',
    icon: 'fas fa-clock'
  },
  {
    id: 3,
    name: 'Computers',
    description: 'Laptops, monitors, and accessories',
    icon: 'fas fa-laptop'
  },
  {
    id: 4,
    name: 'Cameras',
    description: 'DSLR, mirrorless, and accessories',
    icon: 'fas fa-camera'
  },
  {
    id: 5,
    name: 'Smart Home',
    description: 'Home automation and IoT devices',
    icon: 'fas fa-home'
  },
  {
    id: 6,
    name: 'Accessories',
    description: 'Cables, chargers, and peripherals',
    icon: 'fas fa-keyboard'
  }
]

// Get featured products (first 4 products)
const featuredProducts = computed(() => {
  return cartStore.products.slice(0, 4)
})

const addToCart = (product) => {
  if (product.stock > 0) {
    cartStore.addToCart(product)
  }
}

const subscribeNewsletter = () => {
  if (email.value) {
    alert(`Thank you for subscribing with email: ${email.value}`)
    email.value = ''
  }
}
</script>

<style scoped>
.home-page {
  padding-bottom: 4rem;
}

/* Hero Section */
.hero-section {
  background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
  color: white;
  padding: 4rem 0;
  margin-bottom: 4rem;
}

.hero-section .container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-content {
  max-width: 600px;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  line-height: 1.2;
  margin-bottom: 1.5rem;
}

.hero-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.btn-large {
  padding: 1rem 2rem;
  font-size: 1.1rem;
}

.hero-image {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

.hero-image img {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 12px;
}

/* Section Common Styles */
.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a237e;
  margin-bottom: 0.5rem;
}

.section-subtitle {
  color: #666;
  font-size: 1.1rem;
}

/* Categories Section */
.categories-section {
  padding: 4rem 0;
  background: #f8f9fa;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
}

.category-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.category-icon {
  width: 64px;
  height: 64px;
  background: #e8eaf6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.category-icon i {
  font-size: 1.5rem;
  color: #1a237e;
}

.category-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.category-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 1.5rem;
}

.category-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: #1a237e;
  text-decoration: none;
  font-weight: 500;
  transition: gap 0.3s;
}

.category-link:hover {
  gap: 0.75rem;
}

/* Featured Products Section */
.featured-section {
  padding: 4rem 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  margin-bottom: 3rem;
}

.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.product-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.featured-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: #ff5252;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.product-info {
  padding: 1.5rem;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.75rem;
}

.product-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  flex: 1;
}

.product-category {
  font-size: 0.75rem;
  padding: 0.25rem 0.75rem;
  background-color: #e8eaf6;
  color: #1a237e;
  border-radius: 12px;
  font-weight: 500;
  text-transform: capitalize;
}

.product-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 1rem;
  height: 2.8rem;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.product-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a237e;
}

.product-stock {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.product-stock i {
  color: #4caf50;
}

.product-actions .btn {
  width: 100%;
  padding: 0.75rem;
}

.section-footer {
  text-align: center;
}

/* Features Section */
.features-section {
  padding: 4rem 0;
  background: #f8f9fa;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feature-icon {
  width: 64px;
  height: 64px;
  background: #e8eaf6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.feature-icon i {
  font-size: 1.5rem;
  color: #1a237e;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.feature-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
}

/* Newsletter Section */
.newsletter-section {
  padding: 4rem 0;
  background: linear-gradient(135deg, #1a237e 0%, #283593 100%);
  color: white;
}

.newsletter-content {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.newsletter-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.newsletter-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.newsletter-form {
  margin-bottom: 1rem;
}

.form-group {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-input {
  flex: 1;
  padding: 1rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
}

.form-input:focus {
  outline: none;
  box-shadow: 0 0 0 2px rgba(255, 255, 255, 0.3);
}

.form-note {
  font-size: 0.9rem;
  opacity: 0.8;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .hero-section .container {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .hero-content {
    max-width: 100%;
    text-align: center;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .categories-grid,
  .products-grid,
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .form-group {
    flex-direction: column;
  }
  
  .btn-large {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .categories-grid,
  .products-grid,
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .section-title {
    font-size: 1.75rem;
  }
}
</style>