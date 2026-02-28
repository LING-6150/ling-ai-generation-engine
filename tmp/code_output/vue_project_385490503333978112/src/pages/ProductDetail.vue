<template>
  <div class="product-detail">
    <div class="breadcrumb">
      <router-link to="/" class="breadcrumb-link">Home</router-link>
      <span class="breadcrumb-separator">/</span>
      <router-link to="/" class="breadcrumb-link">{{ product.category }}</router-link>
      <span class="breadcrumb-separator">/</span>
      <span class="breadcrumb-current">{{ product.name }}</span>
    </div>
    
    <div class="product-container">
      <div class="product-gallery">
        <div class="main-image">
          <img :src="product.image" :alt="product.name" class="product-main-image">
        </div>
        <div class="thumbnail-images">
          <div 
            v-for="n in 4" 
            :key="n"
            class="thumbnail"
            :class="{ active: activeImage === n }"
            @click="activeImage = n"
          >
            <img :src="`https://picsum.photos/100/100?random=${product.id}${n}`" :alt="`Product view ${n}`">
          </div>
        </div>
      </div>
      
      <div class="product-info">
        <h1 class="product-title">{{ product.name }}</h1>
        
        <div class="product-meta">
          <div class="product-rating">
            <div class="stars">
              <i v-for="n in 5" :key="n" 
                 :class="['fas fa-star', n <= Math.floor(product.rating) ? 'filled' : 'empty']">
              </i>
            </div>
            <span class="rating-value">{{ product.rating.toFixed(1) }}</span>
            <span class="reviews">({{ Math.floor(Math.random() * 100) + 20 }} reviews)</span>
          </div>
          
          <div class="product-sku">
            SKU: {{ product.id.toString().padStart(6, '0') }}
          </div>
        </div>
        
        <div class="product-price-section">
          <div class="price-container">
            <span class="current-price">${{ product.price.toFixed(2) }}</span>
            <span v-if="product.price > 50" class="original-price">
              ${{ (product.price * 1.2).toFixed(2) }}
            </span>
            <span v-if="product.price > 50" class="discount-badge">
              Save {{ ((product.price * 1.2 - product.price) / (product.price * 1.2) * 100).toFixed(0) }}%
            </span>
          </div>
          
          <div class="stock-status" :class="{ 'low-stock': product.stock < 10, 'out-of-stock': product.stock === 0 }">
            <i class="fas fa-box"></i>
            <span v-if="product.stock > 0">
              {{ product.stock }} in stock
            </span>
            <span v-else>
              Out of stock
            </span>
          </div>
        </div>
        
        <div class="product-description">
          <h3>Description</h3>
          <p>{{ product.description }}</p>
          <p>This premium product features advanced technology and superior craftsmanship. Designed for durability and performance, it offers exceptional value for money. Perfect for both personal and professional use.</p>
        </div>
        
        <div class="product-features">
          <h3>Key Features</h3>
          <ul class="features-list">
            <li><i class="fas fa-check"></i> High-quality materials and construction</li>
            <li><i class="fas fa-check"></i> Advanced technology integration</li>
            <li><i class="fas fa-check"></i> Energy efficient design</li>
            <li><i class="fas fa-check"></i> Easy to use and maintain</li>
            <li><i class="fas fa-check"></i> 2-year manufacturer warranty</li>
          </ul>
        </div>
        
        <div class="product-actions">
          <div class="quantity-selector">
            <label for="quantity">Quantity:</label>
            <div class="quantity-controls">
              <button @click="decreaseQuantity" :disabled="quantity <= 1">
                <i class="fas fa-minus"></i>
              </button>
              <input 
                type="number" 
                id="quantity" 
                v-model.number="quantity" 
                min="1" 
                :max="product.stock"
                @change="validateQuantity"
              >
              <button @click="increaseQuantity" :disabled="quantity >= product.stock">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>
          
          <div class="action-buttons">
            <button 
              class="add-to-cart-btn" 
              @click="addToCart"
              :disabled="product.stock === 0"
            >
              <i class="fas fa-cart-plus"></i>
              {{ product.stock > 0 ? 'Add to Cart' : 'Out of Stock' }}
            </button>
            
            <button class="buy-now-btn" @click="buyNow" :disabled="product.stock === 0">
              <i class="fas fa-bolt"></i>
              Buy Now
            </button>
            
            <button class="wishlist-btn" @click="toggleWishlist">
              <i :class="['far', isInWishlist ? 'fas fa-heart' : 'fa-heart']"></i>
            </button>
          </div>
        </div>
        
        <div class="product-shipping">
          <div class="shipping-info">
            <i class="fas fa-shipping-fast"></i>
            <div class="shipping-details">
              <strong>Free Shipping</strong>
              <span>on orders over $100</span>
            </div>
          </div>
          <div class="shipping-info">
            <i class="fas fa-undo-alt"></i>
            <div class="shipping-details">
              <strong>30-Day Returns</strong>
              <span>Easy returns policy</span>
            </div>
          </div>
          <div class="shipping-info">
            <i class="fas fa-shield-alt"></i>
            <div class="shipping-details">
              <strong>Secure Payment</strong>
              <span>SSL encrypted checkout</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="related-products">
      <h2>You May Also Like</h2>
      <div class="related-products-grid">
        <ProductCard 
          v-for="relatedProduct in relatedProducts" 
          :key="relatedProduct.id"
          :product="relatedProduct"
          @add-to-cart="addToCart"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import { useCartStore } from '../stores/cart'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const productId = parseInt(route.params.id)
