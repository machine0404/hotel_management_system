<template>
  <div>
    <el-header height="60px" class="admin-header">
      <el-menu mode="horizontal" :default-active="$route.path" router>
        <el-menu-item index="/admin/user-list">用户列表</el-menu-item>
        <el-menu-item index="/admin/user-edit">修改用户</el-menu-item>
        <el-menu-item index="/admin/order-wait">等待列表</el-menu-item>
        <el-menu-item index="/admin/order-list">订单列表</el-menu-item>
        <el-menu-item index="/admin/room-list">房间列表</el-menu-item>
        <el-menu-item index="/admin/room-detail">详情查询</el-menu-item>
        <el-menu-item index="/admin/room-add">添加房间</el-menu-item>
        <el-menu-item index="/admin/room-edit">修改房间</el-menu-item>
      </el-menu>
      <el-button type="danger" class="logout-btn" @click="logout">退出登录</el-button>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('admin_token')
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #69b3f0;
  color: #fff;
  padding: 0 40px;
}
.logout-btn {
  margin-left: 20px;
}
</style>