<template>
  <div class="home-page">
    <!-- Hero Section -->
    <div class="hero">
      <div class="hero-content">
        <a-tag color="blue" class="hero-tag">✨ Powered by LangChain4j + Spring Boot 3</a-tag>
        <h1 class="hero-title">
          Generate Production-Ready<br />
          <span class="gradient-text">AI Applications</span> in Seconds
        </h1>
        <p class="hero-desc">
          Describe your idea, and our AI will generate a complete, deployable web application for you.
          No coding required.
        </p>
        <div class="hero-actions">
          <a-button type="primary" size="large" @click="$router.push('/user/login')">
            Get Started Free →
          </a-button>
          <a-button size="large" @click="$router.push('/explore')">
            Explore Apps
          </a-button>
        </div>
      </div>
    </div>

    <!-- Features Section -->
    <div class="features">
      <h2 class="section-title">Why Choose AI Code Generator?</h2>
      <div class="feature-cards">
        <div class="feature-card" v-for="feature in features" :key="feature.title">
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.desc }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const features = [
  {
    icon: '⚡',
    title: 'Instant Generation',
    desc: 'From idea to deployed app in under 60 seconds using AI-powered code generation.',
  },
  {
    icon: '🎨',
    title: 'Visual Editor',
    desc: 'Fine-tune your generated app with our visual editor. No coding knowledge needed.',
  },
  {
    icon: '🚀',
    title: 'One-Click Deploy',
    desc: 'Deploy your application to the cloud instantly and share it with the world.',
  },
  {
    icon: '🔒',
    title: 'Enterprise Ready',
    desc: 'Built with Spring Boot 3 microservices architecture for production-grade reliability.',
  },
]
</script>

<style scoped>
.home-page {
  padding-bottom: 60px;
}

.hero {
  text-align: center;
  padding: 80px 24px 60px;
  background: linear-gradient(135deg, #f0f4ff 0%, #fafafa 100%);
  border-radius: 16px;
  margin-bottom: 60px;
}

.hero-tag {
  margin-bottom: 20px;
  font-size: 13px;
  padding: 4px 12px;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  line-height: 1.2;
  color: #1a1a1a;
  margin: 16px 0;
}

.gradient-text {
  background: linear-gradient(90deg, #1677ff, #722ed1);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.hero-desc {
  font-size: 18px;
  color: #666;
  max-width: 560px;
  margin: 0 auto 32px;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.features {
  padding: 0 8px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 40px;
  color: #1a1a1a;
}

.feature-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
  padding: 28px 24px;
  transition: box-shadow 0.3s;
}

.feature-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
}

.feature-icon {
  font-size: 32px;
  margin-bottom: 12px;
}

.feature-card h3 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #1a1a1a;
}

.feature-card p {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}
</style>
