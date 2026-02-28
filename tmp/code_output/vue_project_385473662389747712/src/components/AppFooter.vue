<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-heading">ShopSphere</h3>
          <p class="footer-text">Your trusted destination for quality products and exceptional shopping experience.</p>
        </div>
        
        <div class="footer-section">
          <h4 class="footer-subheading">Quick Links</h4>
          <ul class="footer-links">
            <li><router-link to="/" class="footer-link">Home</router-link></li>
            <li><router-link to="/" class="footer-link">All Products</router-link></li>
            <li><router-link to="/cart" class="footer-link">Shopping Cart</router-link></li>
            <li><router-link to="/" class="footer-link">Contact Us</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4 class="footer-subheading">Customer Service</h4>
          <ul class="footer-links">
            <li><router-link to="/" class="footer-link">FAQ</router-link></li>
            <li><router-link to="/" class="footer-link">Shipping Policy</router-link></li>
            <li><router-link to="/" class="footer-link">Return Policy</router-link></li>
            <li><router-link to="/" class="footer-link">Privacy Policy</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4 class="footer-subheading">Contact Info</h4>
          <p class="footer-text">123 Commerce Street<br>New York, NY 10001<br>contact@shopsphere.com<br>(555) 123-4567</p>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; 2024 ShopSphere. All rights reserved.</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
</script>

<style scoped>
.footer {
  background-color: var(--text-primary);
  color: white;
  padding: 3rem 0 1.5rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-heading {
  color: white;
  font-size: 1.5rem;
  margin-bottom: 1rem;
}

.footer-subheading {
  color: white;
  font-size: 1.125rem;
  margin-bottom: 1rem;
}

.footer-text {
  color: var(--text-light);
  line-height: 1.6;
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 0.5rem;
}

.footer-link {
  color: var(--text-light);
  text-decoration: none;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: white;
}

.footer-bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 1.5rem;
  text-align: center;
  color: var(--text-light);
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>