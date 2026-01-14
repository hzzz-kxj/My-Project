<template>
  <div class="page3-container">
    <!-- 顶部标题栏 - 修改为渐变色 -->
    <div class="page-header">
      <h2>学校与专业收入统计分析</h2>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="refreshData">刷新数据</el-button>
        <el-button size="small" @click="exportData">导出数据</el-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧：学校平均收入TOP10 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学校平均收入TOP10</h3>
            <div class="chart-actions">
              <el-select v-model="schoolSortType" size="small" style="width: 120px" @change="sortSchoolData">
                <el-option label="收入降序" value="desc" />
                <el-option label="收入升序" value="asc" />
                <el-option label="按名称排序" value="name" />
              </el-select>
            </div>
          </div>
          <!-- 柱状图容器 - 添加阴影边框 -->
          <div class="chart-container">
            <div ref="schoolChartRef" style="width: 100%; height: 400px;"></div>
          </div>

          <!-- 学校数据表格 -->
          <div class="data-summary">
            <h4>学校收入数据详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">排名</th>
                  <th>学校名称</th>
                  <th width="120">平均收入</th>
                  <th width="120">收入级别</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="(item, index) in schoolChartData" :key="item.schoolName">
                  <td class="text-center">
                    <el-tag size="small" :type="getRankTag(index + 1)">
                      {{ index + 1 }}
                    </el-tag>
                  </td>
                  <td>{{ item.schoolName }}</td>
                  <td class="text-right">
                    <span class="income-value">¥{{ formatNumber(item.avgIncome) }}</span>
                  </td>
                  <td class="text-center">
                    <el-tag size="small" :type="getIncomeLevel(item.avgIncome)">
                      {{ getIncomeLevelText(item.avgIncome) }}
                    </el-tag>
                  </td>
                </tr>
                </tbody>
              </table>
            </el-scrollbar>
          </div>
        </div>
      </div>

      <!-- 右侧：专业平均收入TOP10 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>专业平均收入TOP10</h3>
            <div class="chart-actions">
              <el-select v-model="majorSortType" size="small" style="width: 120px" @change="sortMajorData">
                <el-option label="收入降序" value="desc" />
                <el-option label="收入升序" value="asc" />
                <el-option label="按名称排序" value="name" />
              </el-select>
            </div>
          </div>
          <!-- 柱状图容器 - 添加阴影边框 -->
          <div class="chart-container">
            <div ref="majorChartRef" style="width: 100%; height: 400px;"></div>
          </div>

          <!-- 专业数据表格 -->
          <div class="data-summary">
            <h4>专业收入数据详情</h4>
            <el-scrollbar height="200px">
              <table class="summary-table">
                <thead>
                <tr>
                  <th width="60">排名</th>
                  <th>专业名称</th>
                  <th width="120">平均收入</th>
                  <th width="120">收入级别</th>
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
                    <span class="income-value">¥{{ formatNumber(item.avgIncome) }}</span>
                  </td>
                  <td class="text-center">
                    <el-tag size="small" :type="getIncomeLevel(item.avgIncome)">
                      {{ getIncomeLevelText(item.avgIncome) }}
                    </el-tag>
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
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <span>🏫</span>
            </div>
            <div class="stat-content">
              <div class="stat-title">学校数量</div>
              <div class="stat-value">{{ schoolData.length }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <span>📚</span>
            </div>
            <div class="stat-content">
              <div class="stat-title">专业数量</div>
              <div class="stat-value">{{ majorData.length }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
              <span>💰</span>
            </div>
            <div class="stat-content">
              <div class="stat-title">最高学校收入</div>
              <div class="stat-value">¥{{ formatNumber(highestSchoolIncome) }}</div>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <span>📊</span>
            </div>
            <div class="stat-content">
              <div class="stat-title">最高专业收入</div>
              <div class="stat-value">¥{{ formatNumber(highestMajorIncome) }}</div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 图表引用
const schoolChartRef = ref<HTMLElement>()
const majorChartRef = ref<HTMLElement>()

let schoolChart: echarts.ECharts | null = null
let majorChart: echarts.ECharts | null = null

// 原始数据
const schoolData = ref<any[]>([])
const majorData = ref<any[]>([])

// 图表数据（排序后的）
const schoolChartData = ref<any[]>([])
const majorChartData = ref<any[]>([])

// 排序类型
const schoolSortType = ref('desc')
const majorSortType = ref('desc')

// 统计数据
const highestSchoolIncome = ref(0)
const highestMajorIncome = ref(0)

// 获取数据
const fetchData = async () => {
  try {
    // 获取学校收入数据
    const schoolResponse = await axios.get("/school/schoolStudentIncomeTop10")
    schoolData.value = schoolResponse.data
    console.log('学校收入数据:', schoolData.value)

    // 获取专业收入数据
    const majorResponse = await axios.get("/school/majorIncomeTop10")
    majorData.value = majorResponse.data
    console.log('专业收入数据:', majorData.value)

    // 计算最高收入
    if (schoolData.value.length > 0) {
      highestSchoolIncome.value = Math.max(...schoolData.value.map(item => item.avgIncome))
    }
    if (majorData.value.length > 0) {
      highestMajorIncome.value = Math.max(...majorData.value.map(item => item.avgIncome))
    }

    // 初始化图表数据
    sortSchoolData()
    sortMajorData()

    // 渲染图表
    renderCharts()

    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('数据加载失败')
  }
}

// 排序学校数据
const sortSchoolData = () => {
  const data = [...schoolData.value]

  if (schoolSortType.value === 'desc') {
    // 收入降序
    data.sort((a, b) => b.avgIncome - a.avgIncome)
  } else if (schoolSortType.value === 'asc') {
    // 收入升序
    data.sort((a, b) => a.avgIncome - b.avgIncome)
  } else {
    // 按名称排序
    data.sort((a, b) => a.schoolName.localeCompare(b.schoolName))
  }

  schoolChartData.value = data
  if (schoolChart) {
    renderSchoolChart()
  }
}

// 排序专业数据
const sortMajorData = () => {
  const data = [...majorData.value]

  if (majorSortType.value === 'desc') {
    // 收入降序
    data.sort((a, b) => b.avgIncome - a.avgIncome)
  } else if (majorSortType.value === 'asc') {
    // 收入升序
    data.sort((a, b) => a.avgIncome - b.avgIncome)
  } else {
    // 按名称排序
    data.sort((a, b) => a.majorName.localeCompare(b.majorName))
  }

  majorChartData.value = data
  if (majorChart) {
    renderMajorChart()
  }
}

// 渲染图表
const renderCharts = () => {
  renderSchoolChart()
  renderMajorChart()
}

// 渲染学校收入图表
const renderSchoolChart = () => {
  if (!schoolChartRef.value) return

  // 销毁之前的图表实例
  if (schoolChart) {
    schoolChart.dispose()
  }

  // 初始化图表
  schoolChart = echarts.init(schoolChartRef.value)

  // 准备数据
  const schoolNames = schoolChartData.value.map(item => {
    // 如果学校名称太长，进行截断
    if (item.schoolName.length > 10) {
      return item.schoolName.substring(0, 10) + '...'
    }
    return item.schoolName
  })

  const incomes = schoolChartData.value.map(item => item.avgIncome)

  // 生成渐变色数组
  const colors = incomes.map((_, index) => {
    if (schoolSortType.value === 'desc') {
      // 降序时，第一名颜色最深
      const opacity = 1 - (index / incomes.length) * 0.6
      return `rgba(64, 158, 255, ${opacity})`
    } else if (schoolSortType.value === 'asc') {
      // 升序时，最后一名颜色最深
      const opacity = 0.4 + (index / incomes.length) * 0.6
      return `rgba(64, 158, 255, ${opacity})`
    } else {
      // 按名称排序时使用固定颜色
      return '#409EFF'
    }
  })

  schoolChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const data = params[0]
        const originalData = schoolChartData.value[data.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 5px;">${originalData.schoolName}</div>
            <div style="margin: 3px 0;">
              <span>平均收入：</span>
              <span style="font-weight: bold; color: #409EFF;">¥${formatNumber(originalData.avgIncome)}</span>
            </div>
            <div style="margin: 3px 0;">
              <span>排名：</span>
              <span style="font-weight: bold;">第${data.dataIndex + 1}名</span>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: schoolNames,
      axisLabel: {
        interval: 0,
        rotate: 25,
        fontSize: 12
      },
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '平均收入（元）',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '学校平均收入',
        type: 'bar',
        barWidth: '60%',
        data: incomes.map((income, index) => ({
          value: income,
          itemStyle: {
            color: colors[index]
          }
        })),
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            return `¥${formatNumber(params.value)}`
          },
          fontSize: 10
        },
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          shadowColor: 'rgba(0, 0, 0, 0.1)',
          shadowBlur: 4,
          shadowOffsetY: 2
        },
        emphasis: {
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.3)',
            shadowBlur: 8,
            shadowOffsetY: 3
          }
        }
      }
    ]
  })
}

