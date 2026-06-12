import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import router from './router'
import './assets/main.css'

/**
 * Mock 总开关（兜底演示方案）：
 * - movie-review/.env.development 里 VITE_USE_MOCK=true  → 浏览器内 mockjs 拦截 /api，前端独立运行
 * - 联调真后端时改为 false（或删掉该行），请求经 vite proxy 转发到 localhost:8080
 * 任何时候改回 true 即恢复纯前端演示，mock 文件本身不动。
 */
async function bootstrap() {
  if (import.meta.env.VITE_USE_MOCK === 'true') {
    await import('./mock/index.js')
    console.log('🧪 Mock 模式已开启：所有 /api 请求由浏览器内 mockjs 拦截')
  }

  const app = createApp(App)
  app.use(createPinia())
  app.use(Antd)
  app.use(router)
  app.mount('#app')
}

bootstrap()
