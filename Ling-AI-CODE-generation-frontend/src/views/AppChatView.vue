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
        <!-- 加载更多按钮 -->
        <div v-if="hasMoreHistory" class="load-more-container">
          <a-button
            type="link"
            size="small"
            :loading="loadingHistory"
            @click="loadMoreHistory"
          >
            Load more history
          </a-button>
        </div>

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
          <a-button size="small" @click="updatePreview">↺ Refresh</a-button>
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
import { listAppChatHistory } from '@/api/chatHistoryController'
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
  createTime?: string
}

const messages = ref<Message[]>([])
const inputMessage = ref('')
const isGenerating = ref(false)
const messagesRef = ref<HTMLElement>()
const iframeRef = ref<HTMLIFrameElement>()
const previewUrl = ref('')
const deploying = ref(false)

// 对话历史相关
const loadingHistory = ref(false)
const hasMoreHistory = ref(false)
const lastCreateTime = ref<string | undefined>(undefined)
const historyLoaded = ref(false)

// 加载对话历史
const loadChatHistory = async (isLoadMore = false) => {
  if (!appId || loadingHistory.value) return
  loadingHistory.value = true
  try {
    const params: API.listAppChatHistoryParams = {
      appId: appId as any,
      pageSize: 10,
    }
    // 如果是加载更多，传入游标
    if (isLoadMore && lastCreateTime.value) {
      params.lastCreateTime = lastCreateTime.value
    }
    const res = await listAppChatHistory(params)
    if (res.data.code === 0 && res.data.data) {
      const chatHistories = res.data.data.records ?? []
      if (chatHistories.length > 0) {
        // 将历史记录转换为消息格式，后端返回的是时间降序，需要反转
        const historyMessages: Message[] = chatHistories
          .map((chat) => ({
            role: (chat.messageType === 'user' ? 'user' : 'ai') as 'user' | 'ai',
            content: chat.message ?? '',
            createTime: chat.createTime,
          }))
          .reverse()

        if (isLoadMore) {
          // 加载更多时，把历史消息插入到列表开头
          messages.value.unshift(...historyMessages)
        } else {
          // 初始加载，直接设置
          messages.value = historyMessages
        }

        // 更新游标：使用最旧的一条消息的创建时间
        lastCreateTime.value = chatHistories[chatHistories.length - 1]?.createTime
        // 判断是否还有更多历史
        hasMoreHistory.value = chatHistories.length === 10
      } else {
        hasMoreHistory.value = false
      }
      historyLoaded.value = true
    }
  } catch (error) {
    console.error('Failed to load chat history:', error)
    message.error('Failed to load chat history')
  } finally {
    loadingHistory.value = false
  }
}

// 加载更多历史消息
const loadMoreHistory = async () => {
  await loadChatHistory(true)
}

// Fetch app info
const fetchAppInfo = async () => {
  try {
    const res = await getAppVoById({ id: appId as any })
    if (res.data.code === 0) {
      appInfo.value = res.data.data

      // 先加载对话历史
      await loadChatHistory()

      // 如果有至少 2 条对话记录，展示对应的网站预览
      if (messages.value.length >= 2) {
        updatePreview()
      }

      // 只有在是自己的应用且没有对话历史时才自动发送初始提示词
      const isOwner = appInfo.value?.userId === loginUserStore.loginUser?.id
      if (
        appInfo.value?.initPrompt &&
        isOwner &&
        messages.value.length === 0 &&
        historyLoaded.value
      ) {
        await sendInitialMessage(appInfo.value.initPrompt)
      }
    } else {
      message.error('Failed to load app info')
      router.push('/')
    }
  } catch (e) {
    message.error('Failed to load app info')
    router.push('/')
  }
}

// 发送初始消息
const sendInitialMessage = async (prompt: string) => {
  messages.value.push({ role: 'user', content: prompt })
  const aiMsgIndex = messages.value.length
  messages.value.push({ role: 'ai', content: '', loading: true })
  await nextTick()
  scrollToBottom()
  isGenerating.value = true
  await generateCode(prompt, aiMsgIndex)
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
  await nextTick()
  scrollToBottom()

  await generateCode(userMsg, aiMsgIndex)
}

