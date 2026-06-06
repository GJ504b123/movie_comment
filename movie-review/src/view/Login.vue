<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-950 px-4 font-sans relative overflow-hidden">
    <!-- 背景影像 -->
    <img
      src="https://images.pexels.com/photos/7991579/pexels-photo-7991579.jpeg"
      alt=""
      class="absolute inset-0 w-full h-full object-cover opacity-20"
    />
    <div class="absolute inset-0 bg-gradient-to-t from-gray-950 via-gray-950/80 to-gray-900/60"></div>

    <div class="relative w-full max-w-md bg-white/95 backdrop-blur-xl rounded-3xl shadow-2xl p-8 space-y-6">
      <div class="text-center space-y-2">
        <div class="inline-flex items-center gap-2 bg-purple-100 px-4 py-2 rounded-xl">
          <span class="text-purple-700 text-xl font-extrabold tracking-wider">MovieReview</span>
        </div>
        <h1 class="text-2xl font-black text-gray-800">欢迎回来</h1>
        <p class="text-sm text-gray-500">登录后即可浏览影片、打分与评论</p>
      </div>

      <a-form layout="vertical" @finish="handleLogin">
        <a-form-item label="用户名">
          <a-input
            v-model:value="form.username"
            size="large"
            placeholder="请输入用户名"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password
            v-model:value="form.password"
            size="large"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          />
        </a-form-item>

        <a-button
          type="primary"
          size="large"
          block
          :loading="loading"
          html-type="submit"
          class="!bg-purple-600 hover:!bg-purple-700 !rounded-xl !font-bold !h-11"
        >
          登 录
        </a-button>
      </a-form>

      <div class="flex items-center justify-between text-sm">
        <span class="text-gray-400">还没有账号？</span>
        <router-link to="/register" class="text-purple-600 font-bold hover:underline">
          立即注册
        </router-link>
      </div>

      <div class="bg-gray-50 rounded-xl p-3 text-xs text-gray-400 leading-relaxed">
        <p class="font-bold text-gray-500 mb-1">测试账号：</p>
        <p>管理员：wenwen / 123456</p>
        <p>普通用户：xiaoming / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import request from '../utils/request'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const form = reactive({
  username: '',
  password: '',
})

const handleLogin = async () => {
  if (!form.username || !form.password) {
    message.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/login', { ...form })
    if (res.code === 200) {
      userStore.setAuth(res.data.token, res.data.user)
      message.success('登录成功')
      if (res.data.user.role === 'admin') {
        router.replace('/admin')
      } else {
        router.replace(route.query.redirect?.toString() || '/')
      }
    } else {
      message.error(res.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>
