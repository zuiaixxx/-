<template>
  <div class="my-booking-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>我的预约</h2>
      <el-button type="primary" @click="loadMyBooking">刷新</el-button>
    </div>

    <!-- 预约记录列表 -->
    <div class="table-container">
      <el-table 
        :data="bookingList" 
        style="width: 100%" 
        v-loading="loading"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="bookingNo" label="预约单号" width="150" />
        <el-table-column prop="studentName" label="学员姓名" width="120" />
        <el-table-column prop="campusId" label="校区编号" width="100" />
        <el-table-column prop="date" label="上课日期" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="100" />
        <el-table-column prop="endTime" label="结束时间" width="100" />
        <el-table-column prop="duration" label="时长(分钟)" width="100" />
        <el-table-column prop="courtNumber" label="球台编号" width="100" />
        <el-table-column prop="status" label="预约状态" width="120" align="center">
          <template #default="scope">
            <el-tag 
              :type="getStatusType(scope.row.status)" 
              size="small"
            >
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="课时费" width="100">
          <template #default="scope">
            ￥{{ scope.row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="支付状态" width="100" align="center">
          <template #default="scope">
            <el-tag 
              :type="getPaymentStatusType(scope.row.paymentStatus)" 
              size="small"
            >
              {{ getPaymentStatusText(scope.row.paymentStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="scope">
            <div class="action-buttons">
              <el-button
                v-if="scope.row.status === 'pending'"
                type="success"
                size="small"
                @click="handleConfirm(scope.row)"
              >
                确认
              </el-button>
              <el-button
                v-if="scope.row.status === 'confirmed'"
                type="primary"
                size="small"
                @click="handleComplete(scope.row)"
              >
                完成
              </el-button>
              <el-button
                v-if="['pending', 'confirmed'].includes(scope.row.status)"
                type="danger"
                size="small"
                @click="handleCancel(scope.row)"
              >
                取消
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && bookingList.length === 0" class="empty-state">
      <el-empty description="暂无预约数据" />
    </div>

    <!-- 取消预约对话框 -->
    <el-dialog
      v-model="cancelDialogVisible"
      title="取消预约"
      width="500px"
    >
      <el-form :model="cancelForm" label-width="100px">
        <el-form-item label="取消原因">
          <el-input
            v-model="cancelForm.cancelReason"
            type="textarea"
            :rows="4"
            placeholder="请输入取消原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancelDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmCancelBooking">确认取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getByCoachBooking, saveBookingRecord } from '@/api/bookingRecord'

// 响应式数据
const loading = ref(false)
const bookingList = ref([])
const cancelDialogVisible = ref(false)
const currentBooking = ref(null)

// 取消表单
const cancelForm = reactive({
  cancelReason: ''
})

// 获取我的预约记录列表
const loadMyBooking = async () => {
  loading.value = true
  try {
    const response = await getByCoachBooking()
    if (response.code === 200) {
      bookingList.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取预约记录失败')
    }
  } catch (error) {
    console.error('获取预约记录失败:', error)
    ElMessage.error('获取预约记录失败')
  } finally {
    loading.value = false
  }
}

// 确认预约
const handleConfirm = (row) => {
  ElMessageBox.confirm(
    `确定要确认预约单号为 "${row.bookingNo}" 的预约吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await saveBookingRecord({
        ...row,
        status: 'confirmed'
      })
      if (response.code === 200) {
        ElMessage.success('确认成功')
        loadMyBooking()
      } else {
        ElMessage.error(response.msg || '确认失败')
      }
    } catch (error) {
      console.error('确认失败:', error)
      ElMessage.error('确认失败')
    }
  })
}

// 完成预约
const handleComplete = (row) => {
  ElMessageBox.confirm(
    `确定要标记预约单号为 "${row.bookingNo}" 的预约为已完成吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await saveBookingRecord({
        ...row,
        status: 'completed'
      })
      if (response.code === 200) {
        ElMessage.success('标记完成成功')
        loadMyBooking()
      } else {
        ElMessage.error(response.msg || '操作失败')
      }
    } catch (error) {
      console.error('操作失败:', error)
      ElMessage.error('操作失败')
    }
  })
}

// 取消预约
const handleCancel = (row) => {
  currentBooking.value = row
  cancelForm.cancelReason = ''
  cancelDialogVisible.value = true
}

// 确认取消预约
const confirmCancelBooking = async () => {
  if (!cancelForm.cancelReason.trim()) {
    ElMessage.warning('请输入取消原因')
    return
  }
  
  try {
    const response = await saveBookingRecord({
      ...currentBooking.value,
      status: 'cancelled',
      cancelReason: cancelForm.cancelReason
    })
    if (response.code === 200) {
      ElMessage.success('取消成功')
      cancelDialogVisible.value = false
      loadMyBooking()
    } else {
      ElMessage.error(response.msg || '取消失败')
    }
  } catch (error) {
    console.error('取消失败:', error)
    ElMessage.error('取消失败')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const statusMap = {
    'pending': 'warning',
    'confirmed': 'primary',
    'cancelled': 'danger',
    'completed': 'success'
  }
  return statusMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'cancelled': '已取消',
    'completed': '已完成'
  }
  return statusMap[status] || status
}

// 获取支付状态类型
const getPaymentStatusType = (paymentStatus) => {
  const statusMap = {
    'unpaid': 'danger',
    'paid': 'success',
    'refunded': 'warning'
  }
  return statusMap[paymentStatus] || 'info'
}

// 获取支付状态文本
const getPaymentStatusText = (paymentStatus) => {
  const statusMap = {
    'unpaid': '未支付',
    'paid': '已支付',
    'refunded': '已退款'
  }
  return statusMap[paymentStatus] || paymentStatus
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadMyBooking()
})
</script>

<style scoped>
.my-booking-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  text-align: left;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.page-header .el-button {
  border-radius: 4px;
}

.table-container {
  margin-bottom: 20px;
}

.el-table {
  text-align: left;
}

.action-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-buttons .el-button {
  border-radius: 4px;
}

.empty-state {
  margin-top: 40px;
  text-align: center;
}

.dialog-footer {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.dialog-footer .el-button {
  border-radius: 4px;
}
</style>