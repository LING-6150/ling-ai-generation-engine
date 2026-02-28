<template>
  <div class="checkout-page">
    <div class="container">
      <h1 class="page-title">Checkout</h1>
      
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p>Your cart is empty. Please add items to your cart before checkout.</p>
        <router-link to="/" class="btn btn-primary">Continue Shopping</router-link>
      </div>

      <div v-else class="checkout-content">
        <div class="checkout-steps">
          <div class="step active">
            <div class="step-number">1</div>
            <div class="step-info">
              <div class="step-title">Shipping Address</div>
              <div class="step-subtitle">Where should we deliver your order?</div>
            </div>
          </div>
          
          <div class="step" :class="{ active: currentStep >= 2 }">
            <div class="step-number">2</div>
            <div class="step-info">
              <div class="step-title">Payment Method</div>
              <div class="step-subtitle">How would you like to pay?</div>
            </div>
          </div>
          
          <div class="step" :class="{ active: currentStep >= 3 }">
            <div class="step-number">3</div>
            <div class="step-info">
              <div class="step-title">Review & Place Order</div>
              <div class="step-subtitle">Confirm your order details</div>
            </div>
          </div>
        </div>

        <div class="checkout-form">
          <!-- Step 1: Shipping Address -->
          <div v-if="currentStep === 1" class="form-step">
            <h2 class="form-title">Shipping Address</h2>
            
            <div class="form-grid">
              <div class="form-group">
                <label for="firstName">First Name</label>
                <input 
                  type="text" 
                  id="firstName" 
                  v-model="shippingAddress.firstName"
                  class="form-input"
                  placeholder="John"
                />
              </div>
              
              <div class="form-group">
                <label for="lastName">Last Name</label>
                <input 
                  type="text" 
                  id="lastName" 
                  v-model="shippingAddress.lastName"
                  class="form-input"
                  placeholder="Doe"
                />
              </div>
              
              <div class="form-group full-width">
                <label for="address">Street Address</label>
                <input 
                  type="text" 
                  id="address" 
                  v-model="shippingAddress.address"
                  class="form-input"
                  placeholder="123 Main Street"
                />
              </div>
              
              <div class="form-group">
                <label for="city">City</label>
                <input 
                  type="text" 
                  id="city" 
                  v-model="shippingAddress.city"
                  class="form-input"
                  placeholder="New York"
                />
              </div>
              
              <div class="form-group">
                <label for="state">State</label>
                <input 
                  type="text" 
                  id="state" 
                  v-model="shippingAddress.state"
                  class="form-input"
                  placeholder="NY"
                />
              </div>
              
              <div class="form-group">
                <label for="zipCode">ZIP Code</label>
                <input 
                  type="text" 
                  id="zipCode" 
                  v-model="shippingAddress.zipCode"
                  class="form-input"
                  placeholder="10001"
                />
              </div>
              
              <div class="form-group">
                <label for="country">Country</label>
                <select 
                  id="country" 
                  v-model="shippingAddress.country"
                  class="form-input"
                >
                  <option value="US">United States</option>
                  <option value="CA">Canada</option>
                  <option value="UK">United Kingdom</option>
                  <option value="AU">Australia</option>
                </select>
              </div>
              
              <div class="form-group">
                <label for="phone">Phone Number</label>
                <input 
                  type="tel" 
                  id="phone" 
                  v-model="shippingAddress.phone"
                  class="form-input"
                  placeholder="(555) 123-4567"
                />
              </div>
            </div>
            
            <div class="form-actions">
              <button class="btn btn-primary" @click="nextStep">Continue to Payment</button>
            </div>
          </div>

          <!-- Step 2: Payment Method -->
          <div v-if="currentStep === 2" class="form-step">
            <h2 class="form-title">Payment Method</h2>
            
            <div class="payment-methods">
              <div 
                v-for="method in paymentMethods" 
                :key="method.id"
                class="payment-method"
                :class="{ active: paymentMethod === method.id }"
                @click="paymentMethod = method.id"
              >
                <div class="method-icon">{{ method.icon }}</div>
                <div class="method-info">
                  <div class="method-name">{{ method.name }}</div>
                  <div class="method-description">{{ method.description }}</div>
                </div>
              </div>
            </div>
            
            <div v-if="paymentMethod === 'card'" class="card-form">
              <div class="form-group">
                <label for="cardNumber">Card Number</label>
                <input 
                  type="text" 
                  id="cardNumber" 
                  v-model="cardDetails.number"
                  class="form-input"
                  placeholder="1234 5678 9012 3456"
                />
              </div>
              
              <div class="form-grid">
                <div class="form-group">
                  <label for="expiry">Expiry Date</label>
                  <input 
                    type="text" 
                    id="expiry" 
                    v-model="cardDetails.expiry"
                    class="form-input"
                    placeholder="MM/YY"
                  />
                </div>
                
                <div class="form-group">
                  <label for="cvv">CVV</label>
                  <input 
                    type="text" 
                    id="cvv" 
                    v-model="cardDetails.cvv"
                    class="form-input"
                    placeholder="123"
                  />
                </div>
              </div>
              
              <div class="form-group">
                <label for="cardName">Name on Card</label>
                <input 
                  type="text" 
                  id="cardName" 
                  v-model="cardDetails.name"
                  class="form-input"
                  placeholder="John Doe"
                />
              </div>
            </div>
            
            <div class="form-actions">
              <button class="btn btn-secondary" @click="prevStep">Back</button>
              <button class="btn btn-primary" @click="nextStep">Review Order</button>
            </div>
          </div>

          <!-- Step 3: Review Order -->
          <div v-if="currentStep === 3" class="form-step">
            <h2 class="form-title">Review Your Order</h2>
            
            <div class="order-review">
              <div class="review-section">
                <h3 class="review-title">Shipping Address</h3>
                <div class="review-content">
                  <p>{{ shippingAddress.firstName }} {{ shippingAddress.lastName }}</p>
                  <p>{{ shippingAddress.address }}</p>
                  <p>{{ shippingAddress.city }}, {{ shippingAddress.state }} {{ shippingAddress.zipCode }}</p>
                  <p>{{ shippingAddress.country }}</p>
                  <p>{{ shippingAddress.phone }}</p>
                </div>
              </div>
              
              <div class="review-section">
                <h3 class="review-title">Payment Method</h3>
                <div class="review-content">
                  <p>{{ getPaymentMethodName(paymentMethod) }}</p>
                  <p v-if="paymentMethod === 'card'">Card ending in {{ cardDetails.number.slice(-4) }}</p>
                </div>
              </div>
              
              <div class="review-section">
                <h3 class="review-title">Order Items</h3>
                <div class="order-items">
                  <div v-for="item in cartItems" :key="item.id" class="order-item">
                    <div class="item-info">
                      <div class="item-name">{{ item.name }}</div>
                      <div class="item-quantity">Quantity: {{ item.quantity }}</div>
                    </div>
                    <div class="item-price">${{ (item.price * item.quantity).toFixed(2) }}</div>
                  </div>
                </div>
              </div>
              
              <div class="review-section">
                <h3 class="review-title">Order Summary</h3>
                <div class="order-summary">
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
              </div>
            </div>
            
            <div class="form-actions">
              <button class="btn btn-secondary" @click="prevStep">Back</button>
              <button class="btn btn-primary" @click="placeOrder">Place Order</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores/cart'

