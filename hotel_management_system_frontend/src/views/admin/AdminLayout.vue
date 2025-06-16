<template>
  <el-container class="admin-layout">
    <el-aside width="250px" class="admin-aside">
      <div class="logo-container">
        <h1 class="logo">酒店管理系统</h1>
        <p class="subtitle">管理员控制台</p>
      </div>
      
      <el-menu
        :default-active="$route.path"
        router
        class="admin-menu"
        background-color="#001529"
        text-color="#fff"
        active-text-color="#409EFF">
        
        <el-sub-menu index="1">
          <template #title>
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="/admin/user-list">
            <el-icon><List /></el-icon>
            用户列表
          </el-menu-item>
          <el-menu-item index="/admin/user-edit">
            <el-icon><Edit /></el-icon>
            修改用户
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="2">
          <template #title>
            <el-icon><Tickets /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="/admin/order-wait">
            <el-icon><Timer /></el-icon>
            等待列表
          </el-menu-item>
          <el-menu-item index="/admin/order-list">
            <el-icon><Document /></el-icon>
            订单列表
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon><House /></el-icon>
            <span>房间管理</span>
          </template>
          <el-menu-item index="/admin/room-list">
            <el-icon><Grid /></el-icon>
            房间列表
          </el-menu-item>
          <el-menu-item index="/admin/room-detail">
            <el-icon><Search /></el-icon>
            详情查询
          </el-menu-item>
          <el-menu-item index="/admin/room-add">
            <el-icon><Plus /></el-icon>
            添加房间
          </el-menu-item>
          <el-menu-item index="/admin/room-edit">
            <el-icon><Setting /></el-icon>
            修改房间
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="admin-header">
        <div class="header-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ getCurrentMenuTitle() }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="admin-dropdown">
              管理员
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="admin-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { 
  User, Edit, List, Tickets, Timer, Document,
  House, Grid, Search, Plus, Setting, CaretBottom,
  SwitchButton
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

function getCurrentMenuTitle() {
  const pathMap = {
    '/admin/user-list': '用户列表',
    '/admin/user-edit': '修改用户',
    '/admin/order-wait': '等待列表',
    '/admin/order-list': '订单列表',
    '/admin/room-list': '房间列表',
    '/admin/room-detail': '详情查询',
    '/admin/room-add': '添加房间',
    '/admin/room-edit': '修改房间'
  }
  return pathMap[route.path] || '首页'
}

function logout() {
  localStorage.removeItem('admin_token')
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.admin-aside {
  background-color: #001529;
  color: white;
  overflow: hidden;
}

.logo-container {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo {
  color: #fff;
  font-size: 1.5rem;
  margin: 0;
  font-weight: 600;
}

.subtitle {
  color: rgba(255, 255, 255, 0.65);
  font-size: 0.9rem;
  margin: 8px 0 0;
}

.admin-menu {
  border-right: none;
}

.admin-menu :deep(.el-sub-menu__title) {
  color: #fff !important;
}

.admin-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65) !important;
}

.admin-menu :deep(.el-menu-item.is-active) {
  background-color: #1890ff !important;
  color: #fff !important;
}

.admin-menu :deep(.el-menu-item:hover) {
  color: #fff !important;
}

.admin-header {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-dropdown {
  cursor: pointer;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.admin-dropdown:hover {
  color: #409EFF;
}

.admin-main {
  background-color: #f0f2f5;
  padding: 24px;
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
  .admin-aside {
    width: 80px !important;
  }
  
  .logo-container {
    padding: 10px;
  }
  
  .logo {
    font-size: 1rem;
  }
  
  .subtitle {
    display: none;
  }
}
</style>