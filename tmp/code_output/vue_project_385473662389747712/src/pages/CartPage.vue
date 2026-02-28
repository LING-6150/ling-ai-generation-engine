<template>
  <div class="cart-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">Your Shopping Cart</h1>
        <p class="page-subtitle">Review your items and proceed to checkout</p>
      </div>

      <div v-if="cartStore.items.length === 0" class="empty-cart">
        <div class="empty-cart-icon">🛒</div>
        <h2 class="empty-cart-title">Your cart is empty</h2>
        <p class="empty-cart-description">Add some products to your cart to see them here</p>
        <router-link to="/products" class="shop-button">
          Continue Shopping →
        </router-link>
      </div>

      <div v-else class="cart-layout">
        <main class="cart-items">
          <div class="cart-header">
            <h2 class="cart-section-title">Cart Items ({{ cartStore.totalItems }})</h2>
            <button class="clear-cart-btn" @click="cartStore.clearCart">
              Clear Cart
            </button>
          </div>

          <div class="items-list">
            <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
              <img :src="item.image" :alt="item.name" class="item-image" />
              
              <div class="item-details">
                <div class="item-header">
                  <h3 class="item-name">{{ item.name }}</h3>
                  <button class="remove-item-btn" @click="cartStore.removeFromCart(item.id)">
                    ✕
                  </button>
                </div>
                
                <div class="item-category">{{ item.category }}</div>
                
                <div class="item-features">
                  <span v-for="feature in item.features.slice(0, 2)" :key="feature" class="feature-tag">
                    {{ feature }}
                  </span>
                </div>
                
                <div class="item-footer">
                  <div class="quantity-controls">
                    <button
                      class="quantity-btn"
                      @click="decreaseQuantity(item.id)"
                      :disabled="item.quantity <= 1"
                    >
                      −
                    </button>
                    <span class="quantity-value">{{ item.quantity }}</span>
                    <button class="quantity-btn" @click="increaseQuantity(item.id)">
                      +
                    </button>
                  </div>
                  
                  <div class="item-price">
                    <div class="item-total">${{ (item.price * item.quantity).toFixed(2) }}</div>
                    <div class="item-unit-price">${{ item.price.toFixed(2) }} each</div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="continue-shopping">
            <router-link to="/products" class="continue-shopping-link">
              ← Continue Shopping
            </router-link>
          </div>
        </main>

        <aside class="cart-summary">
          <div class="summary-card">
            <h2 class="summary-title">Order Summary</h2>
            
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
                <span class="summary-label">Tax</span>
                <span class="summary-value">${{ cartStore.tax.toFixed(2) }}</span>
              </div>
              
              <div class="summary-divider"></div>
              
              <div class="summary-row total-row">
                <span class="summary-label">Total</span>
                <span class="summary-value total-value">${{ cartStore.total.toFixed(2) }}</span>
              </div>
            </div>

            <div class="shipping-note" v-if="cartStore.subtotal < 50">
              Add ${{ (50 - cartStore.subtotal).toFixed(2) }} more to get FREE shipping!
            </div>

            <button class="checkout-btn" @click="proceedToCheckout">
              Proceed to Checkout
            </button>

            <div class="payment-methods">
              <div class="payment-icons">
                <span class="payment-icon">💳</span>
                <span class="payment-icon">💵</span>
                <span class="payment-icon">💰</span>
                <span class="payment-icon">💲</span>
              </div>
              <p class="payment-note">Secure payment with 256-bit SSL encryption</p>
            </div>
          </div>

          <div class="customer-support">
            <h3 class="support-title">Need Help?</h3>
            <p class="support-description">Our customer support team is available 24/7</p>
            <div class="support-contact">
              <span class="contact-icon">📞</span>
              <span class="contact-info">1-800-SHOP-NOW</span>
            </div>
          </div>
        </aside>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useCartStore } from '../stores/cart'
import { useRouter } from 'vue-router'

const cartStore = useCartStore()
const router = useRouter()

const increaseQuantity = (productId) => {
  const item = cartStore.items.find(item => item.id === productId)
  if (item) {
    cartStore.updateQuantity(productId, item.quantity + 1)
  }
}

const decreaseQuantity = (productId) => {
  const item = cartStore.items.find(item => item.id === productId)
  if (item && item.quantity > 1) {
    cartStore.updateQuantity(productId, item.quantity - 1)
  }
}

const proceedToCheckout = () => {
  alert('Checkout functionality would be implemented here')
  // In a real application, this would navigate to checkout page
}
</script>

