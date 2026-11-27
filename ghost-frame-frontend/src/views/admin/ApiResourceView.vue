<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { applyDiff, listResources, scanDiff } from '../../api/apiResource'
import { apiStatusLabel, fieldLabel } from '../../constants/apiResource'

const listLoading = ref(false)
const scanLoading = ref(false)
const applyLoading = ref(false)
const resources = ref([])
const diff = ref(null)

const addedKeys = ref([])
const modifiedKeys = ref([])
const removedKeys = ref([])

const added = computed(() => diff.value?.added ?? [])
const modified = computed(() => diff.value?.modified ?? [])
const removed = computed(() => diff.value?.removed ?? [])

function rowKey(item, index) {
  return item.id != null ? `id-${item.id}` : `new-${item.method}-${item.path}-${index}`
}

function formatTime(value) {
  if (!value) {
    return ''
  }
  return String(value).replace('T', ' ').slice(0, 19)
}

function displayValue(field, value) {
  if (field === 'status') {
    return apiStatusLabel(value)
  }
  return value ?? ''
}

async function loadList() {
  listLoading.value = true
  try {
    resources.value = (await listResources()) ?? []
  } finally {
    listLoading.value = false
  }
}

async function scan() {
  scanLoading.value = true
  try {
    const data = await scanDiff()
    diff.value = data
    addedKeys.value = (data?.added ?? []).map((item, index) => rowKey(item, index))
    modifiedKeys.value = (data?.modified ?? []).map((item, index) => rowKey(item, index))
    removedKeys.value = []
  } finally {
    scanLoading.value = false
  }
}

function toInsert(item) {
  return {
    path: item.path,
    method: item.method ?? '',
    module: item.module,
    summary: item.summary,
    handler: item.handler,
  }
}

function toUpdate(item) {
  return {
    id: item.id,
    module: item.module,
    summary: item.summary,
    handler: item.handler,
  }
}

function picked(list, keys) {
  return list.filter((item, index) => keys.includes(rowKey(item, index)))
}

function toggleAll(zone, checked) {
  const source = zone === 'added' ? added.value : zone === 'modified' ? modified.value : removed.value
  const next = checked ? source.map((item, index) => rowKey(item, index)) : []
  if (zone === 'added') {
    addedKeys.value = next
  } else if (zone === 'modified') {
    modifiedKeys.value = next
  } else {
    removedKeys.value = next
  }
}

async function apply() {
  const inserts = picked(added.value, addedKeys.value).map(toInsert)
  const updates = picked(modified.value, modifiedKeys.value).map(toUpdate)
  const removeIds = picked(removed.value, removedKeys.value)
    .map((item) => item.id)
    .filter((id) => id != null)
  if (!inserts.length && !updates.length && !removeIds.length) {
    ElMessage.warning('请先勾选要应用的差异')
    return
  }
  applyLoading.value = true
  try {
    const result = await applyDiff({ inserts, updates, removeIds })
    const skipped = result?.skipped ?? 0
    if (skipped > 0) {
      ElMessage.warning(`有 ${skipped} 条已过期被跳过，请重新扫描后再提交`)
    } else {
      ElMessage.success(
        `新增 ${result?.inserted ?? 0}，更新 ${result?.updated ?? 0}，移除 ${result?.removed ?? 0}`,
      )
    }
    await Promise.all([loadList(), scan()])
  } finally {
    applyLoading.value = false
  }
}

onMounted(loadList)
</script>

