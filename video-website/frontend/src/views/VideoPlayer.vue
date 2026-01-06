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
        ref="adOverlayRef"
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

// ------------------------
// 视频播放器逻辑
// ------------------------
const {
  video,
  videoPlayer,
  loadVideo,
  setupVideoListeners,
  setMidRollAdListener,
  setAdPlayingStatus
} = useVideoPlayer()

// ------------------------
// 广告管理
// ------------------------
const {
  showAd,
  currentAd,
  adTitle,
  adCountdown,
  isVideoAd,
  isImageAd,
  canSkipAd,
  preRollPlayed,
  playPreRollAd,
  playMidRollAdAt,
  skipAd,
  handleAdEnded,
  trackAdClickReal,
  resetAdState
} = useAdManager()

const { initAdTracker } = useAdTracker()
const adOverlayRef = ref(null)

// ------------------------
// 播放前贴片广告
// ------------------------
const handleVideoPlay = async () => {
  if (!preRollPlayed.value) {
    const ad = await playPreRollAd(videoPlayer.value)
    if (ad && adOverlayRef.value) {
      await nextTick()
      await adOverlayRef.value.playVideoAd(ad)
    }
  }
}

// ------------------------
// 中插广告监听
// ------------------------
const handleMidRollAd = async (positionIndex) => {
  console.log(`[handleMidRollAd] 收到中插广告回调, positionIndex: ${positionIndex}`)
  console.log(`[handleMidRollAd] videoPlayer存在: ${!!videoPlayer.value}, adOverlayRef存在: ${!!adOverlayRef.value}`)
  
  if (!videoPlayer.value) {
    console.log(`[handleMidRollAd] videoPlayer不存在，提前返回`)
    return
  }
  
  setAdPlayingStatus(true)
  console.log(`[handleMidRollAd] 调用 playMidRollAdAt`)
  const ad = await playMidRollAdAt(positionIndex, videoPlayer.value)
  console.log(`[handleMidRollAd] playMidRollAdAt返回:`, ad)
  
  if (ad) {
    await nextTick()
    console.log(`[handleMidRollAd] showAd: ${showAd.value}, adOverlayRef:`, adOverlayRef.value)
    
    if (adOverlayRef.value) {
      console.log(`[handleMidRollAd] 调用 adOverlayRef.playVideoAd`)
      await adOverlayRef.value.playVideoAd(ad)
      console.log(`[handleMidRollAd] playVideoAd完成`)
    } else {
      console.log(`[handleMidRollAd] adOverlayRef仍不存在，尝试延迟重试`)
      for (let i = 0; i < 5; i++) {
        await new Promise(r => setTimeout(r, 100))
        await nextTick()
        if (adOverlayRef.value) {
          await adOverlayRef.value.playVideoAd(ad)
          console.log(`[handleMidRollAd] 延迟重试成功，playVideoAd完成`)
          break
        }
      }
    }
  } else {
    console.log(`[handleMidRollAd] 未获取到广告，广告未播放`)
    setAdPlayingStatus(false)
  }
}

// ------------------------
// 跳过广告
// ------------------------
const skipAdInternal = () => {
  if (!videoPlayer.value) return
  skipAd(videoPlayer.value)
}

// ------------------------
// 广告结束处理
// ------------------------
const handleAdEndedInternal = () => {
  handleAdEnded(videoPlayer.value)
  setAdPlayingStatus(false)
}

// ------------------------
// 广告点击处理
// ------------------------
const trackAdClick = () => trackAdClickReal()

// ------------------------
// 组件挂载
// ------------------------
onMounted(async () => {
  const videoId = route.params.id
  await loadVideo(videoId)
  resetAdState()

  // 初始化广告 SDK
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
