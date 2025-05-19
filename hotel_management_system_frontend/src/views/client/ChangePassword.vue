<template>
  <el-card>
    <el-form :model="form" label-width="100px" style="max-width: 400px;">
      <el-form-item label="原密码">
        <el-input v-model="form.oldPassword" :type="showOld ? 'text' : 'password'" autocomplete="off">
          <template #suffix>
            <el-icon @click="showOld = !showOld" style="cursor:pointer;">
              <component :is="showOld ? View : Hide" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="新密码">
        <el-input v-model="form.newPassword" :type="showNew ? 'text' : 'password'" autocomplete="off">
          <template #suffix>
            <el-icon @click="showNew = !showNew" style="cursor:pointer;">
              <component :is="showNew ? View : Hide" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item label="确认新密码">
        <el-input v-model="form.confirmPassword" :type="showConfirm ? 'text' : 'password'" autocomplete="off">
          <template #suffix>
            <el-icon @click="showConfirm = !showConfirm" style="cursor:pointer;">
              <component :is="showConfirm ? View : Hide" />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="changePassword">修改密码</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { View, Hide } from '@element-plus/icons-vue'

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const showOld = ref(false)
const showNew = ref(false)
const showConfirm = ref(false)

async function changePassword() {
  if (!form.value.oldPassword || !form.value.newPassword || !form.value.confirmPassword) {
    alert('请填写完整')
    return
  }
  if (form.value.newPassword !== form.value.confirmPassword) {
    alert('两次新密码不一致')
    return
  }
  const res = await axios.put('/api/user/change-password', {
    oldPassword: form.value.oldPassword,
    newPassword: form.value.newPassword
  })
  if (res.data.success) {
    alert('修改成功')
    form.value.oldPassword = ''
    form.value.newPassword = ''
    form.value.confirmPassword = ''
  } else {
    alert(res.data.message || '修改失败')
  }
}
</script>