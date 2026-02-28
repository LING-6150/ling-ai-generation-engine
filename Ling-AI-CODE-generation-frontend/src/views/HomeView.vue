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
          Describe your idea, and our AI will generate a complete, deployable web application for
          you. No coding required.
        </p>
        <div class="input-area">
          <a-textarea
            v-model:value="prompt"
            :rows="3"
            placeholder="Describe your website idea... e.g. A personal portfolio with dark theme"
            class="prompt-input"
            :maxlength="1000"
            show-count
          />
          <div class="mode-hint">
            <span class="hint-icon">🤖</span>
            <span class="hint-text">AI will automatically select the best generation mode for your idea</span>
          </div>
        </div>
        <a-button
          type="primary"
          size="large"
          class="generate-btn"
          :loading="generating"
          @click="handleGenerate"
        >
          Generate Now →
        </a-button>
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

    <!-- Featured Apps Section -->
    <div class="featured-apps">
      <h2 class="section-title">Featured Apps</h2>
      <a-spin :spinning="loading">
        <div v-if="featuredApps.length === 0 && !loading" class="empty-state">
          <a-empty description="No featured apps yet" />
        </div>
        <a-row :gutter="[24, 24]" v-else>
          <a-col :span="8" v-for="app in featuredApps" :key="app.id">
            <a-card
              hoverable
              class="app-card"
              @click="$router.push(`/app/${app.id}`)"
            >
              <template #cover>
                <img
                  :src="app.cover || `https://picsum.photos/400/200?random=${app.id}`"
                  alt="App cover"
                  class="app-cover"
                />
              </template>
              <a-card-meta :title="app.appName">
                <template #description>
                  <div class="app-meta">
                    <a-avatar :src="app.user?.userAvatar" :size="20" />
                    <span class="creator-name">
                      {{ app.user?.userName ?? 'Anonymous' }}
                    </span>
                    <a-tag v-if="app.codeGenType" color="blue" class="type-tag">
                      {{ formatCodeGenType(app.codeGenType) }}
                    </a-tag>
                  </div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </a-row>
      </a-spin>

      <!-- Pagination -->
      <div class="pagination" v-if="total > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="total"
          :page-size="pageSize"
          @change="loadFeaturedApps"
          show-less-items
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listGoodAppVoByPage } from '@/api/appController'
import { message } from 'ant-design-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// Generate
const prompt = ref('')
const generating = ref(false)

const handleGenerate = async () => {
  if (!prompt.value.trim()) {
    message.warning('Please describe your website idea first')
    return
  }
  if (!loginUserStore.loginUser.id) {
    message.warning('Please sign in first')
    router.push('/user/login')
    return
  }
  generating.value = true
  try {
    // No codeGenType passed — backend AI routing will decide automatically
    const res = await addApp({ initPrompt: prompt.value })
    if (res.data.code === 0) {
      const appId = res.data.data
      message.success('App created! AI is selecting the best generation mode...')
      router.push(`/chat/${appId}`)
    } else {
      message.error(res.data.message || 'Failed to create app')
    }
  } catch (e) {
    message.error('Network error, please try again')
  } finally {
    generating.value = false
  }
}

// Format codeGenType for display
const formatCodeGenType = (type: string) => {
  const map: Record<string, string> = {
    html: '⚡ HTML',
    multi_file: '📁 Multi-file',
    vue_project: '🖖 Vue',
  }
  return map[type] ?? type
}

// Featured Apps
const featuredApps = ref<API.AppVO[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 6
const total = ref(0)

const loadFeaturedApps = async (page = 1) => {
  loading.value = true
  try {
    const res = await listGoodAppVoByPage({
      pageNum: page,
      pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0) {
      featuredApps.value = res.data.data?.records ?? []
      total.value = res.data.data?.totalRow ?? 0
    }
  } catch (e) {
    message.error('Failed to load featured apps')
  } finally {
    loading.value = false
  }
}

const features = [
  {
    icon: '🤖',
    title: 'AI Smart Routing',
    desc: 'AI automatically selects the optimal generation mode based on your description — HTML, Multi-file, or Vue Project.',
  },
  {
    icon: '⚡',
    title: 'Instant Generation',
    desc: 'From idea to deployed app in under 60 seconds using AI-powered code generation.',
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

onMounted(() => {
  loadFeaturedApps()
})
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

.input-area {
  max-width: 640px;
  margin: 0 auto 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.prompt-input {
  font-size: 15px;
  border-radius: 8px;
}

.mode-hint {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: #f0f7ff;
  border-radius: 8px;
  border: 1px solid #d0e6ff;
}

.hint-icon {
  font-size: 16px;
}

.hint-text {
  font-size: 13px;
  color: #1677ff;
}

.generate-btn {
  min-width: 160px;
}

.features {
  padding: 0 8px;
  margin-bottom: 60px;
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

.featured-apps {
  padding: 0 8px;
}

.app-card {
  cursor: pointer;
  transition: box-shadow 0.3s;
  border-radius: 12px;
  overflow: hidden;
}

.app-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.app-cover {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.app-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
  flex-wrap: wrap;
}

.creator-name {
  font-size: 13px;
  color: #666;
}

.type-tag {
  font-size: 11px;
  margin: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.empty-state {
  padding: 60px 0;
}
</style>
