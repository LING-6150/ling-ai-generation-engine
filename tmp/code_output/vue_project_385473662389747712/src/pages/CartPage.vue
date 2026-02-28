<template>
  <div class="cart-page">
    <div class="container">
      <h1 class="page-title">Shopping Cart</h1>
      
      <div v-if="cartItems.length === 0" class="empty-cart">
        <div class="empty-cart-icon">🛒</div>
        <h2>Your cart is empty</h2>
        <p>Add some products to your cart to get started!</p>
        <router-link to="/" class="btn btn-primary btn-lg">Continue Shopping</router-link>
      </div>

      <div v-else class="cart-content">
        <div class="cart-items">
          <div class="cart-item" v-for="item in cartItems" :key="item.id">
            <div class="item-image">
              <img :src="item.image" :alt="item.name" />
            </div>
            
            <div class="item-details">
              <div class="item-header">
                <h3 class="item-name">{{ item.name }}</h3>
                <button class="remove-btn" @click="removeItem(item.id)">×</button>
              </div>
              
              <div class="item-category">{{ item.category }}</div>
              
              <div class="item-price">${{ item.price.toFixed(2) }}</div>
              
              <div class="item-actions">
                <div class="quantity-control">
                  <button class="qty-btn" @click="decreaseQuantity(item.id)" :disabled="item.quantity <= 1">-</button>
                  <span class="qty-value">{{ item.quantity }}</span>
                  <button class="qty-btn" @click="increaseQuantity(item.id)" :disabled="item.quantity >= 10">+</button>
                </div>
                
                <div class="item-total">${{ (item.price * item.quantity).toFixed(2) }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="cart-summary">
          <h2 class="summary-title">Order Summary</h2>
          
          <div class="summary-details">
            <div class="summary-row">
              <span>Subtotal</span>
              <span>${{ subtotal.toFixed(2) }}</span>
            </div>
            
            <div class="summary-row">
              <span>Shipping</span>
              <span v-if="shippingCost === 0">FREE</span>
              <span v-else>${{ shippingCost.toFixed(2) }}</span>
            </div>
            
            <div class="summary-row">
              <span>Tax</span>
              <span>${{ tax.toFixed(2) }}</span>
            </div>
            
            <div class="summary-row total">
              <span>Total</span>
              <span>${{ total.toFixed(2) }}</span>
            </div>
          </div>

          <div class="shipping-note" v-if="shippingCost === 0">
            🎉 You've qualified for free shipping!
          </div>
          
          <div class="shipping-note" v-else>
            Add ${{ (50 - subtotal).toFixed(2) }} more to get free shipping
          </div>

          <div class="summary-actions">
            <router-link to="/" class="btn btn-secondary btn-full">Continue Shopping</router-link>
            <button class="btn btn-primary btn-full" @click="proceedToCheckout">Proceed to Checkout</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const cartItems = computed(() => cartStore.items)
const subtotal = computed(() => cartStore.subtotal)
const shippingCost = computed(() => cartStore.shippingCost)
const tax = computed(() => cartStore.tax)
const total = computed(() => cartStore.total)

const removeItem = (productId) => {
  cartStore.removeFromCart(productId)
}

const increaseQuantity = (productId) => {
  const item = cartItems.value.find(item => item.id === productId)
  if (item && item.quantity < 10) {
    cartStore.updateQuantity(productId, item.quantity + 1)
  }
}

const decreaseQuantity = (productId) => {
  const item = cartItems.value.find(item => item.id === productId)
  if (item && item.quantity > 1) {
    cartStore.updateQuantity(productId, item.quantity - 1)
  }
}

const proceedToCheckout = () => {
  router.push('/checkout')
}
</script>

<style scoped>
.cart-page {
  padding: 2rem 0 4rem;
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: var(--text-primary);
}

.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-xl);
}

.empty-cart-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
}

.empty-cart h2 {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.empty-cart p {
  color: var(--text-secondary);
  margin-bottom: 2rem;
  font-size: 1.125rem;
}

.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cart-item {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 1.5rem;
  padding: 1.5rem;
  background-color: white;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.item-image {
  width: 120px;
  height: 120px;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.item-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  transition: color 0.2s ease;
}

.remove-btn:hover {
  color: #ef4444;
}

.item-category {
  color: var(--text-secondary);
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.item-price {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--primary-color);
}

.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.qty-btn {
  width: 2rem;
  height: 2rem;
  border: 1px solid var(--border-color);
  background-color: white;
  border-radius: var(--radius-md);
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.qty-btn:hover:not(:disabled) {
  border-color: var(--primary-color);
  color: var(--primary-color);
}

.qty-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.qty-value {
  min-width: 2rem;
  text-align: center;
  font-weight: 500;
}

.item-total {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-primary);
}

.cart-summary {
  background-color: white;
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  position: sticky;
  top: 2rem;
}

.summary-title {
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
  color: var(--text-primary);
}

.summary-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid var(--border-color);
}

.summary-row.total {
  border-bottom: none;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  padding-top: 0.75rem;
  border-top: 2px solid var(--border-color);
}

.shipping-note {
  text-align: center;
  padding: 0.75rem;
  background-color: rgba(16, 185, 129, 0.1);
  color: var(--secondary-color);
  border-radius: var(--radius-md);
  margin-bottom: 1.5rem;
  font-weight: 500;
}

.summary-actions {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.btn-full {
  width: 100%;
}

@media (max-width: 1024px) {
  .cart-content {
    grid-template-columns: 1fr;
  }
  
  .cart-summary {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-title {
    font-size: 2rem;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .item-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
}
</style>