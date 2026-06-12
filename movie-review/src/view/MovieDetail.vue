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
                    @click="handleLike(reply)"
                  >
                    👍 <span>{{ reply.likeCount }}</span>
                  </div>
                </div>

<div v-if="reply.userId === userStore.userInfo?.id" class="absolute top-4 right-4 flex items-center gap-2">
  <span class="text-purple-600 text-xs bg-purple-100 px-2 py-0.5 rounded-md font-bold">
    我的评论
  </span>
  <button 
    @click="handleDeleteReview(reply.id)" 
    class="text-xs text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 px-2 py-0.5 rounded-md font-bold transition-colors cursor-pointer"
  >
    🗑️ 删除
  </button>
</div>
              </div>
            </div>

<div v-if="reviews.total > 0" class="mt-6 flex items-center justify-between text-xs text-gray-400 border-t border-gray-100 pt-4">
  <div class="flex gap-4">
    <p>共 {{ reviews.total }} 条评论</p>
    <p>每页显示 {{ reviews.size }} 条</p>
  </div>
  <a-pagination
    :current="reviews.page"
    :total="reviews.total"
    :page-size="reviews.size"
    simple
    @change="handleReviewPageChange"
  />
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

// 💡 记录当前前台影评正停留在第几页
const currentReviewPage = ref(1)

const myRating = ref(4) 
const myComment = ref('')
const submitting = ref(false)

// ----------------------------------------------------
// 🚀 【合同 3.2】获取影片详情（带动态 reviewPage 传参版）
// ----------------------------------------------------
const fetchDetail = async () => {
  isLoading.value = true
  const currentId = route.params.id || '201'
  try {
    console.log(`🚀 详情页正在向合同路口拉取 /api/movies/${currentId}?reviewPage=${currentReviewPage.value}`)
    
    // 🎯 严格对齐合同 3.2 的 Query 参数：reviewPage 和 reviewSize
    const res = await request.get(`/movies/${currentId}`, {
      params: {
        reviewPage: currentReviewPage.value,
        reviewSize: 5
      }
    })
    console.log('🎁 详情页成功收到大厂标准大礼盒:', res)

    if (res && res.code === 200) {
      const container = res.data || res
      movie.value = container.movie || null
      reviews.value = container.reviews || { list: [], total: 0, page: 1, size: 5, totalPages: 1 }
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

// 🎯 监听前台小飞毯点击：当管理员/用户点击下一页时触发
const handleReviewPageChange = (pageNum) => {
  console.log(`🚲 前台评论正准备翻向第 ${pageNum} 页...`)
  currentReviewPage.value = pageNum // 更改页码
  fetchDetail() // 🔄 重新拉取那一页的切片数据
}

// ----------------------------------------------------
// 🚀 【合同 3.3】发表评论/评分
// ----------------------------------------------------
const submitReview = async () => {
  if (!myComment.value.trim()) {
    message.warning('请输入评论内容')
    return
  }
  if (myRating.value < 0.5) {
    message.warning('请先打分（至少半颗星）')
    return
  }
  submitting.value = true
  const currentId = route.params.id || '201'

  try {
    const payload = {
      rating: Math.round(myRating.value * 2),
      comment: myComment.value.trim()
    }
    
    const res = await request.post(`/movies/${currentId}/reviews`, payload)

    if (res.code === 201 || res.code === 200) {
      message.success('评论发表成功')
      myComment.value = ''
      myRating.value = 4
      
      // 发表成功后，强行把视线拉回第一页，让最新鲜的影评露出来！
      currentReviewPage.value = 1
      fetchDetail()
    } else if (res.code === 409) {
      message.error('您已经评论过这部电影，不能重复评论哦')
    } else {
      message.error(res.message || '评论失败')
    }
  } catch (error) {
    console.error('评论失败:', error)
    if (error.response?.status === 409 || error.data?.code === 409) {
      message.error('您已经评论过这部电影，不能重复评论哦')
    }
  } finally {
    submitting.value = false
  }
}
// 点赞：POST /reviews/{id}/like（后端为计数模式，点一次 +1）
const handleLike = async (reply) => {
  try {
    const res = await request.post(`/reviews/${reply.id}/like`, { liked: true })
    if (res.code === 200) {
      reply.likeCount++
    }
  } catch (error) {
    console.error('点赞失败:', error)
  }
}

// 🚀 【严格对齐合同 3.5】：删除自己的评论
const handleDeleteReview = async (reviewId) => {
  try {
    console.log(`🚀 准备向合同路口 DELETE /api/reviews/${reviewId} 发射导弹，销毁该评论...`)
    const res = await request.delete(`/reviews/${reviewId}`)

    if (res.code === 200) {
      message.success('影评已成功撤销删除！')
      
      // 🔄 本地实时重冲刷：重新拉取详情，电影的平均分、总条数、翻页数据会一秒全部全自动重组！
      fetchDetail()
    } else {
      message.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('💥 删除评论失败:', error)
  }
}

onMounted(() => {
  fetchDetail()
})
</script>
