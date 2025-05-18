<template>
  <el-card>
    <el-input v-model="queryUserId" placeholder="请输入用户ID" style="width: 200px" />
    <el-button @click="queryUser">查询</el-button>
    <el-alert v-if="queryFail" title="未查询到用户" type="error" show-icon />
    <el-form v-if="user" :model="user" label-width="100px" style="margin-top: 20px;">
      <el-form-item label="用户名"><el-input v-model="user.username" /></el-form-item>
      <el-form-item label="密码"><el-input v-model="user.password" /></el-form-item>
      <el-form-item label="积分"><el-input v-model="user.points" /></el-form-item>
      <el-form-item label="手机号码"><el-input v-model="user.phone" /></el-form-item>
      <el-form-item label="邮箱"><el-input v-model="user.email" /></el-form-item>
      <el-form-item label="性别">
        <el-select v-model="user.gender">
          <el-option label="女" :value="0" />
          <el-option label="男" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="账号状态">
        <el-select v-model="user.status">
          <el-option label="未消费" :value="0" />
          <el-option label="已消费" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :disabled="!user" @click="updateUser">确认修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
const queryUserId = ref('')
const user = ref(null)
const queryFail = ref(false)
async function queryUser() {
  try {
    const res = await axios.get(`/api/admin/user/${queryUserId.value}`)
    user.value = res.data
    queryFail.value = !user.value
  } catch {
    user.value = null
    queryFail.value = true
  }
}
async function updateUser() {
  if (!user.value) return
  const res = await axios.put(`/api/admin/user/${user.value.id}`, user.value)
  if (res.data.success) alert('修改成功')
  else alert('修改失败')
}
</script>