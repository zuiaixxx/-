<template>
  <div class="course-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <el-icon class="title-icon">
          <Calendar />
        </el-icon>
        课程展示
      </h1>
      <p class="page-subtitle">预约专业的乒乓球课程</p>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="search-section">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-select
            v-model="searchForm.campusId"
            placeholder="选择校区"
            clearable
            @change="handleSearch"
          >
            <el-option label="全部校区" value="" />
            <el-option 
              v-for="campus in campusList" 
              :key="campus.id" 
              :label="campus.campusName" 
              :value="campus.id" 
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input
            v-model="searchForm.coachName"
            placeholder="搜索教练姓名"
            prefix-icon="Search"
            clearable
            @input="handleSearch"
          />
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="searchForm.available"
            placeholder="课程状态"
            @change="handleSearch"
          >
            <el-option label="全部课程" :value="null" />
            <el-option label="可预约" :value="true" />
            <el-option label="不可预约" :value="false" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 课程列表 -->
    <div class="course-list" v-loading="loading">
      <el-row :gutter="24" v-if="paginatedCourseList.length > 0">
        <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="course in paginatedCourseList" :key="course.id">
          <div class="course-card" :class="{ 'disabled': !course.available }">
            <!-- 课程图片 -->
            <div class="course-image">
              <img 
                :src="course.img || defaultCourseImage" 
                :alt="course.coachName"
                @error="handleImageError"
              />
              <div class="course-status-tag">
                <el-tag :type="getCourseStatusType(course.available)" size="small">
                  {{ getCourseStatusText(course.available) }}
                </el-tag>
              </div>
            </div>

            <!-- 课程信息 -->
            <div class="course-info">
              <h3 class="course-title">{{ course.coachName }}教练</h3>
              <p class="course-campus">{{ getCampusName(course.campusId) }}</p>
              
              <div class="info-item">
                <el-icon class="info-icon"><Calendar /></el-icon>
                <span class="info-text">{{ course.date }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><Clock /></el-icon>
                <span class="info-text">{{ course.startTime }}-{{ course.endTime }}</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><Timer /></el-icon>
                <span class="info-text">{{ course.duration }}分钟</span>
              </div>

              <div class="info-item">
                <el-icon class="info-icon"><Location /></el-icon>
                <span class="info-text">球台：{{ course.courtNumber }}</span>
              </div>

              <!-- 操作按钮 -->
              <div class="course-actions">
                <el-button type="primary" size="small" @click="viewCourseDetail(course)">
                  <el-icon><View /></el-icon>
                  查看详情
                </el-button>
                <el-button 
                  type="success" 
                  size="small" 
                  @click="bookCourse(course)"
                  :disabled="!course.available"
                >
                  <el-icon><Plus /></el-icon>
                  预约课程
                </el-button>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>

      <!-- 空状态 -->
      <el-empty 
        v-else-if="!loading" 
        description="暂无课程数据"
        :image-size="200"
      >
        <el-button type="primary" @click="loadAllCourses">刷新数据</el-button>
      </el-empty>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="courseTotal > 0">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 16, 24, 32]"
        :total="courseTotal"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 课程详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${selectedCourse?.coachName}的课程详情`"
      width="60%"
      center
    >
      <div v-if="selectedCourse" class="course-detail">
        <el-row :gutter="20">
          <el-col :span="12">
            <img 
              :src="selectedCourse.img || defaultCourseImage" 
              :alt="selectedCourse.coachName"
              class="detail-image"
            />
          </el-col>
          <el-col :span="12">
            <div class="detail-info">
              <el-descriptions :column="1" border>
                <el-descriptions-item label="教练姓名">
                  {{ selectedCourse.coachName }}
                </el-descriptions-item>
                <el-descriptions-item label="所属校区">
                  {{ getCampusName(selectedCourse.campusId) }}
                </el-descriptions-item>
                <el-descriptions-item label="课程日期">
                  {{ selectedCourse.date }}
                </el-descriptions-item>
                <el-descriptions-item label="上课时间">
                  {{ selectedCourse.startTime }} - {{ selectedCourse.endTime }}
                </el-descriptions-item>
                <el-descriptions-item label="课程时长">
                  {{ selectedCourse.duration }}分钟
                </el-descriptions-item>
                <el-descriptions-item label="球台编号">
                  {{ selectedCourse.courtNumber }}
                </el-descriptions-item>
                <el-descriptions-item label="课程状态">
                  <el-tag :type="getCourseStatusType(selectedCourse.available)">
                    {{ getCourseStatusText(selectedCourse.available) }}
                  </el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>
          </el-col>
        </el-row>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
          <el-button 
            type="primary" 
            @click="bookCourse(selectedCourse)"
            :disabled="!selectedCourse?.available"
          >
            预约课程
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCourseList } from '@/api/course'
import { getCampusList } from '@/api/campus'
import { saveBookingRecord } from '@/api/bookingRecord'
import { getUserByToken } from '@/api/user'
import { 
  Calendar, Search, Refresh, Clock, Timer, Location, View, Plus
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 响应式数据
const loading = ref(false)
const allCourseList = ref([])
const filteredCourseList = ref([])
const paginatedCourseList = ref([])
const campusList = ref([])
const courseTotal = ref(0)
const currentPage = ref(1)
const pageSize = ref(8)
const detailDialogVisible = ref(false)
const selectedCourse = ref(null)
const currentUser = ref(null)

// 搜索表单
const searchForm = reactive({
  campusId: '',
  coachName: '',
  available: null
})

// 默认课程图片
const defaultCourseImage = 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'

// 获取所有课程数据
const loadAllCourses = async () => {
  loading.value = true
  try {
    const response = await getCourseList({
      pageNum: 1,
      pageSize: 1000
    })
    if (response.code === 200) {
      allCourseList.value = response.data.records || []
      filterAndPaginateCourses()
    } else {
      ElMessage.error(response.msg || '获取课程列表失败')
    }
  } catch (error) {
    console.error('获取课程列表错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 前端筛选和分页
const filterAndPaginateCourses = () => {
  let filtered = allCourseList.value.filter(course => {
    // 校区筛选
    if (searchForm.campusId && course.campusId != searchForm.campusId) {
      return false
    }
    
    // 教练姓名筛选
    if (searchForm.coachName && 
        !course.coachName.toLowerCase().includes(searchForm.coachName.toLowerCase())) {
      return false
    }
    
    // 可预约状态筛选
    if (searchForm.available !== null && searchForm.available !== undefined) {
      if (course.available !== searchForm.available) {
        return false
      }
    }
    
    return true
  })
  
  filteredCourseList.value = filtered
  courseTotal.value = filtered.length
  
  // 分页处理
  const startIndex = (currentPage.value - 1) * pageSize.value
  const endIndex = startIndex + pageSize.value
  paginatedCourseList.value = filtered.slice(startIndex, endIndex)
}

// 获取校区列表
const loadCampusList = async () => {
  try {
    const response = await getCampusList({ pageNum: 1, pageSize: 1000 })
    if (response.code === 200) {
      campusList.value = response.data.records || []
    }
  } catch (error) {
    console.error('获取校区列表错误:', error)
  }
}

// 获取当前用户信息
const getCurrentUser = async () => {
  try {
    const response = await getUserByToken()
    if (response.code === 200) {
      currentUser.value = response.data
    } else {
      ElMessage.error('获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 搜索处理
const handleSearch = () => {
  currentPage.value = 1
  filterAndPaginateCourses()
}

// 重置搜索
const resetSearch = () => {
  searchForm.campusId = ''
  searchForm.coachName = ''
  searchForm.available = null
  currentPage.value = 1
  filterAndPaginateCourses()
}

// 分页处理
const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  filterAndPaginateCourses()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  filterAndPaginateCourses()
}

// 获取校区名称
const getCampusName = (campusId) => {
  const campus = campusList.value.find(c => c.id == campusId)
  return campus ? campus.campusName : '未知校区'
}

// 获取课程状态文本
const getCourseStatusText = (available) => {
  return available ? '可预约' : '不可预约'
}

// 获取课程状态标签类型
const getCourseStatusType = (available) => {
  return available ? 'success' : 'danger'
}

// 课程图片错误处理
const handleImageError = (event) => {
  event.target.src = defaultCourseImage
}

// 查看课程详情
const viewCourseDetail = (course) => {
  selectedCourse.value = course
  detailDialogVisible.value = true
}

// 预约课程
const bookCourse = async (course) => {
  if (!course.available) {
    ElMessage.warning('该课程不可预约')
    return
  }
  
  if (!currentUser.value) {
    ElMessage.error('获取用户信息失败')
    return
  }
  
  try {
    // 生成预约编号
    const bookingNo = `BK${Date.now()}${Math.floor(Math.random() * 1000)}`
    
    const bookingData = {
      bookingNo: bookingNo,
      studentUsername: currentUser.value.username,
      studentName: currentUser.value.realName || currentUser.value.username,
      coachUsername: course.coachUsername,
      coachName: course.coachName,
      campusId: course.campusId.toString(),
      courseId: course.id,
      date: course.date,
      startTime: course.startTime,
      endTime: course.endTime,
      duration: course.duration,
      courtNumber: course.courtNumber,
      status: 'pending',
      amount: 0,
      paymentStatus: 'unpaid'
    }
    
    const response = await saveBookingRecord(bookingData)
    if (response.code === 200) {
      ElMessage.success(`课程预约成功！预约编号：${bookingNo}`)
      detailDialogVisible.value = false
      // 重新加载课程列表
      await loadAllCourses()
    } else {
      ElMessage.error(response.msg || '课程预约失败')
    }
  } catch (error) {
    console.error('课程预约错误:', error)
    ElMessage.error('网络错误，请稍后重试')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadAllCourses()
  loadCampusList()
  getCurrentUser()
})
</script>

<style scoped>
.course-container {
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

.search-section {
  background: white;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.course-list {
  margin-bottom: 24px;
}

.course-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  margin-bottom: 24px;
  height: 100%;
}

.course-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
}

.course-card.disabled {
  opacity: 0.6;
}

.course-image {
  position: relative;
  height: 200px;
  overflow: hidden;
}

.course-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.course-card:hover .course-image img {
  transform: scale(1.05);
}

.course-status-tag {
  position: absolute;
  top: 12px;
  right: 12px;
}

.course-info {
  padding: 20px;
}

.course-title {
  font-size: 18px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 4px 0;
  text-align: center;
}

.course-campus {
  font-size: 12px;
  color: #8492a6;
  margin: 0 0 16px 0;
  text-align: center;
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #606266;
}

.info-icon {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.info-text {
  flex: 1;
  word-break: break-all;
}

.course-actions {
  margin-top: 20px;
  display: flex;
  gap: 8px;
  justify-content: center;
}

.course-actions .el-button {
  flex: 1;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.course-detail .detail-image {
  width: 100%;
  height: 300px;
  object-fit: cover;
  border-radius: 8px;
}

.detail-info {
  height: 100%;
  display: flex;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .course-container {
    padding: 16px;
  }
  
  .page-header {
    padding: 20px 16px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .search-section {
    padding: 16px;
  }
  
  .course-card {
    margin-bottom: 16px;
  }
  
  .course-info {
    padding: 16px;
  }
  
  .course-actions {
    flex-direction: column;
  }
  
  .course-actions .el-button {
    width: 100%;
  }
}
</style>