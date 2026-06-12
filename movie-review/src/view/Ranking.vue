<template>
  <div class="min-h-screen bg-gray-50 font-sans">

    <NavBar :username="userStore.userInfo?.username || '影迷'" @search="goSearch" />

    <div class="max-w-4xl mx-auto px-6 py-8 space-y-8">

      <div class="flex flex-col md:flex-row md:items-center justify-between gap-4 border-b border-gray-100 pb-4">
        <div class="flex items-center gap-3">
          <span class="w-2 h-8 bg-purple-600 rounded-full shadow-[0_0_10px_rgba(147,51,234,0.5)]"></span>
          <div>
            <h1 class="text-3xl font-black text-gray-800 tracking-tight">影片排行榜</h1>
            <p class="text-xs text-gray-400 mt-1">
              {{ currentSort === 'rating' ? '🔥 殿堂级神作：按平均评分从高到低排列' : '⚡ 社区大热门：按大众影评热度从高到低排列' }}
            </p>
          </div>
        </div>

        <div class="flex bg-gray-200/60 p-1 rounded-xl w-fit border border-gray-200/40">
          <button
            @click="changeSort('rating')"
            :class="currentSort === 'rating' ? 'bg-white text-purple-600 shadow-sm font-black' : 'text-gray-500 hover:text-gray-700 font-medium'"
            class="px-4 py-1.5 rounded-lg text-xs transition-all cursor-pointer"
          >
            ⭐ 评分最高
          </button>
          <button
            @click="changeSort('hot')"
            :class="currentSort === 'hot' ? 'bg-white text-purple-600 shadow-sm font-black' : 'text-gray-500 hover:text-gray-700 font-medium'"
            class="px-4 py-1.5 rounded-lg text-xs transition-all cursor-pointer"
          >
            🔥 社区热度
          </button>
        </div>
      </div>

      <div v-if="loading" class="bg-white rounded-2xl p-8 shadow-sm">
        <a-skeleton active :paragraph="{ rows: 5 }" />
      </div>

      <div v-else class="space-y-4">
        <div
          v-for="(item, index) in rankings"
          :key="item.id || item.movieId"
          class="flex items-center gap-5 bg-white rounded-2xl p-4 shadow-sm hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300 cursor-pointer group"
          @click="goDetail(item)"
        >
          <div
            class="flex items-center justify-center w-12 h-12 rounded-xl font-mono font-black text-lg shrink-0 transition-transform duration-300 group-hover:scale-105"
            :class="rankClass(index)"
          >
            {{ index + 1 }}
          </div>

          <img :src="item.coverUrl" class="w-14 h-20 object-cover rounded-xl shadow-sm shrink-0" />

          <div class="flex-1 min-w-0">
            <p class="font-black text-gray-800 text-lg truncate group-hover:text-purple-600 transition-colors">
              {{ item.title }}
            </p>
            <p class="text-xs text-purple-500 font-medium bg-purple-50 px-2 py-0.5 rounded-md w-fit mt-2">
              💬 {{ item.reviewCount }} 条精彩评论
            </p>
          </div>

          <div class="text-right shrink-0 bg-gradient-to-br from-gray-50 to-purple-50/20 p-2 rounded-xl border border-gray-100">
            <p class="text-2xl font-black text-yellow-500">★ {{ item.averageScore?.toFixed(1) }}</p>
            <p class="text-[10px] text-gray-400 font-medium mt-0.5">平均得分</p>
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

// 💡 核心记忆变量：当前停留的排序暗号（'rating' 为评分，'hot' 为热度）
const currentSort = ref('rating')

const rankClass = (index) => {
  if (index === 0) return 'bg-amber-100 text-amber-700 shadow-sm border border-amber-200/30'
  if (index === 1) return 'bg-slate-100 text-slate-600 shadow-sm border border-slate-200/30'
  if (index === 2) return 'bg-orange-100 text-orange-700 shadow-sm border border-orange-200/30'
  return 'bg-purple-50 text-purple-500'
}

const goDetail = (item) => {
  router.push({ name: 'detail', params: { id: item.id || item.movieId || '201' } })
}

const goSearch = (value) => {
  router.push({ name: 'home', query: value ? { keyword: value } : {} })
}

// 🚀 【对齐后端 RankingController】：参数名 sortBy，取值 rating / reviewCount
const fetchRankings = async () => {
  loading.value = true
  try {
    const res = await request.get('/rankings', {
      params: {
        sortBy: currentSort.value === 'hot' ? 'reviewCount' : 'rating',
      }
    })
    
    if (res.code === 200) {
      rankings.value = res.data || []
    }
  } catch (error) {
    console.error('获取排行榜失败:', error)
  } finally {
    loading.value = false
  }
}

// 🎯 监听胶囊点击：当汶汶点击“社区热度”或“评分最高”时触发
const changeSort = (sortType) => {
  if (currentSort.value === sortType) return // 防止重复点击重复刷请求
  currentSort.value = sortType
  fetchRankings() // 🔄 带着新暗号重新拉取洗牌列表！
}

onMounted(() => {
  fetchRankings()
})
</script>