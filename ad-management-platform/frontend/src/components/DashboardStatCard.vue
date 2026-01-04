<template>
  <div class="stat-card" :class="{ 'clickable': clickable, 'loading': loading }" @click="handleClick">
    <h3>{{ title }}</h3>
    <p class="stat-number" v-if="!loading">{{ formattedValue }}</p>
    <el-skeleton :loading="loading" :count="1" :rows="1" animated>
      <template #template>
        <el-skeleton-item variant="h3" style="width: 80%; height: 2.5rem;" />
      </template>
    </el-skeleton>
    <div class="trend" v-if="trend !== undefined && !loading">
      <i :class="['el-icon', trend > 0 ? 'el-icon-caret-top' : 'el-icon-caret-bottom']" 
         :style="{ color: trend > 0 ? '#67c23a' : '#f56c6c' }"></i>
      {{ Math.abs(trend) }}%
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits, computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: [Number, String],
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  },
  clickable: {
    type: Boolean,
    default: true
  },
  trend: {
    type: Number, // 正数表示增长，负数表示下降
    default: undefined
  }
})

const emit = defineEmits(['click'])

const handleClick = () => {
  if (props.clickable) {
    emit('click')
  }
}

const formattedValue = computed(() => {
  if (typeof props.value === 'number') {
    // 如果数值较大，使用千分位分隔符
    return props.value.toLocaleString()
  }
  return props.value
})
</script>

<style scoped>
.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
  transition: all 0.3s ease;
  position: relative;
  min-height: 120px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover:not(.loading) {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.stat-card.loading {
  cursor: default;
}

.stat-card h3 {
  margin: 0 0 0.5rem 0;
  color: #7f8c8d;
  font-size: 1.1rem;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  word-break: break-all;
}

.trend {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  font-size: 0.9rem;
  margin-top: 0.5rem;
}

.trend i {
  margin-right: 0.25rem;
  font-size: 1rem;
}
</style>