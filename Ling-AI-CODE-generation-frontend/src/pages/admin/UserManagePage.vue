<template>
  <div id="userManagePage">
    <!-- 搜索栏 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Account">
        <a-input v-model:value="searchParams.userAccount" placeholder="Enter account" />
      </a-form-item>
      <a-form-item label="Username">
        <a-input v-model:value="searchParams.userName" placeholder="Enter username" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Search</a-button>
      </a-form-item>
    </a-form>
    <a-divider />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="data"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userAvatar'">
          <a-image :src="record.userAvatar" width="60" />
        </template>
        <template v-else-if="column.dataIndex === 'userRole'">
          <div v-if="record.userRole === 'admin'">
            <a-tag color="green">Admin</a-tag>
          </div>
          <div v-else>
            <a-tag color="blue">User</a-tag>
          </div>
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-button danger @click="doDelete(record.id)">Delete</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteUser, listUserVoByPage } from '@/api/userController'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'

// 数据
const data = ref<API.UserVO[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.UserQueryRequest>({
  pageNum: 1,
  pageSize: 10,
})

// 获取数据
const fetchData = async () => {
  const res = await listUserVoByPage({
    ...searchParams,
  })
  if (res.data.data) {
    data.value = res.data.data.records ?? []
    total.value = res.data.data.totalRow ?? 0
  } else {
    message.error('Failed to fetch data, ' + res.data.message)
  }
}

// 页面加载时请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => ({
  current: searchParams.pageNum ?? 1,
  pageSize: searchParams.pageSize ?? 10,
  total: total.value,
  showSizeChanger: true,
  showTotal: (total: number) => `Total ${total} records`,
}))

// 表格变化处理
const doTableChange = (page: any) => {
  searchParams.pageNum = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索
const doSearch = () => {
  searchParams.pageNum = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: string) => {
  if (!id) return
  const res = await deleteUser({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    fetchData()
  } else {
    message.error('Delete failed')
  }
}

// 表格列定义
const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: 'Account', dataIndex: 'userAccount' },
  { title: 'Username', dataIndex: 'userName' },
  { title: 'Avatar', dataIndex: 'userAvatar' },
  { title: 'Bio', dataIndex: 'userProfile' },
  { title: 'Role', dataIndex: 'userRole' },
  { title: 'Created', dataIndex: 'createTime' },
  { title: 'Action', key: 'action' },
]
</script>

<style scoped>
#userManagePage {
  padding: 16px;
}
</style>
