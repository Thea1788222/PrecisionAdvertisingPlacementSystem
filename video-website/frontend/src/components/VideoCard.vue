<template>
  <div class="video-card" @click="$emit('click')">
    <img :src="video.coverUrl" alt="封面" width="200" height="120" />
    <h3>{{ video.title }}</h3>
    <p>{{ video.description }}</p>
    <span v-if="video.category" class="category-tag">{{ video.category }}</span>
  </div>
</template>

<script setup>
defineProps({
  video: Object,
})
</script>

<style src="../styles/VideoCard.css" scoped></style>
