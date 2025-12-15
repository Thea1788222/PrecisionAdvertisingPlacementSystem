import { ref } from 'vue'
import { defineStore } from 'pinia'
import apiService from '@/services/apiService'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(JSON.parse(localStorage.getItem('user')) || null)
  const token = ref(localStorage.getItem('token') || null)

  const isAuthenticated = () => {
    return !!token.value
  }

  const login = async (credentials) => {
    try {
      const response = await apiService.post('/auth/login', credentials)
      const { token: accessToken, user: userData } = response.data
      
      token.value = accessToken
      user.value = userData
      
      // 存储到本地存储
      localStorage.setItem('token', accessToken)
      localStorage.setItem('user', JSON.stringify(userData))
      
      // 设置默认的认证头
      apiService.setAuthToken(accessToken)
      
      return response.data
    } catch (error) {
      throw new Error(error.response?.data?.message || '登录失败')
    }
  }

  const register = async (userData) => {
    try {
      const response = await apiService.post('/auth/register', userData)
      return response.data
    } catch (error) {
      throw new Error(error.response?.data?.message || '注册失败')
    }
  }

  const logout = () => {
    token.value = null
    user.value = null
    
    // 清除本地存储
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    
    // 清除默认的认证头
    apiService.setAuthToken(null)
  }

  return {
    user,
    token,
    isAuthenticated,
    login,
    register,
    logout
  }
})