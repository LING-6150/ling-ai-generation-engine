<template>
  <div class="cart-page">
    <div class="container">
      <!-- Page Header -->
      <div class="page-header">
        <h1 class="page-title">Shopping Cart</h1>
        <p class="page-subtitle">Review your items and proceed to checkout</p>
      </div>

      <div class="cart-content">
        <!-- Cart Items -->
        <div class="cart-items-section">
          <div v-if="cartStore.cartItems.length === 0" class="empty-cart">
            <div class="empty-cart-icon">
              <i class="fas fa-shopping-cart"></i>
            </div>
            <h2 class="empty-cart-title">Your cart is empty</h2>
            <p class="empty-cart-message">Add some products to your cart to see them here</p>
            <router-link to="/products" class="btn btn-primary">
              <i class="fas fa-shopping-bag"></i>
              Browse Products
            </router-link>
          </div>

          <div v-else>
            <div class="cart-items-header">
              <h2 class="cart-items-title">Cart Items ({{ cartStore.totalItems }})</h2>
              <button @click="cartStore.clearCart()" class="btn btn-text">
                <i class="fas fa-trash"></i>
                Clear Cart
              </button>
            </div>

            <div class="cart-items">
              <div v-for="item in cartStore.cartItems" :key="item.id" class="cart-item">
                <div class="cart-item-image">
                  <img :src="item.image" :alt="item.name">
                </div>
                
                <div class="cart-item-details">
                  <div class="cart-item-header">
                    <h3 class="cart-item-name">{{ item.name }}</h3>
                    <button @click="cartStore.removeFromCart(item.id)" class="remove-item">
                      <i class="fas fa-times"></i>
                    </button>
                  </div>
                  
                  <p class="cart-item-description">{{ item.description }}</p>
                  
                  <div class="cart-item-footer">
                    <div class="quantity-controls">
                      <button 
                        @click="decreaseQuantity(item)" 
                        class="quantity-btn"
                        :disabled="item.quantity <= 1"
                      >
                        <i class="fas fa-minus"></i>
                      </button>
                      
                      <input 
                        type="number" 
                        v-model.number="item.quantity" 
                        @change="updateQuantity(item)"
                        min="1"
                        :max="item.stock"
                        class="quantity-input"
                      >
                      
                      <button 
                        @click="increaseQuantity(item)" 
                        class="quantity-btn"
                        :disabled="item.quantity >= item.stock"
                      >
                        <i class="fas fa-plus"></i>
                      </button>
                    </div>
                    
                    <div class="cart-item-price">
                      ${{ (item.price * item.quantity).toFixed(2) }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary -->
        <div v-if="cartStore.cartItems.length > 0" class="order-summary">
          <h2 class="order-summary-title">Order Summary</h2>
          
          <div class="summary-details">
            <div class="summary-row">
              <span class="summary-label">Subtotal</span>
              <span class="summary-value">${{ cartStore.subtotal.toFixed(2) }}</span>
            </div>
            
            <div class="summary-row">
              <span class="summary-label">Shipping</span>
              <span class="summary-value">
                {{ cartStore.shipping === 0 ? 'FREE' : `$${cartStore.shipping.toFixed(2)}` }}
              </span>
            </div>
            
            <div class="summary-row">
              <span class="summary-label">Tax (8%)</span>
              <span class="summary-value">${{ cartStore.tax.toFixed(2) }}</span>
            </div>
            
            <div class="summary-divider"></div>
            
            <div class="summary-row total-row">
              <span class="summary-label">Total</span>
              <span class="summary-value total-value">${{ cartStore.total.toFixed(2) }}</span>
            </div>
          </div>
          
          <div class="shipping-note" v-if="cartStore.subtotal < 50">
            <i class="fas fa-info-circle"></i>
            Add ${{ (50 - cartStore.subtotal).toFixed(2) }} more to get free shipping!
          </div>
          
          <div class="checkout-actions">
            <button @click="proceedToCheckout" class="btn btn-primary btn-large">
              <i class="fas fa-lock"></i>
              Proceed to Checkout
            </button>
            
            <router-link to="/products" class="btn btn-outline">
              <i class="fas fa-arrow-left"></i>
              Continue Shopping
            </router-link>
          </div>
          
          <div class="payment-methods">
            <p class="payment-methods-title">We accept:</p>
            <div class="payment-icons">
              <i class="fab fa-cc-visa"></i>
              <i class="fab fa-cc-mastercard"></i>
              <i class="fab fa-cc-amex"></i>
              <i class="fab fa-cc-paypal"></i>
              <i class="fab fa-cc-apple-pay"></i>
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

const proceedToCheckout = () => {
  alert(`Checkout initiated!\n\nTotal: $${cartStore.total.toFixed(2)}\nItems: ${cartStore.totalItems}\n\nThank you for shopping with TechShop!`)
  cartStore.clearCart()
}
</script>

<style scoped>
.cart-page {
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

/* Cart Content */
.cart-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 3rem;
}

/* Cart Items Section */
.cart-items-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.cart-items-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.btn-text {
  background: none;
  border: none;
  color: #ff5252;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.btn-text:hover {
  background-color: #ffebee;
}

/* Cart Items */
.cart-items {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cart-item {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 1.5rem;
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cart-item-image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
  overflow: hidden;
}

.cart-item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cart-item-details {
  flex: 1;
}

.cart-item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.cart-item-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.remove-item {
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  font-size: 1rem;
  padding: 0.25rem;
  border-radius: 4px;
  transition: color 0.3s, background-color 0.3s;
}

.remove-item:hover {
  color: #ff5252;
  background-color: #ffebee;
}

.cart-item-description {
  color: #666;
  font-size: 0.9rem;
  line-height: 1.4;
  margin-bottom: 1rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.cart-item-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  width: 32px;
  height: 32px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.quantity-btn:hover:not(:disabled) {
  border-color: #1a237e;
  color: #1a237e;
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-input {
  width: 60px;
  height: 32px;
  border: 1px solid #ddd;
  border-radius: 4px;
  text-align: center;
  font-size: 0.9rem;
}

.quantity-input:focus {
  outline: none;
  border-color: #1a237e;
}

.cart-item-price {
  font-size: 1.25rem;
  font-weight: 700;
  color: #1a237e;
}

/* Empty Cart */
.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.empty-cart-icon {
  width: 80px;
  height: 80px;
  background: #e8eaf6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1.5rem;
}

.empty-cart-icon i {
  font-size: 2rem;
  color: #1a237e;
}

.empty-cart-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
}

.empty-cart-message {
  color: #666;
  margin-bottom: 2rem;
}

/* Order Summary */
.order-summary {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 2rem;
}

.order-summary-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1.5rem;
}

.summary-details {
  margin-bottom: 2rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.summary-label {
  color: #666;
  font-size: 0.9rem;
}

.summary-value {
  font-weight: 500;
  color: #333;
}

.summary-divider {
  height: 1px;
  background: #eee;
  margin: 1.5rem 0;
}

.total-row {
  margin-top: 1rem;
}

.total-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a237e;
}

.shipping-note {
  background: #e8f5e9;
  color: #2e7d32;
  padding: 0.75rem;
  border-radius: 4px;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.shipping-note i {
  font-size: 1rem;
}

.checkout-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 2rem;
}

.btn-large {
  padding: 1rem;
  font-size: 1.1rem;
}

.payment-methods-title {
  color: #666;
  font-size: 0.9rem;
  margin-bottom: 0.75rem;
}

.payment-icons {
  display: flex;
  gap: 1rem;
  font-size: 1.5rem;
  color: #666;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .cart-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .order-summary {
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
  
  .cart-item-image {
    width: 100%;
    height: 200px;
  }
  
  .cart-item-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .cart-item-price {
    align-self: flex-end;
  }
}

@media (max-width: 480px) {
  .cart-items-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .checkout-actions .btn {
    width: 100%;
  }
}
</style>