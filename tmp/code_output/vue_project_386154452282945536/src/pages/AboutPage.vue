<template>
  <div class="about-page">
    <div class="container">
      <div class="about-content">
        <header class="about-header">
          <h1>About VueBlog</h1>
          <p class="subtitle">Sharing knowledge and insights about modern web development</p>
        </header>
        
        <div class="about-sections">
          <section class="about-section">
            <div class="section-image">
              <img src="https://picsum.photos/600/400?random=7" alt="Our Mission" />
            </div>
            <div class="section-content">
              <h2>Our Mission</h2>
              <p>
                VueBlog was founded with a simple mission: to create a platform where developers, designers, and tech enthusiasts can share knowledge, learn from each other, and stay updated with the latest trends in web development.
              </p>
              <p>
                We believe that technology should be accessible to everyone, and that the best way to learn is through practical, real-world examples and thoughtful discussions.
              </p>
            </div>
          </section>
          
          <section class="about-section reverse">
            <div class="section-content">
              <h2>What We Cover</h2>
              <p>
                Our content spans across various aspects of modern web development, with a special focus on Vue.js and the surrounding ecosystem. We cover topics ranging from beginner tutorials to advanced architectural patterns.
              </p>
              <ul class="topics-list">
                <li>Vue.js Development & Best Practices</li>
                <li>Modern JavaScript & TypeScript</li>
                <li>UI/UX Design Principles</li>
                <li>Performance Optimization</li>
                <li>DevOps & Deployment Strategies</li>
                <li>Career Development in Tech</li>
              </ul>
            </div>
            <div class="section-image">
              <img src="https://picsum.photos/600/400?random=8" alt="What We Cover" />
            </div>
          </section>
          
          <section class="about-section">
            <div class="section-image">
              <img src="https://picsum.photos/600/400?random=9" alt="Our Team" />
            </div>
            <div class="section-content">
              <h2>Our Team</h2>
              <p>
                VueBlog is maintained by a diverse team of experienced developers, designers, and technical writers who are passionate about sharing their knowledge with the community.
              </p>
              <p>
                Each team member brings unique perspectives and expertise, ensuring that our content is comprehensive, accurate, and valuable to readers at all skill levels.
              </p>
            </div>
          </section>
          
          <section class="values-section">
            <h2>Our Values</h2>
            <div class="values-grid">
              <div class="value-card">
                <div class="value-icon">💡</div>
                <h3>Quality Content</h3>
                <p>We prioritize depth, accuracy, and practical value in every article we publish.</p>
              </div>
              <div class="value-card">
                <div class="value-icon">🤝</div>
                <h3>Community Focus</h3>
                <p>We believe in building a supportive and inclusive community for all developers.</p>
              </div>
              <div class="value-card">
                <div class="value-icon">🚀</div>
                <h3>Continuous Learning</h3>
                <p>We're committed to staying current with the latest technologies and best practices.</p>
              </div>
              <div class="value-card">
                <div class="value-icon">🎯</div>
                <h3>Practical Approach</h3>
                <p>We focus on real-world applications and solutions that developers can use immediately.</p>
              </div>
            </div>
          </section>
          
          <section class="cta-section">
            <h2>Join Our Community</h2>
            <p>
              Whether you're just starting your development journey or you're an experienced professional, there's always something new to learn and share.
            </p>
            <div class="cta-buttons">
              <router-link to="/" class="cta-btn primary">Browse Articles</router-link>
              <button class="cta-btn secondary" @click="subscribeNewsletter">Subscribe to Newsletter</button>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const subscribeNewsletter = () => {
  const email = prompt('Enter your email to subscribe to our newsletter:')
  if (email) {
    alert(`Thank you for subscribing with ${email}! You'll receive our next newsletter.`)
  }
}
</script>

<style scoped>
.about-page {
  min-height: calc(100vh - 200px);
  padding: 2rem 0;
}

.about-content {
  max-width: 1000px;
  margin: 0 auto;
}

.about-header {
  text-align: center;
  margin-bottom: 4rem;
}

.about-header h1 {
  font-size: 3rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1.2rem;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

.about-sections {
  display: flex;
  flex-direction: column;
  gap: 4rem;
}

.about-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 3rem;
  align-items: center;
}

.about-section.reverse {
  direction: rtl;
}

.about-section.reverse .section-content {
  direction: ltr;
}

.section-image {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.section-image img {
  width: 100%;
  height: 300px;
  object-fit: cover;
  display: block;
  transition: transform 0.5s ease;
}

.section-image:hover img {
  transform: scale(1.05);
}

.section-content h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 1.5rem;
}

.section-content p {
  font-size: 1.1rem;
  line-height: 1.7;
  color: #555;
  margin-bottom: 1.5rem;
}

.topics-list {
  list-style: none;
  padding: 0;
  margin: 1.5rem 0;
}

.topics-list li {
  padding: 0.8rem 0;
  padding-left: 2rem;
  position: relative;
  color: #555;
  font-size: 1.1rem;
}

.topics-list li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #667eea;
  font-weight: bold;
}

.values-section {
  text-align: center;
  padding: 3rem 0;
}

.values-section h2 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 3rem;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.value-card {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.value-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
}

.value-icon {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.value-card h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 1rem;
}

.value-card p {
  color: #666;
  line-height: 1.6;
}

.cta-section {
  text-align: center;
  padding: 4rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20px;
  color: white;
}

.cta-section h2 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
}

.cta-section p {
  font-size: 1.2rem;
  max-width: 600px;
  margin: 0 auto 2.5rem;
  opacity: 0.9;
  line-height: 1.6;
}

.cta-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.cta-btn {
  padding: 1rem 2rem;
  border-radius: 8px;
  font-weight: 600;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  border: none;
}

.cta-btn.primary {
  background: white;
  color: #667eea;
}

.cta-btn.primary:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(255, 255, 255, 0.2);
}

.cta-btn.secondary {
  background: transparent;
  color: white;
  border: 2px solid white;
}

.cta-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
}

/* Responsive design */
@media (max-width: 768px) {
  .about-header h1 {
    font-size: 2.2rem;
  }
  
  .about-section {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .about-section.reverse {
    direction: ltr;
  }
  
  .section-image img {
    height: 250px;
  }
  
  .section-content h2 {
    font-size: 1.8rem;
  }
  
  .values-section h2 {
    font-size: 2rem;
  }
  
  .values-grid {
    grid-template-columns: 1fr;
  }
  
  .cta-section {
    padding: 3rem 1.5rem;
  }
  
  .cta-section h2 {
    font-size: 2rem;
  }
  
  .cta-buttons {
    flex-direction: column;
  }
  
  .cta-btn {
    width: 100%;
  }
}
</style>