<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-950 px-4 font-sans relative overflow-hidden select-none">
    
    <div class="absolute -top-40 -right-40 w-96 h-96 bg-purple-600/30 rounded-full blur-3xl animate-pulse"></div>
    <div class="absolute -bottom-40 -left-40 w-96 h-96 bg-indigo-600/20 rounded-full blur-3xl"></div>

    <div class="relative w-full max-w-md bg-white/10 backdrop-blur-2xl rounded-3xl shadow-[0_8px_32px_0_rgba(0,0,0,0.37)] border border-white/10 p-8 space-y-6 transition-all duration-300 hover:border-white/20">
      
      <div class="text-center space-y-2">
        <div class="inline-flex items-center gap-2 bg-white/10 border border-white/10 px-4 py-1.5 rounded-xl backdrop-blur-md shadow-inner">
          <span class="text-purple-300 text-sm font-black tracking-widest font-mono">MOVIE REVIEW</span>
        </div>
        <h1 class="text-2xl font-black text-white tracking-wide">欢迎回来</h1>
        <p class="text-xs text-purple-200/50">最高控制室全面待命，请输入暗号</p>
      </div>

      <div class="space-y-4">
        
        <div class="space-y-1">
          <label class="block text-xs font-bold text-purple-200/70 tracking-wider">USERNAME / 用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="请输入用户名"
            class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/30 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all font-medium"
          />
        </div>

        <div class="space-y-1">
          <label class="block text-xs font-bold text-purple-200/70 tracking-wider">PASSWORD / 密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
            class="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white placeholder-white/30 focus:outline-none focus:border-purple-500 focus:ring-1 focus:ring-purple-500 transition-all font-medium"
          />
        </div>

        <button
          @click="handleLogin"
          :disabled="loading"
          class="w-full mt-2 bg-gradient-to-r from-purple-600 to-indigo-600 hover:from-purple-500 hover:to-indigo-500 text-white font-bold h-11 rounded-xl shadow-lg shadow-purple-900/40 active:scale-[0.98] transition-all disabled:opacity-50 disabled:pointer-events-none flex items-center justify-center gap-2"
        >
          <span v-if="loading" class="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
          <span>{{ loading ? '权限验证中...' : '登 录' }}</span>
        </button>

      </div>

      <div class="flex items-center justify-between text-xs pt-2">
        <span class="text-white/40">还没有最高特权账号？</span>
        <router-link to="/register" class="text-purple-400 font-bold hover:text-purple-300 hover:underline transition-colors">
          立即注册
        </router-link>
      </div>

      <div class="bg-black/20 rounded-xl p-3 border border-white/5 text-[11px] text-white/40 leading-relaxed font-mono">
        <p class="font-bold text-purple-300/60 mb-1">🔑 局域网演练通行暗号：</p>
        <p>超管：<span class="text-white/70">wenwen</span> / 123456</p>
        <p>观众：<span class="text-white/70">xiaoming</span> / 123456</p>
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

    if (res && res.code === 200) {
      userStore.setAuth(res.data.token, res.data.user)
      message.success('登录成功')

      if (res.data.user.role === 'admin') {
        router.replace('/admin')
      } else {
        router.replace(route.query.redirect?.toString() || '/')
      }
    } else {
      message.error(res?.message || '登录失败')
    }
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>