const quantity = ref(1)
const activeImage = ref(1)
const isInWishlist = ref(false)

const product = computed(() => {
  return cartStore.getProductById(productId) || {
    id: productId,
    name: 'Product Not Found',
    description: 'This product could not be found.',
    price: 0,
    category: 'Unknown',
    rating: 0,
    stock: 0,
    image: 'https://picsum.photos/400/300?random=0'
  }
})

const relatedProducts = computed(() => {
  const allProducts = cartStore.getAllProducts()
  return allProducts
    .filter(p => p.id !== productId && p.category === product.value.category)
    .slice(0, 4)
})

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const increaseQuantity = () => {
  if (quantity.value < product.value.stock) {
    quantity.value++
  }
}

const validateQuantity = () => {
  if (quantity.value < 1) {
    quantity.value = 1
  } else if (quantity.value > product.value.stock) {
    quantity.value = product.value.stock
  }
}

const addToCart = () => {
  if (product.value.stock > 0) {
    cartStore.addToCart(product.value, quantity.value)
    alert(`Added ${quantity.value} ${product.value.name}(s) to cart!`)
  }
}

const buyNow = () => {
  if (product.value.stock > 0) {
    cartStore.addToCart(product.value, quantity.value)
    router.push('/checkout')
  }
}

const toggleWishlist = () => {
  isInWishlist.value = !isInWishlist.value
  const message = isInWishlist.value 
    ? 'Added to wishlist!' 
    : 'Removed from wishlist!'
  alert(message)
}

onMounted(() => {
  // Scroll to top when component mounts
  window.scrollTo(0, 0)
})
</script>

<style scoped>
.product-detail {
  max-width: 1200px;
  margin: 0 auto;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  color: #6b7280;
  font-size: 0.9rem;
}

.breadcrumb-link {
  color: #4f46e5;
  text-decoration: none;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #4338ca;
  text-decoration: underline;
}

.breadcrumb-separator {
  color: #d1d5db;
}

.breadcrumb-current {
  color: #1f2937;
  font-weight: 500;
}

.product-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  margin-bottom: 4rem;
}

