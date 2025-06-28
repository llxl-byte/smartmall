import axios from 'axios'
import { ElMessage } from 'element-plus' // 引入 Element Plus 消息提示

// 创建 Axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8083/', // 本地后端地址
  timeout: 10000, // 请求超时时间 (毫秒)
  headers: {'Content-Type': 'application/json;charset=utf-8'} // 设置默认请求头
})

// --- 请求拦截器 ---
service.interceptors.request.use(
  config => {
    // 在发送请求之前做些什么，例如：添加 token
    const token = localStorage.getItem('admin-token') // 从 localStorage 获取 token
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    // 对请求错误做些什么
    console.error('Request Error:', error) // for debug
    return Promise.reject(error)
  }
)

// --- 响应拦截器 ---
service.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    // 后端返回的格式是 { success: true/false, message: '...', data: ... }
    console.log('响应拦截器收到响应:', response);
    const res = response.data
    if (!res.success) { // 如果请求不成功
      ElMessage({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })

      // 如果是未授权错误，跳转到登录页
      if (res.code === 401) {
        // 清除本地存储的token
        localStorage.removeItem('admin-token')
        // 跳转到登录页
        setTimeout(() => {
          window.location.href = '/login'
        }, 1500)
      }

      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      // 成功则直接返回响应
      return response
    }
  },
  error => {
    // 对响应错误做点什么
    console.error('Response Error:', error) // for debug

    // 如果是401错误，说明token无效或过期
    if (error.response && error.response.status === 401) {
      // 清除本地存储的token
      localStorage.removeItem('admin-token')
      // 跳转到登录页
      setTimeout(() => {
        window.location.href = '/login'
      }, 1500)

      ElMessage({
        message: '登录已过期，请重新登录',
        type: 'error',
        duration: 5 * 1000
      })
    } else {
      ElMessage({
        message: error.message || '请求失败',
        type: 'error',
        duration: 5 * 1000
      })
    }

    return Promise.reject(error)
  }
)

export default service // 导出配置好的 Axios 实例