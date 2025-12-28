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
        trackerServer: 'http://track.video.com:8084',
        website: 'video-website'
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

// 获取推荐广告
function getRecommendedAds() {
  try {
    return window.adTracker.getRecommendedAds({
      positions: ['top-banner', 'sidebar', 'feed', 'right-rail-1', 'right-rail-2', 'bottom-banner'],
      category: 'video',
      count: 6
    }).then(ads => {
      stats.value.recommendedAdCount += ads.length
      addDebugInfo(`获取推荐广告成功: ${ads.length}个`)
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
    renderRecommendedAds
  }
}