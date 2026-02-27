<template>
  <div class="app-manage-page">
    <div class="page-header">
      <h1 class="page-title">App Management</h1>
    </div>

    <!-- Search -->
    <div class="search-area">
      <a-input
        v-model:value="searchName"
        placeholder="Search by app name..."
        style="width: 240px"
        @pressEnter="loadApps(1)"
      />
      <a-button type="primary" @click="loadApps(1)">Search</a-button>
      <a-button @click="resetSearch">Reset</a-button>
    </div>

    <!-- Table -->
    <a-table
      :dataSource="apps"
      :columns="columns"
      :loading="loading"
      :pagination="{
        current: currentPage,
        pageSize: pageSize,
        total: total,
        onChange: loadApps,
      }"
      row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'appName'">
          <a @click="$router.push(`/app/${record.id}`)">{{ record.appName }}</a>
        </template>
        <template v-if="column.key === 'cover'">
          <img
            :src="record.cover || `https://picsum.photos/60/40?random=${record.id}`"
            style="width: 60px; height: 40px; object-fit: cover; border-radius: 4px"
          />
        </template>
        <template v-if="column.key === 'priority'">
          <a-tag :color="record.priority === 99 ? 'gold' : 'default'">
            {{ record.priority === 99 ? '⭐ Featured' : record.priority }}
          </a-tag>
        </template>
        <template v-if="column.key === 'deployKey'">
          <a-tag v-if="record.deployKey" color="green">{{ record.deployKey }}</a-tag>
          <a-tag v-else color="default">Not deployed</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-button size="small" @click="openEditModal(record)">Edit</a-button>
            <a-button size="small" danger @click="confirmDelete(record)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- Edit Modal -->
    <a-modal
      v-model:open="editModalVisible"
      title="Edit App"
      @ok="handleEdit"
      :confirm-loading="editLoading"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="App Name">
          <a-input v-model:value="editForm.appName" />
        </a-form-item>
        <a-form-item label="Cover URL">
          <a-input v-model:value="editForm.cover" placeholder="https://..." />
        </a-form-item>
        <a-form-item label="Priority">
          <a-input-number
            v-model:value="editForm.priority"
            :min="0"
            :max="999"
            style="width: 100%"
          />
          <div style="color: #999; font-size: 12px; margin-top: 4px">
            Set to 99 to feature on homepage
          </div>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  listAppVoByPageAdmin,
  deleteAppByAdmin,
  updateAppByAdmin,
} from '@/api/appController'
import { message, Modal } from 'ant-design-vue'

const router = useRouter()

const apps = ref<API.AppVO[]>([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10
const total = ref(0)
const searchName = ref('')

const columns = [
  { title: 'Cover', key: 'cover' },
  { title: 'App Name', key: 'appName', dataIndex: 'appName' },
  { title: 'Code Type', dataIndex: 'codeGenType', key: 'codeGenType' },
  { title: 'Priority', key: 'priority' },
  { title: 'Deploy Key', key: 'deployKey' },
  { title: 'Created', dataIndex: 'createTime', key: 'createTime' },
  { title: 'Action', key: 'action' },
]

const loadApps = async (page = 1) => {
  loading.value = true
  currentPage.value = page
  try {
    const res = await listAppVoByPageAdmin({
      pageNum: page,
      pageSize,
      appName: searchName.value || undefined,
      sortField: 'createTime',
      sortOrder: 'desc',
    })
    if (res.data.code === 0) {
      apps.value = res.data.data?.records ?? []
      total.value = res.data.data?.totalRow ?? 0
    }
  } catch (e) {
    message.error('Failed to load apps')
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchName.value = ''
  loadApps(1)
}

// Edit
const editModalVisible = ref(false)
const editLoading = ref(false)
const editForm = ref({
  id: undefined as any,
  appName: '',
  cover: '',
  priority: 0,
})

const openEditModal = (record: API.AppVO) => {
  editForm.value = {
    id: record.id as any,
    appName: record.appName ?? '',
    cover: record.cover ?? '',
    priority: record.priority ?? 0,
  }
  editModalVisible.value = true
}

const handleEdit = async () => {
  editLoading.value = true
  try {
    const res = await updateAppByAdmin({
      id: editForm.value.id,
      appName: editForm.value.appName,
      cover: editForm.value.cover,
      priority: editForm.value.priority,
    })
    if (res.data.code === 0) {
      message.success('Updated successfully')
      editModalVisible.value = false
      loadApps(currentPage.value)
    } else {
      message.error(res.data.message || 'Update failed')
    }
  } catch (e) {
    message.error('Update failed')
  } finally {
    editLoading.value = false
  }
}

// Delete
const confirmDelete = (record: API.AppVO) => {
  Modal.confirm({
    title: 'Delete App',
    content: `Are you sure you want to delete "${record.appName}"?`,
    okText: 'Delete',
    okType: 'danger',
    cancelText: 'Cancel',
    onOk: async () => {
      try {
        const res = await deleteAppByAdmin({ id: record.id as any })
        if (res.data.code === 0) {
          message.success('Deleted successfully')
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

onMounted(() => {
  loadApps()
})
</script>

<style scoped>
.app-manage-page {
  padding: 0 8px;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.search-area {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}
</style>
