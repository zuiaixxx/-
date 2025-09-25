<template>
  <div class="booking-record-container">
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
        <el-form-item label="所属校区">
          <el-select 
            v-model="searchForm.campusId" 
            placeholder="请选择校区" 
            clearable 
            style="width: 150px"
          >
            <el-option 
              v-for="campus in campusList" 
              :key="campus.id"
              :label="campus.campusName" 
              :value="String(campus.id)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="预约状态">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable 
            style="width: 150px"
          >
            <el-option label="待确认" value="pending" />
            <el-option label="已确认" value="confirmed" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已完成" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作按钮 -->
    <div class="operation-buttons">
      <el-button type="primary" @click="handleAdd">新增预约记录</el-button>
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
      <el-table-column prop="studentName" label="学员姓名" width="120" />
      <el-table-column prop="coachName" label="教练姓名" width="120" />
      <el-table-column label="所属校区" width="120">
        <template #default="scope">
          {{ getCampusName(scope.row.campusId) }}
        </template>
      </el-table-column>
      <el-table-column prop="date" label="上课日期" width="120" />
      <el-table-column label="上课时间" width="180">
        <template #default="scope">
          {{ scope.row.startTime }} - {{ scope.row.endTime }}
        </template>
      </el-table-column>
      <el-table-column prop="duration" label="时长(分钟)" width="100" />
      <el-table-column prop="courtNumber" label="球台编号" width="100" />
      <el-table-column prop="status" label="预约状态" width="120">
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
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="paymentStatus" label="支付状态" width="120">
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
      width="1000px"
      class="booking-record-dialog"
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
              <el-form-item label="预约单号" prop="bookingNo">
                <el-input v-model="form.bookingNo" placeholder="系统自动生成，可手动修改" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="选择学员" prop="studentUsername">
                <el-select 
                  v-model="form.studentUsername" 
                  placeholder="请选择学员（选中后自动填充姓名）" 
                  filterable
                  @change="handleStudentChange"
                  style="width: 100%"
                >
                  <el-option 
                    v-for="user in userList" 
                    :key="user.username"
                    :label="`${user.name}(${user.username})`" 
                    :value="user.username"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="学员姓名">
                <el-input v-model="form.studentName" placeholder="选择学员后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="教练用户名">
                <el-input v-model="form.coachUsername" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="教练姓名">
                <el-input v-model="form.coachName" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="所属校区" prop="campusId">
                <el-select v-model="form.campusId" placeholder="请选择校区" style="width: 100%">
                  <el-option 
                    v-for="campus in campusList" 
                    :key="campus.id"
                    :label="campus.campusName" 
                    :value="String(campus.id)"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4 class="section-title">课程信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="选择课程" prop="courseId">
                <el-select 
                  v-model="form.courseId" 
                  placeholder="请选择课程（选中后自动填充相关信息）" 
                  filterable
                  @change="handleCourseChange"
                  style="width: 100%"
                >
                  <el-option 
                    v-for="course in courseList" 
                    :key="course.id"
                    :label="getCourseDisplayLabel(course)"
                    :value="course.id"
                    :disabled="!course.available"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="上课日期">
                <el-input v-model="form.date" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="开始时间">
                <el-input v-model="form.startTime" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="结束时间">
                <el-input v-model="form.endTime" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="时长(分钟)">
                <el-input v-model="form.duration" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="球台编号">
                <el-input v-model="form.courtNumber" placeholder="选择课程后自动填充" readonly />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课时费" prop="amount">
                <el-input-number 
                  v-model="form.amount" 
                  :precision="2" 
                  :min="0" 
                  :max="9999.99"
                  style="width: 100%"
                  placeholder="选择课程后可自动计算"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <div class="form-section">
          <h4 class="section-title">状态信息</h4>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="预约状态" prop="status">
                <el-select v-model="form.status" placeholder="请选择预约状态" style="width: 100%">
                  <el-option label="待确认" value="pending" />
                  <el-option label="已确认" value="confirmed" />
                  <el-option label="已取消" value="cancelled" />
                  <el-option label="已完成" value="completed" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="支付状态" prop="paymentStatus">
                <el-select v-model="form.paymentStatus" placeholder="请选择支付状态" style="width: 100%">
                  <el-option label="未支付" value="unpaid" />
                  <el-option label="已支付" value="paid" />
                  <el-option label="已退款" value="refunded" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20" v-if="form.status === 'cancelled'">
            <el-col :span="12">
              <el-form-item label="取消方">
                <el-input v-model="form.cancelInitiator" placeholder="请输入取消方用户名" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="取消原因">
                <el-input v-model="form.cancelReason" placeholder="请输入取消原因" />
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
  getBookingRecordList, 
  getBookingRecordById, 
  saveBookingRecord, 
  deleteBookingRecord 
} from '@/api/bookingRecord'
import { getAllUser } from '@/api/user'
import { getCampusList } from '@/api/campus'
import { getAllAvailableCourses } from '@/api/course'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增预约记录')
const formRef = ref()
const userList = ref([])
const campusList = ref([])
const courseList = ref([])

