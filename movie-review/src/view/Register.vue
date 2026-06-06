<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-950 px-4 font-sans relative overflow-hidden">
    <img
      src="https://images.pexels.com/photos/1117132/pexels-photo-1117132.jpeg"
      alt=""
      class="absolute inset-0 w-full h-full object-cover opacity-20"
    />
    <div class="absolute inset-0 bg-gradient-to-t from-gray-950 via-gray-950/80 to-gray-900/60"></div>

    <div class="relative w-full max-w-md bg-white/95 backdrop-blur-xl rounded-3xl shadow-2xl p-8 space-y-6">
      <div class="text-center space-y-2">
        <div class="inline-flex items-center gap-2 bg-purple-100 px-4 py-2 rounded-xl">
          <span class="text-purple-700 text-xl font-extrabold tracking-wider">MovieReview</span>
        </div>
        <h1 class="text-2xl font-black text-gray-800">创建账号</h1>
        <p class="text-sm text-gray-500">注册后需等待管理员审核通过方可登录</p>
      </div>

      <a-form layout="vertical" @finish="handleRegister">
        <a-form-item label="用户名">
          <a-input v-model:value="form.username" size="large" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="form.email" size="large" placeholder="请输入邮箱" allow-clear />
        </a-form-item>
        <a-form-item label="密码">
          <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码" />
        </a-form-item>

        <a-button
          type="primary"
          size="large"
          block
          :loading="loading"
          html-type="submit"
          class="!bg-purple-600 hover:!bg-purple-700 !rounded-xl !font-bold !h-11"
        >
          注 册
        </a-button>
      </a-form>

      <div class="flex items-center justify-between text-sm">
        <span class="text-gray-400">已有账号？</span>
        <router-link to="/login" class="text-purple-600 font-bold hover:underline">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import request from '../utils/request'

const router = useRouter()
const loading = ref(false)
const form = reactive({
  username: '',
  email: '',
  password: '',
})

const handleRegister = async () => {
  if (!form.username || !form.email || !form.password) {
    message.warning('请填写完整的注册信息')
    return
  }
  loading.value = true
  try {
    const res = await request.post('/auth/register', { ...form })
    if (res.code === 201) {
      message.success('注册成功，请等待管理员审核')
      router.replace('/login')
    } else {
      message.error(res.message || '注册失败')
    }
  } catch (error) {
    console.error('注册失败:', error)
  } finally {
    loading.value = false
  }
}
</script>
