import axios from 'axios'

export function loginApi(data) {
  return axios.post('/api/admin/login', data).then(res => res.data)
}