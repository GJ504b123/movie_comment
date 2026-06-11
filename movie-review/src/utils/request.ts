import axios from 'axios'
import { message } from 'ant-design-vue'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// src/utils/request.ts 里的请求拦截器小升级
service.interceptors.request.use(
  (config) => {
    // 💡 智能化摸底：不管是 token 还是 user_token，抓到哪个算哪个！
    const token = localStorage.getItem('token') || localStorage.getItem('user_token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

service.interceptors.response.use(
  (response) => {
    // 3. 响应数据回来：直接返回业务数据体 { code, message, data }
    return response.data
  },
  (error) => {
    // 4. 响应出错做什么
    message.error(error?.response?.data?.message || '网络请求失败，请稍后重试')
    return Promise.reject(error)
  },
)

export default service
