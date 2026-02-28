<template>
  <div class="product-card">
    <div class="product-image-container">
      <img :src="product.image" :alt="product.name" class="product-image" />
      <div v-if="product.inStock" class="stock-badge">In Stock</div>
      <div v-else class="stock-badge out-of-stock">Out of Stock</div>
    </div>
    
    <div class="product-info">
      <div class="product-category">{{ product.category }}</div>
      <h3 class="product-name">{{ product.name }}</h3>
      
      <div class="product-rating">
        <span class="rating-stars">★★★★★</span>
        <span class="rating-value">{{ product.rating }}</span>
      </div>
      
      <div class="product-features">
        <div v-for="feature in product.features.slice(0, 2)" :key="feature" class="feature-tag">
          {{ feature }}
        </div>
      </div>
      
      <div class="product-footer">
        <div class="product-price">${{ product.price.toFixed(2) }}</div>
        <button 
          class="add-to-cart-btn" 
          :disabled="!product.inStock"
          @click="handleAddToCart"
        >
          {{ product.inStock ? 'Add to Cart' : 'Out of Stock' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const cartStore = useCartStore()

const handleAddToCart = () => {
  if (props.product.inStock) {
    cartStore.addToCart(props.product)
  }
}
</script>

<style scoped>
.product-card {
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

.product-image-container {
  position: relative;
  aspect-ratio: 4/3;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.stock-badge {
  position: absolute;
  top: 0.75rem;
  left: 0.75rem;
  background-color: #10b981;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 600;
}

.stock-badge.out-of-stock {
  background-color: #ef4444;
}

.product-info {
  padding: 1.25rem;
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

.product-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
  line-height: 1.4;
  flex: 1;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.rating-stars {
  color: #fbbf24;
  font-size: 1rem;
  letter-spacing: 0.1em;
}

.rating-value {
  color: var(--text-secondary);
  font-size: 0.875rem;
  font-weight: 500;
}

.product-features {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1.25rem;
}

.feature-tag {
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 500;
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

.add-to-cart-btn {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.75rem 1.25rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 600;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.add-to-cart-btn:hover:not(:disabled) {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.add-to-cart-btn:disabled {
  background-color: var(--text-light);
  cursor: not-allowed;
  opacity: 0.7;
}

@media (max-width: 768px) {
  .product-info {
    padding: 1rem;
  }
  
  .product-name {
    font-size: 1rem;
  }
  
  .product-price {
    font-size: 1.25rem;
  }
  
  .add-to-cart-btn {
    padding: 0.5rem 1rem;
    font-size: 0.75rem;
  }
}
</style>