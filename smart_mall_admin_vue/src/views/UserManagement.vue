<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <div class="action-bar">
      <el-input v-model="searchQuery" placeholder="搜索用户..." class="search-input" @input="handleSearch" />
    </div>
    <el-table :data="filteredUsers" style="width: 100%" class="user-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="phone" label="电话" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.status === 'enabled' ? 'success' : 'danger'">
            {{ scope.row.status === 'enabled' ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleViewDetail(scope.row)">查看详情</el-button>
          <el-button size="small" :type="scope.row.status === 'enabled' ? 'danger' : 'primary'" @click="handleToggleStatus(scope.row)">
            {{ scope.row.status === 'enabled' ? '禁用' : '启用' }}
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="totalUsers"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      class="pagination"
    />

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" v-model="detailDialogVisible" width="500px">
      <div class="user-detail">
        <p><strong>ID：</strong> {{ currentUser.id }}</p>
        <p><strong>用户名：</strong> {{ currentUser.username }}</p>
        <p><strong>邮箱：</strong> {{ currentUser.email }}</p>
        <p><strong>电话：</strong> {{ currentUser.phone }}</p>
        <p><strong>状态：</strong> {{ currentUser.status === 'enabled' ? '启用' : '禁用' }}</p>
        <p><strong>注册时间：</strong> {{ currentUser.registerTime }}</p>
        <p><strong>最后登录：</strong> {{ currentUser.lastLogin }}</p>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

// 用户列表数据
const users = ref([])
// 搜索关键字
const searchQuery = ref('')
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)

// 计算过滤后的用户列表（支持搜索）
const filteredUsers = computed(() => {
  let result = users.value
  if (searchQuery.value) {
    result = result.filter(user =>
      user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

// 总条目数
const totalUsers = computed(() => users.value.length)

// 用户详情对话框显示状态
const detailDialogVisible = ref(false)
// 当前查看的用户
const currentUser = ref({})

// 页面加载时获取用户列表
onMounted(() => {
  fetchUsers()
})

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await request.get('/api/users')
    if (response.data.code === 200) {
      users.value = response.data.data
    } else {
      ElMessage.error('获取用户列表失败: ' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败: ' + error.message)
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

// 查看用户详情
const handleViewDetail = (row) => {
  currentUser.value = row
  detailDialogVisible.value = true
}

// 切换用户状态（启用/禁用）
const handleToggleStatus = (row) => {
  const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'
  const actionText = row.status === 'enabled' ? '禁用' : '启用'
  ElMessageBox.confirm(`确定要${actionText}用户 "${row.username}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.put(`/api/users/${row.id}/status`, { status: newStatus })
      if (response.data.code === 200) {
        ElMessage.success(`${actionText}成功`)
        fetchUsers() // 重新获取列表
      } else {
        ElMessage.error(`${actionText}失败: ` + response.data.msg)
      }
    } catch (error) {
      ElMessage.error(`${actionText}失败: ` + error.message)
    }
  }).catch(() => {
    ElMessage.info(`已取消${actionText}`)
  })
}
</script>

<style scoped>
.user-management {
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

.user-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.user-detail p {
  margin: 10px 0;
}
</style>