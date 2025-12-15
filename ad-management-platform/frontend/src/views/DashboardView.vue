<template>
  <div class="dashboard-content">
    <!-- 页面内容 -->
    <div class="content">
      <h1>仪表板</h1>
      <div class="stats-grid">
        <div class="stat-card">
          <h3>广告素材</h3>
          <p class="stat-number">128</p>
        </div>
        <div class="stat-card">
          <h3>广告位</h3>
          <p class="stat-number">24</p>
        </div>
        <div class="stat-card">
          <h3>广告商</h3>
          <p class="stat-number">15</p>
        </div>
        <div class="stat-card">
          <h3>今日展示</h3>
          <p class="stat-number">1,248</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
// 仪表板页面内容
</script>

<style scoped>
.content {
  padding: 2rem;
  background-color: #f8f9fa;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 2rem;
}

.stat-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-card h3 {
  margin: 0 0 1rem 0;
  color: #7f8c8d;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  margin: 0;
  color: #2c3e50;
}
</style>
