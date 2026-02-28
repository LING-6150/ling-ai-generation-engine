<template>
  <nav class="navbar">
    <div class="navbar-container">
      <!-- Logo -->
      <router-link to="/" class="navbar-brand">
        <i class="fas fa-shopping-bag"></i>
        <span class="brand-name">ShopVue</span>
      </router-link>

      <!-- Desktop Navigation -->
      <div class="navbar-menu">
        <router-link to="/" class="nav-link">
          <i class="fas fa-home"></i>
          <span>Home</span>
        </router-link>
        
        <div class="nav-dropdown">
          <button class="nav-link">
            <i class="fas fa-th-large"></i>
            <span>Categories</span>
            <i class="fas fa-chevron-down"></i>
          </button>
          <div class="dropdown-menu">
            <router-link to="/" class="dropdown-item">
              <i class="fas fa-laptop"></i>
              <span>Electronics</span>
            </router-link>
            <router-link to="/" class="dropdown-item">
              <i class="fas fa-tshirt"></i>
              <span>Clothing</span>
            </router-link>
            <router-link to="/" class="dropdown-item">
              <i class="fas fa-home"></i>
              <span>Home & Garden</span>
            </router-link>
            <router-link to="/" class="dropdown-item">
              <i class="fas fa-dumbbell"></i>
              <span>Fitness</span>
            </router-link>
            <router-link to="/" class="dropdown-item">
              <i class="fas fa-gem"></i>
              <span>Accessories</span>
            </router-link>
          </div>
        </div>
        
        <router-link to="/" class="nav-link">
          <i class="fas fa-percent"></i>
          <span>Deals</span>
        </router-link>
        
        <router-link to="/" class="nav-link">
          <i class="fas fa-info-circle"></i>
          <span>About</span>
        </router-link>
      </div>

      <!-- Search Bar -->
      <div class="navbar-search">
        <div class="search-container">
          <i class="fas fa-search"></i>
          <input 
            type="text" 
            placeholder="Search products..." 
            class="search-input"
            v-model="searchQuery"
            @keyup.enter="performSearch"
          >
          <button class="search-btn" @click="performSearch">
            Search
          </button>
        </div>
      </div>

      <!-- User Actions -->
      <div class="navbar-actions">
        <button class="action-btn" @click="toggleTheme">
          <i :class="['fas', themeIcon]"></i>
          <span class="action-label">Theme</span>
        </button>
        
        <router-link to="/" class="action-btn">
          <i class="far fa-heart"></i>
          <span class="action-label">Wishlist</span>
          <span class="badge" v-if="wishlistCount > 0">{{ wishlistCount }}</span>
        </router-link>
        
        <router-link to="/cart" class="action-btn cart-btn">
          <i class="fas fa-shopping-cart"></i>
          <span class="action-label">Cart</span>
          <span class="badge" v-if="cartCount > 0">{{ cartCount }}</span>
        </router-link>
        
        <div class="user-dropdown">
          <button class="action-btn user-btn">
            <i class="fas fa-user"></i>
            <span class="action-label">Account</span>
            <i class="fas fa-chevron-down"></i>
          </button>
          <div class="user-menu">
            <router-link to="/" class="user-menu-item">
              <i class="fas fa-user-circle"></i>
              <span>My Profile</span>
            </router-link>
            <router-link to="/" class="user-menu-item">
              <i class="fas fa-box"></i>
              <span>My Orders</span>
            </router-link>
            <router-link to="/" class="user-menu-item">
              <i class="fas fa-cog"></i>
              <span>Settings</span>
            </router-link>
            <div class="user-menu-divider"></div>
            <button class="user-menu-item logout" @click="logout">
              <i class="fas fa-sign-out-alt"></i>
              <span>Logout</span>
            </button>
          </div>
        </div>
      </div>

      <!-- Mobile Menu Button -->
      <button class="mobile-menu-btn" @click="toggleMobileMenu">
        <i :class="['fas', mobileMenuOpen ? 'fa-times' : 'fa-bars']"></i>
      </button>
    </div>

    <!-- Mobile Menu -->
    <div class="mobile-menu" :class="{ open: mobileMenuOpen }">
      <div class="mobile-search">
        <div class="search-container">
          <i class="fas fa-search"></i>
          <input 
            type="text" 
            placeholder="Search products..." 
            class="search-input"
            v-model="searchQuery"
            @keyup.enter="performSearch"
          >
        </div>
      </div>
      
      <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">
        <i class="fas fa-home"></i>
        <span>Home</span>
      </router-link>
      
      <div class="mobile-nav-section">
        <div class="mobile-nav-header">
          <i class="fas fa-th-large"></i>
          <span>Categories</span>
        </div>
        <div class="mobile-nav-items">
          <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
            Electronics
          </router-link>
          <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
            Clothing
          </router-link>
          <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
            Home & Garden
          </router-link>
          <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
            Fitness
          </router-link>
          <router-link to="/" class="mobile-nav-item" @click="closeMobileMenu">
            Accessories
          </router-link>
        </div>
      </div>
      
      <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">
        <i class="fas fa-percent"></i>
        <span>Deals</span>
      </router-link>
      
      <router-link to="/" class="mobile-nav-link" @click="closeMobileMenu">
        <i class="fas fa-info-circle"></i>
        <span>About</span>
      </router-link>
      
      <div class="mobile-user-actions">
        <router-link to="/" class="mobile-action-btn" @click="closeMobileMenu">
          <i class="far fa-heart"></i>
          <span>Wishlist</span>
          <span class="badge" v-if="wishlistCount > 0">{{ wishlistCount }}</span>
        </router-link>
        
        <router-link to="/cart" class="mobile-action-btn" @click="closeMobileMenu">
          <i class="fas fa-shopping-cart"></i>
          <span>Cart</span>
          <span class="badge" v-if="cartCount > 0">{{ cartCount }}</span>
        </router-link>
        
        <button class="mobile-action-btn" @click="toggleTheme">
          <i :class="['fas', themeIcon]"></i>
          <span>Theme</span>
        </button>
      </div>
      
      <div class="mobile-user-menu">
        <router-link to="/" class="mobile-user-item" @click="closeMobileMenu">
          <i class="fas fa-user-circle"></i>
          <span>My Profile</span>
        </router-link>
        <router-link to="/" class="mobile-user-item" @click="closeMobileMenu">
          <i class="fas fa-box"></i>
          <span>My Orders</span>
        </router-link>
        <router-link to="/" class="mobile-user-item" @click="closeMobileMenu">
          <i class="fas fa-cog"></i>
          <span>Settings</span>
        </router-link>
        <button class="mobile-user-item logout" @click="logout">
          <i class="fas fa-sign-out-alt"></i>
          <span>Logout</span>
        </button>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useCartStore } from '../stores/cart'

