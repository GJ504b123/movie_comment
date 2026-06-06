import axios from 'axios'
import { message } from 'ant-design-vue'

const service = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

service.interceptors.request.use(
  (config) => {
    // 1. 请求发出前：携带 token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers = config.headers || {}
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    // 2. 请求错误做什么
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
