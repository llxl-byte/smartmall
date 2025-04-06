import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '../layout/BasicLayout.vue' // 导入基础布局组件

const routes = [
  {
    path: '/', // 根路径指向布局
    component: BasicLayout, // 使用 BasicLayout 作为顶层组件
    children: [ // 定义子路由
      {
        path: '', // 当访问 '/' 时，默认显示 Dashboard
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: '/users', // 路径为 /users
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: '/items', // 路径为 /items
        name: 'ItemManagement',
        component: () => import('../views/ItemManagement.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: '/orders', // 路径为 /orders
        name: 'OrderManagement',
        component: () => import('../views/OrderManagement.vue'),
        meta: { title: '订单管理' }
      }
      // 更多子路由...
    ]
  },
  // 添加登录页面的顶层路由
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'), // 懒加载登录组件
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(), // 使用 HTML5 History 模式
  routes, // 上面定义的路由规则数组
})

// 可以在这里添加路由守卫 (navigation guards) 来处理登录验证、权限控制等
// router.beforeEach((to, from, next) => { ... })

export default router // 导出路由器实例