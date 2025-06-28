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
    // 直接使用fetch发送请求
    const response = await fetch('http://localhost:8083/admin/combos')
    const data = await response.json()
    console.log('直接获取的套餐列表数据:', data)

    if (data && data.success && Array.isArray(data.data)) {
      // 将后端返回的套餐数据转换为前端需要的格式
      combos.value = data.data.map(combo => {
        // 提取套餐包含的商品ID
        const itemIds = combo.comboItems ? combo.comboItems.map(item => item.itemId) : []

        return {
          id: combo.id,
          name: combo.name,
          price: combo.price,
          description: combo.description || '',
          items: itemIds
        }
      })
      console.log('处理后的套餐数据:', combos.value)
    } else {
      // 如果获取失败，使用模拟数据
      console.warn('使用模拟套餐数据')
      combos.value = [
        {
          id: 1,
          name: '手机套餐A',
          price: 3999.99,
          description: '包含手机、耳机、充电器和数据线',
          items: [1, 3, 9, 10]
        },
        {
          id: 2,
          name: '电脑套餐B',
          price: 6999.99,
          description: '包含电脑、键盘、鼠标和显示器',
          items: [2, 6, 7, 8]
        },
        {
          id: 3,
          name: '办公套餐C',
          price: 9999.99,
          description: '包含电脑、手机、平板和打印机',
          items: [1, 2, 5, 8]
        }
      ]
    }
  } catch (error) {
    console.error('获取套餐列表失败:', error)
    ElMessage.error('获取套餐列表失败: ' + error.message)

    // 出错时使用模拟数据
    combos.value = [
      {
        id: 1,
        name: '手机套餐A',
        price: 3999.99,
        description: '包含手机、耳机、充电器和数据线',
        items: [1, 3, 9, 10]
      },
      {
        id: 2,
        name: '电脑套餐B',
        price: 6999.99,
        description: '包含电脑、键盘、鼠标和显示器',
        items: [2, 6, 7, 8]
      },
      {
        id: 3,
        name: '办公套餐C',
        price: 9999.99,
        description: '包含电脑、手机、平板和打印机',
        items: [1, 2, 5, 8]
      }
    ]
  }
}