.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.main-image {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.product-main-image {
  width: 100%;
  height: 400px;
  object-fit: cover;
}

.thumbnail-images {
  display: flex;
  gap: 1rem;
}

.thumbnail {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.thumbnail:hover {
  border-color: #4f46e5;
}

.thumbnail.active {
  border-color: #4f46e5;
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

.product-title {
  font-size: 2rem;
  color: #1f2937;
  margin: 0;
  line-height: 1.2;
}

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.stars {
  display: flex;
  gap: 2px;
}

.stars i {
  font-size: 1rem;
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
}

.reviews {
  color: #6b7280;
  font-size: 0.9rem;
}

.product-sku {
  color: #6b7280;
  font-size: 0.9rem;
}

.product-price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e5e7eb;
}

.price-container {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.current-price {
  font-size: 2rem;
  font-weight: bold;
  color: #1f2937;
}

.original-price {
  font-size: 1.25rem;
  color: #9ca3af;
  text-decoration: line-through;
}

.discount-badge {
  background: #fef3c7;
  color: #92400e;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 600;
}

.stock-status {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: 500;
}

.stock-status i {
  color: #10b981;
}

.stock-status.low-stock i {
  color: #f59e0b;
}

.stock-status.low-stock {
  color: #92400e;
}

.stock-status.out-of-stock i {
  color: #ef4444;
}

.stock-status.out-of-stock {
  color: #ef4444;
}

.product-description h3,
.product-features h3 {
  font-size: 1.25rem;
  color: #1f2937;
  margin-bottom: 1rem;
}

.product-description p {
  color: #4b5563;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.features-list li {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.75rem;
  color: #4b5563;
}

.features-list i {
  color: #10b981;
}

.product-actions {
  background: #f9fafb;
  padding: 1.5rem;
  border-radius: 12px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 1.5rem;
}

.quantity-selector label {
  font-weight: 500;
  color: #1f2937;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-controls button {
  width: 40px;
  height: 40px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.quantity-controls button:hover:not(:disabled) {
  background: #f3f4f6;
  border-color: #9ca3af;
}

.quantity-controls button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-controls input {
  width: 60px;
  height: 40px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  text-align: center;
  font-size: 1rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
}

.add-to-cart-btn,
.buy-now-btn,
.wishlist-btn {
  flex: 1;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
  border: none;
}

.add-to-cart-btn {
  background: #4f46e5;
  color: white;
}

.add-to-cart-btn:hover:not(:disabled) {
  background: #4338ca;
  transform: translateY(-2px);
}

.buy-now-btn {
  background: #10b981;
  color: white;
}

.buy-now-btn:hover:not(:disabled) {
  background: #059669;
  transform: translateY(-2px);
}

.wishlist-btn {
  background: white;
  color: #4b5563;
  border: 1px solid #d1d5db;
  flex: 0 0 60px;
}

.wishlist-btn:hover {
  background: #f3f4f6;
  color: #ef4444;
  border-color: #9ca3af;
}

.add-to-cart-btn:disabled,
.buy-now-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.product-shipping {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-top: 1.5rem;
}

.shipping-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.shipping-info i {
  font-size: 1.5rem;
  color: #4f46e5;
}

.shipping-details {
  display: flex;
  flex-direction: column;
}

.shipping-details strong {
  font-size: 0.9rem;
  color: #1f2937;
}

.shipping-details span {
  font-size: 0.8rem;
  color: #6b7280;
}

.related-products {
  margin-top: 4rem;
}

.related-products h2 {
  font-size: 1.5rem;
  color: #1f2937;
  margin-bottom: 1.5rem;
}

.related-products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 1024px) {
  .product-container {
    gap: 2rem;
  }
  
  .product-main-image {
    height: 350px;
  }
}

@media (max-width: 768px) {
  .product-container {
    grid-template-columns: 1fr;
  }
  
  .product-main-image {
    height: 300px;
  }
  
  .thumbnail-images {
    justify-content: center;
  }
  
  .product-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
  
  .product-price-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .product-shipping {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .product-title {
    font-size: 1.5rem;
  }
  
  .current-price {
    font-size: 1.5rem;
  }
  
  .original-price {
    font-size: 1rem;
  }
  
  .thumbnail {
    width: 80px;
    height: 80px;
  }
  
  .quantity-selector {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>