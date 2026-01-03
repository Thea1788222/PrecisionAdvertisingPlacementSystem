<template>
  <div>
    <h1>{{ video.title }}</h1>
    <div class="video-container">
      <video ref="videoPlayer" width="640" height="360" controls v-if="video.playUrl">
        <source :src="video.playUrl" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>

      <!-- 广告覆盖层组件 -->
      <AdOverlay 
        v-if="showAd"
        :showAd="showAd"
        :currentAd="currentAd"
        :adTitle="adTitle"
        :adCountdown="adCountdown"
        :isVideoAd="isVideoAd"
        :isImageAd="isImageAd"
        @ad-ended="handleAdEnded"
        @ad-click="trackAdClick"
        @ad-skip="skipAd"
        ref="adOverlayRef"
      />
    </div>
    <p>{{ video.description }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import { useRoute } from 'vue-router'
import { useVideoPlayer } from '@/composables/useVideoPlayer'
import { useAdManager } from '@/composables/useAdManager'
import { useAdTracker } from '@/composables/useAdTracker'
import AdOverlay from '@/components/AdOverlay.vue'

const route = useRoute()

// 初始化各模块
const { 
  video, 
  videoPlayer, 
  isVideoReady,
  loadVideo,
  setTimeUpdateListener,
  setPlayListener,
  setMidRollAdListener,
  getShownAdPositions
} = useVideoPlayer()

const {
  showAd,
  currentAd,
  adTitle,
  adCountdown,
  preRollAdShown,
  isVideoAd,
  isImageAd,
  playPreRollAd,
  playMidRollAd,
  handleAdEnded: handleAdEndedInternal,
  skipAd,
  trackAdClickReal
} = useAdManager()

const { initAdTracker } = useAdTracker()

// 引用
const adOverlayRef = ref(null)

// 初始化广告SDK
const initAdSDK = () => {
  console.log('正在初始化广告追踪SDK...');
  initAdTracker();
};

// 事件处理函数
const handleAdEnded = () => {
  const adVideo = adOverlayRef.value?.adVideo
  handleAdEndedInternal(adVideo, videoPlayer)
}

const trackAdClick = () => {
  trackAdClickReal()
}

const skipAdInternal = () => {
  skipAd()
}

// 处理中插广告
const handleMidRollAd = async () => {
  const adVideo = adOverlayRef.value?.adVideo
  await playMidRollAd(adVideo, videoPlayer)
}

// 播放前贴片广告
const playPreRollAdInternal = async () => {
  const adVideo = adOverlayRef.value?.adVideo
  return await playPreRollAd(adVideo, videoPlayer)
}

// 组件挂载时初始化
onMounted(async () => {
  // 加载视频数据
  const id = route.params.id
  await loadVideo(id)

  // 初始化广告SDK
  initAdSDK()

  // 设置播放进度监听
  setTimeUpdateListener(() => {
    // 检查是否需要播放中插广告
    // 这个逻辑现在在useVideoPlayer中处理，由checkMidRollAd内部调用
  })

  // 设置播放事件监听，用于前贴片广告
  setPlayListener(async () => {
    // 如果前贴片广告未展示，则播放广告
    if (!preRollAdShown.value) {
      await playPreRollAdInternal()
    }
  })

  // 设置中插广告监听
  setMidRollAdListener(async () => {
    await handleMidRollAd()
  })
})

// 组件卸载前清理
onBeforeUnmount(() => {
  // 清理资源
  // useAdManager的cleanup会被自动调用
})
</script>

<style scoped>
.video-container {
  position: relative;
  display: inline-block;
}
</style>
