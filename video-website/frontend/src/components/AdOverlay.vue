<template>
  <div v-if="showAd" class="ad-overlay">
    <div class="ad-content">
      <h3>{{ adTitle }}</h3>
      <div class="ad-countdown" v-if="adCountdown > 0">广告将在 {{ adCountdown }} 秒后关闭</div>
      
      <!-- 视频广告 -->
      <div v-if="isVideoAd" class="ad-video">
        <video ref="adVideo" width="640" height="360" controls @ended="handleAdEnded">
          <source :src="currentAd.playUrl" type="video/mp4" />
          您的浏览器不支持视频播放。
        </video>
      </div>
      
      <!-- 图片广告 -->
      <div v-else-if="isImageAd" class="ad-image-container">
        <img :src="currentAd.imageUrl" :alt="currentAd.title" class="ad-image" @click="trackAdClick" />
      </div>
      
      <div class="ad-info">
        <p>{{ currentAd.title }}</p>
        <button @click="trackAdClick" class="ad-click-btn">了解详情</button>
        <button @click="skipAd" class="ad-skip-btn" :disabled="adCountdown > 0">跳过广告</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  showAd: {
    type: Boolean,
    required: true
  },
  currentAd: {
    type: Object,
    default: () => ({})
  },
  adTitle: {
    type: String,
    default: ''
  },
  adCountdown: {
    type: Number,
    default: 0
  },
  isVideoAd: {
    type: Boolean,
    default: false
  },
  isImageAd: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['ad-ended', 'ad-click', 'ad-skip'])

// 引用
const adVideo = ref(null)

// 事件处理
const handleAdEnded = () => {
  emit('ad-ended')
}

const trackAdClick = () => {
  emit('ad-click')
}

const skipAd = () => {
  emit('ad-skip')
}

// 暴露视频引用给父组件
defineExpose({
  adVideo
})
</script>

<style src="../styles/AdOverlay.css" scoped></style>