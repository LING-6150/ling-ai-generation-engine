<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-section">
        <div class="footer-brand">
          <i class="fas fa-shopping-bag"></i>
          <span class="brand-name">ShopVue</span>
        </div>
        <p class="footer-description">
          Your one-stop destination for premium products. Quality, convenience, and customer satisfaction guaranteed.
        </p>
        <div class="social-links">
          <a href="#" class="social-link">
            <i class="fab fa-facebook-f"></i>
          </a>
          <a href="#" class="social-link">
            <i class="fab fa-twitter"></i>
          </a>
          <a href="#" class="social-link">
            <i class="fab fa-instagram"></i>
          </a>
          <a href="#" class="social-link">
            <i class="fab fa-linkedin-in"></i>
          </a>
        </div>
      </div>
      
      <div class="footer-section">
        <h3 class="footer-heading">Quick Links</h3>
        <ul class="footer-links">
          <li><router-link to="/" class="footer-link">Home</router-link></li>
          <li><router-link to="/" class="footer-link">Shop</router-link></li>
          <li><router-link to="/cart" class="footer-link">Cart</router-link></li>
          <li><router-link to="/" class="footer-link">About Us</router-link></li>
          <li><router-link to="/" class="footer-link">Contact</router-link></li>
        </ul>
      </div>
      
      <div class="footer-section">
        <h3 class="footer-heading">Categories</h3>
        <ul class="footer-links">
          <li><router-link to="/" class="footer-link">Electronics</router-link></li>
          <li><router-link to="/" class="footer-link">Clothing</router-link></li>
          <li><router-link to="/" class="footer-link">Home & Garden</router-link></li>
          <li><router-link to="/" class="footer-link">Fitness</router-link></li>
          <li><router-link to="/" class="footer-link">Accessories</router-link></li>
        </ul>
      </div>
      
      <div class="footer-section">
        <h3 class="footer-heading">Contact Info</h3>
        <ul class="contact-info">
          <li>
            <i class="fas fa-map-marker-alt"></i>
            <span>123 Commerce Street, San Francisco, CA 94107</span>
          </li>
          <li>
            <i class="fas fa-phone"></i>
            <span>+1 (555) 123-4567</span>
          </li>
          <li>
            <i class="fas fa-envelope"></i>
            <span>support@shopvue.com</span>
          </li>
          <li>
            <i class="fas fa-clock"></i>
            <span>Mon-Fri: 9AM-6PM PST</span>
          </li>
        </ul>
      </div>
    </div>
    
    <div class="footer-bottom">
      <div class="footer-bottom-content">
        <p class="copyright">
          &copy; 2024 ShopVue. All rights reserved.
        </p>
        <div class="footer-bottom-links">
          <router-link to="/" class="bottom-link">Privacy Policy</router-link>
          <router-link to="/" class="bottom-link">Terms of Service</router-link>
          <router-link to="/" class="bottom-link">Cookie Policy</router-link>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
// No reactive logic needed for footer
</script>

<style scoped>
.footer {
  background: #1f2937;
  color: #d1d5db;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 3rem 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.footer-brand i {
  font-size: 1.8rem;
  color: #4f46e5;
}

.footer-description {
  line-height: 1.6;
  color: #9ca3af;
}

.social-links {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.social-link {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #374151;
  color: #d1d5db;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  transition: all 0.2s;
}

.social-link:hover {
  background: #4f46e5;
  color: white;
  transform: translateY(-2px);
}

.footer-heading {
  color: white;
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.footer-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.footer-links li {
  margin: 0;
}

.footer-link {
  color: #9ca3af;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-link:hover {
  color: #4f46e5;
}

.contact-info {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.contact-info li {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  color: #9ca3af;
}

.contact-info i {
  color: #4f46e5;
  margin-top: 4px;
  flex-shrink: 0;
}

.footer-bottom {
  border-top: 1px solid #374151;
  padding: 1.5rem 20px;
}

.footer-bottom-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.copyright {
  color: #9ca3af;
  font-size: 0.9rem;
}

.footer-bottom-links {
  display: flex;
  gap: 1.5rem;
}

.bottom-link {
  color: #9ca3af;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.2s;
}

.bottom-link:hover {
  color: #4f46e5;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    padding: 2rem 15px;
  }
  
  .footer-bottom-content {
    flex-direction: column;
    text-align: center;
    gap: 1rem;
  }
  
  .footer-bottom-links {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .footer-content {
    grid-template-columns: 1fr;
    padding: 1.5rem 10px;
  }
  
  .footer-section {
    text-align: center;
  }
  
  .social-links {
    justify-content: center;
  }
  
  .contact-info li {
    text-align: left;
  }
}
</style>