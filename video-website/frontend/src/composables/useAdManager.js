import { ref } from 'vue'
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

  const { getRecommendedAds, trackAdImpression, trackAdClick } = useAdTracker()

  const getAd = async (positionType) => {
    try {
      const ads = await getRecommendedAds()
      console.log(`[调试] 获取到的广告列表:`, ads) // <-- 这里加一行调试信息
      if (ads?.length) {
        // 根据位置类型筛选广告
        const filteredAds = ads.filter(ad => {
          if (positionType === 'video-pre-roll') {
            return ad.position === 'right-rail-1' || !ad.position
          } else if (positionType === 'video-mid-roll') {
            return ad.position === 'right-rail-2' || !ad.position
          }
          return true
        })

        const ad = filteredAds[0] || ads[0]
        if (ad) {
          ad.playUrl = ad.videoUrl || ''
          ad.position = positionType
          ad.bidPrice = ad.bidPrice || 1.0
          return ad
        }
      }
    } catch (e) {
      console.error('获取广告失败:', e)
    }
  }
/*
  const trackAdImpressionReal = async (ad) => {
    try {
      const impressionId = await trackAdImpression(ad.id, ad.position || 'video-ad', ad.bidPrice)
      return impressionId
    } catch (e) {
      console.error('记录广告展示失败:', e)
    }
  }
  */

  const trackAdClickReal = () => {
    if (currentAd.value) trackAdClick(currentAd.value.id)
  }

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

  const hideAd = (adVideoRef = null) => {
    clearInterval(adTimer.value)
    adTimer.value = null
    showAd.value = false
    isVideoAd.value = false
    isImageAd.value = false
    currentAd.value = null

    if (adVideoRef?.pause) {
      adVideoRef.pause()
      adVideoRef.currentTime = 0
      adVideoRef.muted = false
    }
  }

  const playPreRollAd = async (adVideoRef) => {
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

    if (isVideoAd.value && adVideoRef?.play) adVideoRef.play().catch(() => {})
    return true
  }

  const playMidRollAdAt = async (positionIndex, adVideoRef, mainVideoRef) => {
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

    mainVideoRef?.pause()
    if (isVideoAd.value && adVideoRef?.play) adVideoRef.play().catch(() => {})
    return true
  }

  const skipAd = (adVideoRef, mainVideoRef) => {
    hideAd(adVideoRef)
    mainVideoRef?.play().catch(() => {})
  }

  const handleAdEnded = (adVideoRef, mainVideoRef) => {
    hideAd(adVideoRef)
    mainVideoRef?.play().catch(() => {})
  }

  const cleanup = () => {
    clearInterval(adTimer.value)
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
    cleanup
  }
}
