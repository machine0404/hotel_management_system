<template>
  <div class="my-orders">
    <el-card>
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" label="订单ID" width="80" />
        <el-table-column prop="room_id" label="房间ID" width="80" />
        <el-table-column prop="type_name" label="房型" width="100" />
        <el-table-column prop="check_in_date" label="入住时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.check_in_date) }}
          </template>
        </el-table-column>
        <el-table-column prop="check_out_date" label="离店时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.check_out_date) }}
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="下单时间" width="160">
          <template #default="scope">
            {{ formatDate(scope.row.create_time) }}
          </template>
        </el-table-column>
        <el-table-column prop="total_amount" label="总价" width="100">
          <template #default="scope">
            ¥{{ scope.row.total_amount }}
          </template>
        </el-table-column>
        <el-table-column prop="adults" label="入住人数" width="80" />
        <el-table-column prop="invoice_needed" label="发票" width="80" :formatter="fapiaoFormat" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ statusFormat(scope.row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" size="small" @click="deleteOrder(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/user'
import dayjs from 'dayjs'
const orders = ref([])
async function loadOrders() {
  const res = await axios.get('/api/user/my-orders')
  orders.value = res.data
}
onMounted(loadOrders)
function formatDate(val) {
  if (!val) return ''
  const d = dayjs(val)
  if (!d.isValid()) return ''
  return d.format('YYYY-MM-DD')
}
function statusFormat(row) {
  const map = {0: '未处理', 1: '已接受', 2: '已取消'}
  return map[row.status] || '未知'
}
function getStatusType(status) {
  const types = {
    0: 'warning',  // 未处理
    1: 'success',  // 已接受
    2: 'danger'    // 已取消
  }
  return types[status] || 'info'
}
function fapiaoFormat(row) {
  return row.invoice_needed === 1 ? '需要' : '不需要'
}
async function deleteOrder(row) {
  if (!confirm('确定要删除该订单吗？')) return
  const res = await axios.delete(`/api/user/my-orders/${row.id}`)
  if (res.data.success) {
    alert('删除成功')
    loadOrders()
  } else {
    alert(res.data.message || '删除失败')
  }
}
</script>

<style scoped>
.my-orders {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: calc(100vh - 300px);
  /* 让表格横向滚动，防止内容挤压 */
  overflow-x: auto;
}

.my-orders :deep(.el-table) {
  --el-table-header-bg-color: #f5f7fa;
  --el-table-row-hover-bg-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
  min-width: 1100px; /* 增加表格最小宽度，保证横向排布 */
}

.my-orders :deep(.el-table th) {
  background-color: var(--el-table-header-bg-color);
  color: #606266;
  font-weight: 600;
  font-size: 15px;
  padding: 12px 8px;
  height: 48px;
  min-width: 80px;
  white-space: nowrap; /* 防止表头文字换行 */
  text-align: center;
}

.my-orders :deep(.el-table td) {
  font-size: 15px;
  padding: 12px 8px;
  height: 48px;
  min-width: 80px;
  white-space: nowrap; /* 防止内容换行 */
  text-align: center;
}

.my-orders :deep(.el-table--border) {
  border: 1px solid #EBEEF5;
  border-radius: 8px;
}

.my-orders :deep(.el-tag) {
  border-radius: 4px;
  padding: 0 12px;
  height: 28px;
  line-height: 28px;
  font-size: 14px;
  min-width: 60px;
  text-align: center;
  display: inline-block;
  vertical-align: middle;
  box-sizing: border-box;
}

.my-orders :deep(.el-table__empty-block) {
  min-height: 400px;
}

.my-orders :deep(.el-table__empty-text) {
  font-size: 16px;
  color: #909399;
}

@media (max-width: 1200px) {
  .my-orders :deep(.el-table) {
    min-width: 900px;
  }
}

@media (max-width: 768px) {
  .my-orders {
    padding: 10px;
    overflow-x: auto;
  }
  .my-orders :deep(.el-table th),
  .my-orders :deep(.el-table td) {
    padding: 8px 4px;
    font-size: 13px;
    min-width: 70px;
  }
}
</style>