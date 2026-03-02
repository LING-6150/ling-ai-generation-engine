<template>
  <div id="app">
    <nav class="navbar">
      <div class="container">
        <div class="nav-brand">
          <router-link to="/" class="logo">
            <span class="logo-text">VueBlog</span>
          </router-link>
        </div>
        
        <div class="nav-menu">
          <router-link to="/" class="nav-link">Home</router-link>
          <router-link to="/about" class="nav-link">About</router-link>
        </div>
        
        <button class="mobile-menu-btn" @click="toggleMobileMenu">
          <span class="menu-icon"></span>
        </button>
      </div>
      
      <div v-if="mobileMenuOpen" class="mobile-menu">
        <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">Home</router-link>
        <router-link to="/about" class="mobile-nav-link" @click="closeMobileMenu">About</router-link>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
    
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3 class="footer-title">VueBlog</h3>
            <p class="footer-description">
              Sharing knowledge and insights about modern web development, Vue.js, and design principles.
            </p>
          </div>
          
          <div class="footer-section">
            <h4 class="footer-subtitle">Quick Links</h4>
            <ul class="footer-links">
              <li><router-link to="/" class="footer-link">Home</router-link></li>
              <li><router-link to="/about" class="footer-link">About</router-link></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h4 class="footer-subtitle">Categories</h4>
            <ul class="footer-links">
              <li><a href="#" class="footer-link">Technology</a></li>
              <li><a href="#" class="footer-link">Design</a></li>
              <li><a href="#" class="footer-link">Business</a></li>
              <li><a href="#" class="footer-link">Lifestyle</a></li>
            </ul>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p class="copyright">&copy; 2023 VueBlog. All rights reserved.</p>
          <p class="built-with">Built with Vue 3 & Vite</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const mobileMenuOpen = ref(false)

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  line-height: 1.6;
  color: #333;
  background-color: #f8f9fa;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
}

/* Navigation */
.navbar {
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
}

.nav-brand .logo {
  text-decoration: none;
  font-size: 1.5rem;
  font-weight: 800;
  color: #333;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-link {
  text-decoration: none;
  color: #555;
  font-weight: 500;
  padding: 0.5rem 0;
  position: relative;
  transition: color 0.3s;
}

.nav-link:hover {
  color: #667eea;
}

.nav-link.router-link-active {
  color: #667eea;
}

.nav-link.router-link-active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 1px;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
}

.menu-icon {
  display: block;
  width: 24px;
  height: 2px;
  background: #333;
  position: relative;
}

.menu-icon::before,
.menu-icon::after {
  content: '';
  position: absolute;
  width: 24px;
  height: 2px;
  background: #333;
  left: 0;
}

.menu-icon::before {
  top: -6px;
}

.menu-icon::after {
  bottom: -6px;
}

.mobile-menu {
  display: none;
  background: white;
  padding: 1rem;
  border-top: 1px solid #eee;
}

.mobile-nav-link {
  display: block;
  padding: 0.8rem 0;
  text-decoration: none;
  color: #555;
  font-weight: 500;
  border-bottom: 1px solid #eee;
}

.mobile-nav-link:last-child {
  border-bottom: none;
}

.mobile-nav-link:hover {
  color: #667eea;
}

.mobile-nav-link.router-link-active {
  color: #667eea;
}

/* Main content */
.main-content {
  flex: 1;
  padding: 2rem 0;
}

/* Footer */
.footer {
  background: #333;
  color: white;
  padding: 3rem 0 2rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-description {
  color: #aaa;
  line-height: 1.6;
}

.footer-subtitle {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #fff;
}

.footer-links {
  list-style: none;
}

.footer-links li {
  margin-bottom: 0.5rem;
}

.footer-link {
  color: #aaa;
  text-decoration: none;
  transition: color 0.3s;
}

.footer-link:hover {
  color: #667eea;
}

.footer-bottom {
  padding-top: 2rem;
  border-top: 1px solid #444;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.copyright {
  color: #aaa;
  font-size: 0.9rem;
}

.built-with {
  color: #aaa;
  font-size: 0.9rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .nav-menu {
    display: none;
  }
  
  .mobile-menu-btn {
    display: block;
  }
  
  .mobile-menu {
    display: block;
  }
  
  .footer-bottom {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 1rem;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
  }
}
</style>