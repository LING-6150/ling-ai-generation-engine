<template>
  <div class="product-detail-page">
    <div class="container">
      <div class="breadcrumb">
        <router-link to="/" class="breadcrumb-link">Home</router-link>
        <span class="breadcrumb-separator">/</span>
        <router-link to="/" class="breadcrumb-link">{{ product.category }}</router-link>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current">{{ product.name }}</span>
      </div>

      <div class="product-detail">
        <div class="product-gallery">
          <div class="main-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="thumbnail-images">
            <div 
              v-for="n in 4" 
              :key="n" 
              class="thumbnail"
              :class="{ active: selectedImage === n }"
              @click="selectedImage = n"
            >
              <img :src="`https://picsum.photos/100/100?random=${product.id}-${n}`" :alt="`Product view ${n}`" />
            </div>
          </div>
        </div>

        <div class="product-info">
          <div class="product-header">
            <h1 class="product-title">{{ product.name }}</h1>
            <div class="product-meta">
              <div class="product-rating">
                <div class="stars">
                  <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.floor(product.rating) }">★</span>
                </div>
                <span class="rating-text">{{ product.rating }} ({{ product.reviewCount }} reviews)</span>
              </div>
              <div class="product-stock" :class="{ 'in-stock': product.inStock, 'out-of-stock': !product.inStock }">
                {{ product.inStock ? 'In Stock' : 'Out of Stock' }}
              </div>
            </div>
          </div>

          <div class="product-price-section">
            <div class="price">${{ product.price.toFixed(2) }}</div>
            <div class="shipping-info">Free shipping on orders over $50</div>
          </div>

          <div class="product-description">
            <h3>Description</h3>
            <p>{{ product.description }}</p>
          </div>

          <div class="product-features">
            <h3>Features</h3>
            <ul class="features-list">
              <li v-for="(feature, index) in product.features" :key="index">{{ feature }}</li>
            </ul>
          </div>

          <div class="product-actions">
            <div class="quantity-selector">
              <label for="quantity" class="quantity-label">Quantity:</label>
              <div class="quantity-controls">
                <button class="quantity-btn" @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
                <input 
                  type="number" 
                  id="quantity" 
                  v-model.number="quantity" 
                  min="1" 
                  max="10"
                  class="quantity-input"
                />
                <button class="quantity-btn" @click="increaseQuantity" :disabled="quantity >= 10">+</button>
              </div>
            </div>

            <div class="action-buttons">
              <button 
                class="btn btn-primary btn-lg btn-full" 
                :disabled="!product.inStock"
                @click="addToCart"
              >
                {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
              </button>
              <button 
                class="btn btn-secondary btn-lg btn-full"
                :disabled="!product.inStock"
                @click="buyNow"
              >
                Buy Now
              </button>
            </div>
          </div>

          <div class="product-specs">
            <h3>Specifications</h3>
            <div class="specs-grid">
              <div class="spec-item">
                <span class="spec-label">Category:</span>
                <span class="spec-value">{{ product.category }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">SKU:</span>
                <span class="spec-value">SPH-{{ product.id.toString().padStart(4, '0') }}</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">Weight:</span>
                <span class="spec-value">1.2 lbs</span>
              </div>
              <div class="spec-item">
                <span class="spec-label">Dimensions:</span>
                <span class="spec-value">8 x 6 x 4 in</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Related Products -->
      <div class="related-products">
        <h2>Related Products</h2>
        <div class="related-grid">
          <div 
            v-for="relatedProduct in relatedProducts" 
            :key="relatedProduct.id" 
            class="related-card"
            @click="goToProduct(relatedProduct.id)"
          >
            <img :src="relatedProduct.image" :alt="relatedProduct.name" />
            <div class="related-info">
              <h4>{{ relatedProduct.name }}</h4>
              <div class="related-price">${{ relatedProduct.price.toFixed(2) }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getProductById, products } from '../utils/products'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref({})
const quantity = ref(1)
const selectedImage = ref(1)

onMounted(() => {
  const productId = parseInt(route.params.id)
  const foundProduct = getProductById(productId)
  if (foundProduct) {
    product.value = foundProduct
  } else {
    router.push('/')
  }
})

const relatedProducts = computed(() => {
  return products
    .filter(p => p.category === product.value.category && p.id !== product.value.id)
    .slice(0, 4)
})

const increaseQuantity = () => {
  if (quantity.value < 10) {
    quantity.value++
  }
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const addToCart = () => {
  if (product.value.inStock) {
    cartStore.addToCart(product.value, quantity.value)
    quantity.value = 1
  }
}

const buyNow = () => {
  if (product.value.inStock) {
    cartStore.addToCart(product.value, quantity.value)
    router.push('/checkout')
  }
}

const goToProduct = (productId) => {
  router.push(`/product/${productId}`)
}
</script>

<style scoped>
.product-detail-page {
  padding: 2rem 0 4rem;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.breadcrumb-link {
  color: var(--text-secondary);
  text-decoration: none;
}

.breadcrumb-link:hover {
  color: var(--primary-color);
}

.breadcrumb-current {
  color: var(--text-primary);
  font-weight: 500;
}

.breadcrumb-separator {
  color: var(--text-light);
}

.product-detail {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  margin-bottom: 4rem;
}

.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.main-image {
  border-radius: var(--radius-lg);
  overflow: hidden;
  background-color: var(--bg-tertiary);
}

.main-image img {
  width: 100%;
  height: auto;
  display: block;
}

.thumbnail-images {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 0.75rem;
}

.thumbnail {
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: border-color 0.2s ease;
}

.thumbnail:hover {
  border-color: var(--border-color);
}

.thumbnail.active {
  border-color: var(--primary-color);
}

.thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.product-header {
  margin-bottom: 1rem;
}

.product-title {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.product-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1rem;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  display: flex;
  gap: 0.125rem;
}

.star {
  color: var(--border-color);
  font-size: 1.25rem;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.product-stock {
  font-weight: 500;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: 0.875rem;
}

.product-stock.in-stock {
  background-color: rgba(16, 185, 129, 0.1);
  color: var(--secondary-color);
}

.product-stock.out-of-stock {
  background-color: rgba(239, 68, 68, 0.1);
  color: #ef4444;
}

.product-price-section {
  padding: 1.5rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-lg);
}

.price {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.shipping-info {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.product-description h3,
.product-features h3,
.product-specs h3 {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.product-description p {
  color: var(--text-secondary);
  line-height: 1.6;
}

.features-list {
  list-style: none;
  padding-left: 0;
}

.features-list li {
  padding: 0.5rem 0;
  color: var(--text-secondary);
  position: relative;
  padding-left: 1.5rem;
}

.features-list li:before {
  content: "✓";
  position: absolute;
  left: 0;
  color: var(--secondary-color);
  font-weight: bold;
}

.product-actions {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  padding: 1.5rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-lg);
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.quantity-label {
  font-weight: 500;
  color: var(--text-primary);
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  width: 2.5rem;
  height: 2.5rem;
  border: 1px solid var(--border-color);
  background-color: white;
  border-radius: var(--radius-md);
  font-size: 1.25rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quantity-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 4rem;
  height: 2.5rem;
  text-align: center;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-size: 1rem;
  font-family: var(--font-sans);
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.product-specs {
  padding: 1.5rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-lg);
}

.specs-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid var(--border-color);
}

.spec-label {
  color: var(--text-secondary);
  font-weight: 500;
}

.spec-value {
  color: var(--text-primary);
  font-weight: 500;
}

.related-products {
  margin-top: 4rem;
}

.related-products h2 {
  font-size: 1.75rem;
  margin-bottom: 1.5rem;
  color: var(--text-primary);
}

.related-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
}

.related-card {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.related-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.related-card img {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.related-info {
  padding: 1rem;
}

.related-info h4 {
  font-size: 1rem;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
  line-height: 1.4;
}

.related-price {
  font-weight: 600;
  color: var(--primary-color);
}

@media (max-width: 1024px) {
  .product-detail {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .related-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .product-title {
    font-size: 2rem;
  }
  
  .price {
    font-size: 2rem;
  }
  
  .product-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .specs-grid {
    grid-template-columns: 1fr;
  }
  
  .thumbnail-images {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .related-grid {
    grid-template-columns: 1fr;
  }
  
  .quantity-selector {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.75rem;
  }
}
</style>