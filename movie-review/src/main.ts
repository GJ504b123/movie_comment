import { createApp } from 'vue'
import App from './App.vue'
import {createPinia} from 'pinia' 
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import router from './router'
import '../src/assets/main.css'
const app = createApp(App)
app.use(createPinia())
app.use(Antd)
// app.use(router)
app.use(router)

app.mount('#app')
