<template>
  <div v-if="showAd" class="ad-overlay">
    <div class="ad-content">
      <h3>{{ adTitle }}</h3>
      <div class="ad-countdown" v-if="adCountdown > 0">
        <span v-if="isVideoAd && !canSkipAd">广告将在 {{ adCountdown }} 秒后可跳过</span>
        <span v-else-if="isVideoAd && canSkipAd">视频广告播放中，可点击跳过</span>
        <span v-else-if="isImageAd">图片广告将在 {{ adCountdown }} 秒后关闭</span>
      </div>

      <video v-if="isVideoAd" ref="adVideo" width="640" height="360" controls @ended="handleAdEnded">
        <source :src="currentAd.playUrl" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>

      <img v-else-if="isImageAd" :src="currentAd.imageUrl" :alt="currentAd.title" @click="trackAdClick" />

      <div class="ad-actions" v-if="isVideoAd">
        <button @click="skipAd" :disabled="!canSkipAd">
          {{ canSkipAd ? '跳过广告' : adCountdown + '秒后可跳过' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  showAd: { type: Boolean, required: true },
  currentAd: { type: Object, default: () => ({}) },
  adTitle: { type: String, default: '' },
  adCountdown: { type: Number, default: 0 },
  isVideoAd: { type: Boolean, default: false },
  isImageAd: { type: Boolean, default: false },
  canSkipAd: { type: Boolean, default: false }
})

const emit = defineEmits(['ad-ended', 'ad-click', 'ad-skip'])
const adVideo = ref(null)

const handleAdEnded = () => emit('ad-ended')
const trackAdClick = () => emit('ad-click')
const skipAd = () => emit('ad-skip', adVideo.value)

defineExpose({ adVideo })
</script>

<style scoped>
.ad-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}
.ad-content { text-align: center; color: #fff; }
.ad-actions { margin-top: 10px; }
</style>
