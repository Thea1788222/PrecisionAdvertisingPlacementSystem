/**
 * 图表相关的工具函数
 */

/**
 * 创建广告趋势图表的配置
 * @param {Array} dates - 日期数组
 * @param {Array} impressions - 展示次数数组
 * @param {Array} clicks - 点击次数数组
 * @returns {Object} 图表配置对象
 */
export const createTrendChartOption = (dates, impressions, clicks) => {
  return {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        let result = params[0].axisValue + '<br/>'
        params.forEach(param => {
          result += param.marker + ' ' + param.seriesName + ': ' + param.value + '<br/>'
        })
        return result
      }
    },
    legend: {
      data: ['展示次数', '点击次数'],
      top: '10px',  // 将图例放置在顶部
      right: '10px' // 确保图例在右侧，不与y轴标签重叠
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',  // 增加底部空间以适应x轴标签
      top: '15%',     // 增加顶部空间以适应图例
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        interval: 0,  // 显示所有标签
        // rotate: 45,   // 旋转标签以节省空间
        fontSize: 12
      }
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '展示次数',
        type: 'line',
        data: impressions,
        smooth: true,
        lineStyle: {
          color: '#66b1ff',
          width: 2
        },
        itemStyle: {
          color: '#409eff'
        },
        areaStyle: {
          opacity: 0.1
        }
      },
      {
        name: '点击次数',
        type: 'line',
        data: clicks,
        smooth: true,
        lineStyle: {
          color: '#5cb87a',
          width: 2
        },
        itemStyle: {
          color: '#67c23a'
        },
        areaStyle: {
          opacity: 0.1
        }
      }
    ]
  }
}

/**
 * 销毁图表实例
 * @param {Object} chartInstance - 图表实例
 */
export const disposeChart = (chartInstance) => {
  if (chartInstance && typeof chartInstance.dispose === 'function') {
    chartInstance.dispose()
  }
}
