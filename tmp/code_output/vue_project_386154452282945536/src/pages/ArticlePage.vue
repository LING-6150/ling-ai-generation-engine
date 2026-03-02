<template>
  <div class="article-page">
    <div class="container">
      <div v-if="article" class="article-content">
        <div class="article-header">
          <div class="breadcrumb">
            <router-link to="/">Home</router-link>
            <span class="separator">/</span>
            <span class="current">{{ article.title }}</span>
          </div>
          
          <h1 class="article-title">{{ article.title }}</h1>
          
          <div class="article-meta">
            <div class="meta-left">
              <span class="author">
                <span class="label">By</span>
                {{ article.author }}
              </span>
              <span class="date">
                <span class="label">Published</span>
                {{ formatDate(article.date) }}
              </span>
              <span class="read-time">
                <span class="label">Read time</span>
                {{ article.readTime }}
              </span>
            </div>
            <div class="category-tag" :style="{ backgroundColor: categoryColor }">
              {{ categoryName }}
            </div>
          </div>
        </div>
        
        <div class="article-image">
          <img :src="article.image" :alt="article.title" />
        </div>
        
        <div class="article-body">
          <div class="content" v-html="formatContent(article.content)"></div>
          
          <div class="article-footer">
            <div class="share-section">
              <h4>Share this article</h4>
              <div class="share-buttons">
                <button class="share-btn twitter" @click="shareOnTwitter">
                  Twitter
                </button>
                <button class="share-btn linkedin" @click="shareOnLinkedIn">
                  LinkedIn
                </button>
                <button class="share-btn copy" @click="copyLink">
                  Copy Link
                </button>
              </div>
            </div>
            
            <div class="navigation">
              <router-link v-if="prevArticle" :to="`/article/${prevArticle.id}`" class="nav-link prev">
                <span class="nav-label">Previous</span>
                <span class="nav-title">{{ prevArticle.title }}</span>
              </router-link>
              
              <router-link v-if="nextArticle" :to="`/article/${nextArticle.id}`" class="nav-link next">
                <span class="nav-label">Next</span>
                <span class="nav-title">{{ nextArticle.title }}</span>
              </router-link>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="article-not-found">
        <h2>Article not found</h2>
        <p>The article you're looking for doesn't exist or has been removed.</p>
        <router-link to="/" class="back-home">Back to Home</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleById, getCategoryById, articles } from '@/utils/data'

const route = useRoute()
const articleId = parseInt(route.params.id)

const article = computed(() => getArticleById(articleId))
const category = computed(() => article.value ? getCategoryById(article.value.category) : null)
const categoryName = computed(() => category.value?.name || 'Uncategorized')
const categoryColor = computed(() => category.value?.color || '#666')

const prevArticle = computed(() => {
  if (!article.value) return null
  const currentIndex = articles.findIndex(a => a.id === articleId)
  return currentIndex > 0 ? articles[currentIndex - 1] : null
})

const nextArticle = computed(() => {
  if (!article.value) return null
  const currentIndex = articles.findIndex(a => a.id === articleId)
  return currentIndex < articles.length - 1 ? articles[currentIndex + 1] : null
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
}

const formatContent = (content) => {
  return content
    .split('\n\n')
    .map(paragraph => `<p>${paragraph}</p>`)
    .join('')
}

const shareOnTwitter = () => {
  const text = encodeURIComponent(article.value.title)
  const url = encodeURIComponent(window.location.href)
  window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, '_blank')
}

const shareOnLinkedIn = () => {
  const url = encodeURIComponent(window.location.href)
  window.open(`https://www.linkedin.com/sharing/share-offsite/?url=${url}`, '_blank')
}

const copyLink = () => {
  navigator.clipboard.writeText(window.location.href)
  alert('Link copied to clipboard!')
}

onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
})
</script>

<style scoped>
.article-page {
  min-height: calc(100vh - 200px);
  padding: 2rem 0;
}

.article-content {
  max-width: 800px;
  margin: 0 auto;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  color: #666;
}

.breadcrumb a {
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s;
}

.breadcrumb a:hover {
  color: #764ba2;
}

.separator {
  color: #999;
}

.current {
  color: #333;
  font-weight: 500;
}

.article-title {
  font-size: 2.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  color: #333;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
}

.meta-left {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  align-items: center;
}

.author, .date, .read-time {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.label {
  font-size: 0.8rem;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.author span:last-child,
.date span:last-child,
.read-time span:last-child {
  font-weight: 600;
  color: #333;
}

.category-tag {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.article-image {
  margin-bottom: 2.5rem;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.article-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  display: block;
}

.article-body {
  line-height: 1.8;
  color: #444;
}

.content {
  margin-bottom: 3rem;
}

.content p {
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
}

.article-footer {
  padding-top: 2rem;
  border-top: 1px solid #eee;
}

.share-section {
  margin-bottom: 2rem;
}

.share-section h4 {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
}

.share-buttons {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.share-btn {
  padding: 0.8rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.share-btn.twitter {
  background: #1da1f2;
  color: white;
}

.share-btn.linkedin {
  background: #0077b5;
  color: white;
}

.share-btn.copy {
  background: #f8f9fa;
  color: #333;
  border: 1px solid #ddd;
}

.share-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
}

.navigation {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

.nav-link {
  padding: 1.5rem;
  border-radius: 12px;
  text-decoration: none;
  transition: all 0.3s ease;
  border: 1px solid #eee;
}

.nav-link:hover {
  border-color: #667eea;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
}

.nav-link.prev {
  text-align: left;
}

.nav-link.next {
  text-align: right;
}

.nav-label {
  display: block;
  font-size: 0.9rem;
  color: #888;
  margin-bottom: 0.5rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.nav-title {
  display: block;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
}

.article-not-found {
  text-align: center;
  padding: 4rem 2rem;
}

.article-not-found h2 {
  font-size: 2rem;
  color: #333;
  margin-bottom: 1rem;
}

.article-not-found p {
  color: #666;
  margin-bottom: 2rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.back-home {
  display: inline-block;
  padding: 1rem 2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.back-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(102, 126, 234, 0.3);
}

/* Responsive design */
@media (max-width: 768px) {
  .article-title {
    font-size: 2rem;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .meta-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.8rem;
  }
  
  .article-image img {
    height: 250px;
  }
  
  .content p {
    font-size: 1rem;
  }
  
  .navigation {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .share-buttons {
    flex-direction: column;
  }
  
  .share-btn {
    width: 100%;
  }
}
</style>