<template>
  <div class="materials-page">
    <div class="page-header">
      <h1>广告素材管理</h1>
      <button class="btn btn-primary" @click="openCreateModal">
        新建素材
      </button>
    </div>

    <!-- 筛选条件 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="form-group">
          <label>广告商</label>
          <select v-model="filters.advertiserId">
            <option value="">全部广告商</option>
            <option v-for="advertiser in advertisers" :key="advertiser.id" :value="advertiser.id">
              {{ advertiser.name }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>类型</label>
          <select v-model="filters.type">
            <option value="">全部类型</option>
            <option value="banner">图片</option>
            <option value="video">视频</option>
            <option value="native">原生</option>
          </select>
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="filters.category">
            <option value="">全部分类</option>
            <option value="electronics">数码电子</option>
            <option value="fashion">时尚</option>
            <option value="sports">运动</option>
            <option value="home">家居</option>
            <option value="food">美食</option>
          </select>
        </div>
        <div class="form-group">
          <label>状态</label>
          <select v-model="filters.status">
            <option value="">全部状态</option>
            <option value="1">启用</option>
            <option value="0">禁用</option>
          </select>
        </div>
        <div class="form-group">
          <label>关键词</label>
          <input 
            v-model="filters.keyword" 
            type="text" 
            placeholder="请输入关键词"
            @keyup.enter="handleSearch"
          />
        </div>
        <div class="form-group">
          <button class="btn btn-secondary" @click="handleSearch">
            搜索
          </button>
        </div>
      </div>
    </div>

    <!-- 素材卡片列表 -->
    <div class="materials-grid" v-loading="loading">
      <div 
        class="material-card" 
        v-for="material in materials" 
        :key="material.id"
        @click="viewMaterialDetail(material)"
      >
        <div class="card-preview">
          <div v-if="material.type === 'banner' && material.imageUrl" class="image-preview">
            <img :src="material.imageUrl" :alt="material.title" />
          </div>
          <div v-else-if="material.type === 'video' && material.videoUrl" class="video-preview">
            <video :src="material.videoUrl" muted></video>
            <div class="play-icon">▶</div>
          </div>
          <div v-else class="no-preview">
            <span>{{ getMaterialTypeText(material.type) }}</span>
          </div>
        </div>
        <div class="card-content">
          <h3 class="material-title">{{ material.title }}</h3>
          <div class="material-meta">
            <span class="meta-item">
              <i class="icon-advertiser"></i>
              {{ getAdvertiserName(material.advertiserId) }}
            </span>
            <span class="meta-item">
              <i class="icon-category"></i>
              {{ getCategoryText(material.category) }}
            </span>
          </div>
          <div class="material-footer">
            <span :class="['status-badge', { 'status-active': material.status === 1 }]">
              {{ material.status === 1 ? '启用' : '禁用' }}
            </span>
            <span class="bid-price">¥{{ material.bidPrice }}</span>
          </div>
        </div>
      </div>
      
      <div v-if="materials.length === 0 && !loading" class="empty-state">
        <p>暂无广告素材</p>
        <button class="btn btn-primary" @click="openCreateModal">新建素材</button>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination" v-if="totalPages > 1">
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
          <h3>{{ showCreateModal ? '新建广告素材' : '编辑广告素材' }}</h3>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveMaterial">
            <!-- 文件上传区域 -->
            <div class="form-group">
              <label>素材文件 *</label>
              <div 
                class="upload-area" 
                :class="{ 'drag-over': isDragOver, 'has-file': hasFile }"
                @dragover.prevent="handleDragOver"
                @dragleave.prevent="handleDragLeave"
                @drop.prevent="handleDrop"
                @click="triggerFileInput"
              >
                <input 
                  ref="fileInput" 
                  type="file" 
                  accept="image/*,video/*" 
                  @change="handleFileChange" 
                  class="file-input"
                />
                <div v-if="!hasFile" class="upload-placeholder">
                  <div class="upload-icon">📁</div>
                  <p>点击选择文件或拖拽文件到此处</p>
                  <p class="upload-hint">支持图片和视频文件</p>
                </div>
                <div v-else class="file-preview">
                  <div v-if="currentFileType === 'image'" class="image-preview-thumb">
                    <img :src="currentFilePreview" alt="预览" />
                  </div>
                  <div v-else-if="currentFileType === 'video'" class="video-preview-thumb">
                    <video :src="currentFilePreview" muted></video>
                    <div class="play-icon">▶</div>
                  </div>
                  <div class="file-info">
                    <p class="file-name">{{ currentFileName }}</p>
                    <p class="file-size">{{ formatFileSize(currentFileSize) }}</p>
                  </div>
                  <button type="button" class="btn-remove-file" @click.stop="removeFile">×</button>
                </div>
              </div>
              <div class="upload-progress" v-if="uploadProgress > 0 && uploadProgress < 100">
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
                </div>
                <span class="progress-text">{{ uploadProgress }}%</span>
              </div>
            </div>

            <div class="form-group">
              <label>标题 *</label>
              <input
                v-model="currentMaterial.title"
                type="text"
                required
                placeholder="请输入广告标题"
              />
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>广告商 *</label>
                <select v-model="currentMaterial.advertiserId" required>
                  <option value="">请选择广告商</option>
                  <option v-for="advertiser in advertisers" :key="advertiser.id" :value="advertiser.id">
                    {{ advertiser.name }}
                  </option>
                </select>
              </div>
              
              <div class="form-group">
                <label>分类 *</label>
                <select v-model="currentMaterial.category" required>
                  <option value="">请选择分类</option>
                  <option value="electronics">数码电子</option>
                  <option value="fashion">时尚</option>
                  <option value="sports">运动</option>
                  <option value="home">家居</option>
                  <option value="food">美食</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-group">
                <label>出价 (元) *</label>
                <input
                  v-model.number="currentMaterial.bidPrice"
                  type="number"
                  step="0.01"
                  min="0"
                  required
                />
              </div>
              <div class="form-group">
                <label>状态</label>
                <select v-model="currentMaterial.status">
                  <option value="1">启用</option>
                  <option value="0">禁用</option>
                </select>
              </div>
            </div>
            
            <div class="form-group">
              <label>链接地址</label>
              <input
                v-model="currentMaterial.linkUrl"
                type="url"
                placeholder="https://example.com"
              />
            </div>
            
            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal">
                取消
              </button>
              <button 
                type="submit" 
                class="btn btn-primary"
                :disabled="isUploading"
              >
                {{ isUploading ? '上传中...' : '保存' }}
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

const materials = ref([])
const advertisers = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)
const totalPages = ref(1)
const totalElements = ref(0)
const showCreateModal = ref(false)
const showEditModal = ref(false)

