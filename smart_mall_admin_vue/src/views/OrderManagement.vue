<template>
  <div class="order-management">
    <h2>订单管理</h2>
    <div class="action-bar">
      <el-input v-model="searchQuery" placeholder="搜索订单..." class="search-input" @input="handleSearch" />
    </div>
    <el-table :data="filteredOrders" style="width: 100%" class="order-table">
      <el-table-column prop="id" label="订单ID" width="100" />
      <el-table-column prop="userName" label="用户" width="150" />
      <el-table-column prop="totalAmount" label="订单金额 (¥)" width="120" />
      <el-table-column prop="status" label="订单状态" width="120" />
      <el-table-column prop="createTime" label="下单时间" width="180" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleViewDetail(scope.row)">查看详情</el-button>
          <el-button size="small" type="warning" @click="handleProcessRefund(scope.row)" v-if="scope.row.status === '申请退货'">处理退货</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="totalOrders"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      class="pagination"
    />

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" v-model="detailDialogVisible" width="600px">
      <div class="order-detail">
        <p><strong>订单ID：</strong> {{ currentOrder.id }}</p>
        <p><strong>用户：</strong> {{ currentOrder.userName }}</p>
        <p><strong>订单金额：</strong> ¥{{ currentOrder.totalAmount }}</p>
        <p><strong>订单状态：</strong> {{ currentOrder.status }}</p>
        <p><strong>下单时间：</strong> {{ currentOrder.createTime }}</p>
        <p><strong>收货地址：</strong> {{ currentOrder.address }}</p>
        <h3>商品列表</h3>
        <el-table :data="currentOrder.items" style="width: 100%" border>
          <el-table-column prop="name" label="商品名称" width="200" />
          <el-table-column prop="price" label="价格 (¥)" width="100" />
          <el-table-column prop="quantity" label="数量" width="80" />
        </el-table>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 处理退货对话框 -->
    <el-dialog title="处理退货" v-model="refundDialogVisible" width="500px">
      <el-form :model="refundForm" ref="refundFormRef" label-width="100px">
        <el-form-item label="订单ID">
          <el-input v-model="refundForm.orderId" disabled />
        </el-form-item>
        <el-form-item label="退货状态" prop="refundStatus">
          <el-select v-model="refundForm.refundStatus" placeholder="请选择处理结果">
            <el-option label="批准退货" value="approved" />
            <el-option label="拒绝退货" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注" prop="remark">
          <el-input type="textarea" v-model="refundForm.remark" placeholder="请输入处理备注" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="refundDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRefundForm">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

// 订单列表数据
const orders = ref([])
// 搜索关键字
const searchQuery = ref('')
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)

// 计算过滤后的订单列表（支持搜索）
const filteredOrders = computed(() => {
  let result = orders.value
  if (searchQuery.value) {
    result = result.filter(order =>
      order.id.toString().includes(searchQuery.value) || 
      order.userName.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

// 总条目数
const totalOrders = computed(() => orders.value.length)

// 订单详情对话框显示状态
const detailDialogVisible = ref(false)
// 当前查看的订单
const currentOrder = ref({ items: [] })

// 处理退货对话框显示状态
const refundDialogVisible = ref(false)
// 退货处理表单
const refundForm = ref({
  orderId: '',
  refundStatus: '',
  remark: ''
})
const refundFormRef = ref(null)

// 页面加载时获取订单列表
onMounted(() => {
  fetchOrders()
})

// 获取订单列表
const fetchOrders = async () => {
  try {
    const response = await request.get('/api/orders')
    if (response.data.code === 200) {
      orders.value = response.data.data
    } else {
      ElMessage.error('获取订单列表失败: ' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取订单列表失败: ' + error.message)
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
}

// 处理翻页
const handlePageChange = (page) => {
  currentPage.value = page
}

// 查看订单详情
const handleViewDetail = (row) => {
  currentOrder.value = row
  detailDialogVisible.value = true
}

// 处理退货
const handleProcessRefund = (row) => {
  refundForm.value.orderId = row.id
  refundForm.value.refundStatus = ''
  refundForm.value.remark = ''
  refundDialogVisible.value = true
}

// 提交退货处理表单
const submitRefundForm = () => {
  if (!refundForm.value.refundStatus) {
    ElMessage.error('请选择处理结果')
    return
  }
  ElMessageBox.confirm('确定提交退货处理结果吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.post(`/api/orders/${refundForm.value.orderId}/refund`, {
        status: refundForm.value.refundStatus,
        remark: refundForm.value.remark
      })
      if (response.data.code === 200) {
        ElMessage.success('退货处理成功')
        refundDialogVisible.value = false
        fetchOrders() // 重新获取列表
      } else {
        ElMessage.error('退货处理失败: ' + response.data.msg)
      }
    } catch (error) {
      ElMessage.error('退货处理失败: ' + error.message)
    }
  }).catch(() => {
    ElMessage.info('已取消提交')
  })
}
</script>

<style scoped>
.order-management {
  padding: 20px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.order-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.order-detail p {
  margin: 10px 0;
}

.order-detail h3 {
  margin-top: 20px;
  margin-bottom: 10px;
}
</style>