// 渲染专业收入图表
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
    // 如果专业名称太长，进行截断
    if (item.majorName.length > 8) {
      return item.majorName.substring(0, 8) + '...'
    }
    return item.majorName
  })

  const incomes = majorChartData.value.map(item => item.avgIncome)

  // 生成渐变色数组
  const colors = incomes.map((_, index) => {
    if (majorSortType.value === 'desc') {
      // 降序时，第一名颜色最深
      const opacity = 1 - (index / incomes.length) * 0.6
      return `rgba(103, 194, 58, ${opacity})`
    } else if (majorSortType.value === 'asc') {
      // 升序时，最后一名颜色最深
      const opacity = 0.4 + (index / incomes.length) * 0.6
      return `rgba(103, 194, 58, ${opacity})`
    } else {
      // 按名称排序时使用固定颜色
      return '#67c23a'
    }
  })

  majorChart.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const data = params[0]
        const originalData = majorChartData.value[data.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 5px;">${originalData.majorName}</div>
            <div style="margin: 3px 0;">
              <span>平均收入：</span>
              <span style="font-weight: bold; color: #67c23a;">¥${formatNumber(originalData.avgIncome)}</span>
            </div>
            <div style="margin: 3px 0;">
              <span>排名：</span>
              <span style="font-weight: bold;">第${data.dataIndex + 1}名</span>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: majorNames,
      axisLabel: {
        interval: 0,
        rotate: 25,
        fontSize: 12
      },
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value',
      name: '平均收入（元）',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '专业平均收入',
        type: 'bar',
        barWidth: '60%',
        data: incomes.map((income, index) => ({
          value: income,
          itemStyle: {
            color: colors[index]
          }
        })),
        label: {
          show: true,
          position: 'top',
          formatter: (params: any) => {
            return `¥${formatNumber(params.value)}`
          },
          fontSize: 10
        },
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          shadowColor: 'rgba(0, 0, 0, 0.1)',
          shadowBlur: 4,
          shadowOffsetY: 2
        },
        emphasis: {
          itemStyle: {
            shadowColor: 'rgba(0, 0, 0, 0.3)',
            shadowBlur: 8,
            shadowOffsetY: 3
          }
        }
      }
    ]
  })
}

