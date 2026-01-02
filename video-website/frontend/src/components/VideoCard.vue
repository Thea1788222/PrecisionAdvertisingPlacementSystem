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

<style scoped>
.video-card {
  border: 1px solid #ccc;
  padding: 8px;
  width: 220px;
  cursor: pointer;
  transition: transform 0.2s;
  position: relative;
}
.video-card:hover {
  transform: scale(1.05);
}
.video-card img {
  display: block;
  margin-bottom: 8px;
  object-fit: cover;
}
.category-tag {
  display: inline-block;
  background-color: #007bff;
  color: white;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 12px;
  position: absolute;
  top: 10px;
  right: 10px;
}
</style>
