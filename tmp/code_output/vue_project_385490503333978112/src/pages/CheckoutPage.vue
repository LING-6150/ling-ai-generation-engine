<template>
  <div class="checkout-page">
    <h1>Checkout</h1>
    
    <div class="checkout-container" v-if="cartItems.length > 0">
      <div class="checkout-steps">
        <div class="step active">
          <div class="step-number">1</div>
          <div class="step-info">
            <div class="step-title">Shipping</div>
            <div class="step-subtitle">Enter your shipping details</div>
          </div>
        </div>
        <div class="step" :class="{ active: currentStep >= 2 }">
          <div class="step-number">2</div>
          <div class="step-info">
            <div class="step-title">Payment</div>
            <div class="step-subtitle">Select payment method</div>
          </div>
        </div>
        <div class="step" :class="{ active: currentStep >= 3 }">
          <div class="step-number">3</div>
          <div class="step-info">
            <div class="step-title">Review</div>
            <div class="step-subtitle">Review your order</div>
          </div>
        </div>
      </div>
      
      <div class="checkout-content">
        <div class="checkout-form">
          <!-- Step 1: Shipping Information -->
          <div v-if="currentStep === 1" class="step-content">
            <h2>Shipping Information</h2>
            
            <form class="shipping-form" @submit.prevent="nextStep">
              <div class="form-row">
                <div class="form-group">
                  <label for="firstName">First Name *</label>
                  <input 
                    type="text" 
                    id="firstName" 
                    v-model="shippingInfo.firstName"
                    required
                  >
                </div>
                <div class="form-group">
                  <label for="lastName">Last Name *</label>
                  <input 
                    type="text" 
                    id="lastName" 
                    v-model="shippingInfo.lastName"
                    required
                  >
                </div>
              </div>
              
              <div class="form-group">
                <label for="email">Email Address *</label>
                <input 
                  type="email" 
                  id="email" 
                  v-model="shippingInfo.email"
                  required
                >
              </div>
              
              <div class="form-group">
                <label for="phone">Phone Number *</label>
                <input 
                  type="tel" 
                  id="phone" 
                  v-model="shippingInfo.phone"
                  required
                >
              </div>
              
              <div class="form-group">
                <label for="address">Street Address *</label>
                <input 
                  type="text" 
                  id="address" 
                  v-model="shippingInfo.address"
                  required
                >
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label for="city">City *</label>
                  <input 
                    type="text" 
                    id="city" 
                    v-model="shippingInfo.city"
                    required
                  >
                </div>
                <div class="form-group">
                  <label for="state">State *</label>
                  <select id="state" v-model="shippingInfo.state" required>
                    <option value="">Select State</option>
                    <option value="CA">California</option>
                    <option value="NY">New York</option>
                    <option value="TX">Texas</option>
                    <option value="FL">Florida</option>
                    <option value="IL">Illinois</option>
                  </select>
                </div>
                <div class="form-group">
                  <label for="zipCode">ZIP Code *</label>
                  <input 
                    type="text" 
                    id="zipCode" 
                    v-model="shippingInfo.zipCode"
                    required
                  >
                </div>
              </div>
              
              <div class="form-group">
                <label for="country">Country *</label>
                <select id="country" v-model="shippingInfo.country" required>
                  <option value="US">United States</option>
                  <option value="CA">Canada</option>
                  <option value="UK">United Kingdom</option>
                  <option value="AU">Australia</option>
                </select>
              </div>
              
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="shippingInfo.saveAddress">
                  Save this address for future orders
                </label>
              </div>
              
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="shippingInfo.sameAsBilling">
                  Billing address is same as shipping address
                </label>
              </div>
              
              <div class="form-actions">
                <button type="button" class="back-btn" @click="goBack">
                  <i class="fas fa-arrow-left"></i>
                  Back to Cart
                </button>
                <button type="submit" class="next-btn">
                  Continue to Payment
                  <i class="fas fa-arrow-right"></i>
                </button>
              </div>
            </form>
          </div>
          
          <!-- Step 2: Payment Information -->
          <div v-if="currentStep === 2" class="step-content">
            <h2>Payment Method</h2>
            
            <div class="payment-methods">
              <div class="payment-method" :class="{ active: paymentMethod === 'card' }" @click="paymentMethod = 'card'">
                <i class="fas fa-credit-card"></i>
                <div class="method-info">
                  <div class="method-title">Credit/Debit Card</div>
                  <div class="method-description">Pay with Visa, Mastercard, or American Express</div>
                </div>
              </div>
              
              <div class="payment-method" :class="{ active: paymentMethod === 'paypal' }" @click="paymentMethod = 'paypal'">
                <i class="fab fa-paypal"></i>
                <div class="method-info">
                  <div class="method-title">PayPal</div>
                  <div class="method-description">Fast and secure payment with PayPal</div>
                </div>
              </div>
              
              <div class="payment-method" :class="{ active: paymentMethod === 'apple' }" @click="paymentMethod = 'apple'">
                <i class="fab fa-apple"></i>
                <div class="method-info">
                  <div class="method-title">Apple Pay</div>
                  <div class="method-description">Pay with Apple Pay</div>
                </div>
              </div>
            </div>
            
            <div v-if="paymentMethod === 'card'" class="card-form">
              <div class="form-group">
                <label for="cardNumber">Card Number *</label>
                <input 
                  type="text" 
                  id="cardNumber" 
                  v-model="paymentInfo.cardNumber"
                  placeholder="1234 5678 9012 3456"
                  required
                >
              </div>
              
              <div class="form-row">
                <div class="form-group">
                  <label for="expiryDate">Expiry Date *</label>
                  <input 
                    type="text" 
                    id="expiryDate" 
                    v-model="paymentInfo.expiryDate"
                    placeholder="MM/YY"
                    required
                  >
                </div>
                <div class="form-group">
                  <label for="cvv">CVV *</label>
                  <input 
                    type="text" 
                    id="cvv" 
                    v-model="paymentInfo.cvv"
                    placeholder="123"
                    required
                  >
                </div>
              </div>
              
              <div class="form-group">
                <label for="cardName">Name on Card *</label>
                <input 
                  type="text" 
                  id="cardName" 
                  v-model="paymentInfo.cardName"
                  required
                >
              </div>
              
              <div class="form-group">
                <label class="checkbox-label">
                  <input type="checkbox" v-model="paymentInfo.saveCard">
                  Save card for future purchases
                </label>
              </div>
            </div>
            
            <div v-if="paymentMethod === 'paypal'" class="paypal-info">
              <p>You will be redirected to PayPal to complete your payment securely.</p>
            </div>
            
            <div v-if="paymentMethod === 'apple'" class="apple-info">
              <p>Complete your purchase using Apple Pay on your device.</p>
            </div>
            
            <div class="form-actions">
              <button type="button" class="back-btn" @click="prevStep">
                <i class="fas fa-arrow-left"></i>
                Back to Shipping
              </button>
              <button type="button" class="next-btn" @click="nextStep">
                Review Order
                <i class="fas fa-arrow-right"></i>
              </button>
            </div>
          </div>
          
          <!-- Step 3: Review Order -->
          <div v-if="currentStep === 3" class="step-content">
            <h2>Review Your Order</h2>
            
            <div class="order-summary">
              <div class="order-items">
                <h3>Order Items</h3>
                <div class="order-item" v-for="item in cartItems" :key="item.id">
                  <img :src="item.image" :alt="item.name" class="order-item-image">
                  <div class="order-item-details">
                    <div class="order-item-name">{{ item.name }}</div>
                    <div class="order-item-quantity">Quantity: {{ item.quantity }}</div>
                  </div>
                  <div class="order-item-price">${{ (item.price * item.quantity).toFixed(2) }}</div>
                </div>
              </div>
              
              <div class="shipping-info-review">
                <h3>Shipping Information</h3>
                <div class="shipping-details">
                  <p>{{ shippingInfo.firstName }} {{ shippingInfo.lastName }}</p>
                  <p>{{ shippingInfo.address }}</p>
                  <p>{{ shippingInfo.city }}, {{ shippingInfo.state }} {{ shippingInfo.zipCode }}</p>
                  <p>{{ shippingInfo.country }}</p>
                  <p>{{ shippingInfo.email }}</p>
                  <p>{{ shippingInfo.phone }}</p>
                </div>
              </div>
              
              <div class="payment-info-review">
                <h3>Payment Method</h3>
                <div class="payment-details">
                  <p v-if="paymentMethod === 'card'">
                    <i class="fas fa-credit-card"></i>
                    Credit Card ending in {{ paymentInfo.cardNumber.slice(-4) }}
                  </p>
                  <p v-if="paymentMethod === 'paypal'">
                    <i class="fab fa-paypal"></i>
                    PayPal
                  </p>
                  <p v-if="paymentMethod === 'apple'">
                    <i class="fab fa-apple"></i>
                    Apple Pay
                  </p>
                </div>
              </div>
              
              <div class="order-totals">
                <h3>Order Summary</h3>
                <div class="total-row">
                  <span>Subtotal</span>
                  <span>${{ subtotal.toFixed(2) }}</span>
                </div>
                <div class="total-row">
                  <span>Shipping</span>
                  <span v-if="shippingCost > 0">${{ shippingCost.toFixed(2) }}</span>
                  <span v-else class="free">FREE</span>
                </div>
                <div class="total-row">
                  <span>Tax</span>
                  <span>${{ tax.toFixed(2) }}</span>
                </div>
                <div class="total-row grand-total">
                  <span>Total</span>
                  <span>${{ total.toFixed(2) }}</span>
                </div>
              </div>
            </div>
            
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="acceptTerms" required>
                I agree to the <a href="#" class="terms-link">Terms of Service</a> and <a href="#" class="terms-link">Privacy Policy</a> *
              </label>
            </div>
            
            <div class="form-actions">
              <button type="button" class="back-btn" @click="prevStep">
                <i class="fas fa-arrow-left"></i>
                Back to Payment
              </button>
              <button type="button" class="place-order-btn" @click="placeOrder" :disabled="!acceptTerms">
                Place Order
                <i class="fas fa-check"></i>
              </button>
            </div>
          </div>
        </div>
        
        <div class="checkout-sidebar">
          <div class="order-summary-card">
            <h3>Order Summary</h3>
            
            <div class="order-items-preview">
              <div class="order-item-preview" v-for="item in cartItems" :key="item.id">
                <div class="item-preview-info">
                  <div class="item-preview-name">{{ item.name }}</div>
                  <div class="item-preview-quantity">x{{ item.quantity }}</div>
                </div>
                <div class="item-preview-price">${{ (item.price * item.quantity).toFixed(2) }}</div>
              </div>
            </div>
            
            <div class="order-totals-preview">
              <div class="total-row">
                <span>Subtotal</span>
                <span>${{ subtotal.toFixed(2) }}</span>
              </div>
              <div class="total-row">
                <span>Shipping</span>
                <span v-if="shippingCost > 0">${{ shippingCost.toFixed(2) }}</span>
                <span v-else class="free">FREE</span>
              </div>
              <div class="total-row">
                <span>Tax</span>
                <span>${{ tax.toFixed(2) }}</span>
              </div>
              <div class="total-row grand-total">
                <span>Total</span>
                <span>${{ total.toFixed(2) }}</span>
              </div>
            </div>
            
            <div class="secure-checkout">
              <i class="fas fa-lock"></i>
              <span>Secure checkout</span>
            </div>
          </div>
          
          <div class="customer-support">
            <h4>Need Help?</h4>
            <p>Our customer support team is available 24/7 to assist you.</p>
            <div class="support-contact">
              <i class="fas fa-phone"></i>
              <span>+1 (555) 123-4567</span>
            </div>
            <div class="support-contact">
              <i class="fas fa-envelope"></i>
              <span>support@shopvue.com</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div v-else class="empty-checkout">
      <div class="empty-checkout-icon">
        <i class="fas fa-shopping-cart"></i>
      </div>
      <h2>Your cart is empty</h2>
      <p>Add items to your cart to proceed with checkout.</p>
      <button class="continue-shopping-btn" @click="continueShopping">
        Continue Shopping
      </button>
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
const paymentMethod = ref('card')
const acceptTerms = ref(false)