const router = useRouter()
const cartStore = useCartStore()

const currentStep = ref(1)

const shippingAddress = ref({
  firstName: '',
  lastName: '',
  address: '',
  city: '',
  state: '',
  zipCode: '',
  country: 'US',
  phone: ''
})

const paymentMethod = ref('card')

const paymentMethods = [
  { id: 'card', name: 'Credit/Debit Card', description: 'Pay with Visa, Mastercard, or American Express', icon: '💳' },
  { id: 'paypal', name: 'PayPal', description: 'Pay securely with your PayPal account', icon: '🅿️' },
  { id: 'applepay', name: 'Apple Pay', description: 'Pay with Apple Pay', icon: '🍎' }
]

const cardDetails = ref({
  number: '',
  expiry: '',
  cvv: '',
  name: ''
})

const cartItems = computed(() => cartStore.items)
const subtotal = computed(() => cartStore.subtotal)
const shippingCost = computed(() => cartStore.shippingCost)
const tax = computed(() => cartStore.tax)
const total = computed(() => cartStore.total)

const nextStep = () => {
  if (currentStep.value < 3) {
    currentStep.value++
  }
}

const prevStep = () => {
  if (currentStep.value > 1) {
    currentStep.value--
  }
}

const getPaymentMethodName = (methodId) => {
  const method = paymentMethods.find(m => m.id === methodId)
  return method ? method.name : ''
}