const cartStore = useCartStore()
const searchQuery = ref('')
const mobileMenuOpen = ref(false)
const darkTheme = ref(false)

const cartCount = computed(() => cartStore.totalItems)
const wishlistCount = computed(() => 3) // Mock wishlist count

const themeIcon = computed(() => darkTheme.value ? 'fa-sun' : 'fa-moon')

const toggleTheme = () => {
  darkTheme.value = !darkTheme.value
  if (darkTheme.value) {
    document.documentElement.setAttribute('data-theme', 'dark')
  } else {
    document.documentElement.removeAttribute('data-theme')
  }
}

const toggleMobileMenu = () => {
  mobileMenuOpen.value = !mobileMenuOpen.value
}

const closeMobileMenu = () => {
  mobileMenuOpen.value = false
}

const performSearch = () => {
  if (searchQuery.value.trim()) {
    alert(`Searching for: ${searchQuery.value}`)
    searchQuery.value = ''
  }
}

const logout = () => {
  alert('Logged out successfully!')
  closeMobileMenu()
}

// Close mobile menu on escape key
const handleEscape = (event) => {
  if (event.key === 'Escape' && mobileMenuOpen.value) {
    closeMobileMenu()
  }
}

// Close mobile menu when clicking outside
const handleClickOutside = (event) => {
  if (mobileMenuOpen.value && !event.target.closest('.navbar')) {
    closeMobileMenu()
  }
}

onMounted(() => {
  document.addEventListener('keydown', handleEscape)
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('keydown', handleEscape)
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.navbar {
  background: #1a2b4a;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.navbar-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.navbar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  text-decoration: none;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.navbar-brand i {
  color: #60a5fa;
  font-size: 1.8rem;
}

.navbar-menu {
  display: flex;
  gap: 1rem;
  margin-left: 2rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  text-decoration: none;
  color: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  transition: all 0.2s;
  position: relative;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 0.95rem;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.nav-link.router-link-active {
  background: rgba(96, 165, 250, 0.2);
  color: #60a5fa;
}

.nav-dropdown {
  position: relative;
}

.nav-dropdown .nav-link i.fa-chevron-down {
  font-size: 0.8rem;
  margin-left: 4px;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  padding: 0.5rem;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.2s;
  z-index: 1000;
}

.nav-dropdown:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  text-decoration: none;
  color: #374151;
  border-radius: 6px;
  transition: all 0.2s;
}

.dropdown-item:hover {
  background: #f3f4f6;
  color: #111827;
}

.dropdown-item i {
  width: 20px;
  text-align: center;
  color: #6b7280;
}

.navbar-search {
  flex: 1;
  max-width: 400px;
  margin: 0 2rem;
}

.search-container {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  padding: 4px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.2s;
}

.search-container:focus-within {
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
  background: rgba(255, 255, 255, 0.15);
}

.search-container i {
  padding: 0 12px;
  color: rgba(255, 255, 255, 0.7);
}

.search-input {
  flex: 1;
  border: none;
  background: none;
  padding: 8px 0;
  font-size: 0.95rem;
  color: white;
}

.search-input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.search-input:focus {
  outline: none;
}

.search-btn {
  background: #60a5fa;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-btn:hover {
  background: #3b82f6;
}

.navbar-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: none;
  border: none;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
  font-size: 0.95rem;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: white;
}

.action-label {
  display: block;
}

@media (max-width: 1024px) {
  .action-label {
    display: none;
  }
}

.badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #ef4444;
  color: white;
  font-size: 0.75rem;
  font-weight: 600;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
}

