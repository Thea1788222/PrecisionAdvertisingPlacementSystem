<template>
  <div>
    <h1>{{ video.title }}</h1>
    <div class="video-container">
      <video ref="videoPlayer" width="640" height="360" controls v-if="video.playUrl">
        <source :src="video.playUrl" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>

      <!-- 广告容器 -->
      <div v-if="showAd" class="ad-overlay">
        <div class="ad-content">
          <h3>{{ adTitle }}</h3>
          <div class="ad-countdown" v-if="adCountdown > 0">广告将在 {{ adCountdown }} 秒后关闭</div>
          
          <!-- 视频广告 -->
          <div v-if="isVideoAd" class="ad-video">
            <video ref="adVideo" width="640" height="360" controls @ended="handleAdEnded">
              <source :src="currentAd.playUrl" type="video/mp4" />
              您的浏览器不支持视频播放。
            </video>
          </div>
          
          <!-- 图片广告 -->
          <div v-else-if="isImageAd" class="ad-image-container">
            <img :src="currentAd.imageUrl" alt="{{ currentAd.title }}" class="ad-image" @click="trackAdClick" />
          </div>
          
          <div class="ad-info">
            <p>{{ currentAd.title }}</p>
            <button @click="trackAdClick" class="ad-click-btn">了解详情</button>
            <button @click="skipAd" class="ad-skip-btn" :disabled="adCountdown > 0">跳过广告</button>
          </div>
        </div>
      </div>
    </div>
    <p>{{ video.description }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const route = useRoute()
const video = ref({})
const videoPlayer = ref(null)
const adVideo = ref(null)
const showAd = ref(false)
const currentAd = ref(null)
const adImpressionId = ref(null)
const adShown = ref([])
const adPositions = [0.5] // 视频播放50%位置插入广告
const lastPlayTime = ref(0) // 记录上一次播放时间，用于检测是否经过了广告位置

// 广告相关新变量
const adTitle = ref('')
const adCountdown = ref(0)
const adTimer = ref(null)
const preRollAdShown = ref(false) // 标记前贴片广告是否已展示
const isVideoAd = ref(false) // 是否是视频广告
const isImageAd = ref(false) // 是否是图片广告
const adDuration = 5 // 广告展示时长（秒）

// 初始化广告SDK（预留接口，当前未使用）
const initAdSDK = () => {
  console.log('广告SDK初始化接口预留（当前未使用）');
  // 后续可在此处添加真实SDK初始化代码
}

// 获取广告的通用函数
const getAd = async (positionType) => {
  console.log(`获取${positionType}类型广告`);
  
  // 当前仅使用模拟广告数据（预留SDK调用接口）
  // 后续可在此处添加SDK调用代码
  /*
  if (window.adTracker) {
    try {
      const ads = await window.adTracker.getRecommendedAds({
        positions: [positionType],
        count: 1
      })
      
      if (ads && ads.length > 0) {
        return ads[0]
      }
    } catch (error) {
      console.error('获取推荐广告失败:', error)
    }
  }
  */
  
  // 模拟广告数据（本地测试用）
  return getMockAd(positionType)
}

// 获取前贴片广告
const getPreRollAd = async () => {
  return getAd('video-pre-roll')
}

// 获取中插广告
const getMidRollAd = async () => {
  return getAd('video-mid-roll')
}

// 模拟广告数据
const getMockAd = (positionType) => {
  // 使用public目录下的广告资源
  const localAds = {
    'video-pre-roll': [
      {
        id: 'pre-001',
        title: '前贴片视频广告',
        playUrl: '/test_ads_data/ads.local/ads01.mp4',
        position: 'video-pre-roll',
        bidPrice: 2.0,
        type: 'video'
      },
      {
        id: 'pre-002',
        title: '前贴片图片广告',
        imageUrl: '/test_ads_data/ads.local/ads02.jpg',
        position: 'video-pre-roll',
        bidPrice: 1.5,
        type: 'image'
      }
    ],
    'video-mid-roll': [
      {
        id: 'mid-001',
        title: '中插视频广告',
        playUrl: '/test_ads_data/ads.local/ads01.mp4',
        position: 'video-mid-roll',
        bidPrice: 2.5,
        type: 'video'
      },
      {
        id: 'mid-002',
        title: '中插图片广告',
        imageUrl: '/test_ads_data/ads.local/ads02.jpg',
        position: 'video-mid-roll',
        bidPrice: 2.0,
        type: 'image'
      }
    ]
  }
  
  const ads = localAds[positionType] || []
  // 随机选择一个广告
  return ads[Math.floor(Math.random() * ads.length)]
}

// 记录广告展示（预留接口，当前未使用）
const trackAdImpression = async (ad) => {
  console.log('广告展示记录接口预留（当前未使用）:', ad);
  // 生成模拟的impressionId用于演示
  adImpressionId.value = `mock-impression-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
  
  /* 后续可在此处添加真实SDK调用代码
  if (window.adTracker) {
    try {
      const impressionId = await window.adTracker.trackAdImpression(ad.id, ad.position, ad.bidPrice)
      adImpressionId.value = impressionId
      console.log('广告展示记录成功:', ad)
    } catch (error) {
      console.error('广告展示记录失败:', error)
    }
  }
  */
}

// 记录广告点击（预留接口，当前未使用）
const trackAdClick = () => {
  console.log('广告点击记录接口预留（当前未使用）:', adImpressionId.value);
  
  /* 后续可在此处添加真实SDK调用代码
  if (window.adTracker && adImpressionId.value) {
    try {
      window.adTracker.trackAdClick(adImpressionId.value)
      console.log('广告点击记录成功:', adImpressionId.value)
    } catch (error) {
      console.error('广告点击记录失败:', error)
    }
  }
  */
}

// 播放前贴片广告
const playPreRollAd = async () => {
  try {
    // 获取广告
    const ad = await getPreRollAd()
    if (!ad) return
    
    currentAd.value = ad
    
    // 设置广告类型
    isVideoAd.value = !!ad.playUrl
    isImageAd.value = !!ad.imageUrl
    
    // 记录广告展示
    await trackAdImpression(ad)
    
    // 标记前贴片广告已展示
    preRollAdShown.value = true
    
    // 显示广告
    showAd.value = true
    
    // 设置广告标题
    adTitle.value = ad.title || '广告'
    
    // 设置广告倒计时
    startAdCountdown()
    
    // 如果是视频广告，尝试自动播放
    if (isVideoAd.value && adVideo.value) {
      setTimeout(async () => {
        try {
          await adVideo.value.play()
          console.log('前贴片广告自动播放成功')
        } catch (error) {
          console.warn('前贴片广告自动播放被阻止，需要用户交互:', error)
        }
      }, 100)
    }
  } catch (error) {
    console.error('播放前贴片广告失败:', error)
    // 如果广告播放失败，继续播放主视频
    videoPlayer.value.play()
  }
}

// 处理广告结束
const handleAdEnded = () => {
  // 清除倒计时定时器
  if (adTimer.value) {
    clearInterval(adTimer.value)
    adTimer.value = null
  }
  
  // 停止视频广告播放
  if (isVideoAd.value && adVideo.value) {
    adVideo.value.pause()
    adVideo.value.currentTime = 0
  }
  
  // 隐藏广告
  showAd.value = false
  
  // 重置广告类型标记
  isVideoAd.value = false
  isImageAd.value = false
  
  // 继续播放主视频
  if (videoPlayer.value) {
    videoPlayer.value.play()
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
      handleAdEnded()
    }
  }, 1000)
}

// 跳过广告
const skipAd = () => {
  handleAdEnded()
}

// 检查是否需要播放中插广告
const checkMidRollAd = () => {
  if (!videoPlayer.value || !video.value.duration || showAd.value) return

  const currentTime = videoPlayer.value.currentTime
  const videoDuration = video.value.duration
  
  // 检查是否到达广告插入位置
  for (let i = 0; i < adPositions.length; i++) {
    if (adShown.value.includes(i)) continue; // 跳过已显示的广告
    
    const adTime = videoDuration * adPositions[i]
    
    // 增加误差范围到2秒，提高触发概率
    const timeDiff = Math.abs(currentTime - adTime);
    
    // 条件1：当前时间接近广告时间（误差2秒内）
    // 条件2：或者用户拖动进度条经过了广告位置
    const passedAdPosition = lastPlayTime.value < adTime && currentTime >= adTime;
    
    if ((timeDiff < 2 || passedAdPosition) && !adShown.value.includes(i)) {
      console.log(`触发中插广告 ${i+1}，位置：${adPositions[i]*100}%`);
      playMidRollAd(i);
      break;
    }
  }
  
  // 更新上一次播放时间
  lastPlayTime.value = currentTime;
}

// 播放中插广告
const playMidRollAd = async (positionIndex) => {
  try {
    // 暂停主视频
    videoPlayer.value.pause()

    // 获取广告
    const ad = await getMidRollAd()
    if (!ad) {
      // 如果没有广告，继续播放主视频
      videoPlayer.value.play()
      return
    }
    
    currentAd.value = ad
    
    // 设置广告类型
    isVideoAd.value = !!ad.playUrl
    isImageAd.value = !!ad.imageUrl

    // 记录广告展示
    await trackAdImpression(ad)

    // 标记该位置广告已展示
    adShown.value.push(positionIndex)

    // 显示广告
    showAd.value = true
    
    // 设置广告标题
    adTitle.value = ad.title || '广告'
    
    // 设置广告倒计时
    startAdCountdown()

    // 自动播放广告，处理自动播放被阻止的情况
    if (isVideoAd.value && adVideo.value) {
      setTimeout(async () => {
        try {
          // 尝试自动播放广告
          await adVideo.value.play();
          console.log('中插广告自动播放成功');
        } catch (error) {
          console.warn('中插广告自动播放被阻止，需要用户交互:', error);
          // 保持广告显示状态，允许用户手动播放
        }
      }, 100)
    }
  } catch (error) {
    console.error('播放中插广告失败:', error)
    // 如果广告播放失败，继续播放主视频
    videoPlayer.value.play()
  }
}

onMounted(async () => {
  // 加载视频数据
  const id = route.params.id
  const res = await axios.get(`http://localhost:8082/api/videos/${id}`)
  video.value = res.data

  // 初始化广告SDK
  initAdSDK()

  // 确保视频元素加载完成后再添加监听
  if (videoPlayer.value) {
    // 监听视频元数据加载完成事件
    videoPlayer.value.addEventListener('loadedmetadata', () => {
      console.log('视频元数据加载完成，开始监听播放进度');
      // 在视频元数据加载完成后添加timeupdate监听
      videoPlayer.value.addEventListener('timeupdate', checkMidRollAd)
    });
    
    // 监听视频播放事件，用于前贴片广告
    videoPlayer.value.addEventListener('play', async () => {
      // 如果前贴片广告未展示，则暂停主视频并播放广告
      if (!preRollAdShown.value) {
        videoPlayer.value.pause()
        await playPreRollAd()
      }
    });
  }
})

onBeforeUnmount(() => {
  // 移除事件监听
  if (videoPlayer.value) {
    videoPlayer.value.removeEventListener('timeupdate', checkMidRollAd)
  }
})
</script>

<style scoped>
.video-container {
  position: relative;
  display: inline-block;
}

.ad-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10;
}

.ad-content {
  background-color: white;
  padding: 20px;
  border-radius: 5px;
  text-align: center;
}

.ad-video {
  margin: 20px 0;
}

.ad-click-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.ad-click-btn:hover {
  background-color: #0056b3;
}
</style>
