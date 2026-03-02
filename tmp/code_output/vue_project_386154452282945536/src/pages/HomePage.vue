<template>
  <div class="home-page">
    <div class="container">
      <header class="page-header">
        <h1>Latest Articles</h1>
        <p class="subtitle">Discover insights, tutorials, and trends in web development and design</p>
      </header>
      
      <div class="content-wrapper">
        <aside class="sidebar">
          <CategoryFilter 
            :selected-category="selectedCategory"
            @category-change="handleCategoryChange"
          />
          
          <div class="featured-articles">
            <h3>Featured Articles</h3>
            <div class="featured-list">
              <div 
                v-for="article in featuredArticles" 
                :key="article.id"
                class="featured-item"
              >
                <router-link :to="`/article/${article.id}`" class="featured-link">
                  {{ article.title }}
                </router-link>
                <span class="featured-date">{{ formatDate(article.date) }}</span>
              </div>
            </div>
          </div>
        </aside>
        
        <main class="articles-grid">
          <div v-if="filteredArticles.length === 0" class="no-articles">
            <h3>No articles found in this category</h3>
            <p>Try selecting a different category or check back later for new content.</p>
          </div>
          
          <div v-else class="articles-container">
            <ArticleCard 
              v-for="article in filteredArticles" 
              :key="article.id"
              :article="article"
            />
          </div>
          
          <div class="pagination">
            <button 
              class="pagination-btn"
              :disabled="currentPage === 1"
              @click="prevPage"
            >
              Previous
            </button>
            <span class="page-info">Page {{ currentPage }} of {{ totalPages }}</span>
            <button 
              class="pagination-btn"
              :disabled="currentPage === totalPages"
              @click="nextPage"
            >
              Next
            </button>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import CategoryFilter from '@/components/CategoryFilter.vue'
import ArticleCard from '@/components/ArticleCard.vue'
import { getArticlesByCategory, articles } from '@/utils/data'

const selectedCategory = ref('')
const currentPage = ref(1)
const articlesPerPage = 4

const filteredArticles = computed(() => {
  const allArticles = getArticlesByCategory(selectedCategory.value)
  const startIndex = (currentPage.value - 1) * articlesPerPage
  const endIndex = startIndex + articlesPerPage
  return allArticles.slice(startIndex, endIndex)
})

const featuredArticles = computed(() => {
  return articles.slice(0, 3)
})

const totalPages = computed(() => {
  const allArticles = getArticlesByCategory(selectedCategory.value)
  return Math.ceil(allArticles.length / articlesPerPage)
})

const handleCategoryChange = (category) => {
  selectedCategory.value = category
  currentPage.value = 1 // Reset to first page when category changes
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { 
    month: 'short', 
    day: 'numeric' 
  })
}

onMounted(() => {
  // Reset to first page on component mount
  currentPage.value = 1
})
</script>

<style scoped>
.home-page {
  min-height: calc(100vh - 200px);
}

.page-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-header h1 {
  font-size: 2.5rem;
  font-weight: 800;
  color: #333;
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.subtitle {
  font-size: 1.1rem;
  color: #666;
  max-width: 600px;
  margin: 0 auto;
}

.content-wrapper {
  display: grid;
  grid-template-columns: 300px 1fr;
  gap: 2rem;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.featured-articles {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.featured-articles h3 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
}

.featured-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.featured-item {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #eee;
}

.featured-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.featured-link {
  color: #333;
  text-decoration: none;
  font-weight: 500;
  line-height: 1.4;
  transition: color 0.3s;
}

.featured-link:hover {
  color: #667eea;
}

.featured-date {
  font-size: 0.85rem;
  color: #888;
}

.articles-grid {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.articles-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.no-articles {
  text-align: center;
  padding: 4rem 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.no-articles h3 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
}

.no-articles p {
  color: #666;
  max-width: 400px;
  margin: 0 auto;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
  margin-top: 2rem;
  padding: 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.pagination-btn {
  padding: 0.8rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(102, 126, 234, 0.3);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-weight: 500;
  color: #555;
}

/* Responsive design */
@media (max-width: 1024px) {
  .content-wrapper {
    grid-template-columns: 250px 1fr;
    gap: 1.5rem;
  }
}

@media (max-width: 768px) {
  .content-wrapper {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 2rem;
  }
  
  .articles-container {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .pagination {
    flex-direction: column;
    gap: 1rem;
  }
  
  .pagination-btn {
    width: 100%;
  }
}
</style>