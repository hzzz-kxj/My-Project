<template>
  <div class="page6-container">
    <!-- 顶部标题栏 - 修改为渐变色 -->
    <div class="page-header">
      <h2>学校排名前十统计</h2>
      <div class="header-actions">
        <el-radio-group v-model="chartType" @change="changeChartType">
          <el-radio label="bar">柱状图</el-radio>
          <el-radio label="horizontal">横向柱状图</el-radio>
        </el-radio-group>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading">
        <Loading />
      </el-icon>
      <span>正在加载数据...</span>
    </div>

    <div v-else>
      <!-- 图表容器 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学校排名TOP10</h3>
            <div class="chart-actions">
              <el-button type="primary" size="small" @click="exportData">
                <el-icon><Download /></el-icon>
                导出数据
              </el-button>
            </div>
          </div>
          <div class="chart-container">
            <div v-if="!loading && tableData.length > 0" ref="chartContainer" style="width: 100%; height: 500px;"></div>
            <div v-else-if="!loading" class="empty-state">
              <el-icon>
                <DataLine />
              </el-icon>
              <p>暂无数据</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 数据表格 -->
      <div class="table-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>详细数据</h3>
            <div class="table-actions">
              <el-button type="primary" size="small" @click="exportData">
                <el-icon><Download /></el-icon>
                导出数据
              </el-button>
            </div>
          </div>
          <el-table :data="tableData" style="width: 100%" class="data-table">
            <el-table-column prop="schoolRank" label="排名" width="80" sortable>
              <template #default="scope">
                <el-tag :type="getRankTagType(scope.row.schoolRank)">
                  第{{ scope.row.schoolRank }}名
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="schoolName" label="学校名称" min-width="200"></el-table-column>
            <el-table-column prop="schoolAddress" label="所在省份" width="120"></el-table-column>
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="text" @click="viewDetail(scope.row)" size="small">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading, DataLine, Download } from '@element-plus/icons-vue'

// 响应式数据
const chartContainer = ref<HTMLElement>()
const tableData = ref<any[]>([])
const loading = ref(true)
const chartType = ref('bar')
let chart: echarts.ECharts | null = null

// 监听数据变化，确保图表在数据加载后渲染
watch(() => tableData.value, (newData) => {
  if (newData && newData.length > 0) {
    // 等待DOM更新后再初始化图表
    nextTick(() => {
      setTimeout(() => {
        initChart()
      }, 100)
    })
  }
}, { immediate: true })

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    const response = await axios.get("/school/schoolRankTop10")
    tableData.value = response.data

    // 按排名排序，确保数据正确显示
    tableData.value.sort((a, b) => a.schoolRank - b.schoolRank)

    console.log('获取到的数据:', tableData.value)
    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败，请检查网络连接')

    // 如果有模拟数据，可以在这里使用
    // useMockData()
  } finally {
    loading.value = false
  }
}

// 初始化图表
const initChart = () => {
  if (!chartContainer.value) {
    console.warn('图表容器未找到')
    return
  }

  // 销毁之前的图表实例
  if (chart) {
    chart.dispose()
  }

  // 创建新的图表实例
  chart = echarts.init(chartContainer.value)

  // 准备数据
  const schoolNames = tableData.value.map(item => item.schoolName)
  const schoolRanks = tableData.value.map(item => item.schoolRank)
  const provinces = tableData.value.map(item => item.schoolAddress)

  console.log('图表数据:', { schoolNames, schoolRanks, provinces })

  // 生成颜色数组
  const colors = [
    '#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de',
    '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc', '#60acfc'
  ]

  // 图表配置
  const isHorizontal = chartType.value === 'horizontal'
  const option: echarts.EChartsOption = {
    backgroundColor: '#f9f9f9',
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter: (params: any) => {
        const data = params[0]
        const index = data.dataIndex
        return `
          <div style="padding: 8px; background: white; border-radius: 4px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <div style="font-weight: bold; margin-bottom: 6px; color: #333;">${schoolNames[index]}</div>
            <div style="margin: 4px 0; display: flex; align-items: center;">
              <span style="color: #666; min-width: 40px;">排名：</span>
              <span style="color: ${colors[index % colors.length]}; font-weight: bold;">第${schoolRanks[index]}名</span>
            </div>
            <div style="margin: 4px 0; display: flex; align-items: center;">
              <span style="color: #666; min-width: 40px;">省份：</span>
              <span>${provinces[index]}</span>
            </div>
          </div>
        `
      }
    },
    legend: {
      data: ['学校排名'],
      top: '5%',
      textStyle: {
        fontSize: 12
      }
    },
    grid: {
      left: '10%',
      right: '5%',
      bottom: '12%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: isHorizontal ? 'value' : 'category',
      nameLocation: 'middle',
      nameGap: isHorizontal ? 30 : 40,
      nameTextStyle: {
        fontSize: 12,
        color: '#666'
      },
      data: isHorizontal ? null : schoolNames,
      axisLabel: {
        interval: 0,
        rotate: isHorizontal ? 0 : 30,
        fontSize: 10,
        formatter: (value: string) => {
          // 如果名称太长，显示省略号
          return value.length > 8 ? value.substring(0, 8) + '...' : value
        }
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      }
    },
    yAxis: {
      type: isHorizontal ? 'category' : 'value',
      nameLocation: 'middle',
      nameGap: isHorizontal ? 40 : 30,
      nameTextStyle: {
        fontSize: 12,
        color: '#666'
      },
      data: isHorizontal ? schoolNames : null,
      inverse: isHorizontal, // 横向柱状图时倒序显示
      axisLabel: {
        fontSize: 10,
        formatter: isHorizontal ? (value: string) => {
          return value.length > 10 ? value.substring(0, 10) + '...' : value
        } : '{value}'
      },
      axisLine: {
        lineStyle: {
          color: '#ccc'
        }
      }
    },
    series: [
      {
        name: '学校排名',
        type: 'bar',
        data: schoolRanks.map((rank, index) => ({
          value: rank,
          itemStyle: {
            color: colors[index % colors.length],
            borderRadius: [4, 4, 0, 0]
          }
        })),
        label: {
          show: true,
          position: isHorizontal ? 'right' : 'top',
          formatter: '{c}名',
          fontSize: 10,
          color: '#333'
        },
        barWidth: '50%',
        itemStyle: {
          shadowColor: 'rgba(0, 0, 0, 0.1)',
          shadowBlur: 4,
          shadowOffsetY: 2
        }
      }
    ],
    dataZoom: [
      {
        type: 'inside',
        xAxisIndex: [0],
        start: 0,
        end: 100
      }
    ],
    animation: true,
    animationDuration: 1000,
    animationEasing: 'cubicOut'
  }

  chart.setOption(option)

  // 窗口大小改变时重绘图表
  window.addEventListener('resize', handleResize)
}

