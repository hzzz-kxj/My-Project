<template>
  <div class="page6-container">
    <!-- 顶部标题栏 -->
    <div class="page-header">
      <h2>不同家庭水平专业选择TOP10分析</h2>
      <div class="header-actions">
        <el-button type="primary" size="small" @click="fetchData">刷新数据</el-button>
        <el-button size="small" @click="exportData">导出分析</el-button>
      </div>
    </div>

    <!-- 四象限图表区域 -->
    <div class="quadrant-charts">
      <!-- 左上：贫穷家庭专业选择TOP10 -->
      <div class="chart-quadrant top-left">
        <div class="chart-card">
          <div class="chart-header">
            <h3>贫穷家庭专业选择TOP10</h3>
            <el-tag type="danger" size="small">贫穷</el-tag>
          </div>
          <div ref="poorChartRef" style="width: 100%; height: 320px;"></div>
          <div class="chart-stats">
            <div class="stat-item">
              <span class="stat-label">总人数：</span>
              <span class="stat-value">{{ formatNumber(poorTotal) }}人</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">热门专业：</span>
              <span class="stat-value">{{ poorTopMajor?.majorName || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右上：一般家庭专业选择TOP10 -->
      <div class="chart-quadrant top-right">
        <div class="chart-card">
          <div class="chart-header">
            <h3>一般家庭专业选择TOP10</h3>
            <el-tag type="warning" size="small">一般</el-tag>
          </div>
          <div ref="normalChartRef" style="width: 100%; height: 320px;"></div>
          <div class="chart-stats">
            <div class="stat-item">
              <span class="stat-label">总人数：</span>
              <span class="stat-value">{{ formatNumber(normalTotal) }}人</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">热门专业：</span>
              <span class="stat-value">{{ normalTopMajor?.majorName || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 左下：小康家庭专业选择TOP10 -->
      <div class="chart-quadrant bottom-left">
        <div class="chart-card">
          <div class="chart-header">
            <h3>小康家庭专业选择TOP10</h3>
            <el-tag type="success" size="small">小康</el-tag>
          </div>
          <div ref="wellOffChartRef" style="width: 100%; height: 320px;"></div>
          <div class="chart-stats">
            <div class="stat-item">
              <span class="stat-label">总人数：</span>
              <span class="stat-value">{{ formatNumber(wellOffTotal) }}人</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">热门专业：</span>
              <span class="stat-value">{{ wellOffTopMajor?.majorName || '-' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右下：富有家庭专业选择TOP10 -->
      <div class="chart-quadrant bottom-right">
        <div class="chart-card">
          <div class="chart-header">
            <h3>富有家庭专业选择TOP10</h3>
            <el-tag type="primary" size="small">富有</el-tag>
          </div>
          <div ref="richChartRef" style="width: 100%; height: 320px;"></div>
          <div class="chart-stats">
            <div class="stat-item">
              <span class="stat-label">总人数：</span>
              <span class="stat-value">{{ formatNumber(richTotal) }}人</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">热门专业：</span>
              <span class="stat-value">{{ richTopMajor?.majorName || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 对比分析表格 -->
    <div class="comparison-table">
      <el-card shadow="hover" class="table-card">
        <template #header>
          <div class="table-header">
            <h4>不同家庭水平专业选择对比</h4>
            <el-button size="small" type="text" @click="toggleTableExpand">
              {{ tableExpanded ? '收起' : '展开' }}
            </el-button>
          </div>
        </template>
        <div class="table-container" :style="{ height: tableExpanded ? '300px' : '150px' }">
          <el-scrollbar>
            <table class="comparison-table-content">
              <thead>
              <tr>
                <th width="60">排名</th>
                <th>专业名称</th>
                <th width="100">贫穷家庭</th>
                <th width="100">一般家庭</th>
                <th width="100">小康家庭</th>
                <th width="100">富有家庭</th>
                <th width="120">总选择人数</th>
                <th width="100">热度趋势</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="major in comparisonData" :key="major.majorName">
                <td class="text-center">
                  <span class="rank-badge">{{ major.rank }}</span>
                </td>
                <td>{{ major.majorName }}</td>
                <td class="text-right">
                    <span :class="getCountClass('poor', major.poorCount)">
                      {{ formatNumber(major.poorCount) }}
                    </span>
                </td>
                <td class="text-right">
                    <span :class="getCountClass('normal', major.normalCount)">
                      {{ formatNumber(major.normalCount) }}
                    </span>
                </td>
                <td class="text-right">
                    <span :class="getCountClass('wellOff', major.wellOffCount)">
                      {{ formatNumber(major.wellOffCount) }}
                    </span>
                </td>
                <td class="text-right">
                    <span :class="getCountClass('rich', major.richCount)">
                      {{ formatNumber(major.richCount) }}
                    </span>
                </td>
                <td class="text-right">
                  <span class="total-count">{{ formatNumber(major.totalCount) }}</span>
                </td>
                <td class="text-center">
                  <el-icon v-if="major.trend === 'up'" color="#67c23a"><Top /></el-icon>
                  <el-icon v-else-if="major.trend === 'down'" color="#f56c6c"><Bottom /></el-icon>
                  <span v-else>-</span>
                </td>
              </tr>
              </tbody>
            </table>
          </el-scrollbar>
        </div>
      </el-card>
    </div>

    <!-- 底部统计信息 -->
    <div class="summary-stats">
      <div class="stat-cards">
        <el-card shadow="hover" class="summary-card">
          <template #header>
            <div class="card-header">
              <el-icon><PieChart /></el-icon>
              <span>数据概览</span>
            </div>
          </template>
          <div class="card-content">
            <p>不同家庭水平专业选择差异分析</p>
            <p>数据来源：学校招生统计数据库</p>
            <p>更新时间：{{ new Date().toLocaleString() }}</p>
          </div>
        </el-card>

        <el-card shadow="hover" class="summary-card">
          <template #header>
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span>分析结果</span>
            </div>
          </template>
          <div class="card-content">
            <p>不同家庭背景学生专业选择偏好存在显著差异</p>
            <p>热门专业在不同家庭水平中保持相对稳定</p>
            <p>家庭经济状况影响专业选择多样性</p>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, computed } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { PieChart, TrendCharts, Top, Bottom } from '@element-plus/icons-vue'

// 图表引用
const poorChartRef = ref<HTMLElement>()
const normalChartRef = ref<HTMLElement>()
const wellOffChartRef = ref<HTMLElement>()
const richChartRef = ref<HTMLElement>()

// 图表实例
let poorChart: echarts.ECharts | null = null
let normalChart: echarts.ECharts | null = null
let wellOffChart: echarts.ECharts | null = null
let richChart: echarts.ECharts | null = null

// 原始数据
const familyData = ref<any[]>([])

// 分组数据
const poorData = ref<any[]>([])
const normalData = ref<any[]>([])
const wellOffData = ref<any[]>([])
const richData = ref<any[]>([])

// 显示设置
const tableExpanded = ref(false)

// 计算属性
const poorTotal = computed(() => poorData.value.reduce((sum, item) => sum + item.count, 0))
const normalTotal = computed(() => normalData.value.reduce((sum, item) => sum + item.count, 0))
const wellOffTotal = computed(() => wellOffData.value.reduce((sum, item) => sum + item.count, 0))
const richTotal = computed(() => richData.value.reduce((sum, item) => sum + item.count, 0))

const poorTopMajor = computed(() => poorData.value[0])
const normalTopMajor = computed(() => normalData.value[0])
const wellOffTopMajor = computed(() => wellOffData.value[0])
const richTopMajor = computed(() => richData.value[0])

const comparisonData = computed(() => {
  const allMajors = new Set<string>()

  // 收集所有专业
  poorData.value.forEach(item => allMajors.add(item.majorName))
  normalData.value.forEach(item => allMajors.add(item.majorName))
  wellOffData.value.forEach(item => allMajors.add(item.majorName))
  richData.value.forEach(item => allMajors.add(item.majorName))

  const result: any[] = []

  allMajors.forEach(majorName => {
    const poorItem = poorData.value.find(item => item.majorName === majorName)
    const normalItem = normalData.value.find(item => item.majorName === majorName)
    const wellOffItem = wellOffData.value.find(item => item.majorName === majorName)
    const richItem = richData.value.find(item => item.majorName === majorName)

    const counts = [
      poorItem?.count || 0,
      normalItem?.count || 0,
      wellOffItem?.count || 0,
      richItem?.count || 0
    ]

    const totalCount = counts.reduce((sum, count) => sum + count, 0)

    // 判断趋势：如果富有家庭选择人数 > 贫穷家庭，则为上升趋势
    const trend = richItem?.count > poorItem?.count ? 'up' :
      richItem?.count < poorItem?.count ? 'down' : 'stable'

    result.push({
      majorName,
      poorCount: poorItem?.count || 0,
      normalCount: normalItem?.count || 0,
      wellOffCount: wellOffItem?.count || 0,
      richCount: richItem?.count || 0,
      totalCount,
      trend
    })
  })

  // 按总人数排序
  result.sort((a, b) => b.totalCount - a.totalCount)

  // 添加排名
  result.forEach((item, index) => {
    item.rank = index + 1
  })

  return result.slice(0, 15) // 只显示前15个专业
})

// 获取数据
const fetchData = async () => {
  try {
    const response = await axios.get("/school/familyLevelMajorPreference")
    familyData.value = response.data
    console.log('家庭水平专业选择数据:', familyData.value)

    // 处理数据，分组并取TOP10
    processData()

    // 渲染所有图表
    renderAllCharts()

    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('数据加载失败，请检查网络连接')
  }
}

// 处理数据，分组并取TOP10
const processData = () => {
  // 按家庭水平分组
  const poorGroup = familyData.value.filter(item => item.studentFamilyLevel === '贫穷')
  const normalGroup = familyData.value.filter(item => item.studentFamilyLevel === '一般')
  const wellOffGroup = familyData.value.filter(item => item.studentFamilyLevel === '小康')
  const richGroup = familyData.value.filter(item => item.studentFamilyLevel === '富有')

  // 对每组数据按count降序排序，取前10个
  poorData.value = poorGroup.sort((a, b) => b.count - a.count).slice(0, 10)
  normalData.value = normalGroup.sort((a, b) => b.count - a.sort).slice(0, 10)
  wellOffData.value = wellOffGroup.sort((a, b) => b.count - a.count).slice(0, 10)
  richData.value = richGroup.sort((a, b) => b.count - a.count).slice(0, 10)
}

// 渲染所有图表
const renderAllCharts = () => {
  renderChart(poorChartRef.value, poorData.value, '贫穷家庭专业选择TOP10', '#f56c6c', poorChart)
  renderChart(normalChartRef.value, normalData.value, '一般家庭专业选择TOP10', '#e6a23c', normalChart)
  renderChart(wellOffChartRef.value, wellOffData.value, '小康家庭专业选择TOP10', '#67c23a', wellOffChart)
  renderChart(richChartRef.value, richData.value, '富有家庭专业选择TOP10', '#409eff', richChart)
}

// 渲染单个图表
const renderChart = (chartDom: HTMLElement | null, data: any[], title: string, color: string, chartInstance: echarts.ECharts | null) => {
  if (!chartDom || data.length === 0) return

  // 销毁之前的图表实例
  if (chartInstance) {
    chartInstance.dispose()
  }

  // 初始化图表
  chartInstance = echarts.init(chartDom)

  // 准备数据
  const majorNames = data.map(item =>
    item.majorName.length > 6 ? item.majorName.substring(0, 6) + '...' : item.majorName
  )
  const counts = data.map(item => item.count)

  // 生成渐变颜色
  const colors = generateGradientColors(color, counts.length)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      backgroundColor: 'rgba(255, 255, 255, 0.95)',
      borderColor: '#e4e7ed',
      borderWidth: 1,
      formatter: (params: any) => {
        const dataItem = params[0]
        const originalData = data[dataItem.dataIndex]
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 5px; border-bottom: 1px solid #eee; padding-bottom: 4px;">
              ${originalData.majorName}
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">人数：</span>
              <span style="font-weight: bold; color: ${color};">${formatNumber(originalData.count)}人</span>
            </div>
            <div style="margin: 4px 0; display: flex; justify-content: space-between;">
              <span style="color: #666;">排名：</span>
              <span style="font-weight: bold;">第${dataItem.dataIndex + 1}名</span>
            </div>
          </div>
        `
      }
    },
    grid: {
      left: '10%',
      right: '5%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: majorNames,
      axisLabel: {
        interval: 0,
        rotate: 30,
        fontSize: 10
      },
      axisLine: {
        lineStyle: {
          color: '#dcdfe6'
        }
      }
    },
    yAxis: {
      type: 'value',
      name: '人数',
      nameTextStyle: {
        fontSize: 12
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
    series: [{
      name: '选择人数',
      type: 'bar',
      barWidth: '50%',
      data: counts.map((count, index) => ({
        value: count,
        itemStyle: {
          color: colors[index]
        }
      })),
      label: {
        show: true,
        position: 'top',
        formatter: '{c}',
        fontSize: 9
      },
      itemStyle: {
        borderRadius: [4, 4, 0, 0],
        shadowColor: 'rgba(0, 0, 0, 0.1)',
        shadowBlur: 4,
        shadowOffsetY: 2
      }
    }]
  }

  chartInstance.setOption(option)

  // 保存图表实例
  if (chartDom === poorChartRef.value) poorChart = chartInstance
  if (chartDom === normalChartRef.value) normalChart = chartInstance
  if (chartDom === wellOffChartRef.value) wellOffChart = chartInstance
  if (chartDom === richChartRef.value) richChart = chartInstance
}

// 生成渐变色
const generateGradientColors = (baseColor: string, count: number): string[] => {
  const colors = []
  for (let i = 0; i < count; i++) {
    const opacity = 0.6 + (i / count) * 0.4
    colors.push(adjustColor(baseColor, opacity))
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

const getCountClass = (type: string, count: number): string => {
  if (count === 0) return 'zero-count'
  const baseClass = `${type}-count`
  if (count > 3000) return `${baseClass} high-count`
  if (count > 2000) return `${baseClass} medium-count`
  return `${baseClass} low-count`
}

const toggleTableExpand = () => {
  tableExpanded.value = !tableExpanded.value
}

const exportData = () => {
  ElMessage.success('导出功能开发中')
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  poorChart?.resize()
  normalChart?.resize()
  wellOffChart?.resize()
  richChart?.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  poorChart?.dispose()
  normalChart?.dispose()
  wellOffChart?.dispose()
  richChart?.dispose()
})
</script>

<style scoped>
.page6-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  padding: 20px;
}

.page-header {
  background: linear-gradient(135deg, #6a11cb 0%, #2575fc 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(106, 17, 203, 0.2);
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

/* 四象限图表区域 */
.quadrant-charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-template-rows: auto auto;
  gap: 15px;
  margin-bottom: 20px;
  min-height: 700px;
}

.chart-quadrant {
  display: flex;
  height: 100%;
}

.chart-card {
  flex: 1;
  background: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  border: 1px solid #ebeef5;
  min-height: 400px;
}

/* 不同象限的特殊样式 */
.top-left .chart-card {
  border-top: 4px solid #f56c6c;
}

.top-right .chart-card {
  border-top: 4px solid #e6a23c;
}

.bottom-left .chart-card {
  border-top: 4px solid #67c23a;
}

.bottom-right .chart-card {
  border-top: 4px solid #409eff;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  flex-shrink: 0;
}

.chart-header h3 {
  margin: 0;
  font-size: 14px;
  color: #303133;
  font-weight: 600;
}

.chart-stats {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #ebeef5;
  font-size: 12px;
  flex-shrink: 0;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-label {
  color: #909399;
  margin-bottom: 2px;
}

.stat-value {
  font-weight: bold;
  color: #303133;
}

/* 对比分析表格 */
.comparison-table {
  margin-bottom: 20px;
  margin-top: 10px;
}

.table-card {
  border: none;
  border-radius: 8px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header h4 {
  margin: 0;
  color: #6a11cb;
  font-size: 14px;
}

.table-container {
  overflow: hidden;
  transition: height 0.3s ease;
}

.comparison-table-content {
  width: 100%;
  border-collapse: collapse;
  font-size: 12px;
  min-width: 800px;
}

.comparison-table-content th {
  background-color: #f8f9fa;
  padding: 10px;
  text-align: left;
  font-weight: bold;
  color: #495057;
  border-bottom: 2px solid #dee2e6;
  position: sticky;
  top: 0;
  z-index: 1;
}

.comparison-table-content td {
  padding: 8px 10px;
  border-bottom: 1px solid #e9ecef;
  color: #495057;
}

.comparison-table-content tr:hover {
  background-color: #f8f9fa;
}

.text-center {
  text-align: center;
}

.text-right {
  text-align: right;
}

.rank-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  border-radius: 50%;
  background-color: #6a11cb;
  color: white;
  font-weight: bold;
}

/* 计数样式 */
.poor-count {
  color: #f56c6c;
}

.normal-count {
  color: #e6a23c;
}

.well-off-count {
  color: #67c23a;
}

.rich-count {
  color: #409eff;
}

.high-count {
  font-weight: bold;
}

.medium-count {
  font-weight: 600;
}

.low-count {
  font-weight: normal;
}

.zero-count {
  color: #909399;
}

.total-count {
  font-weight: bold;
  color: #6a11cb;
}

/* 底部统计信息 */
.summary-stats {
  margin-top: 20px;
}

.stat-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.summary-card {
  border: none;
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6a11cb;
  font-weight: bold;
  font-size: 14px;
}

.card-content {
  font-size: 12px;
  color: #606266;
  line-height: 1.6;
}

.card-content p {
  margin: 6px 0;
}

/* 自定义滚动条 */
:deep(.el-scrollbar) {
  height: 100%;
}

:deep(.el-scrollbar__wrap) {
  overflow-x: auto;
  overflow-y: hidden;
}

:deep(.el-scrollbar__thumb) {
  background-color: rgba(106, 17, 203, 0.3);
}

:deep(.el-scrollbar__thumb:hover) {
  background-color: #6a11cb;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .quadrant-charts {
    min-height: 800px;
  }
}

@media (max-width: 992px) {
  .quadrant-charts {
    grid-template-columns: 1fr;
    grid-template-rows: repeat(4, auto);
    min-height: 1600px;
  }

  .chart-card {
    min-height: 380px;
  }
}

@media (max-width: 768px) {
  .stat-cards {
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

  .comparison-table-content {
    font-size: 11px;
  }

  .comparison-table-content th,
  .comparison-table-content td {
    padding: 6px 8px;
  }
}

/* 确保图表有足够的空间 */
.chart-quadrant > .chart-card > :nth-child(2) {
  flex: 1;
  min-height: 300px;
}

/* 优化表格与图表的间距 */
.bottom-left, .bottom-right {
  margin-bottom: 0;
}
</style>
