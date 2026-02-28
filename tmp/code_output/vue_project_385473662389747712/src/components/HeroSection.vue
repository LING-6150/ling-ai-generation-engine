<template>
  <section class="hero-section">
    <div class="container">
      <div class="hero-content">
        <div class="hero-text">
          <h1 class="hero-title">Discover Amazing Products</h1>
          <p class="hero-subtitle">
            Shop the latest trends in electronics, fashion, home goods, and more. 
            Quality products at unbeatable prices.
          </p>
          <div class="hero-actions">
            <router-link to="/" class="btn btn-primary btn-lg">Shop Now</router-link>
            <router-link to="/" class="btn btn-secondary btn-lg">Learn More</router-link>
          </div>
        </div>
        <div class="hero-image">
          <img src="https://picsum.photos/600/400?random=hero" alt="Featured Products" />
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero-section {
  background: linear-gradient(135deg, var(--bg-secondary) 0%, white 100%);
  padding: 4rem 0;
  border-bottom: 1px solid var(--border-color);
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.hero-text {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 800;
  color: var(--text-primary);
  line-height: 1.2;
  letter-spacing: -0.025em;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: var(--text-secondary);
  line-height: 1.6;
  max-width: 500px;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.hero-image {
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-lg);
  border: 1px solid var(--border-color);
}

.hero-image img {
  width: 100%;
  height: auto;
  display: block;
  transition: transform 0.3s ease;
}

.hero-image:hover img {
  transform: scale(1.02);
}

@media (max-width: 1024px) {
  .hero-content {
    gap: 3rem;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
}

@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1.125rem;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .hero-actions .btn {
    width: 100%;
  }
}
</style>