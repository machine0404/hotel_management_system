import axios from 'axios'

// 添加请求拦截器
axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

export function loginApi(data) {
  return axios.post('/api/user/login', data).then(res => res.data)
}
