<template>
  <div class="my-apps-page">
    <div class="page-header">
      <h1 class="page-title">My Apps</h1>
      <a-button type="primary" @click="$router.push('/')">
        + Create New App
      </a-button>
    </div>

    <a-spin :spinning="loading">
      <div v-if="apps.length === 0 && !loading" class="empty-state">
        <a-empty description="No apps yet">
          <a-button type="primary" @click="$router.push('/')">
            Create your first app
          </a-button>
        </a-empty>
      </div>

      <a-row :gutter="[24, 24]" v-else>
        <a-col :span="8" v-for="app in apps" :key="app.id">
          <a-card hoverable class="app-card">
            <template #cover>
              <img
                :src="app.cover || `https://picsum.photos/400/200?random=${app.id}`"
                alt="App cover"
                class="app-cover"
              />
            </template>
            <template #actions>
              <span @click="goToChat(app)">💬 Chat</span>
              <span @click="goToDetail(app)">👁 Detail</span>
              <span @click="confirmDelete(app)">🗑 Delete</span>
            </template>
            <a-card-meta :title="app.appName">
              <template #description>
                <div class="app-info">
                  <a-tag v-if="app.deployKey" color="green">Deployed</a-tag>
                  <a-tag v-else color="default">Not deployed</a-tag>
                  <span class="create-time">
                    {{ formatDate(app.createTime) }}
                  </span>
                </div>
              </template>
            </a-card-meta>
          </a-card>
        </a-col>
      </a-row>

      <div class="pagination" v-if="total > 0">
        <a-pagination
          v-model:current="currentPage"
          :total="total"
          :page-size="pageSize"
          @change="loadApps"
          show-less-items
        />
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/loginUser'
import { listMyAppVoByPage, deleteApp as deleteAppApi } from '@/api/appController'
import { message, Modal } from 'ant-design-vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const apps = ref<API.AppVO[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 9
const total = ref(0)

const loadApps = async (page = 1) => {
  loading.value = true
  try {
    const res = await listMyAppVoByPage({
      pageNum: page,
      pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0) {
      apps.value = res.data.data?.records ?? []
      total.value = res.data.data?.totalRow ?? 0
    } else {
      message.error(res.data.message || 'Failed to load apps')
    }
  } catch (e) {
    message.error('Failed to load apps')
  } finally {
    loading.value = false
  }
}

const goToChat = (app: API.AppVO) => {
  router.push(`/chat/${app.id}`)
}

const goToDetail = (app: API.AppVO) => {
  router.push(`/app/${app.id}`)
}

const confirmDelete = (app: API.AppVO) => {
  Modal.confirm({
    title: 'Delete App',
    content: `Are you sure you want to delete "${app.appName}"?`,
    okText: 'Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: async () => {
      try {
        const res = await deleteAppApi({ id: app.id as any })
        if (res.data.code === 0) {
          message.success('App deleted successfully')
          loadApps(currentPage.value)
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

onMounted(async () => {
  // 等待获取登录状态
  await loginUserStore.fetchLoginUser()
  if (!loginUserStore.loginUser?.id) {
    router.push('/user/login')
    return
  }
  loadApps()
})
</script>

<style scoped>
.my-apps-page {
  padding: 0 8px;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.app-card {
  border-radius: 12px;
  overflow: hidden;
  transition: box-shadow 0.3s;
}

.app-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.app-cover {
  width: 100%;
  height: 160px;
  object-fit: cover;
}

.app-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 4px;
}

.create-time {
  font-size: 12px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.empty-state {
  padding: 80px 0;
  text-align: center;
}
</style>
