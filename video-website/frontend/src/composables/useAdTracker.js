import { ref } from 'vue'

// 调试信息记录
const debugLog = ref([])
const stats = ref({
  pageViewCount: 0,
  clickCount: 0,
  searchCount: 0,
  adImpressionCount: 0,
  adClickCount: 0,
  recommendedAdCount: 0
})

// 当前广告列表
const currentAds = ref([])

// 当前视频浏览状态
let currentVideoView = null
let videoViewStartTime = null
let videoViewTimer = null

// 添加调试信息
function addDebugInfo(message) {
  const timestamp = new Date().toLocaleTimeString()
  debugLog.value.unshift(`[${timestamp}] ${message}`)
  if (debugLog.value.length > 20) debugLog.value.pop()
  const debugElement = document.getElementById('debugInfo')
  if (debugElement) {
    debugElement.textContent = debugLog.value.join('\n')
  }
}

// 初始化广告追踪SDK
function initAdTracker() {
  try {
    if (window.adTracker) {
      window.adTracker.init({
        trackerServer: 'http://10.100.164.35:8084',
        website: 'video'
      })
      addDebugInfo('广告追踪SDK初始化成功')
      addDebugInfo(`用户指纹: ${window.adTracker.generateFingerprint()}`)
      return true
    } else {
      addDebugInfo('广告追踪SDK未加载')
      return false
    }
  } catch (error) {
    addDebugInfo(`SDK初始化失败: ${error.message}`)
    return false
  }
}

// 追踪广告展示
function trackAdImpression(adId, position, bidPrice = 1.00) {
  return window.adTracker.trackAdImpression(adId, position, bidPrice)
    .then(impressionId => {
      stats.value.adImpressionCount++
      addDebugInfo(`广告展示: 位置=${position}, ID=${adId}, 出价=${bidPrice}, 展示ID=${impressionId}`)
      return impressionId
    })
    .catch(error => {
      addDebugInfo(`广告展示记录失败: ${error.message}`)
      throw error
    })
}

// 追踪广告点击
function trackAdClick(impressionId) {
  try {
    window.adTracker.trackAdClick(impressionId)
    stats.value.adClickCount++
    addDebugInfo(`广告点击: 展示ID=${impressionId}`)
  } catch (error) {
    addDebugInfo(`广告点击记录失败: ${error.message}`)
  }
}

// 追踪视频点击
function trackVideoClick(videoId, category) {
  try {
    window.adTracker.trackClick({
      targetId: `video_${videoId}`,
      category: category || 'video',
      duration: 1
    })
    stats.value.clickCount++
    addDebugInfo(`视频点击: ID=${videoId}, 类别=${category}`)
  } catch (error) {
    addDebugInfo(`视频点击记录失败: ${error.message}`)
  }
}

// 追踪搜索
let searchTimeout
function trackSearch(query) {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    if (query.length > 0) {
      try {
        window.adTracker.trackSearch({
          targetId: 'search_input',
          keywords: query,
          duration: 2
        })
        stats.value.searchCount++
        addDebugInfo(`搜索记录: ${query}`)
      } catch (error) {
        addDebugInfo(`搜索记录失败: ${error.message}`)
      }
    }
  }, 300)
}

let videoViewElapsedBeforeAd = 0  // 广告开始前视频已观看的时长

// 暂停视频浏览追踪（广告开始时调用）
function pauseVideoViewTracking() {
  if (currentVideoView && videoViewStartTime) {
    const now = new Date()
    const elapsed = (now - videoViewStartTime) / 1000
    videoViewElapsedBeforeAd += elapsed
    
    if (videoViewTimer) {
      clearInterval(videoViewTimer)
      videoViewTimer = null
    }
    
    addDebugInfo(`暂停视频追踪，已累计: ${videoViewElapsedBeforeAd.toFixed(1)}秒`)
  }
}

// 恢复视频浏览追踪（广告结束后调用）
function resumeVideoViewTracking() {
  if (currentVideoView) {
    videoViewStartTime = new Date()
    
    if (videoViewTimer) clearInterval(videoViewTimer)
    videoViewTimer = setInterval(() => {
      if (!document.hidden) {
        const now = new Date()
        const currentElapsed = (now - videoViewStartTime) / 1000
        const totalDuration = Math.floor(videoViewElapsedBeforeAd + currentElapsed)
        addDebugInfo(`继续观看视频: ${currentVideoView.videoId}, 总计已观看 ${totalDuration} 秒`)
      }
    }, 1000)
    
    addDebugInfo(`恢复视频追踪，继续计时`)
  }
}

