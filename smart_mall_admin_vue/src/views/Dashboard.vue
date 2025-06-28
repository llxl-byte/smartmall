<template>
  <div class="dashboard">
    <h2>数据统计</h2>
    <div class="summary-cards">
      <el-card class="summary-card">
        <div slot="header" class="clearfix">
          <span>总销量</span>
        </div>
        <div class="summary-value">{{ summaryData.totalSales }}</div>
        <div class="summary-trend" :class="summaryData.salesTrend > 0 ? 'trend-up' : 'trend-down'">
          {{ summaryData.salesTrend > 0 ? '+' : '' }}{{ summaryData.salesTrend }}% 较上月
        </div>
      </el-card>
      <el-card class="summary-card">
        <div slot="header" class="clearfix">
          <span>总订单数</span>
        </div>
        <div class="summary-value">{{ summaryData.totalOrders }}</div>
        <div class="summary-trend" :class="summaryData.ordersTrend > 0 ? 'trend-up' : 'trend-down'">
          {{ summaryData.ordersTrend > 0 ? '+' : '' }}{{ summaryData.ordersTrend }}% 较上月
        </div>
      </el-card>
      <el-card class="summary-card">
        <div slot="header" class="clearfix">
          <span>注册用户数</span>
        </div>
        <div class="summary-value">{{ summaryData.totalUsers }}</div>
        <div class="summary-trend" :class="summaryData.usersTrend > 0 ? 'trend-up' : 'trend-down'">
          {{ summaryData.usersTrend > 0 ? '+' : '' }}{{ summaryData.usersTrend }}% 较上月
        </div>
      </el-card>
      <el-card class="summary-card">
        <div slot="header" class="clearfix">
          <span>用户活跃度</span>
        </div>
        <div class="summary-value">{{ summaryData.userActivity }}%</div>
        <div class="summary-trend" :class="summaryData.activityTrend > 0 ? 'trend-up' : 'trend-down'">
          {{ summaryData.activityTrend > 0 ? '+' : '' }}{{ summaryData.activityTrend }}% 较上月
        </div>
      </el-card>
    </div>
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>销量趋势图</span>
          </div>
          <div id="sales-chart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>用户活跃度趋势图</span>
          </div>
          <div id="activity-chart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>商品销量排行</span>
          </div>
          <div id="item-sales-chart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <div slot="header" class="clearfix">
            <span>用户地域分布</span>
          </div>
          <div id="user-region-chart" style="width: 100%; height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

// 动态导入ECharts
let echarts = null

// 加载ECharts
const loadECharts = async () => {
  try {
    console.log('开始动态加载ECharts...')
    echarts = await import('echarts')
    console.log('ECharts加载成功:', echarts)
    return echarts
  } catch (error) {
    console.error('ECharts加载失败:', error)
    ElMessage.error('ECharts加载失败: ' + error.message)
    return null
  }
}

// 统计数据
const summaryData = ref({
  totalSales: 0,
  totalOrders: 0,
  totalUsers: 0,
  userActivity: 0,
  salesTrend: 0,
  ordersTrend: 0,
  usersTrend: 0,
  activityTrend: 0
})

// 图表数据
const salesData = ref([])
const activityData = ref([])
const itemSalesData = ref([])
const userRegionData = ref([])

// 图表实例
let salesChart = null
let activityChart = null
let itemSalesChart = null
let userRegionChart = null

// 页面加载时获取数据并初始化图表
onMounted(async () => {
  try {
    console.log('组件已挂载，开始初始化...')

    // 获取统计数据
    fetchSummaryData()

    // 加载ECharts
    await loadECharts()

    if (!echarts) {
      console.error('ECharts加载失败，无法初始化图表')
      return
    }

    // 等待DOM完全渲染后再初始化图表
    await nextTick()

    // 初始化图表
    initCharts()

    // 获取并更新图表数据
    fetchChartData()

    console.log('仪表盘初始化完成')
  } catch (error) {
    console.error('仪表盘初始化失败:', error)
    ElMessage.error('仪表盘初始化失败: ' + error.message)
  }
})

// 页面卸载时销毁图表实例
onUnmounted(() => {
  if (salesChart) salesChart.dispose()
  if (activityChart) activityChart.dispose()
  if (itemSalesChart) itemSalesChart.dispose()
  if (userRegionChart) userRegionChart.dispose()
})

