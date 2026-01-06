<template>
  <div class="dashboard-content">
    <!-- 页面内容 -->
    <div class="content">
      <h1>仪表板</h1>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <DashboardStatCard
          title="广告素材"
          :value="stats.materialCount"
          :trend="stats.materialTrend"
          :loading="loading.stats"
          @click="$router.push('/materials')"
        />
        <DashboardStatCard
          title="广告位"
          :value="stats.positionCount"
          :trend="stats.positionTrend"
          :loading="loading.stats"
          @click="$router.push('/positions')"
        />
        <DashboardStatCard
          title="广告商"
          :value="stats.advertiserCount"
          :trend="stats.advertiserTrend"
          :loading="loading.stats"
          @click="$router.push('/advertisers')"
        />
        <DashboardStatCard
          title="今日展示"
          :value="stats.todayImpressions"
          :trend="stats.impressionTrend"
          :loading="loading.stats"
          @click="$router.push('/statistics')"
        />
      </div>

      <!-- 图表区域 -->
      <div class="chart-section">
        <div class="chart-container">
          <h2>广告效果趋势</h2>
          <div ref="chartContainer" class="chart-wrapper" v-loading="loading.chart"></div>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <h2>最新广告统计数据</h2>
        <el-table
          :data="latestStats"
          style="width: 100%"
          v-loading="loading.table"
          :header-cell-style="{ textAlign: 'center' }"
          :row-class-name="tableRowClassName"
        >
          <el-table-column prop="adTitle" label="广告名称" width="150" align="center"></el-table-column>
          <el-table-column prop="date" label="日期" width="120" align="center"></el-table-column>
          <el-table-column prop="impressionsCount" label="展示次数" width="120" align="center"></el-table-column>
          <el-table-column prop="clicksCount" label="点击次数" width="120" align="center"></el-table-column>
          <el-table-column prop="conversionsCount" label="转化次数" width="120" align="center"></el-table-column>
          <el-table-column prop="ctr" label="点击率" width="120" align="center">
            <template #default="scope">
              {{ formatPercentage(scope.row.ctr) }}
            </template>
          </el-table-column>
          <el-table-column prop="cost" label="成本" width="120" align="center"></el-table-column>
          <el-table-column prop="revenue" label="收入" width="120" align="center"></el-table-column>
        </el-table>
        <div class="pagination-container" v-if="totalStats > pageSize">
          <el-pagination
            @current-change="handlePageChange"
            :current-page="currentPage"
            :page-size="pageSize"
            :total="totalStats"
            layout="total, prev, pager, next"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import apiService from '@/services/apiService'
import DashboardStatCard from '@/components/DashboardStatCard.vue'
import { formatPercentage, debounce } from '@/utils/dashboardUtils'
import { createTrendChartOption, disposeChart } from '@/utils/chartUtils'

// 数据状态
const stats = ref({
  materialCount: 0,
  positionCount: 0,
  advertiserCount: 0,
  todayImpressions: 0,
  materialTrend: 0,
  positionTrend: 0,
  advertiserTrend: 0,
  impressionTrend: 0
})

const latestStats = ref([])
const loading = ref({
  stats: false,
  chart: false,
  table: false
})
const chartContainer = ref(null)
let chartInstance = null

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const totalStats = ref(0)

// 错误状态
const error = ref(null)

// 获取统计数据
const fetchStats = async () => {
  try {
    loading.value.stats = true
    error.value = null

    // 并行获取仪表盘摘要数据和广告统计数据
    const [dashboardRes, statsRes] = await Promise.all([
      apiService.get('/statistics/dashboard').catch(err => {
        console.error('获取仪表盘数据失败:', err)
        return { data: { materialCount: 0, positionCount: 0, advertiserCount: 0, todayImpressions: 0 } }
      }),
      apiService.get('/statistics/ads').catch(err => {
        console.error('获取广告统计数据失败:', err)
        return { data: [] }
      })
    ])

    // 合并趋势数据，如果后端没有提供趋势数据则默认为0
    stats.value = {
      ...dashboardRes.data,
      materialTrend: dashboardRes.data.materialTrend || 0,
      positionTrend: dashboardRes.data.positionTrend || 0,
      advertiserTrend: dashboardRes.data.advertiserTrend || 0,
      impressionTrend: dashboardRes.data.impressionTrend || 0
    }

    // 处理最新的统计数据
    const sortedStats = [...statsRes.data].sort((a, b) => new Date(b.date) - new Date(a.date))
    totalStats.value = sortedStats.length
    const startIndex = (currentPage.value - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    latestStats.value = sortedStats.slice(startIndex, endIndex)

    // 获取趋势数据用于图表
    const trendsRes = await apiService.get('/statistics/trends').catch(err => {
      console.error('获取趋势数据失败:', err)
      return { data: [] }
    })

    // 渲染图表
    renderChart(trendsRes.data)
  } catch (error) {
    console.error('获取统计数据失败:', error)
    error.value = error.message
  } finally {
    loading.value.stats = false
  }
}

// 获取分页数据
const fetchPagedStats = async (page = 1) => {
  try {
    loading.value.table = true
    const statsRes = await apiService.get('/statistics/ads')
    const sortedStats = [...statsRes.data].sort((a, b) => new Date(b.date) - new Date(a.date))
    totalStats.value = sortedStats.length

    const startIndex = (page - 1) * pageSize.value
    const endIndex = startIndex + pageSize.value
    latestStats.value = sortedStats.slice(startIndex, endIndex)
  } catch (error) {
    console.error('获取分页统计数据失败:', error)
  } finally {
    loading.value.table = false
  }
}

// 渲染图表
const renderChart = (data) => {
  if (!chartContainer.value) return

  loading.value.chart = true

  // 销毁之前的实例
  if (chartInstance) {
    disposeChart(chartInstance);
  }

  // 初始化图表
  chartInstance = echarts.init(chartContainer.value)

  // 准备数据
  const dates = data.map(item => item.date)
  const impressions = data.map(item => item.impressions)
  const clicks = data.map(item => item.clicks)

  // 图表配置
  const option = createTrendChartOption(dates, impressions, clicks)

  // 渲染图表
  chartInstance.setOption(option)

  // 使用节流优化resize事件
  let resizeTimeout = null;
  const handleResize = () => {
    if (resizeTimeout) {
      clearTimeout(resizeTimeout);
    }
    resizeTimeout = setTimeout(() => {
      if (chartInstance) {
        chartInstance.resize()
      }
    }, 100);
  };

  window.addEventListener('resize', handleResize);

  // 组件卸载时清理事件监听器和定时器
  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    if (resizeTimeout) {
      clearTimeout(resizeTimeout);
    }
    disposeChart(chartInstance);
  });

  loading.value.chart = false
}

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page
  fetchPagedStats(page)
}

// 表格行样式
const tableRowClassName = ({ row, rowIndex }) => {
  if (rowIndex % 2 === 1) {
    return 'warning-row'
  }
  return ''
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
  min-height: 300px;
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

.pagination-container {
  margin-top: 1.5rem;
  display: flex;
  justify-content: center;
}

.el-table .warning-row {
  background: #f0f9ec;
}
</style>
