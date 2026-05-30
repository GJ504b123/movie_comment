import {defineStore} from 'pinia'
//ref：把一个普通值包装成一个响应式对象，当ref包装的值变化时，所用用到这个值的地方都会自动更新
import { computed, ref } from 'vue'

export const  useUserStore = defineStore('user',()=>{
    const token = ref<string>('') //<string>泛型参数，告诉ref里面是string，返回的是Ref<string>对象
    const userInfo = ref<any>(null)
    const setAuth = (newToken:string,info:any) =>{
        token.value = newToken
        userInfo.value = info
        localStorage.setItem('token',newToken)
        localStorage.setItem('userInfo',JSON.stringify(info))
    }

    const logout = ()=>{
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')//返回字符串
    }

    const restoreAuth =()=>{
        const storedToken = localStorage.getItem('token')
        const storedUserInfo = localStorage.getItem('userInfo')
        if(storedToken && storedUserInfo){
            token.value = storedToken
            userInfo.value = JSON.parse(storedUserInfo) //storedUserInfo是字符串
        }
    }
    const isLoggedIn =computed(()=> !!token.value)
    const isAdmin = computed(()=>userInfo.value?.role === 'admin')
    return{
        token,userInfo,setAuth,logout,restoreAuth,isAdmin,isLoggedIn
    }

})