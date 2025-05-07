<template>
  <div class="combo-management">
    <h2>套餐管理</h2>
    <div class="action-bar">
      <el-button type="primary" @click="handleAddCombo">添加套餐</el-button>
      <el-input v-model="searchQuery" placeholder="搜索套餐..." class="search-input" @input="handleSearch" />
    </div>
    <el-table :data="filteredCombos" style="width: 100%" class="combo-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="套餐名称" width="200" />
      <el-table-column prop="price" label="价格 (¥)" width="100" />
      <el-table-column prop="description" label="描述" width="300" />
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEditCombo(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteCombo(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="totalCombos"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      class="pagination"
    />

    <!-- 添加/编辑套餐对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="comboForm" :rules="rules" ref="comboFormRef" label-width="80px">
        <el-form-item label="套餐名称" prop="name">
          <el-input v-model="comboForm.name" placeholder="请输入套餐名称" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="comboForm.price" :min="0" :precision="2" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="套餐描述" prop="description">
          <el-input type="textarea" v-model="comboForm.description" placeholder="请输入套餐描述" rows="3" />
        </el-form-item>
        <el-form-item label="包含商品" prop="items">
          <el-select v-model="comboForm.items" multiple placeholder="请选择包含的商品" style="width: 100%;">
            <el-option v-for="item in availableItems" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

// 套餐列表数据
const combos = ref([])
// 搜索关键字
const searchQuery = ref('')
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)
// 可用商品列表
const availableItems = ref([])

// 计算过滤后的套餐列表（支持搜索）
const filteredCombos = computed(() => {
  let result = combos.value
  if (searchQuery.value) {
    result = result.filter(combo =>
      combo.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

// 总条目数
const totalCombos = computed(() => combos.value.length)

// 对话框标题
const dialogTitle = ref('添加套餐')
// 对话框显示状态
const dialogVisible = ref(false)
// 是否为编辑模式
const isEditMode = ref(false)
// 套餐表单数据
const comboForm = ref({
  id: null,
  name: '',
  price: 0,
  description: '',
  items: []
})
// 表单引用
const comboFormRef = ref(null)
// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入套餐名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  items: [{ required: true, message: '请选择包含的商品', trigger: 'change' }]
}

// 页面加载时获取套餐列表和可用商品列表
onMounted(() => {
  fetchCombos()
  fetchAvailableItems()
})

// 获取套餐列表
const fetchCombos = async () => {
  try {
    const response = await request.get('/api/combos')
    if (response.data.code === 200) {
      combos.value = response.data.data
    } else {
      ElMessage.error('获取套餐列表失败: ' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取套餐列表失败: ' + error.message)
  }
}

// 获取可用商品列表
const fetchAvailableItems = async () => {
  try {
    const response = await request.get('/api/items')
    if (response.data.code === 200) {
      availableItems.value = response.data.data
    } else {
      ElMessage.error('获取商品列表失败: ' + response.data.msg)
    }
  } catch (error) {
    ElMessage.error('获取商品列表失败: ' + error.message)
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

// 处理添加套餐
const handleAddCombo = () => {
  dialogTitle.value = '添加套餐'
  isEditMode.value = false
  dialogVisible.value = true
  resetForm()
}

// 处理编辑套餐
const handleEditCombo = (row) => {
  dialogTitle.value = '编辑套餐'
  isEditMode.value = true
  dialogVisible.value = true
  // 填充表单数据
  comboForm.value = { ...row }
}

// 处理删除套餐
const handleDeleteCombo = (id) => {
  ElMessageBox.confirm('确定要删除此套餐吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await request.delete(`/api/combos/${id}`)
      if (response.data.code === 200) {
        ElMessage.success('删除成功')
        fetchCombos() // 重新获取列表
      } else {
        ElMessage.error('删除失败: ' + response.data.msg)
      }
    } catch (error) {
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 重置表单
const resetForm = () => {
  comboForm.value = {
    id: null,
    name: '',
    price: 0,
    description: '',
    items: []
  }
  if (comboFormRef.value) {
    comboFormRef.value.resetFields()
  }
}

// 提交表单
const submitForm = () => {
  comboFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let response
        if (isEditMode.value) {
          // 编辑模式
          response = await request.put(`/api/combos/${comboForm.value.id}`, comboForm.value)
        } else {
          // 添加模式
          response = await request.post('/api/combos', comboForm.value)
        }
        if (response.data.code === 200) {
          ElMessage.success(isEditMode.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchCombos() // 重新获取列表
        } else {
          ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + response.data.msg)
        }
      } catch (error) {
        ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + error.message)
      }
    } else {
      return false
    }
  })
}
</script>

<style scoped>
.combo-management {
  padding: 20px;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20px;
}

.search-input {
  width: 300px;
}

.combo-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}
</style>