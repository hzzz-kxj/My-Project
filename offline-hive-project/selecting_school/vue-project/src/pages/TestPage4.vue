<template>
  <div class="page4-container">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <h2>热门专业与地区统计</h2>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="fetchData">刷新数据</el-button>
        <el-button size="small" @click="resetCharts">重置视图</el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：热门专业人数TOP10 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>热门专业人数TOP10</h3>
            <div class="chart-actions">
              <el-select v-model="majorSortType" size="small" style="width: 120px" @change="sortMajorData">
                <el-option label="人数降序" value="desc" />
                <el-option label="人数升序" value="asc" />
                <el-option label="按名称排序" value="name" />
              </el-select>
              <el-tooltip content="显示/隐藏数据标签" placement="top">
                <el-switch v-model="showMajorLabels" size="small" @change="toggleMajorLabels" />
              </el-tooltip>
            </div>
          </div>
          <div ref="majorChartRef" style="width: 100%; height: 400px;"></div>

          <!-- 专业数据表格 -->
          <div class="data-summary">
            <h4>专业人数数据详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">排名</th>
                  <th>专业名称</th>
                  <th width="120">人数</th>
                  <th width="120">占比</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in majorChartData" :key="item.majorName">
                  <td class="text-center">
                    <el-tag size="small" :type="getRankTag(index + 1)">
                      {{ index + 1 }}
                    </el-tag>
                  </td>
                  <td>{{ item.majorName }}</td>
                  <td class="text-right">
                    <span class="count-value">{{ formatNumber(item.count) }}人</span>
                  </td>
                  <td class="text-center">
                    <el-progress
                      :percentage="getMajorPercentage(item.count)"
                      :show-text="false"
                      :stroke-width="10"
                      :color="getProgressColor(index)"
                    />
                    <span class="percentage-text">{{ getMajorPercentage(item.count) }}%</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </el-scrollbar>
          </div>
        </div>
      </div>

      <!-- 右侧：热门地区人数TOP10 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>热门地区人数TOP10</h3>
            <div class="chart-actions">
              <el-select v-model="provinceSortType" size="small" style="width: 120px" @change="sortProvinceData">
                <el-option label="人数降序" value="desc" />
                <el-option label="人数升序" value="asc" />
                <el-option label="按名称排序" value="name" />
              </el-select>
              <el-tooltip content="显示/隐藏数据标签" placement="top">
                <el-switch v-model="showProvinceLabels" size="small" @change="toggleProvinceLabels" />
              </el-tooltip>
            </div>
          </div>
          <div ref="provinceChartRef" style="width: 100%; height: 400px;"></div>

          <!-- 地区数据表格 -->
          <div class="data-summary">
            <h4>地区人数数据详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">排名</th>
                  <th>地区名称</th>
                  <th width="120">人数</th>
                  <th width="120">占比</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in provinceChartData" :key="item.schoolAddress">
                  <td class="text-center">
                    <el-tag size="small" :type="getRankTag(index + 1)">
                      {{ index + 1 }}
                    </el-tag>
                  </td>
                  <td>{{ item.schoolAddress }}</td>
                  <td class="text-right">
                    <span class="count-value">{{ formatNumber(item.count) }}人</span>
                  </td>
                  <td class="text-center">
                    <el-progress
                      :percentage="getProvincePercentage(item.count)"
                      :show-text="false"
                      :stroke-width="10"
                      :color="getProgressColor(index)"
                    />
                    <span class="percentage-text">{{ getProvincePercentage(item.count) }}%</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </el-scrollbar>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部统计信息 -->
    <div class="bottom-stats">
      <el-card shadow="hover" class="stat-card">
        <template #header>
          <div class="stat-header">统计分析</div>
        </template>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon><School /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">专业总数</div>
              <div class="stat-value">{{ majorData.length }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon><Location /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">地区总数</div>
              <div class="stat-value">{{ provinceData.length }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">最大专业人数</div>
              <div class="stat-value">{{ formatNumber(maxMajorCount) }}人</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">最大地区人数</div>
              <div class="stat-value">{{ formatNumber(maxProvinceCount) }}人</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { School, Location, User, TrendCharts } from '@element-plus/icons-vue'

// 图表引用
const majorChartRef = ref<HTMLElement>()
const provinceChartRef = ref<HTMLElement>()

let majorChart: echarts.ECharts | null = null
let provinceChart: echarts.ECharts | null = null

// 原始数据
const majorData = ref<any[]>([])
const provinceData = ref<any[]>([])

// 图表数据（排序后的）
const majorChartData = ref<any[]>([])
const provinceChartData = ref<any[]>([])

// 排序类型
const majorSortType = ref('desc')
const provinceSortType = ref('desc')

// 显示设置
const showMajorLabels = ref(true)
const showProvinceLabels = ref(true)

// 计算属性
const maxMajorCount = computed(() => {
  return majorData.value.length > 0 ? Math.max(...majorData.value.map(item => item.count)) : 0
})

const maxProvinceCount = computed(() => {
  return provinceData.value.length > 0 ? Math.max(...provinceData.value.map(item => item.count)) : 0
})

const majorTotalCount = computed(() => {
  return majorData.value.reduce((sum, item) => sum + item.count, 0)
})

const provinceTotalCount = computed(() => {
  return provinceData.value.reduce((sum, item) => sum + item.count, 0)
})

// 获取数据
const fetchData = async () => {
  try {
    // 获取专业人数数据
    const majorResponse = await axios.get("/school/majorNumber")
    majorData.value = majorResponse.data
    console.log('专业人数数据:', majorData.value)

    // 获取地区人数数据
    const provinceResponse = await axios.get("/school/provinceNumber")
    provinceData.value = provinceResponse.data
    console.log('地区人数数据:', provinceData.value)

    // 初始化图表数据
    sortMajorData()
    sortProvinceData()

    // 渲染图表
    renderCharts()

    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('数据加载失败，请检查网络连接')
  }
}

// 排序专业数据
const sortMajorData = () => {
  const data = [...majorData.value]

  if (majorSortType.value === 'desc') {
    // 人数降序
    data.sort((a, b) => b.count - a.count)
  } else if (majorSortType.value === 'asc') {
    // 人数升序
    data.sort((a, b) => a.count - b.count)
  } else {
    // 按名称排序
    data.sort((a, b) => a.majorName.localeCompare(b.majorName))
  }

  majorChartData.value = data
  if (majorChart) {
    renderMajorChart()
  }
}

// 排序地区数据
const sortProvinceData = () => {
  const data = [...provinceData.value]

  if (provinceSortType.value === 'desc') {
    // 人数降序
    data.sort((a, b) => b.count - a.count)
  } else if (provinceSortType.value === 'asc') {
    // 人数升序
    data.sort((a, b) => a.count - b.count)
  } else {
    // 按名称排序
    data.sort((a, b) => a.schoolAddress.localeCompare(b.schoolAddress))
  }

  provinceChartData.value = data
  if (provinceChart) {
    renderProvinceChart()
  }
}

// 渲染图表
const renderCharts = () => {
  renderMajorChart()
  renderProvinceChart()
}

// 渲染专业人数图表
const renderMajorChart = () => {
  if (!majorChartRef.value) return

  // 销毁之前的图表实例
  if (majorChart) {
    majorChart.dispose()
  }

  // 初始化图表
  majorChart = echarts.init(majorChartRef.value)

  // 准备数据
  const majorNames = majorChartData.value.map(item => {
    return item.majorName.length > 6 ? item.majorName.substring(0, 6) + '...' : item.majorName
  })

  const counts = majorChartData.value.map(item => item.count)

  // 生成颜色数组
  const colors = generateColors(counts.length, majorSortType.value, '#1890ff')

  majorChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      formatter: (params: any) => {
        const data = params[0]
        const originalData = majorChartData.value[data.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 4px;">
              ${originalData.majorName}
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">人数：</span>
              <span style="font-weight: bold; color: #1890ff;">${formatNumber(originalData.count)}人</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">占比：</span>
              <span style="font-weight: bold; color: #67c23a;">${getMajorPercentage(originalData.count)}%</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">排名：</span>
              <span style="font-weight: bold;">第${data.dataIndex + 1}名</span>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      top: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: majorNames,
      axisLabel: {
        interval: 0,
        rotate: majorSortType.value === 'name' ? 0 : 25,
        fontSize: 11
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      nameTextStyle: {
        padding: [0, 0, 0, 40]
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#e8e8e8'
        }
      }
    },
    series: [
      {
        name: '专业人数',
        type: 'bar',
        barWidth: '50%',
        data: counts.map((count, index) => ({
          value: count,
          itemStyle: {
            color: colors[index],
            borderColor: colors[index],
            borderWidth: 1
          }
        })),
        label: {
          show: showMajorLabels.value,
          position: 'top',
          formatter: (params: any) => {
            return formatNumber(params.value)
          },
          fontSize: 10,
          color: '#606266'
        },
        itemStyle: {
          borderRadius: [6, 6, 0, 0],
          shadowColor: 'rgba(0, 0, 0, 0.05)',
          shadowBlur: 3,
          shadowOffsetY: 2
        },
        emphasis: {
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.2)',
            shadowBlur: 6,
            shadowOffsetY: 3
          }
        }
      }
    ]
  })
}

