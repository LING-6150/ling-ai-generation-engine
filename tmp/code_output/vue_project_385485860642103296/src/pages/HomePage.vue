<template>
  <div class="home-page">
    <section class="hero">
      <div class="container">
        <div class="hero-content">
          <h1>Welcome to ShopVue</h1>
          <p class="subtitle">Discover premium products with exceptional quality and service</p>
          <router-link to="/products" class="btn btn-primary">Shop Now</router-link>
        </div>
      </div>
    </section>
    
    <section class="features">
      <div class="container">
        <h2>Why Choose ShopVue?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🚚</div>
            <h3>Free Shipping</h3>
            <p>Free delivery on orders over $100</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔄</div>
            <h3>30-Day Returns</h3>
            <p>Easy returns within 30 days of purchase</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔒</div>
            <h3>Secure Payment</h3>
            <p>100% secure payment processing</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⭐</div>
            <h3>Quality Guarantee</h3>
            <p>Premium quality products guaranteed</p>
          </div>
        </div>
      </div>
    </section>
    
    <section class="featured-products">
      <div class="container">
        <div class="section-header">
          <h2>Featured Products</h2>
          <router-link to="/products" class="view-all">View All Products →</router-link>
        </div>
        <div class="products-grid">
          <ProductCard 
            v-for="product in featuredProducts" 
            :key="product.id" 
            :product="product"
            @add-to-cart="addToCart"
          />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ProductCard from '../components/ProductCard.vue'
import { products } from '../data/products'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const featuredProducts = ref(products.slice(0, 4))

const addToCart = (product) => {
  cartStore.addToCart(product)
}
</script>

<style scoped>
.home-page {
  padding-bottom: 3rem;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 5rem 0;
  text-align: center;
  margin-bottom: 3rem;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.subtitle {
  font-size: 1.25rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.btn {
  display: inline-block;
  padding: 0.75rem 2rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
  border: none;
  cursor: pointer;
  font-size: 1rem;
}

.btn-primary {
  background: white;
  color: #667eea;
}

.btn-primary:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.features {
  padding: 4rem 0;
  background: #f8f9fa;
}

.features h2 {
  text-align: center;
  margin-bottom: 3rem;
  font-size: 2rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  margin-bottom: 0.5rem;
  color: #333;
}

.feature-card p {
  color: #666;
}

.featured-products {
  padding: 4rem 0;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.section-header h2 {
  font-size: 2rem;
}

.view-all {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
}

.view-all:hover {
  text-decoration: underline;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 2rem;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 2.5rem;
  }
  
  .hero {
    padding: 3rem 0;
  }
  
  .section-header {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .hero h1 {
    font-size: 2rem;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .products-grid {
    grid-template-columns: 1fr;
  }
}
</style>