const placeOrder = () => {
  // In a real application, this would process the payment and create an order
  cartStore.setShippingAddress(shippingAddress.value)
  cartStore.setPaymentMethod(paymentMethod.value)
  
  // Clear cart and redirect to confirmation
  cartStore.clearCart()
  router.push('/order-confirmation')
}
</script>

<style scoped>
.checkout-page {
  padding: 2rem 0 4rem;
}

.page-title {
  font-size: 2.5rem;
  margin-bottom: 2rem;
  color: var(--text-primary);
}

.empty-cart {
  text-align: center;
  padding: 3rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-xl);
}

.empty-cart p {
  font-size: 1.125rem;
  margin-bottom: 1.5rem;
  color: var(--text-secondary);
}

.checkout-steps {
  display: flex;
  justify-content: space-between;
  margin-bottom: 3rem;
  position: relative;
}

.checkout-steps:before {
  content: '';
  position: absolute;
  top: 1.5rem;
  left: 0;
  right: 0;
  height: 2px;
  background-color: var(--border-color);
  z-index: 1;
}

.step {
  display: flex;
  align-items: center;
  gap: 1rem;
  position: relative;
  z-index: 2;
  background-color: white;
  padding: 0.5rem;
}

.step.active .step-number {
  background-color: var(--primary-color);
  color: white;
}

.step-number {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 1.25rem;
  transition: all 0.3s ease;
}

.step-info {
  display: flex;
  flex-direction: column;
}

.step-title {
  font-weight: 600;
  color: var(--text-primary);
}

.step-subtitle {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.checkout-form {
  background-color: white;
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.form-step {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.form-title {
  font-size: 1.75rem;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  font-weight: 500;
  color: var(--text-primary);
}

.form-input {
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 1rem;
  transition: border-color 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1);
}

.form-input::placeholder {
  color: var(--text-light);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.payment-methods {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.payment-method {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border: 2px solid var(--border-color);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.2s ease;
}

.payment-method:hover {
  border-color: var(--primary-color);
}

.payment-method.active {
  border-color: var(--primary-color);
  background-color: rgba(37, 99, 235, 0.05);
}

.method-icon {
  font-size: 2rem;
}

.method-info {
  flex: 1;
}

.method-name {
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.25rem;
}

.method-description {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.card-form {
  margin-top: 2rem;
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.order-review {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.review-section {
  padding: 1.5rem;
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-lg);
}

.review-title {
  font-size: 1.25rem;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.review-content p {
  margin-bottom: 0.5rem;
  color: var(--text-secondary);
}

.order-items {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: white;
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
}

.item-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.item-name {
  font-weight: 500;
  color: var(--text-primary);
}

.item-quantity {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.item-price {
  font-weight: 600;
  color: var(--primary-color);
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid var(--border-color);
}

.summary-row.total {
  border-bottom: none;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--text-primary);
  padding-top: 0.5rem;
  border-top: 2px solid var(--border-color);
}

@media (max-width: 768px) {
  .checkout-steps {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .checkout-steps:before {
    display: none;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions button {
    width: 100%;
  }
  
  .payment-method {
    flex-direction: column;
    text-align: center;
  }
}
</style>