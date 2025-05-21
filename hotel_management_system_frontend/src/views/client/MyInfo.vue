<template>
  <el-card>
    <el-form :model="user" label-width="80px">
      <el-form-item label="用户名">
        <el-input v-model="user.username" disabled />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="user.email" />
      </el-form-item>
      <el-form-item label="手机">
        <el-input v-model="user.phone" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="user.gender">
          <el-option label="女" :value="0" />
          <el-option label="男" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="注册时间">
        <el-input :value="formatDate(user.create_time)" disabled />
      </el-form-item>
      <el-form-item label="积分">
        <el-input v-model="user.points" disabled />
      </el-form-item>
      <el-form-item label="状态">
        <el-input :value="user.status === 1 ? '已消费' : '未消费'" disabled />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="updateInfo">保存修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/user'
import dayjs from 'dayjs'

const user = ref({})
onMounted(async () => {
  const res = await axios.get('/api/user/me')
  user.value = res.data
})

function formatDate(val) {
  if (!val) return ''
  // 统一格式为 "2025-05-18 06:50:14"
  return dayjs(val).format('YYYY-MM-DD HH:mm:ss')
}

async function updateInfo() {
  const payload = {
    email: user.value.email,
    phone: user.value.phone,
    gender: user.value.gender
  }
  const res = await axios.put('/api/user/me', payload)
  if (res.data.success) {
    alert('修改成功')
  } else {
    alert('修改失败')
  }
}
</script>