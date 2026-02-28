<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Discover Amazing Products</h1>
        <p class="hero-subtitle">Shop the latest trends in electronics, fashion, home goods, and more with free shipping on orders over $100.</p>
        <div class="hero-actions">
          <button class="hero-btn primary" @click="scrollToProducts">
            Shop Now
            <i class="fas fa-arrow-right"></i>
          </button>
          <button class="hero-btn secondary" @click="scrollToFeatures">
            Learn More
          </button>
        </div>
      </div>
      <div class="hero-image">
        <img src="https://picsum.photos/600/400?random=hero" alt="Shopping experience">
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section" ref="featuresSection">
      <h2 class="section-title">Why Choose ShopVue</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-shipping-fast"></i>
          </div>
          <h3 class="feature-title">Free Shipping</h3>
          <p class="feature-description">Free delivery on orders over $100. Fast and reliable shipping worldwide.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-shield-alt"></i>
          </div>
          <h3 class="feature-title">Secure Payment</h3>
          <p class="feature-description">100% secure payment processing with SSL encryption and fraud protection.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-undo-alt"></i>
          </div>
          <h3 class="feature-title">Easy Returns</h3>
          <p class="feature-description">30-day return policy. No questions asked for returns within the policy period.</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <i class="fas fa-headset"></i>
          </div>
          <h3 class="feature-title">24/7 Support</h3>
          <p class="feature-description">Round-the-clock customer support via chat, email, and phone.</p>
        </div>
      </div>
    </section>

    <!-- Featured Products -->
    <section class="products-section" ref="productsSection">
      <div class="section-header">
        <h2 class="section-title">Featured Products</h2>
        <div class="category-filters">
          <button 
            v-for="category in categories" 
            :key="category"
            :class="['category-btn', { active: activeCategory === category }]"
            @click="filterByCategory(category)"
          >
            {{ category }}
          </button>
        </div>
      </div>
      
      <div class="products-grid">
        <ProductCard 
          v-for="product in filteredProducts" 
          :key="product.id"
          :product="product"
          @add-to-cart="addToCart"
        />
      </div>
      
      <div class="section-footer">
        <button class="view-all-btn" @click="viewAllProducts">
          View All Products
          <i class="fas fa-arrow-right"></i>
        </button>
      </div>
    </section>

    <!-- Promo Banner -->
    <section class="promo-section">
      <div class="promo-content">
        <h2 class="promo-title">Summer Sale</h2>
        <p class="promo-subtitle">Up to 50% off on selected items. Limited time offer!</p>
        <button class="promo-btn" @click="scrollToProducts">
          Shop Sale
          <i class="fas fa-fire"></i>
        </button>
      </div>
    </section>

    <!-- Testimonials -->
    <section class="testimonials-section">
      <h2 class="section-title">What Our Customers Say</h2>
      <div class="testimonials-grid">
        <div class="testimonial-card">
          <div class="testimonial-rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
          <p class="testimonial-text">
            "Excellent shopping experience! The products are high quality and the delivery was super fast."
          </p>
          <div class="testimonial-author">
            <img src="https://picsum.photos/50/50?random=user1" alt="Sarah Johnson" class="author-avatar">
            <div class="author-info">
              <div class="author-name">Sarah Johnson</div>
              <div class="author-role">Verified Buyer</div>
            </div>
          </div>
        </div>
        <div class="testimonial-card">
          <div class="testimonial-rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star-half-alt"></i>
          </div>
          <p class="testimonial-text">
            "Great customer service and easy returns. Will definitely shop here again!"
          </p>
          <div class="testimonial-author">
            <img src="https://picsum.photos/50/50?random=user2" alt="Michael Chen" class="author-avatar">
            <div class="author-info">
              <div class="author-name">Michael Chen</div>
              <div class="author-role">Verified Buyer</div>
            </div>
          </div>
        </div>
        <div class="testimonial-card">
          <div class="testimonial-rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
          </div>
          <p class="testimonial-text">
            "The quality exceeded my expectations. Fast shipping and excellent packaging."
          </p>
          <div class="testimonial-author">
            <img src="https://picsum.photos/50/50?random=user3" alt="Emma Wilson" class="author-avatar">
            <div class="author-info">
              <div class="author-name">Emma Wilson</div>
              <div class="author-role">Verified Buyer</div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Newsletter -->
    <section class="newsletter-section">
      <div class="newsletter-content">
        <h2 class="newsletter-title">Stay Updated</h2>
        <p class="newsletter-subtitle">Subscribe to our newsletter for exclusive deals and new arrivals.</p>
        <form class="newsletter-form" @submit.prevent="subscribeNewsletter">
          <input 
            type="email" 
            v-model="email" 
            placeholder="Enter your email address" 
            class="newsletter-input"
            required
          >
          <button type="submit" class="newsletter-btn">
            Subscribe
            <i class="fas fa-paper-plane"></i>
          </button>
        </form>
        <p class="newsletter-note">By subscribing, you agree to our Privacy Policy.</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ProductCard from '../components/ProductCard.vue'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const featuresSection = ref(null)
