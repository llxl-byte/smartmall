import { createApp } from 'vue'
import ElementPlus from 'element-plus' // 导入 Element Plus
import 'element-plus/dist/index.css' // 导入 Element Plus 样式
import router from './router' // 导入我们创建的 router 实例
import './style.css'
import App from './App.vue'

const app = createApp(App) // 创建 Vue 应用实例

app.use(ElementPlus) // 注册 Element Plus 插件
app.use(router) // 注册路由插件

app.mount('#app') // 挂载应用
