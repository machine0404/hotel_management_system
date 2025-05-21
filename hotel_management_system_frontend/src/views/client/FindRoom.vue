<template>
  <div>
    <el-form :inline="true" :model="searchForm" style="margin-bottom: 20px;">
      <el-form-item label="入住时间">
        <el-date-picker v-model="searchForm.check_in_date" type="date" placeholder="选择日期" />
      </el-form-item>
      <el-form-item label="离店时间">
        <el-date-picker v-model="searchForm.check_out_date" type="date" placeholder="选择日期" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">查找</el-button>
      </el-form-item>
    </el-form>
    <div style="margin-bottom: 10px;">
      <span v-if="searched">共查询到 {{ rooms.length }} 个房间</span>
    </div>
    <div class="room-list">
      <div v-for="room in rooms" :key="room.id" class="room-card" @click="goDetail(room.id)">
        <img :src="room.cover_image" style="width:100%;height:120px;object-fit:cover;" />
        <div>房间号：{{ room.room_number }}（{{ room.type_name }}）</div>
        <div>价格：{{ room.price }}</div>
        <div>特点：{{ room.feature }}</div>
        <div>面积：{{ room.description }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/user'
import { useRouter } from 'vue-router'
const router = useRouter()
const searchForm = ref({
  check_in_date: '',
  check_out_date: ''
})
const rooms = ref([])
const searched = ref(false)

async function loadAllRooms() {
  const res = await axios.get('/api/user/room-list')
  rooms.value = res.data
  searched.value = false
}
onMounted(loadAllRooms)

async function search() {
  const params = {}
  if (searchForm.value.check_in_date) params.check_in_date = searchForm.value.check_in_date
  if (searchForm.value.check_out_date) params.check_out_date = searchForm.value.check_out_date
  const res = await axios.get('/api/user/room-list', { params })
  rooms.value = res.data
  searched.value = true
}
function goDetail(id) {
  router.push(`/client/room-detail/${id}`)
}
</script>

<style scoped>
.room-list { display: flex; flex-wrap: wrap; gap: 16px; }
.room-card { width: 220px; border: 1px solid #eee; border-radius: 8px; padding: 8px; cursor: pointer; margin-bottom: 10px; }
.room-card:hover { box-shadow: 0 2px 8px #ccc; }
</style>