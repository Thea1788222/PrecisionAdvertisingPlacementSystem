<template>
  <div class="positions-page">
    <div class="page-header">
      <h1>广告位管理</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">
        新建广告位
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
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
          <button class="btn btn-secondary" @click="handleSearch">
            搜索
          </button>
        </div>
      </div>
    </div>

    <!-- 广告位列表 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>网站</th>
            <th>位置名称</th>
            <th>尺寸</th>
            <th>描述</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="position in positions" :key="position.id">
            <td>{{ position.id }}</td>
            <td>{{ position.website }}</td>
            <td>{{ position.positionName }}</td>
            <td>{{ position.width }}×{{ position.height }}</td>
            <td>{{ position.description }}</td>
            <td>
              <button class="btn btn-sm btn-outline" @click="editPosition(position)">
                编辑
              </button>
              <button class="btn btn-sm btn-danger" @click="deletePosition(position.id)">
                删除
              </button>
            </td>
          </tr>
          <tr v-if="positions.length === 0">
            <td colspan="6" class="empty-state">
              暂无数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button
        class="btn btn-pagination"
        :disabled="currentPage === 1"
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="page-info">
        第 {{ currentPage }} 页，共 {{ totalPages }} 页
      </span>
      <button
        class="btn btn-pagination"
        :disabled="currentPage === totalPages"
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
            <div class="form-group">
              <label>位置名称 *</label>
              <input
                v-model="currentPosition.positionName"
                type="text"
                required
                placeholder="请输入位置名称"
              />
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
              <button type="submit" class="btn btn-primary">
                保存
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

const positions = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const showCreateModal = ref(false)
const showEditModal = ref(false)

const filters = ref({
  website: ''
})

const currentPosition = ref({
  id: null,
  website: '',
  positionName: '',
  width: 0,
  height: 0,
  description: ''
})

// 模拟数据
onMounted(() => {
  loadPositions()
})

const loadPositions = () => {
  // 模拟 API 调用
  positions.value = [
    {
      id: 1,
      website: '购物网站',
      positionName: '首页顶部横幅',
      width: 970,
      height: 90,
      description: '购物网站首页顶部横幅广告位'
    },
    {
      id: 2,
      website: '视频网站',
      positionName: '前贴片广告',
      width: 1920,
      height: 1080,
      description: '视频播放前的贴片广告'
    }
  ]
  totalPages.value = 1
}

const handleSearch = () => {
  currentPage.value = 1
  loadPositions()
}

const changePage = (page) => {
  currentPage.value = page
  loadPositions()
}

const editPosition = (position) => {
  currentPosition.value = { ...position }
  showEditModal.value = true
}

const deletePosition = (id) => {
  if (confirm('确定要删除这个广告位吗？')) {
    // 模拟删除操作
    positions.value = positions.value.filter(p => p.id !== id)
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
}

const savePosition = () => {
  // 模拟保存操作
  if (showCreateModal.value) {
    // 创建新广告位
    const newPosition = {
      id: positions.value.length + 1,
      ...currentPosition.value
    }
    positions.value.push(newPosition)
  } else {
    // 更新广告位
    const index = positions.value.findIndex(p => p.id === currentPosition.value.id)
    if (index !== -1) {
      positions.value[index] = { ...currentPosition.value }
    }
  }
  
  closeModal()
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

.filter-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 4px;
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
}

.form-group input,
.form-group select,
.form-group textarea {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s ease;
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
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #bd2130;
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
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}

.btn-pagination {
  padding: 0.5rem 1rem;
}

.btn-pagination:disabled {
  background-color: #ccc;
  cursor: not-allowed;
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
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
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

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}
</style>