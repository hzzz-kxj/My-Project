<template>
  <div class="page5-container">
    <!-- 顶部标题栏 - 修改为渐变色 -->
    <div class="page-header">
      <h2>学生生源地统计图表</h2>
    </div>

    <!-- 饼状图容器 -->
    <div class="charts-container">
      <!-- 左侧饼状图：生源地分布 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>生源地分布</h3>
          </div>
          <div class="chart-container">
            <div ref="pieChart1" style="width: 100%; height: 400px;"></div>
          </div>
        </div>
      </div>

      <!-- 右侧饼状图：学历分布 -->
      <div class="chart-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学历分布</h3>
          </div>
          <div class="chart-container">
            <div ref="pieChart2" style="width: 100%; height: 400px;"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计信息卡片 -->
    <div class="stats-container">
      <!-- 生源地统计 -->
      <div class="stats-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>生源地统计</h3>
          </div>
          <div class="stat-content">
            <div class="stat-total">
              <span class="stat-label">总数:</span>
              <span class="stat-value">{{ totalCount.toLocaleString() }}</span>
            </div>
            <div class="stat-details">
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #67c23a;">
                  <el-icon><User /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-count">{{ nonLocalCount.toLocaleString() }}</div>
                  <div class="stat-name">外省学生</div>
                  <div class="stat-rate">{{ nonLocalRate }}%</div>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-icon" style="background-color: #e6a23c;">
                  <el-icon><HomeFilled /></el-icon>
                </div>
                <div class="stat-info">
                  <div class="stat-count">{{ localCount.toLocaleString() }}</div>
                  <div class="stat-name">本省学生</div>
                  <div class="stat-rate">{{ localRate }}%</div>
                </div>
              </div>
            </div>
            <div class="stat-progress">
              <el-progress :percentage="nonLocalRate" :show-text="false" color="#67c23a" />
            </div>
          </div>
        </div>
      </div>

      <!-- 学历统计 -->
      <div class="stats-section">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学历分布统计</h3>
          </div>
          <div class="stat-content">
            <div class="stat-total">
              <span class="stat-label">总数:</span>
              <span class="stat-value">{{ educationTotal.toLocaleString() }}</span>
            </div>
            <div class="stat-education-grid">
              <div class="education-item" v-for="item in educationData" :key="item.studentEducation">
                <div class="education-icon" :style="{ backgroundColor: getEducationColor(item.studentEducation) }">
                  <span>{{ getEducationSymbol(item.studentEducation) }}</span>
                </div>
                <div class="education-info">
                  <div class="education-count">{{ item.count.toLocaleString() }}</div>
                  <div class="education-name">{{ item.studentEducation }}</div>
                  <div class="education-rate">{{ item.rate }}%</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 详细数据表格 -->
    <div class="table-container">
      <div class="chart-card">
        <div class="chart-header">
          <h3>详细数据表格</h3>
        </div>

        <!-- 学历分布表格 -->
        <div class="table-section">
          <h4>学生学历分布</h4>
          <el-table :data="educationTableData" stripe class="data-table">
            <el-table-column prop="studentEducation" label="学历" width="120">
              <template #default="scope">
                <el-tag :type="getEducationTagType(scope.row.studentEducation)" size="small">
                  {{ scope.row.studentEducation }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="人数" width="120" sortable align="right">
              <template #default="scope">
                {{ scope.row.count.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="rate" label="占比" width="120" sortable align="right">
              <template #default="scope">
                {{ scope.row.rate }}%
              </template>
            </el-table-column>
            <el-table-column label="比例" width="300">
              <template #default="scope">
                <el-progress
                  :percentage="parseFloat(scope.row.rate)"
                  :color="getEducationColor(scope.row.studentEducation)"
                  :show-text="false"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 生源地分布表格 -->
        <div class="table-section">
          <h4>学生生源地分布</h4>
          <el-table :data="provinceTableData" stripe class="data-table">
            <el-table-column prop="category" label="分类" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.type" size="small">
                  {{ scope.row.category }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="count" label="人数" width="120" sortable align="right">
              <template #default="scope">
                {{ scope.row.count.toLocaleString() }}
              </template>
            </el-table-column>
            <el-table-column prop="rate" label="占比" width="120" sortable align="right">
              <template #default="scope">
                {{ scope.row.rate }}%
              </template>
            </el-table-column>
            <el-table-column label="比例" width="300">
              <template #default="scope">
                <el-progress
                  :percentage="parseFloat(scope.row.rate)"
                  :color="scope.row.category === '外省学生' ? '#67c23a' : '#e6a23c'"
                  :show-text="false"
                />
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, HomeFilled } from '@element-plus/icons-vue'

// 饼状图引用
const pieChart1 = ref<HTMLElement>()
const pieChart2 = ref<HTMLElement>()

// 生源地统计数据
const totalCount = ref(0)
const localCount = ref(0)
const nonLocalCount = ref(0)
const localRate = ref(0)
const nonLocalRate = ref(0)

// 学历统计数据
const educationData = ref<any[]>([])
const educationTotal = ref(0)
const educationTableData = ref<any[]>([])

// 表格数据
const provinceTableData = ref<any[]>([])

let chart1: any = null
let chart2: any = null

// 获取数据
const fetchData = async () => {
  try {
    console.log('开始获取数据...')

    // 获取生源地数据 - 修正：接口返回的是对象数组
    const provinceResponse = await axios.get("/school/provinceRate")
    console.log('生源地API响应数据:', provinceResponse.data)

    const provinceData = provinceResponse.data
    if (Array.isArray(provinceData) && provinceData.length >= 2) {
      // 注意：数据库字段是 yes_or_no 和 rate，Java实体类映射为 yesOrNo 和 rate
      // 需要根据 yesOrNo 字段来判断是本省还是外省
      const nonLocalItem = provinceData.find(item => item.yesOrNo === 'no')
      const localItem = provinceData.find(item => item.yesOrNo === 'yes')

      if (nonLocalItem && localItem) {
        // 这里rate是比例，需要转换为具体人数（假设有总数）
        // 由于没有具体总数，我们使用rate的比例关系
        const totalRate = nonLocalItem.rate + localItem.rate

        // 总人数为150000显示比例
        const totalStudents = 150000
        nonLocalCount.value = Math.round(nonLocalItem.rate * totalStudents)
        localCount.value = Math.round(localItem.rate * totalStudents)
        totalCount.value = nonLocalCount.value + localCount.value

        // 计算比例
        if (totalCount.value > 0) {
          nonLocalRate.value = parseFloat(((nonLocalCount.value / totalCount.value) * 100).toFixed(2))
          localRate.value = parseFloat(((localCount.value / totalCount.value) * 100).toFixed(2))
        }

        // 准备表格数据
        provinceTableData.value = [
          {
            category: '外省学生',
            type: 'success',
            count: nonLocalCount.value,
            rate: nonLocalRate.value.toFixed(2)
          },
          {
            category: '本省学生',
            type: 'warning',
            count: localCount.value,
            rate: localRate.value.toFixed(2)
          }
        ]
      }
    } else {
      console.warn('生源地数据格式不正确')
      return
    }

    // 获取学历数据
    const educationResponse = await axios.get("/school/studentEducationLevel")
    console.log('学历API响应数据:', educationResponse.data)

    const eduData = educationResponse.data
    if (Array.isArray(eduData)) {
      educationData.value = eduData

      // 计算总数
      educationTotal.value = eduData.reduce((sum, item) => sum + item.count, 0)

      // 计算比例并添加到数据中
      educationData.value = eduData.map(item => ({
        ...item,
        rate: educationTotal.value > 0 ? ((item.count / educationTotal.value) * 100).toFixed(2) : '0.00'
      }))

      // 准备表格数据
      educationTableData.value = educationData.value.map(item => ({
        studentEducation: item.studentEducation,
        count: item.count,
        rate: item.rate
      }))
    }

    // 渲染图表
    renderCharts()
    ElMessage.success('数据加载成功')
  } catch (error) {
    console.error('获取数据失败:', error)
    ElMessage.error('获取数据失败，使用测试数据')

  }
}

// 渲染饼状图
const renderCharts = () => {
  nextTick(() => {
    renderProvinceChart()  // 渲染生源地饼状图
    renderEducationChart() // 渲染学历饼状图
  })
}

// 渲染生源地饼状图
const renderProvinceChart = () => {
  if (!pieChart1.value) {
    console.error('饼状图容器1未找到')
    return
  }

  // 销毁之前的图表实例
  if (chart1) {
    chart1.dispose()
  }

  // 初始化图表
  chart1 = echarts.init(pieChart1.value)

  const option1 = {
    title: {
      text: '本省 vs 外省学生分布',
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 16
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        const data = params.data
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
            <div style="margin: 3px 0;">
              <span>数量：<span style="font-weight: bold;">${data.value.toLocaleString()}</span></span>
            </div>
            <div style="margin: 3px 0;">
              <span>占比：<span style="font-weight: bold; color: ${params.color};">${data.rate}%</span></span>
            </div>
          </div>
        `
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      formatter: function(name: string) {
        const data = option1.series[0].data.find((item: any) => item.name === name)
        return `${name}\n${data.value.toLocaleString()}人 (${data.rate}%)`
      },
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '学生生源地分布',
        type: 'pie',
        radius: ['35%', '65%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'inner',
          formatter: '{d}%',
          fontSize: 14,
          fontWeight: 'bold'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: [
          {
            value: nonLocalCount.value,
            name: '外省学生',
            rate: nonLocalRate.value.toFixed(2),
            itemStyle: {
              color: '#67c23a' // 使用固定颜色
            }
          },
          {
            value: localCount.value,
            name: '本省学生',
            rate: localRate.value.toFixed(2),
            itemStyle: {
              color: '#e6a23c' // 使用固定颜色
            }
          }
        ]
      }
    ]
  }

  chart1.setOption(option1)
  console.log('生源地饼状图渲染完成')
}

// 渲染学历饼状图
const renderEducationChart = () => {
  if (!pieChart2.value) {
    console.error('饼状图容器2未找到')
    return
  }

  // 销毁之前的图表实例
  if (chart2) {
    chart2.dispose()
  }

  // 初始化图表
  chart2 = echarts.init(pieChart2.value)

  // 学历颜色映射
  const educationColors: Record<string, string> = {
    '本科': '#409EFF',
    '硕士': '#67c23a',
    '博士': '#e6a23c'
  }

  const option2 = {
    title: {
      text: '学生学历分布',
      left: 'center',
      textStyle: {
        color: '#333',
        fontSize: 16
      }
    },
    tooltip: {
      trigger: 'item',
      formatter: function(params: any) {
        const data = params.data
        return `
          <div style="padding: 8px;">
            <div style="font-weight: bold; margin-bottom: 5px;">${data.name}</div>
            <div style="margin: 3px 0;">
              <span>数量：<span style="font-weight: bold;">${data.value.toLocaleString()}</span></span>
            </div>
            <div style="margin: 3px 0;">
              <span>占比：<span style="font-weight: bold; color: ${params.color};">${data.rate}%</span></span>
            </div>
          </div>
        `
      }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      formatter: function(name: string) {
        const data = option2.series[0].data.find((item: any) => item.name === name)
        return `${name}\n${data.value.toLocaleString()}人 (${data.rate}%)`
      },
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '学生学历分布',
        type: 'pie',
        radius: ['35%', '65%'],
        center: ['40%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'inner',
          formatter: '{d}%',
          fontSize: 14,
          fontWeight: 'bold'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: educationData.value.map(item => ({
          value: item.count,
          name: item.studentEducation,
          rate: item.rate,
          itemStyle: {
            color: educationColors[item.studentEducation] || '#909399'
          }
        }))
      }
    ]
  }

  chart2.setOption(option2)
  console.log('学历饼状图渲染完成')
}

// 获取学历颜色
const getEducationColor = (education: string): string => {
  const colors: Record<string, string> = {
    '本科': '#409EFF',
    '硕士': '#67c23a',
    '博士': '#e6a23c'
  }
  return colors[education] || '#909399'
}

// 获取学历标签类型
const getEducationTagType = (education: string): string => {
  const types: Record<string, string> = {
    '本科': 'primary',
    '硕士': 'success',
    '博士': 'warning'
  }
  return types[education] || 'info'
}

// 获取学历符号
const getEducationSymbol = (education: string): string => {
  const symbols: Record<string, string> = {
    '本科': '🎓',
    '硕士': '📚',
    '博士': '🎓'
  }
  return symbols[education] || '📝'
}

// 窗口大小改变时重绘
const handleResize = () => {
  if (chart1) chart1.resize()
  if (chart2) chart2.resize()
}

// 组件挂载时获取数据
onMounted(() => {
  console.log('TestPage5 组件已挂载')

  // 先尝试使用API获取数据，如果失败则使用测试数据
  fetchData()

  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
onUnmounted(() => {
  if (chart1) chart1.dispose()
  if (chart2) chart2.dispose()
  window.removeEventListener('resize', handleResize)
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

/* 修改头部为渐变色 */
.page-header {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  padding: 15px 20px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 4px 12px rgba(67, 233, 123, 0.3);
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: white;
  font-size: 20px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.charts-container {
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

/* 添加图表容器样式 */
.chart-container {
  background: white;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  flex: 1;
}

.stats-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.stats-section {
  display: flex;
  flex-direction: column;
}

.stat-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px 0;
}

.stat-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #43e97b;
}

.stat-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-radius: 8px;
  background: #f5f7fa;
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

.stat-info {
  flex: 1;
}

.stat-count {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-rate {
  font-size: 14px;
  font-weight: bold;
}

.stat-progress {
  margin-top: 10px;
}

.stat-education-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.education-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.education-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.education-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 10px;
  color: white;
}

.education-info {
  text-align: center;
}

.education-count {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.education-name {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.education-rate {
  font-size: 14px;
  font-weight: bold;
  color: #43e97b;
}

.table-container {
  margin-top: 20px;
}

.table-section {
  margin-bottom: 30px;
}

.table-section h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.data-table {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .charts-container,
  .stats-container {
    grid-template-columns: 1fr;
  }

  .stat-details,
  .stat-education-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .chart-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .stat-item {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>
