<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from './stores/user'

const userStore = useUserStore()

// 应用启动时恢复登录态
onMounted(() => {
  userStore.restoreAuth()
})
</script>

<template>
  <RouterView />
</template>
