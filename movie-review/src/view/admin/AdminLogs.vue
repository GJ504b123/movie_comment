<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-black text-gray-800">📊 系统访问日志</h2>
        <p class="text-xs text-gray-500 mt-1">监控用户行为，追踪系统操作记录</p>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
      <div class="grid grid-cols-4 gap-4">
        <a-input
          v-model:value="searchKeyword"
          placeholder="搜索用户名或IP..."
          allow-clear
        />
        <a-select v-model:value="filterAction" placeholder="筛选操作类型">
          <a-select-option :value="null">全部操作</a-select-option>
          <a-select-option value="login">登录</a-select-option>
          <a-select-option value="register">注册</a-select-option>
          <a-select-option value="search_movie">搜索影片</a-select-option>
          <a-select-option value="view_movie_detail">查看详情</a-select-option>
          <a-select-option value="post_review">发表评论</a-select-option>
          <a-select-option value="view_ranking">查看排行榜</a-select-option>
        </a-select>
        <a-date-picker v-model:value="startDate" placeholder="开始日期" />
        <a-date-picker v-model:value="endDate" placeholder="结束日期" />
      </div>
      <div class="flex justify-end gap-3 mt-4">
        <a-button @click="handleReset">重置</a-button>
        <a-button type="primary" @click="handleSearch">查询</a-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-3 gap-4">
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">今日访问</p>
            <p class="text-2xl font-black text-gray-800 mt-1">{{ todayCount }}</p>
          </div>
          <span class="text-3xl">🌐</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">登录次数</p>
            <p class="text-2xl font-black text-blue-600 mt-1">{{ loginCount }}</p>
          </div>
          <span class="text-3xl">🔐</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">评论发布</p>
            <p class="text-2xl font-black text-green-600 mt-1">{{ reviewCount }}</p>
          </div>
          <span class="text-3xl">✍️</span>
        </div>
      </div>
    </div>

    <!-- 日志列表 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-3 bg-gray-50">
        <div class="grid grid-cols-12 gap-4 text-xs font-bold text-gray-500">
          <div class="col-span-1">ID</div>
          <div class="col-span-2">用户</div>
          <div class="col-span-2">操作</div>
          <div class="col-span-2">目标ID</div>
          <div class="col-span-2">IP地址</div>
          <div class="col-span-3">时间</div>
        </div>
      </div>

      <div v-if="loading" class="p-12">
        <a-skeleton active :paragraph="{ rows: 4 }" />
      </div>

      <div v-else-if="logs.length === 0" class="p-12 text-center">
        <span class="text-4xl">📋</span>
        <p class="text-gray-500 mt-4 font-bold">暂无日志记录</p>
        <p class="text-xs text-gray-400">系统尚未记录任何访问日志</p>
      </div>

      <div v-else>
        <div
          v-for="log in logs"
          :key="log.id"
          class="border-b border-gray-50 px-6 py-4 hover:bg-purple-50/30 transition-colors"
        >
          <div class="grid grid-cols-12 gap-4 items-center">
            <div class="col-span-1 text-sm font-mono text-gray-600">#{{ log.id }}</div>
            <div class="col-span-2 flex items-center gap-2">
              <img
                :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${log.userId}`"
                class="w-6 h-6 rounded-full border border-gray-200"
              />
              <span class="text-sm text-gray-800">{{ log.username }}</span>
            </div>
            <div class="col-span-2">
              <span
                :class="getActionClass(log.action)"
                class="px-2 py-0.5 rounded-full text-xs font-bold"
              >
                {{ getActionLabel(log.action) }}
              </span>
            </div>
            <div class="col-span-2 text-sm text-gray-500 font-mono">
              {{ log.targetId || '-' }}
            </div>
            <div class="col-span-2 text-sm text-gray-500 font-mono">{{ log.ip }}</div>
            <div class="col-span-3 text-sm text-gray-400">{{ formatDateTime(log.createTime) }}</div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="!loading && logs.length > 0" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
        <p class="text-xs text-gray-400">共 {{ total }} 条日志</p>
        <a-pagination
          :current="page"
          :total="total"
          :page-size="size"
          show-size-changer
          show-quick-jumper
          @change="handlePageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '../../utils/request'

const loading = ref(true)
const searchKeyword = ref('')
const filterAction = ref(null)
const startDate = ref(null)
const endDate = ref(null)
const logs = ref([])
const page = ref(1)
const size = ref(20)
const total = ref(0)

const actionLabels = {
  login: '登录',
  register: '注册',
  search_movie: '搜索影片',
  view_movie_detail: '查看详情',
  post_review: '发表评论',
  update_review: '修改评论',
  delete_review: '删除评论',
  view_ranking: '查看排行榜',
  audit_user: '审核用户',
  add_movie: '添加影片',
  edit_movie: '修改影片',
  delete_movie: '删除影片',
  hide_review: '隐藏评论'
}

const todayCount = computed(() => {
  const today = new Date().toDateString()
  return logs.value.filter(log => new Date(log.createTime).toDateString() === today).length
})

const loginCount = computed(() => logs.value.filter(log => log.action === 'login').length)
const reviewCount = computed(() => logs.value.filter(log => log.action === 'post_review').length)

const getActionLabel = (action) => {
  return actionLabels[action] || action
}

const getActionClass = (action) => {
  const classMap = {
    login: 'bg-blue-100 text-blue-700',
    register: 'bg-green-100 text-green-700',
    search_movie: 'bg-purple-100 text-purple-700',
    view_movie_detail: 'bg-gray-100 text-gray-700',
    post_review: 'bg-yellow-100 text-yellow-700',
    update_review: 'bg-blue-100 text-blue-700',
    delete_review: 'bg-red-100 text-red-700',
    view_ranking: 'bg-purple-100 text-purple-700',
    audit_user: 'bg-cyan-100 text-cyan-700',
    add_movie: 'bg-green-100 text-green-700',
    edit_movie: 'bg-blue-100 text-blue-700',
    delete_movie: 'bg-red-100 text-red-700',
    hide_review: 'bg-red-100 text-red-700'
  }
  return classMap[action] || 'bg-gray-100 text-gray-700'
}

const formatDateTime = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
}

const fetchLogs = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (searchKeyword.value) params.keyword = searchKeyword.value
    if (filterAction.value) params.action = filterAction.value
    
    const res = await request.get('/admin/logs', { params })
    if (res.code === 200) {
      logs.value = res.data.list
      total.value = res.data.total
    }
  } catch (error) {
    console.error('获取日志失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  fetchLogs()
}

const handleReset = () => {
  searchKeyword.value = ''
  filterAction.value = null
  startDate.value = null
  endDate.value = null
  page.value = 1
  fetchLogs()
}

const handlePageChange = (pageNum) => {
  page.value = pageNum
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>
