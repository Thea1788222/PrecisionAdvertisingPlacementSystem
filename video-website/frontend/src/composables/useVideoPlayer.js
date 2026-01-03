import { ref, onMounted, onBeforeUnmount } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

export function useVideoPlayer() {
  const route = useRoute()
  
  // 响应式状态
  const video = ref({})
  const videoPlayer = ref(null)
  const adPositions = [0.5] // 视频播放50%位置插入广告
  const lastPlayTime = ref(0) // 记录上一次播放时间，用于检测是否经过了广告位置
  const adShown = ref([]) // 标记已展示的中插广告位置
  const isVideoReady = ref(false) // 视频是否准备就绪
  
  // 视频播放事件回调
  let onTimeUpdateCallback = null
  let onPlayCallback = null
  
  // 加载视频数据
  const loadVideo = async (id) => {
    try {
      const res = await axios.get(`http://localhost:8082/api/videos/${id}`)
      video.value = res.data
      console.log('视频加载成功:', video.value)
      return video.value
    } catch (error) {
      console.error('加载视频失败:', error)
      throw error
    }
  }
  
  // 设置视频事件监听
  const setupVideoListeners = () => {
    if (!videoPlayer.value) return
    
  // 监听视频元数据加载完成事件
    videoPlayer.value.addEventListener('loadedmetadata', () => {
      console.log('视频元数据加载完成')
      isVideoReady.value = true
      
      // 在视频元数据加载完成后添加timeupdate监听
      videoPlayer.value.addEventListener('timeupdate', handleTimeUpdate)
    })
    
    // 监听视频播放事件
    videoPlayer.value.addEventListener('play', () => {
      console.log('视频开始播放')
      if (onPlayCallback) {
        onPlayCallback()
      }
    })
  }
  
  // 设置播放进度监听
  const setTimeUpdateListener = (callback) => {
    onTimeUpdateCallback = callback
  }
  
  // 设置播放事件监听
  const setPlayListener = (callback) => {
    onPlayCallback = callback
  }
  
  // 视频播放事件处理函数
  const handleTimeUpdate = () => {
    // 调用外部回调
    if (onTimeUpdateCallback) {
      onTimeUpdateCallback()
    }
    // 检查中插广告
    checkMidRollAd()
  }
  
  // 检查是否需要播放中插广告
  const checkMidRollAd = () => {
    if (!videoPlayer.value || !video.value.duration || !isVideoReady.value) return

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
        
        // 标记该位置广告已展示
        adShown.value.push(i)
        
        // 触发中插广告回调
        if (onMidRollAdCallback) {
          onMidRollAdCallback(i)
        }
        break;
      }
    }
    
    // 更新上一次播放时间
    lastPlayTime.value = currentTime;
  }
  
  // 中插广告回调
  let onMidRollAdCallback = null
  const setMidRollAdListener = (callback) => {
    onMidRollAdCallback = callback
  }
  
  // 视频控制方法
  const play = () => {
    if (videoPlayer.value) {
      videoPlayer.value.play()
    }
  }
  
  const pause = () => {
    if (videoPlayer.value) {
      videoPlayer.value.pause()
    }
  }
  
  const getCurrentTime = () => {
    return videoPlayer.value ? videoPlayer.value.currentTime : 0
  }
  
  const getDuration = () => {
    return videoPlayer.value ? videoPlayer.value.duration : 0
  }
  
  // 获取已展示的广告位置索引
  const getShownAdPositions = () => {
    return adShown.value
  }
  
  // 清理资源
  const cleanup = () => {
    if (videoPlayer.value) {
      videoPlayer.value.removeEventListener('timeupdate', onTimeUpdateCallback)
    }
  }
  
  // 组件挂载时初始化
  onMounted(() => {
    setupVideoListeners()
  })
  
  // 组件卸载前清理
  onBeforeUnmount(() => {
    cleanup()
  })
  
  return {
    // 状态
    video,
    videoPlayer,
    isVideoReady,
    
    // 方法
    loadVideo,
    setTimeUpdateListener,
    setPlayListener,
    setMidRollAdListener,
    checkMidRollAd,
    play,
    pause,
    getCurrentTime,
    getDuration,
    getShownAdPositions
  }
}