// 处理窗口大小变化
const handleResize = () => {
  if (chart) {
    chart.resize()
  }
}

// 切换图表类型
const changeChartType = () => {
  initChart()
}

// 获取排名标签类型
const getRankTagType = (rank: number) => {
  if (rank === 1) return 'danger'  // 红色，第一名
  if (rank === 2) return 'warning' // 橙色，第二名
  if (rank === 3) return 'success' // 绿色，第三名
  return '' // 其他名次
}

// 查看详情
const viewDetail = (row: any) => {
  ElMessageBox.alert(
    `<div style="line-height: 1.8;">
      <p><strong>学校名称：</strong>${row.schoolName}</p>
      <p><strong>排名：</strong>第${row.schoolRank}名</p>
      <p><strong>所在省份：</strong>${row.schoolAddress}</p>
    </div>`,
    '学校详情',
    {
      dangerouslyUseHTMLString: true,
      confirmButtonText: '确定'
    }
  )
}

// 导出数据
const exportData = () => {
  const headers = ['排名', '学校名称', '所在省份']
  const csvData = tableData.value.map(item => [
    item.schoolRank,
    `"${item.schoolName}"`,
    item.schoolAddress
  ])

  const csvContent = [
    headers.join(','),
    ...csvData.map(row => row.join(','))
  ].join('\n')

  const blob = new Blob(['\ufeff' + csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  link.href = URL.createObjectURL(blob)
  link.download = `学校排名_${new Date().toLocaleDateString()}.csv`
  link.click()

  ElMessage.success('数据导出成功')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchData()
})

// 组件卸载时清理
onUnmounted(() => {
  if (chart) {
    chart.dispose()
    chart = null
  }
  window.removeEventListener('resize', handleResize)
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

/* 修改头部为渐变色 */
.page-header {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(250, 112, 154, 0.3);
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
  align-items: center;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.loading-container .el-icon {
  font-size: 48px;
  color: #fa709a;
  margin-bottom: 20px;
}

.loading-container span {
  color: #606266;
  font-size: 14px;
}

.chart-section {
  margin-bottom: 20px;
}

/* 修改卡片样式 */
.chart-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
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
  min-height: 500px;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  color: #909399;
}

.empty-state .el-icon {
  font-size: 64px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.table-section {
  margin-top: 20px;
}

.table-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.data-table {
  border-radius: 6px;
  overflow: hidden;
}

.data-table .el-button--text {
  color: #fa709a;
}

.el-tag {
  min-width: 60px;
  text-align: center;
  font-weight: 500;
}

/* 自定义滚动条 */
:deep(.el-table__body-wrapper) {
  scrollbar-width: thin;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-track) {
  background: #f5f5f5;
  border-radius: 3px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background: rgba(250, 112, 154, 0.3);
  border-radius: 3px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb:hover) {
  background: #fa709a;
}

/* 响应式设计 */
@media (max-width: 768px) {
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
