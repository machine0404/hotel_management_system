<template>
  <el-card>
    <div style="margin-bottom: 16px; text-align: right;">
      <el-button type="primary" @click="loadOrders">刷新</el-button>
    </div>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="订单ID" width="80" />
      <el-table-column prop="user_id" label="用户ID" width="80" />
      <el-table-column prop="room_id" label="房间ID" width="80" />
      <el-table-column prop="check_in_date" label="入住时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.check_in_date) }}
        </template>
      </el-table-column>
      <el-table-column prop="check_out_date" label="离店时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.check_out_date) }}
        </template>
      </el-table-column>
      <el-table-column prop="create_time" label="下单时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.create_time) }}
        </template>
      </el-table-column>
      <el-table-column prop="total_amount" label="总价" width="100" />
      <el-table-column prop="adults" label="人数" width="60" />
      <el-table-column prop="invoice_needed" label="发票" width="60" :formatter="fapiaoFormat" />
      <el-table-column prop="status" label="状态" width="80" :formatter="statusFormat" />
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from '@/api/admin'
import dayjs from 'dayjs'
const orders = ref([])
async function loadOrders() {
  const res = await axios.get('/api/admin/order-list')
  orders.value = res.data
}
onMounted(loadOrders)
function fapiaoFormat(row) {
  return row.invoice_needed === 1 ? '需要' : '不需要'
}
function statusFormat(row) {
  const map = {0: '未处理', 1: '已接受', 2: '已取消'}
  return map[row.status] || '其它'
}
function formatDate(val) {
  if (!val) return ''
  const d = dayjs(val)
  if (!d.isValid()) return ''
  return d.format('YYYY-MM-DD')
}
</script>