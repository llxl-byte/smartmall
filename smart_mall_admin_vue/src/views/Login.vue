<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <span>后台管理系统登录</span>
        </div>
      </template>
      <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0px">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            clearable
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
            clearable
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%;">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router' // 导入 useRouter
import request from '../utils/request' // 导入配置好的 Axios 实例

const router = useRouter() // 获取 router 实例

const loginFormRef = ref(null) // 表单引用
const loading = ref(false) // 加载状态

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: '',
})

// 表单校验规则
const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
})

// 处理登录逻辑
const handleLogin = async () => {
  if (!loginFormRef.value) return
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true // 开始加载
      try {
        // 发送 POST 请求到后端 /admin/login 接口
        const response = await request.post('/admin/login', loginForm)

        // 检查后端返回的数据结构 (后端返回 { success: true/false, message: '...', data: ... })
        console.log('登录响应:', response.data)

        if (response.data && response.data.success) {
          ElMessage.success('登录成功')

          // 存储管理员信息
          const adminInfo = response.data.data
          localStorage.setItem('admin-info', JSON.stringify(adminInfo))

          // 存储JWT令牌
          // 在实际项目中，后端应该返回JWT令牌
          // 这里我们使用管理员的用户名生成一个JWT令牌
          const jwtToken = 'Bearer ' + btoa(loginForm.username + ':' + new Date().getTime())
          localStorage.setItem('admin-token', jwtToken)

          // 获取重定向URL (如果有)
          const redirect = router.currentRoute.value.query.redirect || '/'
          router.push(redirect) // 跳转到重定向URL或仪表盘页面
        } else {
          // 如果登录失败，显示后端返回的错误消息
          ElMessage.error(response.data.message || '用户名或密码错误')
        }
      } catch (error) {
        // 处理请求本身发生的错误 (例如网络问题、后端服务未启动等)
        console.error('Login request failed:', error)
        ElMessage.error('登录请求失败，请检查网络或联系管理员')
      } finally {
        loading.value = false // 结束加载
      }
    } else {
      console.log('表单校验失败')
      return false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5; /* 可以设置一个背景色或背景图 */
}

.login-card {
  width: 400px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
}
</style>