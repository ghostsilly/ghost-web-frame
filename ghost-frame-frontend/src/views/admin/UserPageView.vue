<script setup>
import { onMounted, reactive, ref } from 'vue'
import { pageUsers } from '../../api/user'
import { roleLabel } from '../../constants/userRole'
import { statusLabel } from '../../constants/userStatus'

const loading = ref(false)
const records = ref([])
const total = ref(0)

const query = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  nickname: '',
  status: '',
})

function formatTime(value) {
  if (!value) {
    return ''
  }
  return String(value).replace('T', ' ').slice(0, 19)
}

async function load() {
  loading.value = true
  try {
    const data = await pageUsers({
      pageNum: query.pageNum,
      pageSize: query.pageSize,
      username: query.username || undefined,
      nickname: query.nickname || undefined,
      status: query.status || undefined,
    })
    records.value = data?.records ?? []
    total.value = Number(data?.total ?? 0)
  } finally {
    loading.value = false
  }
}

function search() {
  query.pageNum = 1
  load()
}

function reset() {
  query.username = ''
  query.nickname = ''
  query.status = ''
  query.pageNum = 1
  load()
}

function onPageChange(page) {
  query.pageNum = page
  load()
}

function onSizeChange(size) {
  query.pageSize = size
  query.pageNum = 1
  load()
}

onMounted(load)
</script>

<template>
  <div class="user-page">
    <header class="page-head">
      <h1>用户管理</h1>
    </header>

    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent="search">
        <el-form-item label="用户名">
          <el-input v-model="query.username" clearable placeholder="用户名" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="query.nickname" clearable placeholder="昵称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部" style="width: 140px">
            <el-option label="正常" value="NORMAL" />
            <el-option label="禁用" value="LOCKED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="records" border>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="nickname" label="昵称" min-width="120" />
        <el-table-column prop="username" label="用户名" min-width="120" />
        <el-table-column label="角色" width="120">
          <template #default="{ row }">{{ roleLabel(row.role) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'LOCKED' ? 'danger' : 'success'">
              {{ statusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.createdTime) }}</template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="170">
          <template #default="{ row }">{{ formatTime(row.updatedTime) }}</template>
        </el-table-column>
      </el-table>

      <div class="pager">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next"
          :current-page="query.pageNum"
          :page-size="query.pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          @current-change="onPageChange"
          @size-change="onSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-head {
  margin-bottom: 16px;
}

.page-head h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.pager {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
