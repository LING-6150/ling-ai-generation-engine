<template>
  <div class="product-card">
    <div class="product-image">
      <img :src="product.image" :alt="product.name" />
      <div v-if="product.stock < 10" class="stock-badge">Low Stock</div>
    </div>
    <div class="product-info">
      <div class="product-header">
        <h3 class="product-name">{{ product.name }}</h3>
        <span class="product-category">{{ product.category }}</span>
      </div>
      <p class="product-description">{{ product.description }}</p>
      <div class="product-footer">
        <div class="product-price">${{ product.price.toFixed(2) }}</div>
        <div class="product-rating">
          <span class="stars">⭐ {{ product.rating }}</span>
          <span class="stock">Stock: {{ product.stock }}</span>
        </div>
        <button 
          class="btn btn-add-to-cart" 
          @click="$emit('add-to-cart', product)"
          :disabled="product.stock === 0"
        >
          {{ product.stock === 0 ? 'Out of Stock' : 'Add to Cart' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  product: {
    type: Object,
    required: true
  }
})

defineEmits(['add-to-cart'])
</script>

<style scoped>
.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.15);
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
  transition: transform 0.3s;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.stock-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background: #ff4757;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
}

.product-info {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-header {
  margin-bottom: 1rem;
}

.product-name {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  color: #333;
}

.product-category {
  display: inline-block;
  background: #f0f2f5;
  color: #667eea;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.product-description {
  color: #666;
  margin-bottom: 1.5rem;
  flex: 1;
  font-size: 0.95rem;
  line-height: 1.5;
}

.product-footer {
  margin-top: auto;
}

.product-price {
  font-size: 1.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.product-rating {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  color: #666;
}

.stars {
  color: #ffc107;
}

.btn-add-to-cart {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s;
}

.btn-add-to-cart:hover:not(:disabled) {
  opacity: 0.9;
}

.btn-add-to-cart:disabled {
  background: #ccc;
  cursor: not-allowed;
}

@media (max-width: 480px) {
  .product-image {
    height: 180px;
  }
  
  .product-info {
    padding: 1rem;
  }
}
</style>