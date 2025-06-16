<template>
  <el-container class="client-layout">
    <el-header class="header-container">
      <div class="logo">酒店管理系统</div>
      <el-menu 
        mode="horizontal" 
        :default-active="$route.path" 
        router
        class="main-menu"
        background-color="transparent"
        text-color="#2c3e50"
        active-text-color="#409EFF">
        <el-menu-item index="/client/find-room">
          <el-icon><Search /></el-icon>
          找房
        </el-menu-item>
        <el-menu-item index="/client/book-room">
          <el-icon><Calendar /></el-icon>
          订房
        </el-menu-item>
        <el-menu-item index="/client/user-center">
          <el-icon><User /></el-icon>
          个人中心
        </el-menu-item>
        <el-menu-item index="/logout" @click="logout" class="logout-btn">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-menu-item>
      </el-menu>
    </el-header>
    <el-main class="main-container">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Search, Calendar, User, SwitchButton } from '@element-plus/icons-vue'

const router = useRouter()

function logout() {
  localStorage.removeItem('token')
  router.push('/')
}
</script>

<style scoped>
.client-layout {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header-container {
  background: linear-gradient(135deg, #ffffff 0%, #f5f7fa 100%);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 0;
  position: fixed;
  width: 100%;
  z-index: 1000;
  display: flex;
  align-items: center;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  color: #409EFF;
  margin: 0 2rem;
  white-space: nowrap;
}

.main-menu {
  flex-grow: 1;
  border: none;
}

.main-menu :deep(.el-menu-item) {
  height: 60px;
  line-height: 60px;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.main-menu :deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.1);
}

.main-menu :deep(.el-menu-item.is-active) {
  font-weight: bold;
  background-color: rgba(64, 158, 255, 0.15);
}

.logout-btn {
  float: right;
  color: #f56c6c !important;
}

.logout-btn:hover {
  background-color: rgba(245, 108, 108, 0.1) !important;
}

.main-container {
  padding-top: 80px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: calc(100vh - 80px);
  padding-left: 24px;
  padding-right: 24px;
  padding-bottom: 24px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .logo {
    margin: 0 1rem;
    font-size: 1.2rem;
  }
  
  .main-menu :deep(.el-menu-item) {
    padding: 0 10px;
  }
}
</style>