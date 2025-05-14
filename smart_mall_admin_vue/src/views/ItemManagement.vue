<template>
  <div class="item-management">
    <h2>商品管理</h2>
    <div class="action-bar">
      <el-button type="primary" @click="handleAddItem">添加商品</el-button>
      <el-input v-model="searchQuery" placeholder="搜索商品..." class="search-input" @input="handleSearch" />
    </div>
    <el-table :data="filteredItems" style="width: 100%" class="item-table">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="商品名称" width="200" />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="price" label="价格 (¥)" width="100" />
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="图片" width="120">
        <template #default="scope">
          <el-image
            :src="getImageUrl(scope.row.imageUrl)"
            style="width: 50px; height: 50px"
            :preview-src-list="scope.row.imageUrl ? [getImageUrl(scope.row.imageUrl)] : []"
            fit="cover">
            <template #error>
              <div class="image-error-slot">
                <span>加载失败</span>
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <el-button size="small" @click="handleEditItem(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDeleteItem(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage"
      :page-size="pageSize"
      :total="totalItems"
      layout="total, prev, pager, next"
      @current-change="handlePageChange"
      class="pagination"
    />

    <!-- 添加/编辑商品对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="itemForm" :rules="rules" ref="itemFormRef" label-width="80px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="itemForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="itemForm.category" placeholder="请选择分类">
            <el-option v-for="cat in categories" :key="cat.value" :label="cat.label" :value="cat.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="itemForm.price" :min="0" :precision="2" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="itemForm.stock" :min="0" placeholder="请输入库存数量" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input type="textarea" v-model="itemForm.description" placeholder="请输入商品描述" rows="3" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-upload
            :action="uploadAction"
            :headers="uploadHeaders"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :limit="1"
            :file-list="fileList"
            list-type="picture-card"
          >
            <i class="el-icon-plus"></i>
            <template #tip>
              <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </template>
          </el-upload>
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

// 获取图片完整URL
const getImageUrl = (url) => {
  if (!url) return ''
  console.log('原始图片URL:', url);

  // 如果是完整URL，直接返回
  if (url.startsWith('http')) {
    console.log('完整URL:', url);
    return url;
  }

  // 如果是相对路径，添加基础URL
  const fullUrl = `http://localhost:8083${url}`;
  console.log('完整图片URL:', fullUrl);
  return fullUrl;
}

// 商品列表数据
const items = ref([])
// 搜索关键字
const searchQuery = ref('')
// 当前页码
const currentPage = ref(1)
// 每页显示条数
const pageSize = ref(10)
// 商品分类选项
const categories = ref([
  { value: 'mobile', label: '手机' },
  { value: 'computer', label: '电脑' },
  { value: 'accessory', label: '配件' }
])

// 计算过滤后的商品列表（支持搜索）
const filteredItems = computed(() => {
  let result = items.value
  if (searchQuery.value) {
    result = result.filter(item =>
      item.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
  }
  return result.slice((currentPage.value - 1) * pageSize.value, currentPage.value * pageSize.value)
})

// 总条目数
const totalItems = computed(() => items.value.length)

// 对话框标题
const dialogTitle = ref('添加商品')
// 对话框显示状态
const dialogVisible = ref(false)
// 是否为编辑模式
const isEditMode = ref(false)
// 商品表单数据
const itemForm = ref({
  id: null,
  name: '',
  category: '',
  price: 0,
  stock: 0,
  description: '',
  imageUrl: ''
})
// 表单引用
const itemFormRef = ref(null)
// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }]
}
// 上传图片相关
const uploadAction = ref('http://localhost:8083/admin/items/upload') // 后端接口地址
const uploadHeaders = ref({
  'Authorization': `Bearer ${localStorage.getItem('admin-token') || ''}`
})
const fileList = ref([])

// 页面加载时获取商品列表和分类
onMounted(() => {
  fetchItems()
  fetchCategories()
})

// 获取商品列表
const fetchItems = async () => {
  try {
    // 直接使用fetch发送请求，绕过axios的拦截器
    const response = await fetch('http://localhost:8083/admin/items')
    const data = await response.json()
    console.log('直接获取的商品列表数据:', data)

    if (data && data.success && Array.isArray(data.data)) {
      // 将后端返回的商品数据转换为前端需要的格式
      items.value = data.data.map(item => {
        console.log('商品图片URL:', item.mainImage);
        return {
          id: item.id,
          name: item.name,
          category: item.categoryId ? item.categoryId.toString() : '', // 暂时使用分类ID，后续可以获取分类名称
          price: item.price,
          stock: item.stock,
          description: item.description || '',
          imageUrl: item.mainImage || ''
        };
      })
      console.log('处理后的商品数据:', items.value)
    } else {
      ElMessage.error('获取商品列表失败: ' + (data ? data.message : '未知错误'))
    }
  } catch (error) {
    console.error('获取商品列表错误:', error);
    ElMessage.error('获取商品列表失败: ' + error.message)
  }
}

