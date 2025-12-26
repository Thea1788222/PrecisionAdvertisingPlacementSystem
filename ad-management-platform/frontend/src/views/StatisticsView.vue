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
        <p class="stat-number">{{ formatNumber(summary.totalImpressions) }}</p>
        <p class="stat-change" :class="summary.impressionsChange >= 0 ? 'positive' : 'negative'">
          {{ summary.impressionsChange >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.impressionsChange) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>总点击次数</h3>
        <p class="stat-number">{{ formatNumber(summary.totalClicks) }}</p>
        <p class="stat-change" :class="summary.clicksChange >= 0 ? 'positive' : 'negative'">
          {{ summary.clicksChange >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.clicksChange) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>点击率</h3>
        <p class="stat-number">{{ summary.averageCtr.toFixed(2) }}%</p>
        <p class="stat-change" :class="summary.ctrChange >= 0 ? 'positive' : 'negative'">
          {{ summary.ctrChange >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.ctrChange) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>总收入</h3>
        <p class="stat-number">¥{{ formatNumber(summary.totalRevenue) }}</p>
        <p class="stat-change" :class="summary.revenueChange >= 0 ? 'positive' : 'negative'">
          {{ summary.revenueChange >= 0 ? '↑' : '↓' }} {{ Math.abs(summary.revenueChange) }}%
        </p>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>展示与点击趋势</h3>
        <div ref="chartContainer" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <h3>收入分布</h3>
        <div ref="pieChartContainer" class="chart-container"></div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <h3>详细数据</h3>
      <table class="data-table">
        <thead>
          <tr>
            <th>日期</th>
            <th>广告标题</th>
            <th>展示次数</th>
            <th>点击次数</th>
            <th>点击率</th>
            <th>收入 (元)</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in statItems" :key="item.id">
            <td>{{ formatDate(item.date) }}</td>
            <td>{{ item.adTitle }}</td>
            <td>{{ formatNumber(item.impressionsCount) }}</td>
            <td>{{ formatNumber(item.clicksCount) }}</td>
            <td>{{ (item.ctr * 100).toFixed(2) }}%</td>
            <td>{{ item.revenue.toFixed(2) }}</td>
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
import { ref, onMounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import apiService from '@/services/apiService'

const filters = ref({
  startDate: '',
  endDate: '',
  website: ''
})

const statItems = ref([])
const summary = ref({
  totalImpressions: 0,
  totalClicks: 0,
  totalRevenue: 0,
  averageCtr: 0,
  impressionsChange: 0,
  clicksChange: 0,
  revenueChange: 0,
  ctrChange: 0
})

const chartContainer = ref(null)
const pieChartContainer = ref(null)
let chartInstance = null
let pieChartInstance = null

// 挂载时加载数据
onMounted(() => {
  loadStats()
})

// 监控数据变化，重新绘制图表
watch(statItems, () => {
  nextTick(() => {
    renderChart()
    renderPieChart()
  })
})

const loadStats = async () => {
  try {
    // 并行请求多个接口
    const [summaryResponse, trendsResponse, distributionResponse, detailResponse] = await Promise.all([
      apiService.get('/statistics/summary', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          website: filters.value.website
        }
      }),
      apiService.get('/statistics/trends', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          website: filters.value.website
        }
      }),
      apiService.get('/statistics/distribution', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          dimension: 'website',
          metric: 'revenue'
        }
      }),
      apiService.get('/statistics/ads', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate
        }
      })
    ])

    // 更新摘要数据
    summary.value = summaryResponse.data

    // 更新详细数据
    statItems.value = detailResponse.data

    // 存储趋势和分布数据供图表使用
    window.trendsData = trendsResponse.data
    window.distributionData = distributionResponse.data

    // 渲染图表
    nextTick(() => {
      renderChart()
      renderPieChart()
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
    // 使用模拟数据以便开发
    loadMockData()
  }
}

const loadMockData = () => {
  summary.value = {
    totalImpressions: 125480,
    totalClicks: 8752,
    totalRevenue: 15680.00,
    averageCtr: 6.98,
    impressionsChange: 12.5,
    clicksChange: 8.3,
    revenueChange: 15.7,
    ctrChange: -1.2
  }

  statItems.value = [
    {
      id: 1,
      date: '2023-06-01',
      adTitle: '科技产品推广',
      impressionsCount: 15420,
      clicksCount: 1250,
      ctr: 0.081,
      revenue: 2450.00
    },
    {
      id: 2,
      date: '2023-06-01',
      adTitle: '时尚品牌宣传',
      impressionsCount: 22150,
      clicksCount: 1870,
      ctr: 0.084,
      revenue: 3120.00
    },
    {
      id: 3,
      date: '2023-06-01',
      adTitle: '家居用品广告',
      impressionsCount: 9800,
      clicksCount: 680,
      ctr: 0.069,
      revenue: 980.00
    }
  ]

  window.trendsData = [
    {
      date: '2023-06-01',
      impressions: 47370,
      clicks: 3800,
      revenue: 6550.00
    },
    {
      date: '2023-06-02',
      impressions: 51900,
      clicks: 4200,
      revenue: 7250.00
    }
  ]

  window.distributionData = [
    {
      dimension: '购物网站',
      metricValue: 31500,
      percentage: 35.2
    },
    {
      dimension: '视频网站',
      metricValue: 47750,
      percentage: 53.3
    },
    {
      dimension: '新闻网站',
      metricValue: 10300,
      percentage: 11.5
    }
  ]

  nextTick(() => {
    renderChart()
    renderPieChart()
  })
}

const handleSearch = () => {
  loadStats()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

// 渲染趋势图表
const renderChart = () => {
  if (!chartContainer.value) return

  // 销毁之前的实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  // 初始化图表
  chartInstance = echarts.init(chartContainer.value)

  // 准备数据
  const dates = (window.trendsData || []).map(item => item.date)
  const impressions = (window.trendsData || []).map(item => item.impressions)
  const clicks = (window.trendsData || []).map(item => item.clicks)

  // 图表配置
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['展示次数', '点击次数']
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '展示次数',
        type: 'line',
        data: impressions
      },
      {
        name: '点击次数',
        type: 'line',
        data: clicks
      }
    ]
  }

  // 渲染图表
  chartInstance.setOption(option)

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance.resize()
  })
}

// 渲染饼图
const renderPieChart = () => {
  if (!pieChartContainer.value) return

  // 销毁之前的实例
  if (pieChartInstance) {
    pieChartInstance.dispose()
  }

  // 初始化图表
  pieChartInstance = echarts.init(pieChartContainer.value)

  // 准备数据
  const data = (window.distributionData || []).map(item => ({
    name: item.dimension,
    value: item.metricValue
  }))

  // 图表配置
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '收入分布',
        type: 'pie',
        radius: '50%',
        data: data,
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  // 渲染图表
  pieChartInstance.setOption(option)

  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    pieChartInstance.resize()
  })
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

.chart-container {
  height: 300px;
  width: 100%;
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
