<template>
  <div class="app-wrapper" :class="{hideSidebar: !sidebar.opened}">
    <!-- 侧边栏 -->
    <Sidebar class="sidebar-container" />
    
    <!-- 主容器 -->
    <div class="main-container">
      <!-- 导航栏 -->
      <Navbar />
      
      <!-- 主内容区 -->
      <AppMain />
    </div>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
import Navbar from './Navbar.vue'
import AppMain from './AppMain.vue'
import { useSidebarStore } from '@/stores/sidebar'

const sidebar = useSidebarStore()
</script>

<style scoped>
.app-wrapper {
  display: flex;
  height: 100vh;
  width: 100%;
  position: relative;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: margin-left .28s;
}

.hideSidebar {
  .sidebar-container {
    width: 54px;
  }
  
  .main-container {
    margin-left: 54px;
  }
}
</style>