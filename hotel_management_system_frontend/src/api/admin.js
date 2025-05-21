import axios from 'axios'

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
}, error => Promise.reject(error))

export default axios

export function loginApi(data) {
  return axios.post('/api/admin/login', data).then(res => res.data)
}