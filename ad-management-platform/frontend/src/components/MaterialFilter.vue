<template>
  <div class="filter-section">
    <div class="filter-row">
      <div class="form-group">
        <label>广告商</label>
        <select v-model="localFilters.advertiserId" @change="onFilterChange">
          <option value="">全部广告商</option>
          <option v-for="advertiser in advertisers" :key="advertiser.id" :value="advertiser.id">
            {{ advertiser.name }}
          </option>
        </select>
      </div>
      <div class="form-group">
        <label>类型</label>
        <select v-model="localFilters.type" @change="onFilterChange">
          <option value="">全部类型</option>
          <option value="banner">图片</option>
          <option value="video">视频</option>
          <option value="native">原生(图片)</option>
        </select>
      </div>
      <div class="form-group">
        <label>分类</label>
        <select v-model="localFilters.category" @change="onFilterChange">
          <option value="">全部分类</option>
          <option value="electronics">数码电子</option>
          <option value="fashion">时尚</option>
          <option value="sports">运动</option>
          <option value="home">家居</option>
          <option value="food">美食</option>
          <option value="travel">旅游</option>
          <option value="education">教育</option>
          <option value="finance">金融</option>
          <option value="health">健康</option>
          <option value="beauty">美妆</option>
        </select>
      </div>
      <div class="form-group">
        <label>状态</label>
        <select v-model="localFilters.status" @change="onFilterChange">
          <option value="">全部状态</option>
          <option value="1">启用</option>
          <option value="0">禁用</option>
        </select>
      </div>
      <div class="form-group">
        <label>关键词</label>
        <input
          v-model="localFilters.keyword"
          type="text"
          placeholder="请输入关键词"
          @keyup.enter="onSearch"
          @input="onKeywordInput"
        />
      </div>
      <div class="form-group">
        <button class="btn btn-secondary" @click="onSearch">
          搜索
        </button>
        <button class="btn btn-outline" @click="onReset">
          重置
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { debounce } from '@/utils/dashboardUtils'

const props = defineProps({
  filters: {
    type: Object,
    required: true
  },
  advertisers: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:filters', 'search', 'reset'])

// 本地过滤器状态
const localFilters = ref({ ...props.filters })

// 更新本地过滤器状态
watch(() => props.filters, (newFilters) => {
  localFilters.value = { ...newFilters }
}, { deep: true })

// 防抖搜索
const debouncedSearch = debounce(() => {
  emit('search')
}, 500)

const onFilterChange = () => {
  emit('update:filters', localFilters.value)
  emit('search')
}

const onKeywordInput = () => {
  // 对关键词输入进行防抖处理
  debouncedSearch()
}

const onSearch = () => {
  emit('search')
}

const onReset = () => {
  // 重置所有过滤器
  localFilters.value = {
    advertiserId: '',
    type: '',
    category: '',
    status: '',
    keyword: ''
  }
  emit('update:filters', localFilters.value)
  emit('reset')
}
</script>

<style scoped>
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

.btn-secondary {
  background-color: #6c757d;
  color: white;
  margin-right: 0.5rem;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #6c757d;
  color: #6c757d;
}

.btn-outline:hover {
  background-color: #6c757d;
  color: white;
}
</style>