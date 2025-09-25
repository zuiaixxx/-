<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from '@/store'
import { getMyCoach } from '@/api/studentCoach'
import { getByStudentBooking } from '@/api/bookingRecord'
import { getStudentEvaluation, saveEvaluationRecord } from '@/api/evaluationRecord'
import { getByUser, saveTuitionRecord } from '@/api/tuitionRecord'
import { getUserByToken } from '@/api/user'
import { User, Refresh, Plus, Minus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const userStore = useStore()
const userInfo = ref({})
const myCoaches = ref([])
const myBookings = ref([])
const myEvaluations = ref([])
const myTuitionRecords = ref([])
const activeTab = ref('profile')
const loading = ref(false)

// 评价弹窗相关
const evaluationDialogVisible = ref(false)
const evaluationForm = ref({
  bookingNo: '',
  studentUsername: '',
  coachUsername: '',
  studentRating: 5,
  studentFeedback: ''
})
const evaluationLoading = ref(false)

// 支付弹窗相关
const paymentDialogVisible = ref(false)
const paymentForm = ref({
  bookingNo: '',
  amount: 0,
  paymentMethod: 'wechat'
})
const paymentLoading = ref(false)
const qrCodeUrl = ref('')

// 获取用户信息
const getUserInfo = async () => {
  try {
    const response = await getUserByToken()
    if (response.code === 200) {
      userInfo.value = response.data
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 获取我的教练
const getMyCoaches = async () => {
  loading.value = true
  try {
    const response = await getMyCoach()
    if (response.code === 200) {
      myCoaches.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取教练列表失败')
    }
  } catch (error) {
    console.error('获取教练列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取我的预约
const getMyBookings = async () => {
  loading.value = true
  try {
    const response = await getByStudentBooking()
    if (response.code === 200) {
      myBookings.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取预约列表失败')
    }
  } catch (error) {
    console.error('获取预约列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取我的评价
const getMyEvaluations = async () => {
  loading.value = true
  try {
    const response = await getStudentEvaluation()
    if (response.code === 200) {
      myEvaluations.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取评价列表失败')
    }
  } catch (error) {
    console.error('获取评价列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 获取我的缴费记录
const getMyTuitionRecords = async () => {
  loading.value = true
  try {
    const response = await getByUser()
    if (response.code === 200) {
      // 根据新的响应示例，数据直接在 data 数组中
      myTuitionRecords.value = response.data || []
    } else {
      ElMessage.error(response.msg || '获取缴费记录失败')
    }
  } catch (error) {
    console.error('获取缴费记录错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 选项卡切换处理
const handleTabChange = (tabName) => {
  if (tabName === 'coaches') {
    getMyCoaches()
  } else if (tabName === 'bookings') {
    // 同时获取预约和评价数据，以便正确显示评价状态
    Promise.all([getMyBookings(), getMyEvaluations()])
  } else if (tabName === 'evaluations') {
    getMyEvaluations()
  } else if (tabName === 'tuitions') {
    getMyTuitionRecords()
  }
}

// 获取教练等级标签类型
const getCoachLevelType = (level) => {
  const levelMap = {
    '初级': 'info',
    '中级': 'warning',
    '高级': 'danger'
  }
  return levelMap[level] || 'info'
}

// 获取审核状态标签类型
const getApprovalStatusType = (isApproved) => {
  return isApproved ? 'success' : 'warning'
}

// 获取审核状态文本
const getApprovalStatusText = (isApproved) => {
  return isApproved ? '已审核' : '待审核'
}

// 格式化日期时间
const formatDateTime = (dateTimeString) => {
  if (!dateTimeString) return '-'
  const date = new Date(dateTimeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '-'
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 获取预约状态标签类型
const getBookingStatusType = (status) => {
  const statusMap = {
    'confirmed': 'success',
    'pending': 'warning',
    'cancelled': 'danger',
    'completed': 'info'
  }
  return statusMap[status] || 'info'
}

// 获取预约状态文本
const getBookingStatusText = (status) => {
  const statusMap = {
    'confirmed': '已确认',
    'pending': '待确认',
    'cancelled': '已取消',
    'completed': '已完成'
  }
  return statusMap[status] || status
}

// 获取支付状态标签类型
const getPaymentStatusType = (status) => {
  const statusMap = {
    'paid': 'success',
    'unpaid': 'warning',
    'refunded': 'danger',
    'failed': 'danger'
  }
  return statusMap[status] || 'info'
}

// 获取支付状态文本
const getPaymentStatusText = (status) => {
  const statusMap = {
    'paid': '已支付',
    'unpaid': '未支付',
    'refunded': '已退款',
    'failed': '支付失败'
  }
  return statusMap[status] || status
}

// 获取评价状态文本
const getEvaluationStatusText = (studentRating, studentFeedback) => {
  if (studentRating !== null || studentFeedback !== null) {
    return '已评价'
  }
  return '待评价'
}

// 获取评价状态标签类型
const getEvaluationStatusType = (studentRating, studentFeedback) => {
  if (studentRating !== null || studentFeedback !== null) {
    return 'success'
  }
  return 'warning'
}

// 渲染评分星星
const renderStars = (rating) => {
  if (!rating) return '未评分'
  return '★'.repeat(rating) + '☆'.repeat(5 - rating)
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  const methodMap = {
    'wechat': '微信支付',
    'alipay': '支付宝',
    'cash': '现金',
    'card': '银行卡',
    'other': '其他'
  }
  return methodMap[method] || method
}

// 获取支付方式标签类型
const getPaymentMethodType = (method) => {
  const methodMap = {
    'wechat': 'success',
    'alipay': 'primary',
    'cash': 'warning',
    'card': 'info',
    'other': 'danger'
  }
  return methodMap[method] || 'info'
}

// 判断是否为正数金额（充值）
const isPositiveAmount = (amount) => {
  return parseFloat(amount) > 0
}

// 根据备注判断交易类型
const isIncomeTransaction = (remark) => {
  if (!remark) return false
  const incomeKeywords = ['充值', '充铱', '充值记录', '充值成功']
  return incomeKeywords.some(keyword => remark.includes(keyword))
}

// 获取金额标签类型
const getAmountType = (remark) => {
  return isIncomeTransaction(remark) ? 'success' : 'warning'
}

// 获取金额前缀
const getAmountPrefix = (remark) => {
  return isIncomeTransaction(remark) ? '+' : '-'
}

// 获取交易类型文本
const getTransactionTypeText = (remark) => {
  return isIncomeTransaction(remark) ? '充值记录' : '消费记录'
}

// 获取交易类型图标
const getTransactionIcon = (remark) => {
  return isIncomeTransaction(remark) ? 'Plus' : 'Minus'
}

// 检查预约是否可以评价
const canEvaluate = (booking) => {
  return booking.status === 'completed'
}

// 检查预约是否已评价
const hasEvaluated = (bookingNo) => {
  return myEvaluations.value.some(evaluation => 
    evaluation.bookingNo === bookingNo && 
    (evaluation.studentRating !== null || evaluation.studentFeedback !== null)
  )
}

// 打开评价弹窗
const openEvaluationDialog = (booking) => {
  evaluationForm.value = {
    bookingNo: booking.bookingNo,
    studentUsername: booking.studentUsername,
    coachUsername: booking.coachUsername,
    studentRating: 5,
    studentFeedback: ''
  }
  evaluationDialogVisible.value = true
}

// 关闭评价弹窗
const closeEvaluationDialog = () => {
  evaluationDialogVisible.value = false
  evaluationForm.value = {
    bookingNo: '',
    studentUsername: '',
    coachUsername: '',
    studentRating: 5,
    studentFeedback: ''
  }
}

// 提交评价
const submitEvaluation = async () => {
  evaluationLoading.value = true
  try {
    const response = await saveEvaluationRecord(evaluationForm.value)
    if (response.code === 200) {
      ElMessage.success('评价提交成功')
      closeEvaluationDialog()
      // 刷新评价列表
      if (activeTab.value === 'evaluations') {
        getMyEvaluations()
      }
    } else {
      ElMessage.error(response.msg || '评价提交失败')
    }
  } catch (error) {
    console.error('评价提交错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    evaluationLoading.value = false
  }
}

// 检查预约是否需要支付
const needsPayment = (booking) => {
  return booking.paymentStatus === 'unpaid'
}

// 打开支付弹窗
const openPaymentDialog = (booking) => {
  paymentForm.value = {
    bookingNo: booking.bookingNo,
    amount: booking.amount,
    paymentMethod: 'wechat'
  }
  generateQRCode()
  paymentDialogVisible.value = true
}

// 关闭支付弹窗
const closePaymentDialog = () => {
  paymentDialogVisible.value = false
  paymentForm.value = {
    bookingNo: '',
    amount: 0,
    paymentMethod: 'wechat'
  }
  qrCodeUrl.value = ''
}

// 生成二维码（模拟）
const generateQRCode = () => {
  // 模拟生成二维码链接
  const mockPaymentData = {
    amount: paymentForm.value.amount,
    bookingNo: paymentForm.value.bookingNo,
    method: paymentForm.value.paymentMethod
  }
  // 使用一个公共的二维码生成服务
  qrCodeUrl.value = `https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(JSON.stringify(mockPaymentData))}`
}

// 切换支付方式
const changePaymentMethod = (method) => {
  paymentForm.value.paymentMethod = method
  generateQRCode()
}

// 模拟支付成功
const simulatePaymentSuccess = async () => {
  paymentLoading.value = true
  try {
    // 模拟生成缴费记录，使用预约编号作为记录编号
    const tuitionData = {
      recordNo: paymentForm.value.bookingNo,
      username: userInfo.value.username,
      name: userInfo.value.realName || userInfo.value.username,
      amount: paymentForm.value.amount,
      paymentMethod: paymentForm.value.paymentMethod,
      remark: `预约课程支付 - ${paymentForm.value.bookingNo}`
    }
    
    const response = await saveTuitionRecord(tuitionData)
    if (response.code === 200) {
      ElMessage.success('支付成功！')
      closePaymentDialog()
      // 刷新预约列表
      getMyBookings()
    } else {
      ElMessage.error(response.msg || '支付失败')
    }
  } catch (error) {
    console.error('支付错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    paymentLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  getUserInfo()
  // 不在页面加载时获取教练数据，等待用户切换到该选项卡
})
</script>

<template>
  <div class="profile-container">
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon">
          <User />
        </el-icon>
        个人中心
      </h1>
      <p class="page-subtitle">管理您的个人信息和教练</p>
    </div>

    <el-tabs v-model="activeTab" class="profile-tabs" @tab-change="handleTabChange">
      <!-- 个人信息选项卡 -->
      <el-tab-pane label="个人信息" name="profile">
        <el-card class="profile-card">
          <div class="user-info">
            <el-avatar 
              :size="100" 
              :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" 
            />
            <h2>{{ userInfo.realName || userInfo.username || '未登录' }}</h2>
            <p class="user-role">学员</p>
          </div>
          
          <el-descriptions title="详细信息" :column="2" border>
            <el-descriptions-item label="用户名" :span="2">
              {{ userInfo.username || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="真实姓名" :span="2">
              {{ userInfo.realName || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="邮箱">
              {{ userInfo.email || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="手机号">
              {{ userInfo.phone || '暂无' }}
            </el-descriptions-item>
            <el-descriptions-item label="用户状态">
              <el-tag :type="userInfo.status === 0 ? 'success' : 'warning'">
                {{ userInfo.status === 0 ? '正常' : '禁用' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="注册时间">
              {{ userInfo.createTime ? new Date(userInfo.createTime).toLocaleString('zh-CN') : '暂无' }}
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-tab-pane>

      <!-- 我的教练选项卡 -->
      <el-tab-pane label="我的教练" name="coaches">
        <div class="coaches-section" v-loading="loading">
          <div class="section-header">
            <h3>我的教练列表</h3>
            <el-button type="primary" @click="getMyCoaches">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <div v-if="myCoaches.length > 0" class="coaches-grid">
            <div v-for="coach in myCoaches" :key="coach.id" class="coach-item">
              <div class="coach-avatar">
                <img 
                  src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" 
                  :alt="coach.coachName"
                />
                <div class="coach-level-badge">
                  <el-tag type="info" size="small">
                    教练
                  </el-tag>
                </div>
              </div>
              
              <div class="coach-details">
                <h4 class="coach-name">{{ coach.coachName }}</h4>
                <p class="coach-username">教练 ID: {{ coach.coachId }}</p>
                
                <div class="coach-info-grid">
                  <div class="info-row">
                    <span class="label">申请人：</span>
                    <span class="value">{{ coach.userName }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">审核状态：</span>
                    <el-tag :type="getApprovalStatusType(coach.isApproved)" size="small">
                      {{ getApprovalStatusText(coach.isApproved) }}
                    </el-tag>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">申请时间：</span>
                    <span class="value">{{ formatDateTime(coach.applyTime) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">创建时间：</span>
                    <span class="value">{{ formatDateTime(coach.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <el-empty 
            v-else-if="!loading" 
            description="您还没有选择任何教练"
            :image-size="200"
          >
            <el-button type="primary" @click="$router.push('/coach')">
              去选择教练
            </el-button>
          </el-empty>
        </div>
      </el-tab-pane>

      <!-- 我的预约选项卡 -->
      <el-tab-pane label="我的预约" name="bookings">
        <div class="bookings-section" v-loading="loading">
          <div class="section-header">
            <h3>我的预约列表</h3>
            <el-button type="primary" @click="getMyBookings">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <div v-if="myBookings.length > 0" class="bookings-grid">
            <div v-for="booking in myBookings" :key="booking.id" class="booking-item">
              <div class="booking-header">
                <div class="booking-no">
                  <strong>预约编号：{{ booking.bookingNo }}</strong>
                </div>
                <div class="booking-status">
                  <el-tag :type="getBookingStatusType(booking.status)" size="small">
                    {{ getBookingStatusText(booking.status) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="booking-content">
                <div class="booking-info-grid">
                  <div class="info-row">
                    <span class="label">学员姓名：</span>
                    <span class="value">{{ booking.studentName }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">教练姓名：</span>
                    <span class="value">{{ booking.coachName }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">上课日期：</span>
                    <span class="value">{{ formatDate(booking.date) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">上课时间：</span>
                    <span class="value">{{ booking.startTime }} - {{ booking.endTime }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">时长：</span>
                    <span class="value">{{ booking.duration }}分钟</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">球台编号：</span>
                    <span class="value">{{ booking.courtNumber }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">金额：</span>
                    <span class="value amount">￥{{ booking.amount }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">支付状态：</span>
                    <el-tag :type="getPaymentStatusType(booking.paymentStatus)" size="small">
                      {{ getPaymentStatusText(booking.paymentStatus) }}
                    </el-tag>
                  </div>
                  
                  <div v-if="booking.cancelReason" class="info-row">
                    <span class="label">取消原因：</span>
                    <span class="value">{{ booking.cancelReason }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">创建时间：</span>
                    <span class="value">{{ formatDateTime(booking.createTime) }}</span>
                  </div>
                </div>
                
                <!-- 操作按钮区域 -->
                <div class="booking-actions">
                  <!-- 支付按钮 -->
                  <el-button 
                    v-if="needsPayment(booking)" 
                    type="warning" 
                    size="small" 
                    @click="openPaymentDialog(booking)"
                  >
                    去支付
                  </el-button>
                  
                  <!-- 评价按钮 -->
                  <el-button 
                    v-if="canEvaluate(booking) && !hasEvaluated(booking.bookingNo)" 
                    type="primary" 
                    size="small" 
                    @click="openEvaluationDialog(booking)"
                  >
                    去评价
                  </el-button>
                  
                  <!-- 状态显示 -->
                  <el-tag 
                    v-if="canEvaluate(booking) && hasEvaluated(booking.bookingNo)" 
                    type="success" 
                    size="small"
                  >
                    已评价
                  </el-tag>
                </div>
              </div>
            </div>
          </div>

          <el-empty 
            v-else-if="!loading" 
            description="您还没有任何预约记录"
            :image-size="200"
          >
            <el-button type="primary" @click="$router.push('/course')">
              去预约课程
            </el-button>
          </el-empty>
        </div>
      </el-tab-pane>

      <!-- 我的评价选项卡 -->
      <el-tab-pane label="我的评价" name="evaluations">
        <div class="evaluations-section" v-loading="loading">
          <div class="section-header">
            <h3>我的评价列表</h3>
            <el-button type="primary" @click="getMyEvaluations">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <div v-if="myEvaluations.length > 0" class="evaluations-grid">
            <div v-for="evaluation in myEvaluations" :key="evaluation.id" class="evaluation-item">
              <div class="evaluation-header">
                <div class="booking-no">
                  <strong>预约编号：{{ evaluation.bookingNo }}</strong>
                </div>
                <div class="evaluation-status">
                  <el-tag :type="getEvaluationStatusType(evaluation.studentRating, evaluation.studentFeedback)" size="small">
                    {{ getEvaluationStatusText(evaluation.studentRating, evaluation.studentFeedback) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="evaluation-content">
                <div class="evaluation-info-grid">
                  <div class="info-row">
                    <span class="label">学员用户名：</span>
                    <span class="value">{{ evaluation.studentUsername }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">教练用户名：</span>
                    <span class="value">{{ evaluation.coachUsername }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">我的评分：</span>
                    <span class="value rating-stars">{{ renderStars(evaluation.studentRating) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">我的评价：</span>
                    <span class="value">{{ evaluation.studentFeedback || '暂无评价' }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">教练评分：</span>
                    <span class="value rating-stars">{{ renderStars(evaluation.coachRating) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">教练评价：</span>
                    <span class="value">{{ evaluation.coachFeedback || '教练暂未评价' }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">创建时间：</span>
                    <span class="value">{{ formatDateTime(evaluation.createTime) }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">更新时间：</span>
                    <span class="value">{{ formatDateTime(evaluation.updateTime) }}</span>
                  </div>
                </div>
                
                <!-- 操作按钮区域 -->
                <div v-if="!evaluation.studentRating && !evaluation.studentFeedback" class="evaluation-actions">
                  <el-button type="primary" size="small">
                    去评价
                  </el-button>
                </div>
              </div>
            </div>
          </div>

          <el-empty 
            v-else-if="!loading" 
            description="您还没有任何评价记录"
            :image-size="200"
          >
            <el-button type="primary" @click="$router.push('/course')">
              去预约课程
            </el-button>
          </el-empty>
        </div>
      </el-tab-pane>

      <!-- 我的缴费记录选项卡 -->
      <el-tab-pane label="我的缴费" name="tuitions">
        <div class="tuitions-section" v-loading="loading">
          <div class="section-header">
            <h3>我的缴费记录</h3>
            <el-button type="primary" @click="getMyTuitionRecords">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>

          <div v-if="myTuitionRecords.length > 0" class="tuitions-grid">
            <div v-for="tuition in myTuitionRecords" :key="tuition.id" class="tuition-item">
              <div class="tuition-header">
                <div class="record-no">
                  <strong>记录编号：{{ tuition.recordNo }}</strong>
                </div>
                <div class="amount-badge">
                  <el-tag :type="getAmountType(tuition.remark)" size="large">
                    {{ getAmountPrefix(tuition.remark) }}￥{{ tuition.amount }}
                  </el-tag>
                </div>
              </div>
              
              <div class="tuition-content">
                <div class="tuition-info-grid">
                  <div class="info-row">
                    <span class="label">用户名：</span>
                    <span class="value">{{ tuition.username }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">姓名：</span>
                    <span class="value">{{ tuition.name }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">支付方式：</span>
                    <el-tag :type="getPaymentMethodType(tuition.paymentMethod)" size="small">
                      {{ getPaymentMethodText(tuition.paymentMethod) }}
                    </el-tag>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">备注：</span>
                    <span class="value remark">{{ tuition.remark || '无备注' }}</span>
                  </div>
                  
                  <div class="info-row">
                    <span class="label">创建时间：</span>
                    <span class="value">{{ formatDateTime(tuition.createTime) }}</span>
                  </div>
                </div>
                
                <!-- 交易类型指示 -->
                <div class="transaction-type">
                  <el-icon class="type-icon">
                    <component :is="getTransactionIcon(tuition.remark)" />
                  </el-icon>
                  <span class="type-text">
                    {{ getTransactionTypeText(tuition.remark) }}
                  </span>
                </div>
              </div>
            </div>
          </div>

          <el-empty 
            v-else-if="!loading" 
            description="您还没有任何缴费记录"
            :image-size="200"
          />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 评价弹窗 -->
    <el-dialog
      v-model="evaluationDialogVisible"
      title="课程评价"
      width="500px"
      :before-close="closeEvaluationDialog"
    >
      <el-form :model="evaluationForm" label-width="100px" label-position="left">
        <el-form-item label="预约编号">
          <el-input v-model="evaluationForm.bookingNo" disabled />
        </el-form-item>
        
        <el-form-item label="评分" required>
          <el-rate
            v-model="evaluationForm.studentRating"
            :max="5"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            :void-color="'#C6D1DE'"
            size="large"
            show-score
            score-template="{value} 分"
          />
        </el-form-item>
        
        <el-form-item label="评价内容" required>
          <el-input
            v-model="evaluationForm.studentFeedback"
            type="textarea"
            :rows="4"
            placeholder="请分享您的学习收获和建议..."
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeEvaluationDialog">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitEvaluation"
            :loading="evaluationLoading"
            :disabled="!evaluationForm.studentFeedback"
          >
            {{ evaluationLoading ? '提交中...' : '提交评价' }}
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 支付弹窗 -->
    <el-dialog
      v-model="paymentDialogVisible"
      title="课程支付"
      width="500px"
      :before-close="closePaymentDialog"
    >
      <div class="payment-content">
        <div class="payment-info">
          <el-descriptions title="支付信息" :column="2" border>
            <el-descriptions-item label="预约编号">
              {{ paymentForm.bookingNo }}
            </el-descriptions-item>
            <el-descriptions-item label="支付金额">
              <span class="payment-amount">￥{{ paymentForm.amount }}</span>
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="payment-method-section">
          <h4>选择支付方式</h4>
          <div class="payment-methods">
            <div 
              class="payment-method-item"
              :class="{ active: paymentForm.paymentMethod === 'wechat' }"
              @click="changePaymentMethod('wechat')"
            >
              <div class="method-icon wechat-icon">📱</div>
              <div class="method-name">微信支付</div>
            </div>
            
            <div 
              class="payment-method-item"
              :class="{ active: paymentForm.paymentMethod === 'alipay' }"
              @click="changePaymentMethod('alipay')"
            >
              <div class="method-icon alipay-icon">💳</div>
              <div class="method-name">支付宝</div>
            </div>
          </div>
        </div>
        
        <div class="qr-code-section">
          <h4>扫码支付</h4>
          <div class="qr-code-container">
            <img :src="qrCodeUrl" alt="支付二维码" class="qr-code-image" />
            <p class="qr-code-tip">请使用{{ getPaymentMethodText(paymentForm.paymentMethod) }}扫码支付</p>
          </div>
        </div>
        
        <!-- 模拟支付成功按钮 -->
        <div class="simulate-payment">
          <el-alert
            title="模拟测试"
            type="info"
            description="这是模拟支付界面，点击下方按钮模拟支付成功"
            show-icon
            :closable="false"
          />
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePaymentDialog">取消支付</el-button>
          <el-button 
            type="primary" 
            @click="simulatePaymentSuccess"
            :loading="paymentLoading"
          >
            {{ paymentLoading ? '处理中...' : '模拟支付成功' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.profile-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 120px);
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 30px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: white;
}

.page-title {
  font-size: 28px;
  font-weight: 600;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.title-icon {
  font-size: 32px;
}

.page-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.9;
}

.profile-tabs {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-tabs :deep(.el-tabs__header) {
  margin-bottom: 25px;
}

.profile-tabs :deep(.el-tabs__nav-wrap) {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 20px;
}

.profile-tabs :deep(.el-tabs__item) {
  border-radius: 8px;
  margin: 0 4px;
  padding: 0 20px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.profile-tabs :deep(.el-tabs__item.is-active) {
  background: #409EFF;
  color: white;
}

.profile-tabs :deep(.el-tabs__active-bar) {
  display: none;
}

.profile-card {
  border: none;
  box-shadow: none;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 20px 0 30px;
  text-align: center;
}

.user-info h2 {
  margin: 15px 0 5px 0;
  font-size: 24px;
  color: #2c3e50;
}

.user-role {
  color: #8492a6;
  font-size: 14px;
  margin: 0;
}

.coaches-section {
  min-height: 400px;
}

.bookings-section {
  min-height: 400px;
}

.evaluations-section {
  min-height: 400px;
}

.tuitions-section {
  min-height: 400px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.section-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  text-align: left;
}

.coaches-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  align-items: start;
}

.bookings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 24px;
  align-items: start;
}

.evaluations-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
  align-items: start;
}

.tuitions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 24px;
  align-items: start;
}

.coach-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coach-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.coach-avatar {
  position: relative;
  text-align: center;
  margin-bottom: 16px;
}

.coach-avatar img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #f0f0f0;
}

.coach-level-badge {
  position: absolute;
  top: -5px;
  right: calc(50% - 40px);
}

.coach-details {
  text-align: center;
}

.coach-name {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
}

.coach-username {
  margin: 0 0 16px 0;
  font-size: 12px;
  color: #8492a6;
}

.coach-info-grid {
  text-align: left;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-row .label {
  color: #606266;
  font-weight: 500;
}

.info-row .value {
  color: #409eff;
  font-weight: 600;
}

.booking-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.booking-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.booking-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.booking-no {
  font-size: 16px;
  color: #2c3e50;
}

.booking-content {
  padding: 20px;
}

.booking-info-grid .info-row {
  margin-bottom: 12px;
}

.booking-info-grid .info-row .label {
  min-width: 80px;
  display: inline-block;
}

.booking-info-grid .info-row .value.amount {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.booking-actions {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  justify-content: center;
  gap: 12px;
  flex-wrap: wrap;
}

.evaluation-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.evaluation-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.evaluation-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.evaluation-content {
  padding: 20px;
}

.evaluation-info-grid .info-row {
  margin-bottom: 12px;
}

.evaluation-info-grid .info-row .label {
  min-width: 100px;
  display: inline-block;
}

.rating-stars {
  color: #f7ba2a;
  font-size: 16px;
  font-weight: bold;
}

.evaluation-actions {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #f0f2f5;
  text-align: center;
}

.tuition-item {
  background: white;
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tuition-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  border-color: #409eff;
}

.tuition-header {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #e4e7ed;
}

.record-no {
  font-size: 16px;
  color: #2c3e50;
}

.amount-badge {
  font-size: 18px;
  font-weight: bold;
}

.tuition-content {
  padding: 20px;
}

.tuition-info-grid .info-row {
  margin-bottom: 12px;
}

.tuition-info-grid .info-row .label {
  min-width: 80px;
  display: inline-block;
}

.tuition-info-grid .info-row .value.remark {
  color: #8492a6;
  font-style: italic;
}

.transaction-type {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #606266;
  font-size: 14px;
}

.type-icon {
  font-size: 16px;
}

.type-text {
  font-weight: 500;
}

/* 支付相关样式 */
.payment-content {
  padding: 0;
}

.payment-info {
  margin-bottom: 24px;
}

.payment-amount {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.payment-method-section {
  margin-bottom: 24px;
}

.payment-method-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #2c3e50;
  text-align: left;
}

.payment-methods {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.payment-method-item {
  flex: 1;
  max-width: 120px;
  padding: 16px 12px;
  border: 2px solid #e4e7ed;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.payment-method-item:hover {
  border-color: #409eff;
  transform: translateY(-2px);
}

.payment-method-item.active {
  border-color: #409eff;
  background: #f0f9ff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);
}

.method-icon {
  font-size: 24px;
  margin-bottom: 8px;
}

.method-name {
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.payment-method-item.active .method-name {
  color: #409eff;
}

.qr-code-section {
  margin-bottom: 24px;
}

.qr-code-section h4 {
  margin: 0 0 16px 0;
  font-size: 16px;
  color: #2c3e50;
  text-align: left;
}

.qr-code-container {
  text-align: center;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
}

.qr-code-image {
  width: 200px;
  height: 200px;
  border: 2px solid #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.qr-code-tip {
  margin: 16px 0 0 0;
  color: #8492a6;
  font-size: 14px;
}

.simulate-payment {
  margin-top: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .profile-tabs {
    padding: 16px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .coaches-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .bookings-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .evaluations-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .tuitions-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .coach-item {
    padding: 16px;
  }
  
  .booking-item {
    margin-bottom: 16px;
  }
  
  .booking-header {
    padding: 12px 16px;
  }
  
  .booking-content {
    padding: 16px;
  }
  
  .evaluation-item {
    margin-bottom: 16px;
  }
  
  .evaluation-header {
    padding: 12px 16px;
  }
  
  .evaluation-content {
    padding: 16px;
  }
  
  .tuition-item {
    margin-bottom: 16px;
  }
  
  .tuition-header {
    padding: 12px 16px;
  }
  
  .tuition-content {
    padding: 16px;
  }
  
  .payment-methods {
    flex-direction: column;
    gap: 12px;
  }
  
  .payment-method-item {
    max-width: none;
  }
  
  .qr-code-image {
    width: 160px;
    height: 160px;
  }
}
</style> 