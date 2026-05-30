<template>
  <nav class="flex justify-between items-center px-8 py-4 bg-white shadow-sm w-full sticky top-0 z-50">

    <div class="bg-purple-100 px-4 py-2 rounded-lg cursor-pointer hover:bg-purple-200 transition-colors">
      <p class="text-purple-700 text-xl font-extrabold mb-0 tracking-wider">MovieReview</p>
    </div>

    <div class="flex-1 max-w-md mx-8 hidden sm:block">
      <a-input-search
        placeholder="请输入您想要的影片..."
        enter-button
        allow-clear
        size="large"
      />
    </div>

    <div class="flex   items-center gap-3 cursor-pointer hover:opacity-80 transition-opacity">
      <span class="text-gray-700 font-medium">汶汶</span>
      <img 
        src="https://api.dicebear.com/7.x/avataaars/svg?seed=wenwen" 
        alt="用户头像" 
        class="w-10 h-10 rounded-full bg-gray-200 border-2 border-purple-100"
      >
    </div>

  </nav>
</template>

<script setup>
</script>