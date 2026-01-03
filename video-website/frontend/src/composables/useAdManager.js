import { ref } from 'vue'
import { useAdTracker } from './useAdTracker'

export function useAdManager() {
  // 响应式状态
  const showAd = ref(false)
  const currentAd = ref(null)
  const adImpressionId = ref(null)
  const adTitle = ref('')
  const adCountdown = ref(0)
  const adTimer = ref(null)
  const preRollAdShown = ref(false)
  const isVideoAd = ref(false)
  const isImageAd = ref(false)
  const adDuration = 5 // 广告展示时长（秒）
  
  // 广告追踪
  const { getRecommendedAds, trackAdImpression, trackAdClick } = useAdTracker()
  
  // 获取广告的通用函数
  const getAd = async (positionType) => {
    console.log(`获取${positionType}类型广告`)
    
    try {
      // 调用SDK获取推荐广告
      const ads = await getRecommendedAds({
        positions: [positionType],
        count: 1,
        type: 'video' // 获取视频广告类型
      })
      
      if (ads && ads.length > 0) {
        const ad = ads[0]
        // 根据SDK返回的广告类型设置相应的属性
        if (ad.type === 'video' && ad.videoUrl) {
          ad.playUrl = ad.videoUrl
        }
        return ad
      }
      
      throw new Error('SDK未返回广告')
    } catch (error) {
      console.error('获取推荐广告失败:', error)
      throw error
    }
  }
  
  // 获取前贴片广告
  const getPreRollAd = async () => {
    return getAd('video-pre-roll')
  }
  
  // 获取中插广告
  const getMidRollAd = async () => {
    return getAd('video-mid-roll')
  }
  
  // 记录广告展示
  const trackAdImpressionReal = async (ad) => {
    try {
      console.log('正在记录广告展示:', ad)
      
      // 调用SDK记录广告展示
      const impressionId = await trackAdImpression(ad.id, ad.position || 'video-ad', ad.bidPrice || 1.00)
      adImpressionId.value = impressionId
      console.log('广告展示记录成功:', impressionId)
      return impressionId
    } catch (error) {
      console.error('广告展示记录失败:', error)
      throw error
    }
  }
  
  // 记录广告点击
  const trackAdClickReal = () => {
    if (!adImpressionId.value) {
      console.warn('没有有效的广告展示ID，无法记录点击')
      return
    }
    
    try {
      console.log('正在记录广告点击:', adImpressionId.value)
      trackAdClick(adImpressionId.value)
      console.log('广告点击记录成功')
    } catch (error) {
      console.error('广告点击记录失败:', error)
    }
  }
  
  // 开始广告倒计时
  const startAdCountdown = () => {
    // 清除之前的定时器
    if (adTimer.value) {
      clearInterval(adTimer.value)
      adTimer.value = null
    }
    
    // 设置初始倒计时时间
    adCountdown.value = adDuration
    
    // 创建倒计时定时器
    adTimer.value = setInterval(() => {
      adCountdown.value--
      
      // 倒计时结束，关闭广告
      if (adCountdown.value <= 0) {
        hideAd()
      }
    }, 1000)
  }
  
  // 隐藏广告
  const hideAd = (adVideoRef = null) => {
    // 清除倒计时定时器
    if (adTimer.value) {
      clearInterval(adTimer.value)
      adTimer.value = null
    }
    
    // 停止视频广告播放
    if (isVideoAd.value && adVideoRef && adVideoRef.value) {
      adVideoRef.value.pause()
      adVideoRef.value.currentTime = 0
    }
    
    // 隐藏广告
    showAd.value = false
    
    // 重置广告类型标记
    isVideoAd.value = false
    isImageAd.value = false
    
    // 清理当前广告数据
    currentAd.value = null
    adImpressionId.value = null
    adTitle.value = ''
    adCountdown.value = 0
  }
  
  // 跳过广告
  const skipAd = () => {
    hideAd()
  }
  
  // 播放前贴片广告
  const playPreRollAd = async (adVideoRef, mainVideoRef) => {
    try {
      // 获取广告
      const ad = await getPreRollAd()
      if (!ad) return false
      
      currentAd.value = ad
      
      // 设置广告类型
      isVideoAd.value = !!ad.playUrl
      isImageAd.value = !!ad.imageUrl
      
      // 记录广告展示
      await trackAdImpressionReal(ad)
      
      // 标记前贴片广告已展示
      preRollAdShown.value = true
      
      // 显示广告
      showAd.value = true
      
      // 设置广告标题
      adTitle.value = ad.title || '广告'
      
      // 设置广告倒计时
      startAdCountdown()
      
      // 如果是视频广告，尝试自动播放
      if (isVideoAd.value && adVideoRef.value) {
        setTimeout(async () => {
          try {
            await adVideoRef.value.play()
            console.log('前贴片广告自动播放成功')
          } catch (error) {
            console.warn('前贴片广告自动播放被阻止，需要用户交互:', error)
          }
        }, 100)
      }
      
      return true
    } catch (error) {
      console.error('播放前贴片广告失败:', error)
      return false
    }
  }
  
  // 播放中插广告
  const playMidRollAd = async (adVideoRef, mainVideoRef) => {
    try {
      // 暂停主视频
      if (mainVideoRef.value) {
        mainVideoRef.value.pause()
      }

      // 获取广告
      const ad = await getMidRollAd()
      if (!ad) {
        // 如果没有广告，继续播放主视频
        if (mainVideoRef.value) {
          mainVideoRef.value.play()
        }
        return false
      }
      
      currentAd.value = ad
      
      // 设置广告类型
      isVideoAd.value = !!ad.playUrl
      isImageAd.value = !!ad.imageUrl

      // 记录广告展示
      await trackAdImpressionReal(ad)

      // 显示广告
      showAd.value = true
      
      // 设置广告标题
      adTitle.value = ad.title || '广告'
      
      // 设置广告倒计时
      startAdCountdown()

      // 自动播放广告，处理自动播放被阻止的情况
      if (isVideoAd.value && adVideoRef.value) {
        setTimeout(async () => {
          try {
            // 尝试自动播放广告
            await adVideoRef.value.play();
            console.log('中插广告自动播放成功');
          } catch (error) {
            console.warn('中插广告自动播放被阻止，需要用户交互:', error);
            // 保持广告显示状态，允许用户手动播放
          }
        }, 100)
      }
      
      return true
    } catch (error) {
      console.error('播放中插广告失败:', error)
      // 如果广告播放失败，继续播放主视频
      if (mainVideoRef.value) {
        mainVideoRef.value.play()
      }
      return false
    }
  }
  
  // 处理广告结束
  const handleAdEnded = (adVideoRef, mainVideoRef) => {
    hideAd()
    
    // 继续播放主视频
    if (mainVideoRef.value) {
      mainVideoRef.value.play()
    }
  }
  
  // 清理资源
  const cleanup = () => {
    if (adTimer.value) {
      clearInterval(adTimer.value)
      adTimer.value = null
    }
  }
  
  return {
    // 状态
    showAd,
    currentAd,
    adTitle,
    adCountdown,
    preRollAdShown,
    isVideoAd,
    isImageAd,
    
    // 方法
    playPreRollAd,
    playMidRollAd,
    handleAdEnded,
    skipAd,
    trackAdClickReal,
    getPreRollAd,
    getMidRollAd,
    cleanup
  }
}