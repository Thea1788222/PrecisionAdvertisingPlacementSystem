<template>
  <header class="header">
    <div class="container">
      <!-- 网站Logo -->
      <div class="logo">
        <router-link to="/" class="logo-link">
          🛒 购物网站
        </router-link>
      </div>

      <!-- 导航菜单 -->
      <nav class="nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/products" class="nav-link">商品列表</router-link>
        <router-link to="/about" class="nav-link">关于我们</router-link>
      </nav>

      <!-- 搜索框 -->
      <div class="search-box">
        <input 
          type="text" 
          v-model="searchKeyword"
          placeholder="搜索商品..." 
          class="search-input"
          @keyup.enter="handleSearch"
        >
        <button @click="handleSearch" class="search-btn">搜索</button>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    // 稍后可以跳转到搜索页面或过滤商品列表
    console.log('搜索关键词:', searchKeyword.value)
    alert(`搜索: ${searchKeyword.value}`) // 临时提示，稍后替换为实际搜索功能
    searchKeyword.value = ''
  }
}
</script>

<style scoped>
.header {
  background-color: #2c3e50;
  color: white;
  padding: 0;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;
}

.logo-link {
  font-size: 1.5em;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 30px;
}

.nav-link {
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: rgba(255,255,255,0.1);
}

.nav-link.router-link-active {
  background-color: #3498db;
}

.search-box {
  display: flex;
  gap: 8px;
}

.search-input {
  padding: 8px 12px;
  border: none;
  border-radius: 4px;
  width: 200px;
  font-size: 14px;
}

.search-btn {
  padding: 8px 16px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-btn:hover {
  background-color: #2980b9;
}
</style>