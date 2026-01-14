<template>
  <div class="page5-container">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <h2>学校分布与选择倾向分析</h2>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="fetchData">刷新数据</el-button>
        <el-button size="small" @click="resetCharts">重置视图</el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：学生选择倾向饼状图 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学生选择倾向分析</h3>
            <div v-if="preferenceData.length > 0" class="chart-actions">
              <el-tooltip content="切换图例位置" placement="top">
                <el-select v-model="pieLegendPosition" size="small" style="width: 100px" @change="updatePieChart">
                  <el-option label="右侧" value="right" />
                  <el-option label="底部" value="bottom" />
                  <el-option label="顶部" value="top" />
                </el-select>
              </el-tooltip>
            </div>
          </div>

          <!-- 饼状图容器 -->
          <div v-if="preferenceData.length > 0" ref="pieChartRef" style="width: 100%; height: 400px;"></div>
          <div v-else class="empty-chart">
            <el-empty description="暂无选择倾向数据" :image-size="100">
              <template #description>
                <p>暂无学生选择倾向数据</p>
                <p class="empty-tip">请等待数据加载或检查数据源</p>
              </template>
            </el-empty>
          </div>

          <!-- 倾向分析数据表格 -->
          <div v-if="preferenceData.length > 0" class="data-summary">
            <h4>选择倾向数据详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">类型</th>
                  <th>倾向说明</th>
                  <th width="100">人数</th>
                  <th width="120">占比</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="item in preferenceData" :key="item.majorOrRank">
                  <td class="text-center">
                    <div class="color-indicator" :style="{backgroundColor: item.color}"></div>
                  </td>
                  <td>
                    <span :style="{color: item.color, fontWeight: 'bold'}">{{ getPreferenceName(item.majorOrRank) }}</span>
                    <div class="desc-text">{{ item.description }}</div>
                  </td>
                  <td class="text-right">
                    <span class="count-value">{{ formatNumber(item.preferenceCount) }}人</span>
                  </td>
                  <td class="text-center">
                    <el-progress
                      :percentage="item.percentage"
                      :show-text="false"
                      :stroke-width="12"
                      :color="item.color"
                    />
                    <span class="percentage-text">{{ item.percentage }}%</span>
                  </td>
                </tr>
                </tbody>
              </table>
            </el-scrollbar>
          </div>
        </div>
      </div>

      <!-- 右侧：不同地区学校数量柱状图 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>不同地区学校数量TOP10</h3>
            <div v-if="barChartData.length > 0" class="chart-actions">
              <el-select v-model="barSortType" size="small" style="width: 120px" @change="sortBarData">
                <el-option label="数量降序" value="desc" />
                <el-option label="数量升序" value="asc" />
                <el-option label="按地区排序" value="name" />
              </el-select>
              <el-tooltip content="显示/隐藏数据标签" placement="top">
                <el-switch v-model="showBarLabels" size="small" @change="toggleBarLabels" />
              </el-tooltip>
            </div>
          </div>

          <!-- 柱状图容器 -->
          <div v-if="barChartData.length > 0" ref="barChartRef" style="width: 100%; height: 400px;"></div>
          <div v-else class="empty-chart">
            <el-empty description="暂无学校数量数据" :image-size="100">
              <template #description>
                <p>暂无地区学校数量数据</p>
                <p class="empty-tip">请等待数据加载或检查数据源</p>
              </template>
            </el-empty>
          </div>

          <!-- 学校数量数据表格 -->
          <div v-if="barChartData.length > 0" class="data-summary">
            <h4>地区学校数量详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">排名</th>
                  <th>地区名称</th>
                  <th width="100">学校数量</th>
                  <th width="120">占比</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in barChartData" :key="item.schoolAddress">
                  <td class="text-center">
                    <el-tag size="small" :type="getRankTag(index + 1)">
                      {{ index + 1 }}
                    </el-tag>
                  </td>
                  <td>{{ item.schoolAddress }}</td>
                  <td class="text-right">
                    <span class="count-value">{{ formatNumber(item.count) }}所</span>
                  </td>
                  <td class="text-center">
                    <el-progress
                      :percentage="getSchoolPercentage(item.count)"
                      :show-text="false"
                      :stroke-width="12"
                      :color="getProgressColor(index)"
                    />
                    <span class="percentage-text">{{ getSchoolPercentage(item.count) }}%</span>
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);">
              <el-icon><PieChart /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">选择倾向比</div>
              <div class="stat-value">
                {{ preferenceData.length > 0 ? `${preferenceData[0]?.percentage || 0}% : ${preferenceData[1]?.percentage || 0}%` : '暂无数据' }}
              </div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%);">
              <el-icon><School /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">地区总数</div>
              <div class="stat-value">{{ barChartData.length > 0 ? barChartData.length : 0 }}个</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fad0c4 0%, #ffd1ff 100%);">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">学校总数</div>
              <div class="stat-value">{{ barChartData.length > 0 ? formatNumber(totalSchools) : 0 }}所</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">最多学校地区</div>
              <div class="stat-value">{{ maxSchoolArea?.schoolAddress || '暂无数据' }}</div>
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
import { PieChart, School, OfficeBuilding, TrendCharts } from '@element-plus/icons-vue'

