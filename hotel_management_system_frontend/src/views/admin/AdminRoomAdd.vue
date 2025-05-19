<template>
  <el-card>
    <el-form :model="room" label-width="100px">
      <el-form-item label="房间号">
        <el-input v-model="room.room_number" />
      </el-form-item>
      <el-form-item label="最大容纳人数">
        <el-input v-model="room.max_people" />
      </el-form-item>
      <el-form-item label="房间类型">
        <el-select v-model="room.type_id" placeholder="请选择房间类型">
          <el-option
            v-for="type in roomTypes"
            :key="type.id"
            :label="type.name"
            :value="type.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房间状态">
        <el-select v-model="room.status">
          <el-option label="空闲" :value="0" />
          <el-option label="已用" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="客房详情">
        <el-checkbox-group v-model="room.detail">
          <el-checkbox label="电脑" />
          <el-checkbox label="热水" />
          <el-checkbox label="WIFI" />
          <el-checkbox label="电视" />
          <el-checkbox label="早餐" />
        </el-checkbox-group>
        <span style="margin-left: 20px;">
          面积:
          <el-input v-model="room.area" style="width: 80px; display: inline-block;" size="small" />
          <span style="margin-left: 4px;">m²</span>
        </span>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="addRoom">确认添加</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const room = ref({
  room_number: '',
  type_id: '',
  status: '',
  max_people: '',
  area: '',
  detail: []
})
const roomTypes = ref([])

onMounted(async () => {
  const res = await axios.get('/api/admin/room-type-list')
  roomTypes.value = res.data
})

function buildIntroduce() {
  // 构建introduce字符串
  let arr = []
  arr.push(`面积:${room.value.area}m²`)
  arr.push(`容纳:${room.value.max_people}人`)
  arr.push(`电脑:${room.value.detail.includes('电脑') ? '有' : '无'}`)
  arr.push(`热水:${room.value.detail.includes('热水') ? '有' : '无'}`)
  arr.push(`WIFI:${room.value.detail.includes('WIFI') ? '有' : '无'}`)
  arr.push(`电视:${room.value.detail.includes('电视') ? '有' : '无'}`)
  arr.push(`早餐:${room.value.detail.includes('早餐') ? '有' : '无'}`)
  return arr.join(',')
}
async function addRoom() {
  if (!room.value.area) {
    alert('请填写面积')
    return
  }
  const payload = {
    room_number: room.value.room_number,
    type_id: room.value.type_id,
    status: room.value.status,
    max_people: room.value.max_people,
    introduce: buildIntroduce()
  }
  const res = await axios.post('/api/admin/room-add', payload)
  if (res.data.success) {
    alert('添加成功')
    room.value = { room_number: '', type_id: '', status: '', max_people: '', area: '', detail: [] }
  } else {
    alert('添加失败')
  }
}
</script>