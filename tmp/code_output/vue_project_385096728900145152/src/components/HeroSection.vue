<template>
  <section class="hero-section">
    <div class="container">
      <div class="hero-content">
        <div class="hero-text">
          <div class="hero-badge">
            <span class="badge-dot"></span>
            <span class="badge-text">Available for opportunities</span>
          </div>
          
          <h1 class="hero-title">
            Building Digital
            <span class="highlight">Experiences</span>
            That Matter
          </h1>
          
          <p class="hero-description">
            I'm Alex Morgan, a passionate software engineer specializing in modern web technologies. 
            I create scalable, performant applications with clean code and intuitive user experiences.
          </p>
          
          <div class="hero-actions">
            <a href="#contact" class="btn btn-primary" @click.prevent="scrollToContact">
              Get In Touch
            </a>
            <a href="#skills" class="btn btn-secondary" @click.prevent="scrollToSkills">
              View Skills
            </a>
          </div>
          
          <div class="hero-stats">
            <div class="stat">
              <span class="stat-number">5+</span>
              <span class="stat-label">Years Experience</span>
            </div>
            <div class="stat">
              <span class="stat-number">50+</span>
              <span class="stat-label">Projects Completed</span>
            </div>
            <div class="stat">
              <span class="stat-number">100%</span>
              <span class="stat-label">Client Satisfaction</span>
            </div>
          </div>
        </div>
        
        <div class="hero-image">
          <div class="image-container">
            <img src="https://picsum.photos/400/400?random=1" alt="Alex Morgan" class="profile-image" />
            <div class="image-glow"></div>
          </div>
        </div>
      </div>
      
      <div class="tech-stack">
        <div class="tech-label">Tech Stack</div>
        <div class="tech-icons">
          <div class="tech-icon" v-for="tech in techStack" :key="tech.name">
            <div class="tech-icon-inner">
              <span class="tech-text">{{ tech.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const techStack = ref([
  { name: 'Vue.js' },
  { name: 'React' },
  { name: 'TypeScript' },
  { name: 'Node.js' },
  { name: 'Python' },
  { name: 'AWS' },
  { name: 'Docker' },
  { name: 'GraphQL' }
])

const scrollToContact = () => {
  const element = document.getElementById('contact')
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

const scrollToSkills = () => {
  const element = document.getElementById('skills')
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
.hero-section {
  padding: 4rem 0;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 60%;
  height: 200%;
  background: radial-gradient(circle, var(--accent-glow) 0%, transparent 70%);
  z-index: -1;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
  margin-bottom: 4rem;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  background: var(--bg-tertiary);
  padding: 0.5rem 1rem;
  border-radius: 20px;
  margin-bottom: 2rem;
}

.badge-dot {
  width: 8px;
  height: 8px;
  background: var(--success);
  border-radius: 50%;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.badge-text {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.hero-title {
  font-size: 3.5rem;
  line-height: 1.1;
  margin-bottom: 1.5rem;
}

.highlight {
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 2rem;
  max-width: 600px;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 3rem;
}

.hero-stats {
  display: flex;
  gap: 3rem;
}

.stat {
  display: flex;
  flex-direction: column;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  background: linear-gradient(90deg, var(--accent-primary), var(--accent-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 0.875rem;
  color: var(--text-tertiary);
}

.hero-image {
  display: flex;
  justify-content: center;
}

.image-container {
  position: relative;
  width: 400px;
  height: 400px;
}

.profile-image {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  object-fit: cover;
  border: 2px solid var(--border-color);
  position: relative;
  z-index: 1;
}

.image-glow {
  position: absolute;
  top: 20px;
  left: 20px;
  right: -20px;
  bottom: -20px;
  background: linear-gradient(135deg, var(--accent-primary), var(--accent-secondary));
  border-radius: 20px;
  opacity: 0.2;
  filter: blur(20px);
  z-index: 0;
}

.tech-stack {
  display: flex;
  align-items: center;
  gap: 2rem;
  padding: 2rem;
  background: var(--bg-secondary);
  border-radius: 12px;
  border: 1px solid var(--border-color);
}

.tech-label {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-tertiary);
  white-space: nowrap;
}

.tech-icons {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  flex: 1;
}

.tech-icon {
  flex: 1;
  min-width: 100px;
}

.tech-icon-inner {
  background: var(--bg-tertiary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 0.75rem 1rem;
  text-align: center;
  transition: all 0.3s ease;
}

.tech-icon-inner:hover {
  border-color: var(--accent-primary);
  transform: translateY(-2px);
}

.tech-text {
  font-family: 'JetBrains Mono', monospace;
  font-size: 0.875rem;
  color: var(--text-primary);
}

@media (max-width: 1024px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 3rem;
  }
  
  .hero-image {
    order: -1;
  }
  
  .image-container {
    width: 300px;
    height: 300px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .tech-stack {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .tech-icons {
    width: 100%;
  }
  
  .tech-icon {
    min-width: calc(50% - 0.5rem);
  }
}
</style>