<template>
  <div class="min-h-screen bg-gray-50 font-sans">
    
    <NavBar :username="mockLogin.user.username" />

    <div class="max-w-7xl mx-auto px-6 py-8 space-y-12">
      
    <section class="rounded-3xl overflow-hidden shadow-2xl bg-gray-900 border border-white/10">
        <a-carousel autoplay effect="fade">
          <div class="relative h-[450px]  cursor-pointer group">
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
            <h3 class="text-3xl font-black text-gray-800 tracking-tight">热门影片</h3>
          </div>
          <button class="text-purple-600 font-bold hover:underline cursor-pointer" >查看全部</button>
        </div>
        
        <div class="flex flex-wrap items-start justify-start gap-10">
          <MovieCard 
            v-for="item in mockMovie" 
            :key="item.id" 
            :movieInfo="item" 
          />
        </div>
      </section>

    </div>
  </div>
</template>

<script setup>
import MovieCard from '../components/MovieCard.vue'
import NavBar from '../components/NavBar.vue'
import {mockMovieList} from '../mock/movieData'
import {mockLoginResponse} from '../mock/movieData'
const mockMovie = mockMovieList
const mockLogin = mockLoginResponse
console.log(mockMovie)
</script>

<style scoped>
/* 针对 Ant Design 轮播图指示器的“深度选择器”美化 */
:deep(.ant-carousel .slick-dots li button) {
  height: 6px !important;
  border-radius: 3px !important;
  background: rgba(255, 255, 255, 0.4) !important;
}
:deep(.ant-carousel .slick-dots li.slick-active button) {
  background: #9333ea !important; /* 你的主题紫 */
  width: 24px !important;
}
</style>