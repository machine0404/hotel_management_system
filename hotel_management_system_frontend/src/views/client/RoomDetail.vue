<template>
  <div class="room-detail">
    <!-- 图片 -->
    <img :src="fixImg(room.cover_image)" alt="房间图片" class="room-img" />

    <!-- 房间号  房间类型 -->
    <div class="row">
      <span>房间号：{{ room.room_number }}</span>
      <span style="margin-left: 30px;">房间类型：{{ room.type_name }}</span>
    </div>

    <!-- 面积 最大容纳人数 电脑 热水 WIFI 电视 早餐 -->
    <div class="row">
      <span>面积：{{ area }}㎡</span>
      <span style="margin-left: 30px;">最大容纳人数：{{ room.max_people }}人</span>
      <span style="margin-left: 30px;">电脑：{{ has('电脑') }}</span>
      <span style="margin-left: 30px;">热水：{{ has('热水') }}</span>
      <span style="margin-left: 30px;">WIFI：{{ has('WIFI') }}</span>
      <span style="margin-left: 30px;">电视：{{ has('电视') }}</span>
      <span style="margin-left: 30px;">早餐：{{ has('早餐') }}</span>
    </div>

    <!-- 房间价格 -->
    <div class="row price-row">
      <span>房间价格：<span class="price">{{ room.price }}</span> 元/晚</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const room = ref({})

onMounted(async () => {
  const id = route.params.id
  // 推荐用专门的详情接口
  const res = await axios.get('/api/user/room-list')
  const found = res.data.find(r => r.id == id)
  if (found) {
    room.value = found
  }
})

// 解析introduce中的面积
const area = computed(() => {
  if (!room.value.introduce) return ''
  const match = room.value.introduce.match(/面积[:：]?(\d+)[m㎡²]/)
  return match ? match[1] : ''
})

function fixImg(path) {
  if (!path) return '/img/default.jpg'
  return path.replace(/^\.\//, '/')
}
function has(label) {
  if (!room.value.introduce) return '无'
  return room.value.introduce.includes(label + ':有') ? '有' : '无'
}
</script>

<style scoped>
.room-detail {
  max-width: 700px;
  margin: 30px auto;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px #eee;
  padding: 24px;
}
.room-img {
  width: 100%;
  max-height: 320px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 18px;
}
.row {
  margin-bottom: 16px;
  font-size: 16px;
  color: #333;
}
.price-row {
  font-size: 20px;
  font-weight: bold;
  color: #d0021b;
}
.price {
  font-size: 24px;
  color: #d0021b;
}
</style>