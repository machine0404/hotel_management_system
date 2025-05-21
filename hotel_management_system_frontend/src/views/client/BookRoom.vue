<template>
  <el-card>
    <el-form :model="form">
      <el-form-item label="客房类型">
        <el-select v-model="form.type_id" placeholder="请选择客房类型" @change="onTypeChange">
          <el-option
            v-for="type in roomTypes"
            :key="type.id"
            :label="type.name"
            :value="type.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="入住时间">
        <el-date-picker v-model="form.checkin" type="date" />
      </el-form-item>
      <el-form-item label="离店时间">
        <el-date-picker v-model="form.checkout" type="date" />
      </el-form-item>
      <el-form-item label="房间号">
        <el-select v-model="form.room_id" placeholder="请先选择类型和时间" :disabled="roomList.length === 0">
          <el-option
            v-for="room in roomList"
            :key="room.id"
            :label="room.room_number"
            :value="room.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="入住人数">
        <div style="display: flex; align-items: center;">
          <el-button @click="decrease" :disabled="form.people <= 1" size="small">-</el-button>
          <span style="margin: 0 12px;">{{ form.people }}</span>
          <el-button @click="increase" :disabled="form.people >= maxPeople" size="small">+</el-button>
          <span style="margin-left: 8px;">(最大{{ maxPeople }}人)</span>
        </div>
      </el-form-item>

      <el-form-item label="是否需要发票">
        <el-radio-group v-model="form.need_invoice">
          <el-radio :label="true">需要</el-radio>
          <el-radio :label="false">不需要</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="价格">
        <span v-if="selectedRoom && form.checkin && form.checkout">
          {{ totalPrice }} 元
        </span>
        <span v-else></span>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" v-if="!canBook" @click="searchRooms">搜索</el-button>
        <el-button type="success" v-else @click="bookRoom">预定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from '@/api/user'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'

const form = ref({
  type_id: '',
  checkin: '',
  checkout: '',
  room_id: '',
  people: 1,
  need_invoice: false
})
const roomTypes = ref([])
const roomList = ref([])
const canBook = ref(false)

onMounted(async () => {
  const res = await axios.get('/api/user/room-type-list')
  roomTypes.value = res.data
})

const selectedRoom = computed(() => {
  return roomList.value.find(r => r.id === form.value.room_id) || null
})

const maxPeople = computed(() => {
  return selectedRoom.value ? selectedRoom.value.max_people : 1
})

watch(() => form.value.room_id, () => {
  if (form.value.people > maxPeople.value) {
    form.value.people = maxPeople.value
  }
})

const days = computed(() => {
  if (!form.value.checkin || !form.value.checkout) return 0
  const start = new Date(form.value.checkin)
  const end = new Date(form.value.checkout)
  const diff = (end - start) / (1000 * 60 * 60 * 24)
  return diff > 0 ? diff : 0
})

const totalPrice = computed(() => {
  if (!selectedRoom.value || days.value === 0) return 0
  return days.value * selectedRoom.value.price
})

async function searchRooms() {
  if (!form.value.type_id || !form.value.checkin || !form.value.checkout) {
    ElMessage.warning('请先选择类型和时间')
    return
  }
  const res = await axios.get('/api/user/room-list', {
    params: {
      check_in_date: form.value.checkin,
      check_out_date: form.value.checkout
    }
  })
  roomList.value = res.data.filter(r => r.type_id == form.value.type_id)
  if (roomList.value.length === 0) {
    form.value.room_id = ''
    canBook.value = false
    ElMessage.error('没有符合条件的房间')
  } else {
    form.value.room_id = roomList.value[0].id
    canBook.value = true
    ElMessage.success('找到可用房间，请选择房间号并填写入住人数')
  }
}

function decrease() {
  if (form.value.people > 1) form.value.people--
}
function increase() {
  if (form.value.people < maxPeople.value) form.value.people++
}

function formatDate(val) {
  if (!val) return ''
  // 兼容Date对象和字符串
  if (typeof val === 'string') return val.slice(0, 10)
  return dayjs(val).format('YYYY-MM-DD')
}

async function bookRoom() {
  if (!form.value.room_id || !form.value.checkin || !form.value.checkout) {
    ElMessage.warning('请先选择房间和时间')
    return
  }
  const res = await axios.post('/api/user/book-room', {
    room_id: form.value.room_id,
    checkin: formatDate(form.value.checkin),
    checkout: formatDate(form.value.checkout),
    people: form.value.people,
    need_invoice: form.value.need_invoice
  })
  if (res.data.success) {
    ElMessage.success('预定成功')
    // 可跳转或清空表单
  } else {
    ElMessage.error(res.data.message || '预定失败')
  }
}
</script>