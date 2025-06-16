<template>
  <div class="find-room-container">
    <div class="search-section">
      <h2 class="section-title">查找房间</h2>
      <el-form :model="searchForm" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="入住时间">
              <el-date-picker
                v-model="searchForm.check_in_date"
                type="date"
                placeholder="选择入住日期"
                class="date-picker"
                :disabled-date="disablePastDates"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="离店时间">
              <el-date-picker
                v-model="searchForm.check_out_date"
                type="date"
                placeholder="选择离店日期"
                class="date-picker"
                :disabled-date="disableInvalidDates"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8" class="search-button-col">
            <el-button type="primary" @click="search" :loading="loading" class="search-button">
              <el-icon><Search /></el-icon>
              查找房间
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="results-section" v-if="searched">
      <div class="results-header">
        <h3>搜索结果</h3>
        <span class="results-count">共找到 {{ rooms.length }} 个房间</span>
      </div>
    </div>

    <div class="room-list" v-loading="loading">
      <el-empty v-if="rooms.length === 0 && searched" description="暂无符合条件的房间" />
      <el-row :gutter="20">
        <el-col v-for="room in rooms" :key="room.id" :xs="24" :sm="12" :md="8" :lg="6">
          <div class="room-card" @click="goDetail(room.id)">
            <div class="room-image">
              <img :src="room.cover_image || defaultImage" :alt="room.type_name" />
              <div class="room-price">¥{{ room.price }}/晚</div>
            </div>
            <div class="room-info">
              <h4>{{ room.type_name }} - {{ room.room_number }}号房</h4>
              <div class="room-features">
                <el-tag size="small" effect="plain">{{ room.feature }}</el-tag>
                <el-tag size="small" effect="plain" type="success">{{ room.description }}</el-tag>
              </div>
              <el-button type="primary" class="detail-button" text>
                查看详情
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/user'
import { useRouter } from 'vue-router'
import { Search, ArrowRight } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const searchForm = ref({
  check_in_date: '',
  check_out_date: ''
})
const rooms = ref([])
const searched = ref(false)
const loading = ref(false)
const defaultImage = 'https://via.placeholder.com/300x200?text=Room+Image'

// 禁用过去的日期
const disablePastDates = (date) => {
  return date < new Date(new Date().setHours(0, 0, 0, 0))
}

// 禁用早于入住日期的离店日期
const disableInvalidDates = (date) => {
  if (!searchForm.value.check_in_date) return false
  const checkInDate = new Date(searchForm.value.check_in_date)
  return date < checkInDate
}

async function loadAllRooms() {
  loading.value = true
  try {
    const res = await axios.get('/api/user/room-list')
    rooms.value = res.data
    searched.value = false
  } catch (error) {
    ElMessage.error('加载房间列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadAllRooms)

async function search() {
  if (!searchForm.value.check_in_date || !searchForm.value.check_out_date) {
    ElMessage.warning('请选择入住和离店日期')
    return
  }

  loading.value = true
  try {
    const params = {
      check_in_date: searchForm.value.check_in_date,
      check_out_date: searchForm.value.check_out_date
    }
    const res = await axios.get('/api/user/room-list', { params })
    rooms.value = res.data
    searched.value = true
  } catch (error) {
    ElMessage.error('搜索房间失败')
  } finally {
    loading.value = false
  }
}

function goDetail(id) {
  router.push(`/client/room-detail/${id}`)
}
</script>

<style scoped>
.find-room-container {
  padding: 20px;
}

.search-section {
  background: white;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.section-title {
  margin: 0 0 24px;
  color: #2c3e50;
  font-size: 1.5rem;
}

.search-form {
  width: 100%;
}

.date-picker {
  width: 100%;
}

.search-button-col {
  display: flex;
  align-items: flex-end;
}

.search-button {
  width: 140px;
}

.results-section {
  margin-bottom: 20px;
}

.results-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.results-header h3 {
  margin: 0;
  color: #2c3e50;
}

.results-count {
  color: #606266;
  font-size: 0.9rem;
}

.room-list {
  min-height: 200px;
}

.room-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  margin-bottom: 20px;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.room-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.room-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.room-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.room-price {
  position: absolute;
  bottom: 10px;
  right: 10px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.9rem;
}

.room-info {
  padding: 16px;
}

.room-info h4 {
  margin: 0 0 12px;
  color: #2c3e50;
  font-size: 1.1rem;
}

.room-features {
  margin-bottom: 12px;
}

.room-features .el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.detail-button {
  width: 100%;
  margin-top: 8px;
}

@media (max-width: 768px) {
  .search-button-col {
    margin-top: 16px;
  }
  
  .search-button {
    width: 100%;
  }
  
  .room-image {
    height: 160px;
  }
}
</style>