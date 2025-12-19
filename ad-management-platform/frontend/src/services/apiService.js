import axios from 'axios'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: '/api', // 使用相对路径，通过Vite代理转发到后端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 在发送请求之前做些什么
    return config
  },
  (error) => {
    // 对请求错误做些什么
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    // 对响应数据做点什么
    return response
  },
  (error) => {
    // 对响应错误做点什么
    if (error.response?.status === 401) {
      // 未授权，可能需要重新登录
      // 这里可以触发登出操作
    }
    return Promise.reject(error)
  }
)

// 设置认证 token
const setAuthToken = (token) => {
  if (token) {
    apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
  } else {
    delete apiClient.defaults.headers.common['Authorization']
  }
}

// 初始化时设置 token（如果存在）
const storedToken = localStorage.getItem('token')
if (storedToken) {
  setAuthToken(storedToken)
}

export default {
  // 通用方法
  get: (url, config = {}) => apiClient.get(url, config),
  post: (url, data = {}, config = {}) => apiClient.post(url, data, config),
  put: (url, data = {}, config = {}) => apiClient.put(url, data, config),
  delete: (url, config = {}) => apiClient.delete(url, config),

  // 认证相关方法
  login: (credentials) => apiClient.post('/auth/login', credentials),
  register: (userData) => apiClient.post('/auth/register', userData),

  // 设置认证 token
  setAuthToken
}