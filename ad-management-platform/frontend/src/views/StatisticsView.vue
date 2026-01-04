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
            <option value="shop">购物网站</option>
            <option value="video">视频网站</option>
            <option value="news">新闻网站</option>
          </select>
        </div>
        <div class="form-group">
          <label>分布指标</label>
          <select v-model="filters.metric">
            <option value="revenue">收入</option>
            <option value="impressions">展示次数</option>
            <option value="clicks">点击次数</option>
          </select>
        </div>
        <div class="form-group">
          <button class="btn btn-secondary" @click="handleSearch" :disabled="loading.stats">
            <span v-if="loading.stats">查询中...</span>
            <span v-else>查询</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <h3>总展示次数</h3>
        <p class="stat-number">{{ formatNumber(summary.totalImpressions) }}</p>
        <p class="stat-change" :class="getChangeClass(summary.impressionsChange)">
          {{ getChangeSymbol(summary.impressionsChange) }} {{ Math.abs(summary.impressionsChange).toFixed(2) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>总点击次数</h3>
        <p class="stat-number">{{ formatNumber(summary.totalClicks) }}</p>
        <p class="stat-change" :class="getChangeClass(summary.clicksChange)">
          {{ getChangeSymbol(summary.clicksChange) }} {{ Math.abs(summary.clicksChange).toFixed(2) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>点击率</h3>
        <p class="stat-number">{{ summary.averageCtr.toFixed(2) }}%</p>
        <p class="stat-change" :class="getChangeClass(summary.ctrChange)">
          {{ getChangeSymbol(summary.ctrChange) }} {{ Math.abs(summary.ctrChange).toFixed(2) }}%
        </p>
      </div>
      <div class="stat-card">
        <h3>总收入</h3>
        <p class="stat-number">¥{{ formatNumber(summary.totalRevenue) }}</p>
        <p class="stat-change" :class="getChangeClass(summary.revenueChange)">
          {{ getChangeSymbol(summary.revenueChange) }} {{ Math.abs(summary.revenueChange).toFixed(2) }}%
        </p>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <div class="chart-card">
        <h3>展示与点击趋势</h3>
        <div v-if="loading.chart" class="loading">加载中...</div>
        <div ref="chartContainer" class="chart-container"></div>
      </div>

      <div class="chart-card">
        <h3>{{ getDistributionChartTitle }}分布</h3>
        <div v-if="loading.distribution" class="loading">加载中...</div>
        <div ref="pieChartContainer" class="chart-container"></div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <h3>详细数据</h3>
      <div v-if="loading.detail" class="loading">加载详细数据中...</div>
      <table v-else class="data-table">
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
            <td>{{ item.adTitle || '未知广告' }}</td>
            <td>{{ formatNumber(item.impressionsCount) }}</td>
            <td>{{ formatNumber(item.clicksCount) }}</td>
            <td>{{ (item.ctr * 100).toFixed(2) }}%</td>
            <td>{{ (item.revenue || 0).toFixed(2) }}</td>
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
import { ref, onMounted, watch, nextTick, onUnmounted, computed } from 'vue'
import * as echarts from 'echarts'
import apiService from '@/services/apiService'

const filters = ref({
  startDate: '',
  endDate: '',
  website: '',
  metric: 'revenue'
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

// 加载状态管理
const loading = ref({
  stats: false,
  chart: false,
  distribution: false,
  detail: false
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
    loading.value.stats = true
    
    // 并行请求多个接口
    const [summaryResponse, trendsResponse, distributionResponse, detailResponse] = await Promise.all([
      apiService.get('/statistics/summary', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          website: filters.value.website
        }
      }).finally(() => {
        loading.value.stats = false
      }),
      apiService.get('/statistics/trends', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          website: filters.value.website
        }
      }).finally(() => {
        loading.value.chart = false
      }),
      apiService.get('/statistics/distribution', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate,
          dimension: 'website',
          metric: filters.value.metric
        }
      }).finally(() => {
        loading.value.distribution = false
      }),
      apiService.get('/statistics/ads', {
        params: {
          startDate: filters.value.startDate,
          endDate: filters.value.endDate
        }
      }).finally(() => {
        loading.value.detail = false
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
    // 不再使用模拟数据，而是显示错误信息
    alert('加载统计数据失败: ' + (error.message || '未知错误'))
  }
}

// 为图表添加resize处理
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
  if (pieChartInstance) {
    pieChartInstance.resize()
  }
}

// 组件挂载时加载数据
onMounted(() => {
  loadStats()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理资源
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
  }
  if (pieChartInstance) {
    pieChartInstance.dispose()
  }
})

const handleSearch = () => {
  loadStats()
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr
}

const formatNumber = (num) => {
  if (num === null || num === undefined || isNaN(num)) return '0'
  return parseFloat(num).toLocaleString()
}

// 获取变化率符号
const getChangeSymbol = (value) => {
  if (value > 0) return '↑'
  if (value < 0) return '↓'
  return '→'
}

// 获取变化率样式类
const getChangeClass = (value) => {
  if (value > 0) return 'positive'
  if (value < 0) return 'negative'
  return 'neutral'
}

// 获取分布图标题
const getDistributionChartTitle = computed(() => {
  const metricTitles = {
    'revenue': '收入',
    'impressions': '展示次数',
    'clicks': '点击次数'
  }
  return metricTitles[filters.value.metric] || '收入'
})

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
  const metricTitles = {
    'revenue': '收入',
    'impressions': '展示次数',
    'clicks': '点击次数'
  }
  const chartTitle = metricTitles[filters.value.metric] || '收入'
  
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
        name: chartTitle + '分布',
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

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #545b62;
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

.neutral {
  color: #7f8c8d;
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
  min-height: 400px;
}

.chart-card h3 {
  margin-top: 0;
}

.chart-container {
  height: 300px;
  width: 100%;
}

.loading {
  text-align: center;
  padding: 2rem;
  color: #6c757d;
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