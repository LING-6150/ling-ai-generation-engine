<template>
  <div class="app-detail-page">
    <a-spin :spinning="loading">
      <div v-if="appInfo">
        <!-- Header -->
        <div class="detail-header">
          <a-button type="text" @click="$router.back()">← Back</a-button>
          <div class="header-actions">
            <a-button
              v-if="isOwner"
              @click="$router.push(`/app/edit/${appInfo.id}`)"
            >
              ✏️ Edit
            </a-button>
            <a-button
              v-if="isOwner"
              danger
              @click="confirmDelete"
            >
              🗑 Delete
            </a-button>
            <a-button
              type="primary"
              @click="$router.push(`/chat/${appInfo.id}`)"
            >
              💬 Open Chat
            </a-button>
            <a-button
              v-if="appInfo.deployKey"
              type="link"
              :href="`http://localhost/${appInfo.deployKey}`"
              target="_blank"
            >
              🌐 View Live
            </a-button>
          </div>
        </div>

        <!-- Cover -->
        <div class="cover-area">
          <img
            :src="appInfo.cover || `https://picsum.photos/1200/400?random=${appInfo.id}`"
            alt="App cover"
            class="cover-img"
          />
        </div>

        <!-- Info -->
        <div class="info-area">
          <div class="info-left">
            <h1 class="app-name">{{ appInfo.appName }}</h1>
            <p class="init-prompt">{{ appInfo.initPrompt }}</p>
            <div class="tags">
              <a-tag color="blue">{{ appInfo.codeGenType }}</a-tag>
              <a-tag v-if="appInfo.deployKey" color="green">Deployed</a-tag>
              <a-tag v-else color="default">Not deployed</a-tag>
            </div>
          </div>

          <div class="info-right">
            <div class="meta-item">
              <span class="meta-label">Creator</span>
              <div class="creator">
                <a-avatar :src="appInfo.user?.userAvatar" :size="24" />
                <span>{{ appInfo.user?.userName ?? 'Anonymous' }}</span>
              </div>
            </div>
            <div class="meta-item">
              <span class="meta-label">Created</span>
              <span>{{ formatDate(appInfo.createTime) }}</span>
            </div>
            <div class="meta-item" v-if="appInfo.deployedTime">
              <span class="meta-label">Deployed</span>
              <span>{{ formatDate(appInfo.deployedTime) }}</span>
            </div>
            <div class="meta-item" v-if="appInfo.deployKey">
              <span class="meta-label">Deploy URL</span>
              <a :href="`http://localhost/${appInfo.deployKey}`" target="_blank">localhost/{{ appInfo.deployKey }}</a>
            </div>
          </div>
        </div>

        <!-- Preview -->
        <div class="preview-area" v-if="previewUrl">
          <h2 class="section-title">Preview</h2>
          <iframe
            :src="previewUrl"
            class="preview-iframe"
            sandbox="allow-scripts allow-same-origin"
          />
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppVoById, deleteApp as deleteAppApi } from '@/api/appController'
import { message, Modal } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appId = route.params.appId as string
const appInfo = ref<API.AppVO>()
const loading = ref(false)

const isOwner = computed(() => {
  return loginUserStore.loginUser?.id === appInfo.value?.userId ||
    loginUserStore.loginUser?.userRole === 'admin'
})
//重新修改 改成vue逻辑的
const previewUrl = computed(() => {
  if (!appInfo.value?.codeGenType || !appId) return ''
  const codeGenType = appInfo.value.codeGenType
  if (codeGenType === 'vue_project') {
    return `http://localhost:8123/api/static/${codeGenType}_${appId}/dist/index.html`
  }
  return `http://localhost:8123/api/static/${codeGenType}_${appId}/`
})

const fetchAppInfo = async () => {
  loading.value = true
  try {
    const res = await getAppVoById({ id: appId as any })
    if (res.data.code === 0) {
      appInfo.value = res.data.data
    }
  } catch (e) {
    message.error('Failed to load app info')
  } finally {
    loading.value = false
  }
}

const confirmDelete = () => {
  Modal.confirm({
    title: 'Delete App',
    content: `Are you sure you want to delete "${appInfo.value?.appName}"?`,
    okText: 'Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: async () => {
      try {
        const res = await deleteAppApi({ id: appInfo.value?.id as any })
        if (res.data.code === 0) {
          message.success('App deleted')
          router.push('/my/apps')
        } else {
          message.error(res.data.message || 'Delete failed')
        }
      } catch (e) {
        message.error('Delete failed')
      }
    },
  })
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
  })
}

onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
.app-detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 8px 60px;
}

.detail-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.cover-area {
  margin-bottom: 32px;
  border-radius: 12px;
  overflow: hidden;
}

.cover-img {
  width: 100%;
  height: 300px;
  object-fit: cover;
}

.info-area {
  display: flex;
  gap: 48px;
  margin-bottom: 40px;
}

.info-left {
  flex: 1;
}

.app-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 12px;
}

.init-prompt {
  font-size: 15px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 16px;
}

.tags {
  display: flex;
  gap: 8px;
}

.info-right {
  width: 240px;
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.meta-label {
  font-size: 12px;
  color: #999;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.creator {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #1a1a1a;
}

.preview-area {
  margin-top: 40px;
}

.preview-iframe {
  width: 100%;
  height: 500px;
  border: 1px solid #f0f0f0;
  border-radius: 12px;
}
</style>