const shippingInfo = ref({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  address: '',
  city: '',
  state: '',
  zipCode: '',
  country: 'US',
  saveAddress: false,
  sameAsBilling: true
})

const paymentInfo = ref({
  cardNumber: '',
  expiryDate: '',
  cvv: '',
  cardName: '',
  saveCard: false
})

const cartItems = computed(() => cartStore.cartItems)
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

const goBack = () => {
  router.push('/cart')
}

const continueShopping = () => {
  router.push('/')
}

const placeOrder = () => {
  if (!acceptTerms.value) {
    alert('Please accept the terms and conditions to place your order.')
    return
  }
  
  // In a real application, this would process the order
  alert('Thank you for your order! Your order has been placed successfully.')
  
  // Clear the cart
  cartStore.clearCart()
  
  // Redirect to home page
  router.push('/')
}

// Auto-fill demo data for testing
const fillDemoData = () => {
  shippingInfo.value = {
    firstName: 'John',
    lastName: 'Doe',
    email: 'john.doe@example.com',
    phone: '+1 (555) 123-4567',
    address: '123 Main Street',
    city: 'San Francisco',
    state: 'CA',
    zipCode: '94107',
    country: 'US',
    saveAddress: true,
    sameAsBilling: true
  }
  
  paymentInfo.value = {
    cardNumber: '4111111111111111',
    expiryDate: '12/25',
    cvv: '123',
    cardName: 'John Doe',
    saveCard: false
  }
}

