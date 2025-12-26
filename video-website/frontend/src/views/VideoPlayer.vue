<template>
  <div>
    <h1>{{ video.title }}</h1>
    <div class="video-container">
      <video ref="videoPlayer" width="640" height="360" controls v-if="video.playUrl">
        <source :src="video.playUrl" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>

      <!-- 中插广告容器 -->
      <div v-if="showAd" class="ad-overlay">
        <div class="ad-content">
          <h3>视频中插广告</h3>
          <div class="ad-video">
            <video ref="adVideo" width="640" height="360" controls @ended="handleAdEnded">
              <source :src="currentAd.playUrl" type="video/mp4" />
              您的浏览器不支持视频播放。
            </video>
          </div>
          <div class="ad-info">
            <p>{{ currentAd.title }}</p>
            <button @click="trackAdClick" class="ad-click-btn">了解详情</button>
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
const adPositions = [0.25, 0.5, 0.75] // 视频播放25%、50%、75%位置插入广告
const lastPlayTime = ref(0) // 记录上一次播放时间，用于检测是否经过了广告位置

// 初始化广告SDK
const initAdSDK = () => {
  // 设置SDK加载超时检查
  const sdkTimeout = setTimeout(() => {
    if (!window.adTracker) {
      console.warn('广告SDK加载超时，使用模拟SDK');
      // 创建模拟的广告SDK作为备选方案
      window.adTracker = {
        init: () => console.log('模拟广告SDK初始化'),
        trackAdImpression: () => Promise.resolve('mock-impression-' + Date.now()),
        trackAdClick: () => Promise.resolve()
      };
    }
  }, 3000);

  if (window.adTracker) {
    window.adTracker.init({
      trackerServer: 'http://track.test.com:8084',
      website: 'video.test.com',
    });
    clearTimeout(sdkTimeout);
  }
}

// 获取中插广告
const getMidRollAd = async () => {
  // 模拟获取中插广告数据
  return {
    id: 401,
    title: '新产品推广广告',
    playUrl: 'http://example.com/ad-video.mp4',
    position: 'video-mid-roll',
    bidPrice: 2.5,
  }
}

// 记录广告展示
const trackAdImpression = async (ad) => {
  if (window.adTracker) {
    try {
      const impressionId = await window.adTracker.trackAdImpression(ad.id, ad.position, ad.bidPrice)
      adImpressionId.value = impressionId
      console.log('中插广告展示记录成功:', ad)
    } catch (error) {
      console.error('中插广告展示记录失败:', error)
    }
  }
}

// 记录广告点击
const trackAdClick = () => {
  if (window.adTracker && adImpressionId.value) {
    try {
      window.adTracker.trackAdClick(adImpressionId.value)
      console.log('中插广告点击记录成功:', adImpressionId.value)
    } catch (error) {
      console.error('中插广告点击记录失败:', error)
    }
  }
}

// 处理广告结束
const handleAdEnded = () => {
  showAd.value = false
  if (videoPlayer.value) {
    videoPlayer.value.play()
  }
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
    currentAd.value = ad

    // 记录广告展示
    await trackAdImpression(ad)

    // 标记该位置广告已展示
    adShown.value.push(positionIndex)

    // 显示广告
    showAd.value = true

    // 自动播放广告，处理自动播放被阻止的情况
    setTimeout(async () => {
      if (adVideo.value) {
        try {
          // 尝试自动播放广告
          await adVideo.value.play();
          console.log('广告自动播放成功');
        } catch (error) {
          console.warn('广告自动播放被阻止，需要用户交互:', error);
          // 可以在这里显示一个手动播放按钮
          // 目前我们保持广告显示状态，用户可以手动点击播放
        }
      }
    }, 100)
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
