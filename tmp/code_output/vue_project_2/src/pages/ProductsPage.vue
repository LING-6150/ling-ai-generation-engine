<template>
  <div class="products-page">
    <div class="container">
      <!-- Page Header -->
      <div class="page-header">
        <h1 class="page-title">Our Products</h1>
        <p class="page-subtitle">Browse our collection of premium electronics and accessories</p>
      </div>

      <!-- Products Grid -->
      <div class="products-grid">
        <div v-for="product in cartStore.products" :key="product.id" class="product-card">
          <div class="product-image">
            <img :src="product.image" :alt="product.name">
            <div v-if="product.stock < 5" class="stock-badge">Low Stock</div>
            <div v-if="product.stock === 0" class="out-of-stock">Out of Stock</div>
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
              
              <button @click="viewProductDetails(product)" class="btn btn-outline">
                <i class="fas fa-eye"></i>
                View Details
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()

const addToCart = (product) => {
  if (product.stock > 0) {
    cartStore.addToCart(product)
  }
}

const viewProductDetails = (product) => {
  alert(`Product Details:\n\nName: ${product.name}\nPrice: $${product.price.toFixed(2)}\nCategory: ${product.category}\nDescription: ${product.description}\nStock: ${product.stock} available`)
}
</script>

<style scoped>
.products-page {
  padding: 2rem 0 4rem;
}

/* Page Header */
.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a237e;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  color: #666;
  font-size: 1.1rem;
}

/* Products Grid */
.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
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

.stock-badge {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: #ff9800;
  color: white;
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 600;
  text-transform: uppercase;
}

.out-of-stock {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  text-transform: uppercase;
  font-size: 1.25rem;
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

.product-actions {
  display: flex;
  gap: 0.5rem;
}

.product-actions .btn {
  flex: 1;
  padding: 0.75rem;
  font-size: 0.9rem;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Responsive Design */
@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .page-title {
    font-size: 2rem;
  }
}

@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .product-actions {
    flex-direction: column;
  }
  
  .product-image {
    height: 180px;
  }
}
</style>