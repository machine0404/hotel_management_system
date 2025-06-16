<template>
  <el-card class="user-center">
    <el-tabs>
      <el-tab-pane label="我的信息"><MyInfo /></el-tab-pane>
      <el-tab-pane label="我的订单"><MyOrders /></el-tab-pane>
      <el-tab-pane label="密码设置"><ChangePassword /></el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import MyInfo from './MyInfo.vue'
import MyOrders from './MyOrders.vue'
import ChangePassword from './ChangePassword.vue'
</script>

<style scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: calc(100vh - 140px);
  background-color: rgba(255, 255, 255, 0.9);
}

.user-center :deep(.el-tabs__header) {
  margin-bottom: 30px;
  padding: 0 20px;
}

.user-center :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.user-center :deep(.el-tabs__nav) {
  font-size: 18px;
}

.user-center :deep(.el-tabs__item) {
  padding: 0 35px;
  height: 56px;
  line-height: 56px;
  transition: all 0.3s;
}

.user-center :deep(.el-tabs__item:hover) {
  color: #409EFF;
}

.user-center :deep(.el-tabs__item.is-active) {
  font-weight: bold;
  font-size: 20px;
}

.user-center :deep(.el-card__body) {
  padding: 0;
}

.user-center :deep(.el-tabs__content) {
  padding: 20px;
}
</style>