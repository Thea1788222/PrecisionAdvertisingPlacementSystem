<template>
  <div class="category-view">
    <!-- 类别标题和描述 -->
    <div class="category-header">
      <h1>{{ categoryName || '新闻分类' }}</h1>
      <p v-if="categoryDescription">{{ categoryDescription }}</p>
      <button @click="goBack" class="back-button">返回主页</button>
    </div>

    <!-- 分类页顶部广告 -->
    <AdBanner position="category-top" :category="categoryName" />

    <!-- 新闻列表 -->
    <div class="news-section">
      <div v-if="loading" class="loading">
        加载中...
      </div>

      <div v-else-if="newsList.length === 0" class="no-news">
        <h3>暂无新闻</h3>
        <p>该分类下还没有新闻内容</p>
      </div>

      <div v-else class="news-list">
        <div
          v-for="news in newsList"
          :key="news.id"
          @click="goToNewsDetail(news.id)"
          class="news-item"
        >
          <div class="news-content">
            <h3 class="news-title">{{ news.title }}</h3>
            <div class="news-meta">
              <span class="news-time">{{ formatTime(news.publishTime) }}</span>
              <span class="news-author">{{ news.author }}</span>
              <span class="news-views">阅读 {{ news.views }}</span>
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
</template>

<script>
import { newsApi, categoryApi } from '../api/newsApi'
import AdBanner from '../components/AdBanner.vue'
import { getAdTracker } from '../utils/adTracker'

export default {
  name: 'CategoryView',
  components: {
    AdBanner
  },
  data() {
    return {
      categoryId: null,
      categoryName: '',
      categoryDescription: '',
      newsList: [],
      currentPage: 0,
      pageSize: 20,
      totalPages: 1,
      loading: false
    }
  },
  async mounted() {
    // 从路由参数获取类别ID
    this.categoryId = parseInt(this.$route.params.id)
    if (this.categoryId) {
      await this.loadCategoryInfo()
      await this.loadNews()
    }

    // 追踪分类页面浏览
    this.trackPageView()
  },
  methods: {
    async loadCategoryInfo() {
      try {
        const categories = await categoryApi.getAllCategories()
        const category = categories.find(c => c.id === this.categoryId)
        if (category) {
          this.categoryName = category.name
          this.categoryDescription = category.description
        }
      } catch (error) {
        console.error('加载类别信息失败:', error)
      }
    },
    async loadNews() {
      if (!this.categoryId) return

      this.loading = true
      try {
        const response = await newsApi.getNewsByCategory(this.categoryId, this.currentPage, this.pageSize)
        this.newsList = response.content || []
        this.totalPages = response.totalPages || 1
      } catch (error) {
        console.error('加载新闻失败:', error)
        this.newsList = []
      } finally {
        this.loading = false
      }
    },
    goToNewsDetail(newsId) {
      // 找到对应的新闻信息
      const news = this.newsList.find(item => item.id === newsId)
      if (news) {
        // 追踪新闻点击行为
        const tracker = getAdTracker()
        if (tracker) {
          tracker.trackPageView({
            targetId: `news-${newsId}`,
            category: this.categoryName,
            keywords: `新闻,${news.title},${this.categoryName},分类页面`
          })
          console.log('分类页面新闻点击已记录:', news.title)
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
        this.loadNews()
      }
    },
    nextPage() {
      if (this.currentPage < this.totalPages - 1) {
        this.currentPage++
        this.loadNews()
      }
    },

    // 行为追踪方法
    trackPageView() {
      const tracker = getAdTracker()
      if (tracker) {
        tracker.trackPageView({
          targetId: `category-${this.categoryId}`,
          category: this.categoryName,
          keywords: `分类,${this.categoryName},新闻列表`
        })
        console.log('分类页面浏览已记录:', this.categoryName)
      }
    }
  }
}
</script>

<style scoped>
.category-view {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 类别头部样式 */
.category-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 8px;
}

.category-header h1 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 32px;
  font-weight: 600;
}

.category-header p {
  margin: 0 0 20px 0;
  color: #666;
  font-size: 16px;
}

.back-button {
  padding: 10px 20px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.back-button:hover {
  background-color: #5a6268;
}

/* 新闻区域样式 */
.news-section {
  min-height: 400px;
}

.loading {
  text-align: center;
  padding: 50px;
  font-size: 18px;
  color: #666;
}

.no-news {
  text-align: center;
  padding: 50px;
}

.no-news h3 {
  color: #666;
  margin-bottom: 10px;
}

.no-news p {
  color: #999;
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