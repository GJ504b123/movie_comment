import { createApp } from 'vue'
import App from './App.vue'
import { createPinia } from 'pinia' 
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import router from './router'
import './assets/main.css' // 💡 注意：AI 写的路径可能是 ./assets/main.css，去掉前面的 ../


import './mock/index.js' 

const app = createApp(App)
app.use(createPinia())
app.use(Antd)
app.use(router)

app.mount('#app')