<template>
  <div class="api-page">
    <header class="page-head">
      <h1>功能管理</h1>
      <div class="actions">
        <el-button :loading="scanLoading" @click="scan">扫描差异</el-button>
        <el-button type="primary" :loading="applyLoading" :disabled="!diff" @click="apply">
          应用勾选
        </el-button>
      </div>
    </header>

    <el-card v-if="diff" shadow="never" class="block">
      <template #header>
        <span>
          扫描于 {{ formatTime(diff.scanTime) }}，容器 {{ diff.scannedCount }}，库中
          {{ diff.dbCount }}，一致 {{ diff.unchangedCount }}
        </span>
      </template>

      <el-collapse>
        <el-collapse-item name="added">
          <template #title>新增（{{ added.length }}）</template>
          <el-checkbox
            :model-value="added.length > 0 && addedKeys.length === added.length"
            :indeterminate="addedKeys.length > 0 && addedKeys.length < added.length"
            :disabled="!added.length"
            @change="(checked) => toggleAll('added', checked)"
          >
            全选
          </el-checkbox>
          <el-checkbox-group v-model="addedKeys">
            <div v-for="(item, index) in added" :key="rowKey(item, index)" class="diff-row">
              <el-checkbox :value="rowKey(item, index)">
                {{ item.method || 'ANY' }} {{ item.path }}
              </el-checkbox>
              <span class="meta">{{ item.module }} · {{ item.summary }}</span>
            </div>
          </el-checkbox-group>
          <el-empty v-if="!added.length" description="无新增" />
        </el-collapse-item>

        <el-collapse-item name="modified">
          <template #title>修改（{{ modified.length }}）</template>
          <el-checkbox
            :model-value="modified.length > 0 && modifiedKeys.length === modified.length"
            :indeterminate="modifiedKeys.length > 0 && modifiedKeys.length < modified.length"
            :disabled="!modified.length"
            @change="(checked) => toggleAll('modified', checked)"
          >
            全选
          </el-checkbox>
          <el-checkbox-group v-model="modifiedKeys">
            <div v-for="(item, index) in modified" :key="rowKey(item, index)" class="diff-row">
              <el-checkbox :value="rowKey(item, index)">
                {{ item.method || 'ANY' }} {{ item.path }}
              </el-checkbox>
              <ul class="changes">
                <li v-for="change in item.changes || []" :key="change.field">
                  {{ fieldLabel(change.field) }}：{{ displayValue(change.field, change.oldValue) }}
                  → {{ displayValue(change.field, change.newValue) }}
                </li>
              </ul>
            </div>
          </el-checkbox-group>
          <el-empty v-if="!modified.length" description="无修改" />
        </el-collapse-item>

        <el-collapse-item name="removed">
          <template #title>删除（{{ removed.length }}，默认不勾选）</template>
          <el-checkbox
            :model-value="removed.length > 0 && removedKeys.length === removed.length"
            :indeterminate="removedKeys.length > 0 && removedKeys.length < removed.length"
            :disabled="!removed.length"
            @change="(checked) => toggleAll('removed', checked)"
          >
            全选
          </el-checkbox>
          <el-checkbox-group v-model="removedKeys">
            <div v-for="(item, index) in removed" :key="rowKey(item, index)" class="diff-row">
              <el-checkbox :value="rowKey(item, index)">
                {{ item.method || 'ANY' }} {{ item.path }}
              </el-checkbox>
              <span class="meta">{{ item.module }} · {{ item.summary }}</span>
            </div>
          </el-checkbox-group>
          <el-empty v-if="!removed.length" description="无删除" />
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <el-card shadow="never">
      <template #header>
        <span>已入库接口</span>
      </template>
      <el-table v-loading="listLoading" :data="resources" border>
        <el-table-column prop="method" label="方法" width="90" />
        <el-table-column prop="path" label="路径" min-width="220" />
        <el-table-column prop="module" label="模块" width="140" />
        <el-table-column prop="summary" label="说明" min-width="160" />
        <el-table-column label="状态" width="140">
          <template #default="{ row }">
            <el-tag :type="row.status === 'REMOVED' ? 'info' : 'success'">
              {{ apiStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.page-head h1 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.actions {
  display: flex;
  gap: 8px;
}

.block {
  margin-bottom: 16px;
}

.diff-row {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 8px 0;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.meta {
  margin-left: 24px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.changes {
  margin: 0 0 0 24px;
  padding: 0;
  list-style: none;
  color: var(--el-text-color-regular);
  font-size: 13px;
}
</style>
