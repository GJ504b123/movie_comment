<template>
  <div class="min-h-screen bg-gray-50 p-6 md:p-12 font-sans">
    <div class="max-w-4xl mx-auto space-y-8">
      
      <button 
        @click="$router.back()" 
        class="px-5 py-2.5 bg-white text-purple-600 font-bold rounded-full shadow-sm hover:shadow-md transition-all cursor-pointer flex items-center gap-2 w-fit"
      >
        ← 返回首页
      </button>

      <div v-if="isLoading" class="bg-white rounded-3xl shadow-xl p-8 space-y-6 animate-pulse">
        <div class="h-64 bg-gray-200 rounded-2xl w-full"></div>
        <div class="h-8 bg-gray-200 rounded w-1/3"></div>
        <div class="space-y-3">
          <div class="h-4 bg-gray-200 rounded w-full"></div>
          <div class="h-4 bg-gray-200 rounded w-5/6"></div>
          <div class="h-4 bg-gray-200 rounded w-2/3"></div>
        </div>
        <a-skeleton active avatar :paragraph="{ rows: 3 }" class="mt-8" />
      </div>

      <div v-else class="bg-white rounded-3xl shadow-xl overflow-hidden">
        
        <div class="relative h-96 bg-gray-900">
          <img :src="movieData.movie.coverUrl" class="w-full h-full object-cover opacity-70" />
          <div class="absolute inset-0 bg-gradient-to-t from-black via-black/30 to-transparent"></div>
          
          <div class="absolute bottom-6 left-8 text-white space-y-2">
            <div class="flex items-center gap-3">
              <h1 class="text-4xl font-black">{{ movieData.movie.title }}</h1>
              <span class="text-xs bg-white/20 backdrop-blur-md px-2.5 py-1 rounded-md font-medium">
                {{ movieData.movie.releaseDate.split('-')[0] }} 上映 </span>
            </div>
            <p class="text-yellow-400 font-bold text-lg flex items-center gap-1">
              ★ {{ movieData.movie.averageScore }} 分 
              <span class="text-gray-400 text-xs font-normal">({{ movieData.movie.reviewCount }}人评)</span>
            </p>
          </div>
        </div>

        <div class="p-8 space-y-8">
          
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 bg-purple-50/50 p-4 rounded-2xl border border-purple-100/50 text-sm">
            <p class="text-gray-700">
              <span class="font-bold text-purple-900">导演：</span>{{ movieData.movie.director }}
            </p>
            <p class="text-gray-700 line-clamp-1">
              <span class="font-bold text-purple-900">主演：</span>{{ movieData.movie.cast }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-bold text-gray-800 mb-3">剧情简介</h3>
            <p class="text-gray-600 leading-relaxed text-sm">{{ movieData.movie.description }}</p>
          </div>

          <hr class="border-gray-100" />

          <div>
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-xl font-bold text-gray-800">全部影评 ({{ movieData.reviews.total }})</h3>
              <span class="text-xs text-gray-400">点击小铅笔可修改自己的评论</span>
            </div>
            
            <div class="space-y-4">
              <div v-for="reply in movieData.reviews.list" :key="reply.id" class="flex gap-4 p-4 rounded-2xl bg-gray-50 border border-gray-100 relative group">
                
                <img :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${reply.userId}`" class="w-10 h-10 rounded-full border border-gray-200 bg-white" />
                
                <div class="space-y-1 flex-1">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                      <span class="font-bold text-gray-800 text-sm">{{ reply.username }}</span>
                      <span class="text-xs text-yellow-500">★ {{ reply.rating }}分</span>
                    </div>
                    <span class="text-xs text-gray-400">{{ reply.createTime.split('T')[0] }}</span>
                  </div>
                  
                  <p class="text-gray-600 text-sm leading-relaxed pt-1">{{ reply.comment }}</p>
                  
                  <div class="flex items-center gap-1 pt-2 text-xs text-gray-400 cursor-pointer hover:text-purple-600 transition-colors w-fit">
                    👍 <span>{{ reply.likeCount }}</span>
                  </div>
                </div>

                <span v-if="reply.canEdit" class="absolute top-4 right-4 text-purple-600 text-xs bg-purple-100 px-2 py-0.5 rounded-md font-bold">
                  ✍️ 我的评论
                </span>
              </div>
            </div>

            <div class="mt-6 flex items-center justify-between text-xs text-gray-400 border-t border-gray-100 pt-4">
              <p>每页显示 {{ movieData.reviews.size }} 条</p>
              <p>当前第 {{ movieData.reviews.page }} / {{ movieData.reviews.totalPages }} 页</p>
            </div>

          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { mockMovieDatabase } from '../mock/movieData'

const route = useRoute()
const isLoading = ref(true)
const movieData = ref(null)

onMounted(() => {
  const currentId = route.params.id  
  console.log('正在请求电影 ID:', currentId)
  
  setTimeout(() => {
    movieData.value = mockMovieDatabase[currentId]
    isLoading.value = false
  }, 1000)
})
</script>