<template>
  <div>
    <h1>{{ video.title }}</h1>
    <div class="video-container">
      <!-- 主视频 -->
      <video
        ref="videoPlayer"
        width="640"
        height="360"
        controls
        @play="handleVideoPlay"
        v-if="video.playUrl"
      >
        <source :src="video.playUrl" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>

      <!-- 广告覆盖层 -->
      <AdOverlay
        v-if="showAd"
        :showAd="showAd"
        :currentAd="currentAd"
        :adTitle="adTitle"
        :adCountdown="adCountdown"
        :isVideoAd="isVideoAd"
        :isImageAd="isImageAd"
        :canSkipAd="canSkipAd"
        @ad-ended="handleAdEndedInternal"
        @ad-click="trackAdClick"
        @ad-skip="skipAdInternal"
        ref="adOverlayRef"
      />
    </div>
    <p>{{ video.description }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useVideoPlayer } from '@/composables/useVideoPlayer'
import { useAdManager } from '@/composables/useAdManager'
import { useAdTracker } from '@/composables/useAdTracker'
import AdOverlay from '@/components/AdOverlay.vue'

const route = useRoute()

// 视频播放器逻辑
const { 
  video, 
  videoPlayer, 
  loadVideo, 
  setupVideoListeners, 
  setMidRollAdListener 
} = useVideoPlayer()

// 广告管理
const {
  showAd,
  currentAd,
  adTitle,
  adCountdown,
  isVideoAd,
  isImageAd,
  canSkipAd,
  preRollPlayed,
  playedMidRollPositions,
  playPreRollAd,
  playMidRollAdAt,
  skipAd,
  handleAdEnded,
  trackAdClickReal
} = useAdManager()

const { initAdTracker } = useAdTracker()

const adOverlayRef = ref(null)

// 播放前贴片广告
const handleVideoPlay = async () => {
  if (!preRollPlayed.value) {
    const adVideo = adOverlayRef.value?.adVideo || null
    await playPreRollAd(adVideo, videoPlayer.value)
  }
}

// 设置中插广告监听器
const handleMidRollAd = async (positionIndex) => {
  const adVideo = adOverlayRef.value?.adVideo || null
  await playMidRollAdAt(positionIndex, adVideo, videoPlayer.value)
}

// 跳过广告
const skipAdInternal = (adVideoRef) => {
  skipAd(adVideoRef, videoPlayer.value)
}

// 广告结束处理
const handleAdEndedInternal = (adVideoRef) => {
  handleAdEnded(adVideoRef, videoPlayer.value)
}

// 广告点击处理
const trackAdClick = () => {
  trackAdClickReal()
}

// 组件挂载
onMounted(async () => {
  const videoId = route.params.id
  await loadVideo(videoId)

  // 初始化广告SDK
  initAdTracker()

  // 设置中插广告监听器
  setMidRollAdListener(handleMidRollAd)

  // 等待视频元素渲染完成后设置监听器
  await nextTick()
  setupVideoListeners()
})
</script>

<style scoped>
.video-container {
  position: relative;
  display: inline-block;
}
</style>
