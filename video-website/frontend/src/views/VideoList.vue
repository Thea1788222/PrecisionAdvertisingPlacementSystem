<template>
  <div>
    <h1>视频列表</h1>
    <div class="video-list">
      <VideoCard
        v-for="video in videos"
        :key="video.id"
        :video="video"
        @click="goToVideo(video.id)"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import VideoCard from '../components/VideoCard.vue'
import { useRouter } from 'vue-router'

const videos = ref([])
const router = useRouter()

onMounted(async () => {
  const res = await axios.get('http://localhost:8082/api/videos')
  videos.value = res.data
})

function goToVideo(id) {
  router.push(`/video/${id}`)
}
</script>

<style scoped>
.video-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}
</style>
