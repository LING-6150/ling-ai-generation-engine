<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-info">
          <div class="footer-logo">
            <span class="logo-text">AM</span>
            <span class="logo-name">Alex Morgan</span>
          </div>
          <p class="footer-tagline">Building the future, one line of code at a time.</p>
        </div>
        
        <div class="footer-links">
          <div class="link-group">
            <h4>Navigation</h4>
            <a href="#home" @click.prevent="scrollToSection('home')">Home</a>
            <a href="#skills" @click.prevent="scrollToSection('skills')">Skills</a>
            <a href="#contact" @click.prevent="scrollToSection('contact')">Contact</a>
          </div>
          
          <div class="link-group">
            <h4>Connect</h4>
            <a href="https://github.com" target="_blank" rel="noopener">GitHub</a>
            <a href="https://linkedin.com" target="_blank" rel="noopener">LinkedIn</a>
            <a href="https://twitter.com" target="_blank" rel="noopener">Twitter</a>
          </div>
        </div>
      </div>
      
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} Alex Morgan. All rights reserved.</p>
        <p>Built with Vue 3 & Vite</p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref } from 'vue'

const currentYear = ref(new Date().getFullYear())

const scrollToSection = (sectionId) => {
  const element = document.getElementById(sectionId)
  if (element) {
    const offset = 80
    const elementPosition = element.getBoundingClientRect().top
    const offsetPosition = elementPosition + window.pageYOffset - offset
    
    window.scrollTo({
      top: offsetPosition,
      behavior: 'smooth'
    })
  }
}
</script>

<style scoped>
.footer {
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding: 3rem 0 2rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 1fr 2fr;
  gap: 3rem;
  margin-bottom: 3rem;
}

.footer-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.footer-logo {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 0.5rem;
}

.footer-logo .logo-text {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary));
  color: white;
  font-weight: 700;
  font-size: 1rem;
  border-radius: 6px;
}

.footer-logo .logo-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-primary);
}

.footer-tagline {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
}

.link-group h4 {
  color: var(--text-primary);
  margin-bottom: 1rem;
  font-size: 1rem;
}

.link-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.link-group a {
  color: var(--text-secondary);
  font-size: 0.875rem;
  transition: color 0.3s ease;
}

.link-group a:hover {
  color: var(--accent-primary);
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 2rem;
  border-top: 1px solid var(--border-color);
}

.footer-bottom p {
  color: var(--text-tertiary);
  font-size: 0.875rem;
  margin: 0;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .footer-links {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .footer-bottom {
    flex-direction: column;
    gap: 1rem;
    text-align: center;
  }
}
</style>