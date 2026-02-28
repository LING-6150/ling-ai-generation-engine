<template>
  <nav class="navigation">
    <div class="container">
      <div class="nav-content">
        <div class="logo">
          <a href="#home" @click.prevent="scrollToSection('home')">
            <span class="logo-text">AM</span>
            <span class="logo-name">Alex Morgan</span>
          </a>
        </div>
        
        <div class="nav-links">
          <a href="#home" @click.prevent="scrollToSection('home')" :class="{ active: activeSection === 'home' }">Home</a>
          <a href="#skills" @click.prevent="scrollToSection('skills')" :class="{ active: activeSection === 'skills' }">Skills</a>
          <a href="#contact" @click.prevent="scrollToSection('contact')" :class="{ active: activeSection === 'contact' }">Contact</a>
        </div>
        
        <button class="mobile-menu-btn" @click="toggleMobileMenu">
          <span></span>
          <span></span>
          <span></span>
        </button>
      </div>
    </div>
    
    <div class="mobile-menu" :class="{ active: mobileMenuOpen }">
      <a href="#home" @click.prevent="scrollToSection('home')" :class="{ active: activeSection === 'home' }">Home</a>
      <a href="#skills" @click.prevent="scrollToSection('skills')" :class="{ active: activeSection === 'skills' }">Skills</a>
      <a href="#contact" @click.prevent="scrollToSection('contact')" :class="{ active: activeSection === 'contact' }">Contact</a>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'

const activeSection = ref('home')
const mobileMenuOpen = ref(false)

const sections = ['home', 'skills', 'contact']

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
    
    activeSection.value = sectionId
    mobileMenuOpen.value = false
  }
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const handleScroll = () => {
  const scrollPosition = window.scrollY + 100
  
  for (const section of sections) {
    const element = document.getElementById(section)
    if (element) {
      const offsetTop = element.offsetTop
      const offsetHeight = element.offsetHeight
      
      if (scrollPosition >= offsetTop && scrollPosition < offsetTop + offsetHeight) {
        activeSection.value = section
        break
      }
    }
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.navigation {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(10, 10, 15, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color);
  z-index: 1000;
  padding: 1rem 0;
}

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo a {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.logo-text {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary));
  color: white;
  font-weight: 700;
  font-size: 1.25rem;
  border-radius: 8px;
}

.logo-name {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-links a {
  color: var(--text-secondary);
  font-weight: 500;
  position: relative;
  padding: 0.5rem 0;
}

.nav-links a:hover,
.nav-links a.active {
  color: var(--text-primary);
}

.nav-links a.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
  border-radius: 1px;
}

.mobile-menu-btn {
  display: none;
  flex-direction: column;
  gap: 4px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
}

.mobile-menu-btn span {
  width: 24px;
  height: 2px;
  background: var(--text-primary);
  transition: all 0.3s ease;
}

.mobile-menu {
  display: none;
  flex-direction: column;
  background: var(--bg-secondary);
  border-top: 1px solid var(--border-color);
  padding: 1rem;
}

.mobile-menu a {
  padding: 1rem;
  color: var(--text-secondary);
  font-weight: 500;
  border-bottom: 1px solid var(--border-color);
}

.mobile-menu a:last-child {
  border-bottom: none;
}

.mobile-menu a:hover,
.mobile-menu a.active {
  color: var(--text-primary);
  background: var(--bg-tertiary);
}

@media (max-width: 768px) {
  .nav-links {
    display: none;
  }
  
  .mobile-menu-btn {
    display: flex;
  }
  
  .mobile-menu.active {
    display: flex;
  }
}
</style>