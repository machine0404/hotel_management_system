<template>
  <div class="login-bg">
    <div class="bubble-container"></div>
    <div class="box">
        <!-- 滑动盒子 -->
        <div class="pre-box" :style="preBoxStyle">
            <h1>WELCOME</h1>
            <p>JOIN US!</p>
            <div class="img-box">
                <img :src="imgSrc" alt="">
            </div>
        </div>
        <!-- 注册盒子 -->
        <div class="register-form">
          <div class="title-box">
            <h1>注册</h1>
          </div>
          <div class="input-box">
            <input type="text" placeholder="用户名" v-model="registerForm.username">
            <input type="password" placeholder="密码" v-model="registerForm.password">
            <input type="password" placeholder="确认密码" v-model="registerForm.confirmPassword">
          </div>
          <div class="btn-box">
            <button @click="handleRegister">注册</button>
            <p @click="mySwitch">已有账号?去登录</p>
          </div>
        </div>
        <!-- 登录盒子 -->
        <div class="login-form">
            <div class="title-box">
                <h1>登录</h1>
            </div>
            <div class="input-box">
                <input type="text" placeholder="用户名" v-model="loginForm.username">
                <input type="password" placeholder="密码" v-model="loginForm.password">
            </div>
            <div class="btn-box">
                <button @click="handleLogin">登录</button>
                <p @click="mySwitch">没有账号?去注册</p>
            </div>
        </div>
    </div>
    <!-- 右下角按钮 -->
    <button class="switch-admin-btn" @click="goAdminLogin">管理员登录</button>
  </div>
</template>

<script>
import waoku from '@/assets/waoku.jpg'
import wuwu from '@/assets/wuwu.jpeg'
import { loginApi, registerApi } from '@/api/user'

export default {
  name: 'App',
  data() {
  return {
    flag: true,
    loginForm: {
      username: '',
      password: ''
    },
    registerForm: {
      username: '',
      password: '',
      confirmPassword: ''
    }
  };
},
  computed: {
    preBoxStyle() {
      return {
        transform: this.flag ? 'translateX(0%)' : 'translateX(100%)',
        backgroundColor: this.flag ? '#edd4dc' : '#c9e0ed'
      }
    },
    imgSrc() {
      return this.flag ? waoku : wuwu
    }
  },
  methods: {
    mySwitch() {
      this.flag = !this.flag;
    },
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        alert('用户名和密码不能为空')
        return
      }
      try {
        const res = await loginApi(this.loginForm)
        // 保存 token 到本地
        if (res.token) {
          localStorage.setItem('token', res.token)
          this.$router.push('/client/find-room') // 登录成功后跳转
        } else {
          alert('用户名或密码错误')
        }
      } catch (err) {
        alert('用户名或密码错误')
      }
    },
    async handleRegister() {
      if (!this.registerForm.username || !this.registerForm.password) {
        alert('用户名和密码不能为空')
        return
      }
      if (this.registerForm.password !== this.registerForm.confirmPassword) {
        alert('两次输入的密码不一致')
        return
      }
      try {
        const res = await registerApi({
          username: this.registerForm.username,
          password: this.registerForm.password
        })
        alert(res.message || '注册成功')
        this.mySwitch() // 注册成功后切换到登录
      } catch (err) {
        alert(err.response?.data?.message || '注册失败')
      }
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
    },
    goAdminLogin() {
      this.$router.push('/admin/login')
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
/* 去除input的轮廓 */
input {
    outline: none;
}

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

/* 最外层的大盒子 */
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
}

/* 滑动的盒子 */
.pre-box {
    width: 50%;
    height: 100%;
    position: absolute;
    left: 0;
    top: 0;
    z-index: 99;
    border-radius: 4px;
    background-color: #edd4dc;
    box-shadow: 2px 1px 19px rgba(0, 0, 0, .1);
    transition: 0.5s ease-in-out;
}

/* 滑动盒子的标题 */
.pre-box h1 {
    margin-top: 150px;
    text-align: center;
    letter-spacing: 5px;
    color: white;
    user-select: none;
    text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 滑动盒子的文字 */
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

/* 图片盒子 */
.img-box {
    width: 200px;
    height: 200px;
    margin: 20px auto;
    border-radius: 50%;
    user-select: none;
    overflow: hidden;
    box-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 图片 */
.img-box img {
    width: 100%;
    transition: 0.5s;
}

/* 登录和注册盒子 */
.login-form,
.register-form {
    flex: 1;
    height: 100%;
}

/* 标题盒子 */
.title-box {
    height: 300px;
    line-height: 500px;
}

/* 标题 */
.title-box h1 {
    text-align: center;
    color: white;
    user-select: none;
    letter-spacing: 5px;
    text-shadow: 4px 4px 3px rgba(0, 0, 0, .1);
}

/* 输入框盒子 */
.input-box {
    display: flex;
    flex-direction: column;
    align-items: center;
}

/* 输入框 */
input {
    width: 60%;
    height: 40px;
    margin-bottom: 20px;
    text-indent: 10px;
    border: 1px solid #fff;
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 120px;
    backdrop-filter: blur(10px);
}

input:focus {
    color: #442fc7;
}

input:focus::placeholder {
    opacity: 0;
}

input::placeholder {
    color: #bdbdbd;
}

/* 按钮盒子 */
.btn-box {
    display: flex;
    justify-content: center;
}

/* 按钮 */
button {
    width: 100px;
    height: 30px;
    margin: 0 7px;
    line-height: 30px;
    border: none;
    border-radius: 4px;
    background-color: #69b3f0;
    color: white;
}

/* 按钮悬停时 */
button:hover {
    cursor: pointer;
    opacity: .8;
}

/* 按钮文字 */
.btn-box p {
    height: 30px;
    line-height: 30px;
    user-select: none;
    font-size: 14px;
    color: white;
}

.btn-box p:hover {
    cursor: pointer;
    border-bottom: 1px solid white;
}

/* 管理员登录按钮 */
.switch-admin-btn {
  position: fixed;
  right: 40px;
  bottom: 40px;
  z-index: 100;
  background: #69b3f0;
  color: #fff;
  border: none;
  border-radius: 6px;
  padding: 10px 22px;         /* 缩小内边距 */
  font-size: 14px;            /* 字体变小 */
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  transition: background .2s;
  white-space: nowrap;
  display: flex;              /* 新增：flex布局 */
  align-items: center;        /* 垂直居中 */
  justify-content: center;    /* 水平居中 */
}

.switch-admin-btn:hover {
  background: #3a8ee6;
  cursor: pointer;
}
</style>
