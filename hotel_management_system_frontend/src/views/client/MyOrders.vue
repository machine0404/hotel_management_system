<template>
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
      <el-table-column prop="total_amount" label="总价" width="100" />
      <el-table-column prop="adults" label="入住人数" width="80" />
      <el-table-column prop="invoice_needed" label="发票" width="80" :formatter="fapiaoFormat" />
      <el-table-column prop="status" label="状态" width="100" :formatter="statusFormat" />
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button type="danger" size="small" @click="deleteOrder(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
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