<template>
  <div class="chat-page">
    <!-- Left: Chat Panel -->
    <div class="chat-panel">
      <div class="chat-header">
        <a-button type="text" @click="$router.push('/')">
          ← Back
        </a-button>
        <span class="app-name">{{ appInfo?.appName ?? 'AI Code Generator' }}</span>
        <a-button
          v-if="appInfo?.deployKey"
          type="link"
          :href="`http://localhost/${appInfo.deployKey}`"
          target="_blank"
        >
          View Live →
        </a-button>
      </div>

      <!-- Messages -->
      <div class="messages" ref="messagesRef">
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message', msg.role]"
        >
          <div class="message-avatar">
            <a-avatar v-if="msg.role === 'ai'" style="background-color: #1677ff">AI</a-avatar>
            <a-avatar v-else :src="loginUserStore.loginUser.userAvatar">
              {{ loginUserStore.loginUser.userName?.[0] ?? 'U' }}
            </a-avatar>
          </div>
          <div class="message-content">
            <div class="message-text" v-if="!msg.loading">{{ msg.content }}</div>
            <div class="message-loading" v-else>
              <a-spin size="small" />
              <span>Generating your website...</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Input Area -->
      <div class="input-area">
        <a-textarea
          v-model:value="inputMessage"
          placeholder="Describe what you want to change or improve..."
          :rows="3"
          :disabled="isGenerating"
          @keydown.ctrl.enter="sendMessage"
        />
        <div class="input-actions">
          <span class="hint">Ctrl + Enter to send</span>
          <a-button
            type="primary"
            :loading="isGenerating"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
          >
            {{ isGenerating ? 'Generating...' : 'Send' }}
          </a-button>
        </div>
      </div>
    </div>

    <!-- Right: Preview Panel -->
    <div class="preview-panel">
      <div class="preview-header">
        <span class="preview-title">Live Preview</span>
        <div class="preview-actions">
          <a-button
            size="small"
            :disabled="!appInfo?.id || isGenerating"
            :loading="deploying"
            @click="deployApp"
          >
            🚀 Deploy
          </a-button>
          <a-button size="small" @click="refreshPreview">↺ Refresh</a-button>
        </div>
      </div>

      <div class="preview-content">
        <div v-if="!previewUrl" class="preview-placeholder">
          <div class="placeholder-content">
            <div class="placeholder-icon">🌐</div>
            <p>Your generated website will appear here</p>
            <p class="placeholder-hint">Send a message to start generating</p>
          </div>
        </div>
        <iframe
          v-else
          :src="previewUrl"
          class="preview-iframe"
          ref="iframeRef"
          sandbox="allow-scripts allow-same-origin"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { getAppVoById, deployApp as deployAppApi } from '@/api/appController'
import { message } from 'ant-design-vue'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const appId = route.params.appId as string
const appInfo = ref<API.AppVO>()

// Messages
interface Message {
  role: 'user' | 'ai'
  content: string
  loading?: boolean
}

const messages = ref<Message[]>([
  {
    role: 'ai',
    content: `Hi! I'm your AI assistant. I'll generate a complete website based on your description. What would you like to build today?`,
  },
])

const inputMessage = ref('')
const isGenerating = ref(false)
const messagesRef = ref<HTMLElement>()
const iframeRef = ref<HTMLIFrameElement>()
const previewUrl = ref('')
const deploying = ref(false)

// Fetch app info
const fetchAppInfo = async () => {
  try {
    const res = await getAppVoById({ id: appId as any })
    if (res.data.code === 0) {
      appInfo.value = res.data.data
      // If already has code, show preview
      if (appInfo.value?.codeGenType && appInfo.value?.id) {
        previewUrl.value = `http://localhost:8123/api/static/${appInfo.value.codeGenType}_${appId}/`
      }
      // If has initPrompt, auto-send first message
      if (appInfo.value?.initPrompt && messages.value.length === 1) {
        inputMessage.value = appInfo.value.initPrompt
        await sendMessage()
      }
    }
  } catch (e) {
    message.error('Failed to load app info')
  }
}

