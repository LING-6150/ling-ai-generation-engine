<template>
  <div id="app">
    <!-- Navigation -->
    <nav class="navbar">
      <div class="container">
        <div class="navbar-brand">
          <router-link to="/" class="logo">
            <i class="fas fa-laptop-code"></i>
            <span>TechShop</span>
          </router-link>
        </div>
        
        <div class="navbar-menu">
          <router-link to="/" class="nav-link" :class="{ active: $route.path === '/' }">
            <i class="fas fa-home"></i>
            Home
          </router-link>
          <router-link to="/products" class="nav-link" :class="{ active: $route.path === '/products' }">
            <i class="fas fa-shopping-bag"></i>
            Products
          </router-link>
          <router-link to="/cart" class="nav-link cart-link" :class="{ active: $route.path === '/cart' }">
            <i class="fas fa-shopping-cart"></i>
            Cart
            <span v-if="cartStore.totalItems > 0" class="cart-badge">
              {{ cartStore.totalItems }}
            </span>
          </router-link>
        </div>
      </div>
    </nav>

    <!-- Main Content -->
    <main class="main-content">
      <router-view />
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>TechShop</h3>
            <p>Premium electronics for modern living. Quality products at competitive prices.</p>
            <div class="social-links">
              <a href="#" class="social-link"><i class="fab fa-facebook"></i></a>
              <a href="#" class="social-link"><i class="fab fa-twitter"></i></a>
              <a href="#" class="social-link"><i class="fab fa-instagram"></i></a>
              <a href="#" class="social-link"><i class="fab fa-linkedin"></i></a>
            </div>
          </div>
          
          <div class="footer-section">
            <h4>Quick Links</h4>
            <ul>
              <li><router-link to="/">Home</router-link></li>
              <li><router-link to="/products">Products</router-link></li>
              <li><router-link to="/cart">Cart</router-link></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4>Support</h4>
            <ul>
              <li><a href="#">Contact Us</a></li>
              <li><a href="#">FAQ</a></li>
              <li><a href="#">Shipping Policy</a></li>
              <li><a href="#">Returns & Exchanges</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4>Contact Info</h4>
            <ul>
              <li><i class="fas fa-map-marker-alt"></i> 123 Tech Street, Silicon Valley</li>
              <li><i class="fas fa-phone"></i> (555) 123-4567</li>
              <li><i class="fas fa-envelope"></i> info@techshop.com</li>
            </ul>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p>&copy; 2024 TechShop. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { useCartStore } from './stores/cart'

const cartStore = useCartStore()
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Navigation */
.navbar {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
}

.navbar-brand .logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  font-size: 1.5rem;
  font-weight: 700;
  color: #1a237e;
  text-decoration: none;
}

.navbar-brand .logo i {
  font-size: 1.75rem;
}

.navbar-menu {
  display: flex;
  gap: 2rem;
  align-items: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #666;
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  color: #1a237e;
  background-color: #f5f5f5;
}

.nav-link.active {
  color: #1a237e;
  background-color: #e8eaf6;
}

.cart-link {
  position: relative;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #ff5252;
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  min-width: 20px;
  height: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 0.25rem;
}

/* Main Content */
.main-content {
  flex: 1;
}

/* Footer */
.footer {
  background: #1a237e;
  color: white;
  padding: 3rem 0 1.5rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 3rem;
  margin-bottom: 2rem;
}

.footer-section h3,
.footer-section h4 {
  color: white;
  margin-bottom: 1rem;
  font-size: 1.25rem;
}

.footer-section h3 {
  font-size: 1.5rem;
}

.footer-section p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.footer-section ul {
  list-style: none;
}

.footer-section ul li {
  margin-bottom: 0.75rem;
}

.footer-section ul li a,
.footer-section ul li {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  transition: color 0.3s;
}

.footer-section ul li a:hover {
  color: white;
}

.social-links {
  display: flex;
  gap: 1rem;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  color: white;
  text-decoration: none;
  transition: all 0.3s;
}

.social-link:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.footer-bottom {
  text-align: center;
  padding-top: 1.5rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

/* Responsive Design */
@media (max-width: 1024px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .navbar .container {
    padding: 1rem;
  }
  
  .navbar-menu {
    gap: 1rem;
  }
  
  .nav-link {
    padding: 0.5rem;
  }
  
  .nav-link span {
    display: none;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}

@media (max-width: 480px) {
  .navbar .container {
    flex-direction: column;
    gap: 1rem;
  }
  
  .navbar-menu {
    width: 100%;
    justify-content: space-between;
  }
}
</style>