// 渲染地区人数图表
const renderProvinceChart = () => {
  if (!provinceChartRef.value) return

  // 销毁之前的图表实例
  if (provinceChart) {
    provinceChart.dispose()
  }

  // 初始化图表
  provinceChart = echarts.init(provinceChartRef.value)

  // 准备数据
  const provinceNames = provinceChartData.value.map(item => item.schoolAddress)
  const counts = provinceChartData.value.map(item => item.count)

  // 生成颜色数组
  const colors = generateColors(counts.length, provinceSortType.value, '#fa541c')

  provinceChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow',
        shadowStyle: {
          color: 'rgba(150, 150, 150, 0.1)'
        }
      },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      formatter: (params: any) => {
        const data = params[0]
        const originalData = provinceChartData.value[data.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 4px;">
              ${originalData.schoolAddress}
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">人数：</span>
              <span style="font-weight: bold; color: #fa541c;">${formatNumber(originalData.count)}人</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">占比：</span>
              <span style="font-weight: bold; color: #67c23a;">${getProvincePercentage(originalData.count)}%</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">排名：</span>
              <span style="font-weight: bold;">第${data.dataIndex + 1}名</span>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      top: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: provinceNames,
      axisLabel: {
        interval: 0,
        rotate: provinceSortType.value === 'name' ? 0 : 0,
        fontSize: 12
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      nameTextStyle: {
        padding: [0, 0, 0, 40]
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#e8e8e8'
        }
      }
    },
    series: [
      {
        name: '地区人数',
        type: 'bar',
        barWidth: '50%',
        data: counts.map((count, index) => ({
          value: count,
          itemStyle: {
            color: colors[index],
            borderColor: colors[index],
            borderWidth: 1
          }
        })),
        label: {
          show: showProvinceLabels.value,
          position: 'top',
          formatter: (params: any) => {
            return formatNumber(params.value)
          },
          fontSize: 10,
          color: '#606266'
        },
        itemStyle: {
          borderRadius: [0, 0, 6, 6],
          shadowColor: 'rgba(0, 0, 0, 0.05)',
          shadowBlur: 3,
          shadowOffsetY: 2
        },
        emphasis: {
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.2)',
            shadowBlur: 6,
            shadowOffsetY: 3
          }
        }
      }
    ]
  })
}

