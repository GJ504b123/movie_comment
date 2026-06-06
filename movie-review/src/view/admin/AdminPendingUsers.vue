<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-black text-gray-800">👥 用户审核中心</h2>
        <p class="text-xs text-gray-500 mt-1">管理新注册用户的审核流程，决定谁能加入影评社区</p>
      </div>
      <div class="flex items-center gap-2">
        <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-bold bg-yellow-100 text-yellow-800">
          ⏳ {{ pendingCount }} 人待审核
        </span>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
      <a-input
        v-model:value="searchKeyword"
        placeholder="搜索用户名或邮箱..."
        size="large"
        allow-clear
      />
    </div>

    <!-- 用户列表 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-3 bg-gray-50">
        <div class="grid grid-cols-12 gap-4 text-xs font-bold text-gray-500">
          <div class="col-span-2">用户ID</div>
          <div class="col-span-3">用户名</div>
          <div class="col-span-4">邮箱</div>
          <div class="col-span-2">注册时间</div>
          <div class="col-span-1 text-center">操作</div>
        </div>
      </div>

      <div v-if="loading" class="p-12">
        <a-skeleton active :paragraph="{ rows: 4 }" />
      </div>

      <div v-else-if="users.length === 0" class="p-12 text-center">
        <span class="text-4xl">🎉</span>
        <p class="text-gray-500 mt-4 font-bold">暂无待审核用户</p>
        <p class="text-xs text-gray-400">所有注册用户都已通过审核</p>
      </div>

      <div v-else>
        <div
          v-for="user in users"
          :key="user.id"
          class="border-b border-gray-50 px-6 py-4 hover:bg-purple-50/30 transition-colors"
        >
          <div class="grid grid-cols-12 gap-4 items-center">
            <div class="col-span-2 text-sm font-mono text-gray-600">#{{ user.id }}</div>
            <div class="col-span-3">
              <div class="flex items-center gap-2">
                <img
                  :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${user.id}`"
                  class="w-8 h-8 rounded-full border border-gray-200"
                />
                <span class="font-bold text-gray-800 text-sm">{{ user.username }}</span>
              </div>
            </div>
            <div class="col-span-4 text-sm text-gray-600 truncate">{{ user.email }}</div>
            <div class="col-span-2 text-xs text-gray-400">{{ formatDate(user.createTime) }}</div>
            <div class="col-span-1 flex items-center justify-center gap-1">
              <button
                @click="handleApprove(user.id)"
                class="px-2.5 py-1 bg-green-100 text-green-700 rounded-lg text-xs font-bold hover:bg-green-200 transition-colors"
              >
                ✓ 通过
              </button>
              <button
                @click="handleReject(user.id)"
                class="px-2.5 py-1 bg-red-100 text-red-700 rounded-lg text-xs font-bold hover:bg-red-200 transition-colors"
              >
                ✗ 拒绝
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="!loading && users.length > 0" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
        <p class="text-xs text-gray-400">共 {{ total }} 条记录</p>
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
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import request from '../../utils/request'

const loading = ref(true)
const searchKeyword = ref('')
const users = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const pendingCount = ref(0)

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users/pending', { 
      params: { page: page.value, size: size.value } 
    })
    if (res.code === 200) {
      users.value = res.data.list
      total.value = res.data.total
      pendingCount.value = res.data.total
    }
  } catch (error) {
    console.error('获取待审核用户失败:', error)
  } finally {
    loading.value = false
  }
}

const handleApprove = async (userId) => {
  try {
    const res = await request.put(`/admin/users/${userId}/approve`)
    if (res.code === 200) {
      message.success(`用户 #${userId} 已通过审核`)
      users.value = users.value.filter(u => u.id !== userId)
      total.value--
      pendingCount.value--
    }
  } catch (error) {
    console.error('审核失败:', error)
  }
}

const handleReject = async (userId) => {
  try {
    const res = await request.put(`/admin/users/${userId}/reject`)
    if (res.code === 200) {
      message.warning(`用户 #${userId} 已被拒绝`)
      users.value = users.value.filter(u => u.id !== userId)
      total.value--
      pendingCount.value--
    }
  } catch (error) {
    console.error('操作失败:', error)
  }
}

const handlePageChange = (pageNum) => {
  page.value = pageNum
  fetchUsers()
}

onMounted(() => {
  fetchUsers()
})
</script>