// 生成代码 - 使用 EventSource 处理流式响应
const generateCode = async (userMessage: string, aiMessageIndex: number) => {
  let eventSource: EventSource | null = null
  let streamCompleted = false

  try {
    const params = new URLSearchParams({
      appId: appId || '',
      message: userMessage,
    })
    const url = `http://localhost:8123/api/app/chat/gen/code?${params}`

    // 创建 EventSource 连接
    eventSource = new EventSource(url, {
      withCredentials: true,
    })

    let fullContent = ''

    // 处理接收到的消息
    eventSource.onmessage = function (event) {
      if (streamCompleted) return
      try {
        const parsed = JSON.parse(event.data)

        // Vue 项目模式：JSON 消息格式
        if (parsed.type) {
          if (parsed.type === 'ai_response') {
            // AI 文本响应
            const content = parsed.data
            if (content !== undefined && content !== null) {
              fullContent += content
              messages.value[aiMessageIndex].content = fullContent
              messages.value[aiMessageIndex].loading = false
              scrollToBottom()
            }
          } else if (parsed.type === 'tool_request') {
            // 工具调用请求：显示"[Selecting Tool] Writing file"
            const toolMsg = `\n\n[Selecting Tool] Writing file: ${parsed.name}\n`
            fullContent += toolMsg
            messages.value[aiMessageIndex].content = fullContent
            messages.value[aiMessageIndex].loading = false
            scrollToBottom()
          } else if (parsed.type === 'tool_executed') {
            // 工具执行完成：显示写入的文件路径
            const args = JSON.parse(parsed.arguments || '{}')
            const toolMsg = `[Tool Executed] ✅ ${args.relativeFilePath}\n`
            fullContent += toolMsg
            messages.value[aiMessageIndex].content = fullContent
            messages.value[aiMessageIndex].loading = false
            scrollToBottom()
          }
        } else {
          // 原生模式：普通文本格式 {"d": "chunk"}
          const content = parsed.d
          if (content !== undefined && content !== null) {
            fullContent += content
            messages.value[aiMessageIndex].content = fullContent
            messages.value[aiMessageIndex].loading = false
            scrollToBottom()
          }
        }
      } catch (error) {
        console.error('解析消息失败:', error)
        handleError(error, aiMessageIndex)
      }
    }

    // 处理done事件
    eventSource.addEventListener('done', function () {
      if (streamCompleted) return

      streamCompleted = true
      isGenerating.value = false
      eventSource?.close()

      // 延迟更新预览，确保后端已完成处理
      setTimeout(async () => {
        await fetchAppInfo()
        updatePreview()
      }, 1000)
    })

    // 处理错误
    eventSource.onerror = function () {
      if (streamCompleted || !isGenerating.value) return
      // 检查是否是正常的连接关闭
      if (eventSource?.readyState === EventSource.CONNECTING) {
        streamCompleted = true
        isGenerating.value = false
        eventSource?.close()

        setTimeout(async () => {
          await fetchAppInfo()
          updatePreview()
        }, 1000)
      } else {
        handleError(new Error('SSE connection error'), aiMessageIndex)
      }
    }
  } catch (error) {
    console.error('创建 EventSource 失败: ', error)
    handleError(error, aiMessageIndex)
  }
}

// 错误处理函数
const handleError = (error: unknown, aiMessageIndex: number) => {
  console.error('生成代码失败: ', error)
  messages.value[aiMessageIndex].content = 'Generation failed, please try again.'
  messages.value[aiMessageIndex].loading = false
  message.error('Generation failed, please try again.')
  isGenerating.value = false
}

// Update preview
const updatePreview = () => {
  if (appId) {
    const codeGenType = appInfo.value?.codeGenType ?? 'multi_file'
    let previewPath = `http://localhost:8123/api/static/${codeGenType}_${appId}/`
    // Vue 项目需要访问 dist 目录
    if (codeGenType === 'vue_project') {
      previewPath = `http://localhost:8123/api/static/${codeGenType}_${appId}/dist/index.html`
    }
    previewUrl.value = previewPath + `?t=${Date.now()}`
  }
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

.load-more-container {
  text-align: center;
  padding: 8px 0;
  margin-bottom: 8px;
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
