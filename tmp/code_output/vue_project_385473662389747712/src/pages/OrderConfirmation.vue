<template>
  <div class="confirmation-page">
    <div class="container">
      <div class="confirmation-content">
        <div class="confirmation-icon">✅</div>
        
        <h1 class="confirmation-title">Order Confirmed!</h1>
        
        <p class="confirmation-message">
          Thank you for your order. Your order has been successfully placed and is being processed.
        </p>
        
        <div class="confirmation-details">
          <div class="detail-card">
            <div class="detail-label">Order Number</div>
            <div class="detail-value">#{{ orderNumber }}</div>
          </div>
          
          <div class="detail-card">
            <div class="detail-label">Estimated Delivery</div>
            <div class="detail-value">{{ estimatedDelivery }}</div>
          </div>
          
          <div class="detail-card">
            <div class="detail-label">Total Amount</div>
            <div class="detail-value">${{ totalAmount.toFixed(2) }}</div>
          </div>
        </div>
        
        <div class="confirmation-actions">
          <router-link to="/" class="btn btn-primary btn-lg">Continue Shopping</router-link>
          <router-link to="/" class="btn btn-secondary btn-lg">View Order Details</router-link>
        </div>
        
        <div class="confirmation-note">
          <p>You will receive an email confirmation shortly with your order details and tracking information.</p>
          <p>If you have any questions about your order, please contact our customer support team.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const orderNumber = ref('')
const estimatedDelivery = ref('')
const totalAmount = ref(0)

onMounted(() => {
  // Generate a random order number
  orderNumber.value = 'ORD-' + Math.floor(100000 + Math.random() * 900000)
  
  // Calculate estimated delivery (3-7 business days from now)
  const deliveryDate = new Date()
  deliveryDate.setDate(deliveryDate.getDate() + 3 + Math.floor(Math.random() * 5))
  estimatedDelivery.value = deliveryDate.toLocaleDateString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
  
  // Set a random total amount for demonstration
  totalAmount.value = 150 + Math.random() * 200
})
</script>

<style scoped>
.confirmation-page {
  padding: 4rem 0;
  min-height: 70vh;
  display: flex;
  align-items: center;
}

.confirmation-content {
  max-width: 600px;
  margin: 0 auto;
  text-align: center;
}

.confirmation-icon {
  font-size: 4rem;
  margin-bottom: 2rem;
}

.confirmation-title {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.confirmation-message {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 3rem;
  line-height: 1.6;
}

.confirmation-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-bottom: 3rem;
}

.detail-card {
  background-color: white;
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.detail-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.detail-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: var(--primary-color);
}

.confirmation-actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 3rem;
}

.confirmation-note {
  background-color: var(--bg-tertiary);
  border-radius: var(--radius-lg);
  padding: 1.5rem;
  text-align: left;
}

.confirmation-note p {
  color: var(--text-secondary);
  margin-bottom: 0.75rem;
  line-height: 1.6;
}

.confirmation-note p:last-child {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .confirmation-details {
    grid-template-columns: 1fr;
  }
  
  .confirmation-actions {
    flex-direction: column;
  }
  
  .confirmation-title {
    font-size: 2rem;
  }
}
</style>