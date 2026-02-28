<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <h1 class="hero-title">Discover Premium Products</h1>
          <p class="hero-subtitle">Shop the latest trends and timeless classics with exceptional quality and service.</p>
          <router-link to="/products" class="hero-button">
            Shop Now →
          </router-link>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="container">
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🚚</div>
            <h3 class="feature-title">Free Shipping</h3>
            <p class="feature-description">On orders over $50</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">↩️</div>
            <h3 class="feature-title">Easy Returns</h3>
            <p class="feature-description">30-day return policy</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔒</div>
            <h3 class="feature-title">Secure Payment</h3>
            <p class="feature-description">100% secure transactions</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⭐</div>
            <h3 class="feature-title">Quality Guarantee</h3>
            <p class="feature-description">Premium quality products</p>
          </div>
        </div>
      </div>
    </section>

    <section class="featured-products">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">Featured Products</h2>
          <router-link to="/products" class="view-all-link">
            View All Products →
          </router-link>
        </div>
        
        <div class="products-grid">
          <ProductCard
            v-for="product in featuredProducts"
            :key="product.id"
            :product="product"
          />
        </div>
      </div>
    </section>

    <section class="categories-section">
      <div class="container">
        <h2 class="section-title">Shop by Category</h2>
        <div class="categories-grid">
          <div class="category-card">
            <img src="https://picsum.photos/400/300?random=1" alt="Electronics" class="category-image" />
            <div class="category-content">
              <h3 class="category-title">Electronics</h3>
              <p class="category-description">Latest gadgets and devices</p>
            </div>
          </div>
          <div class="category-card">
            <img src="https://picsum.photos/400/300?random=2" alt="Fashion" class="category-image" />
            <div class="category-content">
              <h3 class="category-title">Fashion</h3>
              <p class="category-description">Trendy clothing & accessories</p>
            </div>
          </div>
          <div class="category-card">
            <img src="https://picsum.photos/400/300?random=3" alt="Home & Living" class="category-image" />
            <div class="category-content">
              <h3 class="category-title">Home & Living</h3>
              <p class="category-description">Furniture & decor items</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="newsletter-section">
      <div class="container">
        <div class="newsletter-content">
          <h2 class="newsletter-title">Stay Updated</h2>
          <p class="newsletter-description">Subscribe to our newsletter for exclusive deals and new arrivals.</p>
          <form @submit.prevent="subscribeNewsletter" class="newsletter-form">
            <input
              v-model="email"
              type="email"
              placeholder="Enter your email"
              required
              class="newsletter-input"
            />
            <button type="submit" class="newsletter-button">Subscribe</button>
          </form>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ProductCard from '../components/ProductCard.vue'

const email = ref('')

const featuredProducts = [
  {
    id: 1,
    name: 'Wireless Bluetooth Headphones',
    category: 'Electronics',
    price: 89.99,
    rating: 4.5,
    image: 'https://picsum.photos/400/300?random=4',
    features: ['Noise Cancelling', '30hr Battery', 'Wireless'],
    inStock: true
  },
  {
    id: 2,
    name: 'Premium Leather Backpack',
    category: 'Fashion',
    price: 129.99,
    rating: 4.8,
    image: 'https://picsum.photos/400/300?random=5',
    features: ['Waterproof', 'Laptop Sleeve', 'Multiple Pockets'],
    inStock: true
  },
  {
    id: 3,
    name: 'Smart Fitness Watch',
    category: 'Electronics',
    price: 199.99,
    rating: 4.6,
    image: 'https://picsum.photos/400/300?random=6',
    features: ['Heart Rate Monitor', 'GPS', 'Water Resistant'],
    inStock: true
  },
  {
    id: 4,
    name: 'Ceramic Coffee Mug Set',
    category: 'Home',
    price: 34.99,
    rating: 4.7,
    image: 'https://picsum.photos/400/300?random=7',
    features: ['Microwave Safe', 'Dishwasher Safe', '4 Pieces'],
    inStock: true
  }
]

const subscribeNewsletter = () => {
  alert(`Thank you for subscribing with: ${email.value}`)
  email.value = ''
}
</script>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 4rem;
}

.hero-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  padding: 6rem 0;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 1.5rem;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 2.5rem;
  line-height: 1.6;
}

.hero-button {
  display: inline-block;
  background-color: white;
  color: var(--primary-color);
  padding: 1rem 2.5rem;
  border-radius: var(--radius-full);
  text-decoration: none;
  font-weight: 600;
  font-size: 1.125rem;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.hero-button:hover {
  background-color: transparent;
  color: white;
  border-color: white;
  transform: translateY(-2px);
}

.features-section {
  padding: 4rem 0;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.feature-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.featured-products {
  padding: 4rem 0;
  background-color: var(--bg-secondary);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
}

.view-all-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.view-all-link:hover {
  color: var(--primary-dark);
  transform: translateX(4px);
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.categories-section {
  padding: 4rem 0;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
  margin-top: 2rem;
}

.category-card {
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  aspect-ratio: 16/9;
}

.category-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.category-card:hover .category-image {
  transform: scale(1.05);
}

.category-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.8));
  color: white;
  padding: 2rem;
}

.category-title {
  font-size: 1.75rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.category-description {
  opacity: 0.9;
  font-size: 1rem;
}

.newsletter-section {
  background-color: var(--bg-primary);
  padding: 4rem 0;
  text-align: center;
}

.newsletter-content {
  max-width: 600px;
  margin: 0 auto;
}

.newsletter-title {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.newsletter-description {
  color: var(--text-secondary);
  font-size: 1.125rem;
  margin-bottom: 2rem;
  line-height: 1.6;
}

.newsletter-form {
  display: flex;
  gap: 1rem;
  max-width: 500px;
  margin: 0 auto;
}

.newsletter-input {
  flex: 1;
  padding: 1rem 1.5rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-family: var(--font-sans);
  font-size: 1rem;
  background-color: white;
}

.newsletter-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.newsletter-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: var(--radius-full);
  font-family: var(--font-sans);
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.newsletter-button:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

@media (max-width: 1024px) {
  .features-grid,
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
}

@media (max-width: 768px) {
  .home-page {
    gap: 3rem;
  }
  
  .hero-section {
    padding: 4rem 0;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .features-grid,
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .newsletter-form {
    flex-direction: column;
  }
  
  .newsletter-input,
  .newsletter-button {
    width: 100%;
  }
}
</style>