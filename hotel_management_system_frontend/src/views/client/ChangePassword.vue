<template>
  <div class="change-password">
    <el-form :model="form" label-width="120px" class="password-form">
      <div class="form-content">
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
          <el-button type="primary" @click="changePassword" class="submit-btn">修改密码</el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from '@/api/user'
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
    old_password: form.value.oldPassword,
    new_password: form.value.newPassword
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

<style scoped>
.change-password {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.password-form {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.form-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.form-content :deep(.el-form-item) {
  margin-bottom: 35px;
}

.form-content :deep(.el-form-item:last-child) {
  margin-bottom: 0;
  text-align: center;
  margin-top: 50px;
}

.password-form :deep(.el-input__wrapper) {
  padding: 6px 20px;
}

.password-form :deep(.el-input__inner) {
  height: 45px;
  font-size: 16px;
}

.password-form :deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
  padding-right: 20px;
}

.submit-btn {
  width: 180px;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
}

/* 图标样式 */
.password-form :deep(.el-input__suffix) {
  color: #909399;
  transition: color 0.3s;
  font-size: 18px;
  padding: 0 5px;
}

.password-form :deep(.el-input__suffix):hover {
  color: #409EFF;
}
</style>