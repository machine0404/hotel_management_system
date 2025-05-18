<template>
  <el-card>
    <el-table :data="rooms" style="width: 100%">
      <el-table-column prop="id" label="房间ID" width="80" />
      <el-table-column prop="room_number" label="房间号" width="100" />
      <el-table-column prop="type_name" label="房间类型" width="100" />
      <el-table-column prop="status" label="房间状态" width="100" :formatter="statusFormat" />
      <el-table-column prop="max_people" label="最大人数" width="100" />
      <el-table-column prop="introduce" label="客房简介" width="200" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="danger" size="small" @click="deleteRoom(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const rooms = ref([])
async function loadRooms() {
  const res = await axios.get('/api/admin/room-list')
  rooms.value = res.data
}
onMounted(loadRooms)
function statusFormat(row) {
  return row.status === 1 ? '已用' : '空闲'
}
async function deleteRoom(row) {
  if (confirm('确定要删除该房间吗？')) {
    const res = await axios.delete(`/api/admin/room/${row.id}`)
    if (res.data.success) {
      alert('删除成功')
      loadRooms()
    } else {
      alert('删除失败')
    }
  }
}
</script>