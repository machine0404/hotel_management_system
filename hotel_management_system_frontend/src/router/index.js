// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import ClientLayout from '../views/client/ClientLayout.vue'
import FindRoom from '../views/client/FindRoom.vue'
import RoomDetail from '../views/client/RoomDetail.vue'
import BookRoom from '../views/client/BookRoom.vue'
import UserCenter from '../views/client/UserCenter.vue'
import MyOrders from '../views/client/MyOrders.vue'
import MyInfo from '../views/client/MyInfo.vue'
import ChangePassword from '../views/client/ChangePassword.vue'
import SearchResult from '../views/client/SearchResult.vue'
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
    path: '/client',
    component: ClientLayout,
    children: [
      { path: '', redirect: '/client/find-room' },
      { path: 'find-room', component: FindRoom },
      { path: 'room-detail/:id', component: RoomDetail },
      { path: 'book-room', component: BookRoom },
      { path: 'user-center', component: UserCenter },
      { path: 'my-orders', component: MyOrders },
      { path: 'my-info', component: MyInfo },
      { path: 'change-password', component: ChangePassword },
      { path: 'search-result', component: SearchResult }
    ]
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
  },
  {
    path: '/client/room-detail/:id',
    component: () => import('@/views/client/RoomDetail.vue')
  },
  {
    path: '/client/my-info',
    component: () => import('@/views/client/MyInfo.vue')
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
  if (to.path.startsWith('/client')) {
    const token = localStorage.getItem('token')
    if (!token) return next('/')
  }
  next()
})

export default router
