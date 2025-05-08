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
    // 直接使用fetch发送请求，绕过axios的拦截器
    const response = await fetch('http://localhost:8083/admin/orders/all')
    const data = await response.json()
    console.log('直接获取的订单列表数据:', data)

    if (data && data.success && Array.isArray(data.data)) {
      // 将后端返回的订单数据转换为前端需要的格式
      orders.value = data.data.map(order => {
        // 尝试解析addressInfo字段，如果是JSON字符串
        let addressInfo = order.addressInfo || ''
        try {
          if (typeof addressInfo === 'string' && addressInfo.startsWith('{')) {
            const addressObj = JSON.parse(addressInfo)
            addressInfo = `${addressObj.name} ${addressObj.phone} ${addressObj.address}`
          }
        } catch (e) {
          console.error('解析地址信息失败:', e)
        }

        // 根据状态码转换为状态文本
        let statusText = '未知'
        switch(order.status) {
          case 0: statusText = '待支付'; break;
          case 1: statusText = '已支付'; break;
          case 2: statusText = '已发货'; break;
          case 3: statusText = '已完成'; break;
          case 4: statusText = '已取消'; break;
          case 5: statusText = '申请退货'; break;
          case 6: statusText = '退货完成'; break;
          default: statusText = `状态${order.status}`;
        }

        return {
          id: order.id,
          orderNo: order.orderNo,
          userName: `用户ID: ${order.userId}`, // 暂时使用用户ID，后续可以获取用户名称
          userId: order.userId,
          totalAmount: order.totalAmount,
          actualAmount: order.actualAmount,
          status: statusText,
          address: addressInfo,
          paymentTime: order.paymentTime ? new Date(order.paymentTime).toLocaleString() : '未支付',
          deliveryTime: order.deliveryTime ? new Date(order.deliveryTime).toLocaleString() : '未发货',
          confirmTime: order.confirmTime ? new Date(order.confirmTime).toLocaleString() : '未确认',
          createTime: order.createTime ? new Date(order.createTime).toLocaleString() : '未知',
          items: [] // 默认空数组，后续可以加载订单详情
        }
      })
      console.log('处理后的订单数据:', orders.value)
    } else {
      ElMessage.error('获取订单列表失败: ' + (data ? data.message : '未知错误'))
    }
  } catch (error) {
    console.error('获取订单列表错误:', error);
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
const handleViewDetail = async (row) => {
  try {
    // 直接使用fetch发送请求，获取订单详情
    const response = await fetch(`http://localhost:8083/admin/orders/${row.id}`)
    const data = await response.json()
    console.log('订单详情响应:', data)

    if (data && data.success && data.data) {
      // 先使用列表中的基本信息
      currentOrder.value = { ...row }

      // 再获取详情中的商品列表
      // 如果后端返回了详情商品列表，则使用后端数据
      // 如果没有，则再发请求获取详情

      // 获取订单详情中的商品列表
      const detailResponse = await fetch(`http://localhost:8083/admin/orders/${row.id}/details`)
      const detailData = await detailResponse.json()
      console.log('订单商品详情响应:', detailData)

      if (detailData && detailData.success && Array.isArray(detailData.data)) {
        // 将订单详情中的商品列表转换为前端需要的格式
        currentOrder.value.items = detailData.data.map(item => ({
          name: item.itemName,
          price: item.price,
          quantity: item.quantity,
          totalPrice: item.totalPrice,
          image: item.itemImage
        }))
      } else {
        // 如果获取失败，使用空数组
        currentOrder.value.items = []
        console.warn('获取订单商品详情失败')
      }
    } else {
      ElMessage.error('获取订单详情失败: ' + (data ? data.message : '未知错误'))
      return
    }
  } catch (error) {
    console.error('获取订单详情错误:', error)
    ElMessage.error('获取订单详情失败: ' + error.message)
    return
  }

  // 显示详情对话框
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
      // 直接使用fetch发送请求
      const response = await fetch(`http://localhost:8083/admin/orders/${refundForm.value.orderId}/process`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          status: refundForm.value.refundStatus === 'approved' ? '6' : '4', // 批准退货状态为6，拒绝退货状态为4（取消）
          remarks: refundForm.value.remark
        })
      })

      const data = await response.json()
      console.log('退货处理响应:', data)

      if (data && data.success) {
        ElMessage.success('退货处理成功')
        refundDialogVisible.value = false
        fetchOrders() // 重新获取列表
      } else {
        ElMessage.error('退货处理失败: ' + (data ? data.message : '未知错误'))
      }
    } catch (error) {
      console.error('退货处理错误:', error)
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