// 生成颜色数组
const generateColors = (count: number, sortType: string, baseColor: string): string[] => {
  const colors = []

  for (let i = 0; i < count; i++) {
    if (sortType === 'desc') {
      // 降序时，第一名颜色最深
      const opacity = 1 - (i / count) * 0.5
      colors.push(adjustColor(baseColor, opacity))
    } else if (sortType === 'asc') {
      // 升序时，最后一名颜色最深
      const opacity = 0.5 + (i / count) * 0.5
      colors.push(adjustColor(baseColor, opacity))
    } else {
      // 按名称排序时使用交替颜色
      colors.push(i % 2 === 0 ? baseColor : adjustColor(baseColor, 0.8))
    }
  }

  return colors
}

// 调整颜色透明度
const adjustColor = (color: string, opacity: number): string => {
  if (color.startsWith('#')) {
    const r = parseInt(color.slice(1, 3), 16)
    const g = parseInt(color.slice(3, 5), 16)
    const b = parseInt(color.slice(5, 7), 16)
    return `rgba(${r}, ${g}, ${b}, ${opacity})`
  }
  return color
}

// 辅助函数
const formatNumber = (num: number): string => {
  return num.toLocaleString('zh-CN')
}

const getRankTag = (rank: number): string => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return ''
}

