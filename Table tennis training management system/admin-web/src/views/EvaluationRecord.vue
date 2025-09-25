<template>
  <div class="evaluation-record-container">
    <!-- 搜索区域 -->
    <div class="search-form">
      <el-form :inline="true" :model="searchForm" class="demo-form-inline">
        <el-form-item label="预约单号">
          <el-input 
            v-model="searchForm.bookingNo" 
            placeholder="请输入预约单号" 
            clearable 
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd">新增评价记录</el-button>
    </div>

    <!-- 表格 -->
    <el-table 
      :data="tableData" 
      style="width: 100%" 
      v-loading="loading"
      border
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="bookingNo" label="预约单号" width="160" />
      <el-table-column prop="studentUsername" label="学员用户名" width="120" />
      <el-table-column prop="coachUsername" label="教练用户名" width="120" />
      <el-table-column label="学员评分" width="120">
        <template #default="scope">
          <el-rate 
            :model-value="scope.row.studentRating || 0" 
            disabled 
            size="small"
            show-score
          />
        </template>
      </el-table-column>
      <el-table-column label="教练评分" width="120">
        <template #default="scope">
          <el-rate 
            :model-value="scope.row.coachRating || 0" 
            disabled 
            size="small"
            show-score
          />
        </template>
      </el-table-column>
      <el-table-column prop="studentFeedback" label="学员反馈" width="200" show-overflow-tooltip />
      <el-table-column prop="coachFeedback" label="教练反馈" width="200" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="scope">
          <el-button 
            type="primary" 
            size="small" 
            @click="handleEdit(scope.row)"
          >
            编辑
          </el-button>
          <el-button 
            type="danger" 
            size="small" 
            @click="handleDelete(scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :small="false"
        :disabled="false"
        :background="true"
        layout="total, sizes, prev, pager, next, jumper"
        :total="pagination.total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      class="evaluation-record-dialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
        style="padding: 0 20px"
      >
        <div class="form-section">
          <h4 class="section-title">基本信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="选择预约记录" prop="bookingNo">
                <el-select 
                  v-model="form.bookingNo" 
                  placeholder="请选择预约记录（选中后自动填充相关信息）" 
                  filterable
                  @change="handleBookingChange"
                  style="width: 100%"
                >
                  <el-option 
                    v-for="booking in bookingList" 
                    :key="booking.bookingNo"
                    :label="`${booking.bookingNo} - ${booking.studentName} - ${booking.coachName} - ${booking.date}`" 
                    :value="booking.bookingNo"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="学员用户名">
                <el-input v-model="form.studentUsername" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学员姓名">
                <el-input v-model="form.studentName" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="教练用户名">
                <el-input v-model="form.coachUsername" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="教练姓名">
                <el-input v-model="form.coachName" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="上课日期">
                <el-input v-model="form.date" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="上课时间">
                <el-input :model-value="form.startTime && form.endTime ? `${form.startTime} - ${form.endTime}` : ''" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="球台编号">
                <el-input v-model="form.courtNumber" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="课时费">
                <el-input :model-value="form.amount ? `¥${form.amount}` : ''" placeholder="选择预约记录后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4 class="section-title">学员评价</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学员评分" prop="studentRating">
                <el-rate 
                  v-model="form.studentRating" 
                  :max="5"
                  show-score
                  allow-half
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="学员反馈" prop="studentFeedback">
                <el-input 
                  v-model="form.studentFeedback" 
                  type="textarea" 
                  :rows="4"
                  placeholder="请输入学员的收获与教训"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4 class="section-title">教练评价</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="教练评分" prop="coachRating">
                <el-rate 
                  v-model="form.coachRating" 
                  :max="5"
                  show-score
                  allow-half
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="24">
              <el-form-item label="教练反馈" prop="coachFeedback">
                <el-input 
                  v-model="form.coachFeedback" 
                  type="textarea" 
                  :rows="4"
                  placeholder="请输入教练对学员表现的评价与建议"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { 
  getEvaluationRecordList, 
  getEvaluationRecordById, 
  saveEvaluationRecord, 
  deleteEvaluationRecord 
} from '@/api/evaluationRecord'
import { getBookingRecordList } from '@/api/bookingRecord'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增评价记录')
const formRef = ref()
const bookingList = ref([])

