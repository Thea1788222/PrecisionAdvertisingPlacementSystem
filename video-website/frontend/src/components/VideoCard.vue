<template>
  <div class="video-card" @click="$emit('click')">
    <img :src="video.coverUrl" alt="封面" width="200" height="120"/>
    <h3>{{ video.title }}</h3>
    <p>{{ video.description }}</p>
  </div>
</template>

<script setup>
defineProps({
  video: Object
})
</script>

<style scoped>
.video-card {
  border: 1px solid #ccc;
  padding: 8px;
  width: 220px;
  cursor: pointer;
  transition: transform 0.2s;
}
.video-card:hover {
  transform: scale(1.05);
}
.video-card img {
  display: block;
  margin-bottom: 8px;
  object-fit: cover;
}
</style>
