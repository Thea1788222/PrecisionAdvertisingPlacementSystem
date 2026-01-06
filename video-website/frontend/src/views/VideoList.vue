<template>
  <div class="app-container">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="logo">视频网站</div>
      <div class="search-bar">
        <div class="search-container">
          <input
            type="text"
            id="searchInput"
            v-model="searchQuery"
            @keyup.enter="performSearch"
            placeholder="搜索视频..."
          >
          <button @click="performSearch" class="search-btn">🔍</button>
        </div>
      </div>
      <div class="nav-icons">
        <div class="nav-icon">🔍</div>
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
            <li
              v-for="category in categories"
              :key="category.key"
              :data-category="category.key"
              :class="{ active: selectedCategory === category.key }"
              @click="selectCategory(category.key)"
            >
              {{ category.icon }} {{ category.name }}
            </li>
          </ul>

          <!-- 侧边广告位 -->
          <div class="sidebar-ad" id="sidebarAd">
            <h4>广告推广</h4>
            <div id="sidebarContent" class="ad-content">侧边栏广告位</div>
          </div>
        </aside>

        <!-- 视频内容 -->
        <main class="video-content">
          <h1>
            {{ selectedCategory === 'all' ? '热门视频' : categories.find(c => c.key === selectedCategory)?.name + '视频' }}
          </h1>

          <div v-if="loading" class="loading">加载中...</div>

          <div v-else class="video-list">
            <VideoCard
              v-for="video in videos"
              :key="video.id"
              :video="video"
              @click="goToVideo(video.id)"
            />
          </div>

          <div v-if="!loading && videos.length === 0" class="no-videos">
            暂无视频数据
          </div>

          <!-- 信息流广告 -->
          <div class="highlight" id="feedAd" style="text-align: center; margin: 20px 0;">
            <h4>广告推广</h4>
            <div id="feedContent" class="ad-content">这里是信息流广告位</div>
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
import { ref, onMounted} from 'vue'
import axios from 'axios'
import VideoCard from '../components/VideoCard.vue'
import { useRouter } from 'vue-router'
import { useAdTracker } from '../composables/useAdTracker'
import '../styles/video-list.css'

const videos = ref([])
const selectedCategory = ref('all')
const loading = ref(false)
const searchQuery = ref('')
const router = useRouter()
const {
  initAdTracker,
  getRecommendedAds,
  trackSearch,
  trackVideoClick,
  addDebugInfo
} = useAdTracker()


const categories = [
  { key: 'all', name: '全部', icon: '📹' },
  { key: 'electronics', name: '电子产品', icon: '📱' },
  { key: 'fashion', name: '时尚', icon: '👗' },
  { key: 'sports', name: '体育', icon: '⚽' },
  { key: 'home', name: '家居', icon: '🏠' },
  { key: 'food', name: '美食', icon: '🍽️' },
  { key: 'travel', name: '旅游', icon: '✈️' },
  { key: 'education', name: '教育', icon: '📚' },
  { key: 'finance', name: '金融', icon: '💰' },
  { key: 'health', name: '健康', icon: '🏥' },
  { key: 'beauty', name: '美容', icon: '💄' }
]

// loadVideos 函数
const loadVideos = async (category = 'all') => {
  loading.value = true
  try {
    let url = '/api/videos'  // ← 改为相对路径
    if (category !== 'all') {
      url = `/api/videos/category/${category}`  // ← 改为相对路径
    }
    const res = await axios.get(url)
    videos.value = res.data
    addDebugInfo(`加载视频成功: ${res.data.length}个视频 (分类: ${category})`)
  } catch (error) {
    addDebugInfo(`加载视频失败: ${error.message}`)
  } finally {
    loading.value = false
  }
}


onMounted(async () => {
  // 初始化广告追踪SDK
  const sdkInitialized = initAdTracker()

  // 加载视频数据
  await loadVideos()

  // 如果SDK初始化成功，获取推荐广告（原生图片类型）
  if (sdkInitialized) {
    getRecommendedAds({ type: 'native' })
  }

  // 设置分类点击事件
  document.querySelectorAll('.categories li').forEach(li => {
    li.addEventListener('click', () => {
      const category = li.dataset.category
      selectCategory(category)
    })
  })
})



function selectCategory(category) {
  selectedCategory.value = category
  searchQuery.value = ''
  loadVideos(category)
}

// 搜索功能
async function performSearch() {
  const query = searchQuery.value.trim()
  if (!query) return

  // 使用SDK追踪搜索行为
  trackSearch(query)

  loading.value = true
  try {
    const response = await axios.get('http://10.100.164.25:8082/api/videos/search', {
      params: {
        query: query
      }
    })

    videos.value = response.data
    selectedCategory.value = 'search'

    if (response.data.length === 0) {
      addDebugInfo(`搜索"${query}"没有找到相关结果`)
    } else {
      addDebugInfo(`搜索"${query}"完成: 找到 ${response.data.length} 个结果`)
    }
  } catch (error) {
    addDebugInfo(`搜索失败: ${error.message}`)
    // 搜索失败时清空视频列表
    videos.value = []
  } finally {
    loading.value = false
  }
}

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
