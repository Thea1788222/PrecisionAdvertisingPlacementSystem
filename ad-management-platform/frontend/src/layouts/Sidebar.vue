<template>
  <div class="sidebar">
    <div class="logo">
      <h2>广告管理系统</h2>
    </div>
    
    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      router
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409eff"
    >
      <el-menu-item index="/dashboard">
        <el-icon><House /></el-icon>
        <span>仪表板</span>
      </el-menu-item>
      
      <el-menu-item index="/materials">
        <el-icon><Document /></el-icon>
        <span>广告素材</span>
      </el-menu-item>
      
      <el-menu-item index="/positions">
        <el-icon><Location /></el-icon>
        <span>广告位</span>
      </el-menu-item>
      
      <el-menu-item index="/advertisers">
        <el-icon><User /></el-icon>
        <span>广告商</span>
      </el-menu-item>
      
      <el-menu-item index="/statistics">
        <el-icon><DataAnalysis /></el-icon>
        <span>数据统计</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { House, Document, Location, User, DataAnalysis } from '@element-plus/icons-vue'

const route = useRoute()

const activeMenu = computed(() => {
  const { meta, path } = route
  // if set path, the sidebar will highlight the path you set
  if (meta.activeMenu) {
    return meta.activeMenu
  }
  return path
})
</script>

<style scoped>
.sidebar {
  background-color: #304156;
  height: 100%;
  width: 210px;
  transition: width 0.28s;
  position: relative;
  overflow: hidden;
}

.logo {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #2b3a4d;
  color: #fff;
}

.sidebar-menu {
  border: none;
  height: calc(100% - 50px);
  overflow-y: auto;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #2b3a4d !important;
}
</style>