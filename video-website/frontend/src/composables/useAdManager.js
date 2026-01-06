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

  const { getRecommendedAds, trackAdClick } = useAdTracker()

  // -----------------------
  // 获取广告
  // -----------------------
  const getAd = async (positionType) => {
    try {
      const ads = await getRecommendedAds()
      if (ads?.length) {
        const filteredAds = ads.filter(ad => {
          if (positionType === 'video-pre-roll') {
            return ad.position === 'right-rail-1' || !ad.position
          } else if (positionType === 'video-mid-roll') {
            return ad.position === 'right-rail-2' || !ad.position
          }
          return true
        })
        const ad = filteredAds[0] || ads[0]
        console.log('广告视频 URL:', ad.videoUrl)
        console.log('原始广告对象:', JSON.stringify(ad, null, 2))


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

    mainVideoRef?.pause()
    return ad
  }

  // -----------------------
  // 跳过广告
  // -----------------------
  const skipAd = (mainVideoRef) => hideAd(mainVideoRef)

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
    trackAdClickReal
  }
}