// Uncomment to auto-fill demo data for testing
// fillDemoData()
</script>

<style scoped>
.checkout-page {
  max-width: 1200px;
  margin: 0 auto;
}

.checkout-page h1 {
  margin-bottom: 2rem;
  color: #1f2937;
}

.checkout-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.checkout-steps {
  display: flex;
  background: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
  padding: 1.5rem;
}

.step {
  display: flex;
  align-items: center;
  gap: 1rem;
  flex: 1;
  opacity: 0.5;
}

.step.active {
  opacity: 1;
}

.step-number {
  width: 36px;
  height: 36px;
  background: #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  color: #6b7280;
}

.step.active .step-number {
  background: #4f46e5;
  color: white;
}

.step-info {
  flex: 1;
}

.step-title {
  font-weight: 600;
  color: #1f2937;
}

.step-subtitle {
  font-size: 0.875rem;
  color: #6b7280;
}

.checkout-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  padding: 2rem;
}

.checkout-form {
  padding-right: 2rem;
  border-right: 1px solid #e5e7eb;
}

.step-content {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.step-content h2 {
  color: #1f2937;
  margin-bottom: 0.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #4b5563;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #4f46e5;
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-weight: normal;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: auto;
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
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-method:hover {
  border-color: #4f46e5;
}

.payment-method.active {
  border-color: #4f46e5;
  background: #f5f3ff;
}

.payment-method i {
  font-size: 1.5rem;
  color: #4f46e5;
}

.method-info {
  flex: 1;
}

.method-title {
  font-weight: 600;
  color: #1f2937;
}

.method-description {
  font-size: 0.875rem;
  color: #6b7280;
}

.card-form {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.paypal-info,
.apple-info {
  margin-top: 1.5rem;
  padding: 1rem;
  background: #f9fafb;
  border-radius: 8px;
  color: #4b5563;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e5e7eb;
}

.back-btn,
.next-btn,
.place-order-btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
  border: none;
}

.back-btn {
  background: white;
  border: 2px solid #4f46e5;
  color: #4f46e5;
}

.back-btn:hover {
  background: #f5f3ff;
}

.next-btn {
  background: #4f46e5;
  color: white;
}

.next-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

.place-order-btn {
  background: #10b981;
  color: white;
}

.place-order-btn:hover:not(:disabled) {
  background: #059669;
  transform: translateY(-2px);
}

.place-order-btn:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
}

.order-summary {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.order-items,
.shipping-info-review,
.payment-info-review,
.order-totals {
  background: #f9fafb;
  padding: 1.5rem;
  border-radius: 8px;
}

.order-items h3,
.shipping-info-review h3,
.payment-info-review h3,
.order-totals h3 {
  margin-bottom: 1rem;
  color: #1f2937;
}

.order-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem 0;
  border-bottom: 1px solid #e5e7eb;
}

.order-item:last-child {
  border-bottom: none;
}

.order-item-image {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}

.order-item-details {
  flex: 1;
}

.order-item-name {
  font-weight: 500;
  color: #1f2937;
}

.order-item-quantity {
  font-size: 0.875rem;
  color: #6b7280;
}

.order-item-price {
  font-weight: 600;
  color: #1f2937;
}

.shipping-details p,
.payment-details p {
  margin: 0.5rem 0;
  color: #4b5563;
}

.payment-details i {
  margin-right: 0.5rem;
  color: #4f46e5;
}

.total-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  color: #4b5563;
}

