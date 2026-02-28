<template>
  <div class="product-card">
    <div class="product-image">
      <img :src="product.image" :alt="product.name" />
      <div v-if="!product.inStock" class="out-of-stock">Out of Stock</div>
    </div>
    
    <div class="product-content">
      <div class="product-category">{{ product.category }}</div>
      <h3 class="product-title">{{ product.name }}</h3>
      <p class="product-description">{{ product.description }}</p>
      
      <div class="product-rating">
        <div class="stars">
          <span v-for="n in 5" :key="n" class="star" :class="{ filled: n <= Math.floor(product.rating) }">★</span>
        </div>
        <span class="rating-text">{{ product.rating }} ({{ product.reviewCount }} reviews)</span>
      </div>
      
      <div class="product-footer">
        <div class="product-price">${{ product.price.toFixed(2) }}</div>
        <button 
          class="btn btn-primary btn-sm" 
          :disabled="!product.inStock"
          @click="addToCart"
        >
          {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const router = useRouter()
const cartStore = useCartStore()

const addToCart = () => {
  cartStore.addToCart(props.product)
}

const viewDetails = () => {
  router.push(`/product/${props.product.id}`)
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

.product-image {
  position: relative;
  height: 200px;
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

.out-of-stock {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  background-color: rgba(239, 68, 68, 0.9);
  color: white;
  padding: 0.25rem 0.5rem;
  border-radius: var(--radius-sm);
  font-size: 0.75rem;
  font-weight: 500;
}

.product-content {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-category {
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.product-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
  color: var(--text-primary);
  line-height: 1.4;
}

.product-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
  margin-bottom: 1rem;
  flex: 1;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.stars {
  display: flex;
  gap: 0.125rem;
}

.star {
  color: var(--border-color);
  font-size: 1rem;
}

.star.filled {
  color: #fbbf24;
}

.rating-text {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.product-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

@media (max-width: 768px) {
  .product-image {
    height: 180px;
  }
  
  .product-content {
    padding: 1rem;
  }
  
  .product-title {
    font-size: 1.125rem;
  }
  
  .product-price {
    font-size: 1.25rem;
  }
}
</style>