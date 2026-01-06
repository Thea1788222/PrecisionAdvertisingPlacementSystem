import { ref, nextTick } from 'vue'
import { useAdTracker } from './useAdTracker'

export function useAdManager() {
  const showAd = ref(false)
  const currentAd = ref(null)
  const adTitle = ref('')
  const adCountdown = ref(0)
  const adTimer = ref(null)
  const isVideoAd = ref(false)
  const isImageAd = ref(false)
  const canSkipAd = ref(false)
  const preRollPlayed = ref(false)
  const playedMidRollPositions = ref(new Set())

  const { getRecommendedAds, trackAdClick, trackAdImpression, addDebugInfo } = useAdTracker()

  // SDK位置参数映射
  const SDK_POSITIONS = {
    'video-pre-roll': 'right-rail-1',  // 前贴片广告
    'video-mid-roll': 'right-rail-2'   // 中插广告
  }

  // -----------------------
  // 获取广告
  // -----------------------
  const getAd = async (positionType) => {
    try {
      const sdkPosition = SDK_POSITIONS[positionType]
      console.log(`[getAd] 请求广告类型: ${positionType}, SDK位置: ${sdkPosition}`)
      
      const ads = await getRecommendedAds({ type: 'video', positions: [sdkPosition] })
      console.log(`[getAd] 获取到广告数组:`, ads)
      
      if (ads?.length) {
        const ad = ads[0]
        console.log(`[getAd] 选中广告:`, ad)
        
        if (ad) {
          ad.playUrl = ad.videoUrl || ''
          ad.position = positionType
          ad.bidPrice = ad.bidPrice || 1.0
          return ad
        }
      }
      console.log(`[getAd] 未找到可用广告`)
      return null
    } catch (e) {
      console.error('[getAd] 获取广告失败:', e)
      return null
    }
  }

  // -----------------------
  // 追踪广告展示
  // -----------------------
  const trackAdImpressionForAd = (ad) => {
    if (ad) {
      trackAdImpression(ad.id || Date.now(), ad.position, ad.bidPrice)
        .then(impressionId => {
          ad.impressionId = impressionId
          addDebugInfo(`中插广告展示: 位置=${ad.position}, ID=${ad.id}`)
        })
        .catch(error => {
          addDebugInfo(`中插广告展示记录失败: ${error.message}`)
        })
    }
  }

  // -----------------------
  // 倒计时逻辑
  // -----------------------
  const startAdCountdown = (adType, duration = 5) => {
    clearInterval(adTimer.value)
    adCountdown.value = duration
    canSkipAd.value = adType === 'video' ? false : true

    adTimer.value = setInterval(() => {
      adCountdown.value--
      if (adType === 'video' && adCountdown.value <= 0) canSkipAd.value = true
      if (adCountdown.value <= 0 && adType === 'image') hideAd()
    }, 1000)
  }

  // -----------------------
  // 隐藏广告
  // -----------------------
  const hideAd = (mainVideoRef = null) => {
    clearInterval(adTimer.value)
    showAd.value = false
    isVideoAd.value = false
    isImageAd.value = false
    currentAd.value = null

    if (mainVideoRef?.play) mainVideoRef.play().catch(() => {})
  }

  // -----------------------
  // 播放前贴片广告
  // -----------------------
  const playPreRollAd = async (mainVideoRef) => {
    if (preRollPlayed.value) return false
    preRollPlayed.value = true

    const ad = await getAd('video-pre-roll')
    if (!ad) return false

    currentAd.value = ad
    isVideoAd.value = !!ad.playUrl
    isImageAd.value = !!ad.imageUrl
    showAd.value = true
    adTitle.value = ad.title || '广告'
    startAdCountdown(isVideoAd.value ? 'video' : 'image')

    mainVideoRef?.pause()
    return ad
  }

  // -----------------------
  // 播放中插广告
  // -----------------------
  const playMidRollAdAt = async (positionIndex, mainVideoRef) => {
    if (playedMidRollPositions.value.has(positionIndex)) return false
    playedMidRollPositions.value.add(positionIndex)

    const ad = await getAd('video-mid-roll')
    if (!ad) return false

    currentAd.value = ad
    isVideoAd.value = !!ad.playUrl
    isImageAd.value = !!ad.imageUrl
    showAd.value = true
    adTitle.value = ad.title || '中插广告'
    startAdCountdown(isVideoAd.value ? 'video' : 'image')

    trackAdImpressionForAd(ad)

    mainVideoRef?.pause()
    return ad
  }

  // -----------------------
  // 跳过广告
  // -----------------------
  const skipAd = (mainVideoRef) => hideAd(mainVideoRef)

  // -----------------------
  // 重置广告状态（换视频时调用）
  // -----------------------
  const resetAdState = () => {
    preRollPlayed.value = false
    playedMidRollPositions.value.clear()
    hideAd()
  }

  // -----------------------
  // 广告播放结束
  // -----------------------
  const handleAdEnded = (mainVideoRef) => hideAd(mainVideoRef)

  // -----------------------
  // 广告点击
  // -----------------------
  const trackAdClickReal = () => {
    if (currentAd.value) trackAdClick(currentAd.value.id)
  }

  return {
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
    trackAdClickReal,
    resetAdState
  }
}
