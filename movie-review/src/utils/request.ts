import axios from "axios";
const service = axios.create({});
service.interceptors.request.use(

    (config) =>{
        //1.请求发出前做什么
        return config
    },
    (error) =>{
        //2. 请求错误做什么
        return Promise.reject(error)
    }
);

service.interceptors.response.use(
    (response) =>{
    //3. 响应数据回来之前做什么
        return response
    },

    (error) =>{
        //4. 响应出错做什么
        return Promise.reject(error)
    }
)