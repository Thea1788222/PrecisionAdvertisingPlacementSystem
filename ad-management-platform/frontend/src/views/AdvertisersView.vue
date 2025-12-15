<template>
  <div class="advertisers-page">
    <div class="page-header">
      <h1>广告商管理</h1>
      <button class="btn btn-primary" @click="showCreateModal = true">
        新建广告商
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="form-group">
          <label>名称</label>
          <input
            v-model="filters.name"
            type="text"
            placeholder="请输入广告商名称"
          />
        </div>
        <div class="form-group">
          <button class="btn btn-secondary" @click="handleSearch">
            搜索
          </button>
        </div>
      </div>
    </div>

    <!-- 广告商列表 -->
    <div class="table-container">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>名称</th>
            <th>联系人</th>
            <th>邮箱</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="advertiser in advertisers" :key="advertiser.id">
            <td>{{ advertiser.id }}</td>
            <td>{{ advertiser.name }}</td>
            <td>{{ advertiser.contact }}</td>
            <td>{{ advertiser.email }}</td>
            <td>
              <span :class="['status-badge', { 'status-active': advertiser.status === 1 }]">
                {{ advertiser.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>
              <button class="btn btn-sm btn-outline" @click="editAdvertiser(advertiser)">
                编辑
              </button>
              <button class="btn btn-sm btn-danger" @click="deleteAdvertiser(advertiser.id)">
                删除
              </button>
            </td>
          </tr>
          <tr v-if="advertisers.length === 0">
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
          <h3>{{ showCreateModal ? '新建广告商' : '编辑广告商' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveAdvertiser">
            <div class="form-group">
              <label>名称 *</label>
              <input
                v-model="currentAdvertiser.name"
                type="text"
                required
                placeholder="请输入广告商名称"
              />
            </div>
            <div class="form-group">
              <label>联系人 *</label>
              <input
                v-model="currentAdvertiser.contact"
                type="text"
                required
                placeholder="请输入联系人"
              />
            </div>
            <div class="form-group">
              <label>邮箱 *</label>
              <input
                v-model="currentAdvertiser.email"
                type="email"
                required
                placeholder="请输入邮箱"
              />
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="currentAdvertiser.status">
                <option value="1">启用</option>
                <option value="0">禁用</option>
              </select>
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

const advertisers = ref([])
const currentPage = ref(1)
const totalPages = ref(1)
const showCreateModal = ref(false)
const showEditModal = ref(false)

const filters = ref({
  name: ''
})

const currentAdvertiser = ref({
  id: null,
  name: '',
  contact: '',
  email: '',
  status: 1
})

// 模拟数据
onMounted(() => {
  loadAdvertisers()
})

const loadAdvertisers = () => {
  // 模拟 API 调用
  advertisers.value = [
    {
      id: 1,
      name: '科技数码有限公司',
      contact: '张经理',
      email: 'zhang@tech.com',
      status: 1
    },
    {
      id: 2,
      name: '时尚服装集团',
      contact: '李总监',
      email: 'li@fashion.com',
      status: 1
    }
  ]
  totalPages.value = 1
}

const handleSearch = () => {
  currentPage.value = 1
  loadAdvertisers()
}

const changePage = (page) => {
  currentPage.value = page
  loadAdvertisers()
}

const editAdvertiser = (advertiser) => {
  currentAdvertiser.value = { ...advertiser }
  showEditModal.value = true
}

const deleteAdvertiser = (id) => {
  if (confirm('确定要删除这个广告商吗？')) {
    // 模拟删除操作
    advertisers.value = advertisers.value.filter(a => a.id !== id)
  }
}

const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
}

const saveAdvertiser = () => {
  // 模拟保存操作
  if (showCreateModal.value) {
    // 创建新广告商
    const newAdvertiser = {
      id: advertisers.value.length + 1,
      ...currentAdvertiser.value
    }
    advertisers.value.push(newAdvertiser)
  } else {
    // 更新广告商
    const index = advertisers.value.findIndex(a => a.id === currentAdvertiser.value.id)
    if (index !== -1) {
      advertisers.value[index] = { ...currentAdvertiser.value }
    }
  }
  
  closeModal()
}
</script>

<style scoped>
.advertisers-page {
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
.form-group select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
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

.status-badge {
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.875rem;
  background-color: #f8f9fa;
  color: #6c757d;
}

.status-active {
  background-color: #d4edda;
  color: #155724;
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