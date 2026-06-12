<template>
  <div class="min-h-screen bg-gray-50 font-sans">

    <NavBar :username="userStore.userInfo?.username || '影迷'" @search="handleSearch" />

    <div class="max-w-7xl mx-auto px-6 py-8 space-y-12">

      <section class="rounded-3xl overflow-hidden shadow-2xl bg-gray-900 border border-white/10">
        <a-carousel autoplay effect="fade">
          <div class="relative h-[450px] cursor-pointer group">
            <img src="https://images.pexels.com/photos/1117132/pexels-photo-1117132.jpeg"
                 class="w-full h-full object-cover opacity-60 group-hover:scale-105 transition-transform duration-1000" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent"></div>
            <div class="absolute bottom-16 left-12 text-white">
              <span class="bg-purple-600/90 backdrop-blur-md text-xs px-3 py-1 rounded-full font-bold uppercase tracking-widest">Featured</span>
              <h2 class="text-5xl font-black mt-4 drop-shadow-lg">星际穿越</h2>
              <p class="text-gray-300 mt-4 text-lg max-w-xl line-clamp-2">在这场穿越虫洞的壮丽旅程中，探索爱与科学的终极边界。</p>
            </div>
          </div>
          <div class="relative h-[450px] cursor-pointer group">
            <img src="https://images.pexels.com/photos/7991579/pexels-photo-7991579.jpeg"
                 class="w-full h-full object-cover opacity-60 group-hover:scale-105 transition-transform duration-1000" />
            <div class="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent"></div>
            <div class="absolute bottom-16 left-12 text-white">
              <span class="bg-pink-600/90 backdrop-blur-md text-xs px-3 py-1 rounded-full font-bold uppercase tracking-widest">Classic</span>
              <h2 class="text-5xl font-black mt-4 drop-shadow-lg">霸王别姬</h2>
              <p class="text-gray-300 mt-4 text-lg max-w-xl line-clamp-2">不疯魔不成活，在时代的洪流中看尽梨园的爱恨情仇。</p>
            </div>
          </div>
        </a-carousel>
      </section>

      <section>
        <div class="flex items-center justify-between mb-8">
          <div class="flex items-center gap-3">
            <span class="w-2 h-8 bg-purple-600 rounded-full shadow-[0_0_10px_rgba(147,51,234,0.5)]"></span>
            <h3 class="text-3xl font-black text-gray-800 tracking-tight">
              {{ keyword ? `“${keyword}” 的搜索结果` : '热门影片' }}
            </h3>
          </div>
          <button class="text-purple-600 font-bold hover:underline cursor-pointer" @click="router.push('/ranking')">
            查看排行榜
          </button>
        </div>

        <div v-if="loading" class="flex flex-wrap gap-10">
          <div v-for="n in 3" :key="n" class="w-56">
            <a-skeleton active :paragraph="{ rows: 2 }" />
          </div>
        </div>

        <div v-else-if="movies.length === 0" class="text-center py-20 text-gray-400">
          <p class="text-lg font-bold">没有找到相关影片</p>
          <p class="text-sm mt-2">换个关键词试试吧</p>
        </div>

        <div v-else class="flex flex-wrap items-start justify-start gap-10">
          <MovieCard
            v-for="item in movies"
            :key="item.id"
            :movieInfo="item"
          />
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import MovieCard from '../components/MovieCard.vue'
import NavBar from '../components/NavBar.vue'
import request from '../utils/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const movies = ref([])
const loading = ref(true)
// 支持从其他页面（如排行榜搜索框）通过 ?keyword= 跳转过来直接出搜索结果
const keyword = ref(route.query.keyword?.toString() || '')

const fetchMovies = async () => {
  loading.value = true
  try {
    // 后端 GET /movies 的排序参数名是 sort（rating / releaseDate）
    const params = { page: 1, size: 20, sort: 'rating' }
    if (keyword.value) params.keyword = keyword.value
    const res = await request.get('/movies', { params })
    if (res.code === 200) {
      movies.value = res.data.list
    }
  } catch (error) {
    console.error('获取影片列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = (value) => {
  keyword.value = value || ''
  fetchMovies()
}

// 路由 query 变化时（如在排行榜再次搜索）同步刷新
watch(() => route.query.keyword, (val) => {
  keyword.value = val?.toString() || ''
  fetchMovies()
})

onMounted(() => {
  fetchMovies()
})
</script>

<style scoped>
:deep(.ant-carousel .slick-dots li button) {
  height: 6px !important;
  border-radius: 3px !important;
  background: rgba(255, 255, 255, 0.4) !important;
}
:deep(.ant-carousel .slick-dots li.slick-active button) {
  background: #9333ea !important;
  width: 24px !important;
}
</style>
