<template>
  <div class="book-room">
    <el-form :model="form" label-width="120px" class="booking-form">
      <div class="form-content">
        <div class="form-section">
          <h2 class="section-title">预订信息</h2>
          <el-form-item label="客房类型">
            <el-select v-model="form.type_id" placeholder="请选择客房类型" @change="onTypeChange" class="full-width">
              <el-option
                v-for="type in roomTypes"
                :key="type.id"
                :label="type.name"
                :value="type.id"
              />
            </el-select>
          </el-form-item>
          <div class="date-group">
            <el-form-item label="入住时间">
              <el-date-picker
                v-model="form.checkin"
                type="date"
                placeholder="选择入住日期"
                :disabled-date="disablePastDates"
                value-format="YYYY-MM-DD"
                class="full-width"
              />
            </el-form-item>
            <el-form-item label="离店时间">
              <el-date-picker v-model="form.checkout" type="date" class="full-width" />
            </el-form-item>
          </div>
          <el-form-item label="房间号">
            <el-select v-model="form.room_id" placeholder="请先选择类型和时间" :disabled="roomList.length === 0" class="full-width">
              <el-option
                v-for="room in roomList"
                :key="room.id"
                :label="room.room_number"
                :value="room.id"
              />
            </el-select>
          </el-form-item>
        </div>

        <div class="form-section">
          <h2 class="section-title">入住详情</h2>
          <el-form-item label="入住人数" class="people-counter">
            <div class="counter-container">
              <el-button @click="decrease" :disabled="form.people <= 1" size="large" class="counter-btn">
                <el-icon><Minus /></el-icon>
              </el-button>
              <span class="people-count">{{ form.people }}</span>
              <el-button @click="increase" :disabled="form.people >= maxPeople" size="large" class="counter-btn">
                <el-icon><Plus /></el-icon>
              </el-button>
              <span class="max-people">(最大{{ maxPeople }}人)</span>
            </div>
          </el-form-item>

          <el-form-item label="是否需要发票" class="invoice-option">
            <el-radio-group v-model="form.need_invoice">
              <el-radio :label="true">需要</el-radio>
              <el-radio :label="false">不需要</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="价格" class="price-display">
            <span v-if="selectedRoom && form.checkin && form.checkout" class="total-price">
              ¥ {{ totalPrice }}
            </span>
            <span v-else class="no-price">请完善预订信息</span>
          </el-form-item>
        </div>

        <div class="form-actions">
          <el-button type="primary" v-if="!canBook" @click="searchRooms" size="large">搜索可用房间</el-button>
          <el-button type="success" v-else @click="bookRoom" size="large">确认预订</el-button>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import axios from '@/api/user'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { Plus, Minus } from '@element-plus/icons-vue'

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
  const start = dayjs(form.value.checkin)
  const end = dayjs(form.value.checkout)
  const diff = end.diff(start, 'day')
  return diff >= 0 ? diff + 1 : 0
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

// 禁用过去的日期
function disablePastDates(date) {
  // 只禁用今天0点之前的日期，今天可以选
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}
</script>

<style scoped>
.book-room {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: calc(100vh - 300px);
}

.booking-form {
  max-width: 1000px;
  margin: 0 auto;
}

.form-content {
  padding: 20px;
}

.form-section {
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 30px;
  margin-bottom: 30px;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 25px 0;
  padding-bottom: 15px;
  border-bottom: 1px solid #EBEEF5;
}

.date-group {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.full-width {
  width: 100%;
}

.counter-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.counter-btn {
  width: 40px;
  height: 40px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.people-count {
  font-size: 18px;
  min-width: 30px;
  text-align: center;
}

.max-people {
  color: #909399;
  margin-left: 10px;
}

.invoice-option {
  margin-top: 25px;
}

.price-display {
  margin-top: 25px;
}

.total-price {
  font-size: 24px;
  color: #f56c6c;
  font-weight: bold;
}

.no-price {
  color: #909399;
  font-size: 16px;
}

.form-actions {
  text-align: center;
  margin-top: 40px;
}

.form-actions .el-button {
  min-width: 180px;
  height: 45px;
  font-size: 16px;
}

:deep(.el-form-item__label) {
  font-size: 16px;
  font-weight: 500;
  color: #606266;
}

:deep(.el-input__inner),
:deep(.el-select__input) {
  height: 45px;
  font-size: 16px;
}

:deep(.el-radio__label) {
  font-size: 16px;
}

@media (max-width: 768px) {
  .form-section {
    padding: 20px;
  }

  .date-group {
    grid-template-columns: 1fr;
  }

  .form-actions .el-button {
    width: 100%;
  }
}
</style>