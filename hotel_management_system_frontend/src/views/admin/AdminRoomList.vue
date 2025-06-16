<template>
  <div class="room-list-container">
    <div class="page-header">
      <div class="header-left">
        <h2 class="page-title">房间管理</h2>
        <el-tag type="info" class="room-count">共 {{ rooms.length }} 个房间</el-tag>
      </div>
      <div class="header-right">
        <el-input
          v-model="searchQuery"
          placeholder="搜索房间号或类型"
          prefix-icon="Search"
          clearable
          @clear="handleSearchClear"
          class="search-input"
        />
        <el-button type="primary" @click="handleAddRoom">
          <el-icon><Plus /></el-icon>
          添加房间
        </el-button>
      </div>
    </div>

    <el-card class="room-table-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <el-radio-group v-model="statusFilter" @change="handleStatusChange">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0">空闲</el-radio-button>
            <el-radio-button label="1">已用</el-radio-button>
          </el-radio-group>
        </div>
      </template>

      <el-table 
        :data="filteredRooms" 
        style="width: 100%"
        :header-cell-style="{ background: '#f5f7fa' }"
        @row-click="handleRowClick">
        <el-table-column prop="room_number" label="房间号" width="100">
          <template #default="{ row }">
            <span class="room-number">{{ row.room_number }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type_name" label="房间类型" width="120">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type_name)">{{ row.type_name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'danger' : 'success'" effect="dark">
              {{ statusFormat(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="max_people" label="最大人数" width="100">
          <template #default="{ row }">
            <el-badge :value="row.max_people" type="info" />
          </template>
        </el-table-column>
        <el-table-column prop="introduce" label="客房简介" min-width="200">
          <template #default="{ row }">
            <span>{{ row.introduce }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="danger" size="small" @click.stop="handleDelete(row)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="table-footer" v-if="filteredRooms.length === 0">
        <el-empty description="暂无房间数据" />
      </div>
    </el-card>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="删除确认"
      width="30%"
      :close-on-click-modal="false">
      <span>确定要删除房间 {{ selectedRoom?.room_number }} 吗？此操作不可恢复。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete" :loading="deleteLoading">
            确定删除
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete, Plus, Search } from '@element-plus/icons-vue'

const router = useRouter()
const rooms = ref([])
const loading = ref(false)
const deleteLoading = ref(false)
const deleteDialogVisible = ref(false)
const selectedRoom = ref(null)
const searchQuery = ref('')
const statusFilter = ref('')

// 根据房间类型返回不同的标签类型
function getTypeTagType(type) {
  const typeMap = {
    '标准间': '',
    '豪华间': 'success',
    '套房': 'warning',
    '总统套房': 'danger'
  }
  return typeMap[type] || 'info'
}

// 过滤后的房间列表
const filteredRooms = computed(() => {
  let result = rooms.value
  
  // 状态过滤
  if (statusFilter.value !== '') {
    result = result.filter(room => room.status.toString() === statusFilter.value)
  }
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(room => 
      room.room_number.toString().includes(query) ||
      room.type_name.toLowerCase().includes(query)
    )
  }
  
  return result
})

// 加载房间列表
async function loadRooms() {
  loading.value = true
  try {
    const res = await axios.get('/api/admin/room-list')
    rooms.value = res.data
  } catch (error) {
    ElMessage.error('加载房间列表失败')
  } finally {
    loading.value = false
  }
}

// 状态格式化
function statusFormat(row) {
  return row.status === 1 ? '已用' : '空闲'
}

// 处理删除操作
function handleDelete(row) {
  selectedRoom.value = row
  deleteDialogVisible.value = true
}

// 确认删除
async function confirmDelete() {
  if (!selectedRoom.value) return
  
  deleteLoading.value = true
  try {
    const res = await axios.delete(`/api/admin/room/${selectedRoom.value.id}`)
    if (res.data.success) {
      ElMessage.success('删除成功')
      await loadRooms()
      deleteDialogVisible.value = false
    } else {
      ElMessage.error('删除失败')
    }
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  } finally {
    deleteLoading.value = false
  }
}

// 处理编辑操作
function handleEdit(row) {
  router.push(`/admin/room-edit/${row.id}`)
}

// 处理添加房间
function handleAddRoom() {
  router.push('/admin/room-add')
}

// 处理行点击
function handleRowClick(row) {
  router.push(`/admin/room-detail/${row.id}`)
}

// 处理状态过滤变化
function handleStatusChange() {
  // 状态改变时可以添加额外的逻辑
}

// 处理搜索清除
function handleSearchClear() {
  searchQuery.value = ''
}

onMounted(loadRooms)
</script>

<style scoped>
.room-list-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  color: #2c3e50;
}

.room-count {
  font-size: 0.9rem;
}

.header-right {
  display: flex;
  gap: 12px;
}

.search-input {
  width: 240px;
}

.room-table-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-number {
  font-weight: 600;
  color: #2c3e50;
}

.table-footer {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 16px;
  }
  
  .header-right {
    flex-direction: column;
  }
  
  .search-input {
    width: 100%;
  }
}
</style>