// Send message
const sendMessage = async () => {
  if (!inputMessage.value.trim() || isGenerating.value) return

  const userMsg = inputMessage.value.trim()
  inputMessage.value = ''

  // Add user message
  messages.value.push({ role: 'user', content: userMsg })

  // Add AI loading message
  const aiMsgIndex = messages.value.length
  messages.value.push({ role: 'ai', content: '', loading: true })

  isGenerating.value = true
  await scrollToBottom()

  // Build SSE URL
  const params = new URLSearchParams({
    appId: appId,
    message: userMsg,
  })
  const url = `http://localhost:8123/api/app/chat/gen/code?${params}`

  let fullContent = ''
  let eventSource: EventSource | null = null

  try {
    eventSource = new EventSource(url, { withCredentials: true })

    eventSource.onmessage = (event) => {
      try {
        const parsed = JSON.parse(event.data)
        const content = parsed.d
        if (content !== undefined && content !== null) {
          fullContent += content
          messages.value[aiMsgIndex] = {
            role: 'ai',
            content: fullContent,
            loading: false,
          }
          scrollToBottom()
        }
      } catch (e) {
        console.error('Parse error:', e)
      }
    }

    eventSource.addEventListener('done', async () => {
      eventSource?.close()
      isGenerating.value = false
      messages.value[aiMsgIndex] = {
        role: 'ai',
        content: fullContent || 'Website generated successfully!',
        loading: false,
      }
      // Refresh app info and preview after 1s
      setTimeout(async () => {
        await fetchAppInfo()
        updatePreview()
      }, 1000)
    })

    eventSource.onerror = (error) => {
      if (eventSource?.readyState === EventSource.CONNECTING) {
        eventSource.close()
        isGenerating.value = false
        return
      }
      console.error('SSE error:', error)
      eventSource?.close()
      isGenerating.value = false
      messages.value[aiMsgIndex] = {
        role: 'ai',
        content: 'Sorry, generation failed. Please try again.',
        loading: false,
      }
    }
  } catch (e) {
    isGenerating.value = false
    message.error('Failed to connect to AI service')
  }
}

// Update preview
const updatePreview = () => {
  if (appInfo.value?.codeGenType && appId) {
    previewUrl.value = `http://localhost:8123/api/static/${appInfo.value.codeGenType}_${appId}/?t=${Date.now()}`
  }
}

const refreshPreview = () => {
  updatePreview()
}

// Deploy app
const deployApp = async () => {
  if (!appInfo.value?.id) return
  deploying.value = true
  try {
    const res = await deployAppApi({ appId: appInfo.value.id as any })
    if (res.data.code === 0) {
      message.success('Deployed successfully!')
      await fetchAppInfo()
    } else {
      message.error(res.data.message || 'Deploy failed')
    }
  } catch (e) {
    message.error('Deploy failed')
  } finally {
    deploying.value = false
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

onMounted(() => {
  fetchAppInfo()
})
</script>

<style scoped>
.chat-page {
  display: flex;
  height: calc(100vh - 64px);
  gap: 0;
  overflow: hidden;
}

/* Left Panel */
.chat-panel {
  width: 420px;
  min-width: 420px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #f0f0f0;
  background: #fff;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  min-height: 52px;
}

.app-name {
  font-weight: 600;
  font-size: 15px;
  color: #1a1a1a;
  flex: 1;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.message.user {
  flex-direction: row-reverse;
}

.message-content {
  max-width: 280px;
}

.message-text {
  background: #f5f5f5;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
  white-space: pre-wrap;
  max-height: 200px;
  overflow-y: auto;
}

.message.user .message-text {
  background: #1677ff;
  color: #fff;
}

.message-loading {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f5f5f5;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  color: #666;
}

.input-area {
  padding: 16px;
  border-top: 1px solid #f0f0f0;
}

.input-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.hint {
  font-size: 12px;
  color: #999;
}

/* Right Panel */
.preview-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #fafafa;
  overflow: hidden;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
  min-height: 52px;
}

.preview-title {
  font-weight: 600;
  font-size: 15px;
  color: #1a1a1a;
}

.preview-actions {
  display: flex;
  gap: 8px;
}

.preview-content {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.placeholder-content {
  text-align: center;
  color: #999;
}

.placeholder-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.placeholder-content p {
  font-size: 15px;
  margin: 0;
}

.placeholder-hint {
  font-size: 13px !important;
  margin-top: 8px !important;
  color: #bbb !important;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
}
</style>
