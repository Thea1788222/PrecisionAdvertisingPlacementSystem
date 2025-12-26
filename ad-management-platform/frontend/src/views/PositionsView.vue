<template>
  <div class="positions-page">
    <div class="page-header">
      <h1>广告位管理</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">
        <i class="icon-plus"></i>
        新建广告位
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section card">
      <div class="filter-row">
        <div class="form-group">
          <label>网站</label>
          <select v-model="filters.website">
            <option value="">全部网站</option>
            <option value="shopping">购物网站</option>
            <option value="video">视频网站</option>
            <option value="news">新闻网站</option>
          </select>
        </div>
        <div class="form-group">
          <label>位置标识</label>
          <input
            v-model="filters.positionKey"
            type="text"
            placeholder="请输入位置标识"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="form-group">
          <label>位置名称</label>
          <input
            v-model="filters.positionName"
            type="text"
            placeholder="请输入位置名称"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="form-actions">
          <button class="btn btn-secondary" @click="resetFilters">
            重置
          </button>
          <button class="btn btn-primary" @click="handleSearch">
            <i class="icon-search"></i>
            搜索
          </button>
        </div>
      </div>
    </div>

    <!-- 广告位卡片列表 -->
    <div class="positions-grid" v-loading="loading">
      <div
        class="position-card"
        v-for="position in positions"
        :key="position.id"
      >
        <div class="card-header">
          <h3 class="position-name">{{ position.positionName }}</h3>
          <span class="position-key">{{ position.positionKey }}</span>
        </div>
        <div class="card-body">
          <div class="position-info">
            <div class="info-item">
              <span class="info-label">所属网站:</span>
              <span class="info-value">{{ getWebsiteText(position.website) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">尺寸:</span>
              <span class="info-value">{{ position.width }} × {{ position.height }} px</span>
            </div>
            <div class="info-item">
              <span class="info-label">描述:</span>
              <span class="info-value desc">{{ position.description || '无描述' }}</span>
            </div>
          </div>
        </div>
        <div class="card-footer">
          <button class="btn btn-sm btn-outline" @click="editPosition(position)">
            编辑
          </button>
          <button class="btn btn-sm btn-danger" @click="deletePosition(position.id)">
            删除
          </button>
        </div>
      </div>

      <div v-if="positions.length === 0 && !loading" class="empty-state">
        <div class="empty-icon">📋</div>
        <p>暂无广告位数据</p>
        <button class="btn btn-primary" @click="showCreateModal = true">
          <i class="icon-plus"></i>
          新建广告位
        </button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
      <button
        class="btn btn-pagination"
        :disabled="currentPage === 1 || loading"
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="page-info">
        第 {{ currentPage }} 页，共 {{ totalPages }} 页
      </span>
      <button
        class="btn btn-pagination"
        :disabled="currentPage === totalPages || loading"
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 创建/编辑弹窗 -->
    <div v-if="showCreateModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ showCreateModal ? '新建广告位' : '编辑广告位' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="savePosition">
            <div class="form-group">
              <label>网站 *</label>
              <select v-model="currentPosition.website" required>
                <option value="">请选择网站</option>
                <option value="shopping">购物网站</option>
                <option value="video">视频网站</option>
                <option value="news">新闻网站</option>
              </select>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>位置标识 *</label>
                <input
                  v-model="currentPosition.positionKey"
                  type="text"
                  required
                  placeholder="请输入位置标识"
                />
              </div>
              <div class="form-group">
                <label>位置名称 *</label>
                <input
                  v-model="currentPosition.positionName"
                  type="text"
                  required
                  placeholder="请输入位置名称"
                />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>宽度 (像素) *</label>
                <input
                  v-model.number="currentPosition.width"
                  type="number"
                  min="1"
                  required
                />
              </div>
              <div class="form-group">
                <label>高度 (像素) *</label>
                <input
                  v-model.number="currentPosition.height"
                  type="number"
                  min="1"
                  required
                />
              </div>
            </div>
            <div class="form-group">
              <label>描述</label>
              <textarea
                v-model="currentPosition.description"
                rows="3"
                placeholder="请输入广告位描述">
              </textarea>
            </div>
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">
                取消
              </button>
              <button type="submit" class="btn btn-primary" :disabled="saving">
                {{ saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import apiService from '@/services/apiService'

const positions = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const totalElements = ref(0)
const showCreateModal = ref(false)
const showEditModal = ref(false)
const loading = ref(false)
const saving = ref(false)

const filters = ref({
  website: '',
  positionKey: '',
  positionName: ''
})

const currentPosition = ref({
  id: null,
  website: '',
  positionKey: '',
  positionName: '',
  width: null,
  height: null,
  description: ''
})

// 页面挂载时加载数据
onMounted(() => {
  loadPositions()
})

// 加载广告位数据
const loadPositions = async () => {
  try {
    loading.value = true
    const response = await apiService.get('/positions', {
      params: {
        page: currentPage.value - 1, // 后端分页从0开始
        size: pageSize.value,
        website: filters.value.website || null,
        positionKey: filters.value.positionKey || null,
        positionName: filters.value.positionName || null
      }
    })

    // 处理分页数据
    if (response.data.content) {
      // 后端返回的是分页数据结构
      positions.value = response.data.content
      totalPages.value = response.data.totalPages || 1
      totalElements.value = response.data.totalElements || response.data.content.length
    } else {
      // 后端返回的是数组
      positions.value = response.data
      totalPages.value = 1
      totalElements.value = response.data.length
    }
  } catch (error) {
    console.error('获取广告位列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1
  loadPositions()
}

// 重置筛选条件
const resetFilters = () => {
  filters.value = {
    website: '',
    positionKey: '',
    positionName: ''
  }
  handleSearch()
}

// 分页功能
const changePage = (page) => {
  currentPage.value = page
  loadPositions()
}

// 编辑广告位
const editPosition = (position) => {
  currentPosition.value = { ...position }
  showEditModal.value = true
}

// 删除广告位
const deletePosition = async (id) => {
  if (confirm('确定要删除这个广告位吗？')) {
    try {
      await apiService.delete(`/positions/${id}`)
      loadPositions() // 重新加载数据
    } catch (error) {
      console.error('删除广告位失败:', error)
      alert('删除广告位失败: ' + (error.response?.data?.message || error.message))
    }
  }
}

// 关闭模态框
const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  // 重置表单
  currentPosition.value = {
    id: null,
    website: '',
    positionKey: '',
    positionName: '',
    width: null,
    height: null,
    description: ''
  }
}

// 保存广告位
const savePosition = async () => {
  try {
    saving.value = true
    if (showCreateModal.value) {
      // 创建新广告位
      await apiService.post('/positions', currentPosition.value)
    } else {
      // 更新广告位
      await apiService.put(`/positions/${currentPosition.value.id}`, currentPosition.value)
    }
    closeModal()
    loadPositions() // 重新加载数据
  } catch (error) {
    console.error('保存广告位失败:', error)
    alert('保存广告位失败: ' + (error.response?.data?.message || error.message))
  } finally {
    saving.value = false
  }
}

// 获取网站文本描述
const getWebsiteText = (website) => {
  const websiteMap = {
    'shopping': '购物网站',
    'video': '视频网站',
    'news': '新闻网站'
  }
  return websiteMap[website] || website
}
</script>

<style scoped>
.positions-page {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
  color: #333;
}

.icon-plus::before {
  content: "+";
  margin-right: 5px;
}

.icon-search::before {
  content: "🔍";
  margin-right: 5px;
}

.card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
  font-size: 0.9rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.25);
}

.form-row {
  display: flex;
  gap: 1rem;
}

.form-row .form-group {
  flex: 1;
}

.btn {
  display: inline-flex;
  align-items: center;
  padding: 0.75rem 1.25rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 123, 255, 0.3);
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(108, 117, 125, 0.3);
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #bd2130;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(220, 53, 69, 0.3);
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #007bff;
  color: #007bff;
}

.btn-outline:hover {
  background-color: #007bff;
  color: white;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.btn-pagination {
  padding: 0.5rem 1rem;
}

.btn-pagination:disabled {
  background-color: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.form-actions {
  display: flex;
  gap: 0.5rem;
  align-self: flex-end;
  padding-bottom: 0.05rem;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-top: 2rem;
}

.page-info {
  font-weight: 500;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
  grid-column: 1 / -1;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 100%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.modal-close:hover {
  background-color: #f8f9fa;
}

.modal-body {
  padding: 1.5rem;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  margin-top: 2rem;
}

/* 新增的卡片样式 */
.positions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.position-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.position-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 1.25rem 1.25rem 0;
  border-bottom: 1px solid #eee;
}

.position-name {
  margin: 0 0 0.5rem 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #333;
}

.position-key {
  background-color: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.card-body {
  padding: 1.25rem;
}

.position-info {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.info-item {
  display: flex;
}

.info-label {
  font-weight: 500;
  color: #555;
  width: 80px;
  flex-shrink: 0;
}

.info-value {
  color: #333;
  flex: 1;
}

.info-value.desc {
  color: #6c757d;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
  padding: 0.75rem 1.25rem;
  background-color: #f8f9fa;
  border-top: 1px solid #eee;
}
</style>
