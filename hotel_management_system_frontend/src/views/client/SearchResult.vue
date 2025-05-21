<template>
  <div>
    <div>共找到 {{ rooms.length }} 个房间</div>
    <div class="room-list">
      <div v-for="room in rooms" :key="room.id" class="room-card" @click="goDetail(room.id)">
        <img :src="room.cover_image" style="width:100%;height:120px;object-fit:cover;" />
        <div>房间号：{{ room.room_number }}</div>
        <div>类型：{{ room.type_name }}</div>
        <div>每日单价：{{ room.price }}</div>
        <div>特点：{{ room.feature }}</div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from '@/api/user'
const route = useRoute()
const router = useRouter()
const rooms = ref([])
onMounted(async () => {
  const res = await axios.get('/api/room-list', { params: route.query })
  rooms.value = res.data
})
function goDetail(id) {
  router.push(`/client/room-detail/${id}`)
}
</script>