// 追踪视频浏览时长 - 开始
function startVideoViewTracking(videoId, category) {
  if (currentVideoView) {
    recordVideoView()
  }
  
  currentVideoView = { videoId, category }
  videoViewStartTime = new Date()
  videoViewElapsedBeforeAd = 0
  
  if (videoViewTimer) clearInterval(videoViewTimer)
  videoViewTimer = setInterval(() => {
    if (!document.hidden) {
      const now = new Date()
      const totalDuration = videoViewElapsedBeforeAd + (now - videoViewStartTime) / 1000
      addDebugInfo(`正在观看视频: ${videoId}, 已观看 ${Math.floor(totalDuration)} 秒`)
    }
  }, 1000)
}

// 追踪视频浏览时长 - 记录
function recordVideoView() {
  if (currentVideoView && videoViewStartTime) {
    const now = new Date()
    const currentElapsed = (now - videoViewStartTime) / 1000
    const totalDuration = Math.floor(videoViewElapsedBeforeAd + currentElapsed)
    
    const finalDuration = Math.max(1, Math.min(totalDuration, 3600))
    
    try {
      window.adTracker.trackPageView({
        targetId: `video_${currentVideoView.videoId}`,
        category: currentVideoView.category || 'video',
        duration: finalDuration
      })
      stats.value.pageViewCount++
      addDebugInfo(`视频浏览记录: ${currentVideoView.videoId}, 时长: ${finalDuration}秒`)
    } catch (error) {
      addDebugInfo(`视频浏览记录失败: ${error.message}`)
    }
    
    currentVideoView = null
    videoViewStartTime = null
    videoViewElapsedBeforeAd = 0
    if (videoViewTimer) {
      clearInterval(videoViewTimer)
      videoViewTimer = null
    }
  }
}

// 当前广告浏览状态
let currentAdView = null
let adViewStartTime = null
let adViewTimer = null

// 追踪广告浏览时长 - 开始
function startAdViewTracking(adId, category, position) {
  if (currentAdView) {
    recordAdView()
  }
  
  pauseVideoViewTracking()
  
  currentAdView = { adId, category, position }
  adViewStartTime = new Date()
  
  if (adViewTimer) clearInterval(adViewTimer)
  adViewTimer = setInterval(() => {
    if (!document.hidden) {
      const currentTime = new Date()
      const duration = Math.floor((currentTime - adViewStartTime) / 1000)
      addDebugInfo(`正在观看广告: ${adId}, 已观看 ${duration} 秒`)
    }
  }, 1000)
}

// 追踪广告浏览时长 - 记录
function recordAdView() {
  if (currentAdView && adViewStartTime) {
    const endTime = new Date()
    const duration = Math.floor((endTime - adViewStartTime) / 1000)
    
    const finalDuration = Math.max(1, Math.min(duration, 3600))
    
    try {
      window.adTracker.trackPageView({
        targetId: `ad_${currentAdView.adId}`,
        category: currentAdView.category || 'video',
        duration: finalDuration
      })
      stats.value.pageViewCount++
      addDebugInfo(`广告浏览记录: ${currentAdView.adId}, 时长: ${finalDuration}秒`)
    } catch (error) {
      addDebugInfo(`广告浏览记录失败: ${error.message}`)
    }
    
    currentAdView = null
    adViewStartTime = null
    if (adViewTimer) {
      clearInterval(adViewTimer)
      adViewTimer = null
    }
    
    resumeVideoViewTracking()
  }
}

// 获取推荐广告
function getRecommendedAds(options = {}) {
  const { type = '', positions, category = '', count = 6 } = options
  try {
    const requestParams = {
      positions: positions || ['top-banner', 'sidebar', 'feed', 'right-rail-1', 'right-rail-2', 'bottom-banner'],
      category: category,
      count: count
    }
    if (type) {
      requestParams.type = type
    }
    return window.adTracker.getRecommendedAds(requestParams).then(ads => {
      stats.value.recommendedAdCount += ads.length
      addDebugInfo(`获取推荐广告成功: ${ads.length}个, 类型=${type || '不限'}`)
      currentAds.value = ads
      renderRecommendedAds(ads)
      return ads
    }).catch(error => {
      addDebugInfo(`获取推荐广告失败: ${error.message}`)
      throw error
    })
  } catch (error) {
    addDebugInfo(`获取推荐广告请求失败: ${error.message}`)
    throw error
  }
}

