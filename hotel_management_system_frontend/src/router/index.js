// src/router/index.js
import { createRouter, createWebHistory } from 'vue-router'
import LoginRegister from '../views/LoginRegister.vue'
import FindRoom from '../views/findroom.vue' // 新增
import AdminLogin from '../views/AdminLogin.vue'

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
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
