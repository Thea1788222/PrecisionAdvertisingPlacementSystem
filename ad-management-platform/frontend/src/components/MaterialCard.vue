<template>
  <div class="material-card" :class="{ selected: isSelected }" @click="onViewDetail">
    <div class="card-preview">
      <div v-if="(material.type === 'banner' || material.type === 'native') && material.imageUrl" class="image-preview">
        <img :src="material.imageUrl" :alt="material.title" loading="lazy" />
      </div>
      <div v-else-if="material.type === 'video' && material.videoUrl" class="video-preview">
        <video :src="material.videoUrl" muted controls></video>
        <div class="play-icon">▶</div>
      </div>
      <div v-else class="no-preview">
        <span>{{ getMaterialTypeText(material.type) }}</span>
      </div>
    </div>
    <div class="card-content">
      <h3 class="material-title" :title="material.title">{{ material.title }}</h3>
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
    <div class="selection-checkbox" v-if="showCheckbox" @click.stop="toggleSelection">
      <input 
        type="checkbox" 
        :checked="isSelected"
        @click.stop
      />
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  material: {
    type: Object,
    required: true
  },
  advertisers: {
    type: Array,
    default: () => []
  },
  showCheckbox: {
    type: Boolean,
    default: false
  },
  isSelected: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view-detail', 'toggle-selection'])

const onViewDetail = () => {
  emit('view-detail', props.material)
}

const toggleSelection = () => {
  emit('toggle-selection', props.material.id)
}

const getAdvertiserName = (id) => {
  if (!id) return '未知广告商'
  const advertiser = props.advertisers.find(item => item.id === id)
  return advertiser ? advertiser.name : '未知广告商'
}

const getCategoryText = (category) => {
  const categories = {
    electronics: '数码电子',
    fashion: '时尚',
    sports: '运动',
    home: '家居',
    food: '美食',
    travel: '旅游',
    education: '教育',
    finance: '金融',
    health: '健康',
    beauty: '美妆'
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
.material-card {
  border: 1px solid #eee;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  background: white;
  position: relative;
}

.material-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.material-card.selected {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
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

.selection-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
  background: white;
  border-radius: 4px;
  padding: 2px;
}
</style>