<template>
  <div class="cart-page">
    <h1>Your Shopping Cart</h1>
    
    <div class="cart-container" v-if="cartItems.length > 0">
      <div class="cart-items">
        <div class="cart-header">
          <div class="header-product">Product</div>
          <div class="header-price">Price</div>
          <div class="header-quantity">Quantity</div>
          <div class="header-total">Total</div>
          <div class="header-actions">Actions</div>
        </div>
        
        <div class="cart-item" v-for="item in cartItems" :key="item.id">
          <div class="item-product">
            <img :src="item.image" :alt="item.name" class="item-image">
            <div class="item-details">
              <h3 class="item-name">{{ item.name }}</h3>
              <div class="item-category">{{ item.category }}</div>
              <div class="item-stock" :class="{ 'low-stock': item.stock < 10 }">
                {{ item.stock }} in stock
              </div>
            </div>
          </div>
          
          <div class="item-price">
            ${{ item.price.toFixed(2) }}
          </div>
          
          <div class="item-quantity">
            <div class="quantity-controls">
              <button @click="decreaseQuantity(item)" :disabled="item.quantity <= 1">
                <i class="fas fa-minus"></i>
              </button>
              <input 
                type="number" 
                v-model.number="item.quantity" 
                min="1" 
                :max="item.stock"
                @change="updateQuantity(item)"
              >
              <button @click="increaseQuantity(item)" :disabled="item.quantity >= item.stock">
                <i class="fas fa-plus"></i>
              </button>
            </div>
          </div>
          
          <div class="item-total">
            ${{ (item.price * item.quantity).toFixed(2) }}
          </div>
          
          <div class="item-actions">
            <button class="remove-btn" @click="removeItem(item.id)">
              <i class="fas fa-trash"></i>
            </button>
            <button class="wishlist-btn" @click="moveToWishlist(item)">
              <i class="far fa-heart"></i>
            </button>
          </div>
        </div>
      </div>
      
      <div class="cart-summary">
        <h2>Order Summary</h2>
        
        <div class="summary-details">
          <div class="summary-row">
            <span>Subtotal</span>
            <span>${{ subtotal.toFixed(2) }}</span>
          </div>
          
          <div class="summary-row">
            <span>Shipping</span>
            <span v-if="shippingCost > 0">${{ shippingCost.toFixed(2) }}</span>
            <span v-else class="free">FREE</span>
          </div>
          
          <div class="summary-row">
            <span>Tax (8%)</span>
            <span>${{ tax.toFixed(2) }}</span>
          </div>
          
          <div class="summary-divider"></div>
          
          <div class="summary-row total">
            <span>Total</span>
            <span>${{ total.toFixed(2) }}</span>
          </div>
          
          <div class="shipping-note" v-if="shippingCost > 0">
            <i class="fas fa-info-circle"></i>
            Add ${{ (100 - subtotal).toFixed(2) }} more to get free shipping!
          </div>
          <div class="shipping-note free" v-else>
            <i class="fas fa-check-circle"></i>
            You've unlocked free shipping!
          </div>
        </div>
        
        <div class="summary-actions">
          <button class="continue-shopping-btn" @click="continueShopping">
            <i class="fas fa-arrow-left"></i>
            Continue Shopping
          </button>
          <button class="checkout-btn" @click="proceedToCheckout">
            Proceed to Checkout
            <i class="fas fa-arrow-right"></i>
          </button>
        </div>
        
        <div class="payment-methods">
          <h3>Secure Payment</h3>
          <div class="payment-icons">
            <i class="fab fa-cc-visa"></i>
            <i class="fab fa-cc-mastercard"></i>
            <i class="fab fa-cc-amex"></i>
            <i class="fab fa-cc-paypal"></i>
            <i class="fab fa-cc-apple-pay"></i>
          </div>
          <p class="secure-note">
            <i class="fas fa-lock"></i>
            Your payment information is encrypted and secure
          </p>
        </div>
        
        <div class="cart-promo">
          <h3>Have a Promo Code?</h3>
          <div class="promo-input">
            <input type="text" placeholder="Enter promo code" v-model="promoCode">
            <button class="apply-btn" @click="applyPromo">
              Apply
            </button>
          </div>
        </div>
      </div>
    </div>
    
    <div class="empty-cart" v-else>
      <div class="empty-cart-icon">
        <i class="fas fa-shopping-cart"></i>
      </div>
      <h2>Your cart is empty</h2>
      <p>Looks like you haven't added any items to your cart yet.</p>
      <button class="start-shopping-btn" @click="continueShopping">
        Start Shopping
      </button>
    </div>
    
    <div class="recently-viewed" v-if="recentlyViewed.length > 0">
      <h2>Recently Viewed</h2>
      <div class="recently-viewed-grid">
        <ProductCard 
          v-for="product in recentlyViewed" 
          :key="product.id"
          :product="product"
          @add-to-cart="addToCart"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()