<style scoped>
.cart-page {
  padding: 3rem 0;
}

.page-header {
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.page-subtitle {
  color: var(--text-secondary);
  font-size: 1.125rem;
}

.empty-cart {
  text-align: center;
  padding: 4rem 2rem;
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
}

.empty-cart-icon {
  font-size: 4rem;
  margin-bottom: 1.5rem;
  opacity: 0.5;
}

.empty-cart-title {
  font-size: 1.75rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.empty-cart-description {
  color: var(--text-secondary);
  margin-bottom: 2rem;
}

.shop-button {
  display: inline-block;
  background-color: var(--primary-color);
  color: white;
  padding: 1rem 2rem;
  border-radius: var(--radius-full);
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.shop-button:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.cart-layout {
  display: grid;
  grid-template-columns: 1fr 380px;
  gap: 3rem;
}

.cart-items {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cart-section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
}

.clear-cart-btn {
  background-color: transparent;
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
  padding: 0.5rem 1rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.clear-cart-btn:hover {
  background-color: var(--bg-tertiary);
  color: var(--text-primary);
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.cart-item {
  display: grid;
  grid-template-columns: 120px 1fr;
  gap: 1.5rem;
  padding: 1.5rem;
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  transition: all 0.2s ease;
}

.cart-item:hover {
  border-color: var(--primary-color);
  box-shadow: var(--shadow-sm);
}

.item-image {
  width: 100%;
  height: 120px;
  object-fit: cover;
  border-radius: var(--radius-md);
}

.item-details {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.item-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.item-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.remove-item-btn {
  background: none;
  border: none;
  color: var(--text-light);
  font-size: 1.25rem;
  cursor: pointer;
  padding: 0.25rem;
  transition: color 0.2s ease;
}

.remove-item-btn:hover {
  color: var(--error-color);
}

.item-category {
  color: var(--text-secondary);
  font-size: 0.875rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.item-features {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.feature-tag {
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  padding: 0.25rem 0.75rem;
  border-radius: var(--radius-full);
  font-size: 0.75rem;
  font-weight: 500;
}

.item-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
}

.quantity-controls {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.quantity-btn {
  width: 2rem;
  height: 2rem;
  background-color: white;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-full);
  font-size: 1.25rem;
  color: var(--text-primary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.quantity-btn:hover:not(:disabled) {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.quantity-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-value {
  min-width: 2rem;
  text-align: center;
  font-weight: 600;
  color: var(--text-primary);
}

.item-price {
  text-align: right;
}

.item-total {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

.item-unit-price {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.continue-shopping {
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.continue-shopping-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.2s ease;
}

.continue-shopping-link:hover {
  color: var(--primary-dark);
  transform: translateX(-4px);
}

.cart-summary {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.summary-card {
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  padding: 2rem;
}

.summary-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1.5rem;
}

.summary-details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.summary-label {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.summary-value {
  color: var(--text-primary);
  font-weight: 500;
  font-size: 0.875rem;
}

.summary-divider {
  height: 1px;
  background-color: var(--border-color);
  margin: 0.5rem 0;
}

.total-row {
  margin-top: 0.5rem;
}

.total-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
}

.shipping-note {
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  padding: 0.75rem 1rem;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  text-align: center;
  margin-bottom: 1.5rem;
}

.checkout-btn {
  width: 100%;
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 1rem 2rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-bottom: 1.5rem;
}

.checkout-btn:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.payment-methods {
  text-align: center;
}

.payment-icons {
  display: flex;
  justify-content: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.payment-icon {
  font-size: 1.5rem;
  opacity: 0.7;
}

.payment-note {
  color: var(--text-light);
  font-size: 0.75rem;
}

.customer-support {
  background-color: var(--bg-primary);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  padding: 1.5rem;
  text-align: center;
}

.support-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.support-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

.support-contact {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.contact-icon {
  font-size: 1.25rem;
}

.contact-info {
  color: var(--primary-color);
  font-weight: 600;
  font-size: 0.875rem;
}

@media (max-width: 1024px) {
  .cart-layout {
    grid-template-columns: 1fr;
  }
  
  .cart-item {
    grid-template-columns: 100px 1fr;
  }
}

@media (max-width: 768px) {
  .cart-page {
    padding: 2rem 0;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .cart-item {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .item-image {
    width: 100%;
    height: 200px;
  }
  
  .item-footer {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .item-price {
    text-align: left;
  }
}
</style>