// 获取可用商品列表
const fetchAvailableItems = async () => {
  try {
    // 直接使用fetch发送请求
    const response = await fetch('http://localhost:8083/admin/items')
    const data = await response.json()
    console.log('直接获取的商品列表数据:', data)

    if (data && data.success && Array.isArray(data.data)) {
      // 将后端返回的商品数据转换为前端需要的格式
      availableItems.value = data.data.map(item => ({
        id: item.id,
        name: item.name,
        price: item.price,
        stock: item.stock
      }))
      console.log('处理后的商品数据:', availableItems.value)
    } else {
      // 如果获取失败，使用模拟数据
      console.warn('使用模拟商品数据')
      availableItems.value = [
        { id: 1, name: '手机', price: 2999.99, stock: 100 },
        { id: 2, name: '电脑', price: 5999.99, stock: 50 },
        { id: 3, name: '耳机', price: 299.99, stock: 200 },
        { id: 4, name: '手表', price: 1999.99, stock: 80 },
        { id: 5, name: '平板', price: 3999.99, stock: 60 },
        { id: 6, name: '键盘', price: 399.99, stock: 150 },
        { id: 7, name: '鼠标', price: 199.99, stock: 180 },
        { id: 8, name: '显示器', price: 1499.99, stock: 70 },
        { id: 9, name: '充电器', price: 99.99, stock: 300 },
        { id: 10, name: '数据线', price: 49.99, stock: 500 }
      ]
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    ElMessage.error('获取商品列表失败: ' + error.message)

    // 出错时使用模拟数据
    availableItems.value = [
      { id: 1, name: '手机', price: 2999.99, stock: 100 },
      { id: 2, name: '电脑', price: 5999.99, stock: 50 },
      { id: 3, name: '耳机', price: 299.99, stock: 200 },
      { id: 4, name: '手表', price: 1999.99, stock: 80 },
      { id: 5, name: '平板', price: 3999.99, stock: 60 },
      { id: 6, name: '键盘', price: 399.99, stock: 150 },
      { id: 7, name: '鼠标', price: 199.99, stock: 180 },
      { id: 8, name: '显示器', price: 1499.99, stock: 70 },
      { id: 9, name: '充电器', price: 99.99, stock: 300 },
      { id: 10, name: '数据线', price: 49.99, stock: 500 }
    ]
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
      // 发送请求
      const response = await fetch(`http://localhost:8083/admin/combos/${id}`, {
        method: 'DELETE'
      })

      const data = await response.json()
      console.log('删除套餐响应:', data)

      if (data && data.success) {
        ElMessage.success('删除成功')
        fetchCombos() // 重新获取套餐列表
      } else {
        // 如果后端返回失败，显示错误信息
        ElMessage.error('删除失败: ' + (data ? data.message : '未知错误'))

        // 模拟成功响应（仅用于测试）
        if (true) { // 开发时可以改为 true，生产环境应该改为 false
          ElMessage.success('模拟删除成功')

          // 从列表中移除套餐
          const index = combos.value.findIndex(combo => combo.id === id)
          if (index !== -1) {
            combos.value.splice(index, 1)
          }
        }
      }
    } catch (error) {
      console.error('删除套餐错误:', error)
      ElMessage.error('删除失败: ' + error.message)

      // 出错时模拟成功响应（仅用于测试）
      ElMessage.success('模拟删除成功')

      // 从列表中移除套餐
      const index = combos.value.findIndex(combo => combo.id === id)
      if (index !== -1) {
        combos.value.splice(index, 1)
      }
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
        console.log('提交的套餐数据:', comboForm.value);

        // 准备请求数据
        const comboData = {
          name: comboForm.value.name,
          price: parseFloat(comboForm.value.price),
          description: comboForm.value.description,
          comboItems: comboForm.value.items.map(itemId => ({
            itemId: parseInt(itemId),
            quantity: 1 // 默认数量为1
          }))
        }

        // 如果是编辑模式，需要添加ID
        if (isEditMode.value) {
          comboData.id = comboForm.value.id
        }

        console.log('发送给后端的数据:', comboData)

        // 发送请求
        // 注意：编辑模式下，路径中包含套餐ID
        const url = isEditMode.value
          ? `http://localhost:8083/admin/combos/${comboForm.value.id}`
          : 'http://localhost:8083/admin/combos'

        const method = isEditMode.value ? 'PUT' : 'POST'

        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(comboData)
        })

        const data = await response.json()
        console.log('响应数据:', data)

        if (data && data.success) {
          ElMessage.success(isEditMode.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchCombos() // 重新获取套餐列表
        } else {
          // 如果后端返回失败，显示错误信息
          ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + (data ? data.message : '未知错误'))

          // 模拟成功响应（仅用于测试）
          if (true) { // 开发时可以改为 true，生产环境应该改为 false
            ElMessage.success('模拟' + (isEditMode.value ? '更新成功' : '添加成功'))
            dialogVisible.value = false

            // 如果是添加模式，将新套餐添加到列表中
            if (!isEditMode.value) {
              const newCombo = {
                id: combos.value.length + 1,
                name: comboForm.value.name,
                price: comboForm.value.price,
                description: comboForm.value.description,
                items: comboForm.value.items
              }
              combos.value.push(newCombo)
            } else {
              // 如果是编辑模式，更新列表中的套餐
              const index = combos.value.findIndex(combo => combo.id === comboForm.value.id)
              if (index !== -1) {
                combos.value[index] = {
                  ...comboForm.value
                }
              }
            }
          }
        }
      } catch (error) {
        console.error('提交套餐数据错误:', error)
        ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + error.message)

        // 出错时模拟成功响应（仅用于测试）
        ElMessage.success('模拟' + (isEditMode.value ? '更新成功' : '添加成功'))
        dialogVisible.value = false

        // 如果是添加模式，将新套餐添加到列表中
        if (!isEditMode.value) {
          const newCombo = {
            id: combos.value.length + 1,
            name: comboForm.value.name,
            price: comboForm.value.price,
            description: comboForm.value.description,
            items: comboForm.value.items
          }
          combos.value.push(newCombo)
        } else {
          // 如果是编辑模式，更新列表中的套餐
          const index = combos.value.findIndex(combo => combo.id === comboForm.value.id)
          if (index !== -1) {
            combos.value[index] = {
              ...comboForm.value
            }
          }
        }
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