<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-title">ShopSphere</h3>
          <p class="footer-description">
            Your trusted destination for premium shopping experience. 
            Quality products, exceptional service.
          </p>
          <div class="social-links">
            <a href="#" class="social-link">📘</a>
            <a href="#" class="social-link">🐦</a>
            <a href="#" class="social-link">📷</a>
            <a href="#" class="social-link">💼</a>
          </div>
        </div>

        <div class="footer-section">
          <h4 class="footer-heading">Shop</h4>
          <ul class="footer-links">
            <li><router-link to="/products" class="footer-link">All Products</router-link></li>
            <li><a href="#" class="footer-link">New Arrivals</a></li>
            <li><a href="#" class="footer-link">Best Sellers</a></li>
            <li><a href="#" class="footer-link">Sale</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="footer-heading">Support</h4>
          <ul class="footer-links">
            <li><a href="#" class="footer-link">Help Center</a></li>
            <li><a href="#" class="footer-link">Contact Us</a></li>
            <li><a href="#" class="footer-link">Shipping Info</a></li>
            <li><a href="#" class="footer-link">Returns & Exchanges</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="footer-heading">Company</h4>
          <ul class="footer-links">
            <li><a href="#" class="footer-link">About Us</a></li>
            <li><a href="#" class="footer-link">Careers</a></li>
            <li><a href="#" class="footer-link">Privacy Policy</a></li>
            <li><a href="#" class="footer-link">Terms of Service</a></li>
          </ul>
        </div>

        <div class="footer-section">
          <h4 class="footer-heading">Newsletter</h4>
          <p class="newsletter-description">
            Subscribe to get special offers and updates
          </p>
          <div class="newsletter-form">
            <input
              v-model="email"
              type="email"
              placeholder="Your email"
              class="newsletter-input"
            />
            <button class="newsletter-button" @click="subscribe">
              Subscribe
            </button>
          </div>
        </div>
      </div>

      <div class="footer-bottom">
        <div class="copyright">
          © 2024 ShopSphere. All rights reserved.
        </div>
        <div class="payment-methods">
          <span class="payment-method">💳</span>
          <span class="payment-method">💵</span>
          <span class="payment-method">💰</span>
          <span class="payment-method">💲</span>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue'

const email = ref('')

const subscribe = () => {
  if (email.value.trim()) {
    alert(`Thank you for subscribing with ${email.value}!`)
    email.value = ''
  }
}
</script>

<style scoped>
.footer {
  background-color: var(--bg-primary);
  border-top: 1px solid var(--border-color);
  padding: 3rem 0 1.5rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 2rem;
  margin-bottom: 3rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-title {
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.footer-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.6;
}

.social-links {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  background-color: var(--bg-tertiary);
  color: var(--text-secondary);
  border-radius: var(--radius-full);
  text-decoration: none;
  font-size: 1.25rem;
  transition: all 0.2s ease;
}

.social-link:hover {
  background-color: var(--primary-color);
  color: white;
  transform: translateY(-2px);
}

.footer-heading {
  font-size: 1rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 0.5rem;
}

.footer-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.footer-link {
  color: var(--text-secondary);
  text-decoration: none;
  font-size: 0.875rem;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: var(--primary-color);
}

.newsletter-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
  margin-bottom: 1rem;
}

.newsletter-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.newsletter-input {
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 0.875rem;
  background-color: white;
  color: var(--text-primary);
}

.newsletter-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.newsletter-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-weight: 600;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.newsletter-button:hover {
  background-color: var(--primary-dark);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.footer-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 1.5rem;
  border-top: 1px solid var(--border-color);
}

.copyright {
  color: var(--text-light);
  font-size: 0.875rem;
}

.payment-methods {
  display: flex;
  gap: 0.5rem;
}

.payment-method {
  font-size: 1.5rem;
  opacity: 0.7;
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .footer {
    padding: 2rem 0 1rem;
  }
  
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
    margin-bottom: 2rem;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>