const getMajorPercentage = (count: number): number => {
  if (majorTotalCount.value === 0) return 0
  return Math.round((count / majorTotalCount.value) * 10000) / 100
}

const getProvincePercentage = (count: number): number => {
  if (provinceTotalCount.value === 0) return 0
  return Math.round((count / provinceTotalCount.value) * 10000) / 100
}

const getProgressColor = (index: number): string => {
  const colors = ['#67c23a', '#e6a23c', '#f56c6c', '#409eff']
  return colors[index % colors.length]
}

// 切换数据标签显示
const toggleMajorLabels = () => {
  if (majorChart) {
    const option = majorChart.getOption()
    option.series[0].label.show = showMajorLabels.value
    majorChart.setOption(option)
  }
}

const toggleProvinceLabels = () => {
  if (provinceChart) {
    const option = provinceChart.getOption()
    option.series[0].label.show = showProvinceLabels.value
    provinceChart.setOption(option)
  }
}

// 重置图表
const resetCharts = () => {
  majorSortType.value = 'desc'
  provinceSortType.value = 'desc'
  sortMajorData()
  sortProvinceData()
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  majorChart?.resize()
  provinceChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  majorChart?.dispose()
  provinceChart?.dispose()
})
</script>

<style scoped>
.page4-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: white;
  font-size: 20px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.header-actions {
  display: flex;
  gap: 10px;
}

.main-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.chart-section {
  display: flex;
  flex-direction: column;
}

.chart-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  height: 100%;
  display: flex;
  flex-direction: column;
  border: 1px solid #ebeef5;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.chart-header h3 {
  margin: 0;
  font-size: 16px;
  color: #303133;
  font-weight: 600;
}

.chart-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.data-summary {
  margin-top: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  border-top: 1px solid #ebeef5;
  padding-top: 15px;
}

.data-summary h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.summary-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
}

.summary-table th {
  background-color: #f5f7fa;
  padding: 8px;
  text-align: left;
  font-weight: bold;
  color: #909399;
  border-bottom: 1px solid #ebeef5;
}

.summary-table td {
  padding: 8px;
  border-bottom: 1px solid #ebeef5;
  color: #606266;
}

.summary-table tr:hover {
  background-color: #f5f7fa;
}

.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

.count-value {
  font-weight: bold;
  color: #1890ff;
}

.percentage-text {
  font-size: 11px;
  color: #909399;
  margin-top: 4px;
  display: inline-block;
}

.bottom-stats {
  margin-top: 20px;
}

.stat-card {
  width: 100%;
  border: none;
  border-radius: 8px;
}

.stat-header {
  font-weight: bold;
  color: #667eea;
  font-size: 14px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 10px;
  border-radius: 6px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  transition: all 0.3s ease;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-right: 15px;
  color: white;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  letter-spacing: 0.5px;
}

/* 自定义滚动条 */
:deep(.el-scrollbar) {
  height: 100%;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

:deep(.el-scrollbar__thumb) {
  background-color: rgba(24, 144, 255, 0.3);
}

:deep(.el-scrollbar__thumb:hover) {
  background-color: #1890ff;
}

:deep(.el-progress-bar) {
  padding-right: 0;
  margin-right: 0;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }

  .header-actions {
    align-self: flex-end;
  }

  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .chart-actions {
    align-self: flex-end;
  }
}
</style>