// 搜索表单
const searchForm = reactive({
  bookingNo: '',
  campusId: '',
  status: ''
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
  studentName: '',
  coachUsername: '',
  coachName: '',
  campusId: '',
  courseId: null,
  date: '',
  startTime: '',
  endTime: '',
  duration: null,
  courtNumber: '',
  status: 'pending',
  amount: null,
  paymentStatus: 'unpaid',
  cancelInitiator: '',
  cancelReason: ''
})

// 表单验证规则
const rules = {
  bookingNo: [
    { required: true, message: '请输入预约单号', trigger: 'blur' },
    { max: 32, message: '预约单号长度不能超过32个字符', trigger: 'blur' }
  ],
  studentUsername: [
    { required: true, message: '请选择学员', trigger: 'change' }
  ],
  campusId: [
    { required: true, message: '请选择校区', trigger: 'change' }
  ],
  courseId: [
    { required: true, message: '请选择课程', trigger: 'change' }
  ],
  amount: [
    { required: true, message: '请输入课时费', trigger: 'blur' }
  ]
}

// 获取所有课程列表
const getCourseListData = async () => {
  try {
    const response = await getAllAvailableCourses()
    if (response.code === 200) {
      courseList.value = response.data.records
    }
  } catch (error) {
    console.error('获取课程列表失败:', error)
  }
}

// 课程选择改变时的处理
const handleCourseChange = (courseId) => {
  const selectedCourse = courseList.value.find(course => course.id === courseId)
  if (selectedCourse) {
    // 自动填充课程相关信息
    form.date = selectedCourse.date
    form.startTime = selectedCourse.startTime
    form.endTime = selectedCourse.endTime
    form.duration = selectedCourse.duration
    form.courtNumber = selectedCourse.courtNumber
    
    // 自动填充教练信息
    form.coachUsername = selectedCourse.coachUsername
    form.coachName = selectedCourse.coachName
    
    // 自动填充校区信息
    form.campusId = String(selectedCourse.campusId)
    
    // 根据课程可用性设置预约状态
    if (selectedCourse.available) {
      form.status = 'pending'
    } else {
      form.status = 'cancelled'
    }
    
    // 自动计算课时费（默认每分钟2元，可以根据实际情况调整）
    if (selectedCourse.duration && !form.amount) {
      const ratePerMinute = 2 // 每分钟费率
      form.amount = selectedCourse.duration * ratePerMinute
    }
  }
}

// 获取校区名称
const getCampusName = (campusId) => {
  const campus = campusList.value.find(c => String(c.id) === String(campusId))
  return campus ? campus.campusName : campusId
}

