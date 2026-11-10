<script setup>
// 自动导入模式下没有 app.use(ElementPlus)，用 ConfigProvider 提供中文 locale
import zhCn from 'element-plus/es/locale/lang/zh-cn'
</script>

<template>
  <el-config-provider :locale="zhCn">
    <router-view />
  </el-config-provider>
</template>
