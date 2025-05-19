<template>
  <div class="room-list">
    <div
      class="room-card"
      v-for="room in rooms"
      :key="room.id"
      @click="goDetail(room.id)"
    >
      <img :src="fixImg(room.cover_image)" class="room-img" />
      <div class="room-info">
        <div>{{ room.room_number }} - {{ room.type_name }}</div>
        <div>￥{{ room.price }}/晚</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const rooms = ref([])
const router = useRouter()

onMounted(async () => {
  const res = await axios.get('/api/user/room-list')
  rooms.value = res.data
})

function goDetail(id) {
  router.push(`/client/room-detail/${id}`)
}

function fixImg(path) {
  if (!path) return '/img/default.jpg'
  return path.replace(/^\.\//, '/')
}
</script>

<style scoped>
.room-list {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: center;
  margin: 30px 0;
}
.room-card {
  width: 260px;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
  background: #fff;
  cursor: pointer;
  transition: box-shadow .2s;
  overflow: hidden;
}
.room-card:hover {
  box-shadow: 0 4px 16px #ccc;
}
.room-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
}
.room-info {
  padding: 12px;
  font-size: 16px;
  color: #333;
  display: flex;
  justify-content: space-between;
}
</style>