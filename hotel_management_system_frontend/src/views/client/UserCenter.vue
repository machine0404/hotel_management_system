<template>
  <el-card>
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