// 图表引用
const pieChartRef = ref<HTMLElement>()
const barChartRef = ref<HTMLElement>()

let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

// 原始数据
const preferenceRawData = ref<any[]>([])
const schoolAddressData = ref<any[]>([])

// 图表数据
const preferenceData = ref<any[]>([])
const barChartData = ref<any[]>([])

// 显示设置
const pieLegendPosition = ref('right')
const barSortType = ref('desc')
const showBarLabels = ref(true)

// 计算属性
const totalSchools = computed(() => {
  return barChartData.value.length > 0 ? schoolAddressData.value.reduce((sum, item) => sum + item.count, 0) : 0
})

const maxSchoolArea = computed(() => {
  return barChartData.value.length > 0 ? barChartData.value[0] : null
})

// 获取数据
const fetchData = async () => {
  try {
    // 获取选择倾向数据
    const preferenceResponse = await axios.get("/school/majorOrSchoolRankPreference")
    preferenceRawData.value = preferenceResponse.data
    console.log('选择倾向数据:', preferenceRawData.value)

    // 获取地区学校数量数据
    const schoolAddressResponse = await axios.get("/school/schoolAddressNumber")
    schoolAddressData.value = schoolAddressResponse.data
    console.log('地区学校数量数据:', schoolAddressData.value)

    // 处理倾向数据
    processPreferenceData()

    // 初始化柱状图数据
    sortBarData()

    // 渲染图表
    renderCharts()

    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('数据加载失败，请检查网络连接')
  }
}

// 获取倾向名称
const getPreferenceName = (majorOrRank: string) => {
  const nameMap: Record<string, string> = {
    'major_rank': '倾向于专业排名',
    'school_rank': '倾向于学校排名'
  }
  return nameMap[majorOrRank] || majorOrRank
}

// 处理倾向数据
const processPreferenceData = () => {
  if (!preferenceRawData.value || preferenceRawData.value.length === 0) {
    console.warn('倾向数据为空')
    preferenceData.value = []
    return
  }

  const total = preferenceRawData.value.reduce((sum, item) => sum + item.preferenceCount, 0)

  preferenceData.value = preferenceRawData.value.map(item => ({
    ...item,
    percentage: Math.round((item.preferenceCount / total) * 10000) / 100,
    color: item.majorOrRank === 'major_rank' ? '#e6a23c' : '#409eff',
    description: item.majorOrRank === 'major_rank'
      ? '学生更关注所选专业的排名情况'
      : '学生更关注学校的整体排名情况'
  }))
}

// 排序柱状图数据
const sortBarData = () => {
  const data = [...schoolAddressData.value]

  if (barSortType.value === 'desc') {
    // 数量降序
    data.sort((a, b) => b.count - a.count)
  } else if (barSortType.value === 'asc') {
    // 数量升序
    data.sort((a, b) => a.count - b.count)
  } else {
    // 按地区排序
    data.sort((a, b) => a.schoolAddress.localeCompare(b.schoolAddress))
  }

  barChartData.value = data
  if (barChart && barChartData.value.length > 0) {
    renderBarChart()
  }
}

// 渲染图表
const renderCharts = () => {
  if (preferenceData.value.length > 0) {
    renderPieChart()
  }
  if (barChartData.value.length > 0) {
    renderBarChart()
  }
}