.total-row.grand-total {
  font-size: 1.25rem;
  font-weight: bold;
  color: #1f2937;
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #e5e7eb;
}

.free {
  color: #10b981;
  font-weight: 600;
}

.terms-link {
  color: #4f46e5;
  text-decoration: none;
}

.terms-link:hover {
  text-decoration: underline;
}

.checkout-sidebar {
  position: sticky;
  top: 20px;
}

.order-summary-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  margin-bottom: 1.5rem;
}

.order-summary-card h3 {
  margin-bottom: 1.5rem;
  color: #1f2937;
}

.order-items-preview {
  margin-bottom: 1.5rem;
}

.order-item-preview {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.75rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e5e7eb;
}

.order-item-preview:last-child {
  border-bottom: none;
}

.item-preview-info {
  flex: 1;
}

.item-preview-name {
  font-weight: 500;
  color: #1f2937;
  font-size: 0.9rem;
}

.item-preview-quantity {
  font-size: 0.875rem;
  color: #6b7280;
}

.item-preview-price {
  font-weight: 600;
  color: #1f2937;
}

.order-totals-preview {
  margin-bottom: 1.5rem;
}

.secure-checkout {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 1rem;
  background: #f0f9ff;
  border-radius: 8px;
  color: #0369a1;
  font-weight: 500;
}

