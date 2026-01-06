<template>
  <div class="search-view">
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

    <!-- 搜索页顶部广告 -->
    <AdBanner position="search-top" category="news" />

    <!-- 搜索结果 -->
    <div class="results-section">
      <div v-if="loading" class="loading">
        搜索中...
      </div>

      <div v-else-if="searchResults.length === 0 && hasSearched" class="no-results">
        <h3>未找到相关新闻</h3>
        <p>没有找到包含 "{{ searchKeyword }}" 的新闻</p>
        <button @click="goBack" class="back-button">返回主页</button>
      </div>

      <div v-else-if="hasSearched" class="results-list">
        <h2 class="results-title">搜索结果：{{ searchKeyword }}</h2>
        <p class="results-count">共找到 {{ totalResults }} 条新闻</p>

        <div class="news-list">
          <div
            v-for="news in searchResults"
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

        <!-- 分页 -->
        <div v-if="totalPages > 1" class="pagination">
          <button @click="prevPage" :disabled="currentPage === 0">上一页</button>
          <span>第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage >= totalPages - 1">下一页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi } from '../api/newsApi'
import AdBanner from '../components/AdBanner.vue'
import { getAdTracker } from '../utils/adTracker'

export default {
  name: 'SearchView',
  components: {
    AdBanner
  },
  data() {
    return {
      searchKeyword: '',
      searchResults: [],
      currentPage: 0,
      pageSize: 20,
      totalPages: 1,
      totalResults: 0,
      loading: false,
      hasSearched: false
    }
  },
  mounted() {
    // 从路由参数获取搜索关键词
    const keyword = this.$route.query.keyword
    if (keyword) {
      this.searchKeyword = keyword
      this.performSearch()
    }

    // 追踪搜索页面浏览
    this.trackPageView()
  },
  methods: {
    async handleSearch() {
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

        // 更新URL参数
        this.$router.push({
          name: 'search',
          query: { keyword: this.searchKeyword }
        })
        this.performSearch()
      }
    },
    async performSearch() {
      if (!this.searchKeyword.trim()) return

      this.loading = true
      try {
        const response = await newsApi.searchNews(this.searchKeyword, this.currentPage, this.pageSize)
        this.searchResults = response.content || []
        this.totalPages = response.totalPages || 1
        this.totalResults = response.totalElements || 0
        this.hasSearched = true
      } catch (error) {
        console.error('搜索失败:', error)
        this.searchResults = []
        this.hasSearched = true
      } finally {
        this.loading = false
      }
    },
    goToNewsDetail(newsId) {
      // 找到对应的新闻信息
      const news = this.searchResults.find(item => item.id === newsId)
      if (news) {
        // 追踪新闻点击行为
        const tracker = getAdTracker()
        if (tracker) {
          tracker.trackPageView({
            targetId: `news-${newsId}`,
            category: news.category ? news.category.name : 'news',
            keywords: `新闻,${news.title},搜索结果`
          })
          console.log('搜索结果新闻点击已记录:', news.title)
        }
      }

      this.$router.push({
        name: 'news-detail',
        params: { id: newsId }
      })
    },
    goBack() {
      this.$router.push({ name: 'home' })
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
    prevPage() {
      if (this.currentPage > 0) {
        this.currentPage--
        this.performSearch()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        this.performSearch()
      }
    },

    // 行为追踪方法
    trackPageView() {
      const tracker = getAdTracker()
      if (tracker) {
        tracker.trackPageView({
          targetId: 'search-page',
          category: 'news',
          keywords: '搜索,新闻,查询'
        })
        console.log('搜索页面浏览已记录')
      }
    }
  }
}
</script>

<style scoped>
.search-view {
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

/* 结果区域样式 */
.results-section {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}

.no-results {
  text-align: center;
  padding: 50px;
}

.no-results h3 {
  color: #666;
  margin-bottom: 10px;
}

.no-results p {
  color: #999;
  margin-bottom: 20px;
}

.back-button {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.back-button:hover {
  background-color: #5a6268;
}

.results-title {
  font-size: 24px;
  margin-bottom: 10px;
  color: #333;
}

.results-count {
  color: #666;
  margin-bottom: 30px;
}

/* 新闻列表样式 */
.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
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

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
}

.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagination button:not(:disabled):hover {
  background-color: #f8f9fa;
}
</style>