<template>
  <el-container class="basic-layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo-area">后台管理</div>
      <el-menu
        default-active="/"
        class="el-menu-vertical-demo"
        router
      >
        <el-menu-item index="/">
          <el-icon><icon-menu /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-sub-menu index="/management">
           <template #title>
             <el-icon><location /></el-icon>
             <span>系统管理</span>
           </template>
           <el-menu-item index="/users">用户管理</el-menu-item>
           <el-menu-item index="/items">商品管理</el-menu-item>
           <el-menu-item index="/orders">订单管理</el-menu-item>
        </el-sub-menu>
        <!-- 更多菜单项 -->
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div>面包屑导航 / 其他信息</div>
        <div>用户头像 / 下拉菜单</div>
      </el-header>
      <el-main class="main-content">
        <router-view /> <!-- 子路由对应的页面组件将在这里显示 -->
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 导入 Element Plus 图标 (如果需要的话，但通常全局注册更方便)
// import { Menu as IconMenu, Location } from '@element-plus/icons-vue'

// 这里可以添加布局相关的逻辑，例如处理菜单折叠等
</script>

<style scoped>
.basic-layout-container {
  height: 100vh; /* 让布局容器占满整个视口高度 */
}

.aside {
  background-color: #545c64; /* 侧边栏背景色 */
  color: #fff;
  overflow-x: hidden; /* 防止水平滚动条 */
}

.logo-area {
  height: 60px; /* 与 Header 高度一致 */
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  background-color: #434a50; /* Logo 区域背景色稍深 */
}

.el-menu {
  border-right: none; /* 去掉菜单默认的右边框 */
  background-color: #545c64; /* 菜单背景色 */
}
.el-menu-item, .el-sub-menu__title {
  color: #fff; /* 菜单文字颜色 */
}
.el-menu-item:hover, .el-sub-menu__title:hover {
  background-color: #434a50; /* 鼠标悬停背景色 */
}
.el-menu-item.is-active {
  color: #ffd04b; /* 选中项文字颜色 */
  background-color: #434a50; /* 选中项背景色 */
}
/* Element Plus v2.2.0+ 需要这样修改子菜单背景色 */
.el-sub-menu .el-menu-item {
    background-color: #3d434a !important; /* 子菜单项背景色 */
    color: #fff !important;
}
.el-sub-menu .el-menu-item:hover {
    background-color: #545c64 !important; /* 子菜单项悬停背景色 */
}
.el-sub-menu .el-menu-item.is-active {
    color: #ffd04b !important; /* 子菜单选中项文字颜色 */
    background-color: #545c64 !important; /* 子菜单选中项背景色 */
}


.header {
  background-color: #fff; /* 顶栏背景色 */
  color: #333;
  line-height: 60px; /* 顶栏高度 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee; /* 顶栏下边框 */
}

.main-content {
  background-color: #f4f4f5; /* 主内容区背景色 */
  padding: 20px; /* 主内容区内边距 */
  height: calc(100vh - 60px); /* 计算主内容区高度 */
  overflow-y: auto; /* 内容超出时显示垂直滚动条 */
}
</style>