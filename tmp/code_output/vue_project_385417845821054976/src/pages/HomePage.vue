<template>
  <div class="home-page">
    <div class="calculator-container">
      <Calculator />
    </div>
    <div class="features">
      <h2>Features</h2>
      <div class="feature-grid">
        <div class="feature">
          <h3>Basic Operations</h3>
          <p>Addition, subtraction, multiplication, and division</p>
        </div>
        <div class="feature">
          <h3>Calculation History</h3>
          <p>View your recent calculations</p>
        </div>
        <div class="feature">
          <h3>Responsive Design</h3>
          <p>Works on desktop, tablet, and mobile</p>
        </div>
        <div class="feature">
          <h3>Clean Interface</h3>
          <p>Modern, intuitive user interface</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import Calculator from '@/components/Calculator.vue'
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.calculator-container {
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
  margin-bottom: 3rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.features {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.features h2 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
  font-size: 1.8rem;
  font-weight: 300;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature {
  text-align: center;
  padding: 1.5rem;
  background: #f8f9fa;
  border-radius: 10px;
  transition: transform 0.3s ease;
}

.feature:hover {
  transform: translateY(-5px);
}

.feature h3 {
  color: #667eea;
  margin-bottom: 0.5rem;
  font-size: 1.2rem;
}

.feature p {
  color: #666;
  font-size: 0.95rem;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .calculator-container {
    margin: 0 1rem 2rem;
  }
  
  .features {
    margin: 0 1rem;
    padding: 1.5rem;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
}
</style>