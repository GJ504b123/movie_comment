<template>
  <div class="flex min-h-screen bg-gray-100">
    
    <div class="w-64 bg-gray-950 text-gray-200 flex flex-col h-screen sticky top-0">
      
      <div class="p-6 border-b border-gray-800 flex items-center gap-3">
        <span class="text-xl">🎬</span>
        <div>
          <h1 class="font-bold text-white text-base tracking-wider">汶汶控制室</h1>
          <p class="text-[10px] text-gray-500 font-mono">SUPER ADMIN PANEL</p>
        </div>
      </div>

      <nav class="flex-1 p-4 space-y-2">
        <div 
          @click="router.push('/admin/users')"
          :class="router.currentRoute.value.path === '/admin/users' ? 'bg-purple-600 text-white' : 'hover:bg-gray-900 text-gray-400 hover:text-white'"
          class="flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer font-bold text-sm transition-all"
        >
          <span>👥</span> 用户审核
        </div>

        <div 
          @click="router.push('/admin/movies')"
          :class="router.currentRoute.value.path === '/admin/movies' ? 'bg-purple-600 text-white' : 'hover:bg-gray-900 text-gray-400 hover:text-white'"
          class="flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer font-bold text-sm transition-all"
        >
          <span>🎥</span> 影片管理
        </div>

        <div 
          @click="router.push('/admin/reviews')"
          :class="router.currentRoute.value.path === '/admin/reviews' ? 'bg-purple-600 text-white' : 'hover:bg-gray-900 text-gray-400 hover:text-white'"
          class="flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer font-bold text-sm transition-all"
        >
          <span>💬</span> 评论审核中心
        </div>

        <div 
          @click="router.push('/admin/logs')"
          :class="router.currentRoute.value.path === '/admin/logs' ? 'bg-purple-600 text-white' : 'hover:bg-gray-900 text-gray-400 hover:text-white'"
          class="flex items-center gap-3 px-4 py-3 rounded-xl cursor-pointer font-bold text-sm transition-all"
        >
          <span>📊</span> 系统日志
        </div>
      </nav>

      <div class="p-4 border-t border-gray-800">
        <div 
          @click="router.push('/')"
          class="flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl border border-gray-700 hover:border-white text-xs font-bold transition-all cursor-pointer"
        >
          <span>⬅️</span> 返回社区前台
        </div>
      </div>

    </div>

    <div class="flex-1 flex flex-col">
      <header class="bg-white border-b border-gray-200 h-16 flex items-center justify-between px-8 shadow-sm">
        <div class="text-sm font-bold text-gray-500">
          系统当前路径：<span class="text-purple-600 font-mono">{{ router.currentRoute.value.path }}</span>
        </div>
        <div class="flex items-center gap-2">
          <span class="inline-block w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
          <span class="text-xs font-bold text-gray-700">最高管理员：汶汶 (在线)</span>
        </div>
      </header>

      <main class="flex-1 p-8 overflow-y-auto">
        <RouterView />
      </main>
    </div>

  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
</script>
