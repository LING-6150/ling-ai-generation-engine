<template>
  <div id="app">
    <header class="app-header">
      <nav class="navbar">
        <div class="nav-brand">
          <router-link to="/" class="brand-link">
            <span class="brand-icon">✓</span>
            <span class="brand-text">TodoList</span>
          </router-link>
        </div>
        
        <div class="nav-links">
          <router-link 
            to="/" 
            class="nav-link"
            :class="{ active: $route.path === '/' }"
          >
            <span class="nav-icon">🏠</span>
            <span class="nav-text">Dashboard</span>
          </router-link>
        </div>
        
        <div class="nav-actions">
          <div class="user-info">
            <div class="user-avatar">
              <span class="avatar-text">👤</span>
            </div>
            <div class="user-details">
              <span class="user-name">Welcome</span>
              <span class="user-status">Ready to be productive</span>
            </div>
          </div>
        </div>
      </nav>
    </header>
    
    <main class="app-main">
      <router-view />
    </main>
    
    <footer class="app-footer">
      <div class="footer-content">
        <div class="footer-section">
          <h3 class="footer-title">TodoList App</h3>
          <p class="footer-description">
            A modern task management application built with Vue 3.
          </p>
        </div>
        
        <div class="footer-section">
          <h4 class="footer-subtitle">Features</h4>
          <ul class="footer-links">
            <li>Priority-based tasks</li>
            <li>Real-time statistics</li>
            <li>Local storage</li>
            <li>Responsive design</li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4 class="footer-subtitle">Built With</h4>
          <div class="tech-stack">
            <span class="tech-item">Vue 3</span>
            <span class="tech-item">Vite</span>
            <span class="tech-item">Vue Router</span>
            <span class="tech-item">CSS3</span>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p class="copyright">
          &copy; 2024 TodoList App. All rights reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'

onMounted(() => {
  // Remove loading screen
  const loadingContainer = document.querySelector('.loading-container')
  if (loadingContainer) {
    setTimeout(() => {
      loadingContainer.style.opacity = '0'
      loadingContainer.style.transform = 'translateY(-20px)'
      setTimeout(() => {
        loadingContainer.style.display = 'none'
      }, 300)
    }, 500)
  }
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  color: #333;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar {
  max-width: 1200px;
  margin: 0 auto;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.nav-brand {
  flex-shrink: 0;
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-decoration: none;
  color: #333;
  font-weight: 700;
  font-size: 1.5rem;
  transition: all 0.2s;
}

.brand-link:hover {
  color: #667eea;
  transform: translateY(-1px);
}

.brand-icon {
  font-size: 1.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.brand-text {
  display: none;
}

.nav-links {
  display: flex;
  gap: 1rem;
  flex: 1;
  justify-content: center;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: #666;
  border-radius: 8px;
  transition: all 0.2s;
  font-weight: 500;
}

.nav-link:hover {
  background-color: #f0f0f0;
  color: #333;
}

.nav-link.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.nav-icon {
  font-size: 1.25rem;
}

.nav-text {
  display: none;
}

.nav-actions {
  flex-shrink: 0;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.25rem;
}

.avatar-text {
  font-size: 1.25rem;
}

.user-details {
  display: none;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: #333;
  font-size: 0.9rem;
}

.user-status {
  font-size: 0.8rem;
  color: #666;
}

.app-main {
  flex: 1;
  padding: 2rem 1rem;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.app-footer {
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 2rem 1rem 1rem;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-section {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-title {
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-description {
  color: #ccc;
  line-height: 1.6;
  font-size: 0.9rem;
}

.footer-subtitle {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: white;
}

.footer-links {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.footer-links li {
  color: #ccc;
  font-size: 0.9rem;
  transition: color 0.2s;
  cursor: pointer;
}

.footer-links li:hover {
  color: #667eea;
}

.tech-stack {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tech-item {
  background: rgba(255, 255, 255, 0.1);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  color: #ccc;
  transition: all 0.2s;
}

.tech-item:hover {
  background: rgba(102, 126, 234, 0.2);
  color: white;
}

.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.copyright {
  color: #999;
  font-size: 0.9rem;
}

@media (min-width: 768px) {
  .brand-text {
    display: inline;
  }
  
  .nav-text {
    display: inline;
  }
  
  .user-details {
    display: flex;
  }
  
  .app-main {
    padding: 2rem;
  }
  
  .footer-content {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
  }
  
  .nav-links {
    order: 3;
    width: 100%;
    justify-content: center;
  }
  
  .nav-actions {
    order: 2;
  }
  
  .app-main {
    padding: 1rem;
  }
  
  .footer-content {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 480px) {
  .nav-links {
    flex-direction: column;
    align-items: center;
  }
  
  .nav-link {
    width: 100%;
    justify-content: center;
  }
  
  .app-main {
    padding: 0.5rem;
  }
}
</style>