// 获取商品分类
const fetchCategories = async () => {
  try {
    const response = await fetch('http://localhost:8083/selectAll')
    const data = await response.json()
    console.log('直接获取的分类数据:', data)

    if (Array.isArray(data)) {
      // 将后端返回的分类数据转换为前端需要的格式
      categories.value = data.map(cat => ({
        value: cat.id.toString(),
        label: cat.name
      }))
      console.log('处理后的分类数据:', categories.value)
    } else {
      ElMessage.error('获取分类列表失败')
    }
  } catch (error) {
    console.error('获取分类列表错误:', error);
    ElMessage.error('获取分类列表失败: ' + error.message)
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

// 处理添加商品
const handleAddItem = () => {
  dialogTitle.value = '添加商品'
  isEditMode.value = false
  dialogVisible.value = true
  fileList.value = []
  resetForm()
}

// 处理编辑商品
const handleEditItem = (row) => {
  dialogTitle.value = '编辑商品'
  isEditMode.value = true
  dialogVisible.value = true
  // 填充表单数据
  itemForm.value = { ...row }
  fileList.value = row.imageUrl ? [{ name: '商品图片', url: getImageUrl(row.imageUrl) }] : []
}

// 处理删除商品
const handleDeleteItem = (id) => {
  ElMessageBox.confirm('确定要删除此商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 直接使用fetch发送请求
      const response = await fetch(`http://localhost:8083/admin/items/${id}`, {
        method: 'DELETE'
      })
      const data = await response.json()
      console.log('删除商品响应:', data)

      if (data && data.success) {
        ElMessage.success('删除成功')
        fetchItems() // 重新获取列表
      } else {
        ElMessage.error('删除失败: ' + (data ? data.message : '未知错误'))
      }
    } catch (error) {
      console.error('删除商品错误:', error)
      ElMessage.error('删除失败: ' + error.message)
    }
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 重置表单
const resetForm = () => {
  itemForm.value = {
    id: null,
    name: '',
    category: '',
    price: 0,
    stock: 0,
    description: '',
    imageUrl: ''
  }
  fileList.value = []
  if (itemFormRef.value) {
    itemFormRef.value.resetFields()
  }
}

// 提交表单
const submitForm = () => {
  itemFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        console.log('提交的商品数据:', itemForm.value);

        // 准备请求参数
        const url = isEditMode.value
          ? `http://localhost:8083/admin/items/${itemForm.value.id}`
          : 'http://localhost:8083/admin/items';

        const method = isEditMode.value ? 'PUT' : 'POST';

        // 直接使用fetch发送请求
        const response = await fetch(url, {
          method: method,
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            name: itemForm.value.name,
            categoryId: parseInt(itemForm.value.category),
            price: parseFloat(itemForm.value.price),
            stock: parseInt(itemForm.value.stock),
            description: itemForm.value.description,
            mainImage: itemForm.value.imageUrl
          })
        });

        const data = await response.json();
        console.log('响应数据:', data);

        if (data && data.success) {
          ElMessage.success(isEditMode.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          fetchItems() // 重新获取列表
        } else {
          ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + (data ? data.message : '未知错误'))
        }
      } catch (error) {
        console.error('提交商品数据错误:', error);
        ElMessage.error((isEditMode.value ? '更新失败: ' : '添加失败: ') + error.message)
      }
    } else {
      return false
    }
  })
}

// 上传图片成功回调
const handleUploadSuccess = (response, file, fileList) => {
  console.log('上传成功响应:', response);
  if (response.success) {
    // 确保图片URL格式正确
    itemForm.value.imageUrl = response.data // 后端返回的图片URL
    console.log('设置的图片URL:', itemForm.value.imageUrl);
    // 测试图片是否可访问
    const testImg = new Image();
    testImg.onload = () => console.log('图片加载成功:', getImageUrl(itemForm.value.imageUrl));
    testImg.onerror = () => console.error('图片加载失败:', getImageUrl(itemForm.value.imageUrl));
    testImg.src = getImageUrl(itemForm.value.imageUrl);

    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败: ' + response.message)
    fileList.splice(fileList.indexOf(file), 1) // 移除失败的文件
  }
}

// 上传图片失败回调
const handleUploadError = (error, file, fileList) => {
  ElMessage.error('图片上传失败: ' + error.message)
  fileList.splice(fileList.indexOf(file), 1) // 移除失败的文件
}

// 上传前检查
const beforeUpload = (file) => {
  const isImage = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('上传图片只能是 JPG/PNG 格式!')
  }
  if (!isLt2M) {
    ElMessage.error('上传图片大小不能超过 2MB!')
  }
  return isImage && isLt2M
}
</script>

<style scoped>
.item-management {
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

.item-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: center;
}

.el-upload__tip {
  font-size: 12px;
  color: #606266;
  margin-top: 7px;
}

.image-error-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 12px;
}
</style>