<template>
  <div
    class="group flex flex-col rounded-2xl shadow-md hover:shadow-2xl bg-white overflow-hidden cursor-pointer transition-all duration-300 hover:-translate-y-1 w-56"
    @click="gotoDetail"
  >
    <div class="h-72 w-full overflow-hidden relative">
      <img
        :src="movieInfo.coverUrl"
        alt="电影海报"
        class="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
      />
      <div class="absolute top-3 right-3 bg-black/70 backdrop-blur-md text-yellow-400 text-sm font-bold px-2.5 py-1 rounded-lg">
        ★ {{ movieInfo.averageScore }}
      </div>
    </div>
    <div class="p-4 space-y-1">
      <p class="font-bold text-gray-800 truncate">{{ movieInfo.title }}</p>
      <div class="flex items-center justify-between text-xs text-gray-400">
        <span>{{ movieInfo.releaseDate }}</span>
        <span>{{ movieInfo.reviewCount }} 条评论</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const props = defineProps({
  movieInfo: { type: Object, required: true },
})
const router = useRouter()

const gotoDetail = () => {
  router.push({
    name: 'detail',
    params: { id: props.movieInfo.id },
  })
}
</script>
<style scoped></style>
