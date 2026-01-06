import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import axios from 'axios'

export function useVideoPlayer() {
  // ------------------------------
  // 响应式状态
  // ------------------------------
  const video = ref({})
  const videoPlayer = ref(null)
  const adPositions = [0.5] // 视频50%位置插入广告
  const isVideoReady = ref(false)
  const isAdPlaying = ref(false)
  const playedMidRollPositions = ref(new Set())
  const lastTriggerTime = ref(0) // 防抖：上次触发时间

  // ------------------------------
  // 外部回调和监听器状态
  // ------------------------------
  let onTimeUpdateCallback = null
  let onMidRollAdCallback = null
  let timeUpdateListenerSet = false

  // ------------------------------
  // 加载视频数据
  // ------------------------------
  const loadVideo = async (id) => {
    try {
      const res = await axios.get(`/api/videos/${id}`)
      video.value = res.data
      playedMidRollPositions.value.clear()
      lastTriggerTime.value = 0
      isVideoReady.value = false
      isAdPlaying.value = false
      return video.value
    } catch (error) {
      console.error('加载视频失败:', error)
      throw error
    }
  }

  // ------------------------------
  // 设置视频事件监听器
  // ------------------------------
  const setupVideoListeners = () => {
    if (!videoPlayer.value) return false

    // 视频元数据加载完成
    videoPlayer.value.addEventListener('loadedmetadata', () => {
      isVideoReady.value = true
      nextTick(() => {
        if (timeUpdateListenerSet) {
          videoPlayer.value.removeEventListener('timeupdate', handleTimeUpdate)
          timeUpdateListenerSet = false
        }
        videoPlayer.value.addEventListener('timeupdate', handleTimeUpdate)
        timeUpdateListenerSet = true
        setTimeout(checkMidRollAd, 500)
      })
    })

    // 视频播放开始
    videoPlayer.value.addEventListener('play', () => {
      if (!timeUpdateListenerSet) {
        nextTick(() => {
          videoPlayer.value.addEventListener('timeupdate', handleTimeUpdate)
          timeUpdateListenerSet = true
        })
      }
    })

    return true
  }

  // ------------------------------
  // 外部回调设置
  // ------------------------------
  const setTimeUpdateListener = (callback) => onTimeUpdateCallback = callback
  const setMidRollAdListener = (callback) => onMidRollAdCallback = callback
  const setAdPlayingStatus = (status) => isAdPlaying.value = status

  // ------------------------------
  // 播放进度处理
  // ------------------------------
  const handleTimeUpdate = () => {
    if (onTimeUpdateCallback) onTimeUpdateCallback()
    if (isVideoReady.value && video.value.duration > 0 && !isAdPlaying.value) {
      checkMidRollAd()
    }
  }

  // ------------------------------
  // 中插广告逻辑（每次到达都触发）
  // ------------------------------
  const checkMidRollAd = () => {
    if (!videoPlayer.value || !video.value.duration || !isVideoReady.value || isAdPlaying.value) return

    const current = videoPlayer.value.currentTime
    const duration = video.value.duration
    const now = Date.now()

    if (now - lastTriggerTime.value < 2000) return // 2秒防抖
    lastTriggerTime.value = now

    const adTime = duration * adPositions[0]
    const timeDiff = Math.abs(current - adTime)

    if (duration >= 10 && timeDiff < 3) {
      if (!playedMidRollPositions.value.has(0)) {
        console.log(`[中插广告] 触发！当前时间: ${current.toFixed(1)}s, 广告时间点: ${adTime.toFixed(1)}s`)
        playedMidRollPositions.value.add(0)
        if (onMidRollAdCallback) onMidRollAdCallback(0)
      }
    }
  }

  // ------------------------------
  // 视频控制方法
  // ------------------------------
  const play = () => videoPlayer.value?.play()
  const pause = () => videoPlayer.value?.pause()
  const getCurrentTime = () => videoPlayer.value?.currentTime || 0
  const getDuration = () => videoPlayer.value?.duration || 0

  // ------------------------------
  // 清理资源
  // ------------------------------
  const cleanup = () => {
    if (videoPlayer.value && timeUpdateListenerSet) {
      videoPlayer.value.removeEventListener('timeupdate', handleTimeUpdate)
    }
  }

  onMounted(() => {})
  onBeforeUnmount(() => cleanup())

  // ------------------------------
  // 返回给外部使用
  // ------------------------------
  return {
    // 状态
    video,
    videoPlayer,
    isVideoReady,
    isAdPlaying,
    playedMidRollPositions,

    // 方法
    loadVideo,
    setupVideoListeners,
    setTimeUpdateListener,
    setMidRollAdListener,
    setAdPlayingStatus,
    checkMidRollAd,
    play,
    pause,
    getCurrentTime,
    getDuration
  }
}
