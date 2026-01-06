<template>
  <div class="admin-view">
    <h1>新闻管理后台</h1>
    
    <!-- 标签页切换 -->
    <div class="tabs">
      <button
        :class="{ active: activeTab === 'categories' }"
        @click="activeTab = 'categories'"
        class="tab-button"
      >
        类别管理
      </button>
      <button
        :class="{ active: activeTab === 'news' }"
        @click="activeTab = 'news'"
        class="tab-button"
      >
        新闻管理
      </button>
    </div>

    <!-- 类别管理 -->
    <div v-if="activeTab === 'categories'" class="category-management">
      <div class="management-header">
        <h2>类别管理</h2>
        <button @click="showCategoryDialog = true" class="add-button">添加类别</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>类别名称</th>
            <th>描述</th>
            <th>排序</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="category in categories" :key="category.id">
            <td>{{ category.id }}</td>
            <td>{{ category.name }}</td>
            <td>{{ category.description || '-' }}</td>
            <td>{{ category.sortOrder }}</td>
            <td>
              <button @click="editCategory(category)" class="edit-button">编辑</button>
              <button @click="deleteCategory(category.id)" class="delete-button">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新闻管理 -->
    <div v-if="activeTab === 'news'" class="news-management">
      <div class="management-header">
        <h2>新闻管理</h2>
        <button @click="showNewsDialog = true" class="add-button">添加新闻</button>
      </div>

      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>标题</th>
            <th>作者</th>
            <th>类别</th>
            <th>发布时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="news in newsList" :key="news.id">
            <td>{{ news.id }}</td>
            <td class="title-cell">{{ news.title }}</td>
            <td>{{ news.author }}</td>
            <td>{{ news.category ? news.category.name : '-' }}</td>
            <td>{{ formatTime(news.publishTime) }}</td>
            <td>
              <button @click="editNews(news)" class="edit-button">编辑</button>
              <button @click="deleteNews(news.id)" class="delete-button">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 分页 -->
      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 0">上一页</button>
        <span>第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="currentPage >= totalPages - 1">下一页</button>
      </div>
    </div>

    <!-- 类别编辑对话框 -->
    <div v-if="showCategoryDialog" class="dialog-overlay" @click="showCategoryDialog = false">
      <div class="dialog" @click.stop>
        <h3>{{ editingCategory ? '编辑类别' : '添加类别' }}</h3>
        <form @submit.prevent="saveCategory">
          <div class="form-group">
            <label>类别名称 *</label>
            <input v-model="categoryForm.name" type="text" required />
          </div>
          <div class="form-group">
            <label>描述</label>
            <input v-model="categoryForm.description" type="text" />
          </div>
          <div class="form-group">
            <label>排序顺序</label>
            <input v-model.number="categoryForm.sortOrder" type="number" />
          </div>
          <div class="form-actions">
            <button type="submit" class="save-button">保存</button>
            <button type="button" @click="cancelCategoryDialog" class="cancel-button">取消</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 新闻编辑对话框 -->
    <div v-if="showNewsDialog" class="dialog-overlay" @click="showNewsDialog = false">
      <div class="dialog large-dialog" @click.stop>
        <h3>{{ editingNews ? '编辑新闻' : '添加新闻' }}</h3>
        <form @submit.prevent="saveNews">
          <div class="form-group">
            <label>标题 *</label>
            <input v-model="newsForm.title" type="text" required />
          </div>
          <div class="form-group">
            <label>作者 *</label>
            <input v-model="newsForm.author" type="text" required />
          </div>
          <div class="form-group">
            <label>类别 *</label>
            <select v-model.number="newsForm.categoryId" required>
              <option value="">请选择类别</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">
                {{ cat.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>发布时间 *</label>
            <input v-model="newsForm.publishTime" type="datetime-local" required />
          </div>
          <div class="form-group">
            <label>正文内容 *</label>
            <textarea v-model="newsForm.content" rows="10" required></textarea>
          </div>
          <div class="form-actions">
            <button type="submit" class="save-button">保存</button>
            <button type="button" @click="cancelNewsDialog" class="cancel-button">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { newsApi, categoryApi } from '../api/newsApi'

export default {
  name: 'AdminView',
  data() {
    return {
      activeTab: 'categories',
      categories: [],
      newsList: [],
      currentPage: 0,
      pageSize: 20,
      totalPages: 1,
      
      // 类别对话框
      showCategoryDialog: false,
      editingCategory: null,
      categoryForm: {
        name: '',
        description: '',
        sortOrder: 0
      },
      
      // 新闻对话框
      showNewsDialog: false,
      editingNews: null,
      newsForm: {
        title: '',
        content: '',
        author: '',
        categoryId: null,
        publishTime: ''
      }
    }
  },
  async mounted() {
    await this.loadCategories()
    await this.loadNews() // 总是加载新闻数据
  },
  watch: {
    activeTab(newTab) {
      if (newTab === 'news') {
        this.loadNews()
      }
    }
  },
  methods: {
    async loadCategories() {
      try {
        this.categories = await categoryApi.getAllCategories()
      } catch (error) {
        console.error('加载类别失败:', error)
      }
    },
    async loadNews() {
      try {
        const response = await newsApi.getAllNews(this.currentPage, this.pageSize)
        this.newsList = response.content || []
        this.totalPages = response.totalPages || 1
      } catch (error) {
        console.error('加载新闻失败:', error)
        alert('加载新闻失败: ' + (error.message || '未知错误'))
      }
    },
    editCategory(category) {
      this.editingCategory = category
      this.categoryForm = {
        name: category.name,
        description: category.description || '',
        sortOrder: category.sortOrder || 0
      }
      this.showCategoryDialog = true
    },
    async saveCategory() {
      try {
        if (this.editingCategory) {
          await categoryApi.updateCategory(this.editingCategory.id, this.categoryForm)
        } else {
          await categoryApi.createCategory(this.categoryForm)
        }
        this.cancelCategoryDialog()
        await this.loadCategories()
      } catch (error) {
        alert('保存失败: ' + (error.message || '未知错误'))
      }
    },
    async deleteCategory(id) {
      if (confirm('确定要删除这个类别吗？')) {
        try {
          const result = await categoryApi.deleteCategory(id)
          if (result) {
            await this.loadCategories()
            alert('删除成功！')
          } else {
            alert('删除失败：服务器错误')
          }
        } catch (error) {
          alert('删除失败: ' + (error.message || '该类别下还有新闻，无法删除'))
        }
      }
    },
    cancelCategoryDialog() {
      this.showCategoryDialog = false
      this.editingCategory = null
      this.categoryForm = {
        name: '',
        description: '',
        sortOrder: 0
      }
    },
    editNews(news) {
      this.editingNews = news
      this.newsForm = {
        title: news.title,
        content: news.content,
        author: news.author,
        categoryId: news.categoryId || news.category?.id,
        publishTime: this.formatDateTimeLocal(news.publishTime)
      }
      this.showNewsDialog = true
    },
    async saveNews() {
      try {
        const newsData = {
          ...this.newsForm,
          publishTime: new Date(this.newsForm.publishTime).toISOString()
        }
        if (this.editingNews) {
          await newsApi.updateNews(this.editingNews.id, newsData)
        } else {
          await newsApi.createNews(newsData)
        }
        this.cancelNewsDialog()
        await this.loadNews()
      } catch (error) {
        alert('保存失败: ' + (error.message || '未知错误'))
      }
    },
    async deleteNews(id) {
      if (confirm('确定要删除这条新闻吗？')) {
        try {
          await newsApi.deleteNews(id)
          await this.loadNews()
        } catch (error) {
          alert('删除失败: ' + (error.message || '未知错误'))
        }
      }
    },
    cancelNewsDialog() {
      this.showNewsDialog = false
      this.editingNews = null
      this.newsForm = {
        title: '',
        content: '',
        author: '',
        categoryId: null,
        publishTime: ''
      }
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
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    },
    formatDateTimeLocal(time) {
      if (!time) return ''
      const date = new Date(time)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day}T${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.admin-view {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

h1 {
  margin-bottom: 30px;
  color: #333;
}

/* 标签页样式 */
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  border-bottom: 2px solid #e0e0e0;
}

.tab-button {
  padding: 12px 24px;
  background: none;
  border: none;
  border-bottom: 3px solid transparent;
  cursor: pointer;
  font-size: 16px;
  color: #666;
  transition: all 0.3s;
}

.tab-button:hover {
  color: #007bff;
}

.tab-button.active {
  color: #007bff;
  border-bottom-color: #007bff;
  font-weight: 600;
}

/* 管理头部 */
.management-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.add-button {
  padding: 10px 20px;
  background-color: #28a745;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.add-button:hover {
  background-color: #218838;
}

/* 表格样式 */
.data-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.data-table th,
.data-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
  color: #333; /* 加深字体颜色 */
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #333;
}

.data-table tr:hover {
  background-color: #f8f9fa;
}

.title-cell {
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.edit-button,
.delete-button {
  padding: 6px 12px;
  margin-right: 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.edit-button {
  background-color: #007bff;
  color: white;
}

.edit-button:hover {
  background-color: #0056b3;
}

.delete-button {
  background-color: #dc3545;
  color: white;
}

.delete-button:hover {
  background-color: #c82333;
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

/* 对话框样式 */
.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.large-dialog {
  max-width: 800px;
}

.dialog h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #333;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
}

.form-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 30px;
}

.save-button,
.cancel-button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.save-button {
  background-color: #007bff;
  color: white;
}

.save-button:hover {
  background-color: #0056b3;
}

.cancel-button {
  background-color: #6c757d;
  color: white;
}

.cancel-button:hover {
  background-color: #5a6268;
}
</style>
