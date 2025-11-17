<template>
  <div>
    <h2>视频列表</h2>

    <div v-if="loading">加载中...</div>

    <div v-else>
      <div
        v-for="video in videos"
        :key="video.id"
        style="margin-bottom: 10px;"
      >
        <img :src="video.thumbnailUrl" style="width: 120px;" />
        <div>{{ video.title }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "VideoList",
  data() {
    return {
      videos: [],
      loading: true,
    };
  },
  mounted() {
    axios
      .get("http://localhost:8082/api/videos")
      .then((res) => {
        this.videos = res.data;
      })
      .finally(() => {
        this.loading = false;
      });
  },
};
</script>
