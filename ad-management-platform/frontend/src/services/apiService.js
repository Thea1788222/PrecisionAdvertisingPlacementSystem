import axios from 'axios'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: '/api', // 使用相对路径，通过Vite代理转发到后端
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 检查token是否过期
const isTokenExpired = (token) => {
  if (!token) return true
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const currentTime = Math.floor(Date.now() / 1000)
    return payload.exp < currentTime
  } catch (e) {
    return true
  }
}

// 清除过期token
const clearExpiredToken = () => {
  const token = localStorage.getItem('token')
  if (token && isTokenExpired(token)) {
    localStorage.removeItem('token')
    delete apiClient.defaults.headers.common['Authorization']
    return true
  }
  return false
}

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 在发送请求之前检查token是否过期
    clearExpiredToken()
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
      // 未授权，清除本地token并跳转到登录页
      localStorage.removeItem('token')
      delete apiClient.defaults.headers.common['Authorization']
      window.location.href = '/#/login'
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

// 初始化时设置 token（如果存在且未过期）
const storedToken = localStorage.getItem('token')
if (storedToken && !isTokenExpired(storedToken)) {
  setAuthToken(storedToken)
} else {
  // 如果token过期则清除
  localStorage.removeItem('token')
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
  setAuthToken,
  
  // token过期检查方法
  isTokenExpired,
  clearExpiredToken
}