const promoCode = ref('')

const cartItems = computed(() => cartStore.cartItems)
const subtotal = computed(() => cartStore.subtotal)
const shippingCost = computed(() => cartStore.shippingCost)
const tax = computed(() => cartStore.tax)
const total = computed(() => cartStore.total)

// Mock recently viewed products
const recentlyViewed = computed(() => {
  const allProducts = cartStore.getAllProducts()
  return allProducts.slice(0, 3) // Show first 3 products as recently viewed
})

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    cartStore.updateQuantity(item.id, item.quantity - 1)
  }
}

const increaseQuantity = (item) => {
  if (item.quantity < item.stock) {
    cartStore.updateQuantity(item.id, item.quantity + 1)
  }
}

const updateQuantity = (item) => {
  if (item.quantity < 1) {
    item.quantity = 1
  } else if (item.quantity > item.stock) {
    item.quantity = item.stock
  }
  cartStore.updateQuantity(item.id, item.quantity)
}

const removeItem = (productId) => {
  cartStore.removeFromCart(productId)
}

const moveToWishlist = (item) => {
  alert(`Added ${item.name} to your wishlist!`)
  cartStore.removeFromCart(item.id)
}

const continueShopping = () => {
  router.push('/')
}

const proceedToCheckout = () => {
  router.push('/checkout')
}

const applyPromo = () => {
  if (promoCode.value.trim()) {
    alert(`Promo code "${promoCode.value}" applied! (This is a demo)`)
    promoCode.value = ''
  }
}

const addToCart = (product) => {
  cartStore.addToCart(product)
}

