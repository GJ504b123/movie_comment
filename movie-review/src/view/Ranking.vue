<template>
  <div class="min-h-screen bg-gray-50 font-sans">

    <NavBar :username="userStore.userInfo?.username || '影迷'" @search="goSearch" />

    <div class="max-w-4xl mx-auto px-6 py-8 space-y-8">

      <div class="flex items-center gap-3">
        <span class="w-2 h-8 bg-purple-600 rounded-full shadow-[0_0_10px_rgba(147,51,234,0.5)]"></span>
        <div>
          <h1 class="text-3xl font-black text-gray-800 tracking-tight">影片排行榜</h1>
          <p class="text-sm text-gray-500 mt-1">按平均评分从高到低排列</p>
        </div>
      </div>

      <div v-if="loading" class="bg-white rounded-2xl p-8 shadow-sm">
        <a-skeleton active :paragraph="{ rows: 5 }" />
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="(item, index) in rankings"
          :key="item.id || item.movieId"
          class="flex items-center gap-5 bg-white rounded-2xl p-4 shadow-sm hover:shadow-lg transition-all cursor-pointer"
          @click="goDetail(item)"
        >
          <div
            class="flex items-center justify-center w-12 h-12 rounded-xl font-black text-lg shrink-0"
            :class="rankClass(index)"
          >
            {{ index + 1 }}
          </div>

          <img :src="item.coverUrl" class="w-14 h-20 object-cover rounded-lg shrink-0" />

          <div class="flex-1 min-w-0">
            <p class="font-bold text-gray-800 text-lg truncate">{{ item.title }}</p>
            <p class="text-xs text-gray-400 mt-1">{{ item.reviewCount }} 条评论</p>
          </div>

          <div class="text-right shrink-0">
            <p class="text-2xl font-black text-yellow-500">★ {{ item.averageScore }}</p>
            <p class="text-xs text-gray-400">平均分</p>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import NavBar from '../components/NavBar.vue'
import request from '../utils/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

const rankings = ref([])
const loading = ref(true)

const rankClass = (index) => {
  if (index === 0) return 'bg-yellow-100 text-yellow-600'
  if (index === 1) return 'bg-gray-200 text-gray-600'
  if (index === 2) return 'bg-orange-100 text-orange-600'
  return 'bg-purple-50 text-purple-500'
}

const goDetail = (item) => {
  router.push({ name: 'detail', params: { id: item.id || item.movieId } })
}

const goSearch = (value) => {
  router.push({ name: 'home', query: value ? { keyword: value } : {} })
}

const fetchRankings = async () => {
  loading.value = true
  try {
    const res = await request.get('/movies/ranking')
    if (res.code === 200) {
      rankings.value = res.data
    }
  } catch (error) {
    console.error('获取排行榜失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchRankings()
})
</script>
