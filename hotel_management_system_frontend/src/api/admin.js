import axios from 'axios'

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

export function loginApi(data) {
  return axios.post('/api/admin/login', data).then(res => res.data)
}

// 其它管理员相关接口可在这里继续添加