// 渲染饼状图
const renderPieChart = () => {
  if (!pieChartRef.value || preferenceData.value.length === 0) return

  // 销毁之前的图表实例
  if (pieChart) {
    pieChart.dispose()
  }

  // 初始化图表
  pieChart = echarts.init(pieChartRef.value)

  // 准备数据
  const pieData = preferenceData.value.map(item => ({
    name: getPreferenceName(item.majorOrRank),
    value: item.preferenceCount,
    percentage: item.percentage,
    itemStyle: {
      color: item.color
    }
  }))

  // 计算图例位置
  let legendPosition: any = {}
  switch (pieLegendPosition.value) {
    case 'right':
      legendPosition = { orient: 'vertical', right: '10%', top: 'center' }
      break
    case 'bottom':
      legendPosition = { bottom: 10 }
      break
    case 'top':
      legendPosition = { top: 10 }
      break
  }

  pieChart.setOption({
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      textStyle: {
        color: '#333'
      },
      formatter: (params: any) => {
        const data = params.data
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 4px;">
              ${data.name}
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">人数：</span>
              <span style="font-weight: bold; color: ${data.itemStyle.color};">${formatNumber(data.value)}人</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">占比：</span>
              <span style="font-weight: bold; color: #67c23a;">${data.percentage}%</span>
            </div>
          </div>
        `
      }
    },
    legend: {
      ...legendPosition,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '选择倾向',
        type: 'pie',
        radius: ['40%', '70%'],
        center: pieLegendPosition.value === 'right' ? ['30%', '50%'] : ['50%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2,
          shadowColor: 'rgba(0, 0, 0, 0.1)',
          shadowBlur: 5,
          shadowOffsetY: 2
        },
        label: {
          show: true,
          formatter: '{b}\n{c}人 ({d}%)',
          fontSize: 12,
          lineHeight: 18
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          },
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.2)',
            shadowBlur: 10,
            shadowOffsetY: 5
          }
        },
        labelLine: {
          length: 15,
          length2: 10
        },
        data: pieData
      }
    ]
  })
}

// 更新饼状图
const updatePieChart = () => {
  if (pieChart && preferenceData.value.length > 0) {
    renderPieChart()
  }
}

// 渲染柱状图
const renderBarChart = () => {
  if (!barChartRef.value || barChartData.value.length === 0) return

  // 销毁之前的图表实例
  if (barChart) {
    barChart.dispose()
  }

  // 初始化图表
  barChart = echarts.init(barChartRef.value)

  // 准备数据
  const areaNames = barChartData.value.map(item => item.schoolAddress)
  const counts = barChartData.value.map(item => item.count)

  // 生成颜色数组
  const colors = generateColors(counts.length, barSortType.value, '#67c23a')

  barChart.setOption({
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
        const originalData = barChartData.value[data.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 8px; border-bottom: 1px solid #eee; padding-bottom: 4px;">
              ${originalData.schoolAddress}
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">学校数量：</span>
              <span style="font-weight: bold; color: #67c23a;">${formatNumber(originalData.count)}所</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">占比：</span>
              <span style="font-weight: bold; color: #e6a23c;">${getSchoolPercentage(originalData.count)}%</span>
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
      data: areaNames,
      axisLabel: {
        interval: 0,
        rotate: barSortType.value === 'name' ? 0 : 0,
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
      name: '学校数量（所）',
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
        name: '学校数量',
        type: 'bar',
        barWidth: '60%',
        data: counts.map((count, index) => ({
          value: count,
          itemStyle: {
            color: colors[index],
            borderColor: colors[index],
            borderWidth: 1
          }
        })),
        label: {
          show: showBarLabels.value,
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

const getSchoolPercentage = (count: number): number => {
  if (totalSchools.value === 0) return 0
  return Math.round((count / totalSchools.value) * 10000) / 100
}

const getProgressColor = (index: number): string => {
  const colors = ['#67c23a', '#e6a23c', '#f56c6c', '#409eff']
  return colors[index % colors.length]
}

// 切换数据标签显示
const toggleBarLabels = () => {
  if (barChart && barChartData.value.length > 0) {
    const option = barChart.getOption()
    option.series[0].label.show = showBarLabels.value
    barChart.setOption(option)
  }
}

// 重置图表
const resetCharts = () => {
  barSortType.value = 'desc'
  pieLegendPosition.value = 'right'
  if (schoolAddressData.value.length > 0) {
    sortBarData()
  }
  if (preferenceData.value.length > 0) {
    updatePieChart()
  }
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.page5-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(255, 154, 158, 0.2);
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

/* 空图表样式 */
.empty-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 2px dashed #e5e7eb;
}

.empty-tip {
  color: #9ca3af;
  font-size: 12px;
  margin-top: 4px;
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

.color-indicator {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  display: inline-block;
  vertical-align: middle;
}

.desc-text {
  font-size: 11px;
  color: #909399;
  margin-top: 2px;
}

.count-value {
  font-weight: bold;
  color: #e6a23c;
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
  color: #ff9a9e;
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
  background-color: rgba(255, 154, 158, 0.3);
}

:deep(.el-scrollbar__thumb:hover) {
  background-color: #ff9a9e;
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
