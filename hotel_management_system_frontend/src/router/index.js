// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import FindRoom from '../views/findroom.vue' // 新增
import AdminLogin from '../views/AdminLogin.vue'
import AdminLayout from '../views/admin/AdminLayout.vue'
import AdminUserList from '../views/admin/AdminUserList.vue'
import AdminUserEdit from '../views/admin/AdminUserEdit.vue'
import AdminOrderWait from '../views/admin/AdminOrderWait.vue'
import AdminOrderList from '../views/admin/AdminOrderList.vue'
import AdminRoomList from '../views/admin/AdminRoomList.vue'
import AdminRoomDetail from '../views/admin/AdminRoomDetail.vue'
import AdminRoomAdd from '../views/admin/AdminRoomAdd.vue'
import AdminRoomEdit from '../views/admin/AdminRoomEdit.vue'

const routes = [
  {
    path: '/',
    name: 'LoginRegister',
    component: LoginRegister
  },
  {
    path: '/findroom',
    name: 'FindRoom',
    component: FindRoom
  },
  {
    path: '/admin/login',
    name: 'AdminLogin',
    component: AdminLogin
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: '', redirect: '/admin/user-list' },
      { path: 'user-list', component: AdminUserList },
      { path: 'user-edit', component: AdminUserEdit },
      { path: 'order-wait', component: AdminOrderWait },
      { path: 'order-list', component: AdminOrderList },
      { path: 'room-list', component: AdminRoomList },
      { path: 'room-detail', component: AdminRoomDetail },
      { path: 'room-add', component: AdminRoomAdd },
      { path: 'room-edit', component: AdminRoomEdit }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      return next('/admin/login')
    }
  }
  next()
})

export default router
