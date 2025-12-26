<template>
  <div class="dashboard-content">
    <!-- 页面内容 -->
    <div class="content">
      <h1>仪表板</h1>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card clickable" @click="$router.push('/materials')">
          <h3>广告素材</h3>
          <p class="stat-number">{{ stats.materialCount }}</p>
        </div>
        <div class="stat-card clickable" @click="$router.push('/positions')">
          <h3>广告位</h3>
          <p class="stat-number">{{ stats.positionCount }}</p>
        </div>
        <div class="stat-card clickable" @click="$router.push('/advertisers')">
          <h3>广告商</h3>
          <p class="stat-number">{{ stats.advertiserCount }}</p>
        </div>
        <div class="stat-card clickable" @click="$router.push('/statistics')">
          <h3>今日展示</h3>
          <p class="stat-number">{{ stats.todayImpressions }}</p>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="chart-section">
        <div class="chart-container">
          <h2>广告效果趋势</h2>
          <div ref="chartContainer" class="chart-wrapper"></div>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <h2>最新广告统计数据</h2>
        <el-table :data="latestStats" style="width: 100%" v-loading="loading" :header-cell-style="{ textAlign: 'center' }">
          <el-table-column prop="adTitle" label="广告名称" width="150" align="center"></el-table-column>
          <el-table-column prop="date" label="日期" width="120" align="center"></el-table-column>
          <el-table-column prop="impressionsCount" label="展示次数" width="120" align="center"></el-table-column>
          <el-table-column prop="clicksCount" label="点击次数" width="120" align="center"></el-table-column>
          <el-table-column prop="conversionsCount" label="转化次数" width="120" align="center"></el-table-column>
          <el-table-column prop="ctr" label="点击率" width="120" align="center">
            <template #default="scope">
              {{ scope.row.ctr !== undefined && scope.row.ctr !== null ? (scope.row.ctr * 100).toFixed(2) + '%' : '0.00%' }}
            </template>
          </el-table-column>
          <el-table-column prop="cost" label="成本" width="120" align="center"></el-table-column>
          <el-table-column prop="revenue" label="收入" width="120" align="center"></el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import apiService from '@/services/apiService'

// 数据状态
const stats = ref({
  materialCount: 0,
  positionCount: 0,
  advertiserCount: 0,
  todayImpressions: 0
})

const latestStats = ref([])
const loading = ref(false)
const chartContainer = ref(null)
let chartInstance = null

// 获取统计数据
const fetchStats = async () => {
  try {
    loading.value = true

    // 获取各类统计数据
    const [materialsRes, positionsRes, advertisersRes, statsRes] = await Promise.all([
      apiService.get('/materials'),
      apiService.get('/positions'),
      apiService.get('/advertisers'),
      apiService.get('/statistics/ads')
    ])

    // 更新统计数据
    stats.value.materialCount = materialsRes.data.content ? materialsRes.data.content.length : materialsRes.data.length
    stats.value.positionCount = positionsRes.data.content ? positionsRes.data.content.length : positionsRes.data.length
    stats.value.advertiserCount = advertisersRes.data.content ? advertisersRes.data.content.length : advertisersRes.data.length
    // 处理最新的统计数据
    const sortedStats = [...statsRes.data].sort((a, b) => new Date(b.date) - new Date(a.date))
    latestStats.value = sortedStats.slice(0, 10)

    // 计算今日展示次数
    const today = new Date().toISOString().split('T')[0]
    const todayStat = sortedStats.find(stat => stat.date === today)
    stats.value.todayImpressions = todayStat ? todayStat.impressionsCount : 0

    // 渲染图表
    renderChart(sortedStats.slice(0, 7).reverse())
  } catch (error) {
    console.error('获取统计数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 渲染图表
const renderChart = (data) => {
  if (!chartContainer.value) return

  // 销毁之前的实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  // 初始化图表
  chartInstance = echarts.init(chartContainer.value)

  // 准备数据
  const dates = data.map(item => item.date)
  const impressions = data.map(item => item.impressionsCount)
  const clicks = data.map(item => item.clicksCount)

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
        data: impressions,
        smooth: true
      },
      {
        name: '点击次数',
        type: 'line',
        data: clicks,
        smooth: true
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

// 组件挂载时获取数据
onMounted(() => {
  fetchStats()
})
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
  transition: all 0.3s ease;
}

.stat-card.clickable {
  cursor: pointer;
}

.stat-card.clickable:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.stat-card h3 {
  margin: 0 0 1rem 0;
  color: #7f8c8d;
  font-size: 1.1rem;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  margin: 0;
  color: #2c3e50;
}

.chart-section {
  margin-top: 2rem;
}

.chart-container {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.chart-container h2 {
  margin-top: 0;
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
}

.chart-wrapper {
  width: 100%;
  height: 400px;
}

.table-section {
  margin-top: 2rem;
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.table-section h2 {
  margin-top: 0;
  color: #2c3e50;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
}
</style>