// 获取所有用户列表
const getUserListData = async () => {
  try {
    const response = await getAllUser()
    if (response.code === 200) {
      userList.value = response.data
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 获取校区列表
const getCampusListData = async () => {
  try {
    const response = await getCampusList({ pageNum: 1, pageSize: 1000 })
    if (response.code === 200) {
      campusList.value = response.data.records
    }
  } catch (error) {
    console.error('获取校区列表失败:', error)
  }
}

// 学员选择改变时的处理
const handleStudentChange = (username) => {
  const selectedUser = userList.value.find(user => user.username === username)
  if (selectedUser) {
    form.studentName = selectedUser.name
    
    // 如果学员没有预约单号，自动生成一个
    if (!form.bookingNo) {
      const timestamp = Date.now()
      const randomNum = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
      form.bookingNo = `BK${timestamp}${randomNum}`
    }
  }
}

// 移除教练选择处理函数，因为教练信息现在通过课程自动填充
// const handleCoachChange = (username) => {
//   const selectedCoach = coachList.value.find(coach => coach.username === username)
//   if (selectedCoach) {
//     form.coachName = selectedCoach.name
//     
//     // 过滤出该教练的可用课程
//     filterCoursesByCoach(username)
//   }
// }

// 根据教练过滤课程
const filterCoursesByCoach = (coachUsername) => {
  // 这里可以根据需要实现课程过滤逻辑
  // 暂时保持所有课程显示，但可以在未来扩展
}

// 格式化课程显示标签
const getCourseDisplayLabel = (course) => {
  const campusName = getCampusName(course.campusId)
  const availableText = course.available ? '[可预约]' : '[已满]'
  return `${course.coachName} | ${course.date} ${course.startTime}-${course.endTime} | 球台${course.courtNumber} | ${campusName} ${availableText}`
}

// 获取预约记录列表
const getList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      ...searchForm
    }
    const response = await getBookingRecordList(params)
    if (response.code === 200) {
      tableData.value = response.data.records
      pagination.total = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取预约记录列表失败')
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
  dialogTitle.value = '新增预约记录'
  resetForm()
  await Promise.all([getUserListData(), getCampusListData(), getCourseListData()])
  
  // 自动生成预约单号
  if (!form.bookingNo) {
    const timestamp = Date.now()
    const randomNum = Math.floor(Math.random() * 1000).toString().padStart(3, '0')
    form.bookingNo = `BK${timestamp}${randomNum}`
  }
  
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑预约记录'
  await Promise.all([getUserListData(), getCampusListData(), getCourseListData()])
  try {
    const response = await getBookingRecordById(row.id)
    if (response.code === 200) {
      Object.keys(form).forEach(key => {
        form[key] = response.data[key]
      })
      dialogVisible.value = true
    }
  } catch (error) {
    ElMessage.error('获取预约记录详情失败')
  }
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除预约单号为 "${row.bookingNo}" 的预约记录吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const response = await deleteBookingRecord(row.id)
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
    const response = await saveBookingRecord({ ...form })
    if (response.code === 200) {
      ElMessage.success(form.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      getList()
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
    } else if (key === 'status') {
      form[key] = 'pending'
    } else if (key === 'paymentStatus') {
      form[key] = 'unpaid'
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

// 获取状态标签类型
const getStatusType = (status) => {
  const typeMap = {
    'pending': 'warning',
    'confirmed': 'primary',
    'cancelled': 'danger',
    'completed': 'success'
  }
  return typeMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    'pending': '待确认',
    'confirmed': '已确认',
    'cancelled': '已取消',
    'completed': '已完成'
  }
  return textMap[status] || status
}

// 获取支付状态标签类型
const getPaymentStatusType = (paymentStatus) => {
  const typeMap = {
    'unpaid': 'warning',
    'paid': 'success',
    'refunded': 'info'
  }
  return typeMap[paymentStatus] || ''
}

// 获取支付状态文本
const getPaymentStatusText = (paymentStatus) => {
  const textMap = {
    'unpaid': '未支付',
    'paid': '已支付',
    'refunded': '已退款'
  }
  return textMap[paymentStatus] || paymentStatus
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  getList()
  getCampusListData()
})
</script>

<style scoped>
.booking-record-container {
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

.booking-record-dialog .form-section {
  margin-bottom: 24px;
}

.booking-record-dialog .section-title {
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