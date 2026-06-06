<template>
  <nav class="flex justify-between items-center px-8 py-4 bg-white shadow-sm w-full sticky top-0 z-50">

    <div
      class="bg-purple-100 px-4 py-2 rounded-lg cursor-pointer hover:bg-purple-200 transition-colors"
      @click="router.push('/')"
    >
      <p class="text-purple-700 text-xl font-extrabold mb-0 tracking-wider">MovieReview</p>
    </div>

    <div class="flex-1 max-w-md mx-8 hidden sm:block">
      <a-input-search
        v-model:value="keyword"
        placeholder="请输入您想要的影片..."
        enter-button
        allow-clear
        size="large"
        @search="onSearch"
      />
    </div>

    <div class="flex items-center gap-6">
      <button
        class="text-gray-600 font-bold hover:text-purple-600 transition-colors cursor-pointer hidden md:block"
        @click="router.push('/ranking')"
      >
        排行榜
      </button>

      <button
        v-if="userStore.isAdmin"
        class="text-gray-600 font-bold hover:text-purple-600 transition-colors cursor-pointer hidden md:block"
        @click="router.push('/admin')"
      >
        管理后台
      </button>

      <a-dropdown>
        <div class="flex items-center gap-3 cursor-pointer hover:opacity-80 transition-opacity">
          <span class="text-gray-700 font-medium">{{ username }}</span>
          <img
            :src="`https://api.dicebear.com/7.x/avataaars/svg?seed=${username}`"
            alt="用户头像"
            class="w-10 h-10 rounded-full bg-gray-200 border-2 border-purple-100"
          />
        </div>
        <template #overlay>
          <a-menu>
            <a-menu-item v-if="userStore.isAdmin" key="admin" @click="router.push('/admin')">
              管理后台
            </a-menu-item>
            <a-menu-item key="logout" @click="handleLogout">退出登录</a-menu-item>
          </a-menu>
        </template>
      </a-dropdown>
    </div>

  </nav>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const props = defineProps({
  username: { type: String, required: true },
})

const emit = defineEmits(['search'])

const router = useRouter()
const userStore = useUserStore()
const keyword = ref('')

const onSearch = (value) => {
  emit('search', value)
}

const handleLogout = () => {
  userStore.logout()
  router.replace('/login')
}
</script>