.secure-checkout i {
  font-size: 1.2rem;
}

.customer-support {
  background: #f9fafb;
  padding: 1.5rem;
  border-radius: 12px;
}

.customer-support h4 {
  margin-bottom: 1rem;
  color: #1f2937;
}

.customer-support p {
  color: #6b7280;
  margin-bottom: 1rem;
  font-size: 0.9rem;
}

.support-contact {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: #4b5563;
  font-size: 0.9rem;
}

.support-contact i {
  color: #4f46e5;
}

.empty-checkout {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.empty-checkout-icon {
  font-size: 4rem;
  color: #d1d5db;
  margin-bottom: 1.5rem;
}

.empty-checkout h2 {
  margin-bottom: 1rem;
  color: #1f2937;
}

.empty-checkout p {
  color: #6b7280;
  margin-bottom: 2rem;
}

.continue-shopping-btn {
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

.continue-shopping-btn:hover {
  background: #4338ca;
  transform: translateY(-2px);
}

@media (max-width: 1024px) {
  .checkout-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .checkout-form {
    padding-right: 0;
    border-right: none;
  }
  
  .checkout-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .checkout-steps {
    flex-direction: column;
    gap: 1rem;
  }
  
  .step {
    width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 1rem;
  }
  
  .back-btn,
  .next-btn,
  .place-order-btn {
    width: 100%;
    justify-content: center;
  }
  
  .payment-method {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .checkout-content {
    padding: 1rem;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .order-item {
    flex-direction: column;
    text-align: center;
  }
  
  .order-item-image {
    width: 80px;
    height: 80px;
  }
}
</style>