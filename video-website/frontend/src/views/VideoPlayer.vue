<template>
  <div>
    <h1>{{ video.title }}</h1>
    <video width="640" height="360" controls v-if="video.playUrl">
      <source :src="video.playUrl" type="video/mp4" />
      您的浏览器不支持视频播放。
    </video>
    <p>{{ video.description }}</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'

const route = useRoute()
const video = ref({})

onMounted(async () => {
  const id = route.params.id
  const res = await axios.get(`http://localhost:8082/api/videos/${id}`)
  video.value = res.data
})
</script>
