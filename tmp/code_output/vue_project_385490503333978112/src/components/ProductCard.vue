<template>
  <div class="product-card">
    <div class="product-image-container">
      <router-link :to="`/product/${product.id}`" class="product-image-link">
        <img :src="product.image" :alt="product.name" class="product-image">
      </router-link>
      <div class="product-badges">
        <span v-if="product.stock < 10" class="badge low-stock">Low Stock</span>
        <span v-if="product.price > 100" class="badge premium">Premium</span>
      </div>
      <button class="quick-view-btn" @click="quickView">
        <i class="fas fa-eye"></i>
      </button>
    </div>
    
    <div class="product-info">
      <div class="product-category">{{ product.category }}</div>
      <h3 class="product-name">
        <router-link :to="`/product/${product.id}`" class="product-name-link">
          {{ product.name }}
        </router-link>
      </h3>
      
      <div class="product-rating">
        <div class="stars">
          <i v-for="n in 5" :key="n" 
             :class="['fas fa-star', n <= Math.floor(product.rating) ? 'filled' : 'empty']">
          </i>
        </div>
        <span class="rating-value">{{ product.rating.toFixed(1) }}</span>
        <span class="reviews">({{ Math.floor(Math.random() * 100) + 20 }})</span>
      </div>
      
      <p class="product-description">{{ product.description }}</p>
      
      <div class="product-footer">
        <div class="product-price">
          <span class="current-price">${{ product.price.toFixed(2) }}</span>
          <span v-if="product.price > 50" class="original-price">
            ${{ (product.price * 1.2).toFixed(2) }}
          </span>
        </div>
        
        <div class="product-actions">
          <button 
            class="add-to-cart-btn" 
            @click="addToCart"
            :disabled="product.stock === 0"
          >
            <i class="fas fa-cart-plus"></i>
            <span>{{ product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['add-to-cart'])

const router = useRouter()

const addToCart = () => {
  if (props.product.stock > 0) {
    emit('add-to-cart', props.product)
  }
}

const quickView = () => {
  router.push(`/product/${props.product.id}`)
}
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
}

.product-image-container {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.product-image-link {
  display: block;
  width: 100%;
  height: 100%;
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

.product-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.badge.low-stock {
  background: #fef3c7;
  color: #92400e;
}

.badge.premium {
  background: #dbeafe;
  color: #1e40af;
}

.quick-view-btn {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 36px;
  height: 36px;
  background: white;
  border: none;
  border-radius: 50%;
  color: #4b5563;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.product-card:hover .quick-view-btn {
  opacity: 1;
  transform: translateY(0);
}

.quick-view-btn:hover {
  background: #4f46e5;
  color: white;
}

.product-info {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-category {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.product-name {
  margin: 0 0 0.75rem 0;
  font-size: 1.1rem;
  line-height: 1.4;
}

.product-name-link {
  color: #1f2937;
  text-decoration: none;
  transition: color 0.2s;
}

.product-name-link:hover {
  color: #4f46e5;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.stars {
  display: flex;
  gap: 2px;
}

.stars i {
  font-size: 0.9rem;
}

.stars i.filled {
  color: #fbbf24;
}

.stars i.empty {
  color: #d1d5db;
}

.rating-value {
  font-weight: 600;
  color: #1f2937;
  font-size: 0.9rem;
}

.reviews {
  font-size: 0.875rem;
  color: #6b7280;
}

.product-description {
  color: #4b5563;
  font-size: 0.9rem;
  line-height: 1.5;
  margin-bottom: 1.5rem;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  margin-top: auto;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.current-price {
  font-size: 1.25rem;
  font-weight: bold;
  color: #1f2937;
}

.original-price {
  font-size: 1rem;
  color: #9ca3af;
  text-decoration: line-through;
}

.product-actions {
  display: flex;
  gap: 0.5rem;
}

.add-to-cart-btn {
  flex: 1;
  background: #4f46e5;
  color: white;
  border: none;
  padding: 10px 16px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.add-to-cart-btn:hover:not(:disabled) {
  background: #4338ca;
  transform: translateY(-2px);
}

.add-to-cart-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.add-to-cart-btn i {
  font-size: 1rem;
}

@media (max-width: 768px) {
  .product-image-container {
    height: 180px;
  }
  
  .product-info {
    padding: 1.25rem;
  }
  
  .product-name {
    font-size: 1rem;
  }
  
  .product-description {
    font-size: 0.85rem;
  }
  
  .add-to-cart-btn {
    padding: 8px 12px;
    font-size: 0.85rem;
  }
}

@media (max-width: 480px) {
  .product-image-container {
    height: 160px;
  }
  
  .product-info {
    padding: 1rem;
  }
  
  .quick-view-btn {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>