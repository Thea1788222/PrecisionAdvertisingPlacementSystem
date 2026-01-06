<template>
  <div class="home-view">
    <!-- 搜索框 -->
    <div class="search-section">
      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索新闻..."
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <button @click="handleSearch" class="search-button">搜索</button>
      </div>
    </div>

    <!-- 顶部横幅广告 -->
    <AdBanner position="top-banner" />

    <!-- 分类板块 -->
    <div class="categories-section">
      <h2 class="section-title">新闻分类</h2>
      <div class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          @click="goToCategory(category.id)"
          class="category-card"
        >
          <h3>{{ category.name }}</h3>
          <p v-if="category.description">{{ category.description }}</p>
        </div>
      </div>
    </div>

    <!-- 最新头条板块 -->
    <div class="headlines-section">
      <h2 class="section-title">最新头条</h2>
      <div class="news-list">
        <div
          v-for="news in latestNews"
          :key="news.id"
          @click="goToNewsDetail(news.id)"
          class="news-item"
        >
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <div class="news-meta">
              <span class="news-time">{{ formatTime(news.publishTime) }}</span>
              <span class="news-author">{{ news.author }}</span>
              <span v-if="news.category" class="news-category">{{ news.category.name }}</span>
            </div>
            <p class="news-preview">{{ getPreview(news.content) }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi, categoryApi } from '../api/newsApi'
import AdBanner from '../components/AdBanner.vue'
import { getAdTracker } from '../utils/adTracker'

export default {
  name: 'HomeView',
  components: {
    AdBanner
  },
  data() {
    return {
      searchKeyword: '',
      categories: [],
      latestNews: [],
      loading: false
    }
  },
  async mounted() {
    await this.loadData()

    // 追踪页面浏览行为
    this.trackPageView()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        // 并行加载类别和最新新闻
        const [categories, news] = await Promise.all([
          categoryApi.getAllCategories(),
          newsApi.getLatestNews()
        ])
        this.categories = categories
        this.latestNews = news
      } catch (error) {
        console.error('加载数据失败:', error)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      if (this.searchKeyword.trim()) {
        this.$router.push({
          name: 'search',
          query: { keyword: this.searchKeyword }
        })
      }
    },
    goToCategory(categoryId) {
      this.$router.push({
        name: 'category',
        params: { id: categoryId }
      })
    },
    goToNewsDetail(newsId) {
      this.$router.push({
        name: 'news-detail',
        params: { id: newsId }
      })
    },
    getPreview(content) {
      if (!content) return ''
      if (content.length <= 100) return content
      return content.substring(0, 100) + '...'
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },

    // 行为追踪方法
    trackPageView() {
      const tracker = getAdTracker()
      if (tracker) {
        tracker.trackPageView({
          targetId: 'homepage',
          category: 'news',
          keywords: '新闻,首页,头条'
        })
        console.log('页面浏览已记录: 新闻首页')
      }
    },

    handleSearch() {
      if (this.searchKeyword.trim()) {
        // 追踪搜索行为
        const tracker = getAdTracker()
        if (tracker) {
          tracker.trackSearch({
            keywords: this.searchKeyword.trim(),
            category: 'news',
            targetId: 'search-input'
          })
          console.log('搜索行为已记录:', this.searchKeyword)
        }

        this.$router.push({
          name: 'search',
          query: { keyword: this.searchKeyword }
        })
      }
    },

    goToCategory(categoryId) {
      // 找到对应的分类信息
      const category = this.categories.find(cat => cat.id === categoryId)
      if (category) {
        // 追踪分类点击行为
        const tracker = getAdTracker()
        if (tracker) {
          tracker.trackClick({
            targetId: `category-${categoryId}`,
            category: category.name,
            keywords: `分类,${category.name},新闻`
          })
          console.log('分类点击已记录:', category.name)
        }
      }

      this.$router.push({
        name: 'category',
        params: { id: categoryId }
      })
    },

    goToNewsDetail(newsId) {
      // 找到对应的新闻信息
      const news = this.latestNews.find(item => item.id === newsId)
      if (news) {
        // 追踪新闻点击行为
        const tracker = getAdTracker()
        if (tracker) {
          tracker.trackPageView({
            targetId: `news-${newsId}`,
            category: news.category ? news.category.name : 'news',
            keywords: `新闻,${news.title},${news.category ? news.category.name : ''}`
          })
          console.log('新闻点击已记录:', news.title)
        }
      }

      this.$router.push({
        name: 'news-detail',
        params: { id: newsId }
      })
    }
  }
}
</script>

<style scoped>
.home-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 搜索框样式 */
.search-section {
  margin-bottom: 40px;
}

.search-box {
  display: flex;
  gap: 10px;
  max-width: 600px;
  margin: 0 auto;
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  font-size: 16px;
  border: 2px solid #ddd;
  border-radius: 4px;
  outline: none;
}

.search-input:focus {
  border-color: #007bff;
}

.search-button {
  padding: 12px 24px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.search-button:hover {
  background-color: #0056b3;
}

/* 分类板块样式 */
.categories-section {
  margin-bottom: 40px;
}

.section-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.category-card {
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;
}

.category-card:hover {
  background: #e9ecef;
  border-color: #007bff;
  transform: translateY(-2px);
}

.category-card h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 18px;
}

.category-card p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

/* 最新头条样式 */
.headlines-section {
  margin-bottom: 40px;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-item {
  padding: 20px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.news-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.news-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.news-title {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.news-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #666;
}

.news-preview {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 15px;
}
</style>
