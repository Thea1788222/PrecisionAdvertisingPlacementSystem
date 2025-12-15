<template>
  <div class="statistics-page">
    <div class="page-header">
      <h1>数据统计</h1>
    </div>

    <!-- 时间筛选 -->
    <div class="filter-section">
      <div class="filter-row">
        <div class="form-group">
          <label>开始日期</label>
          <input
            v-model="filters.startDate"
            type="date"
          />
        </div>
        <div class="form-group">
          <label>结束日期</label>
          <input
            v-model="filters.endDate"
            type="date"
          />
        </div>
        <div class="form-group">
          <label>网站</label>
          <select v-model="filters.website">
            <option value="">全部网站</option>
            <option value="shopping">购物网站</option>
            <option value="video">视频网站</option>
            <option value="news">新闻网站</option>
          </select>
        </div>
        <div class="form-group">
          <button class="btn btn-secondary" @click="handleSearch">
            查询
          </button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>总展示次数</h3>
        <p class="stat-number">125,480</p>
        <p class="stat-change positive">↑ 12.5%</p>
      </div>
      <div class="stat-card">
        <h3>总点击次数</h3>
        <p class="stat-number">8,752</p>
        <p class="stat-change positive">↑ 8.3%</p>
      </div>
      <div class="stat-card">
        <h3>点击率</h3>
        <p class="stat-number">6.98%</p>
        <p class="stat-change negative">↓ 1.2%</p>
      </div>
      <div class="stat-card">
        <h3>总收入</h3>
        <p class="stat-number">¥15,680</p>
        <p class="stat-change positive">↑ 15.7%</p>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>展示与点击趋势</h3>
        <div class="chart-placeholder">
          <p>展示与点击趋势图表</p>
          <div class="chart-demo">
            <div class="chart-bar" style="height: 80%"></div>
            <div class="chart-bar" style="height: 60%"></div>
            <div class="chart-bar" style="height: 90%"></div>
            <div class="chart-bar" style="height: 70%"></div>
            <div class="chart-bar" style="height: 85%"></div>
            <div class="chart-bar" style="height: 75%"></div>
            <div class="chart-bar" style="height: 95%"></div>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <h3>收入分布</h3>
        <div class="chart-placeholder">
          <p>收入分布饼图</p>
          <div class="pie-chart-demo">
            <div class="pie-slice slice-1"></div>
            <div class="pie-slice slice-2"></div>
            <div class="pie-slice slice-3"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <h3>详细数据</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>日期</th>
            <th>网站</th>
            <th>展示次数</th>
            <th>点击次数</th>
            <th>点击率</th>
            <th>收入 (元)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in statItems" :key="item.date">
            <td>{{ item.date }}</td>
            <td>{{ item.website }}</td>
            <td>{{ item.impressions }}</td>
            <td>{{ item.clicks }}</td>
            <td>{{ item.ctr }}%</td>
            <td>{{ item.revenue }}</td>
          </tr>
          <tr v-if="statItems.length === 0">
            <td colspan="6" class="empty-state">
              暂无数据
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const filters = ref({
  startDate: '',
  endDate: '',
  website: ''
})

const statItems = ref([])

// 模拟数据
onMounted(() => {
  loadStats()
})

const loadStats = () => {
  // 模拟 API 调用
  statItems.value = [
    {
      date: '2023-06-01',
      website: '购物网站',
      impressions: 15420,
      clicks: 1250,
      ctr: 8.1,
      revenue: 2450
    },
    {
      date: '2023-06-01',
      website: '视频网站',
      impressions: 22150,
      clicks: 1870,
      ctr: 8.4,
      revenue: 3120
    },
    {
      date: '2023-06-01',
      website: '新闻网站',
      impressions: 9800,
      clicks: 680,
      ctr: 6.9,
      revenue: 980
    }
  ]
}

const handleSearch = () => {
  loadStats()
}
</script>

<style scoped>
.statistics-page {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  margin: 0;
}

.filter-section {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 4px;
  margin-bottom: 2rem;
}

.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #555;
}

.form-group input,
.form-group select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.form-row {
  display: flex;
  gap: 1rem;
}

.btn {
  display: inline-block;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  text-align: center;
  text-decoration: none;
  transition: all 0.3s ease;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #0056b3;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background-color: #bd2130;
}

.btn-outline {
  background-color: transparent;
  border: 1px solid #007bff;
  color: #007bff;
}

.btn-outline:hover {
  background-color: #007bff;
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
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
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
}

.stat-change {
  margin: 0;
  font-size: 0.875rem;
}

.positive {
  color: #27ae60;
}

.negative {
  color: #e74c3c;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.chart-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-card h3 {
  margin-top: 0;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.chart-demo {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  height: 150px;
  gap: 10px;
  margin-top: 20px;
}

.chart-bar {
  width: 30px;
  background-color: #3498db;
  border-radius: 4px 4px 0 0;
}

.pie-chart-demo {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}

.pie-slice {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}

.slice-1 {
  background: conic-gradient(#3498db 0deg 120deg, transparent 120deg 360deg);
}

.slice-2 {
  background: conic-gradient(#2ecc71 0deg 90deg, transparent 90deg 360deg);
}

.slice-3 {
  background: conic-gradient(#e74c3c 0deg 150deg, transparent 150deg 360deg);
}

.table-container {
  overflow-x: auto;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th,
.data-table td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

.data-table th {
  background-color: #f8f9fa;
  font-weight: 600;
}

.data-table h3 {
  margin: 2rem 0 1rem 0;
  color: #333;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #6c757d;
}
</style>