// 搜索表单
const searchForm = reactive({
  bookingNo: ''
})

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据
const form = reactive({
  id: null,
  bookingNo: '',
  studentUsername: '',
  studentName: '', // 新增字段用于显示
  studentFeedback: '',
  studentRating: 0,
  coachUsername: '',
  coachName: '', // 新增字段用于显示
  coachFeedback: '',
  coachRating: 0,
  // 额外的预约记录相关字段（便于后续扩展或显示）
  campusId: '',
  courseId: null,
  date: '',
  startTime: '',
  endTime: '',
  duration: null,
  courtNumber: '',
  amount: null
})

// 表单验证规则
const rules = {
  bookingNo: [
    { required: true, message: '请选择预约记录', trigger: 'change' }
  ],
  studentRating: [
    { required: true, message: '请给出学员评分', trigger: 'change' }
  ],
  coachRating: [
    { required: true, message: '请给出教练评分', trigger: 'change' }
  ]
}

// 获取已完成的预约记录列表（用于下拉选择）
const getBookingListData = async () => {
  try {
    const response = await getBookingRecordList({ 
      pageNum: 1, 
      pageSize: 1000, 
      status: 'completed' 
    })
    if (response.code === 200) {
      bookingList.value = response.data.records
    }
  } catch (error) {
    console.error('获取预约记录列表失败:', error)
  }
}

// 预约记录选择改变时的处理
const handleBookingChange = (bookingNo) => {
  const selectedBooking = bookingList.value.find(booking => booking.bookingNo === bookingNo)
  if (selectedBooking) {
    // 自动填充所有相关字段
    form.studentUsername = selectedBooking.studentUsername
    form.coachUsername = selectedBooking.coachUsername
    
    // 额外填充其他可能用到的字段（如果将来扩展表单）
    form.studentName = selectedBooking.studentName
    form.coachName = selectedBooking.coachName
    form.campusId = selectedBooking.campusId
    form.courseId = selectedBooking.courseId
    form.date = selectedBooking.date
    form.startTime = selectedBooking.startTime
    form.endTime = selectedBooking.endTime
    form.duration = selectedBooking.duration
    form.courtNumber = selectedBooking.courtNumber
    form.amount = selectedBooking.amount
  }
}

// 获取评价记录列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getEvaluationRecordList(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取评价记录列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.pageNum = 1
  getList()
}

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
  pagination.pageNum = 1
  getList()
}

// 新增
const handleAdd = async () => {
  dialogTitle.value = '新增评价记录'
  resetForm()
  await getBookingListData()
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑评价记录'
  await getBookingListData()
  try {
    const response = await getEvaluationRecordById(row.id)
    if (response.code === 200) {
      Object.keys(form).forEach(key => {
        form[key] = response.data[key]
      })
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取评价记录详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除预约单号为 "${row.bookingNo}" 的评价记录吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteEvaluationRecord(row.id)
      if (response.code === 200) {
        ElMessage.success('删除成功')
        getList()
      }
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    const response = await saveEvaluationRecord({ ...form })
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      getList()
    }else {
      ElMessage.error(response.msg|| '操作失败')
    }
  } catch (error) {
    ElMessage.error(form.id ? '更新失败' : '新增失败')
  }
}

// 重置表单
const resetForm = () => {
  Object.keys(form).forEach(key => {
    if (key === 'id' || key === 'courseId' || key === 'duration' || key === 'amount') {
      form[key] = null
    } else if (key === 'studentRating' || key === 'coachRating') {
      form[key] = 0
    } else {
      form[key] = ''
    }
  })
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  getList()
}

// 当前页改变
const handleCurrentChange = (val) => {
  pagination.pageNum = val
  getList()
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  getList()
})
</script>

<style scoped>
.evaluation-record-container {
  padding: 20px;
}

.search-form {
  background: #f5f5f5;
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.operation-buttons {
  margin-bottom: 20px;
  text-align: left;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.evaluation-record-dialog .form-section {
  margin-bottom: 24px;
}

.evaluation-record-dialog .section-title {
  margin: 0 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #e4e7ed;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.dialog-footer {
  text-align: right;
}
</style>