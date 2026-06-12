<template>
  <div class="space-y-6">
    <!-- 页面标题 -->
    <div class="flex items-center justify-between">
      <div>
        <h2 class="text-2xl font-black text-gray-800">💬 评论审核中心</h2>
        <p class="text-xs text-gray-500 mt-1">管理全站影评内容，及时处理违规评论</p>
      </div>
      <div class="flex items-center gap-3">
        <a-select v-model:value="filterHidden" placeholder="筛选状态">
          <a-select-option :value="null">全部</a-select-option>
          <a-select-option :value="false">正常显示</a-select-option>
          <a-select-option :value="true">已隐藏</a-select-option>
        </a-select>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm flex gap-4">
      <a-input
        v-model:value="searchKeyword"
        placeholder="搜索影片标题或用户名..."
        size="large"
        allow-clear
        class="flex-1"
      />
      <a-button type="primary">搜索</a-button>
    </div>

    <!-- 统计卡片 -->
    <div class="grid grid-cols-4 gap-4">
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">总评论数</p>
            <p class="text-2xl font-black text-gray-800 mt-1">{{ totalCount }}</p>
          </div>
          <span class="text-3xl">💬</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">正常显示</p>
            <p class="text-2xl font-black text-green-600 mt-1">{{ visibleCount }}</p>
          </div>
          <span class="text-3xl">✅</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">已隐藏</p>
            <p class="text-2xl font-black text-red-600 mt-1">{{ hiddenCount }}</p>
          </div>
          <span class="text-3xl">🔒</span>
        </div>
      </div>
      <div class="bg-white rounded-2xl border border-gray-200 p-4 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-xs text-gray-500">总点赞数</p>
            <p class="text-2xl font-black text-purple-600 mt-1">{{ totalLikes }}</p>
          </div>
          <span class="text-3xl">👍</span>
        </div>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="bg-white rounded-2xl border border-gray-200 shadow-sm overflow-hidden">
      <div class="border-b border-gray-100 px-6 py-3 bg-gray-50">
        <div class="grid grid-cols-12 gap-4 text-xs font-bold text-gray-500">
          <div class="col-span-1">ID</div>
          <div class="col-span-2">影片</div>
          <div class="col-span-2">用户</div>
          <div class="col-span-1">评分</div>
          <div class="col-span-3">评论内容</div>
          <div class="col-span-1">点赞</div>
          <div class="col-span-1">状态</div>
          <div class="col-span-1 text-center">操作</div>
        </div>
      </div>

      <div v-if="loading" class="p-12">
        <a-skeleton active :paragraph="{ rows: 4 }" />
      </div>

      <div v-else-if="displayReviews.length === 0" class="p-12 text-center">
        <span class="text-4xl">📭</span>
        <p class="text-gray-500 mt-4 font-bold">暂无评论</p>
        <p class="text-xs text-gray-400">用户还没有发表任何影评</p>
      </div>

      <div v-else>
        <div
          v-for="review in displayReviews"
          :key="review.id"
          :class="review.hidden ? 'bg-gray-100' : ''"
          class="border-b border-gray-50 px-6 py-4 hover:bg-purple-50/30 transition-colors"
        >
          <div class="grid grid-cols-12 gap-4 items-center">
            <div class="col-span-1 text-sm font-mono text-gray-600">#{{ review.id }}</div>
            <div class="col-span-2 text-sm text-gray-800 truncate font-medium">{{ review.movieTitle }}</div>
            <div class="col-span-2 flex items-center gap-2">
              <img
                :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${review.username}`"
                class="w-6 h-6 rounded-full border border-gray-200"
              />
              <span class="text-sm text-gray-600">{{ review.username }}</span>
            </div>
            <div class="col-span-1">
              <span class="text-yellow-500 font-bold">★ {{ review.rating }}</span>
            </div>
            <div class="col-span-3">
              <p class="text-sm text-gray-600 line-clamp-1">{{ review.comment }}</p>
            </div>
            <div class="col-span-1 text-sm text-gray-400">{{ review.likeCount }}</div>
            <div class="col-span-1">
              <span
                :class="review.hidden ? 'bg-red-100 text-red-700' : 'bg-green-100 text-green-700'"
                class="px-2 py-0.5 rounded-full text-xs font-bold"
              >
                {{ review.hidden ? '已隐藏' : '正常' }}
              </span>
            </div>
            <div class="col-span-1 flex items-center justify-center gap-1">
              <button
                @click="toggleVisibility(review)"
                class="px-2.5 py-1 rounded-lg text-xs font-bold transition-colors"
                :class="review.hidden ? 'bg-green-100 text-green-700 hover:bg-green-200' : 'bg-red-100 text-red-700 hover:bg-red-200'"
              >
                {{ review.hidden ? '显示' : '隐藏' }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="!loading && reviews.length > 0" class="px-6 py-4 border-t border-gray-100 flex items-center justify-between">
        <p class="text-xs text-gray-400">共 {{ total }} 条评论</p>
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
const filterHidden = ref(null)
const reviews = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const totalCount = ref(0)
const visibleCount = ref(0)
const hiddenCount = ref(0)
const totalLikes = ref(0)

// 关键词在当前页内即时过滤（影片名 / 用户名）——后端 2.6 不支持 keyword 参数
const displayReviews = computed(() => {
  const kw = searchKeyword.value.trim()
  if (!kw) return reviews.value
  return reviews.value.filter(
    (r) => (r.movieTitle || '').includes(kw) || (r.username || '').includes(kw)
  )
})

const fetchReviews = async () => {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (filterHidden.value !== null) params.hidden = filterHidden.value

    const res = await request.get('/admin/reviews', { params })
    if (res.code === 200) {
      const responseData = res.data || res
      reviews.value = responseData.list || []
      total.value = responseData.total || 0
      
      // 💡 大厂级统计容错：如果后端/Mock 没给统计卡片字段，前端根据返回列表自动算出来！
      totalCount.value = responseData.totalCount || reviews.value.length
      visibleCount.value = responseData.visibleCount || reviews.value.filter(r => !r.hidden).length
      hiddenCount.value = responseData.hiddenCount || reviews.value.filter(r => r.hidden).length
      totalLikes.value = responseData.totalLikes || reviews.value.reduce((sum, r) => sum + (r.likeCount || 0), 0)
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 🚀 核心修复：纠正取反状态
const toggleVisibility = async (record) => {
  try {
    // 🎯 算好准备转变成的最新状态：如果是正常(false)，现在要变成隐藏(true)
    const nextHiddenStatus = !record.hidden
    console.log(`🚀 后台正在向合同路口 /admin/reviews/${record.id}/visibility 发射状态汽车, 目标值: ${nextHiddenStatus}`)
    
    const res = await request.put(`/admin/reviews/${record.id}/visibility`, {
      hidden: nextHiddenStatus
    })

    if (res.code === 200) {
      message.success(nextHiddenStatus ? '评论已成功隐藏' : '评论已恢复正常显示')
      // 🔄 刷新后台表格数据和统计卡片！
      fetchReviews() 
    } else {
      message.error(res.message || '操作失败')
    }
  } catch (error) {
    console.error('💥 后台切换评论可见性失败:', error)
  }
}

const handlePageChange = (pageNum) => {
  page.value = pageNum
  fetchReviews()
}

onMounted(() => {
  fetchReviews()
})
</script>