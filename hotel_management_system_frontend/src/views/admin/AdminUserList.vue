<template>
  <el-card>
    <el-table :data="users" style="width: 100%">
      <el-table-column prop="id" label="用户ID" width="80" />
      <el-table-column prop="username" label="用户名" width="100" />
      <el-table-column prop="email" label="邮箱" width="140" />
      <el-table-column prop="gender" label="性别" width="60" :formatter="genderFormat" />
      <el-table-column prop="phone" label="手机号码" width="120" />
      <el-table-column prop="points" label="积分" width="80" />
      <el-table-column prop="status" label="状态" width="80" :formatter="statusFormat" />
      <el-table-column prop="create_time" label="创建时间" width="200" :formatter="timeFormat" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="danger" size="small" @click="deleteUser(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const users = ref([])
async function loadUsers() {
  const res = await axios.get('/api/admin/user-list')
  users.value = res.data
}
onMounted(loadUsers)
function genderFormat(row) {
  return row.gender === 1 ? '男' : '女'
}
function statusFormat(row) {
  return row.status === 1 ? '已消费' : '未消费'
}
function timeFormat(row) {
  return row.create_time?.replace('T', ' ').substring(0, 19)
}
async function deleteUser(row) {
  if (confirm('确定要删除该用户吗？')) {
    const res = await axios.delete(`/api/admin/user/${row.id}`)
    if (res.data.success) {
      alert('删除成功')
      loadUsers()
    } else {
      alert('删除失败')
    }
  }
}
</script>