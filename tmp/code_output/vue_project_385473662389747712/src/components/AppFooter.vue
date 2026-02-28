<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <div class="footer-logo">ShopSphere</div>
          <p class="footer-description">
            Your one-stop shop for all your needs. Quality products, competitive prices, and excellent customer service.
          </p>
          <div class="social-links">
            <a href="#" class="social-link">📘</a>
            <a href="#" class="social-link">🐦</a>
            <a href="#" class="social-link">📷</a>
            <a href="#" class="social-link">📺</a>
          </div>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">Shop</h3>
          <ul class="footer-links">
            <li><router-link to="/" class="footer-link">All Products</router-link></li>
            <li><router-link to="/" class="footer-link">New Arrivals</router-link></li>
            <li><router-link to="/" class="footer-link">Best Sellers</router-link></li>
            <li><router-link to="/" class="footer-link">Sale Items</router-link></li>
            <li><router-link to="/" class="footer-link">Gift Cards</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">Support</h3>
          <ul class="footer-links">
            <li><router-link to="/" class="footer-link">Help Center</router-link></li>
            <li><router-link to="/" class="footer-link">Contact Us</router-link></li>
            <li><router-link to="/" class="footer-link">Shipping Info</router-link></li>
            <li><router-link to="/" class="footer-link">Returns & Exchanges</router-link></li>
            <li><router-link to="/" class="footer-link">FAQ</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">Company</h3>
          <ul class="footer-links">
            <li><router-link to="/" class="footer-link">About Us</router-link></li>
            <li><router-link to="/" class="footer-link">Careers</router-link></li>
            <li><router-link to="/" class="footer-link">Press</router-link></li>
            <li><router-link to="/" class="footer-link">Blog</router-link></li>
            <li><router-link to="/" class="footer-link">Privacy Policy</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h3 class="footer-heading">Newsletter</h3>
          <p class="newsletter-text">Subscribe to get special offers, free giveaways, and exclusive deals.</p>
          <div class="newsletter-form">
            <input 
              type="email" 
              v-model="email"
              placeholder="Enter your email"
              class="newsletter-input"
            />
            <button class="newsletter-btn" @click="subscribe">Subscribe</button>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <div class="copyright">
          © 2024 ShopSphere. All rights reserved.
        </div>
        <div class="payment-methods">
          <span class="payment-icon">💳</span>
          <span class="payment-icon">🏦</span>
          <span class="payment-icon">🅿️</span>
          <span class="payment-icon">🍎</span>
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
  background-color: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding: 4rem 0 2rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 3rem;
  margin-bottom: 3rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
}

.footer-description {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
}

.social-links {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2.5rem;
  height: 2.5rem;
  background-color: white;
  border-radius: 50%;
  text-decoration: none;
  font-size: 1.25rem;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.social-link:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.footer-heading {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 1rem;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
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

.newsletter-text {
  color: var(--text-secondary);
  font-size: 0.875rem;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.newsletter-form {
  display: flex;
  gap: 0.5rem;
}

.newsletter-input {
  flex: 1;
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: var(--radius-md);
  font-family: var(--font-sans);
  font-size: 0.875rem;
  transition: border-color 0.2s ease;
}

.newsletter-input:focus {
  outline: none;
  border-color: var(--primary-color);
}

.newsletter-btn {
  padding: 0.75rem 1.5rem;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--radius-md);
  font-weight: 500;
  font-size: 0.875rem;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.newsletter-btn:hover {
  background-color: var(--primary-dark);
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.copyright {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.payment-methods {
  display: flex;
  gap: 0.5rem;
}

.payment-icon {
  font-size: 1.5rem;
  opacity: 0.7;
}

@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
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
  
  .newsletter-form {
    flex-direction: column;
  }
}
</style>