// 辅助函数
const formatNumber = (num: number): string => {
  return num.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getRankTag = (rank: number): string => {
  if (rank === 1) return 'danger'
  if (rank === 2) return 'warning'
  if (rank === 3) return 'success'
  return ''
}

const getIncomeLevel = (income: number): string => {
  if (income >= 23000) return 'success'
  if (income >= 20000) return 'warning'
  return 'info'
}

const getIncomeLevelText = (income: number): string => {
  if (income >= 23000) return '高收入'
  if (income >= 20000) return '中等收入'
  return '一般收入'
}

// 刷新数据
const refreshData = () => {
  fetchData()
  ElMessage.info('正在刷新数据...')
}

// 导出数据
const exportData = () => {
  // 这里可以实现数据导出功能
  ElMessage.success('数据导出功能待实现')
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  schoolChart?.resize()
  majorChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  schoolChart?.dispose()
  majorChart?.dispose()
})
</script>

<style scoped>
.page3-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  padding: 20px;
}

/* 修改头部为渐变色 */
.page-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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

/* 修改卡片样式 */
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

/* 添加图表容器样式 */
.chart-container {
  background: white;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
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

.income-value {
  font-weight: bold;
  color: #409eff;
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
  background-color: rgba(102, 126, 234, 0.3);
}

:deep(.el-scrollbar__thumb:hover) {
  background-color: #667eea;
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
