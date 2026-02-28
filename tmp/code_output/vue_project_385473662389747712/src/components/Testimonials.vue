<template>
  <section class="testimonials">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">What Our Customers Say</h2>
        <p class="section-subtitle">Join thousands of satisfied customers who trust us for quality products</p>
      </div>
      
      <div class="testimonials-grid">
        <div v-for="testimonial in testimonials" :key="testimonial.id" class="testimonial-card">
          <div class="testimonial-content">
            <div class="testimonial-text">"{{ testimonial.text }}"</div>
            <div class="testimonial-rating">
              <span v-for="n in 5" :key="n" class="star filled">★</span>
            </div>
          </div>
          <div class="testimonial-author">
            <div class="author-avatar">
              <img :src="testimonial.avatar" :alt="testimonial.name" />
            </div>
            <div class="author-info">
              <div class="author-name">{{ testimonial.name }}</div>
              <div class="author-role">{{ testimonial.role }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'

const testimonials = ref([
  {
    id: 1,
    name: "Sarah Johnson",
    role: "Tech Enthusiast",
    text: "The wireless headphones exceeded my expectations. The sound quality is incredible and the battery life is amazing. Best purchase I've made this year!",
    avatar: "https://picsum.photos/100/100?random=avatar1"
  },
  {
    id: 2,
    name: "Michael Chen",
    role: "Professional Photographer",
    text: "As a professional photographer, I'm very particular about my gear. The camera kit I purchased is exceptional quality and has improved my workflow significantly.",
    avatar: "https://picsum.photos/100/100?random=avatar2"
  },
  {
    id: 3,
    name: "Emma Rodriguez",
    role: "Fitness Instructor",
    text: "The smart fitness watch has transformed my training routine. The heart rate monitoring is accurate and the GPS tracking is perfect for my outdoor runs.",
    avatar: "https://picsum.photos/100/100?random=avatar3"
  },
  {
    id: 4,
    name: "David Wilson",
    role: "Home Chef",
    text: "The air fryer oven has become an essential part of my kitchen. It's so versatile and makes cooking healthier meals a breeze. Highly recommended!",
    avatar: "https://picsum.photos/100/100?random=avatar4"
  }
])
</script>

<style scoped>
.testimonials {
  padding: 4rem 0;
  background-color: var(--bg-secondary);
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 0.75rem;
}

.section-subtitle {
  font-size: 1.125rem;
  color: var(--text-secondary);
  max-width: 600px;
  margin: 0 auto;
}

.testimonials-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 2rem;
}

.testimonial-card {
  background-color: white;
  border-radius: var(--radius-lg);
  padding: 2rem;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.testimonial-content {
  flex: 1;
}

.testimonial-text {
  font-size: 1.125rem;
  line-height: 1.6;
  color: var(--text-primary);
  margin-bottom: 1rem;
  font-style: italic;
}

.testimonial-rating {
  display: flex;
  gap: 0.125rem;
}

.star {
  color: #fbbf24;
  font-size: 1.25rem;
}

.testimonial-author {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.author-avatar {
  width: 3rem;
  height: 3rem;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid var(--border-color);
}

.author-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.author-name {
  font-weight: 600;
  color: var(--text-primary);
}

.author-role {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

@media (max-width: 768px) {
  .testimonials-grid {
    grid-template-columns: 1fr;
  }
  
  .section-title {
    font-size: 2rem;
  }
}
</style>