// 文件上传相关
const fileInput = ref(null)
const isDragOver = ref(false)
const hasFile = ref(false)
const currentFile = ref(null)
const currentFileName = ref('')
const currentFileSize = ref(0)
const currentFileType = ref('')
const currentFilePreview = ref('')
const isUploading = ref(false)
const uploadProgress = ref(0)

const filters = ref({
  advertiserId: '',
  type: '',
  category: '',
  status: '',
  keyword: ''
})

const currentMaterial = ref({
  id: null,
  title: '',
  type: '',
  imageUrl: '',
  videoUrl: '',
  linkUrl: '',
  advertiserId: '',
  bidPrice: 0,
  category: '',
  status: 1
})

// 初始化
onMounted(() => {
  loadMaterials()
  loadAdvertisers()
})

// 获取广告素材列表
const loadMaterials = async () => {
  try {
    loading.value = true
    const response = await apiService.get('/materials', {
      params: {
        page: currentPage.value - 1, // 后端分页从0开始
        size: pageSize.value,
        advertiserId: filters.value.advertiserId || null,
        type: filters.value.type || null,
        category: filters.value.category || null,
        status: filters.value.status || null,
        keyword: filters.value.keyword || null
      }
    })
    
    materials.value = response.data.content || response.data
    if (response.data.totalPages !== undefined) {
      totalPages.value = response.data.totalPages
      totalElements.value = response.data.totalElements
    } else {
      totalPages.value = 1
      totalElements.value = materials.value.length
    }
  } catch (error) {
    console.error('获取广告素材失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取广告商列表
const loadAdvertisers = async () => {
  try {
    const response = await apiService.get('/advertisers', {
      params: {
        page: 0,
        size: 100 // 获取所有广告商
      }
    })
    advertisers.value = response.data.content || response.data
  } catch (error) {
    console.error('获取广告商列表失败:', error)
  }
}

// 搜索功能
const handleSearch = () => {
  currentPage.value = 1
  loadMaterials()
}

// 分页功能
const changePage = (page) => {
  currentPage.value = page
  loadMaterials()
}

// 打开创建模态框
const openCreateModal = () => {
  resetForm()
  showCreateModal.value = true
}

// 编辑广告素材
const editMaterial = (material) => {
  currentMaterial.value = { ...material }
  // 根据素材类型设置预览
  if (material.type === 'banner' && material.imageUrl) {
    currentFilePreview.value = material.imageUrl
    currentFileType.value = 'image'
    hasFile.value = true
  } else if (material.type === 'video' && material.videoUrl) {
    currentFilePreview.value = material.videoUrl
    currentFileType.value = 'video'
    hasFile.value = true
  }
  showEditModal.value = true
}

// 查看广告素材详情
const viewMaterialDetail = (material) => {
  editMaterial(material)
}

// 删除广告素材
const deleteMaterial = async (id) => {
  if (confirm('确定要删除这个广告素材吗？')) {
    try {
      await apiService.delete(`/materials/${id}`)
      // 重新加载数据
      loadMaterials()
    } catch (error) {
      console.error('删除广告素材失败:', error)
      alert('删除失败: ' + (error.response?.data?.message || '未知错误'))
    }
  }
}

// 关闭模态框
const closeModal = () => {
  showCreateModal.value = false
  showEditModal.value = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  currentMaterial.value = {
    id: null,
    title: '',
    type: '',
    imageUrl: '',
    videoUrl: '',
    linkUrl: '',
    advertiserId: '',
    bidPrice: 0,
    category: '',
    status: 1
  }
  hasFile.value = false
  currentFile.value = null
  currentFileName.value = ''
  currentFileSize.value = 0
  currentFileType.value = ''
  currentFilePreview.value = ''
  uploadProgress.value = 0
  isUploading.value = false
}

// 保存广告素材
const saveMaterial = async () => {
  try {
    // 模拟文件上传（实际应该上传到后端，然后由后端上传到阿里云OSS）
    if (hasFile.value && currentFile.value) {
      // 模拟上传过程
      isUploading.value = true
      uploadProgress.value = 0
      
      // 模拟上传进度
      const interval = setInterval(() => {
        uploadProgress.value += 10
        if (uploadProgress.value >= 100) {
          clearInterval(interval)
          isUploading.value = false
        }
      }, 100)
      
      // 等待模拟上传完成
      await new Promise(resolve => setTimeout(resolve, 1000))
      
      // 设置模拟的URL
      if (currentFileType.value === 'image') {
        currentMaterial.value.imageUrl = currentFilePreview.value
        currentMaterial.value.type = 'banner'
      } else if (currentFileType.value === 'video') {
        currentMaterial.value.videoUrl = currentFilePreview.value
        currentMaterial.value.type = 'video'
      }
    }
    
    if (showCreateModal.value) {
      // 创建新素材
      const response = await apiService.post('/materials', currentMaterial.value)
      materials.value.push(response.data)
    } else {
      // 更新素材
      const response = await apiService.put(`/materials/${currentMaterial.value.id}`, currentMaterial.value)
      const index = materials.value.findIndex(m => m.id === currentMaterial.value.id)
      if (index !== -1) {
        materials.value[index] = response.data
      }
    }
    
    closeModal()
  } catch (error) {
    console.error('保存广告素材失败:', error)
    alert('保存失败: ' + (error.response?.data?.message || '未知错误'))
  }
}

// 文件上传相关方法
const triggerFileInput = () => {
  if (!isUploading.value) {
    fileInput.value.click()
  }
}

const handleDragOver = () => {
  if (!isUploading.value) {
    isDragOver.value = true
  }
}

const handleDragLeave = () => {
  isDragOver.value = false
}

const handleDrop = (e) => {
  if (!isUploading.value) {
    isDragOver.value = false
    const files = e.dataTransfer.files
    if (files.length > 0) {
      processFile(files[0])
    }
  }
}

const handleFileChange = (e) => {
  const files = e.target.files
  if (files.length > 0) {
    processFile(files[0])
  }
}

const processFile = (file) => {
  // 检查文件类型
  if (!file.type.match('image.*') && !file.type.match('video.*')) {
    alert('请选择图片或视频文件')
    return
  }

  // 检查文件大小 (限制为10MB)
  if (file.size > 10 * 1024 * 1024) {
    alert('文件大小不能超过10MB')
    return
  }

  currentFile.value = file
  currentFileName.value = file.name
  currentFileSize.value = file.size
  
  // 设置文件类型
  if (file.type.startsWith('image/')) {
    currentFileType.value = 'image'
  } else if (file.type.startsWith('video/')) {
    currentFileType.value = 'video'
  }
  
  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    currentFilePreview.value = e.target.result
    hasFile.value = true
  }
  reader.readAsDataURL(file)
}

const removeFile = () => {
  hasFile.value = false
  currentFile.value = null
  currentFileName.value = ''
  currentFileSize.value = 0
  currentFileType.value = ''
  currentFilePreview.value = ''
  currentMaterial.value.type = ''
  currentMaterial.value.imageUrl = ''
  currentMaterial.value.videoUrl = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 工具方法
const getAdvertiserName = (id) => {
  const advertiser = advertisers.value.find(item => item.id === id)
  return advertiser ? advertiser.name : '未知广告商'
}

const getCategoryText = (category) => {
  const categories = {
    electronics: '数码电子',
    fashion: '时尚',
    sports: '运动',
    home: '家居',
    food: '美食'
  }
  return categories[category] || category
}

const getMaterialTypeText = (type) => {
  const types = {
    banner: '图片',
    video: '视频',
    native: '原生'
  }
  return types[type] || type
}
</script>

<style scoped>
.materials-page {
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
  grid-column: 1 / -1;
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

/* 新增的样式 */

.materials-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.material-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.material-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.card-preview {
  height: 180px;
  overflow: hidden;
  position: relative;
  background-color: #f8f9fa;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-preview {
  position: relative;
  width: 100%;
  height: 100%;
}

.video-preview video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 2rem;
  color: white;
  background-color: rgba(0, 0, 0, 0.7);
  width: 50px;
  height: 50px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.no-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #6c757d;
  font-size: 0.875rem;
}

.card-content {
  padding: 1rem;
}

.material-title {
  margin: 0 0 0.5rem 0;
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.material-meta {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  color: #6c757d;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.material-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.bid-price {
  font-weight: 600;
  color: #28a745;
}

.upload-area {
  border: 2px dashed #ddd;
  border-radius: 4px;
  padding: 2rem;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.upload-area:hover,
.upload-area.drag-over {
  border-color: #007bff;
  background-color: #f8f9ff;
}

.upload-area.has-file {
  padding: 1rem;
  text-align: left;
}

.file-input {
  display: none;
}

.upload-placeholder .upload-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.upload-placeholder p {
  margin: 0 0 0.5rem 0;
}

.upload-hint {
  font-size: 0.875rem;
  color: #6c757d;
}

.file-preview {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.image-preview-thumb {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
}

.image-preview-thumb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-preview-thumb {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.video-preview-thumb video {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-preview-thumb .play-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 1rem;
  color: white;
  background-color: rgba(0, 0, 0, 0.7);
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.file-info {
  flex: 1;
}

.file-name {
  margin: 0 0 0.25rem 0;
  font-weight: 500;
}

.file-size {
  margin: 0;
  font-size: 0.875rem;
  color: #6c757d;
}

.btn-remove-file {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #dc3545;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-progress {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-top: 1rem;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background-color: #eee;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #007bff;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.875rem;
  color: #6c757d;
  min-width: 3rem;
  text-align: right;
}

@media (max-width: 768px) {
  .materials-grid {
    grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  }
  
  .form-row {
    flex-direction: column;
    gap: 0;
  }
}
</style>