// 获取统计数据
const fetchSummaryData = async () => {
  try {
    // 直接使用fetch发送请求
    const response = await fetch('http://localhost:8083/api/stats/summary')
    const data = await response.json()
    console.log('统计数据响应:', data)

    if (data && data.success) {
      summaryData.value = data.data
    } else {
      // 如果获取失败，使用模拟数据
      console.warn('使用模拟统计数据')
      summaryData.value = {
        totalSales: 1256,
        totalOrders: 328,
        totalUsers: 512,
        userActivity: 68,
        salesTrend: 12.5,
        ordersTrend: 8.3,
        usersTrend: 15.2,
        activityTrend: -2.1
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    ElMessage.error('获取统计数据失败: ' + error.message)

    // 出错时使用模拟数据
    summaryData.value = {
      totalSales: 1256,
      totalOrders: 328,
      totalUsers: 512,
      userActivity: 68,
      salesTrend: 12.5,
      ordersTrend: 8.3,
      usersTrend: 15.2,
      activityTrend: -2.1
    }
  }
}

// 获取图表数据
const fetchChartData = async () => {
  try {
    // 使用模拟数据
    console.log('使用模拟图表数据')

    // 销量趋势模拟数据
    salesData.value = [
      { date: '1月', value: 120 },
      { date: '2月', value: 132 },
      { date: '3月', value: 101 },
      { date: '4月', value: 134 },
      { date: '5月', value: 90 },
      { date: '6月', value: 230 },
      { date: '7月', value: 210 },
      { date: '8月', value: 120 },
      { date: '9月', value: 132 },
      { date: '10月', value: 101 },
      { date: '11月', value: 134 },
      { date: '12月', value: 90 }
    ]
    updateSalesChart()

    // 用户活跃度趋势模拟数据
    activityData.value = [
      { date: '1月', value: 45 },
      { date: '2月', value: 52 },
      { date: '3月', value: 48 },
      { date: '4月', value: 55 },
      { date: '5月', value: 60 },
      { date: '6月', value: 58 },
      { date: '7月', value: 62 },
      { date: '8月', value: 65 },
      { date: '9月', value: 68 },
      { date: '10月', value: 64 },
      { date: '11月', value: 70 },
      { date: '12月', value: 68 }
    ]
    updateActivityChart()

    // 商品销量排行模拟数据
    itemSalesData.value = [
      { name: '手机', value: 320 },
      { name: '电脑', value: 240 },
      { name: '耳机', value: 180 },
      { name: '手表', value: 150 },
      { name: '平板', value: 130 },
      { name: '键盘', value: 120 },
      { name: '鼠标', value: 100 },
      { name: '音箱', value: 80 },
      { name: '充电器', value: 60 },
      { name: '数据线', value: 30 }
    ]
    updateItemSalesChart()

    // 用户地域分布模拟数据
    userRegionData.value = [
      { name: '北京', value: 25 },
      { name: '上海', value: 20 },
      { name: '广州', value: 18 },
      { name: '深圳', value: 15 },
      { name: '杭州', value: 12 },
      { name: '南京', value: 10 },
      { name: '其他', value: 20 }
    ]
    updateUserRegionChart()

  } catch (error) {
    console.error('获取图表数据失败:', error)
    ElMessage.error('获取图表数据失败: ' + error.message)
  }
}

// 初始化图表
const initCharts = () => {
  try {
    // 检查DOM元素是否存在
    const salesElement = document.getElementById('sales-chart')
    const activityElement = document.getElementById('activity-chart')
    const itemSalesElement = document.getElementById('item-sales-chart')
    const userRegionElement = document.getElementById('user-region-chart')

    if (!salesElement || !activityElement || !itemSalesElement || !userRegionElement) {
      console.error('图表容器元素未找到')
      ElMessage.error('图表容器元素未找到')
      return
    }

    // 初始化销量趋势图
    salesChart = echarts.init(salesElement)
    salesChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value' },
      series: [{ data: [], type: 'line', smooth: true, color: '#5470c6' }]
    })

    // 初始化用户活跃度趋势图
    activityChart = echarts.init(activityElement)
    activityChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value', max: 100 },
      series: [{ data: [], type: 'line', smooth: true, color: '#91cc75' }]
    })

    // 初始化商品销量排行图
    itemSalesChart = echarts.init(itemSalesElement)
    itemSalesChart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: { type: 'category', data: [] },
      yAxis: { type: 'value' },
      series: [{ data: [], type: 'bar', color: '#fac858' }]
    })

    // 初始化用户地域分布图
    userRegionChart = echarts.init(userRegionElement)
    userRegionChart.setOption({
      tooltip: { trigger: 'item' },
      series: [{ type: 'pie', data: [], radius: '50%', emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } } }]
    })

  } catch (error) {
    console.error('图表初始化失败:', error)
    ElMessage.error('图表初始化失败: ' + error.message)
  }
}

// 更新销量趋势图
const updateSalesChart = () => {
  try {
    if (salesChart && salesData.value.length > 0) {
      const dates = salesData.value.map(item => item.date)
      const values = salesData.value.map(item => item.value)
      salesChart.setOption({
        xAxis: { data: dates },
        series: [{ data: values }]
      })
    }
  } catch (error) {
    console.error('更新销量趋势图失败:', error)
  }
}

// 更新用户活跃度趋势图
const updateActivityChart = () => {
  try {
    if (activityChart && activityData.value.length > 0) {
      const dates = activityData.value.map(item => item.date)
      const values = activityData.value.map(item => item.value)
      activityChart.setOption({
        xAxis: { data: dates },
        series: [{ data: values }]
      })
    }
  } catch (error) {
    console.error('更新用户活跃度趋势图失败:', error)
  }
}

// 更新商品销量排行图
const updateItemSalesChart = () => {
  try {
    if (itemSalesChart && itemSalesData.value.length > 0) {
      const names = itemSalesData.value.map(item => item.name)
      const values = itemSalesData.value.map(item => item.value)
      itemSalesChart.setOption({
        xAxis: { data: names },
        series: [{ data: values }]
      })
    }
  } catch (error) {
    console.error('更新商品销量排行图失败:', error)
  }
}

// 更新用户地域分布图
const updateUserRegionChart = () => {
  try {
    if (userRegionChart && userRegionData.value.length > 0) {
      userRegionChart.setOption({
        series: [{ data: userRegionData.value }]
      })
    }
  } catch (error) {
    console.error('更新用户地域分布图失败:', error)
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.summary-cards {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.summary-card {
  width: 23%;
  text-align: center;
}

.summary-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 10px;
}

.summary-trend {
  font-size: 14px;
}

.trend-up {
  color: #67c23a;
}

.trend-down {
  color: #f56c6c;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 360px;
}
</style>