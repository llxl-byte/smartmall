import axios from 'axios'
import { ElMessage } from 'element-plus' // 引入 Element Plus 消息提示

// 创建 Axios 实例
const service = axios.create({
  baseURL: 'http://localhost:8083', // API 的基础 URL (根据你的后端服务地址修改)
  timeout: 10000, // 请求超时时间 (毫秒)
  headers: {'Content-Type': 'application/json;charset=utf-8'} // 设置默认请求头
})

// --- 请求拦截器 (可选，后续可添加) ---
// service.interceptors.request.use(
//   config => {
//     // 在发送请求之前做些什么，例如：添加 token
//     // const token = localStorage.getItem('admin-token') // 假设 token 存储在 localStorage
//     // if (token) {
//     //   config.headers['Authorization'] = 'Bearer ' + token
//     // }
//     return config
//   },
//   error => {
//     // 对请求错误做些什么
//     console.error('Request Error:', error) // for debug
//     return Promise.reject(error)
//   }
// )

// --- 响应拦截器 (可选，后续可添加) ---
// service.interceptors.response.use(
//   response => {
//     // 对响应数据做点什么
//     // 如果后端返回的格式是 { code: '200', msg: '...', data: ... }
//     const res = response.data
//     if (res.code !== '200' && res.code !== 200) { // 假设 '200' 或 200 是成功状态码
//       ElMessage({
//         message: res.msg || 'Error',
//         type: 'error',
//         duration: 5 * 1000
//       })
//       // 可以根据不同的 code 做不同的处理，例如 token 过期跳转登录页
//       return Promise.reject(new Error(res.msg || 'Error'))
//     } else {
//       // 成功则直接返回 data 部分 (根据后端实际返回格式调整)
//       return res.data
//     }
//   },
//   error => {
//     // 对响应错误做点什么
//     console.error('Response Error:', error) // for debug
//     ElMessage({
//       message: error.message,
//       type: 'error',
//       duration: 5 * 1000
//     })
//     return Promise.reject(error)
//   }
// )

export default service // 导出配置好的 Axios 实例