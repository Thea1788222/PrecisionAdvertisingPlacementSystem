<template>
  <div class="news-detail-view">
    <div v-if="loading" class="loading">
      加载中...
    </div>

    <div v-else-if="!news" class="not-found">
      <h2>新闻不存在</h2>
      <p>抱歉，您查找的新闻不存在或已被删除。</p>
      <button @click="goBack" class="back-button">返回</button>
    </div>

    <div v-else class="news-content">
      <!-- 新闻标题 -->
      <h1 class="news-title">{{ news.title }}</h1>

      <!-- 新闻元信息 -->
      <div class="news-meta">
        <span class="news-author">作者：{{ news.author }}</span>
        <span class="news-time">发布时间：{{ formatTime(news.publishTime) }}</span>
        <span class="news-category" v-if="news.category">分类：{{ news.category.name }}</span>
        <span class="news-views">阅读量：{{ news.views }}</span>
      </div>

      <!-- 分割线 -->
      <hr class="divider">

      <!-- 新闻正文 -->
      <div class="news-body">
        <div class="news-text" v-html="formatContent(news.content)"></div>
      </div>

      <!-- 文章中部广告 -->
      <div class="article-ad">
        <AdBanner
          position="article-middle"
          :category="news.category ? news.category.name : 'news'"
        />
      </div>

      <!-- 返回按钮 -->
      <div class="action-buttons">
        <button @click="goBack" class="back-button">返回</button>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi } from '../api/newsApi'
import AdBanner from '../components/AdBanner.vue'
import { getAdTracker } from '../utils/adTracker'

export default {
  name: 'NewsDetailView',
  components: {
    AdBanner
  },
  data() {
    return {
      newsId: null,
      news: null,
      loading: false,
      pageViewStartTime: null
    }
  },
  async mounted() {
    // 从路由参数获取新闻ID
    this.newsId = parseInt(this.$route.params.id)
    if (this.newsId) {
      await this.loadNews()
    }

    // 记录页面浏览时间（用于计算停留时长）
    this.pageViewStartTime = Date.now()
  },

  beforeUnmount() {
    // 页面离开时记录阅读时长
    if (this.pageViewStartTime && this.news) {
      const readDuration = Math.floor((Date.now() - this.pageViewStartTime) / 1000)
      const tracker = getAdTracker()
      if (tracker && readDuration > 5) { // 只记录阅读超过5秒的行为
        tracker.trackReading({
          targetId: `news-${this.newsId}`,
          category: this.news.category ? this.news.category.name : 'news',
          duration: readDuration,
          keywords: `新闻,${this.news.title},阅读`
        })
        console.log('新闻阅读时长已记录:', readDuration, '秒')
      }
    }
  },
  methods: {
    async loadNews() {
      if (!this.newsId) return

      this.loading = true
      try {
        this.news = await newsApi.getNewsById(this.newsId)
        if (this.news) {
          // 新闻加载成功，记录详细的页面浏览行为
          const tracker = getAdTracker()
          if (tracker) {
            tracker.trackPageView({
              targetId: `news-${this.newsId}`,
              category: this.news.category ? this.news.category.name : 'news',
              keywords: `新闻详情,${this.news.title},${this.news.category ? this.news.category.name : ''},${this.news.author}`
            })
            console.log('新闻详情页面浏览已记录:', this.news.title)
          }
        } else {
          // 新闻不存在
          this.news = null
        }
      } catch (error) {
        console.error('加载新闻详情失败:', error)
        this.news = null
      } finally {
        this.loading = false
      }
    },
    goBack() {
      // 返回上一页，如果没有上一页则返回主页
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        this.$router.push({ name: 'home' })
      }
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
    formatContent(content) {
      if (!content) return ''

      // 将换行符转换为HTML换行
      return content.replace(/\n/g, '<br>')
    }
  }
}
</script>

<style scoped>
.news-detail-view {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.loading {
  text-align: center;
  padding: 100px;
  font-size: 18px;
  color: #666;
}

.not-found {
  text-align: center;
  padding: 100px;
}

.not-found h2 {
  color: #666;
  margin-bottom: 20px;
}

.not-found p {
  color: #999;
  margin-bottom: 30px;
}

.back-button {
  padding: 12px 24px;
  background-color: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.back-button:hover {
  background-color: #5a6268;
}

/* 新闻内容样式 */
.news-content {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.news-title {
  margin: 0 0 30px 0;
  font-size: 32px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
}

.news-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
  font-size: 14px;
  color: #666;
}

.news-author,
.news-time,
.news-category,
.news-views {
  display: inline-block;
}

.divider {
  border: none;
  border-top: 1px solid #e0e0e0;
  margin: 30px 0;
}

.news-body {
  margin-bottom: 40px;
}

.news-text {
  font-size: 18px;
  line-height: 1.8;
  color: #333;
}

.news-text p {
  margin-bottom: 20px;
}

.news-text br {
  display: block;
  margin-bottom: 10px;
}

.article-ad {
  margin: 40px 0;
  text-align: center;
}

.action-buttons {
  text-align: center;
  padding-top: 30px;
  border-top: 1px solid #e0e0e0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .news-detail-view {
    padding: 10px;
  }

  .news-content {
    padding: 20px;
  }

  .news-title {
    font-size: 24px;
  }

  .news-meta {
    flex-direction: column;
    gap: 10px;
  }

  .news-text {
    font-size: 16px;
  }
}
</style>