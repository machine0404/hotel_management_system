<template>
  <el-card>
    <el-input v-model="queryRoomId" placeholder="请输入房间ID" style="width: 200px" />
    <el-button @click="queryRoom">查询</el-button>
    <el-alert v-if="queryFail" title="未查询到房间" type="error" show-icon />
    <el-form v-if="room" :model="room" label-width="100px" style="margin-top: 20px;">
      <el-form-item label="房间ID"><el-input v-model="room.id" disabled /></el-form-item>
      <el-form-item label="最大人数"><el-input v-model="room.max_people" disabled /></el-form-item>
      <el-form-item label="房间号"><el-input v-model="room.room_number" disabled /></el-form-item>
      <el-form-item label="面积"><el-input v-model="room.description" disabled /></el-form-item>
      <el-form-item label="客房详情"><el-input v-model="room.introduce" disabled /></el-form-item>
      <el-form-item label="每日单价"><el-input v-model="room.price" disabled /></el-form-item>
      <el-form-item label="房间类别"><el-input v-model="room.type_name" disabled /></el-form-item>
    </el-form>
  </el-card>
</template>
<script setup>
import { ref } from 'vue'
import axios from 'axios'
const queryRoomId = ref('')
const room = ref(null)
const queryFail = ref(false)
async function queryRoom() {
  try {
    const res = await axios.get(`/api/admin/room/${queryRoomId.value}`)
    room.value = res.data
    queryFail.value = !room.value
  } catch {
    room.value = null
    queryFail.value = true
  }
}
</script>