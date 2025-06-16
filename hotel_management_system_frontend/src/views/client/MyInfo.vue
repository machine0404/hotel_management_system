<template>
  <div class="my-info">
    <el-form :model="user" label-width="120px" class="info-form">
      <div class="form-content">
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
          <el-select v-model="user.gender" class="gender-select">
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
          <el-button type="primary" @click="updateInfo" class="submit-btn">保存修改</el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
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

<style scoped>
.my-info {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
}

.info-form {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
}

.form-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 30px;
  padding: 20px;
}

.form-content :deep(.el-form-item) {
  margin-bottom: 0;
}

.info-form :deep(.el-input__wrapper) {
  padding: 6px 20px;
}

.info-form :deep(.el-input__inner) {
  height: 45px;
  font-size: 16px;
}

.info-form :deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
  padding-right: 20px;
}

.gender-select {
  width: 100%;
}

.gender-select :deep(.el-input__wrapper) {
  padding: 6px 20px;
}

/* 最后一个表单项（按钮）占据整行 */
.form-content :deep(.el-form-item:last-child) {
  grid-column: 1 / -1;
  text-align: center;
  margin-top: 20px;
}

.submit-btn {
  width: 180px;
  height: 45px;
  font-size: 16px;
  font-weight: 500;
}

/* 禁用输入框的特殊样式 */
.info-form :deep(.el-input.is-disabled .el-input__wrapper) {
  background-color: #f5f7fa;
}

.info-form :deep(.el-input.is-disabled .el-input__inner) {
  color: #606266;
}

@media (max-width: 768px) {
  .form-content {
    grid-template-columns: 1fr;
  }
}
</style>