// 渲染推荐广告
function renderRecommendedAds(ads) {
  if (ads && ads.length > 0) {
    // 顶部横幅广告
    if (ads[0]) {
      const topBannerContent = document.getElementById('topBannerContent')
      if (topBannerContent) {
        topBannerContent.innerHTML = `
          <div class="ad-content-inner">
            ${ads[0].imageUrl ? `<img src="${ads[0].imageUrl}" alt="${ads[0].title || '广告'}" class="ad-image">` : ''}
            <div class="ad-text">
              <strong>${ads[0].title || '推荐广告'}</strong><br>
              ${ads[0].description || '广告描述'}
            </div>
          </div>
        `
        // 添加点击事件追踪
        document.getElementById('topBannerAd').onclick = () => {
          if (ads[0].impressionId) {
            trackAdClick(ads[0].impressionId)
          }
        }
      }
    }
    
    // 侧边栏广告
    if (ads[1]) {
      const sidebarContent = document.getElementById('sidebarContent')
      if (sidebarContent) {
        sidebarContent.innerHTML = `
          <div class="ad-content-inner">
            ${ads[1].imageUrl ? `<img src="${ads[1].imageUrl}" alt="${ads[1].title || '广告'}" class="ad-image sidebar">` : ''}
            <div class="ad-text">
              <strong>${ads[1].title || '推荐广告'}</strong><br>
              ${ads[1].description || '广告描述'}
            </div>
          </div>
        `
        document.getElementById('sidebarAd').onclick = () => {
          if (ads[1].impressionId) {
            trackAdClick(ads[1].impressionId)
          }
        }
      }
    }
    
    // 信息流广告
    if (ads[2]) {
      const feedContent = document.getElementById('feedContent')
      if (feedContent) {
        feedContent.innerHTML = `
          <div class="ad-content-inner">
            ${ads[2].imageUrl ? `<img src="${ads[2].imageUrl}" alt="${ads[2].title || '广告'}" class="ad-image feed">` : ''}
            <div class="ad-text">
              <strong>${ads[2].title || '推荐广告'}</strong><br>
              ${ads[2].description || '广告描述'}
            </div>
          </div>
        `
        document.getElementById('feedAd').onclick = () => {
          if (ads[2].impressionId) {
            trackAdClick(ads[2].impressionId)
          }
        }
      }
    }
    
    // 右侧广告1
    if (ads[3]) {
      const rightAd1Content = document.getElementById('rightAd1Content')
      if (rightAd1Content) {
        rightAd1Content.innerHTML = `
          <div class="ad-content-inner">
            ${ads[3].imageUrl ? `<img src="${ads[3].imageUrl}" alt="${ads[3].title || '广告'}" class="ad-image right">` : ''}
            <div class="ad-text">
              <strong>${ads[3].title || '推荐广告'}</strong><br>
              ${ads[3].description || '广告描述'}
            </div>
          </div>
        `
        document.getElementById('rightAd1').onclick = () => {
          if (ads[3].impressionId) {
            trackAdClick(ads[3].impressionId)
          }
        }
      }
    }
    
    // 右侧广告2
    if (ads[4]) {
      const rightAd2Content = document.getElementById('rightAd2Content')
      if (rightAd2Content) {
        rightAd2Content.innerHTML = `
          <div class="ad-content-inner">
            ${ads[4].imageUrl ? `<img src="${ads[4].imageUrl}" alt="${ads[4].title || '广告'}" class="ad-image right">` : ''}
            <div class="ad-text">
              <strong>${ads[4].title || '推荐广告'}</strong><br>
              ${ads[4].description || '广告描述'}
            </div>
          </div>
        `
        document.getElementById('rightAd2').onclick = () => {
          if (ads[4].impressionId) {
            trackAdClick(ads[4].impressionId)
          }
        }
      }
    }
    
    // 底部广告
    if (ads[5]) {
      const bottomContent = document.getElementById('bottomContent')
      if (bottomContent) {
        bottomContent.innerHTML = `
          <div class="ad-content-inner">
            ${ads[5].imageUrl ? `<img src="${ads[5].imageUrl}" alt="${ads[5].title || '广告'}" class="ad-image bottom">` : ''}
            <div class="ad-text">
              <strong>${ads[5].title || '推荐广告'}</strong><br>
              ${ads[5].description || '广告描述'}
            </div>
          </div>
        `
        document.getElementById('bottomAd').onclick = () => {
          if (ads[5].impressionId) {
            trackAdClick(ads[5].impressionId)
          }
        }
      }
    }
    
    // 记录广告展示
    ads.forEach((ad, index) => {
      const position = ad.position || `position_${index + 1}`
      trackAdImpression(ad.id || (index + 1), position, ad.bidPrice || 1.00)
        .then(impressionId => {
          ad.impressionId = impressionId
        })
        .catch(error => {
          addDebugInfo(`记录广告展示失败: ${error.message}`)
        })
    })
  }
}

export function useAdTracker() {
  return {
    debugLog,
    stats,
    currentAds,
    addDebugInfo,
    initAdTracker,
    trackAdImpression,
    trackAdClick,
    trackVideoClick,
    trackSearch,
    getRecommendedAds,
    renderRecommendedAds,
    startVideoViewTracking,
    recordVideoView,
    startAdViewTracking,
    recordAdView,
    pauseVideoViewTracking,
    resumeVideoViewTracking
  }
}