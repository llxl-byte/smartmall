import { createRouter, createWebHistory } from 'vue-router'
import BasicLayout from '../layout/BasicLayout.vue' // 导入基础布局组件

const routes = [
  {
    path: '/', // 根路径指向布局
    component: BasicLayout, // 使用 BasicLayout 作为顶层组件
    meta: { requiresAuth: true }, // 需要登录才能访问
    children: [ // 定义子路由
      {
        path: '', // 当访问 '/' 时，默认显示 Dashboard
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '仪表盘', requiresAuth: true }
      },
      {
        path: '/users', // 路径为 /users
        name: 'UserManagement',
        component: () => import('../views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: '/items', // 路径为 /items
        name: 'ItemManagement',
        component: () => import('../views/ItemManagement.vue'),
        meta: { title: '商品管理', requiresAuth: true }
      },
      {
        path: '/orders', // 路径为 /orders
        name: 'OrderManagement',
        component: () => import('../views/OrderManagement.vue'),
        meta: { title: '订单管理', requiresAuth: true }
      },
      {
        path: '/combos', // 路径为 /combos
        name: 'ComboManagement',
        component: () => import('../views/ComboManagement.vue'),
        meta: { title: '套餐管理', requiresAuth: true }
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

// 添加路由守卫处理登录验证、权限控制等
router.beforeEach((to, from, next) => {
  // 检查目标路由是否需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 检查用户是否已登录（判断 localStorage 中是否有 token）
    const token = localStorage.getItem('admin-token')
    if (!token) {
      // 如果未登录，重定向到登录页
      next({
        path: '/login',
        // 保存原本要去的路由，以便登录成功后重定向回去
        query: { redirect: to.fullPath }
      })
    } else {
      // 如果已登录，允许导航
      next()
    }
  } else {
    // 如果路由不需要认证，直接允许导航
    next()
  }
})

export default router // 导出路由器实例