onMounted(() => {
  // Any initialization logic
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-page h1 {
  margin-bottom: 2rem;
  color: #1f2937;
}

.cart-container {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
}

.cart-items {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.cart-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
  gap: 1rem;
  padding: 1.5rem;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  font-weight: 600;
  color: #4b5563;
}

.cart-item {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 0.5fr;
  gap: 1rem;
  padding: 1.5rem;
  border-bottom: 1px solid #e5e7eb;
  align-items: center;
}

.cart-item:last-child {
  border-bottom: none;
}

.item-product {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.item-details {
  flex: 1;
}

.item-name {
  font-weight: 500;
  color: #1f2937;
  margin-bottom: 4px;
  font-size: 1rem;
}

.item-category {
  font-size: 0.875rem;
  color: #6b7280;
  margin-bottom: 4px;
}

.item-stock {
  font-size: 0.875rem;
  font-weight: 500;
}

.item-stock.low-stock {
  color: #dc2626;
}

.item-price,
.item-total {
  font-weight: 600;
  color: #1f2937;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-controls button {
  width: 32px;
  height: 32px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
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
  width: 50px;
  height: 32px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  text-align: center;
  font-size: 0.9rem;
}

.item-actions {
  display: flex;
  gap: 0.5rem;
}

.remove-btn,
.wishlist-btn {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.remove-btn {
  background: #fee2e2;
  color: #dc2626;
}

.remove-btn:hover {
  background: #fecaca;
}

.wishlist-btn {
  background: #f3f4f6;
  color: #4b5563;
}

.wishlist-btn:hover {
  background: #e5e7eb;
  color: #dc2626;
}

.cart-summary {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  position: sticky;
  top: 20px;
}

.cart-summary h2 {
  margin-bottom: 1.5rem;
  color: #1f2937;
}

.summary-details {
  margin-bottom: 1.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  color: #4b5563;
}

.summary-row.total {
  font-size: 1.25rem;
  font-weight: bold;
  color: #1f2937;
  margin-top: 0.75rem;
}

.summary-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 1rem 0;
}

.free {
  color: #10b981;
  font-weight: 600;
}

.shipping-note {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem;
  background: #f0f9ff;
  border-radius: 8px;
  color: #0369a1;
  font-size: 0.875rem;
  margin-top: 1rem;
}

.shipping-note.free {
  background: #f0fdf4;
  color: #065f46;
}

.shipping-note i {
  font-size: 1rem;
}

.summary-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin: 1.5rem 0;
}

.continue-shopping-btn,
.checkout-btn {
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

.continue-shopping-btn {
  background: white;
  border: 2px solid #4f46e5;
  color: #4f46e5;
}

.continue-shopping-btn:hover {
  background: #f5f3ff;
}

.checkout-btn {
  background: #4f46e5;
  color: white;
}

.checkout-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

.payment-methods {
  padding: 1.5rem 0;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
}

.payment-methods h3 {
  font-size: 1rem;
  color: #1f2937;
  margin-bottom: 1rem;
}

.payment-icons {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 2rem;
  color: #4b5563;
}

.secure-note {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.875rem;
  color: #6b7280;
}

.secure-note i {
  color: #10b981;
}

.cart-promo {
  padding-top: 1.5rem;
}

.cart-promo h3 {
  font-size: 1rem;
  color: #1f2937;
  margin-bottom: 1rem;
}

.promo-input {
  display: flex;
  gap: 0.5rem;
}

.promo-input input {
  flex: 1;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 0.9rem;
}

.apply-btn {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.apply-btn:hover {
  background: #4338ca;
}

.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.empty-cart-icon {
  font-size: 4rem;
  color: #d1d5db;
  margin-bottom: 1.5rem;
}

.empty-cart h2 {
  margin-bottom: 1rem;
  color: #1f2937;
}

.empty-cart p {
  color: #6b7280;
  margin-bottom: 2rem;
}

.start-shopping-btn {
  background: #4f46e5;
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.start-shopping-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

.recently-viewed {
  margin-top: 4rem;
}

.recently-viewed h2 {
  font-size: 1.5rem;
  color: #1f2937;
  margin-bottom: 1.5rem;
}

.recently-viewed-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

@media (max-width: 1024px) {
  .cart-container {
    grid-template-columns: 1fr;
  }
  
  .cart-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .cart-header {
    display: none;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 1rem;
    padding: 1rem;
  }
  
  .item-product {
    flex-direction: column;
    text-align: center;
  }
  
  .item-image {
    width: 120px;
    height: 120px;
  }
  
  .item-price,
  .item-quantity,
  .item-total,
  .item-actions {
    display: flex;
    justify-content: center;
  }
  
  .item-actions {
    justify-content: center;
  }
  
  .summary-actions {
    flex-direction: column;
  }
  
  .continue-shopping-btn,
  .checkout-btn {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .cart-page {
    padding: 0 10px;
  }
  
  .cart-item {
    padding: 1rem 0.5rem;
  }
  
  .quantity-controls input {
    width: 40px;
  }
  
  .payment-icons {
    font-size: 1.5rem;
  }
  
  .recently-viewed-grid {
    grid-template-columns: 1fr;
  }
}
</style>