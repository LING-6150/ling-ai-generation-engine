<template>
  <article class="article-card">
    <div class="article-image">
      <img :src="article.image" :alt="article.title" />
      <span class="category-badge" :style="{ backgroundColor: categoryColor }">
        {{ categoryName }}
      </span>
    </div>
    <div class="article-content">
      <h3 class="article-title">
        <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
      </h3>
      <p class="article-excerpt">{{ article.excerpt }}</p>
      <div class="article-meta">
        <span class="author">{{ article.author }}</span>
        <span class="date">{{ formatDate(article.date) }}</span>
        <span class="read-time">{{ article.readTime }} read</span>
      </div>
    </div>
  </article>
</template>

<script setup>
import { computed } from 'vue'
import { getCategoryById } from '@/utils/data'

const props = defineProps({
  article: {
    type: Object,
    required: true
  }
})

const category = computed(() => getCategoryById(props.article.category))
const categoryName = computed(() => category.value?.name || 'Uncategorized')
const categoryColor = computed(() => category.value?.color || '#666')

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'short', 
    day: 'numeric' 
  })
}
</script>

<style scoped>
.article-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.article-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 12px rgba(0, 0, 0, 0.15);
}

.article-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.article-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .article-image img {
  transform: scale(1.05);
}

.category-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 6px 12px;
  border-radius: 20px;
  color: white;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.article-content {
  padding: 1.5rem;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-title {
  font-size: 1.3rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
  line-height: 1.4;
}

.article-title a {
  color: #333;
  text-decoration: none;
  transition: color 0.3s;
}

.article-title a:hover {
  color: #667eea;
}

.article-excerpt {
  color: #666;
  margin-bottom: 1.2rem;
  line-height: 1.6;
  flex: 1;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.9rem;
  color: #888;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.author {
  font-weight: 600;
  color: #555;
}

.date::before {
  content: '•';
  margin-right: 0.5rem;
}

.read-time::before {
  content: '•';
  margin-right: 0.5rem;
}

/* Responsive design */
@media (max-width: 768px) {
  .article-image {
    height: 180px;
  }
  
  .article-content {
    padding: 1.2rem;
  }
  
  .article-title {
    font-size: 1.2rem;
  }
  
  .article-meta {
    flex-wrap: wrap;
    gap: 0.5rem;
  }
}
</style>