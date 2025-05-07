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
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'
import * as echarts from 'echarts'

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
onMounted(() => {
  fetchSummaryData()
  fetchChartData()
  initCharts()
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
    const response = await request.get('/api/stats/summary')
    if (response.data.code === 200) {
      summaryData.value = response.data.data
    } else {
      ElMessage.error('获取统计数据失败: ' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取统计数据失败: ' + error.message)
  }
}

// 获取图表数据
const fetchChartData = async () => {
  try {
    // 获取销量趋势数据
    const salesResponse = await request.get('/api/stats/sales-trend')
    if (salesResponse.data.code === 200) {
      salesData.value = salesResponse.data.data
      updateSalesChart()
    }
    // 获取用户活跃度趋势数据
    const activityResponse = await request.get('/api/stats/activity-trend')
    if (activityResponse.data.code === 200) {
      activityData.value = activityResponse.data.data
      updateActivityChart()
    }
    // 获取商品销量排行数据
    const itemSalesResponse = await request.get('/api/stats/item-sales')
    if (itemSalesResponse.data.code === 200) {
      itemSalesData.value = itemSalesResponse.data.data
      updateItemSalesChart()
    }
    // 获取用户地域分布数据
    const userRegionResponse = await request.get('/api/stats/user-region')
    if (userRegionResponse.data.code === 200) {
      userRegionData.value = userRegionResponse.data.data
      updateUserRegionChart()
    }
  } catch (error) {
    ElMessage.error('获取图表数据失败: ' + error.message)
  }
}

// 初始化图表
const initCharts = () => {
  // 初始化销量趋势图
  salesChart = echarts.init(document.getElementById('sales-chart'))
  salesChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [{ data: [], type: 'line', smooth: true, color: '#5470c6' }]
  })

  // 初始化用户活跃度趋势图
  activityChart = echarts.init(document.getElementById('activity-chart'))
  activityChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value', max: 100 },
    series: [{ data: [], type: 'line', smooth: true, color: '#91cc75' }]
  })

  // 初始化商品销量排行图
  itemSalesChart = echarts.init(document.getElementById('item-sales-chart'))
  itemSalesChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    xAxis: { type: 'category', data: [] },
    yAxis: { type: 'value' },
    series: [{ data: [], type: 'bar', color: '#fac858' }]
  })

  // 初始化用户地域分布图
  userRegionChart = echarts.init(document.getElementById('user-region-chart'))
  userRegionChart.setOption({
    tooltip: { trigger: 'item' },
    series: [{ type: 'pie', data: [], radius: '50%', emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } } }]
  })
}

// 更新销量趋势图
const updateSalesChart = () => {
  if (salesChart && salesData.value.length > 0) {
    const dates = salesData.value.map(item => item.date)
    const values = salesData.value.map(item => item.value)
    salesChart.setOption({
      xAxis: { data: dates },
      series: [{ data: values }]
    })
  }
}

// 更新用户活跃度趋势图
const updateActivityChart = () => {
  if (activityChart && activityData.value.length > 0) {
    const dates = activityData.value.map(item => item.date)
    const values = activityData.value.map(item => item.value)
    activityChart.setOption({
      xAxis: { data: dates },
      series: [{ data: values }]
    })
  }
}

// 更新商品销量排行图
const updateItemSalesChart = () => {
  if (itemSalesChart && itemSalesData.value.length > 0) {
    const names = itemSalesData.value.map(item => item.name)
    const values = itemSalesData.value.map(item => item.value)
    itemSalesChart.setOption({
      xAxis: { data: names },
      series: [{ data: values }]
    })
  }
}

// 更新用户地域分布图
const updateUserRegionChart = () => {
  if (userRegionChart && userRegionData.value.length > 0) {
    userRegionChart.setOption({
      series: [{ data: userRegionData.value }]
    })
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