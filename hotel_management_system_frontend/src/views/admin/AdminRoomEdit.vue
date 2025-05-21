<template>
  <el-card>
    <el-input v-model="queryRoomId" placeholder="请输入房间ID" style="width: 200px" />
    <el-button @click="queryRoom">查询</el-button>
    <el-alert v-if="queryFail" title="未查询到房间" type="error" show-icon />
    <el-form v-if="room" :model="room" label-width="100px" style="margin-top: 20px;">
      <el-form-item label="房间号"><el-input v-model="room.room_number" /></el-form-item>
      <el-form-item label="房间类别">
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
      <el-form-item label="最大容纳人数"><el-input v-model="room.max_people" /></el-form-item>
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
        <el-button type="primary" :disabled="!room" @click="updateRoom">确认修改</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/admin'
const queryRoomId = ref('')
const room = ref(null)
const queryFail = ref(false)

// 新增：房间类型列表
const roomTypes = ref([])
onMounted(async () => {
  const res = await axios.get('/api/admin/room-type-list')
  roomTypes.value = res.data
})

// 解析introduce字符串为detail数组和area
function parseIntroduce(introduce) {
  const detail = []
  let area = ''
  if (introduce) {
    const arr = introduce.split(',')
    arr.forEach(item => {
      if (item.startsWith('面积:')) area = item.replace('面积:', '').replace('m²', '')
      if (item.startsWith('电脑:') && item.endsWith('有')) detail.push('电脑')
      if (item.startsWith('热水:') && item.endsWith('有')) detail.push('热水')
      if (item.startsWith('WIFI:') && item.endsWith('有')) detail.push('WIFI')
      if (item.startsWith('电视:') && item.endsWith('有')) detail.push('电视')
      if (item.startsWith('早餐:') && item.endsWith('有')) detail.push('早餐')
    })
  }
  return { detail, area }
}

async function queryRoom() {
  try {
    const res = await axios.get(`/api/admin/room/${queryRoomId.value}`)
    if (res.data && res.data.data) {
      const roomData = res.data.data
      const { detail, area } = parseIntroduce(roomData.introduce)
      room.value = {
        id: roomData.id,
        room_number: roomData.room_number,
        type_id: roomData.type_id,
        status: roomData.status,
        max_people: roomData.max_people,
        detail,
        area,
        introduce: roomData.introduce
      }
      queryFail.value = false
    } else {
      room.value = null
      queryFail.value = true
    }
  } catch {
    room.value = null
    queryFail.value = true
  }
}

function buildIntroduce() {
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

async function updateRoom() {
  if (!room.value) return
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
  const res = await axios.put(`/api/admin/room/${room.value.id}`, payload)
  if (res.data.success) {
    alert('修改成功')
  } else {
    alert('修改失败')
  }
}
</script>