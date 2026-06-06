<template>
  <div class="min-h-screen bg-gray-50 p-6 md:p-12 font-sans">
    <div class="max-w-4xl mx-auto space-y-8">

      <button
        @click="router.push('/')"
        class="px-5 py-2.5 bg-white text-purple-600 font-bold rounded-full shadow-sm hover:shadow-md transition-all cursor-pointer flex items-center gap-2 w-fit"
      >
        ← 返回首页
      </button>

      <div v-if="isLoading" class="bg-white rounded-3xl shadow-xl p-8 space-y-6">
        <div class="h-64 bg-gray-200 rounded-2xl w-full animate-pulse"></div>
        <a-skeleton active avatar :paragraph="{ rows: 3 }" class="mt-8" />
      </div>

      <div v-else-if="!movie" class="bg-white rounded-3xl shadow-xl p-16 text-center">
        <p class="text-lg font-bold text-gray-500">影片不存在或已下架</p>
      </div>

      <div v-else class="bg-white rounded-3xl shadow-xl overflow-hidden">

        <div class="relative h-96 bg-gray-900">
          <img :src="movie.coverUrl" class="w-full h-full object-cover opacity-70" />
          <div class="absolute inset-0 bg-gradient-to-t from-black via-black/30 to-transparent"></div>

          <div class="absolute bottom-6 left-8 text-white space-y-2">
            <div class="flex items-center gap-3">
              <h1 class="text-4xl font-black">{{ movie.title }}</h1>
              <span class="text-xs bg-white/20 backdrop-blur-md px-2.5 py-1 rounded-md font-medium">
                {{ movie.releaseDate?.split('-')[0] }} 上映
              </span>
            </div>
            <p class="text-yellow-400 font-bold text-lg flex items-center gap-1">
              ★ {{ movie.averageScore }} 分
              <span class="text-gray-400 text-xs font-normal">({{ movie.reviewCount }}人评)</span>
            </p>
          </div>
        </div>

        <div class="p-8 space-y-8">

          <div class="grid grid-cols-1 md:grid-cols-2 gap-4 bg-purple-50/50 p-4 rounded-2xl border border-purple-100/50 text-sm">
            <p class="text-gray-700">
              <span class="font-bold text-purple-900">导演：</span>{{ movie.director }}
            </p>
            <p class="text-gray-700 line-clamp-1">
              <span class="font-bold text-purple-900">主演：</span>{{ movie.cast }}
            </p>
          </div>

          <div>
            <h3 class="text-xl font-bold text-gray-800 mb-3">剧情简介</h3>
            <p class="text-gray-600 leading-relaxed text-sm">{{ movie.description }}</p>
          </div>

          <hr class="border-gray-100" />

          <!-- 打分 + 评论表单 -->
          <div class="bg-gray-50 rounded-2xl p-6 border border-gray-100 space-y-4">
            <h3 class="text-lg font-bold text-gray-800">发表我的影评</h3>
            <div class="flex items-center gap-3">
              <span class="text-sm text-gray-600 font-medium">我的评分：</span>
              <a-rate v-model:value="myRating" allow-half />
              <span class="text-yellow-500 font-bold text-sm">{{ myRating * 2 }} 分</span>
            </div>
            <a-textarea
              v-model:value="myComment"
              :rows="3"
              placeholder="写下你对这部影片的看法..."
              :maxlength="200"
              show-count
            />
            <div class="flex justify-end">
              <a-button
                type="primary"
                :loading="submitting"
                class="!bg-purple-600 hover:!bg-purple-700 !rounded-xl !font-bold"
                @click="submitReview"
              >
                提交影评
              </a-button>
            </div>
          </div>

          <div>
            <div class="flex items-center justify-between mb-6">
              <h3 class="text-xl font-bold text-gray-800">全部影评 ({{ reviews.total }})</h3>
            </div>

            <div v-if="reviews.list.length === 0" class="text-center py-10 text-gray-400 text-sm">
              还没有人评论，快来抢沙发吧～
            </div>

            <div class="space-y-4">
              <div
                v-for="reply in reviews.list"
                :key="reply.id"
                class="flex gap-4 p-4 rounded-2xl bg-gray-50 border border-gray-100 relative group"
              >
                <img :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${reply.userId}`" class="w-10 h-10 rounded-full border border-gray-200 bg-white" />

                <div class="space-y-1 flex-1">
                  <div class="flex items-center justify-between">
                    <div class="flex items-center gap-2">
                      <span class="font-bold text-gray-800 text-sm">{{ reply.username }}</span>
                      <span class="text-xs text-yellow-500">★ {{ reply.rating }}分</span>
                    </div>
                    <span class="text-xs text-gray-400">{{ reply.createTime?.split('T')[0] }}</span>
                  </div>

                  <p class="text-gray-600 text-sm leading-relaxed pt-1">{{ reply.comment }}</p>

                  <div
                    class="flex items-center gap-1 pt-2 text-xs text-gray-400 cursor-pointer hover:text-purple-600 transition-colors w-fit"
                    @click="reply.likeCount++"
                  >
                    👍 <span>{{ reply.likeCount }}</span>
                  </div>
                </div>

                <span
                  v-if="reply.userId === userStore.userInfo?.id"
                  class="absolute top-4 right-4 text-purple-600 text-xs bg-purple-100 px-2 py-0.5 rounded-md font-bold"
                >
                  我的评论
                </span>
              </div>
            </div>

            <div class="mt-6 flex items-center justify-between text-xs text-gray-400 border-t border-gray-100 pt-4">
              <p>每页显示 {{ reviews.size }} 条</p>
              <p>当前第 {{ reviews.page }} / {{ reviews.totalPages || 1 }} 页</p>
            </div>

          </div>

        </div>

      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import request from '../utils/request'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isLoading = ref(true)
const movie = ref(null)
const reviews = ref({ list: [], total: 0, page: 1, size: 5, totalPages: 1 })

const myRating = ref(4)
const myComment = ref('')
const submitting = ref(false)

const fetchDetail = async () => {
  isLoading.value = true
  const currentId = route.params.id
  try {
    const res = await request.get(`/movies/${currentId}`)
    if (res.code === 200 && res.data) {
      movie.value = res.data
      reviews.value = res.data.reviews || { list: [], total: 0, page: 1, size: 5, totalPages: 1 }
    } else {
      movie.value = null
    }
  } catch (error) {
    console.error('获取影片详情失败:', error)
    movie.value = null
  } finally {
    isLoading.value = false
  }
}

const submitReview = async () => {
  if (!myComment.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  submitting.value = true
  try {
    const payload = {
      movieId: Number(route.params.id),
      userId: userStore.userInfo?.id,
      username: userStore.userInfo?.username,
      rating: myRating.value * 2,
      comment: myComment.value.trim(),
    }
    const res = await request.post('/reviews', payload)
    if (res.code === 201) {
      message.success('评论发表成功')
      // 本地插入，提供即时反馈
      reviews.value.list.unshift({
        id: res.data.id,
        userId: payload.userId,
        username: payload.username,
        rating: payload.rating,
        comment: payload.comment,
        likeCount: 0,
        createTime: res.data.createTime,
      })
      reviews.value.total += 1
      myComment.value = ''
      myRating.value = 4
    }
  } catch (error) {
    console.error('评论失败:', error)
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchDetail()
})
</script>