.cart-btn .badge {
  background: #60a5fa;
}

.user-dropdown {
  position: relative;
}

.user-btn {
  padding-right: 8px;
}

.user-btn i.fa-chevron-down {
  font-size: 0.8rem;
  margin-left: 4px;
}

.user-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  min-width: 200px;
  padding: 0.5rem;
  opacity: 0;
  visibility: hidden;
  transform: translateY(10px);
  transition: all 0.2s;
  z-index: 1000;
}

.user-dropdown:hover .user-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.user-menu-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  text-decoration: none;
  color: #4b5563;
  border-radius: 6px;
  transition: all 0.2s;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  cursor: pointer;
  font-size: 0.9rem;
}

.user-menu-item:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.user-menu-item.logout {
  color: #ef4444;
}

.user-menu-item.logout:hover {
  background: #fee2e2;
}

.user-menu-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 0.5rem 0;
}

.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  font-size: 1.5rem;
  color: white;
  cursor: pointer;
  padding: 8px;
  border-radius: 8px;
  transition: background-color 0.2s;
}

.mobile-menu-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.mobile-menu {
  display: none;
  position: fixed;
  top: 70px;
  left: 0;
  right: 0;
  bottom: 0;
  background: white;
  padding: 1rem;
  overflow-y: auto;
  transform: translateX(-100%);
  transition: transform 0.3s ease;
  z-index: 999;
}

.mobile-menu.open {
  transform: translateX(0);
}

.mobile-search {
  margin-bottom: 1rem;
}

.mobile-search .search-container {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px 12px;
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  text-decoration: none;
  color: #4b5563;
  border-radius: 8px;
  transition: all 0.2s;
  margin-bottom: 0.5rem;
}

.mobile-nav-link:hover,
.mobile-nav-link.router-link-active {
  background: #f3f4f6;
  color: #1f2937;
}

.mobile-nav-section {
  margin-bottom: 1rem;
}

.mobile-nav-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  color: #4b5563;
  font-weight: 500;
}

.mobile-nav-items {
  padding-left: 2rem;
}

.mobile-nav-item {
  display: block;
  padding: 8px 16px;
  text-decoration: none;
  color: #6b7280;
  border-radius: 6px;
  transition: all 0.2s;
  margin-bottom: 0.25rem;
}

.mobile-nav-item:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.mobile-user-actions {
  display: flex;
  gap: 0.5rem;
  padding: 1rem 0;
  border-top: 1px solid #e5e7eb;
  border-bottom: 1px solid #e5e7eb;
  margin: 1rem 0;
}

.mobile-action-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 12px;
  background: none;
  border: none;
  border-radius: 8px;
  color: #4b5563;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.2s;
  position: relative;
}

.mobile-action-btn:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.mobile-user-menu {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.mobile-user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  text-decoration: none;
  color: #4b5563;
  border-radius: 8px;
  transition: all 0.2s;
  border: none;
  background: none;
  text-align: left;
  cursor: pointer;
}

.mobile-user-item:hover {
  background: #f3f4f6;
  color: #1f2937;
}

.mobile-user-item.logout {
  color: #ef4444;
}

.mobile-user-item.logout:hover {
  background: #fee2e2;
}

@media (max-width: 1024px) {
  .navbar-menu,
  .navbar-search,
  .navbar-actions {
    display: none;
  }
  
  .mobile-menu-btn {
    display: block;
  }
  
  .mobile-menu {
    display: block;
  }
}

@media (max-width: 768px) {
  .navbar-container {
    padding: 0 15px;
    height: 60px;
  }
  
  .navbar-brand {
    font-size: 1.25rem;
  }
  
  .navbar-brand i {
    font-size: 1.5rem;
  }
  
  .mobile-menu {
    top: 60px;
  }
}

@media (max-width: 480px) {
  .navbar-container {
    padding: 0 10px;
  }
  
  .mobile-menu {
    padding: 0.75rem;
  }
  
  .mobile-user-actions {
    flex-direction: column;
  }
}
</style>