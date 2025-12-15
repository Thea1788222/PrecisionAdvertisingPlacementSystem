<template>
  <section class="app-main">
    <router-view />
  </section>
</template>

<script setup>
// 主内容区域组件，用于渲染路由视图
</script>

<style scoped>
.app-main {
  flex: 1;
  overflow: auto;
  padding: 20px;
  background-color: #f0f2f5;
  height: calc(100vh - 50px);
}
</style>