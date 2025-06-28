<script setup>
// App.vue 通常作为根组件，用于承载路由视图
</script>

<template>
  <router-view /> <!-- 这里会显示当前路由匹配到的组件 -->
</template>

<style>
/* 这里可以放一些全局的基础样式，或者在 src/style.css 中管理 */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  /* 可以根据需要设置一些全局布局样式，例如最小高度等 */
  min-height: 100vh;
}
</style>
