<template>
  <el-card>
    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="id" label="订单ID" width="80" />
      <el-table-column prop="user_id" label="用户ID" width="80" />
      <el-table-column prop="room_id" label="房间ID" width="80" />
      <el-table-column prop="check_in_date" label="入住时间" width="180" />
      <el-table-column prop="check_out_date" label="离店时间" width="180" />
      <el-table-column prop="create_time" label="下单时间" width="180" />
      <el-table-column prop="total_amount" label="总价" width="100" />
      <el-table-column prop="adults" label="人数" width="60" />
      <el-table-column prop="invoice_needed" label="发票" width="60" :formatter="fapiaoFormat" />
      <el-table-column prop="status" label="状态" width="80" :formatter="statusFormat" />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button type="success" size="small" @click="handleAccept(scope.row)">接受</el-button>
          <el-button type="danger" size="small" @click="handleCancel(scope.row)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const orders = ref([])
onMounted(async () => {
  const res = await axios.get('/api/admin/order-wait')
  orders.value = res.data
})
function fapiaoFormat(row) {
  return row.invoice_needed === 1 ? '需要' : '不需要'
}
function statusFormat(row) {
  const map = {0: '未处理', 1: '已入住', 2: '退订', 3: '已完成'}
  return map[row.status] || '未知'
}
async function handleAccept(row) {
  if (confirm('确定要接受该订单吗？')) {
    const res = await axios.post(`/api/admin/order/${row.id}/accept`)
    if (res.data.success) {
      alert('已接受')
      // 刷新
      const r = await axios.get('/api/admin/order-wait')
      orders.value = r.data
    } else {
      alert('操作失败')
    }
  }
}
async function handleCancel(row) {
  if (confirm('确定要取消该订单吗？')) {
    const res = await axios.post(`/api/admin/order/${row.id}/cancel`)
    if (res.data.success) {
      alert('已取消')
      // 刷新
      const r = await axios.get('/api/admin/order-wait')
      orders.value = r.data
    } else {
      alert('操作失败')
    }
  }
}
</script>