const productsSection = ref(null)
const activeCategory = ref('All')
const email = ref('')

const categories = computed(() => {
  const allCategories = ['All', ...new Set(cartStore.getAllProducts().map(p => p.category))]
  return allCategories
})

const filteredProducts = computed(() => {
  const products = cartStore.getAllProducts()
  if (activeCategory.value === 'All') {
    return products
  }
  return products.filter(product => product.category === activeCategory.value)
})

const scrollToProducts = () => {
  productsSection.value?.scrollIntoView({ behavior: 'smooth' })
}

const scrollToFeatures = () => {
  featuresSection.value?.scrollIntoView({ behavior: 'smooth' })
}

const filterByCategory = (category) => {
  activeCategory.value = category
}

const addToCart = (product) => {
  cartStore.addToCart(product)
}

const viewAllProducts = () => {
  activeCategory.value = 'All'
  scrollToProducts()
}

const subscribeNewsletter = () => {
  if (email.value) {
    alert(`Thank you for subscribing with ${email.value}!`)
    email.value = ''
  }
}

onMounted(() => {
  // Any initialization logic can go here
})
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 4rem;
}

/* Hero Section */
.hero-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 2rem;
  border-radius: 16px;
  margin-top: 1rem;
}

.hero-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: bold;
  line-height: 1.2;
  margin: 0;
}

.hero-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  line-height: 1.6;
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.hero-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  border: none;
}

.hero-btn.primary {
  background: white;
  color: #4f46e5;
}

.hero-btn.primary:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

.hero-btn.secondary {
  background: transparent;
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.hero-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: white;
}

.hero-image {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
}

.hero-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
}

/* Features Section */
.features-section {
  padding: 3rem 0;
}

.section-title {
  text-align: center;
  font-size: 2rem;
  color: #1f2937;
  margin-bottom: 3rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  color: #4f46e5;
  margin-bottom: 1rem;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 0.75rem;
}

.feature-description {
  color: #6b7280;
  line-height: 1.6;
}

/* Products Section */
.products-section {
  padding: 3rem 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  gap: 1rem;
}

.category-filters {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 20px;
  color: #4b5563;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
}

.category-btn:hover {
  border-color: #4f46e5;
  color: #4f46e5;
}

.category-btn.active {
  background: #4f46e5;
  border-color: #4f46e5;
  color: white;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.section-footer {
  text-align: center;
  margin-top: 2rem;
}

.view-all-btn {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.view-all-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

/* Promo Section */
.promo-section {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 4rem 2rem;
  border-radius: 16px;
  text-align: center;
}

.promo-content {
  max-width: 600px;
  margin: 0 auto;
}

.promo-title {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.promo-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.promo-btn {
  background: white;
  color: #f5576c;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.promo-btn:hover {
  background: #f3f4f6;
  transform: translateY(-2px);
}

/* Testimonials Section */
.testimonials-section {
  padding: 3rem 0;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.testimonial-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.testimonial-rating {
  color: #fbbf24;
  margin-bottom: 1rem;
  font-size: 1.2rem;
}

.testimonial-text {
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 1.5rem;
  font-style: italic;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
}

.author-info {
  flex: 1;
}

.author-name {
  font-weight: 600;
  color: #1f2937;
}

.author-role {
  font-size: 0.875rem;
  color: #6b7280;
}

/* Newsletter Section */
.newsletter-section {
  background: #f9fafb;
  padding: 4rem 2rem;
  border-radius: 16px;
  text-align: center;
}

.newsletter-content {
  max-width: 600px;
  margin: 0 auto;
}

.newsletter-title {
  font-size: 2rem;
  color: #1f2937;
  margin-bottom: 1rem;
}

.newsletter-subtitle {
  color: #6b7280;
  margin-bottom: 2rem;
}

.newsletter-form {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.newsletter-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.newsletter-input:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.newsletter-btn {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.newsletter-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

.newsletter-note {
  font-size: 0.875rem;
  color: #6b7280;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .hero-section {
    grid-template-columns: 1fr;
    text-align: center;
  }
  
  .hero-content {
    align-items: center;
  }
  
  .hero-subtitle {
    max-width: 100%;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-actions {
    flex-direction: column;
    align-items: center;
  }
  
  .hero-btn {
    width: 100%;
    justify-content: center;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .category-filters {
    width: 100%;
    justify-content: center;
  }
  
  .newsletter-form {
    flex-direction: column;
  }
  
  .newsletter-btn {
    width: 100%;
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-section,
  .promo-section,
  .newsletter-section {
    padding: 2rem 1rem;
  }
  
  .section-title {
    font-size: 1.75rem;
  }
  
  .features-grid,
  .testimonials-grid {
    grid-template-columns: 1fr;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>