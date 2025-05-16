import axios from 'axios'

// 模拟用户数据
const mockUserDB = {
  username: 'testuser',
  password: '123456'
}

// 模拟登录接口
export function loginApi(data) {
  return axios.post('http://localhost:8080/api/user/login', data)
    .then(res => {
      if (res.data.code === 0) {
        return res.data
      } else {
        // 模拟后端异常结构
        return Promise.reject({ response: { data: { message: res.data.message } } })
      }
    })
}

// 模拟注册接口
export function registerApi(data) {
  return new Promise((resolve, reject) => {
    setTimeout(() => {
      const { username } = data
      if (username === mockUserDB.username) {
        reject({ response: { data: { message: '用户名已存在' } } })
      } else {
        // 模拟注册成功
        mockUserDB.username = data.username
        mockUserDB.password = data.password
        resolve({ message: '注册成功' })
      }
    }, 1000)
  })
}
