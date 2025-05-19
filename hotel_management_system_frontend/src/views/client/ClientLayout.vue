<template>
  <el-container>
    <el-header>
      <el-menu mode="horizontal" :default-active="$route.path" router>
        <el-menu-item index="/client/find-room">找房</el-menu-item>
        <el-menu-item index="/client/book-room">订房</el-menu-item>
        <el-menu-item index="/client/user-center">个人中心</el-menu-item>
        <el-menu-item index="/logout" @click="logout" style="float:right;color:#f56c6c;">退出登录</el-menu-item>
      </el-menu>
    </el-header>
    <el-main>
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
function logout() {
  localStorage.removeItem('token')
  router.push('/')
}
</script>