<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="logo">视频网站</div>
      <div class="search-bar">
        <input type="text" id="searchInput" placeholder="搜索视频...">
      </div>
      <div class="nav-icons">
        <div class="nav-icon">🔍</div>
        <div class="nav-icon">👤</div>
      </div>
    </header>

    <div class="container">
      <!-- 顶部广告位 -->
      <div class="top-ad" id="topBannerAd">
        <h3>推荐广告</h3>
        <div id="topBannerContent" class="ad-content">这里是顶部广告展示区域</div>
      </div>

      <!-- 主要布局 -->
      <div class="main-layout">
        <!-- 侧边栏 -->
        <aside class="sidebar">
          <h3>视频分类</h3>
          <ul class="categories">
            <li data-category="movie">🎬 电影</li>
            <li data-category="tv">📺 电视剧</li>
            <li data-category="anime">🎨 动画</li>
            <li data-category="music">🎵 音乐</li>
            <li data-category="game">🎮 游戏</li>
            <li data-category="documentary">📚 纪录片</li>
          </ul>
          
          <!-- 侧边广告位 -->
          <div class="sidebar-ad" id="sidebarAd">
            <h4>广告推广</h4>
            <div id="sidebarContent" class="ad-content">侧边栏广告位</div>
          </div>
        </aside>

        <!-- 视频内容 -->
        <main class="video-content">
          <h1>热门视频</h1>
          <div class="video-list">
            <VideoCard
              v-for="video in videos.slice(0, 3)"
              :key="video.id"
              :video="video"
              @click="goToVideo(video.id)"
            />
          </div>
          
          <!-- 信息流广告 -->
          <div class="highlight" id="feedAd" style="text-align: center; margin: 20px 0;">
            <h4>广告推广</h4>
            <div id="feedContent" class="ad-content">这里是信息流广告位</div>
          </div>
          
          <div class="video-list">
            <VideoCard
              v-for="video in videos.slice(3)"
              :key="video.id"
              :video="video"
              @click="goToVideo(video.id)"
            />
          </div>
        </main>

        <!-- 右侧广告 -->
        <aside class="right-ads">
          <div class="right-ad" id="rightAd1">
            <h4>推荐广告</h4>
            <div id="rightAd1Content" class="ad-content">推荐视频广告</div>
          </div>
          <div class="right-ad" id="rightAd2">
            <h4>热门广告</h4>
            <div id="rightAd2Content" class="ad-content">热门视频推荐</div>
          </div>
        </aside>
      </div>

      <!-- 底部广告位 -->
      <div class="bottom-ad" id="bottomAd">
        <h3>相关推荐</h3>
        <div id="bottomContent" class="ad-content">底部广告展示区域</div>
      </div>

      <!-- 调试面板 -->
      <div class="debug-panel">
        <h3>广告追踪调试</h3>
        <div class="debug-info" id="debugInfo">广告追踪SDK初始化中...</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'
import VideoCard from '../components/VideoCard.vue'
import { useRouter } from 'vue-router'
import { useAdTracker } from '../composables/useAdTracker'
import '../styles/video-list.css'

const videos = ref([])
const router = useRouter()
const {
  initAdTracker,
  getRecommendedAds,
  trackSearch,
  trackVideoClick,
  addDebugInfo
} = useAdTracker()

let searchTimeout

onMounted(async () => {
  // 初始化广告追踪SDK
  const sdkInitialized = initAdTracker()
  
  // 加载视频数据
  try {
    const res = await axios.get('http://localhost:8082/api/videos')
    videos.value = res.data
    addDebugInfo(`加载视频成功: ${res.data.length}个视频`)
  } catch (error) {
    addDebugInfo(`加载视频失败: ${error.message}`)
  }
  
  // 如果SDK初始化成功，获取推荐广告
  if (sdkInitialized) {
    getRecommendedAds()
  }
  
  // 设置搜索事件监听
  const searchInput = document.getElementById('searchInput')
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      const query = e.target.value.trim()
      trackSearch(query)
    })
  }
  
  // 设置分类点击事件
  document.querySelectorAll('.categories li').forEach(li => {
    li.addEventListener('click', () => {
      const category = li.dataset.category
      addDebugInfo(`分类点击: ${category}`)
    })
  })
})

onUnmounted(() => {
  // 清除定时器
  clearTimeout(searchTimeout)
})

function goToVideo(id) {
  // 追踪视频点击
  const video = videos.value.find(v => v.id === id)
  if (video) {
    trackVideoClick(id, video.category)
  }
  // 跳转到视频详情页
  router.push(`/video/${id}`)
}
</script>
