<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h2>智能商城后台管理系统</h2>
      </div>
      <el-card class="login-card" :body-style="{ padding: '30px' }">
        <div class="welcome-text">
          <h3>欢迎登录</h3>
          <p>请使用您的管理员账号登录系统</p>
        </div>
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" label-width="0px">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              clearable
            >
              <template #prefix>
                <i class="el-icon-user">👤</i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              show-password
              clearable
              @keyup.enter="handleLogin"
            >
              <template #prefix>
                <i class="el-icon-lock">🔒</i>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-button" @click="handleLogin" :loading="loading">
              登 录
            </el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <p>© 2023 智能商城管理系统</p>
        </div>
      </el-card>
    </div>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1440 320"><path fill="%23ffffff" fill-opacity="0.05" d="M0,192L48,197.3C96,203,192,213,288,229.3C384,245,480,267,576,250.7C672,235,768,181,864,181.3C960,181,1056,235,1152,234.7C1248,235,1344,181,1392,154.7L1440,128L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z"></path></svg>') no-repeat bottom;
  background-size: 100%;
  z-index: 0;
}

.login-box {
  width: 450px;
  z-index: 1;
  animation: fadeIn 0.8s ease-in-out;
}

.login-title {
  text-align: center;
  margin-bottom: 20px;
}

.login-title h2 {
  color: white;
  font-size: 28px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  margin: 0;
}

.login-card {
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
}

.welcome-text {
  text-align: center;
  margin-bottom: 25px;
}

.welcome-text h3 {
  font-size: 22px;
  color: #333;
  margin-bottom: 5px;
}

.welcome-text p {
  color: #666;
  font-size: 14px;
}

.login-button {
  width: 100%;
  height: 45px;
  font-size: 16px;
  border-radius: 4px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%);
  border: none;
  transition: all 0.3s ease;
}

.login-button:hover {
  background: linear-gradient(to right, #00f2fe 0%, #4facfe 100%);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.el-input {
  margin-bottom: 5px;
}

.el-input__inner {
  height: 45px;
}

.el-icon-user, .el-icon-lock {
  font-size: 16px;
  margin-right: 5px;
}
</style>