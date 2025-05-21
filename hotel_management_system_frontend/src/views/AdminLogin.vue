<!-- filepath: d:\hotel_management_system\hotel_management_system_frontend\src\views\AdminLogin.vue -->
<template>
  <div class="login-bg">
    <div class="bubble-container"></div>
    <div class="box">
      <!-- 左侧滑动盒子 -->
      <div class="pre-box">
        <h1>ADMIN</h1>
        <p>欢迎来到后台管理</p>
        <div class="img-box">
          <img :src="imgSrc" alt="admin" />
        </div>
      </div>
      <!-- 管理员登录表单 -->
      <div class="login-form">
        <div class="title-box">
          <h1>管理员登录</h1>
        </div>
        <div class="input-box">
          <input type="text" placeholder="用户名" v-model="loginForm.username" />
          <input type="password" placeholder="密码" v-model="loginForm.password" />
        </div>
        <div class="btn-box">
          <button @click="handleLogin">登录</button>
        </div>
      </div>
    </div>
    <!-- 右下角按钮 -->
    <button class="switch-user-btn" @click="goUserLogin">用户登录</button>
  </div>
</template>

<script>
import adminImg from '@/assets/admin.jpg' // 你可以换成自己的图片

import { loginApi } from '@/api/admin'

export default {
  name: 'AdminLogin',
  data() {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      bubleTimer: null
    };
  },
  computed: {
    imgSrc() {
      return adminImg
    }
  },
  methods: {
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        alert('用户名和密码不能为空')
        return
      }
      try {
        const res = await loginApi(this.loginForm)
        if (res.token) {
          localStorage.setItem('admin_token', res.token)
        }
        alert(res.message || '登录成功')
        this.$router.push('/admin/user-list')
      } catch (err) {
        alert(err.response?.data?.message || '登录失败')
      }
    },
    goUserLogin() {
      this.$router.push('/')
    },
    bubleCreate() {
      const container = this.$el.querySelector('.bubble-container');
      const buble = document.createElement('span');
      let r = Math.random() * 5 + 25;
      buble.style.width = r + 'px';
      buble.style.height = r + 'px';
      buble.style.left = Math.random() * this.$el.offsetWidth + 'px';
      const scopeId = this.$el.getAttributeNames().find(name => name.startsWith('data-v-'));
      if (scopeId) {
        buble.setAttribute(scopeId, '');
      }
      container.append(buble);
      setTimeout(() => {
        buble.remove();
      }, 4000);
    }
  },
  mounted() {
    this.bubleTimer = setInterval(this.bubleCreate, 200);
  },
  beforeUnmount() {
    clearInterval(this.bubleTimer);
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  width: 100vw;
  overflow-x: hidden;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(to right, rgb(247, 209, 215), rgb(191, 227, 241));
}
.bubble-container {
  position: fixed;
  left: 0;
  top: 0;
  width: 100vw;
  height: 100vh;
  pointer-events: none;
  z-index: 1;
}
.bubble-container span {
  position: absolute;
  z-index: 0;
  bottom: 0;
  border-radius: 50%;
  background: radial-gradient(circle at 72% 28%, #fff 3px, #ff7edf 8%, #5b5b5b, #aad7f9 100%);
  box-shadow: inset 0 0 6px #fff,
    inset 3px 0 6px #eaf5fc,
    inset 2px -2px 10px #efcde6,
    inset 0 0 60px #f9f6de,
    0 0 20px #fff;
  animation: myMove 4s linear infinite;
}
@keyframes myMove {
  0% {
    transform: translateY(0%);
    opacity: 1;
  }
  50% {
    transform: translate(10%, -1000%);
  }
  75% {
    transform: translate(-20%, -1200%);
  }
  99% {
    opacity: .9;
  }
  100% {
    transform: translateY(-1800%) scale(1.5);
    opacity: 0;
  }
}
.box {
  width: 1050px;
  height: 600px;
  display: flex;
  position: relative;
  z-index: 2;
  margin: auto;
  border-radius: 8px;
  border: 1px solid rgba(255, 255, 255, .6);
  box-shadow: 2px 1px 19px rgba(0, 0, 0, .1);
  background: rgba(255,255,255,0.3);
}
.pre-box {
  width: 50%;
  height: 100%;
  border-radius: 4px 0 0 4px;
  background-color: #edd4dc;
  box-shadow: 2px 1px 19px rgba(0, 0, 0, .1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.pre-box h1 {
  text-align: center;
  letter-spacing: 5px;
  color: white;
  user-select: none;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}
.pre-box p {
  height: 30px;
  line-height: 30px;
  text-align: center;
  margin: 20px 0;
  user-select: none;
  font-weight: bold;
  color: white;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}
.img-box {
  width: 220px;
  height: 220px;
  margin: 0 auto;
  border-radius: 50%;
  user-select: none;
  overflow: hidden;
  box-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}
.img-box img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.login-form {
  flex: 1;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.title-box {
  height: 100px;
  line-height: 100px;
}
.title-box h1 {
  text-align: center;
  color: white;
  user-select: none;
  letter-spacing: 5px;
  text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}
.input-box {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.input-box input {
  width: 60%;
  height: 40px;
  margin-bottom: 20px;
  padding: 0 10px;
  border: 1px solid #fff;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 120px;
  backdrop-filter: blur(10px);
  outline: none;
}
.input-box input:focus {
  color: #442fc7;
}
.input-box input::placeholder {
  color: #bdbdbd;
}
.btn-box {
  display: flex;
  justify-content: center;
}
.btn-box button {
  width: 100px;
  height: 30px;
  margin: 0 7px;
  line-height: 30px;
  border: none;
  border-radius: 4px;
  background-color: #69b3f0;
  color: white;
}
.btn-box button:hover {
  cursor: pointer;
  opacity: .8;
}
.switch-user-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 100;
  background: #69b3f0;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 12px 32px; /* 和 .switch-admin-btn 一样 */
  font-size: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: background .2s;
  white-space: nowrap;
}
.switch-user-btn:hover {
  background: #3a8ee6;
  cursor: pointer;
}
</style>